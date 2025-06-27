export function UsersIcon({ size = 24 }: { size?: number }) {
  return (
    <div title="Количество пользователей">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 24 24"
      >
        <path
          fill="currentColor"
          d="M10 3.5C8.036 3.5 6.421 5.055 6.421 7s1.615 3.5 3.58 3.5c1.654 0 3.06-1.103 3.463-2.613a2.644 2.644 0 1 0 .039-1.61C13.16 4.682 11.713 3.5 10 3.5M7.421 7c0-1.369 1.143-2.5 2.58-2.5c1.436 0 2.578 1.131 2.578 2.5S11.437 9.5 10 9.5C8.564 9.5 7.421 8.369 7.421 7m6.936.143a1.643 1.643 0 1 1 3.286 0a1.643 1.643 0 0 1-3.286 0M8.15 11.917c-.367-.237-.874-.406-1.394-.263q-.24.067-.478.142l-.985.316c-1.155.37-2.044 1.284-2.345 2.438l-.007.027l-.417 2.937c-.152 1.072.439 2.156 1.588 2.423C5.29 20.21 7.2 20.5 10 20.5s4.71-.29 5.888-.563c1.15-.267 1.74-1.351 1.588-2.423l-.417-2.937l-.007-.027c-.3-1.154-1.19-2.069-2.345-2.438l-.984-.316a12 12 0 0 0-.479-.142c-.52-.143-1.027.026-1.393.263c-.394.254-1.045.569-1.85.569s-1.458-.315-1.851-.57m-1.129.701c.155-.043.367-.003.586.138c.489.317 1.329.73 2.393.73c1.065 0 1.905-.413 2.394-.73c.218-.14.43-.18.585-.138q.22.06.439.13l.984.316c.833.267 1.458.915 1.675 1.711l.41 2.88c.09.636-.253 1.175-.825 1.308c-1.097.254-2.93.537-5.662.537s-4.564-.283-5.662-.537c-.571-.133-.914-.672-.824-1.308l.41-2.88c.216-.796.84-1.444 1.674-1.711l.985-.315q.218-.07.438-.13m11.162-2.32a1.05 1.05 0 0 0-.914.202c-.27.21-.748.5-1.269.5a.5.5 0 0 0 0 1c.858 0 1.556-.457 1.883-.71a.1.1 0 0 1 .043-.024h.004q.201.053.399.118l.685.225c.526.173.925.594 1.066 1.114l.283 2.049c.056.403-.156.705-.46.777q-.335.082-.813.16a.5.5 0 1 0 .16.988q.512-.085.884-.175c.895-.213 1.333-1.07 1.22-1.887l-.291-2.106l-.007-.027a2.62 2.62 0 0 0-1.73-1.843l-.685-.225a8 8 0 0 0-.458-.136"
        ></path>
      </svg>
    </div>
  );
}

export function LoggedInIcon({ size = 24 }: { size?: number }) {
  return (
    <div title="Зарегестрирован" className="text-green-600">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 24 24"
      >
        <path
          fill="currentColor"
          d="M9.836 2.034q.168.058.329.136l1.283.632a1.25 1.25 0 0 0 1.104 0l1.283-.632a2.75 2.75 0 0 1 3.682 1.253l.073.162l.063.167l.46 1.353c.125.368.414.656.781.781l1.354.46a2.75 2.75 0 0 1 1.581 3.819l-.631 1.283a1.25 1.25 0 0 0 0 1.104l.631 1.283a2.75 2.75 0 0 1-1.581 3.818l-1.354.46a1.25 1.25 0 0 0-.78.781l-.461 1.354a2.75 2.75 0 0 1-3.818 1.581l-1.283-.631a1.25 1.25 0 0 0-1.104 0l-1.283.631a2.75 2.75 0 0 1-3.818-1.581l-.46-1.354a1.25 1.25 0 0 0-.782-.78l-1.353-.461a2.75 2.75 0 0 1-1.582-3.818l.632-1.283a1.25 1.25 0 0 0 0-1.104l-.632-1.283a2.75 2.75 0 0 1 1.582-3.818l1.353-.46a1.25 1.25 0 0 0 .781-.782l.46-1.353a2.75 2.75 0 0 1 3.49-1.718m5.634 6.935l-5.42 5.42l-1.974-2.37a.75.75 0 1 0-1.152.96l2.5 3a.75.75 0 0 0 1.106.051l6-6a.75.75 0 1 0-1.06-1.06"
        ></path>
      </svg>
    </div>
  );
}

export function NoSortIcon({ size = 24 }: { size?: number }) {
  return (
    <div title="Нет сортировки">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 320 512"
      >
        <path
          fill="currentColor"
          d="M41 288h238c21.4 0 32.1 25.9 17 41L177 448c-9.4 9.4-24.6 9.4-33.9 0L24 329c-15.1-15.1-4.4-41 17-41m255-105L177 64c-9.4-9.4-24.6-9.4-33.9 0L24 183c-15.1 15.1-4.4 41 17 41h238c21.4 0 32.1-25.9 17-41"
        ></path>
      </svg>
    </div>
  );
}

export function UpSortIcon({ size = 24 }: { size?: number }) {
  return (
    <div title="По возрастанию">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 320 512"
      >
        <path
          fill="currentColor"
          d="M279 224H41c-21.4 0-32.1-25.9-17-41L143 64c9.4-9.4 24.6-9.4 33.9 0l119 119c15.2 15.1 4.5 41-16.9 41"
        ></path>
      </svg>
    </div>
  );
}

