import { LoadingIcon } from "./icons.tsx";

export default function Toggle({
  state = false,
  isSubmitting = false,
  onToggle,
}: {
  state: boolean;
  isSubmitting: boolean;
  onToggle?: (state?: boolean) => void | Promise<void>;
}) {
  return (
    <button
      onClick={() => onToggle && onToggle(!state)}
      className={
        state
          ? "group h-6 w-10 items-center rounded-full bg-gray-700 p-1 outline-2 outline-gray-700 transition duration-300 hover:outline-gray-600"
          : "group h-6 w-10 items-center rounded-full bg-gray-800 p-1 outline-2 outline-gray-700 transition duration-300 hover:outline-gray-600"
      }
    >
      <div
        className={
          state
            ? "grid h-4 w-4 translate-x-4 grid-cols-1 grid-rows-1 transition duration-300"
            : "grid h-4 w-4 grid-cols-1 grid-rows-1 transition duration-300"
        }
      >
        <span
          className={
            isSubmitting
              ? "rounded-full bg-transparent transition delay-50 group-hover:scale-120"
              : state
                ? "rounded-full bg-gray-400 shadow-md transition duration-300 group-hover:scale-120"
                : "rounded-full bg-gray-700 shadow-md transition duration-300 group-hover:scale-120"
          }
        >
          <div
            className={
              isSubmitting
                ? "transition delay-50"
                : "opacity-0 transition delay-50"
            }
          >
            <LoadingIcon size={16} />
          </div>
        </span>
      </div>
    </button>
  );
}
