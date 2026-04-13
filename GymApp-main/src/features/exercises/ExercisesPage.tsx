export default function ExercisesPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="space-y-1">
          <h1 className="text-3xl font-bold tracking-tight">Exercise Library</h1>
          <p className="text-muted-foreground">Browse and search for exercises to add to your routines.</p>
        </div>
      </div>
      
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {["Chest", "Back", "Legs", "Shoulders", "Arms", "Core"].map((muscle) => (
          <div key={muscle} className="p-4 border rounded-xl hover:border-primary transition-colors cursor-pointer group">
            <h3 className="font-bold group-hover:text-primary transition-colors">{muscle}</h3>
            <p className="text-xs text-muted-foreground mt-1">24 exercises available</p>
          </div>
        ))}
      </div>
    </div>
  );
}
