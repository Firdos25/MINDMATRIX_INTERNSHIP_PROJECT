INSERT INTO users (full_name, email, phone, password_hash, role)
VALUES ('Demo Farmer', 'demo@raithavarta.com', '9876543210', '$2a$10$Dq8HL00fUBWQFxYo1EzV8ul6WN2rQ.9pM20qB0w94fQ6PRRxnQy6e', 'ADMIN');

INSERT INTO notifications (user_id, title, body)
VALUES
  (1, 'Welcome to Raitha-Varta', 'Your account is ready. Start leaf disease detection from dashboard.'),
  (1, 'Daily Tip', 'Water crops during early morning to reduce fungal spread.');
