import React, { useMemo } from "react";
import { type Chat, type User } from "../util/types.ts";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  deleteUserFromChat,
  fetchUsers,
  toggleUserExclusion,
} from "../util/api.ts";
import {
  type Column,
  type ColumnDef,
  createColumnHelper,
  flexRender,
  getCoreRowModel,
  getFacetedRowModel,
  getFacetedUniqueValues,
  getFilteredRowModel,
  getSortedRowModel,
  useReactTable,
} from "@tanstack/react-table";
import {
  BlockIcon,
  DownSortIcon,
  LoadingIcon,
  LoggedInIcon,
  NoSortIcon,
  UpSortIcon,
} from "./icons.tsx";
import Toggle from "./toggle.tsx";

export function Table({ chat }: { chat: Chat }) {
  const query = useQuery({
    queryKey: ["chatUsers", { id: chat.id }],
    queryFn: () => fetchUsers(chat.id),
    placeholderData: [],
  });

  if (query.isError) {
    return <div>Error: {query.error.message}</div>;
  }

  const users = query.data!;

  const queryClient = useQueryClient();
  const deleteMutation = useMutation({
    mutationFn: deleteUserFromChat,
    onSuccess: async ({ userId }) => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["chats"] }),
        queryClient.setQueryData(
          ["chatUsers", { id: chat.id }],
          users.filter((user) => user.id !== userId),
        ),
      ]);
    },
  });
  const excludeMutation = useMutation({
    mutationFn: toggleUserExclusion,
    onSuccess: async ({ chatId, userId }) => {
      if (chatId != chat.id) return;

      queryClient.setQueryData(
        ["chatUsers", { id: chat.id }],
        users.map((user) => {
          if (user.id !== userId) return user;

          user.isExcluded = !user.isExcluded;
          return user;
        }),
      );
    },
  });

  const columnHelper = createColumnHelper<User>();
  const columns = useMemo<ColumnDef<User, any>[]>(() => {
    const value: ColumnDef<User, any>[] = [
      columnHelper.accessor("isActive", {
        cell: (info) => info.getValue() && <LoggedInIcon />,
        header: "",
        enableColumnFilter: false,
      }),
      columnHelper.accessor("email", {
        header: "Почта",
      }),
      columnHelper.accessor("lastName", {
        header: "Фамилия",
      }),
      columnHelper.accessor("firstName", {
        header: "Имя",
      }),
      columnHelper.accessor("middleName", {
        header: "Отчество",
      }),
      columnHelper.accessor("registeredAt", {
        cell: (info) => info.getValue()?.toLocaleString() ?? "",
        header: "Зарегистрирован",
        enableColumnFilter: false,
      }),
      columnHelper.accessor("userName", {
        header: "Имя в ТГ",
      }),
      columnHelper.accessor("isExcluded", {
        header: "Исключён",
        enableColumnFilter: false,
      }),
    ];
    if (chat.isAdmin) {
      value.push(
        columnHelper.display({
          id: "delete-action",
          cell: (props) => {
            const userId = props.row.original.id;
            return (
              <button
                onClick={() =>
                  deleteMutation.mutate({
                    chatId: chat.id,
                    userId: userId,
                  })
                }
              >
                {deleteMutation.variables?.userId === userId &&
                deleteMutation.isPending ? (
                  <LoadingIcon />
                ) : (
                  <BlockIcon />
                )}
              </button>
            );
          },
          enableSorting: false,
          enableColumnFilter: false,
        }),
      );
    }
    return value;
  }, [chat]);

  const table = useReactTable<User>({
    columns,
    data: query.data!,
    initialState: {
      sorting: [
        {
          id: "isActive",
          desc: true,
        },
      ],
    },
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFacetedRowModel: getFacetedRowModel(),
    getFacetedUniqueValues: getFacetedUniqueValues(),
  });

  return (
    <table className="table min-w-full table-auto border-collapse text-sm">
      <thead>
        {table.getHeaderGroups().map((headerGroup) => (
          <tr key={headerGroup.id}>
            {headerGroup.headers.map((header) => (
              <th
                key={header.id}
                colSpan={header.colSpan}
                className={
                  header.column.getIsSorted()
                    ? "rounded-t-lg border-b border-gray-600 bg-gray-700 p-2 text-left align-top font-medium select-none"
                    : "rounded-t-lg border-b border-gray-600 p-2 text-left align-top font-medium transition select-none hover:bg-gray-800"
                }
              >
                <div
                  className={
                    header.column.getCanSort()
                      ? "flex cursor-pointer flex-row"
                      : "flex flex-row"
                  }
                  onClick={header.column.getToggleSortingHandler()}
                >
                  {header.column.getCanSort() && (
                    <button className="">
                      {renderSwitch(header.column.getIsSorted())}
                    </button>
                  )}
                  {flexRender(
                    header.column.columnDef.header,
                    header.getContext(),
                  )}
                </div>
                <div className="h-2" />
                {header.column.getCanFilter() && (
                  <Filter column={header.column} />
                )}
              </th>
            ))}
          </tr>
        ))}
      </thead>
      <tbody>
        {table.getRowModel().rows.map((row) => (
          <tr
            key={row.id}
            className="table-row bg-transparent transition hover:bg-gray-900"
          >
            {row.getVisibleCells().map((cell) => {
              return (
                <td
                  key={cell.id}
                  className="border-b border-gray-700 p-4 text-gray-400"
                >
                  {cell.column.id === "isExcluded" ? (
                    <Toggle
                      state={cell.row.original.isExcluded}
                      isSubmitting={
                        cell.row.original.id ===
                          excludeMutation.variables?.userId &&
                        excludeMutation.isPending
                      }
                      onToggle={() =>
                        excludeMutation.mutate({
                          chatId: chat.id,
                          userId: cell.row.original.id,
                        })
                      }
                    />
                  ) : (
                    flexRender(cell.column.columnDef.cell, cell.getContext())
                  )}
                </td>
              );
            })}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

function renderSwitch(value: string | boolean) {
  switch (value) {
    case false:
      return <NoSortIcon />;
    case "asc":
      return <UpSortIcon />;
    case "desc":
      return <DownSortIcon />;
  }
}

function Filter({ column }: { column: Column<User, any> }) {
  const columnFilterValue = column.getFilterValue();

  const sortedUniqueValues = React.useMemo(
    () =>
      Array.from(column.getFacetedUniqueValues().keys()).sort().slice(0, 5000),
    [column.getFacetedUniqueValues()],
  );

  return (
    <>
      <datalist id={column.id + "list"}>
        {sortedUniqueValues.map((value: any) => (
          <option value={value} key={value} />
        ))}
      </datalist>
      <DebouncedInput
        className="max-w-40 rounded-xl bg-gray-900 px-4 py-2 outline-0 outline-gray-600 hover:outline-2 focus:outline-2"
        onChange={(value) => column.setFilterValue(value)}
        placeholder={`Поиск... (${column.getFacetedUniqueValues().size})`}
        type="text"
        value={(columnFilterValue ?? "") as string}
        list={column.id + "list"}
      />
    </>
  );
}

function DebouncedInput({
  value: initialValue,
  onChange,
  debounce = 500,
  ...props
}: {
  value: string | number;
  onChange: (value: string | number) => void;
  debounce?: number;
} & Omit<React.InputHTMLAttributes<HTMLInputElement>, "onChange">) {
  const [value, setValue] = React.useState(initialValue);

  React.useEffect(() => {
    setValue(initialValue);
  }, [initialValue]);

  React.useEffect(() => {
    const timeout = setTimeout(() => {
      onChange(value);
    }, debounce);

    return () => clearTimeout(timeout);
  }, [value]);

  return (
    <input
      {...props}
      value={value}
      onChange={(e) => setValue(e.target.value)}
    />
  );
}
