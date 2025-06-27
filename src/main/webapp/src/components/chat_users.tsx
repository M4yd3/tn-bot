import { type Chat } from "../util/types.ts";
import { Table } from "./table.tsx";

export default function ChatUsers({ chat }: { chat: Chat }) {
  return (
    <div className="relative col-start-2 row-start-1 grid grid-cols-subgrid pt-16">
      <div className="grid w-full grid-cols-1">
        <div className="p-2">
          <div className="h-full overflow-x-auto">
            <Table chat={chat}></Table>
          </div>
        </div>
      </div>
    </div>
  );
}
