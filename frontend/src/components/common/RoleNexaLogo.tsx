import { Link } from "react-router-dom";

interface RoleNexaLogoProps {
  to?: string;
  showWordmark?: boolean;
  className?: string;
}

export function RoleNexaLogo({
  to = "/",
  showWordmark = true,
  className = "",
}: RoleNexaLogoProps) {
  return (
    <Link
      to={to}
      aria-label="RoleNexa home"
      className={`inline-flex items-center gap-2.5 ${className}`}
    >
      <svg
        viewBox="0 0 40 40"
        className="h-10 w-10 shrink-0"
        fill="none"
        aria-hidden="true"
      >
        <rect
          x="1"
          y="1"
          width="38"
          height="38"
          rx="11"
          className="fill-primary"
        />
        <path
          d="M12 29V11h9.2c4.1 0 6.8 2.3 6.8 6 0 2.8-1.5 4.7-4.1 5.5L30 29h-6.2l-5.5-6.1h-.8V29H12Zm5.5-10.5h3.4c1.2 0 1.9-.5 1.9-1.5s-.7-1.5-1.9-1.5h-3.4v3Z"
          fill="white"
        />
      </svg>

      {showWordmark && (
        <span className="text-xl font-bold tracking-tight text-foreground">
          Role<span className="text-primary">Nexa</span>
        </span>
      )}
    </Link>
  );
}
