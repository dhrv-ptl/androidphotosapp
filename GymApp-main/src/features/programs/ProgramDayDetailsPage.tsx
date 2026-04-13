import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
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
  GripVertical, 
  Save,
  Dumbbell,
  Search,
  X
} from "lucide-react";

export default function ProgramDayDetailsPage() {
  const { id: programId, dayId } = useParams();
  const [day, setDay] = useState<any>(null);
  const [exercises, setExercises] = useState<any[]>([]);
  const [availableExercises, setAvailableExercises] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showAddExercise, setShowAddExercise] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      if (!dayId) return;

      const { data: dayData, error: dayError } = await supabase
        .from("program_days")
        .select("*")
        .eq("id", dayId)
        .single();

      if (dayError) {
        toast.error("Day not found");
        navigate(`/app/programs/${programId}`);
        return;
      }

      const { data: exercisesData, error: exercisesError } = await supabase
        .from("program_day_exercises")
        .select("*, exercises(*)")
        .eq("day_id", dayId)
        .order("exercise_order", { ascending: true });

      const { data: allExercises, error: allExercisesError } = await supabase
        .from("exercises")
        .select("*")
        .eq("is_public", true);

      setDay(dayData);
      setExercises(exercisesData || []);
      setAvailableExercises(allExercises || []);
      setLoading(false);
    };

    fetchData();
  }, [dayId, programId, navigate]);

  const handleAddExercise = async (exercise: any) => {
    const newOrder = exercises.length + 1;
    try {
      const { data, error } = await supabase
        .from("program_day_exercises")
        .insert({
          day_id: dayId,
          exercise_id: exercise.id,
          exercise_order: newOrder,
          target_sets: 3,
          target_reps: "8-12",
        })
        .select("*, exercises(*)")
        .single();

      if (error) throw error;

      setExercises([...exercises, data]);
      setShowAddExercise(false);
      setSearchQuery("");
      toast.success(`${exercise.name} added!`);
    } catch (error: any) {
      toast.error(error.message || "Failed to add exercise");
    }
  };

  const handleUpdateExercise = async (id: string, updates: any) => {
    try {
      const { error } = await supabase
        .from("program_day_exercises")
        .update(updates)
        .eq("id", id);

      if (error) throw error;

      setExercises(exercises.map(ex => ex.id === id ? { ...ex, ...updates } : ex));
    } catch (error: any) {
      toast.error(error.message || "Failed to update exercise");
    }
  };

  const handleDeleteExercise = async (id: string) => {
    try {
      const { error } = await supabase
        .from("program_day_exercises")
        .delete()
        .eq("id", id);

      if (error) throw error;

      setExercises(exercises.filter(ex => ex.id !== id));
      toast.success("Exercise removed");
    } catch (error: any) {
      toast.error(error.message || "Failed to remove exercise");
    }
  };

  const filteredExercises = availableExercises.filter(ex => 
    ex.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500 pb-20">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <Link to={`/app/programs/${programId}`}>
            <Button variant="ghost" size="icon" className="rounded-full">
              <ChevronLeft className="h-5 w-5" />
            </Button>
          </Link>
          <div className="space-y-1">
            <h1 className="text-3xl font-bold tracking-tight">{day.name}</h1>
            <p className="text-muted-foreground">Configure planned exercises and targets.</p>
          </div>
        </div>
        <Button className="gap-2 rounded-full px-6" onClick={() => setShowAddExercise(true)}>
          <Plus className="h-4 w-4" />
          Add Exercise
        </Button>
      </div>

      <div className="space-y-4">
        {exercises.length > 0 ? (
          exercises.map((ex, index) => (
            <Card key={ex.id} className="bg-card/50 backdrop-blur-sm group">
              <CardContent className="p-4 md:p-6">
                <div className="flex flex-col md:flex-row md:items-center gap-6">
                  <div className="flex items-center gap-4 flex-1">
                    <div className="cursor-grab text-muted-foreground hover:text-primary transition-colors">
                      <GripVertical className="h-5 w-5" />
                    </div>
                    <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center text-primary">
                      <Dumbbell className="h-5 w-5" />
                    </div>
                    <div>
                      <h3 className="font-bold text-lg">{ex.exercises.name}</h3>
                      <p className="text-xs text-muted-foreground">Order: {index + 1}</p>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 md:flex items-center gap-4">
                    <div className="space-y-1.5">
                      <Label htmlFor={`sets-${ex.id}`} className="text-[10px] uppercase font-bold text-muted-foreground">Sets</Label>
                      <Input 
                        id={`sets-${ex.id}`}
                        type="number" 
                        value={ex.target_sets || ""} 
                        onChange={(e) => handleUpdateExercise(ex.id, { target_sets: parseInt(e.target.value) })}
                        className="w-20 h-9"
                      />
                    </div>
                    <div className="space-y-1.5">
                      <Label htmlFor={`reps-${ex.id}`} className="text-[10px] uppercase font-bold text-muted-foreground">Reps</Label>
                      <Input 
                        id={`reps-${ex.id}`}
                        type="text" 
                        value={ex.target_reps || ""} 
                        onChange={(e) => handleUpdateExercise(ex.id, { target_reps: e.target.value })}
                        className="w-24 h-9"
                      />
                    </div>
                    <div className="space-y-1.5">
                      <Label htmlFor={`rir-${ex.id}`} className="text-[10px] uppercase font-bold text-muted-foreground">RIR</Label>
                      <Input 
                        id={`rir-${ex.id}`}
                        type="number" 
                        placeholder="0-4"
                        value={ex.target_rir ?? ""} 
                        onChange={(e) => handleUpdateExercise(ex.id, { target_rir: e.target.value === "" ? null : parseInt(e.target.value) })}
                        className="w-20 h-9"
                      />
                    </div>
                    <div className="flex items-end h-full">
                      <Button 
                        variant="ghost" 
                        size="icon" 
                        className="text-muted-foreground hover:text-destructive transition-colors"
                        onClick={() => handleDeleteExercise(ex.id)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))
        ) : (
          <div className="border border-dashed rounded-2xl p-20 flex flex-col items-center justify-center text-center space-y-6 bg-muted/10">
            <div className="h-20 w-20 rounded-full bg-muted flex items-center justify-center">
              <Dumbbell className="h-10 w-10 text-muted-foreground" />
            </div>
            <div className="space-y-2 max-w-sm">
              <h3 className="text-xl font-bold">No exercises planned</h3>
              <p className="text-muted-foreground">Add exercises to this training day to build your routine.</p>
            </div>
            <Button size="lg" className="rounded-full px-8" onClick={() => setShowAddExercise(true)}>
              Add Your First Exercise
            </Button>
          </div>
        )}
      </div>

      {/* Add Exercise Modal/Overlay */}
      {showAddExercise && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80 backdrop-blur-sm animate-in fade-in duration-200">
          <Card className="w-full max-w-2xl max-h-[80vh] flex flex-col shadow-2xl">
            <CardHeader className="border-b">
              <div className="flex items-center justify-between">
                <CardTitle>Add Exercise</CardTitle>
                <Button variant="ghost" size="icon" onClick={() => setShowAddExercise(false)}>
                  <X className="h-5 w-5" />
                </Button>
              </div>
              <div className="relative mt-4">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input 
                  placeholder="Search exercises..." 
                  className="pl-10 h-11"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  autoFocus
                />
              </div>
            </CardHeader>
            <CardContent className="flex-1 overflow-y-auto p-0">
              <div className="divide-y">
                {filteredExercises.map((exercise) => (
                  <div 
                    key={exercise.id} 
                    className="flex items-center justify-between p-4 hover:bg-accent transition-colors cursor-pointer group"
                    onClick={() => handleAddExercise(exercise)}
                  >
                    <div className="flex items-center gap-4">
                      <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center text-primary">
                        <Dumbbell className="h-5 w-5" />
                      </div>
                      <div>
                        <p className="font-medium">{exercise.name}</p>
                        <p className="text-xs text-muted-foreground">{exercise.description || "No description."}</p>
                      </div>
                    </div>
                    <Button variant="ghost" size="sm" className="opacity-0 group-hover:opacity-100 transition-opacity">
                      Add
                    </Button>
                  </div>
                ))}
                {filteredExercises.length === 0 && (
                  <div className="p-12 text-center text-muted-foreground italic">
                    No exercises found matching "{searchQuery}"
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
