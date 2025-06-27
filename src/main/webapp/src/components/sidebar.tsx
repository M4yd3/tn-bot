import { fetchChats } from "../util/api.ts";
import { useQuery } from "@tanstack/react-query";
import SearchableChatsList from "./searchable_chats_list.tsx";

export default function Sidebar() {
  const query = useQuery({
    queryKey: ["chats"],
    queryFn: () => fetchChats().then((data) => data),
    placeholderData: [],
  });

  if (query.isError) {
    return <div>Error: {query.error.message}</div>;
  }

  return (
    <div className="relative col-start-1 row-span-full row-start-1 border-r-1 border-gray-800">
      <div className="absolute inset-0">
        <SearchableChatsList chats={query.data!} />
      </div>
    </div>
  );
}
