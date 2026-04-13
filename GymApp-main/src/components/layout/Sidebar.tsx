import { NavLink } from "react-router-dom";
import { 
  LayoutDashboard, 
  ClipboardList, 
  History, 
  Dumbbell, 
  MapPin, 
  BarChart3, 
  Settings,
  Zap
} from "lucide-react";
import { cn } from "../../lib/utils";

const navigation = [
  { name: "Dashboard", href: "/app/dashboard", icon: LayoutDashboard },
  { name: "Programs", href: "/app/programs", icon: ClipboardList },
  { name: "Sessions", href: "/app/sessions", icon: History },
  { name: "Exercises", href: "/app/exercises", icon: Dumbbell },
  { name: "Gyms", href: "/app/gyms", icon: MapPin },
  { name: "Analytics", href: "/app/analytics", icon: BarChart3 },
  { name: "Settings", href: "/app/settings", icon: Settings },
];

export default function Sidebar() {
  return (
    <aside className="hidden md:flex flex-col w-64 border-r bg-card/50 backdrop-blur-sm">
      <div className="h-16 flex items-center px-6 border-b">
        <div className="flex items-center gap-2 font-bold text-xl tracking-tight">
          <Zap className="h-6 w-6 text-primary fill-primary" />
          <span>IronTrack</span>
        </div>
      </div>
      
      <nav className="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
        {navigation.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            className={({ isActive }) =>
              cn(
                "flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-colors",
                isActive
                  ? "bg-primary text-primary-foreground shadow-sm"
                  : "text-muted-foreground hover:bg-accent hover:text-accent-foreground"
              )
            }
          >
            <item.icon className="h-4 w-4 shrink-0" />
            {item.name}
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t">
        <div className="bg-primary/10 rounded-lg p-4">
          <p className="text-xs font-semibold text-primary uppercase tracking-wider mb-1">Pro Plan</p>
          <p className="text-xs text-muted-foreground mb-3">Unlock advanced analytics and video analysis.</p>
          <button className="w-full py-2 px-3 bg-primary text-primary-foreground rounded-md text-xs font-medium hover:bg-primary/90 transition-colors">
            Upgrade Now
          </button>
        </div>
      </div>
    </aside>
  );
}
