import { useReducer } from "react";
import {
  SelectedChatContext,
  selectedChatReducer,
} from "./state/selected_chat.ts";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { TextFilterContext, textFilterReducer } from "./state/search_filter.ts";
import Router from "./router.tsx";
import AppBar from "./components/appbar.tsx";

const queryClient = new QueryClient();

export default function App() {
  const [selectedChat, selectedChatDispatch] = useReducer(
    selectedChatReducer,
    null,
  );
  const [textFilter, textFilterDispatch] = useReducer(textFilterReducer, "");

  return (
    <SelectedChatContext.Provider
      value={{ state: selectedChat, dispatch: selectedChatDispatch }}
    >
      <TextFilterContext.Provider
        value={{ state: textFilter, dispatch: textFilterDispatch }}
      >
        <QueryClientProvider client={queryClient}>
          <AppBar />
          <Router />
        </QueryClientProvider>
      </TextFilterContext.Provider>
    </SelectedChatContext.Provider>
  );
}
