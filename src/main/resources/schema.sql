DROP TABLE IF EXISTS racket_reservations CASCADE;
DROP TABLE IF EXISTS rackets CASCADE;
DROP TABLE IF EXISTS event_registrations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS attendances CASCADE;
DROP TABLE IF EXISTS sessions CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       has_own_racket BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sessions (
                          session_id SERIAL PRIMARY KEY,
                          session_date DATE NOT NULL,
                          start_time TIME NOT NULL,
                          end_time TIME NOT NULL,
                          session_type VARCHAR(50) NOT NULL
);

CREATE TABLE attendances (
                             attendance_id SERIAL PRIMARY KEY,
                             user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                             session_id INT NOT NULL REFERENCES sessions(session_id) ON DELETE CASCADE,
                             status VARCHAR(20) NOT NULL,
                             marked_at TIMESTAMP,
                             UNIQUE(user_id, session_id)
);

CREATE TABLE events (
                        event_id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        description TEXT,
                        event_date DATE NOT NULL,
                        start_time TIME NOT NULL,
                        end_time TIME NOT NULL,
                        event_type VARCHAR(50) NOT NULL,
                        requires_partner BOOLEAN DEFAULT FALSE,
                        max_participants INT NOT NULL,
                        registration_deadline TIMESTAMP NOT NULL
);

CREATE TABLE event_registrations (
                                     registration_id SERIAL PRIMARY KEY,
                                     event_id INT NOT NULL REFERENCES events(event_id) ON DELETE CASCADE,
                                     user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
                                     guest_name VARCHAR(100),
                                     guest_email VARCHAR(100),
                                     guest_phone VARCHAR(20),
                                     skill_level VARCHAR(50) NOT NULL,
                                     needs_racket BOOLEAN DEFAULT FALSE,
                                     partner_name VARCHAR(100),
                                     status VARCHAR(20) NOT NULL,
                                     registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rackets (
                         racket_id SERIAL PRIMARY KEY,
                         brand VARCHAR(50) NOT NULL,
                         model VARCHAR(50) NOT NULL,
                         status VARCHAR(20) NOT NULL
);

CREATE TABLE racket_reservations (
                                     reservation_id SERIAL PRIMARY KEY,
                                     racket_id INT NOT NULL REFERENCES rackets(racket_id) ON DELETE CASCADE,
                                     user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
                                     registration_id INT REFERENCES event_registrations(registration_id) ON DELETE CASCADE,
                                     reservation_type VARCHAR(20) NOT NULL,
                                     start_date DATE NOT NULL,
                                     end_date DATE,
                                     status VARCHAR(20) NOT NULL
);