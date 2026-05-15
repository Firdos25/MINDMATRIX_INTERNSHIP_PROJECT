-- USERS CRUD
INSERT INTO users (full_name, email, phone, password_hash) VALUES (?, ?, ?, ?);
SELECT id, full_name, email, phone, role FROM users WHERE id = ?;
UPDATE users SET full_name = ?, phone = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?;
DELETE FROM users WHERE id = ?;

-- LOGIN HISTORY CRUD
INSERT INTO login_history (user_id, device_name, ip_address, status) VALUES (?, ?, ?, ?);
SELECT * FROM login_history WHERE user_id = ? ORDER BY login_at DESC;
UPDATE login_history SET status = ? WHERE id = ?;
DELETE FROM login_history WHERE id = ?;

-- SESSIONS CRUD
INSERT INTO sessions (user_id, session_token, device_name, expires_at) VALUES (?, ?, ?, ?);
SELECT * FROM sessions WHERE user_id = ? ORDER BY created_at DESC;
UPDATE sessions SET is_active = 0, logged_out_at = CURRENT_TIMESTAMP WHERE session_token = ?;
DELETE FROM sessions WHERE id = ?;

-- LEAF DISEASE REPORTS CRUD
INSERT INTO leaf_disease_reports (user_id, uploaded_image_id, crop_name, leaf_condition, notes) VALUES (?, ?, ?, ?, ?);
SELECT * FROM leaf_disease_reports WHERE user_id = ? ORDER BY created_at DESC;
UPDATE leaf_disease_reports SET notes = ? WHERE id = ?;
DELETE FROM leaf_disease_reports WHERE id = ?;

-- UPLOADED IMAGES CRUD
INSERT INTO uploaded_images (user_id, file_path) VALUES (?, ?);
SELECT * FROM uploaded_images WHERE user_id = ? ORDER BY uploaded_at DESC;
UPDATE uploaded_images SET file_path = ? WHERE id = ?;
DELETE FROM uploaded_images WHERE id = ?;

-- DISEASE PREDICTIONS CRUD
INSERT INTO disease_predictions (report_id, user_id, disease_name, confidence, description, causes, prevention, recommended_input, treatment)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
SELECT * FROM disease_predictions WHERE user_id = ? ORDER BY predicted_at DESC;
UPDATE disease_predictions SET treatment = ? WHERE id = ?;
DELETE FROM disease_predictions WHERE id = ?;

-- NOTIFICATIONS CRUD
INSERT INTO notifications (user_id, title, body) VALUES (?, ?, ?);
SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC;
UPDATE notifications SET is_read = 1 WHERE id = ?;
DELETE FROM notifications WHERE id = ?;
