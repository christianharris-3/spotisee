import { useCallback, useEffect, useMemo, useRef, useState } from "react";

import TableHeaderControl from "./TableHeaderControl.jsx";
import FilterMenu from "./trackerFilters/FilterMenu.jsx";
import {
  applyFilters,
  getFilterConfig,
  getUniqueOptions,
  hasActiveFilter,
  sortData,
} from "./trackerFilters/filterEngine.js";

const EMPTY_ARRAY = Object.freeze([]);
const EMPTY_OBJECT = Object.freeze({});

/**
 * Defines a column displayed by `SortableTable`.
 *
 * @typedef {Object} SortableTableColumn
 * @property {string | number} key - Unique column key.
 * @property {import("react").ReactNode} [label] - Column heading.
 * @property {function(Object, number): *} [accessor] - Reads the cell value from a row.
 * @property {function(*, Object, Object): import("react").ReactNode} [render] - Renders a cell from its value and row.
 * @property {string | function(*, Object, Object): string} [query] - URL prefix or URL builder used to render the value as a link.
 * @property {Object | function(*, Object, Object): Object} [linkProps] - Native anchor properties applied to a query link.
 * @property {boolean} [sortable=true] - Whether the column can be sorted.
 * @property {boolean} [filterable] - Whether the schema-backed filter menu is enabled.
 * @property {'text' | 'number' | 'date' | 'boolean'} [sortType='text'] - Built-in comparison type.
 * @property {function(Object, number): *} [sortAccessor] - Reads the value used for sorting.
 * @property {function(Object, Object): number} [sortComparator] - Custom ascending row comparator.
 * @property {Array<string>} [sortFields] - Fields used for lexicographic composite sorting.
 * @property {Array<'asc' | 'desc' | null>} [sortDirections] - Sort directions cycled by the header.
 * @property {function(Object): Object} [getHeaderProps] - Returns native `th` properties.
 * @property {function(*, Object, number): Object} [getCellProps] - Returns native `td` properties.
 */

function useControllableState({ value, defaultValue, onChange }) {
  const [internalValue, setInternalValue] = useState(defaultValue);
  const controlled = value !== undefined;
  const currentValue = controlled ? value : internalValue;

  const setValue = useCallback(
      (nextValueOrUpdater) => {
        const nextValue =
            typeof nextValueOrUpdater === "function"
                ? nextValueOrUpdater(currentValue)
                : nextValueOrUpdater;

        if (!controlled) {
          setInternalValue(nextValue);
        }

        onChange?.(nextValue);
      },
      [controlled, currentValue, onChange],
  );

  return [currentValue, setValue];
}

function defaultGetRowKey(row, index) {
  return row?.id ?? index;
}

function getColumnValue(row, column, rowIndex) {
  return typeof column.accessor === "function"
      ? column.accessor(row, rowIndex)
      : row?.[column.key];
}

function normalizeHref(href) {
  if (!href || typeof href !== "string") {
    return href;
  }

  const hasProtocolOrRelativePrefix = /^(?:[a-z][a-z\d+.-]*:|\/|#|\?)/i.test(
      href,
  );
  const looksLikeHostname = /^[a-z\d.-]+\.[a-z\d-]+(?::\d+)?(?:\/|$)/i.test(
      href,
  );

  if (!hasProtocolOrRelativePrefix && looksLikeHostname) {
    return `https://${href}`;
  }

  return href;
}

function renderColumnValue(value, row, column, rowIndex) {
  const context = {
    column,
    rowIndex,
  };

  if (typeof column.render === "function") {
    return column.render(value, row, context);
  }

  if (!column.query || value === null || value === undefined) {
    return value;
  }

  const rawHref =
      typeof column.query === "function"
          ? column.query(value, row, context)
          : `${column.query}${encodeURIComponent(String(value))}`;
  const href = normalizeHref(rawHref);

  if (!href) {
    return value;
  }

  const linkProps =
      typeof column.linkProps === "function"
          ? column.linkProps(value, row, context)
          : (column.linkProps ?? {});

  return (
      <a {...linkProps} href={href}>
        {value}
      </a>
  );
}

