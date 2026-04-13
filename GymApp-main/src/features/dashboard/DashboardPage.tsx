import React, { useState, useEffect } from "react";
import { supabase } from "../../lib/supabase";
import { 
  Card, 
  CardContent, 
  CardDescription, 
  CardHeader, 
  CardTitle,
  CardFooter
} from "../../components/ui/card";
import { Button } from "../../components/ui/button";
import { 
  Play, 
  Trophy, 
  Calendar, 
  History,
  ChevronRight,
  Dumbbell,
  Clock,
  Zap,
  ArrowUpRight
} from "lucide-react";
import { formatDistanceToNow, format } from "date-fns";
import { useNavigate, Link } from "react-router-dom";

// Mock data structured for Supabase
const MOCK_DASHBOARD_DATA = {
  todayWorkout: {
    id: "00000000-0000-0000-0000-000000000000",
    name: "Push A (Hypertrophy)",
    exercises: [
      { name: "Incline DB Press", sets: 3, reps: "8-10" },
      { name: "Weighted Dips", sets: 3, reps: "6-8" },
      { name: "Lateral Raises", sets: 4, reps: "12-15" }
    ]
  },
  lastSession: {
    id: "session-123",
    name: "Legs B",
    completed_at: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(), // 2 days ago
  },
  recentPRs: [
    { id: "pr-1", exercise: "Deadlift", weight: 180, reps: 5, date: "2026-03-25" },
    { id: "pr-2", exercise: "Bench Press", weight: 100, reps: 3, date: "2026-03-24" },
  ],
  lastSessions: [
    { id: "s1", name: "Push A", date: "2026-03-27", volume: 8450, duration: "75m" },
    { id: "s2", name: "Legs B", date: "2026-03-26", volume: 12200, duration: "105m" },
    { id: "s3", name: "Pull A", date: "2026-03-24", volume: 9100, duration: "80m" },
    { id: "s4", name: "Push B", date: "2026-03-23", volume: 7800, duration: "70m" },
    { id: "s5", name: "Legs A", date: "2026-03-21", volume: 11500, duration: "95m" },
    { id: "s6", name: "Pull B", date: "2026-03-20", volume: 8900, duration: "85m" },
    { id: "s7", name: "Push A", date: "2026-03-18", volume: 8200, duration: "72m" },
  ]
};

