export type Chat = {
  id: number;
  title: string;
  isAdmin: boolean;
  users: number[];
};

export type User = {
  id: number;
  email: string | null;
  isActive: boolean;
  firstName: string | null;
  lastName: string | null;
  middleName: string | null;
  registeredAt: Date | null;
  userName: string | null;
  isExcluded: boolean;
};

export type Setting = {
  id: number;
  name: string;
  value: string;
  type: SettingType;
};

export type SettingType = "INTEGER" | "PATTERN" | "DURATION";
