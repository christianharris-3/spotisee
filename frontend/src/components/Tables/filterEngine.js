const DEFAULT_LOCALE_OPTIONS = {
    numeric: true,
    sensitivity: "base",
};

/**
 * Resolves a column's filter configuration from a schema supplied by the
 * table consumer.
 *
 * @param {Object<string, string | Object>} filterSchema - Filter definitions keyed by column key.
 * @param {string | number} key - The column key to resolve.
 * @returns {Object | null} The normalized configuration, or `null` when the column is not filterable.
 */
export function getFilterConfig(filterSchema = {}, key) {
    const entry = filterSchema?.[key];

    if (!entry) {
        return null;
    }

    if (typeof entry === "string") {
        return { type: entry };
    }

    return {
        type: "text",
        ...entry,
    };
}

/**
 * Parses supported date values without changing valid `Date` instances.
 *
 * @param {*} value - The date value to parse.
 * @returns {Date | null} A valid date or `null`.
 */
export function parseDateValue(value) {
    if (!value) {
        return null;
    }

    if (value instanceof Date) {
        return Number.isNaN(value.getTime()) ? null : value;
    }

    if (typeof value !== "string") {
        const parsed = new Date(value);
        return Number.isNaN(parsed.getTime()) ? null : parsed;
    }

    const trimmed = value.trim();
    let match;

    match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(trimmed);
    if (match) {
        return validLocalDate(Number(match[1]), Number(match[2]), Number(match[3]));
    }

    match = /^(\d{2})[-/](\d{2})[-/](\d{4})$/.exec(trimmed);
    if (match) {
        return validLocalDate(Number(match[3]), Number(match[2]), Number(match[1]));
    }

    const parsed = new Date(trimmed);
    return Number.isNaN(parsed.getTime()) ? null : parsed;
}

function validLocalDate(year, month, day) {
    const parsed = new Date(year, month - 1, day);

    if (
        parsed.getFullYear() !== year ||
        parsed.getMonth() !== month - 1 ||
        parsed.getDate() !== day
    ) {
        return null;
    }

    return parsed;
}

function isEmpty(value) {
    return (
        value === null ||
        value === undefined ||
        (typeof value === "string" && value.trim() === "")
    );
}

function normalizeText(value) {
    return String(value ?? "").toLocaleLowerCase();
}

function matchesText(value, search, mode = "contains") {
    const candidate = normalizeText(value);
    const target = normalizeText(search);

    if (mode === "equals") {
        return candidate === target;
    }

    if (mode === "startsWith") {
        return candidate.startsWith(target);
    }

    if (mode === "endsWith") {
        return candidate.endsWith(target);
    }

    return candidate.includes(target);
}

function readValue(row, key, accessor) {
    return typeof accessor === "function"
        ? accessor(row)
        : row?.[accessor ?? key];
}

/**
 * Reports whether a filter contains a value that affects the result set.
 *
 * @param {Object | null | undefined} filter - The current filter state.
 * @param {Object | null | undefined} config - The resolved filter configuration.
 * @returns {boolean} Whether the filter is active.
 */
export function hasActiveFilter(filter, config) {
    if (!filter || !config) {
        return false;
    }

    if (typeof config.isActive === "function") {
        return Boolean(config.isActive(filter));
    }

    if (Array.isArray(filter.values)) {
        return filter.values.length > 0;
    }

    if (filter.mode === "today" || filter.mode === "notToday") {
        return true;
    }

    if (Object.prototype.hasOwnProperty.call(filter, "value")) {
        return !isEmpty(filter.value);
    }

    return false;
}

function applyTextFilter(value, filter, config) {
    if (isEmpty(filter.value)) {
        return true;
    }

    const values = Array.isArray(value) ? value : [value];
    const mode = filter.mode ?? config.mode ?? "contains";

    return values.some((candidate) => matchesText(candidate, filter.value, mode));
}

function applyMultiFilter(value, filter, config) {
    if (!Array.isArray(filter.values) || filter.values.length === 0) {
        return true;
    }

    const equals = config.equals ?? Object.is;
    const rowValues = Array.isArray(value) ? value : [value];

    return filter.values.some((option) =>
        rowValues.some((candidate) => equals(option, candidate)),
    );
}

