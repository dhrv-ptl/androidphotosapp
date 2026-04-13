import express from "express";
import { createServer as createViteServer } from "vite";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function startServer() {
  const app = express();
  const PORT = 3000;

  // API routes
  app.get("/api/health", (req, res) => {
    res.json({ status: "ok" });
  });

  // Programs API
  app.get("/api/programs", async (req, res) => {
    // In a real app, we would use the user's session from the request
    // For now, we'll return mock data or proxy to Supabase
    res.json({ programs: [] });
  });

  app.post("/api/programs", express.json(), async (req, res) => {
    const { name, description, user_id } = req.body;
    // Logic to create program in Supabase
    res.status(201).json({ id: "new-id", name, description });
  });

  app.get("/api/programs/:id", async (req, res) => {
    const { id } = req.params;
    res.json({ id, name: "Program Name", days: [] });
  });

  app.post("/api/programs/:id/days", express.json(), async (req, res) => {
    const { id } = req.params;
    const { name, day_order } = req.body;
    res.status(201).json({ id: "day-id", name, day_order });
  });

  app.get("/api/programs/:id/days/:dayId", async (req, res) => {
    const { dayId } = req.params;
    res.json({ id: dayId, name: "Day Name", exercises: [] });
  });

  // Vite middleware for development
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    // Serve static files in production
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*", (req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

startServer();
