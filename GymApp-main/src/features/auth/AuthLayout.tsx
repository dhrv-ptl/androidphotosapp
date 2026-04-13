import { Outlet, Link } from "react-router-dom";
import { Zap } from "lucide-react";

export default function AuthLayout() {
  return (
    <div className="min-h-screen flex flex-col md:flex-row bg-background">
      <div className="hidden md:flex flex-col justify-between w-1/2 bg-primary p-12 text-primary-foreground relative overflow-hidden">
        <div className="relative z-10">
          <Link className="flex items-center gap-2 mb-12" to="/">
            <Zap className="h-8 w-8 fill-primary-foreground" />
            <span className="font-bold text-2xl tracking-tight">IronTrack</span>
          </Link>
          <h2 className="text-4xl font-bold mb-6 leading-tight">
            The only tracker you'll ever need to reach your peak.
          </h2>
          <p className="text-xl text-primary-foreground/80 max-w-md">
            Join thousands of athletes who use IronTrack to optimize their training and see real results.
          </p>
        </div>
        
        <div className="relative z-10">
          <div className="flex items-center gap-4 mb-4">
            <div className="flex -space-x-2">
              {[1, 2, 3, 4].map((i) => (
                <div key={i} className="h-10 w-10 rounded-full border-2 border-primary bg-accent overflow-hidden">
                  <img src={`https://i.pravatar.cc/150?u=${i}`} alt="User" />
                </div>
              ))}
            </div>
            <p className="text-sm font-medium">Joined by 10,000+ lifters</p>
          </div>
        </div>

        {/* Decorative background elements */}
        <div className="absolute top-0 right-0 -translate-y-1/2 translate-x-1/2 w-[600px] h-[600px] bg-white/10 rounded-full blur-3xl"></div>
        <div className="absolute bottom-0 left-0 translate-y-1/2 -translate-x-1/2 w-[400px] h-[400px] bg-black/10 rounded-full blur-3xl"></div>
      </div>

      <div className="flex-1 flex items-center justify-center p-6 md:p-12">
        <div className="w-full max-w-md space-y-8">
          <div className="md:hidden flex justify-center mb-8">
            <Link className="flex items-center gap-2" to="/">
              <Zap className="h-8 w-8 text-primary fill-primary" />
              <span className="font-bold text-2xl tracking-tight">IronTrack</span>
            </Link>
          </div>
          <Outlet />
        </div>
      </div>
    </div>
  );
}
