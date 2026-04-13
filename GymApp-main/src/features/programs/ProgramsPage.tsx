import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
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
import { Plus, ClipboardList, ChevronRight, Activity } from "lucide-react";

export default function ProgramsPage() {
  const [programs, setPrograms] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPrograms = async () => {
      const { data, error } = await supabase
        .from("programs")
        .select("*")
        .order("created_at", { ascending: false });

      if (!error) {
        setPrograms(data || []);
      }
      setLoading(false);
    };

    fetchPrograms();
  }, []);

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div className="flex items-center justify-between">
        <div className="space-y-1">
          <h1 className="text-3xl font-bold tracking-tight">Training Programs</h1>
          <p className="text-muted-foreground">Create and manage your workout routines.</p>
        </div>
        <Link to="/app/programs/new">
          <Button className="gap-2">
            <Plus className="h-4 w-4" />
            Create Program
          </Button>
        </Link>
      </div>
      
      {loading ? (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {[1, 2, 3].map((i) => (
            <Card key={i} className="animate-pulse">
              <CardHeader className="h-32 bg-muted/50 rounded-t-xl" />
              <CardContent className="h-24 bg-muted/20" />
            </Card>
          ))}
        </div>
      ) : programs.length > 0 ? (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {programs.map((program) => (
            <Card key={program.id} className="hover:border-primary/50 transition-all group overflow-hidden">
              <CardHeader className="pb-2">
                <div className="flex items-center justify-between">
                  <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center text-primary">
                    <ClipboardList className="h-5 w-5" />
                  </div>
                  {program.is_active && (
                    <span className="text-[10px] font-bold bg-green-500/10 text-green-500 px-2 py-0.5 rounded-full uppercase tracking-wider">
                      Active
                    </span>
                  )}
                </div>
                <CardTitle className="mt-4 group-hover:text-primary transition-colors">{program.name}</CardTitle>
                <CardDescription className="line-clamp-2 min-h-[2.5rem]">{program.description || "No description provided."}</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex items-center gap-4 text-xs text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Activity className="h-3 w-3" />
                    <span>4 Days / Week</span>
                  </div>
                </div>
              </CardContent>
              <CardFooter className="bg-muted/30 border-t py-3">
                <Link to={`/app/programs/${program.id}`} className="w-full">
                  <Button variant="ghost" className="w-full justify-between group-hover:bg-primary group-hover:text-primary-foreground transition-all">
                    Manage Program
                    <ChevronRight className="h-4 w-4" />
                  </Button>
                </Link>
              </CardFooter>
            </Card>
          ))}
        </div>
      ) : (
        <div className="border border-dashed rounded-2xl p-20 flex flex-col items-center justify-center text-center space-y-6 bg-muted/10">
          <div className="h-20 w-20 rounded-full bg-muted flex items-center justify-center">
            <ClipboardList className="h-10 w-10 text-muted-foreground" />
          </div>
          <div className="space-y-2 max-w-sm">
            <h3 className="text-xl font-bold">No programs yet</h3>
            <p className="text-muted-foreground">Create your first training program to start logging your progress and reaching your goals.</p>
          </div>
          <Link to="/app/programs/new">
            <Button size="lg" className="rounded-full px-8">
              Create Your First Program
            </Button>
          </Link>
        </div>
      )}
    </div>
  );
}
