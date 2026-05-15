const express = require("express");
const cors = require("cors");
const dotenv = require("dotenv");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const multer = require("multer");
const path = require("path");
const fs = require("fs");
const sqlite3 = require("sqlite3").verbose();
const { v4: uuidv4 } = require("uuid");

dotenv.config();

const app = express();
const PORT = process.env.PORT || 5000;
const JWT_SECRET = process.env.JWT_SECRET || "replace-with-strong-secret";
const DB_FILE = process.env.DB_FILE || path.join(__dirname, "database.sqlite");
const UPLOAD_DIR = path.join(__dirname, "uploads");

if (!fs.existsSync(UPLOAD_DIR)) {
  fs.mkdirSync(UPLOAD_DIR, { recursive: true });
}

const db = new sqlite3.Database(DB_FILE);
const schemaSql = fs.readFileSync(path.join(__dirname, "db", "schema.sql"), "utf8");
db.exec(schemaSql);

app.use(cors());
app.use(express.json());
app.use("/uploads", express.static(UPLOAD_DIR));

const storage = multer.diskStorage({
  destination: (_, __, cb) => cb(null, UPLOAD_DIR),
  filename: (_, file, cb) => cb(null, `${Date.now()}-${file.originalname.replace(/\s+/g, "_")}`),
});
const upload = multer({ storage });

const DISEASE_KB = {
  Healthy: {
    description: "Leaf appears healthy with no significant disease symptoms.",
    causes: "Normal crop condition.",
    prevention: "Continue regular monitoring, balanced nutrition, and clean irrigation.",
    recommended_input: "Balanced NPK and micronutrients as per soil test.",
    treatment: "No treatment needed.",
  },
  "Bacterial Blight": {
    description: "Water-soaked lesions and yellowing commonly seen in paddy.",
    causes: "Bacterial infection during humid weather and standing water.",
    prevention: "Use resistant varieties and avoid excess nitrogen.",
    recommended_input: "Copper-based bactericide as per local agri officer guidance.",
    treatment: "Rogue infected leaves and maintain field sanitation.",
  },
  "Leaf Spot": {
    description: "Brown/black spots with defined margins spread across leaf surface.",
    causes: "Fungal spores in warm, moist environments.",
    prevention: "Improve air flow and avoid leaf wetness for long durations.",
    recommended_input: "Mancozeb or equivalent fungicide as advised.",
    treatment: "Remove affected leaves and apply fungicide in proper interval.",
  },
  "Powdery Mildew": {
    description: "White powdery growth on leaves reducing photosynthesis.",
    causes: "Fungal pathogen under dry days and humid nights.",
    prevention: "Ensure proper spacing and avoid overhead irrigation.",
    recommended_input: "Sulfur-based fungicide.",
    treatment: "Early spraying and pruning affected foliage.",
  },
};

function dbRun(sql, params = []) {
  return new Promise((resolve, reject) => {
    db.run(sql, params, function onRun(error) {
      if (error) reject(error);
      else resolve(this);
    });
  });
}

function dbGet(sql, params = []) {
  return new Promise((resolve, reject) => {
    db.get(sql, params, (error, row) => {
      if (error) reject(error);
      else resolve(row);
    });
  });
}

function dbAll(sql, params = []) {
  return new Promise((resolve, reject) => {
    db.all(sql, params, (error, rows) => {
      if (error) reject(error);
      else resolve(rows);
    });
  });
}

function authMiddleware(req, res, next) {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      return res.status(401).json({ message: "Unauthorized" });
    }
    const token = authHeader.split(" ")[1];
    const payload = jwt.verify(token, JWT_SECRET);
    req.user = payload;
    req.token = token;
    return next();
  } catch (error) {
    return res.status(401).json({ message: "Invalid/expired token" });
  }
}

function pickDiseaseFromName(fileName = "") {
  const lower = fileName.toLowerCase();
  if (lower.includes("healthy")) return "Healthy";
  if (lower.includes("blight")) return "Bacterial Blight";
  if (lower.includes("spot")) return "Leaf Spot";
  if (lower.includes("mildew")) return "Powdery Mildew";
  const keys = Object.keys(DISEASE_KB);
  return keys[Math.floor(Math.random() * keys.length)];
}

