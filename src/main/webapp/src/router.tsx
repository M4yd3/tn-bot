import ChatsView from "./routes/chats_view.tsx";
import Settings from "./routes/settings.tsx";

export default function Router() {
  if (window.location.pathname === "/") {
    window.location.pathname = "/chats";
    return;
  }

  if (window.location.pathname === "/chats") {
    return <ChatsView />;
  }

  if (window.location.pathname === "/settings") {
    return <Settings />;
  }

  return <div>404</div>;
}
