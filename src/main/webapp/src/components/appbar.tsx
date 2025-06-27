import { useContext } from "react";
import { TextFilterContext } from "../state/search_filter.ts";
import { SelectedChatContext } from "../state/selected_chat.ts";
import { ChatsIcon, SettingsIcon } from "./icons.tsx";

export default function AppBar() {
  return (
    <div className="fixed top-0 right-0 left-0 z-10 flex h-16 w-dvw flex-row items-center border-b-1 border-gray-800 bg-neutral-950 p-2 px-4">
      <SearchBar />
      <Title />
      {window.location.pathname === "/chats" && (
        <button
          onClick={() => (window.location.pathname = "/settings")}
          className="float-right"
        >
          <SettingsIcon size={32} />
        </button>
      )}
      {window.location.pathname === "/settings" && (
        <button onClick={() => (window.location.pathname = "/chats")}>
          <ChatsIcon size={32} />
        </button>
      )}
    </div>
  );
}

function SearchBar() {
  const { state, dispatch } = useContext(TextFilterContext);

  function onFilterTextChange(text: string) {
    const query = text.trim();

    if (query === state) return;

    dispatch(query);
  }

  return (
    <input
      className="h-12 w-[calc(var(--container-xs)-var(--spacing)*10)] rounded-xl bg-gray-900 px-4 py-2 outline-0 outline-gray-600 hover:outline-2 focus:outline-2"
      type="text"
      placeholder="Введите критерии поиска..."
      onChange={(event) => onFilterTextChange(event.target.value)}
    />
  );
}

function Title() {
  const { state: selectedChat } = useContext(SelectedChatContext);

  const title: string = (() => {
    switch (window.location.pathname) {
      case "/chats":
        if (selectedChat) return selectedChat.title;
        return "Чаты";
      case "/settings":
        return "Настройки";
      default:
        return "404";
    }
  })();

  return (
    <h1 className="flex-grow pl-8 text-3xl font-medium tracking-tight text-white">
      {title}
    </h1>
  );
}