export function DownSortIcon({ size = 24 }: { size?: number }) {
  return (
    <div title="По убыванию">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 320 512"
      >
        <path
          fill="currentColor"
          d="M41 288h238c21.4 0 32.1 25.9 17 41L177 448c-9.4 9.4-24.6 9.4-33.9 0L24 329c-15.1-15.1-4.4-41 17-41"
        ></path>
      </svg>
    </div>
  );
}

export function BlockIcon({ size = 24 }: { size?: number }) {
  return (
    <div
      title="Заблокировать"
      className="text-red-700 transition hover:scale-120 hover:text-red-500"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 16 16"
      >
        <g
          fill="none"
          stroke="currentColor"
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={1.5}
        >
          <circle cx={8} cy={8} r={6.25}></circle>
          <path d="m4.25 11.75l8-8"></path>
        </g>
      </svg>
    </div>
  );
}

export function LoadingIcon({ size = 24 }: { size?: number }) {
  return (
    <div title="Загрузка">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 24 24"
      >
        <circle cx={12} cy={2} r={0} fill="currentColor">
          <animate
            attributeName="r"
            begin={0}
            calcMode="spline"
            dur="1s"
            keySplines="0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8"
            repeatCount="indefinite"
            values="0;2;0;0"
          ></animate>
        </circle>
        <circle
          cx={12}
          cy={2}
          r={0}
          fill="currentColor"
          transform="rotate(45 12 12)"
        >
          <animate
            attributeName="r"
            begin="0.125s"
            calcMode="spline"
            dur="1s"
            keySplines="0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8"
            repeatCount="indefinite"
            values="0;2;0;0"
          ></animate>
        </circle>
        <circle
          cx={12}
          cy={2}
          r={0}
          fill="currentColor"
          transform="rotate(90 12 12)"
        >
          <animate
            attributeName="r"
            begin="0.25s"
            calcMode="spline"
            dur="1s"
            keySplines="0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8"
            repeatCount="indefinite"
            values="0;2;0;0"
          ></animate>
        </circle>
        <circle
          cx={12}
          cy={2}
          r={0}
          fill="currentColor"
          transform="rotate(135 12 12)"
        >
          <animate
            attributeName="r"
            begin="0.375s"
            calcMode="spline"
            dur="1s"
            keySplines="0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8"
            repeatCount="indefinite"
            values="0;2;0;0"
          ></animate>
        </circle>
        <circle
          cx={12}
          cy={2}
          r={0}
          fill="currentColor"
          transform="rotate(180 12 12)"
        >
          <animate
            attributeName="r"
            begin="0.5s"
            calcMode="spline"
            dur="1s"
            keySplines="0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8"
            repeatCount="indefinite"
            values="0;2;0;0"
          ></animate>
        </circle>
        <circle
          cx={12}
          cy={2}
          r={0}
          fill="currentColor"
          transform="rotate(225 12 12)"
        >
          <animate
            attributeName="r"
            begin="0.625s"
            calcMode="spline"
            dur="1s"
            keySplines="0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8"
            repeatCount="indefinite"
            values="0;2;0;0"
          ></animate>
        </circle>
        <circle
          cx={12}
          cy={2}
          r={0}
          fill="currentColor"
          transform="rotate(270 12 12)"
        >
          <animate
            attributeName="r"
            begin="0.75s"
            calcMode="spline"
            dur="1s"
            keySplines="0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8"
            repeatCount="indefinite"
            values="0;2;0;0"
          ></animate>
        </circle>
        <circle
          cx={12}
          cy={2}
          r={0}
          fill="currentColor"
          transform="rotate(315 12 12)"
        >
          <animate
            attributeName="r"
            begin="0.875s"
            calcMode="spline"
            dur="1s"
            keySplines="0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8;0.2 0.2 0.4 0.8"
            repeatCount="indefinite"
            values="0;2;0;0"
          ></animate>
        </circle>
      </svg>
    </div>
  );
}

export function SettingsIcon({ size = 24 }: { size?: number }) {
  return (
    <div
      title="Настройки"
      className="text-gray-400 hover:scale-120 hover:text-gray-200"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 20 20"
      >
        <g transform="translate(10 10)">
          <path
            id="oouiSettings0"
            fill="currentColor"
            d="M1.5-10h-3l-1 6.5h5m0 7h-5l1 6.5h3"
          ></path>
          <use href="#oouiSettings0" transform="rotate(45)"></use>
          <use href="#oouiSettings0" transform="rotate(90)"></use>
          <use href="#oouiSettings0" transform="rotate(135)"></use>
        </g>
        <path
          fill="currentColor"
          d="M10 2.5a7.5 7.5 0 0 0 0 15a7.5 7.5 0 0 0 0-15v4a3.5 3.5 0 0 1 0 7a3.5 3.5 0 0 1 0-7"
        ></path>
      </svg>
    </div>
  );
}

export function ChatsIcon({ size = 24 }: { size?: number }) {
  return (
    <div
      title="Настройки"
      className="text-gray-400 hover:scale-120 hover:text-gray-200"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size}
        height={size}
        viewBox="0 0 256 256"
      >
        <path
          fill="currentColor"
          d="M232 96a16 16 0 0 0-16-16h-32V48a16 16 0 0 0-16-16H40a16 16 0 0 0-16 16v128a8 8 0 0 0 13 6.22L72 154v30a16 16 0 0 0 16 16h93.59L219 230.22a8 8 0 0 0 5 1.78a8 8 0 0 0 8-8Zm-42.55 89.78a8 8 0 0 0-5-1.78H88v-32h80a16 16 0 0 0 16-16V96h32v111.25Z"
        ></path>
      </svg>
    </div>
  );
}
