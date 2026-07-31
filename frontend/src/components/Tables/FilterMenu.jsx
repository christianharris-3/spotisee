import { useState } from "react";

/**
 * Renders the built-in menu for a schema-driven table filter.
 *
 * Custom schema entries can replace the menu entirely with `renderMenu`.
 *
 * @component
 * @param {Object} props - The component properties.
 * @param {Object} props.column - The active table column.
 * @param {Object} props.config - The resolved filter configuration.
 * @param {Object} [props.filter] - The current filter value.
 * @param {Array<*>} [props.options=[]] - Options available to a multi-value filter.
 * @param {function} props.onApply - Applies or clears the filter.
 * @param {function} props.onClose - Closes the menu.
 * @returns {import("react").ReactElement | null} The rendered filter controls.
 */
export default function FilterMenu({
  column,
  config,
  onApply,
  onClose,
  filter,
  options = [],
}) {
  const label = column.label ?? String(column.key);
  const type = config?.type;
  const [value, setValue] = useState(filter?.value ?? "");
  const [mode, setMode] = useState(
    filter?.mode ?? (type === "number" ? "equals" : ""),
  );
  const [selected, setSelected] = useState(filter?.values ?? []);
  const [optionSearch, setOptionSearch] = useState("");

  const apply = (nextFilter, close = true) => {
    onApply(nextFilter);

    if (close) {
      onClose();
    }
  };

  if (!config) {
    return null;
  }

  if (typeof config.renderMenu === "function") {
    return config.renderMenu({
      apply,
      close: onClose,
      column,
      config,
      filter,
      options,
    });
  }

  if (type === "boolean") {
    const labels = {
      true: "Yes",
      false: "No",
      null: "Null",
      ...config.labels,
    };

    return (
      <div style={{ padding: 10 }}>
        <button type="button" onClick={() => apply({ value: true })}>
          {labels.true}
        </button>

        <button type="button" onClick={() => apply({ value: false })}>
          {labels.false}
        </button>

        {config.allowNull && (
          <button type="button" onClick={() => apply({ value: "null" })}>
            {labels.null}
          </button>
        )}

        <button type="button" onClick={() => apply(null)}>
          Clear
        </button>
      </div>
    );
  }

  if (type === "multi") {
    const formatOption = config.formatOption ?? ((option) => String(option));
    const visibleOptions = options.filter((option) =>
      formatOption(option)
        .toLocaleLowerCase()
        .includes(optionSearch.trim().toLocaleLowerCase()),
    );
    const isSelected = (option) =>
      selected.some((candidate) => Object.is(candidate, option));
    const toggle = (option) => {
      setSelected((current) =>
        current.some((candidate) => Object.is(candidate, option))
          ? current.filter((candidate) => !Object.is(candidate, option))
          : [...current, option],
      );
    };
    const selectVisible = () => {
      setSelected((current) => [
        ...current,
        ...visibleOptions.filter(
          (option) =>
            !current.some((candidate) => Object.is(candidate, option)),
        ),
      ]);
    };

    return (
      <div
        style={{
          color: "#111827",
          width: "min(200px, calc(100vw - 40px))",
        }}
      >
        <div
          style={{
            alignItems: "center",
            display: "flex",
            justifyContent: "space-between",
            marginBottom: 10,
          }}
        >
          <strong>{label}</strong>

          <span
            style={{
              background: selected.length ? "#dbeafe" : "#f3f4f6",
              borderRadius: 999,
              color: selected.length ? "#1d4ed8" : "#6b7280",
              fontSize: 12,
              padding: "3px 8px",
            }}
          >
            {selected.length} selected
          </span>
        </div>

        <input
          type="search"
          aria-label={`Search ${label} options`}
          placeholder={config.searchPlaceholder ?? "Search values..."}
          value={optionSearch}
          onChange={(event) => setOptionSearch(event.target.value)}
          style={{
            border: "1px solid #d1d5db",
            borderRadius: 6,
            boxSizing: "border-box",
            marginBottom: 8,
            padding: "8px 10px",
            width: "100%",
          }}
        />

        <div
          style={{
            display: "flex",
            gap: 6,
            marginBottom: 8,
          }}
        >
          <button
            type="button"
            onClick={selectVisible}
            disabled={visibleOptions.length === 0}
            style={{
              fontSize: 12,
              padding: "5px 8px",
            }}
          >
            Select shown
          </button>

          <button
            type="button"
            onClick={() => setSelected([])}
            disabled={selected.length === 0}
            style={{
              fontSize: 12,
              padding: "5px 8px",
            }}
          >
            Clear selection
          </button>
        </div>

        <div
          role="group"
          aria-label={`${label} options`}
          style={{
            background: "white",
            border: "1px solid #e5e7eb",
            borderRadius: 6,
            maxHeight: 260,
            overflowY: "auto",
          }}
        >
          {visibleOptions.length === 0 ? (
            <p
              style={{
                color: "#6b7280",
                margin: 0,
                padding: 12,
              }}
            >
              No matching values
            </p>
          ) : (
            visibleOptions.map((option) => {
              const optionLabel = formatOption(option);

              return (
                <label
                  key={`${typeof option}:` + optionLabel}
                  style={{
                    alignItems: "center",
                    background: isSelected(option) ? "#eff6ff" : "white",
                    borderBottom: "1px solid #f3f4f6",
                    boxSizing: "border-box",
                    cursor: "pointer",
                    display: "flex",
                    gap: 6,
                    minHeight: 38,
                    padding: "8px 10px",
                    width: "100%",
                  }}
                >
                  <span
                    title={optionLabel}
                    style={{
                      flex: "1 1 auto",
                      minWidth: 0,
                      overflowWrap: "anywhere",
                      paddingRight: 2,
                      textAlign: "left",
                      whiteSpace: "normal",
                    }}
                  >
                    {optionLabel}
                  </span>

                  <input
                    type="checkbox"
                    checked={isSelected(option)}
                    onChange={() => toggle(option)}
                    style={{
                      accentColor: "#2563eb",
                      boxSizing: "border-box",
                      flex: "0 0 16px",
                      height: 16,
                      margin: 0,
                      padding: 0,
                      width: 16,
                    }}
                  />
                </label>
              );
            })
          )}
        </div>

        <div
          style={{
            borderTop: "1px solid #e5e7eb",
            display: "flex",
            gap: 8,
            justifyContent: "flex-end",
            marginTop: 10,
            paddingTop: 10,
          }}
        >
          <button type="button" aria-label="Cancel" onClick={onClose}>
            Cancel
          </button>

          <button
            type="button"
            aria-label="Apply"
            onClick={() => apply(selected.length ? { values: selected } : null)}
            style={{
              background: "#2563eb",
              border: "1px solid #2563eb",
              borderRadius: 5,
              color: "white",
              padding: "6px 12px",
            }}
          >
            Apply
            {selected.length ? ` (${selected.length})` : ""}
          </button>
        </div>
      </div>
    );
  }

  if (type === "date") {
    const activeStyle = (candidate) => ({
      background: mode === candidate ? "#2563eb" : "#eee",
      color: mode === candidate ? "white" : "black",
      marginRight: 5,
    });
    const applyDate = (nextMode) => {
      setMode(nextMode);
      apply(
        nextMode === "today" || nextMode === "notToday"
          ? { mode: nextMode }
          : {
              mode: nextMode,
              value,
            },
      );
    };

    return (
      <div style={{ padding: 10 }}>
        <input
          aria-label={`Filter ${label} date`}
          type="date"
          value={value}
          onChange={(event) => setValue(event.target.value)}
        />

        <div style={{ marginTop: 10 }}>
          {[
            ["on", "On"],
            ["before", "Before"],
            ["after", "After"],
            ["today", "Today"],
            ["notToday", "Not Today"],
          ].map(([candidate, buttonLabel]) => (
            <button
              key={candidate}
              type="button"
              style={activeStyle(candidate)}
              onClick={() => applyDate(candidate)}
            >
              {buttonLabel}
            </button>
          ))}

          <button type="button" onClick={() => apply(null)}>
            Clear
          </button>
        </div>
      </div>
    );
  }

  if (type === "number") {
    return (
      <div style={{ padding: 10 }}>
        <select
          aria-label={`Filter mode for ${label}`}
          value={mode}
          onChange={(event) => setMode(event.target.value)}
        >
          <option value="equals">Equals</option>
          <option value="lessThan">Less than</option>
          <option value="lessThanOrEqual">Less than or equal</option>
          <option value="greaterThan">Greater than</option>
          <option value="greaterThanOrEqual">Greater than or equal</option>
        </select>

        <input
          aria-label={`Filter ${label} number`}
          type="number"
          value={value}
          onChange={(event) => setValue(event.target.value)}
        />

        <button
          type="button"
          onClick={() => apply(value === "" ? null : { mode, value })}
        >
          Apply
        </button>

        <button type="button" onClick={() => apply(null)}>
          Clear
        </button>
      </div>
    );
  }

  if (type === "text" || type === "compositeText") {
    return (
      <div style={{ padding: 10 }}>
        <input
          aria-label={`Filter ${label}`}
          placeholder={config.placeholder ?? `Filter ${label}`}
          value={value}
          onChange={(event) => {
            const nextValue = event.target.value;

            setValue(nextValue);
            apply(
              nextValue.trim() === ""
                ? null
                : {
                    mode: config.mode ?? "contains",
                    value: nextValue,
                  },
              false,
            );
          }}
        />

        <button type="button" onClick={() => apply(null)}>
          Clear
        </button>
      </div>
    );
  }

  throw new TypeError(
    `FilterMenu cannot render filter type "${type}". ` +
      "Provide config.renderMenu for custom filter types.",
  );
}