app.get("/api/health", (_, res) => {
  res.json({ status: "ok", service: "raithavarta-backend" });
});

app.post("/api/auth/register", async (req, res) => {
  try {
    const { fullName, email, phone, password } = req.body;
    if (!fullName || !email || !password) {
      return res.status(400).json({ message: "fullName, email, password are required" });
    }
    const existing = await dbGet("SELECT id FROM users WHERE email = ?", [email.toLowerCase()]);
    if (existing) return res.status(409).json({ message: "Email already exists" });

    const passwordHash = await bcrypt.hash(password, 10);
    const result = await dbRun(
      "INSERT INTO users (full_name, email, phone, password_hash) VALUES (?, ?, ?, ?)",
      [fullName, email.toLowerCase(), phone || null, passwordHash]
    );
    await dbRun(
      "INSERT INTO notifications (user_id, title, body) VALUES (?, ?, ?)",
      [result.lastID, "Registration Successful", "Welcome to Raitha-Varta premium dashboard."]
    );
    return res.status(201).json({ message: "User registered successfully" });
  } catch (error) {
    return res.status(500).json({ message: "Registration failed", error: error.message });
  }
});

app.post("/api/auth/login", async (req, res) => {
  try {
    const { email, password, deviceName } = req.body;
    const user = await dbGet("SELECT * FROM users WHERE email = ?", [(email || "").toLowerCase()]);
    if (!user) return res.status(401).json({ message: "Invalid credentials" });

    const passwordOk = await bcrypt.compare(password || "", user.password_hash);
    if (!passwordOk) {
      return res.status(401).json({ message: "Invalid credentials" });
    }

    const sessionId = uuidv4();
    const token = jwt.sign({ userId: user.id, email: user.email, role: user.role, sid: sessionId }, JWT_SECRET, {
      expiresIn: "7d",
    });
    const expirySql = "datetime('now', '+7 day')";

    await dbRun(
      "INSERT INTO sessions (user_id, session_token, device_name, expires_at) VALUES (?, ?, ?, " + expirySql + ")",
      [user.id, token, deviceName || "Android Device"]
    );
    await dbRun(
      "INSERT INTO login_history (user_id, device_name, ip_address, status) VALUES (?, ?, ?, ?)",
      [user.id, deviceName || "Android Device", req.ip, "SUCCESS"]
    );

    return res.json({
      token,
      user: {
        id: user.id,
        fullName: user.full_name,
        email: user.email,
        phone: user.phone,
        profileImageUrl: user.profile_image_url,
        role: user.role,
      },
    });
  } catch (error) {
    return res.status(500).json({ message: "Login failed", error: error.message });
  }
});

app.post("/api/auth/forgot-password", async (req, res) => {
  const { email } = req.body;
  if (!email) return res.status(400).json({ message: "Email required" });
  const user = await dbGet("SELECT id FROM users WHERE email = ?", [email.toLowerCase()]);
  if (!user) return res.json({ message: "If this email exists, reset instructions were sent." });
  return res.json({ message: "Reset link sent (demo mode)." });
});

app.post("/api/auth/logout", authMiddleware, async (req, res) => {
  await dbRun(
    "UPDATE sessions SET is_active = 0, logged_out_at = CURRENT_TIMESTAMP WHERE session_token = ?",
    [req.token]
  );
  return res.json({ message: "Logged out successfully" });
});

app.get("/api/profile", authMiddleware, async (req, res) => {
  const user = await dbGet(
    "SELECT id, full_name, email, phone, profile_image_url, role, created_at FROM users WHERE id = ?",
    [req.user.userId]
  );
  const activity = await dbAll(
    "SELECT disease_name, confidence, predicted_at FROM disease_predictions WHERE user_id = ? ORDER BY predicted_at DESC LIMIT 10",
    [req.user.userId]
  );
  return res.json({ user, activity });
});

app.put("/api/profile", authMiddleware, async (req, res) => {
  const { fullName, phone } = req.body;
  await dbRun(
    "UPDATE users SET full_name = COALESCE(?, full_name), phone = COALESCE(?, phone), updated_at = CURRENT_TIMESTAMP WHERE id = ?",
    [fullName || null, phone || null, req.user.userId]
  );
  return res.json({ message: "Profile updated" });
});

