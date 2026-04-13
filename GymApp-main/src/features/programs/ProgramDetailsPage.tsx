import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { supabase } from "../../lib/supabase";
import { Button } from "../../components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from "../../components/ui/card";
import { toast } from "sonner";
import { Loader2, ChevronLeft, Plus, Calendar, Activity, ChevronRight, Settings, Trash2 } from "lucide-react";

export default function ProgramDetailsPage() {
  const { id } = useParams();
  const [program, setProgram] = useState<any>(null);
  const [days, setDays] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProgramDetails = async () => {
      if (!id) return;

      const { data: programData, error: programError } = await supabase
        .from("programs")
        .select("*")
        .eq("id", id)
        .single();

      if (programError) {
        toast.error("Program not found");
        navigate("/app/programs");
        return;
      }

      const { data: daysData, error: daysError } = await supabase
        .from("program_days")
        .select("*")
        .eq("program_id", id)
        .order("day_order", { ascending: true });

      setProgram(programData);
      setDays(daysData || []);
      setLoading(false);
    };

    fetchProgramDetails();
  }, [id, navigate]);

  const handleAddDay = async () => {
    if (!id) return;
    const newDayOrder = days.length + 1;
    const newDayName = `Day ${newDayOrder}`;

    try {
      const { data, error } = await supabase
        .from("program_days")
        .insert({
          program_id: id,
          name: newDayName,
          day_order: newDayOrder,
        })
        .select()
        .single();

      if (error) throw error;

      setDays([...days, data]);
      toast.success("Training day added!");
    } catch (error: any) {
      toast.error(error.message || "Failed to add day");
    }
  };

  const handleDeleteProgram = async () => {
    if (!id || !window.confirm("Are you sure you want to delete this program? All training days and planned exercises will be lost.")) return;

    try {
      const { error } = await supabase
        .from("programs")
        .delete()
        .eq("id", id);

      if (error) throw error;

      toast.success("Program deleted successfully");
      navigate("/app/programs");
    } catch (error: any) {
      toast.error(error.message || "Failed to delete program");
    }
  };

  if (loading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <Link to="/app/programs">
            <Button variant="ghost" size="icon" className="rounded-full">
              <ChevronLeft className="h-5 w-5" />
            </Button>
          </Link>
          <div className="space-y-1">
            <h1 className="text-3xl font-bold tracking-tight">{program.name}</h1>
            <p className="text-muted-foreground">{program.description || "Training routine overview."}</p>
          </div>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="icon" className="rounded-full">
            <Settings className="h-4 w-4" />
          </Button>
          <Button variant="destructive" size="icon" className="rounded-full" onClick={handleDeleteProgram}>
            <Trash2 className="h-4 w-4" />
          </Button>
          <Button className="gap-2 rounded-full px-6" onClick={handleAddDay}>
            <Plus className="h-4 w-4" />
            Add Day
          </Button>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {days.length > 0 ? (
          days.map((day) => (
            <Card key={day.id} className="hover:border-primary/50 transition-all group overflow-hidden">
              <CardHeader className="pb-2">
                <div className="flex items-center justify-between">
                  <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center text-primary">
                    <Calendar className="h-5 w-5" />
                  </div>
                  <span className="text-[10px] font-bold bg-muted text-muted-foreground px-2 py-0.5 rounded-full uppercase tracking-wider">
                    Day {day.day_order}
                  </span>
                </div>
                <CardTitle className="mt-4 group-hover:text-primary transition-colors">{day.name}</CardTitle>
                <CardDescription>Planned exercises and targets.</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex items-center gap-4 text-xs text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Activity className="h-3 w-3" />
                    <span>0 Exercises</span>
                  </div>
                </div>
              </CardContent>
              <CardFooter className="bg-muted/30 border-t py-3">
                <Link to={`/app/programs/${id}/days/${day.id}`} className="w-full">
                  <Button variant="ghost" className="w-full justify-between group-hover:bg-primary group-hover:text-primary-foreground transition-all">
                    Edit Exercises
                    <ChevronRight className="h-4 w-4" />
                  </Button>
                </Link>
              </CardFooter>
            </Card>
          ))
        ) : (
          <div className="col-span-full border border-dashed rounded-2xl p-20 flex flex-col items-center justify-center text-center space-y-6 bg-muted/10">
            <div className="h-20 w-20 rounded-full bg-muted flex items-center justify-center">
              <Calendar className="h-10 w-10 text-muted-foreground" />
            </div>
            <div className="space-y-2 max-w-sm">
              <h3 className="text-xl font-bold">No training days added</h3>
              <p className="text-muted-foreground">Add training days (e.g., "Push", "Pull", "Legs") to organize your program.</p>
            </div>
            <Button size="lg" className="rounded-full px-8" onClick={handleAddDay}>
              Add Your First Training Day
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}
