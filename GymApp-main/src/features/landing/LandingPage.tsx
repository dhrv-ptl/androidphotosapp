import { Link } from "react-router-dom";
import { Zap, ArrowRight, CheckCircle2, BarChart3, Users, Dumbbell } from "lucide-react";

export default function LandingPage() {
  return (
    <div className="flex flex-col min-h-screen bg-background text-foreground">
      <header className="px-4 lg:px-6 h-16 flex items-center border-b sticky top-0 bg-background/80 backdrop-blur-md z-50">
        <Link className="flex items-center justify-center gap-2" to="/">
          <Zap className="h-6 w-6 text-primary fill-primary" />
          <span className="font-bold text-xl tracking-tight">IronTrack</span>
        </Link>
        <nav className="ml-auto flex gap-4 sm:gap-6 items-center">
          <Link className="text-sm font-medium hover:text-primary transition-colors" to="#features">
            Features
          </Link>
          <Link className="text-sm font-medium hover:text-primary transition-colors" to="/login">
            Login
          </Link>
          <Link className="inline-flex h-9 items-center justify-center rounded-full bg-primary px-4 py-2 text-sm font-medium text-primary-foreground shadow transition-colors hover:bg-primary/90 focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50" to="/signup">
            Get Started
          </Link>
        </nav>
      </header>
      <main className="flex-1">
        <section className="w-full py-12 md:py-24 lg:py-32 xl:py-48 px-4">
          <div className="container mx-auto flex flex-col items-center text-center space-y-8">
            <div className="space-y-4 max-w-3xl">
              <h1 className="text-4xl font-extrabold tracking-tighter sm:text-5xl md:text-6xl lg:text-7xl">
                Track Your Gains with <span className="text-primary">Precision</span>
              </h1>
              <p className="mx-auto max-w-[700px] text-muted-foreground md:text-xl/relaxed lg:text-base/relaxed xl:text-xl/relaxed">
                The ultimate gym training tracker for serious lifters. Log sessions, analyze performance, and crush your PRs.
              </p>
            </div>
            <div className="flex flex-col sm:flex-row gap-4">
              <Link to="/signup" className="inline-flex h-11 items-center justify-center rounded-full bg-primary px-8 py-2 text-sm font-medium text-primary-foreground shadow transition-colors hover:bg-primary/90 focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                Start Tracking Free
                <ArrowRight className="ml-2 h-4 w-4" />
              </Link>
              <Link to="/login" className="inline-flex h-11 items-center justify-center rounded-full border border-input bg-background px-8 py-2 text-sm font-medium shadow-sm transition-colors hover:bg-accent hover:text-accent-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                View Demo
              </Link>
            </div>
            <div className="w-full max-w-5xl pt-12">
              <div className="relative rounded-xl border bg-card shadow-2xl overflow-hidden aspect-video">
                <img 
                  src="https://picsum.photos/seed/gym-dashboard/1200/800" 
                  alt="App Dashboard Preview" 
                  className="object-cover w-full h-full opacity-80"
                  referrerPolicy="no-referrer"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-background via-transparent to-transparent"></div>
              </div>
            </div>
          </div>
        </section>

        <section id="features" className="w-full py-12 md:py-24 lg:py-32 bg-accent/50">
          <div className="container mx-auto px-4">
            <div className="grid gap-12 lg:grid-cols-3">
              <div className="flex flex-col items-center text-center space-y-4">
                <div className="p-3 rounded-2xl bg-primary/10 text-primary">
                  <Dumbbell className="h-8 w-8" />
                </div>
                <h3 className="text-xl font-bold">Smart Logging</h3>
                <p className="text-muted-foreground">Quickly log sets, reps, and RPE. IronTrack remembers your previous weights to keep you progressing.</p>
              </div>
              <div className="flex flex-col items-center text-center space-y-4">
                <div className="p-3 rounded-2xl bg-primary/10 text-primary">
                  <BarChart3 className="h-8 w-8" />
                </div>
                <h3 className="text-xl font-bold">Deep Analytics</h3>
                <p className="text-muted-foreground">Visualize your volume, intensity, and PR trends over time with beautiful, interactive charts.</p>
              </div>
              <div className="flex flex-col items-center text-center space-y-4">
                <div className="p-3 rounded-2xl bg-primary/10 text-primary">
                  <Users className="h-8 w-8" />
                </div>
                <h3 className="text-xl font-bold">Gym Communities</h3>
                <p className="text-muted-foreground">Connect with your local gym, see who's training, and share your programs with friends.</p>
              </div>
            </div>
          </div>
        </section>
      </main>
      <footer className="border-t py-6 md:py-0">
        <div className="container mx-auto flex flex-col items-center justify-between gap-4 md:h-24 md:flex-row px-4">
          <p className="text-sm text-muted-foreground">
            © 2026 IronTrack. Built for the dedicated.
          </p>
          <div className="flex gap-4 text-sm text-muted-foreground">
            <Link to="#" className="hover:underline underline-offset-4">Privacy</Link>
            <Link to="#" className="hover:underline underline-offset-4">Terms</Link>
          </div>
        </div>
      </footer>
    </div>
  );
}
