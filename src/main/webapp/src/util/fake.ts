import chats from "../dummy_data/chats.ts";
import { type Chat, type User } from "./types.ts";
import exclusions from "../dummy_data/exclusions.ts";
import users from "../dummy_data/users.ts";

export async function fetchFakeChats(): Promise<Chat[]> {
  await new Promise((r) => setTimeout(r, 200));

  return structuredClone(
    Array.from(chats.values()).map((c) => {
      const banned = bannedUsers.get(c.id);
      if (!banned) return c;
      c.users = c.users.filter((u) => !banned.includes(u));
      return c;
    }),
  );
}

export async function fetchFakeUsers(chatId: number): Promise<User[]> {
  await new Promise((r) => setTimeout(r, 200));

  const result: User[] = [];

  const chat = chats.get(chatId);
  const chatExclusions = exclusions.get(chatId) ?? [];
  if (!chat) return result;

  const banned = bannedUsers.get(chatId) ?? [];

  for (const userId of chat.users) {
    const user = users.get(userId);

    if (!user || banned?.includes(user.id)) continue;

    user.isExcluded = chatExclusions.includes(user.id);

    result.push(user);
  }

  return structuredClone(result);
}

export async function fakeDeleteUser(
  chatId: number,
  userId: number,
): Promise<boolean> {
  const chat = chats.get(chatId);

  if (!chat || !chat.users.includes(userId)) return false;

  bannedUsers.set(chatId, (bannedUsers.get(chatId) ?? []).concat(userId));
  return true;
}

export async function fakeExcludeUser(
  chatId: number,
  userId: number,
): Promise<boolean> {
  const chat = chats.get(chatId);

  if (!chat) return false;

  exclusions.set(chatId, (exclusions.get(chatId) ?? []).concat(userId));
  return true;
}

const bannedUsers = new Map<number, number[]>();