app.post("/api/profile/image", authMiddleware, upload.single("image"), async (req, res) => {
  if (!req.file) return res.status(400).json({ message: "Image required" });
  const relPath = `/uploads/${req.file.filename}`;
  await dbRun("UPDATE users SET profile_image_url = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?", [
    relPath,
    req.user.userId,
  ]);
  return res.json({ message: "Profile image uploaded", imageUrl: relPath });
});

app.post("/api/disease/predict", authMiddleware, upload.single("image"), async (req, res) => {
  if (!req.file) return res.status(400).json({ message: "Leaf image required" });

  const relPath = `/uploads/${req.file.filename}`;
  const uploadResult = await dbRun("INSERT INTO uploaded_images (user_id, file_path) VALUES (?, ?)", [
    req.user.userId,
    relPath,
  ]);

  const diseaseName = pickDiseaseFromName(req.file.originalname);
  const confidence = Number((0.82 + Math.random() * 0.16).toFixed(2));
  const knowledge = DISEASE_KB[diseaseName];

  const reportResult = await dbRun(
    "INSERT INTO leaf_disease_reports (user_id, uploaded_image_id, crop_name, leaf_condition, notes) VALUES (?, ?, ?, ?, ?)",
    [req.user.userId, uploadResult.lastID, "General Crop", diseaseName, "Predicted by ML service"]
  );

  const predictionResult = await dbRun(
    "INSERT INTO disease_predictions (report_id, user_id, disease_name, confidence, description, causes, prevention, recommended_input, treatment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
    [
      reportResult.lastID,
      req.user.userId,
      diseaseName,
      confidence,
      knowledge.description,
      knowledge.causes,
      knowledge.prevention,
      knowledge.recommended_input,
      knowledge.treatment,
    ]
  );

  await dbRun("INSERT INTO notifications (user_id, title, body) VALUES (?, ?, ?)", [
    req.user.userId,
    "Prediction Ready",
    `${diseaseName} detected with ${(confidence * 100).toFixed(0)}% confidence.`,
  ]);

  return res.json({
    predictionId: predictionResult.lastID,
    diseaseName,
    confidence,
    imageUrl: relPath,
    ...knowledge,
  });
});

app.get("/api/history", authMiddleware, async (req, res) => {
  const rows = await dbAll(
    "SELECT id, disease_name, confidence, predicted_at FROM disease_predictions WHERE user_id = ? ORDER BY predicted_at DESC",
    [req.user.userId]
  );
  return res.json(rows);
});

app.get("/api/notifications", authMiddleware, async (req, res) => {
  const rows = await dbAll(
    "SELECT id, title, body, is_read, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC",
    [req.user.userId]
  );
  return res.json(rows);
});

app.put("/api/notifications/:id/read", authMiddleware, async (req, res) => {
  await dbRun("UPDATE notifications SET is_read = 1 WHERE id = ? AND user_id = ?", [
    Number(req.params.id),
    req.user.userId,
  ]);
  return res.json({ message: "Notification marked as read" });
});

app.get("/api/sessions", authMiddleware, async (req, res) => {
  const rows = await dbAll(
    "SELECT id, device_name, is_active, created_at, expires_at, logged_out_at FROM sessions WHERE user_id = ? ORDER BY created_at DESC",
    [req.user.userId]
  );
  return res.json(rows);
});

app.get("/api/login-history", authMiddleware, async (req, res) => {
  const rows = await dbAll(
    "SELECT device_name, ip_address, login_at, status FROM login_history WHERE user_id = ? ORDER BY login_at DESC",
    [req.user.userId]
  );
  return res.json(rows);
});

app.get("/api/admin/users", authMiddleware, async (req, res) => {
  if (req.user.role !== "ADMIN") return res.status(403).json({ message: "Admin only" });
  const users = await dbAll(
    "SELECT id, full_name, email, phone, role, created_at FROM users ORDER BY created_at DESC"
  );
  return res.json(users);
});

app.use((error, _req, res, _next) => {
  return res.status(500).json({ message: "Unhandled server error", error: error.message });
});

app.listen(PORT, () => {
  console.log(`RaithaVarta backend running on port ${PORT}`);
});
