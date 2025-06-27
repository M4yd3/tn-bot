import type { Chat, Setting, User } from "./types.ts";
import {
  fakeDeleteUser,
  fakeExcludeUser,
  fetchFakeChats,
  fetchFakeUsers,
} from "./fake.ts";

const isFake = false;

const authority = "http://localhost:8080/api";

export async function fetchChats(): Promise<Chat[]> {
  if (isFake) return fetchFakeChats();

  return fetch(`${authority}/chats`).then((r) => r.json());
}

export async function fetchUsers(chatId: number): Promise<User[]> {
  if (isFake) return fetchFakeUsers(chatId);

  return fetch(`${authority}/chats/${chatId}`).then((r) => r.json());
}

export async function deleteUserFromChat({
  chatId,
  userId,
}: {
  chatId: number;
  userId: number;
}): Promise<{ chatId: number; userId: number }> {
  let result;

  if (isFake) {
    result = fakeDeleteUser(chatId, userId);
    await new Promise((r) => setTimeout(r, 500));
  } else {
    result = await fetch(`${authority}/chats/${chatId}/${userId}`, {
      method: "DELETE",
    }).then((r) => r.json());
  }

  if (!result) throw new Error("Could not delete user");

  return { chatId, userId };
}

export async function toggleUserExclusion({
  chatId,
  userId,
}: {
  chatId: number;
  userId: number;
}): Promise<{ chatId: number; userId: number }> {
  let result;

  if (isFake) {
    result = fakeExcludeUser(chatId, userId);
    await new Promise((r) => setTimeout(r, 500));
  } else {
    result = await fetch(`${authority}/chats/exclude/${chatId}/${userId}`, {
      method: "POST",
    }).then((r) => r.json());
  }

  if (!result) throw new Error("Could not exclude user");

  return { chatId, userId };
}

export async function fetchSettings(): Promise<Setting[]> {
  const result: Setting[] = await fetch(`${authority}/settings`).then((r) =>
    r.json(),
  );
  result.sort((a, b) => a.id - b.id);
  return result;
}

export async function saveSetting(setting: Setting): Promise<Setting> {
  const result = await fetch(`${authority}/setting`, {
    method: "PUT",
    body: JSON.stringify(setting),
    headers: {
      "Content-Type": "application/json",
    },
  }).then((r) => r.json());

  if (!result) throw new Error("Could not save settings");

  return setting;
}
