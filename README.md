A web application designed to streamline internal operations for a badminton club. The system manages equipment tracking, automated training rosters, and event registrations.

### Features

Event Management: Create and manage club events with automated capacity tracking.

Registration System: Atomic registration processing with integrated racket reservation logic.

Attendance Tracking: Batch-initialize training rosters for club members.

Inventory Control: Track equipment status and member assignments.

Role-Based Access: Support for Members, Coaches, and Administrators.

### Tech Stack
Language: Java 17+

Build Tool: Maven

Database: SQL (JDBC)

Testing: JUnit 5, Mockito, AssertJ

Quality Assurance: JaCoCo (Code Coverage)

### Project Structure
The project follows a standard layered architecture to ensure separation of concerns:

dao/: Data Access Objects for database persistence.

model/: Entity classes representing club resources.

service/: Business logic and validation rules.

config/: Infrastructure configuration (Connection Pooling).