function getFilterOptions(data, column, config) {
  if (Array.isArray(config.options)) {
    return config.options;
  }

  if (typeof config.options === "function") {
    return config.options(data, column);
  }

  const accessor =
      config.optionAccessor ??
      config.accessor ??
      config.getValue ??
      column.accessor ??
      column.key;

  return getUniqueOptions(data, accessor, config.compareOptions);
}

/**
 * Displays, sorts and filters tabular data using consumer-defined columns and
 * an explicit filter schema.
 *
 * Filters and sorting can be controlled with `filters`/`sort`, or left
 * uncontrolled with `defaultFilters`/`defaultSort`. Columns without a schema
 * entry remain sortable but do not display a filter menu.
 *
 * @component
 * @param {Object} props - The component properties.
 * @param {Array<Object>} [props.data=[]] - Rows displayed by the table.
 * @param {Array<SortableTableColumn>} [props.columns=[]] - Column definitions.
 * @param {Object<string, string | Object>} [props.filterSchema={}] - Filter definitions keyed by column key.
 * @param {Object<string, Object>} [props.filters] - Controlled filter state.
 * @param {Object<string, Object>} [props.defaultFilters={}] - Initial uncontrolled filter state.
 * @param {function(Object): void} [props.onFiltersChange] - Called whenever filters change.
 * @param {{key: string | number, direction: 'asc' | 'desc'} | null} [props.sort] - Controlled sort state.
 * @param {{key: string | number, direction: 'asc' | 'desc'} | null} [props.defaultSort=null] - Initial uncontrolled sort state.
 * @param {function(Object | null): void} [props.onSortChange] - Called whenever sorting changes.
 * @param {function(Object, number): string | number} [props.getRowKey] - Returns a unique key for a row.
 * @param {function(Object, number): Object} [props.getRowProps] - Returns native `tr` properties.
 * @param {import("react").ReactNode} [props.emptyMessage='No data available.'] - Content shown when no rows match.
 * @param {function(Object): import("react").ReactNode} [props.renderFilterMenu] - Replaces the built-in filter menu.
 * @param {string} [props.filterMenuClassName=''] - CSS class applied to the filter menu container.
 * @param {Object} [props.filterMenuStyle={}] - Additional inline styles for the filter menu container.
 * @returns {import("react").ReactElement} The rendered table and active filter menu.
 */