export default function DashboardPage() {
  const [user, setUser] = useState<any>(null);
  const [data, setData] = useState(MOCK_DASHBOARD_DATA);
  const navigate = useNavigate();

  useEffect(() => {
    supabase.auth.getUser().then(({ data: { user } }) => {
      setUser(user);
    });
  }, []);

  const daysSinceLastSession = formatDistanceToNow(new Date(data.lastSession.completed_at), { addSuffix: false });

  const handleStartSession = () => {
    navigate("/app/sessions/new");
  };

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500 pb-12">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1">
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p className="text-muted-foreground">Welcome back, {user?.user_metadata?.full_name?.split(' ')[0] || "Lifter"}.</p>
        </div>
        <Button 
          size="lg" 
          className="rounded-full shadow-lg hover:shadow-primary/20 transition-all gap-2"
          onClick={handleStartSession}
        >
          <Play className="h-5 w-5 fill-current" />
          Quick Start Session
        </Button>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {/* Today's Planned Workout */}
        <Card className="lg:col-span-2 overflow-hidden border-primary/20 bg-gradient-to-br from-card to-primary/5">
          <CardHeader className="pb-2">
            <div className="flex items-center justify-between">
              <CardTitle className="text-xl flex items-center gap-2">
                <Calendar className="h-5 w-5 text-primary" />
                Today's Planned Workout
              </CardTitle>
              <span className="text-xs font-medium bg-primary/10 text-primary px-2 py-1 rounded-full uppercase tracking-wider">
                Recommended
              </span>
            </div>
            <CardDescription>Based on your current program: PPL Hypertrophy</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4 py-2">
              <h3 className="text-2xl font-bold text-primary">{data.todayWorkout.name}</h3>
              <div className="grid gap-3">
                {data.todayWorkout.exercises.map((ex, i) => (
                  <div key={i} className="flex items-center justify-between p-3 rounded-lg bg-background/50 border">
                    <div className="flex items-center gap-3">
                      <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center text-primary font-bold text-xs">
                        {i + 1}
                      </div>
                      <span className="font-medium">{ex.name}</span>
                    </div>
                    <span className="text-sm text-muted-foreground font-mono">{ex.sets} x {ex.reps}</span>
                  </div>
                ))}
              </div>
            </div>
          </CardContent>
          <CardFooter className="bg-primary/5 border-t border-primary/10 py-4">
            <Button className="w-full gap-2" onClick={() => navigate(`/app/sessions/new?program_day=${data.todayWorkout.id}`)}>
              <Play className="h-4 w-4 fill-current" />
              Start This Workout
            </Button>
          </CardFooter>
        </Card>

        {/* Stats Column */}
        <div className="space-y-6">
          {/* Days Since Last Session */}
          <Card className="bg-card/50 backdrop-blur-sm">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wider">Last Session</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-baseline gap-2">
                <span className="text-4xl font-bold">{daysSinceLastSession}</span>
                <span className="text-muted-foreground">ago</span>
              </div>
              <p className="text-sm text-muted-foreground mt-2 flex items-center gap-1">
                <History className="h-3 w-3" />
                {data.lastSession.name}
              </p>
            </CardContent>
          </Card>

          {/* Recent PRs */}
          <Card className="bg-card/50 backdrop-blur-sm">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground uppercase tracking-wider">Recent PRs</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {data.recentPRs.map((pr) => (
                <div 
                  key={pr.id} 
                  className="flex items-center justify-between group cursor-pointer"
                  onClick={() => navigate("/app/analytics")}
                >
                  <div className="flex items-center gap-3">
                    <div className="h-8 w-8 rounded-lg bg-yellow-500/10 flex items-center justify-center text-yellow-500">
                      <Trophy className="h-4 w-4" />
                    </div>
                    <div>
                      <p className="text-sm font-semibold">{pr.exercise}</p>
                      <p className="text-xs text-muted-foreground">{format(new Date(pr.date), 'MMM d, yyyy')}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-bold text-primary">{pr.weight} kg</p>
                    <p className="text-xs text-muted-foreground">{pr.reps} reps</p>
                  </div>
                </div>
              ))}
              <Button 
                variant="ghost" 
                size="sm" 
                className="w-full text-xs text-muted-foreground hover:text-primary"
                onClick={() => navigate("/app/analytics")}
              >
                View All PRs
                <ChevronRight className="ml-1 h-3 w-3" />
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Last 7 Sessions Summary */}
      <Card className="bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <div className="flex items-center justify-between">
            <div>
              <CardTitle>Recent Activity</CardTitle>
              <CardDescription>Your last 7 training sessions</CardDescription>
            </div>
            <Button variant="outline" size="sm" className="gap-2" onClick={() => navigate("/app/sessions")}>
              <History className="h-4 w-4" />
              Full History
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-1">
            <div className="grid grid-cols-4 md:grid-cols-5 text-xs font-semibold text-muted-foreground uppercase tracking-wider pb-4 px-4">
              <div className="col-span-1">Workout</div>
              <div className="col-span-1">Date</div>
              <div className="hidden md:block col-span-1">Duration</div>
              <div className="col-span-1">Volume</div>
              <div className="col-span-1 text-right">Progress</div>
            </div>
            <div className="space-y-2">
              {data.lastSessions.map((session) => (
                <div 
                  key={session.id} 
                  className="grid grid-cols-4 md:grid-cols-5 items-center p-4 rounded-xl hover:bg-accent/50 transition-colors cursor-pointer group border border-transparent hover:border-border"
                  onClick={() => navigate(`/app/sessions/${session.id}`)}
                >
                  <div className="col-span-1 flex items-center gap-3">
                    <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                      <Dumbbell className="h-4 w-4" />
                    </div>
                    <span className="font-medium text-sm truncate">{session.name}</span>
                  </div>
                  <div className="col-span-1 text-sm text-muted-foreground">
                    {format(new Date(session.date), 'MMM d')}
                  </div>
                  <div className="hidden md:flex col-span-1 items-center gap-1 text-sm text-muted-foreground">
                    <Clock className="h-3 w-3" />
                    {session.duration}
                  </div>
                  <div className="col-span-1 text-sm font-mono font-medium">
                    {session.volume.toLocaleString()} kg
                  </div>
                  <div className="col-span-1 text-right">
                    <div className="inline-flex items-center gap-1 text-xs font-medium text-green-500 bg-green-500/10 px-2 py-0.5 rounded-full">
                      <ArrowUpRight className="h-3 w-3" />
                      +4%
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

