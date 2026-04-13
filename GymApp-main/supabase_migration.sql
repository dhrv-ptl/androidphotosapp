-- Supabase Migration: IronTrack Gym Tracker
-- Run this in your Supabase SQL Editor

-- 1. Profiles (Extends Auth Users)
CREATE TABLE profiles (
  id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  username TEXT UNIQUE,
  full_name TEXT,
  avatar_url TEXT,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Gyms
CREATE TABLE gyms (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  location TEXT,
  owner_id UUID REFERENCES auth.users(id),
  is_public BOOLEAN DEFAULT false,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Equipment Brands
CREATE TABLE equipment_brands (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL UNIQUE,
  logo_url TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Machines
CREATE TABLE machines (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  brand_id UUID REFERENCES equipment_brands(id),
  description TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 5. Gym Machines (Inventory)
CREATE TABLE gym_machines (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  gym_id UUID REFERENCES gyms(id) ON DELETE CASCADE,
  machine_id UUID REFERENCES machines(id) ON DELETE CASCADE,
  quantity INTEGER DEFAULT 1,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 6. Muscle Groups
CREATE TABLE muscle_groups (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL UNIQUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 7. Exercises
CREATE TABLE exercises (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  description TEXT,
  muscle_group_id UUID REFERENCES muscle_groups(id),
  is_public BOOLEAN DEFAULT true,
  created_by UUID REFERENCES auth.users(id),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 8. Exercise Machine Links
CREATE TABLE exercise_machine_links (
  exercise_id UUID REFERENCES exercises(id) ON DELETE CASCADE,
  machine_id UUID REFERENCES machines(id) ON DELETE CASCADE,
  PRIMARY KEY (exercise_id, machine_id)
);

-- 9. Programs
CREATE TABLE programs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  description TEXT,
  is_active BOOLEAN DEFAULT false,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 10. Program Days
CREATE TABLE program_days (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  program_id UUID REFERENCES programs(id) ON DELETE CASCADE,
  name TEXT NOT NULL, -- e.g., "Push A", "Legs"
  day_order INTEGER NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 11. Program Day Exercises
CREATE TABLE program_day_exercises (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  day_id UUID REFERENCES program_days(id) ON DELETE CASCADE,
  exercise_id UUID REFERENCES exercises(id) ON DELETE CASCADE,
  exercise_order INTEGER NOT NULL,
  target_sets INTEGER,
  target_reps TEXT, -- e.g., "8-12"
  target_rir INTEGER, -- Reps In Reserve
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 12. Sessions (Workout Logs)
CREATE TABLE sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
  program_day_id UUID REFERENCES program_days(id),
  gym_id UUID REFERENCES gyms(id),
  name TEXT, -- e.g., "Push Day"
  start_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  end_time TIMESTAMP WITH TIME ZONE,
  completed_at TIMESTAMP WITH TIME ZONE,
  duration_seconds INTEGER,
  notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 13. Session Exercises
CREATE TABLE session_exercises (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id UUID REFERENCES sessions(id) ON DELETE CASCADE,
  exercise_id UUID REFERENCES exercises(id) ON DELETE CASCADE,
  exercise_order INTEGER NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 14. Set Logs
CREATE TABLE set_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  session_exercise_id UUID REFERENCES session_exercises(id) ON DELETE CASCADE,
  set_number INTEGER NOT NULL,
  weight DECIMAL NOT NULL,
  reps INTEGER NOT NULL,
  rpe INTEGER,
  is_pr BOOLEAN DEFAULT false,
  is_completed BOOLEAN DEFAULT true,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 15. Set Videos
CREATE TABLE set_videos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  set_log_id UUID REFERENCES set_logs(id) ON DELETE CASCADE,
  video_url TEXT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 16. PRs (Personal Records)
CREATE TABLE prs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
  exercise_id UUID REFERENCES exercises(id) ON DELETE CASCADE,
  weight DECIMAL NOT NULL,
  reps INTEGER NOT NULL,
  achieved_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 17. Session Context Scores (How did it feel?)
CREATE TABLE session_context_scores (
  session_id UUID PRIMARY KEY REFERENCES sessions(id) ON DELETE CASCADE,
  energy_level INTEGER CHECK (energy_level BETWEEN 1 AND 10),
  sleep_quality INTEGER CHECK (sleep_quality BETWEEN 1 AND 10),
  stress_level INTEGER CHECK (stress_level BETWEEN 1 AND 10),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 18. Songs
CREATE TABLE songs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title TEXT NOT NULL,
  artist TEXT,
  spotify_url TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 19. PR Song Links (What were you listening to?)
CREATE TABLE pr_song_links (
  pr_id UUID REFERENCES prs(id) ON DELETE CASCADE,
  song_id UUID REFERENCES songs(id) ON DELETE CASCADE,
  PRIMARY KEY (pr_id, song_id)
);

-- 20. Macro Profiles
CREATE TABLE macro_profiles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
  protein INTEGER,
  carbs INTEGER,
  fats INTEGER,
  calories INTEGER,
  is_active BOOLEAN DEFAULT false,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 21. Reminders
CREATE TABLE reminders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  remind_at TIMESTAMP WITH TIME ZONE NOT NULL,
  is_completed BOOLEAN DEFAULT false,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 22. User Settings
CREATE TABLE user_settings (
  user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  theme TEXT DEFAULT 'dark',
  unit_system TEXT DEFAULT 'metric', -- 'metric' or 'imperial'
  notifications_enabled BOOLEAN DEFAULT true,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 23. Gym Visits
CREATE TABLE gym_visits (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
  gym_id UUID REFERENCES gyms(id) ON DELETE CASCADE,
  check_in TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  check_out TIMESTAMP WITH TIME ZONE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ROW LEVEL SECURITY (RLS)

-- Enable RLS on all tables
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE gyms ENABLE ROW LEVEL SECURITY;
ALTER TABLE equipment_brands ENABLE ROW LEVEL SECURITY;
ALTER TABLE machines ENABLE ROW LEVEL SECURITY;
ALTER TABLE gym_machines ENABLE ROW LEVEL SECURITY;
ALTER TABLE muscle_groups ENABLE ROW LEVEL SECURITY;
ALTER TABLE exercises ENABLE ROW LEVEL SECURITY;
ALTER TABLE programs ENABLE ROW LEVEL SECURITY;
ALTER TABLE program_days ENABLE ROW LEVEL SECURITY;
ALTER TABLE program_day_exercises ENABLE ROW LEVEL SECURITY;
ALTER TABLE sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE session_exercises ENABLE ROW LEVEL SECURITY;
ALTER TABLE set_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE set_videos ENABLE ROW LEVEL SECURITY;
ALTER TABLE prs ENABLE ROW LEVEL SECURITY;
ALTER TABLE session_context_scores ENABLE ROW LEVEL SECURITY;
ALTER TABLE songs ENABLE ROW LEVEL SECURITY;
ALTER TABLE macro_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE reminders ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE gym_visits ENABLE ROW LEVEL SECURITY;

-- Shared Library Tables (Readable by all authenticated users)
CREATE POLICY "Public muscle groups are readable by all" ON muscle_groups FOR SELECT USING (true);
CREATE POLICY "Public exercises are readable by all" ON exercises FOR SELECT USING (is_public = true);
CREATE POLICY "Public brands are readable by all" ON equipment_brands FOR SELECT USING (true);
CREATE POLICY "Public machines are readable by all" ON machines FOR SELECT USING (true);

-- User Private Data (Only readable/writable by owner)
CREATE POLICY "Users can manage their own profile" ON profiles FOR ALL USING (auth.uid() = id);
CREATE POLICY "Users can manage their own gyms" ON gyms FOR ALL USING (auth.uid() = owner_id);
CREATE POLICY "Users can manage their own programs" ON programs FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage their own program days" ON program_days FOR ALL USING (
  EXISTS (
    SELECT 1 FROM programs p
    WHERE p.id = program_days.program_id AND p.user_id = auth.uid()
  )
);
CREATE POLICY "Users can manage their own program day exercises" ON program_day_exercises FOR ALL USING (
  EXISTS (
    SELECT 1 FROM program_days pd
    JOIN programs p ON pd.program_id = p.id
    WHERE pd.id = program_day_exercises.day_id AND p.user_id = auth.uid()
  )
);
CREATE POLICY "Users can manage their own sessions" ON sessions FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage their own session exercises" ON session_exercises FOR ALL USING (
  EXISTS (
    SELECT 1 FROM sessions s
    WHERE s.id = session_exercises.session_id AND s.user_id = auth.uid()
  )
);
CREATE POLICY "Users can manage their own set logs" ON set_logs FOR ALL USING (
  EXISTS (
    SELECT 1 FROM session_exercises se
    JOIN sessions s ON se.session_id = s.id
    WHERE se.id = set_logs.session_exercise_id AND s.user_id = auth.uid()
  )
);
CREATE POLICY "Users can manage their own PRs" ON prs FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage their own settings" ON user_settings FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage their own reminders" ON reminders FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage their own macro profiles" ON macro_profiles FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage their own gym visits" ON gym_visits FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage their own set videos" ON set_videos FOR ALL USING (
  EXISTS (
    SELECT 1 FROM set_logs sl
    JOIN session_exercises se ON sl.session_exercise_id = se.id
    JOIN sessions s ON se.session_id = s.id
    WHERE sl.id = set_videos.set_log_id AND s.user_id = auth.uid()
  )
);
CREATE POLICY "Users can manage their own session context scores" ON session_context_scores FOR ALL USING (
  EXISTS (
    SELECT 1 FROM sessions s
    WHERE s.id = session_context_scores.session_id AND s.user_id = auth.uid()
  )
);
CREATE POLICY "Users can manage their own PR song links" ON pr_song_links FOR ALL USING (
  EXISTS (
    SELECT 1 FROM prs p
    WHERE p.id = pr_song_links.pr_id AND p.user_id = auth.uid()
  )
);
CREATE POLICY "Users can manage their own gym machines" ON gym_machines FOR ALL USING (
  EXISTS (
    SELECT 1 FROM gyms g
    WHERE g.id = gym_machines.gym_id AND g.owner_id = auth.uid()
  )
);

-- 24. Triggers for Profiles
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, full_name, avatar_url)
  VALUES (new.id, new.raw_user_meta_data->>'full_name', new.raw_user_meta_data->>'avatar_url');
  
  INSERT INTO public.user_settings (user_id)
  VALUES (new.id);
  
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();