export default function SortableTable({
                                        data = EMPTY_ARRAY,
                                        columns = EMPTY_ARRAY,
                                        filterSchema = EMPTY_OBJECT,
                                        filters: controlledFilters,
                                        defaultFilters = EMPTY_OBJECT,
                                        onFiltersChange,
                                        sort: controlledSort,
                                        defaultSort = null,
                                        onSortChange,
                                        getRowKey = defaultGetRowKey,
                                        getRowProps,
                                        emptyMessage = "No data available.",
                                        renderFilterMenu,
                                        filterMenuClassName = "",
                                        filterMenuStyle = EMPTY_OBJECT,
                                        ...tableProps
                                      }) {
  const [filters, setFilters] = useControllableState({
    value: controlledFilters,
    defaultValue: defaultFilters,
    onChange: onFiltersChange,
  });
  const [sort, setSort] = useControllableState({
    value: controlledSort,
    defaultValue: defaultSort,
    onChange: onSortChange,
  });
  const [activeColumn, setActiveColumn] = useState(null);
  const [menuPosition, setMenuPosition] = useState(null);
  const menuRef = useRef(null);

  useEffect(() => {
    if (!activeColumn) {
      return undefined;
    }

    const handlePointerDown = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setActiveColumn(null);
      }
    };
    const handleKeyDown = (event) => {
      if (event.key === "Escape") {
        setActiveColumn(null);
      }
    };

    document.addEventListener("mousedown", handlePointerDown);
    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.removeEventListener("mousedown", handlePointerDown);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [activeColumn]);

  const visibleColumns = useMemo(
      () => columns.filter((column) => column.hidden !== true),
      [columns],
  );

  const processedData = useMemo(() => {
    const filtered = applyFilters(data, filters, filterSchema);

    if (!sort) {
      return filtered;
    }

    const column = visibleColumns.find(
        (candidate) => candidate.key === sort.key,
    );

    return column
        ? sortData(filtered, column, sort.direction, filterSchema)
        : filtered;
  }, [data, filterSchema, filters, sort, visibleColumns]);

  const handleSort = (key) => {
    const column = visibleColumns.find((candidate) => candidate.key === key);

    if (!column || column.sortable === false) {
      return;
    }

    setSort((previous) => {
      const directions = column.sortDirections ?? ["asc", "desc", null];
      const currentDirection =
          previous?.key === key ? previous.direction : null;
      const currentIndex = directions.indexOf(currentDirection);
      const nextDirection = directions[(currentIndex + 1) % directions.length];

      return nextDirection
          ? {
            key,
            direction: nextDirection,
          }
          : null;
    });
  };

  const activeConfig = activeColumn
      ? getFilterConfig(filterSchema, activeColumn.key)
      : null;
  const activeOptions =
      activeColumn && activeConfig?.type === "multi"
          ? getFilterOptions(data, activeColumn, activeConfig)
          : EMPTY_ARRAY;

  const applyActiveFilter = (nextFilter) => {
    if (!activeColumn) {
      return;
    }

    setFilters((previous = EMPTY_OBJECT) => {
      const next = { ...previous };

      if (!nextFilter) {
        delete next[activeColumn.key];
      } else {
        next[activeColumn.key] = nextFilter;
      }

      return next;
    });
  };

  const filterMenuProps = activeColumn
      ? {
        column: activeColumn,
        config: activeConfig,
        filter: filters?.[activeColumn.key],
        options: activeOptions,
        onApply: applyActiveFilter,
        onClose: () => setActiveColumn(null),
      }
      : null;

  return (
      <>
        <table {...tableProps}>
          <thead>
          <tr>
            {visibleColumns.map((column) => {
              const config = getFilterConfig(filterSchema, column.key);
              const sortable = column.sortable !== false;
              const filterable = Boolean(config) && column.filterable !== false;
              const filtered = hasActiveFilter(filters?.[column.key], config);
              const headerProps = column.getHeaderProps?.(column) ?? {};

              return (
                  <th {...headerProps} key={column.key}>
                    <TableHeaderControl
                        label={column.label ?? String(column.key)}
                        colKey={column.key}
                        sortConfig={sort}
                        onSort={handleSort}
                        sortable={sortable}
                        filterable={filterable}
                        filtered={filtered}
                        onFilter={(event) => {
                          const rect = event.currentTarget.getBoundingClientRect();

                          setMenuPosition({
                            top: rect.bottom + window.scrollY,
                            left: rect.left + window.scrollX,
                          });
                          setActiveColumn(column);
                        }}
                    />
                  </th>
              );
            })}
          </tr>
          </thead>

          <tbody>
          {processedData.length === 0 ? (
              <tr>
                <td colSpan={Math.max(visibleColumns.length, 1)}>
                  {emptyMessage}
                </td>
              </tr>
          ) : (
              processedData.map((row, rowIndex) => {
                const rowProps = getRowProps?.(row, rowIndex) ?? {};
                const rowKey = getRowKey(row, rowIndex) ?? rowIndex;

                return (
                    <tr {...rowProps} key={rowKey}>
                      {visibleColumns.map((column) => {
                        const value = getColumnValue(row, column, rowIndex);
                        const cellProps =
                            column.getCellProps?.(value, row, rowIndex) ?? {};

                        return (
                            <td {...cellProps} key={column.key}>
                              {renderColumnValue(value, row, column, rowIndex)}
                            </td>
                        );
                      })}
                    </tr>
                );
              })
          )}
          </tbody>
        </table>

        {activeColumn && activeConfig && menuPosition && (
            <div
                ref={menuRef}
                className={filterMenuClassName}
                style={{
                  position: "absolute",
                  top: `${menuPosition.top}px`,
                  left: `${menuPosition.left}px`,
                  background: "white",
                  border: "1px solid black",
                  padding: "10px",
                  zIndex: 9999,
                  ...filterMenuStyle,
                }}
            >
              {renderFilterMenu ? (
                  renderFilterMenu(filterMenuProps)
              ) : (
                  <FilterMenu key={activeColumn.key} {...filterMenuProps} />
              )}
            </div>
        )}
      </>
  );
}
