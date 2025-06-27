import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchSettings, saveSetting } from "../util/api.ts";
import { type Setting, type SettingType } from "../util/types.ts";
import { useContext, useMemo, useRef, useState } from "react";
import { LoadingIcon } from "../components/icons.tsx";
import { TextFilterContext } from "../state/search_filter.ts";
import Fuse from "fuse.js";

export default function Settings() {
  const query = useQuery({
    queryFn: fetchSettings,
    queryKey: ["settings"],
    placeholderData: [],
  });

  if (query.isError) {
    return <div>Error: {query.error.message}</div>;
  }

  return <SettingsInner settings={query.data!} />;
}

function SettingsInner({ settings }: { settings: Setting[] }) {
  const queryClient = useQueryClient();
  const saveMutation = useMutation({
    mutationFn: saveSetting,
    onSuccess: async (newSetting) =>
      queryClient.setQueryData(
        ["settings"],
        settings.map((setting) => {
          if (setting.id !== newSetting.id) return setting;

          setting.value = newSetting.value;
          return setting;
        }),
      ),
  });

  const { state: textFilter } = useContext(TextFilterContext);
  const fuse = useMemo(
    () =>
      new Fuse(settings, {
        keys: ["name", "type", "value"],
        useExtendedSearch: true,
      }),
    [settings],
  );

  const filteredSettings = textFilter
    ? fuse.search(textFilter).map((result) => result.item)
    : settings;

  function handleFocusOut(setting: Setting) {
    const oldSetting = settings.find((s) => s.id === setting.id);

    if (!oldSetting || oldSetting.value === setting.value) return;

    saveMutation.mutate(setting);
  }

  return (
    <div className="absolute top-16 w-full p-2">
      <table className="table min-w-full table-auto border-collapse text-sm">
        <thead>
          <tr>
            <th className="rounded-t-lg border-b border-gray-600 p-2 text-left font-medium transition select-none hover:bg-gray-800">
              Название
            </th>
            <th className="rounded-t-lg border-b border-gray-600 p-2 text-left font-medium transition select-none hover:bg-gray-800">
              Тип
            </th>
            <th className="rounded-t-lg border-b border-gray-600 p-2 text-left font-medium transition select-none hover:bg-gray-800">
              Значение
            </th>
          </tr>
        </thead>
        <tbody>
          {filteredSettings!.map((setting) => (
            <tr
              key={setting.id}
              className="table-row bg-transparent transition hover:bg-gray-900"
            >
              <td className="border-b border-gray-700 p-4 text-gray-400">
                {setting.name}
              </td>
              <td className="border-b border-gray-700 p-4 text-gray-400">
                {setting.type}
              </td>
              <td className="border-b border-gray-700 p-2 text-gray-400">
                {saveMutation.variables?.id === setting.id &&
                saveMutation.isPending ? (
                  <LoadingIcon />
                ) : (
                  <EditableField
                    setting={setting}
                    onFocusOut={handleFocusOut}
                  />
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function EditableField({
  setting,
  onFocusOut,
}: {
  setting: Setting;
  onFocusOut: (setting: Setting) => void;
}) {
  const [value, setValue] = useState(setting);
  const [error, setError] = useState<String | null>(null);
  const ref = useRef<HTMLInputElement>(null);

  return (
    <>
      <input
        type={typeToFormType(value.type)}
        onSubmit={console.log}
        value={value.value}
        className={
          error
            ? "rounded-xl bg-gray-900 px-4 py-2 outline-2 outline-red-800"
            : "rounded-xl bg-gray-900 px-4 py-2 outline-gray-600 hover:outline-2 focus:outline-2"
        }
        onChange={(e) => {
          if (e.target.value === value.value) return;

          const newValue = structuredClone(value);
          newValue.value = e.target.value;
          return setValue(newValue);
        }}
        ref={ref}
        onBlur={() => {
          const newValue = structuredClone(value);
          newValue.value = value.value.trim();
          if (newValue.value !== value.value) setValue(newValue);
          const isValid = validateSetting(newValue);

          if (!isValid) return setError("Некорректное значение");

          setError(null);
          onFocusOut(newValue);
        }}
        onKeyDown={(e) => e.key === "Enter" && ref.current?.blur()}
      />
    </>
  );
}

function typeToFormType(type: SettingType) {
  switch (type) {
    case "INTEGER":
      return "number";
    case "PATTERN":
      return "text";
    case "DURATION":
      return "text";
    default:
      return "";
  }
}

const durationRegexp = new RegExp(
  "^P(\\d+D)?(T(((\\d+H)(\\d+M)?(\\d+([.,]\\d+)?S)?)|((\\d+H)?(\\d+M)(\\d+([.,]\\d+)?S)?)|((\\d+H)?(\\d+M)?(\\d+([.,]\\d+)?S))))?$",
  "i",
);

function validateSetting(setting: Setting) {
  switch (setting.type) {
    case "INTEGER":
      if (isNaN(parseInt(setting.value))) return false;
      break;
    case "PATTERN":
      try {
        new RegExp(setting.value);
        return true;
      } catch (e) {
        return false;
      }
    case "DURATION":
      return durationRegexp.test(setting.value);
    default:
      return false;
  }
  return true;
}
