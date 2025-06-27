import { type Chat } from "../util/types.ts";
import { SelectedChatContext } from "../state/selected_chat.ts";
import { useContext } from "react";
import { UsersIcon } from "./icons.tsx";

export default function ChatsList({ chats }: { chats: Chat[] }) {
  return (
    <div className="sticky top-16 bottom-0 left-0 h-full max-h-[calc(100dvh-(var(--spacing)*16))] w-xs overflow-y-auto px-4 py-2">
      <div className="flex flex-col gap-2">
        <ChatListHeader
          key="Управляемые чаты"
          title="Управляемые чаты"
        ></ChatListHeader>
        {chats
          .filter((chat) => chat.isAdmin)
          .map((chat) => (
            <ChatListItem chat={chat} key={chat.id}></ChatListItem>
          ))}
        <ChatListHeader
          key="Неуправляемые чаты"
          title="Неуправляемые чаты"
        ></ChatListHeader>
        {chats
          .filter((chat) => !chat.isAdmin)
          .map((chat) => (
            <ChatListItem chat={chat} key={chat.id}></ChatListItem>
          ))}
      </div>
    </div>
  );
}

function ChatListHeader({ title }: { title: string }) {
  return (
    <div className="border-y-2 border-gray-800 p-2 text-center">
      <span className="font-semibold">{title}</span>
    </div>
  );
}

function ChatListItem({ chat }: { chat: Chat }) {
  const { state, dispatch } = useContext(SelectedChatContext);

  return (
    <div
      className={
        state?.id === chat.id
          ? "flex items-center justify-between gap-1 rounded-lg bg-gray-700 p-2 transition hover:scale-110"
          : "flex items-center justify-between gap-1 rounded-lg bg-gray-900 p-2 transition hover:scale-110 hover:bg-gray-800"
      }
      onClick={() => dispatch(chat)}
    >
      <span>{chat.title}</span>
      <div className="flex flex-row gap-0.5">
        <span>{chat.users.length}</span>
        <UsersIcon />
      </div>
    </div>
  );
}