function applyBooleanFilter(value, filter) {
    if (filter.value === undefined || filter.value === null) {
        return true;
    }

    if (filter.value === "null") {
        return value === null || value === undefined;
    }

    return value === filter.value;
}

function applyNumberFilter(value, filter) {
    if (isEmpty(filter.value)) {
        return true;
    }

    const rowNumber = Number(value);
    const target = Number(filter.value);

    if (Number.isNaN(target)) {
        return true;
    }

    if (Number.isNaN(rowNumber)) {
        return false;
    }

    if (filter.mode === "lessThan") {
        return rowNumber < target;
    }

    if (filter.mode === "lessThanOrEqual") {
        return rowNumber <= target;
    }

    if (filter.mode === "greaterThan") {
        return rowNumber > target;
    }

    if (filter.mode === "greaterThanOrEqual") {
        return rowNumber >= target;
    }

    return rowNumber === target;
}

function applyDateFilter(value, filter, now) {
    const rowDate = parseDateValue(value);

    if (filter.mode === "today" || filter.mode === "notToday") {
        if (!rowDate) {
            return false;
        }

        const isToday = rowDate.toDateString() === now.toDateString();

        return filter.mode === "today" ? isToday : !isToday;
    }

    const target = parseDateValue(filter.value);

    if (!target) {
        return true;
    }

    if (!rowDate) {
        return false;
    }

    if (filter.mode === "before") {
        return rowDate < target;
    }

    if (filter.mode === "after") {
        return rowDate > target;
    }

    return rowDate.toDateString() === target.toDateString();
}

/**
 * Applies a collection of schema-driven filters to an array.
 *
 * Custom filter configurations may provide a `predicate` function with the
 * signature `(value, filter, row, config) => boolean`.
 *
 * @param {Array<Object>} data - Rows to filter.
 * @param {Object<string, Object>} filters - Active filter values keyed by column key.
 * @param {Object<string, string | Object>} filterSchema - Filter definitions keyed by column key.
 * @param {Object} [options={}] - Filtering options.
 * @param {Date} [options.now] - Date used by `today` and `notToday` filters.
 * @returns {Array<Object>} The filtered rows.
 */
export function applyFilters(
    data,
    filters = {},
    filterSchema = {},
    { now = new Date() } = {},
) {
    if (!Array.isArray(data)) {
        return [];
    }

    return data.filter((row) =>
        Object.entries(filters).every(([key, filter]) => {
            const config = getFilterConfig(filterSchema, key);

            if (!hasActiveFilter(filter, config)) {
                return true;
            }

            const value = readValue(row, key, config.accessor ?? config.getValue);

            if (typeof config.predicate === "function") {
                return Boolean(config.predicate(value, filter, row, config));
            }

            if (config.type === "text") {
                return applyTextFilter(value, filter, config);
            }

            if (config.type === "compositeText") {
                const compositeValue =
                    typeof config.accessor === "function" ||
                    typeof config.getValue === "function"
                        ? value
                        : (config.fields ?? []).map((field) => row?.[field]);

                return applyTextFilter(compositeValue, filter, config);
            }

            if (config.type === "multi") {
                return applyMultiFilter(value, filter, config);
            }

            if (config.type === "boolean") {
                return applyBooleanFilter(value, filter);
            }

            if (config.type === "number") {
                return applyNumberFilter(value, filter);
            }

            if (config.type === "date") {
                return applyDateFilter(value, filter, now);
            }

            throw new TypeError(
                `Unsupported filter type "${config.type}" for column "${key}".`,
            );
        }),
    );
}

function compareNullableValues(rawA, rawB, convert) {
    const a = convert(rawA);
    const b = convert(rawB);
    const aMissing = a === null || a === undefined || Number.isNaN(a);
    const bMissing = b === null || b === undefined || Number.isNaN(b);

    if (aMissing && bMissing) {
        return 0;
    }

    if (aMissing) {
        return -1;
    }

    if (bMissing) {
        return 1;
    }

    if (a < b) {
        return -1;
    }

    if (a > b) {
        return 1;
    }

    return 0;
}

