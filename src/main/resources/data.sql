-- Seed Initial Users (Password for all is 'password123')
INSERT INTO users (first_name, last_name, email, password_hash, role, has_own_racket) VALUES
                                                                                          ('Admin', 'User', 'admin@badminton.com', '$2a$10$wYq73WvSlbM9B2gXwzY7e.nLpYI3Z6kH6OQjOn8v1ZqPca6XvEfeC', 'ADMIN', true),
                                                                                          ('Coach', 'Dan', 'dan@badminton.com', '$2a$10$wYq73WvSlbM9B2gXwzY7e.nLpYI3Z6kH6OQjOn8v1ZqPca6XvEfeC', 'COACH', true),
                                                                                          ('John', 'Doe', 'john@badminton.com', '$2a$10$wYq73WvSlbM9B2gXwzY7e.nLpYI3Z6kH6OQjOn8v1ZqPca6XvEfeC', 'MEMBER', false);

-- Seed Base Rackets
INSERT INTO rackets (brand, model, status) VALUES
                                               ('Yonex', 'Astrox 99 Game', 'AVAILABLE'),
                                               ('Yonex', 'Voltric 0.5DG', 'AVAILABLE'),
                                               ('Victor', 'Thruster K 15', 'AVAILABLE'),
                                               ('Lining', 'Tectonic 7', 'MAINTENANCE');

-- Seed An Event
INSERT INTO events (name, description, event_date, start_time, end_time, event_type, requires_partner, max_participants, registration_deadline) VALUES
    ('Friday Night Free Play', 'Open floor matches for everyone.', '2026-07-10', '18:00:00', '21:00:00', 'FREE_PLAY', false, 24, '2026-07-09 23:59:59');