import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { supabase } from "../../lib/supabase";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import { Label } from "../../components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from "../../components/ui/card";
import { toast } from "sonner";
import { Loader2, ChevronLeft, Save } from "lucide-react";

export default function NewProgramPage() {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const { data: { user } } = await supabase.auth.getUser();
      if (!user) throw new Error("User not found");

      const { data, error } = await supabase
        .from("programs")
        .insert({
          name,
          description,
          user_id: user.id,
        })
        .select()
        .single();

      if (error) throw error;

      toast.success("Program created successfully!");
      navigate(`/app/programs/${data.id}`);
    } catch (error: any) {
      toast.error(error.message || "Failed to create program");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div className="flex items-center gap-4">
        <Link to="/app/programs">
          <Button variant="ghost" size="icon" className="rounded-full">
            <ChevronLeft className="h-5 w-5" />
          </Button>
        </Link>
        <div className="space-y-1">
          <h1 className="text-3xl font-bold tracking-tight">New Program</h1>
          <p className="text-muted-foreground">Define your training goals and routine.</p>
        </div>
      </div>

      <form onSubmit={handleCreate}>
        <Card className="bg-card/50 backdrop-blur-sm shadow-xl">
          <CardHeader>
            <CardTitle>Program Details</CardTitle>
            <CardDescription>Give your program a name and a brief description of its focus.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="name">Program Name</Label>
              <Input 
                id="name" 
                placeholder="e.g., PPL Hypertrophy, 5/3/1 Strength" 
                value={name}
                onChange={(e) => setName(e.target.value)}
                required 
                className="h-11"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="description">Description (Optional)</Label>
              <textarea 
                id="description" 
                placeholder="Describe your goals, frequency, or any special instructions..." 
                className="w-full min-h-[120px] bg-background border rounded-md px-3 py-2 text-sm focus:ring-1 focus:ring-primary outline-none transition-all"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
            </div>
          </CardContent>
          <CardFooter className="bg-muted/30 border-t py-6 flex justify-end gap-4">
            <Link to="/app/programs">
              <Button variant="ghost" type="button">Cancel</Button>
            </Link>
            <Button type="submit" className="gap-2 px-8" disabled={loading}>
              {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
              Create Program
            </Button>
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}
