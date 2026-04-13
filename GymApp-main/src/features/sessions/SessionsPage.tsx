export default function SessionsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="space-y-1">
          <h1 className="text-3xl font-bold tracking-tight">Training History</h1>
          <p className="text-muted-foreground">Review your past workouts and performance.</p>
        </div>
      </div>
      
      <div className="border rounded-xl overflow-hidden">
        <table className="w-full text-sm text-left">
          <thead className="bg-muted/50 text-muted-foreground font-medium border-b">
            <tr>
              <th className="px-6 py-4">Date</th>
              <th className="px-6 py-4">Workout</th>
              <th className="px-6 py-4">Duration</th>
              <th className="px-6 py-4">Volume</th>
              <th className="px-6 py-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            <tr className="hover:bg-accent/30 transition-colors">
              <td className="px-6 py-4">Mar 28, 2026</td>
              <td className="px-6 py-4 font-medium">Push A</td>
              <td className="px-6 py-4">1h 15m</td>
              <td className="px-6 py-4">8,450 kg</td>
              <td className="px-6 py-4 text-right">
                <button className="text-primary hover:underline">View Details</button>
              </td>
            </tr>
            <tr className="hover:bg-accent/30 transition-colors">
              <td className="px-6 py-4">Mar 26, 2026</td>
              <td className="px-6 py-4 font-medium">Legs B</td>
              <td className="px-6 py-4">1h 45m</td>
              <td className="px-6 py-4">12,200 kg</td>
              <td className="px-6 py-4 text-right">
                <button className="text-primary hover:underline">View Details</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