/**
 * Compares values using a supported sort type.
 *
 * @param {*} rawA - The first value.
 * @param {*} rawB - The second value.
 * @param {'text' | 'number' | 'date' | 'boolean'} [type='text'] - Comparison type.
 * @returns {number} A standard comparator result.
 */
export function compareValues(rawA, rawB, type = "text") {
    if (type === "date") {
        return compareNullableValues(rawA, rawB, (value) =>
            parseDateValue(value)?.getTime(),
        );
    }

    if (type === "number") {
        return compareNullableValues(rawA, rawB, (value) => {
            if (isEmpty(value)) {
                return null;
            }

            return Number(value);
        });
    }

    if (type === "boolean") {
        return compareNullableValues(rawA, rawB, (value) =>
            value === null || value === undefined ? null : Number(Boolean(value)),
        );
    }

    return String(rawA ?? "").localeCompare(
        String(rawB ?? ""),
        undefined,
        DEFAULT_LOCALE_OPTIONS,
    );
}

function resolveSortType(column, config) {
    const requestedType =
        column.sortType ?? config?.sortType ?? config?.type ?? "text";

    if (requestedType === "date" || requestedType === "number") {
        return requestedType;
    }

    if (requestedType === "boolean") {
        return "boolean";
    }

    return "text";
}

/**
 * Returns a stable, sorted copy of an array.
 *
 * A column may provide `sortAccessor`, `sortComparator`, `sortType` or
 * `sortFields`. Schema entries may provide the equivalent values.
 *
 * @param {Array<Object>} data - Rows to sort.
 * @param {Object} column - Column definition.
 * @param {'asc' | 'desc'} direction - Sort direction.
 * @param {Object<string, string | Object>} [filterSchema={}] - Schema used for shared accessors and sort metadata.
 * @returns {Array<Object>} A sorted copy of the rows.
 */
export function sortData(data, column, direction = "asc", filterSchema = {}) {
    if (!Array.isArray(data)) {
        return [];
    }

    if (!column) {
        return [...data];
    }

    const config = getFilterConfig(filterSchema, column.key);
    const customComparator = column.sortComparator ?? config?.sortComparator;
    const sortFields =
        column.sortFields ??
        config?.sortFields ??
        (config?.type === "compositeText" ? config.fields : undefined);
    const sortAccessor =
        column.sortAccessor ?? config?.sortAccessor ?? column.accessor;
    const sortType = resolveSortType(column, config);
    const multiplier = direction === "desc" ? -1 : 1;

    return [...data]
        .map((row, index) => ({ row, index }))
        .sort((a, b) => {
            let comparison = 0;

            if (typeof customComparator === "function") {
                comparison = customComparator(a.row, b.row);
            } else if (Array.isArray(sortFields)) {
                for (const field of sortFields) {
                    comparison = compareValues(a.row?.[field], b.row?.[field], "text");

                    if (comparison !== 0) {
                        break;
                    }
                }
            } else {
                comparison = compareValues(
                    readValue(a.row, column.key, sortAccessor),
                    readValue(b.row, column.key, sortAccessor),
                    sortType,
                );
            }

            return comparison === 0 ? a.index - b.index : comparison * multiplier;
        })
        .map(({ row }) => row);
}

/**
 * Collects unique, non-empty option values from a row collection.
 *
 * @param {Array<Object>} data - Source rows.
 * @param {string | number | function} accessor - Property key or accessor function.
 * @param {function} [compare] - Optional option comparator.
 * @returns {Array<*>} Sorted unique values.
 */
export function getUniqueOptions(data, accessor, compare) {
    const values = [];

    for (const row of Array.isArray(data) ? data : []) {
        const rowValue =
            typeof accessor === "function" ? accessor(row) : row?.[accessor];
        const candidates = Array.isArray(rowValue) ? rowValue : [rowValue];

        for (const value of candidates) {
            if (isEmpty(value)) {
                continue;
            }

            if (!values.some((existing) => Object.is(existing, value))) {
                values.push(value);
            }
        }
    }

    return values.sort(
        compare ??
        ((a, b) =>
            String(a).localeCompare(String(b), undefined, DEFAULT_LOCALE_OPTIONS)),
    );
}