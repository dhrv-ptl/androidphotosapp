import React, { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { supabase } from "../../lib/supabase";
import { Button } from "../../components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from "../../components/ui/card";
import { Input } from "../../components/ui/input";
import { Label } from "../../components/ui/label";
import { toast } from "sonner";
import { 
  Loader2, 
  ChevronLeft, 
  Plus, 
  Trash2, 
  Save,
  Clock,
  Dumbbell,
  CheckCircle2
} from "lucide-react";

export default function NewSessionPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const programDayId = searchParams.get("program_day");
  
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [sessionName, setSessionName] = useState("New Session");
  const [exercises, setExercises] = useState<any[]>([]);

  useEffect(() => {
    const isValidUUID = (uuid: string) => {
      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
      return uuidRegex.test(uuid);
    };

    if (programDayId && isValidUUID(programDayId)) {
      fetchProgramDay();
    } else if (programDayId) {
      toast.error("Invalid program day ID. Please select a workout from your programs.");
    }
  }, [programDayId]);

  const fetchProgramDay = async () => {
    setLoading(true);
    try {
      const { data, error } = await supabase
        .from("program_days")
        .select(`
          name,
          program_day_exercises (
            exercise_id,
            exercise_order,
            target_sets,
            target_reps,
            target_rir,
            exercises (name)
          )
        `)
        .eq("id", programDayId)
        .single();

      if (error) throw error;

      setSessionName(data.name || "New Session");
      
      const mappedExercises = data.program_day_exercises.map((pde: any) => ({
        exercise_id: pde.exercise_id,
        name: pde.exercises.name,
        sets: Array.from({ length: pde.target_sets || 3 }, (_, i) => ({
          reps: pde.target_reps || "",
          weight: "",
          completed: false
        }))
      }));
      
      setExercises(mappedExercises);
    } catch (error: any) {
      toast.error("Error loading program: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAddSet = (exerciseIndex: number) => {
    const newExercises = [...exercises];
    newExercises[exerciseIndex].sets.push({ reps: "", weight: "", completed: false });
    setExercises(newExercises);
  };

  const handleRemoveSet = (exerciseIndex: number, setIndex: number) => {
    const newExercises = [...exercises];
    newExercises[exerciseIndex].sets.splice(setIndex, 1);
    setExercises(newExercises);
  };

  const handleUpdateSet = (exerciseIndex: number, setIndex: number, field: string, value: any) => {
    const newExercises = [...exercises];
    newExercises[exerciseIndex].sets[setIndex][field] = value;
    setExercises(newExercises);
  };

  const handleSaveSession = async () => {
    setSaving(true);
    try {
      const { data: { user } } = await supabase.auth.getUser();
      if (!user) throw new Error("Not authenticated");

      // 1. Create session
      const { data: session, error: sessionError } = await supabase
        .from("sessions")
        .insert({
          user_id: user.id,
          name: sessionName,
          completed_at: new Date().toISOString(),
          duration_seconds: 3600 // Mock duration
        })
        .select()
        .single();

      if (sessionError) throw sessionError;

      // 2. Create session exercises and logs
      for (const ex of exercises) {
        const { data: sessionEx, error: exError } = await supabase
          .from("session_exercises")
          .insert({
            session_id: session.id,
            exercise_id: ex.exercise_id,
            exercise_order: exercises.indexOf(ex)
          })
          .select()
          .single();

        if (exError) throw exError;

        const logs = ex.sets.map((set: any, i: number) => ({
          session_exercise_id: sessionEx.id,
          set_number: i + 1,
          reps: parseInt(set.reps) || 0,
          weight: parseFloat(set.weight) || 0,
          is_completed: set.completed
        }));

        const { error: logsError } = await supabase.from("set_logs").insert(logs);
        if (logsError) throw logsError;
      }

      toast.success("Session saved successfully!");
      navigate("/app/dashboard");
    } catch (error: any) {
      toast.error("Error saving session: " + error.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex h-[50vh] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-20">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" onClick={() => navigate(-1)}>
            <ChevronLeft className="h-5 w-5" />
          </Button>
          <div>
            <Input 
              value={sessionName} 
              onChange={(e) => setSessionName(e.target.value)}
              className="text-2xl font-bold bg-transparent border-none p-0 h-auto focus-visible:ring-0"
            />
            <p className="text-sm text-muted-foreground flex items-center gap-1">
              <Clock className="h-3 w-3" />
              Started just now
            </p>
          </div>
        </div>
        <Button onClick={handleSaveSession} disabled={saving || exercises.length === 0} className="gap-2">
          {saving ? <Loader2 className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
          Finish Session
        </Button>
      </div>

      {exercises.length === 0 ? (
        <Card className="border-dashed">
          <CardContent className="flex flex-col items-center justify-center py-12 space-y-4">
            <div className="h-12 w-12 rounded-full bg-primary/10 flex items-center justify-center text-primary">
              <Dumbbell className="h-6 w-6" />
            </div>
            <div className="text-center">
              <h3 className="font-semibold">No exercises added</h3>
              <p className="text-sm text-muted-foreground">Add exercises to start tracking your session.</p>
            </div>
            <Button variant="outline" className="gap-2">
              <Plus className="h-4 w-4" />
              Add Exercise
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-6">
          {exercises.map((ex, exIndex) => (
            <Card key={exIndex}>
              <CardHeader className="pb-3">
                <div className="flex items-center justify-between">
                  <CardTitle className="text-lg">{ex.name}</CardTitle>
                  <Button variant="ghost" size="icon" className="text-destructive">
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-12 gap-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider px-2">
                  <div className="col-span-1 text-center">Set</div>
                  <div className="col-span-4">Weight (kg)</div>
                  <div className="col-span-4">Reps</div>
                  <div className="col-span-3 text-center">Done</div>
                </div>
                
                <div className="space-y-2">
                  {ex.sets.map((set: any, setIndex: number) => (
                    <div key={setIndex} className="grid grid-cols-12 gap-4 items-center">
                      <div className="col-span-1 text-center font-medium text-sm">
                        {setIndex + 1}
                      </div>
                      <div className="col-span-4">
                        <Input 
                          type="number" 
                          placeholder="0"
                          value={set.weight}
                          onChange={(e) => handleUpdateSet(exIndex, setIndex, "weight", e.target.value)}
                          className="h-9"
                        />
                      </div>
                      <div className="col-span-4">
                        <Input 
                          type="number" 
                          placeholder="0"
                          value={set.reps}
                          onChange={(e) => handleUpdateSet(exIndex, setIndex, "reps", e.target.value)}
                          className="h-9"
                        />
                      </div>
                      <div className="col-span-3 flex justify-center">
                        <Button 
                          variant={set.completed ? "default" : "outline"} 
                          size="icon" 
                          className={`h-9 w-9 rounded-full transition-all ${set.completed ? "bg-green-500 hover:bg-green-600" : ""}`}
                          onClick={() => handleUpdateSet(exIndex, setIndex, "completed", !set.completed)}
                        >
                          <CheckCircle2 className={`h-5 w-5 ${set.completed ? "text-white" : "text-muted-foreground"}`} />
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
                
                <Button 
                  variant="ghost" 
                  size="sm" 
                  className="w-full border-dashed border-2 h-10 gap-2"
                  onClick={() => handleAddSet(exIndex)}
                >
                  <Plus className="h-4 w-4" />
                  Add Set
                </Button>
              </CardContent>
            </Card>
          ))}
          
          <Button variant="outline" className="w-full py-8 border-dashed border-2 gap-2">
            <Plus className="h-5 w-5" />
            Add Exercise
          </Button>
        </div>
      )}
    </div>
  );
}
