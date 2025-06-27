import Sidebar from "../components/sidebar.tsx";
import ChatUsers from "../components/chat_users.tsx";
import { useContext } from "react";
import { SelectedChatContext } from "../state/selected_chat.ts";

export default function ChatsView() {
  const { state: selectedChat } = useContext(SelectedChatContext);

  return (
    <div className="grid min-h-dvh grid-cols-[var(--container-xs)_minmax(0,1fr)] grid-rows-[1fr_1px_auto_1px_auto]">
      <Sidebar />
      {selectedChat && <ChatUsers chat={selectedChat} />}
    </div>
  );
}
