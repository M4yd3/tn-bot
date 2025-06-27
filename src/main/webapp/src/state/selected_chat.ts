import { type Chat } from "../util/types.ts";
import { createContext, type Dispatch } from "react";

export function selectedChatReducer(_: Chat | null, action: Chat) {
  return action;
}

export const SelectedChatContext = createContext<{
  state: Chat | null;
  dispatch: Dispatch<Chat>;
}>({
  state: null,
  dispatch: () => null,
});
