import { createContext, type Dispatch } from "react";

export function textFilterReducer(_: string, action: string) {
  return action;
}

export const TextFilterContext = createContext<{
  state: string;
  dispatch: Dispatch<string>;
}>({
  state: "",
  dispatch: () => "",
});
