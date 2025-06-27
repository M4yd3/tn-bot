import ChatsList from "./chats_list.tsx";
import { type Chat } from "../util/types.ts";
import { useContext, useMemo } from "react";
import { TextFilterContext } from "../state/search_filter.ts";
import Fuse from "fuse.js";

export default function SearchableChatsList({ chats }: { chats: Chat[] }) {
  const { state: textFilter } = useContext(TextFilterContext);
  const fuse = useMemo(() => {
    return new Fuse(chats, {
      keys: ["title"],
      useExtendedSearch: true,
    });
  }, [chats]);

  const filteredChats = textFilter
    ? fuse.search(textFilter).map((result) => result.item)
    : chats;

  return <ChatsList chats={filteredChats} />;
}
