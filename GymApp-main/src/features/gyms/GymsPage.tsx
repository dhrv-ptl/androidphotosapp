export default function GymsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="space-y-1">
          <h1 className="text-3xl font-bold tracking-tight">My Gyms</h1>
          <p className="text-muted-foreground">Manage the locations where you train.</p>
        </div>
        <button className="bg-primary text-primary-foreground px-4 py-2 rounded-md text-sm font-medium hover:bg-primary/90 transition-colors">
          Add Gym
        </button>
      </div>
      
      <div className="grid gap-6 md:grid-cols-2">
        <div className="p-6 border rounded-xl bg-card/50 backdrop-blur-sm space-y-4">
          <div className="flex justify-between items-start">
            <div>
              <h3 className="text-xl font-bold">PureGym London</h3>
              <p className="text-sm text-muted-foreground">123 Main St, London</p>
            </div>
            <span className="px-2 py-1 bg-primary/10 text-primary text-[10px] font-bold uppercase rounded">Primary</span>
          </div>
          <div className="flex gap-4 text-sm">
            <div>
              <p className="text-muted-foreground text-xs">Visits</p>
              <p className="font-medium">142</p>
            </div>
            <div>
              <p className="text-muted-foreground text-xs">Machines</p>
              <p className="font-medium">45</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
