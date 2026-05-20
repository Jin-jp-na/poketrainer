# PokéTrainer

A full-stack Pokédex + Team Builder web app built with **Spring Boot 3** and **vanilla HTML/CSS/JS**. Browse all 151 Gen 1 Pokémon, explore their moves, abilities, and stats — then assemble a team and analyze its type coverage.

---

## Features

- **Pokédex** — Browse all 1025 Pokémon.
- **Detail Panel** — Tabs for Moves (Level Up / TM / Egg / Tutor), Abilities (with descriptions), and Misc info (flavor text, evolution chain)
- **Team Builder** — Pick up to 6 Pokémon, assign up to 4 moves each
  - **Defense Coverage** — 18-type matrix showing weaknesses, resistances, and immunities for every team member
  - **Offense Coverage** — See which types your team's moves cover super-effectively


---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.3.5, Java 17, Spring Data JPA |
| Database | MySQL 8.0 |
| Frontend | Vanilla HTML / CSS / JavaScript |
| External API | [PokeAPI](https://pokeapi.co) (moves, abilities, flavor text) |

---

## Prerequisites

- Java 17+
- MySQL 8.0+
- Maven (or use the included `mvnw` wrapper)

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/your-username/poketrainer.git
cd poketrainer
```

### 2. Create the database

Log in to MySQL and run:

```sql
CREATE DATABASE IF NOT EXISTS poketrainer_db;
```

### 3. Configure credentials

**Option A — Environment variables (recommended)**

```bash
# Linux / macOS
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password

# Windows PowerShell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_mysql_password"
```

**Option B — Local properties file**

Copy the example file and fill in your credentials:

```bash
cp application-local.properties.example src/main/resources/application-local.properties
```

Then edit `src/main/resources/application-local.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 4. Build and run

```bash
# Using the Maven wrapper (no Maven installation needed)
./mvnw spring-boot:run        # Linux / macOS
.\mvnw spring-boot:run        # Windows PowerShell
```

The app starts at **http://localhost:8080**

### 5. Import Pokémon data

On first run the database is empty. Trigger the import via:

```
GET http://localhost:8080/api/pokemon/import
```

This fetches all 1025 Pokémon from PokeAPI and stores them in MySQL.  
Import takes ~30 seconds. Refresh the page when done.

---

## Project Structure

```
poketrainer/
├── src/
│   ├── main/
│   │   ├── java/com/poketrainer/
│   │   │   ├── config/           # App config, data seeder
│   │   │   ├── controller/       # REST controllers (Pokemon, Team, User)
│   │   │   ├── dto/              # Request/response DTOs
│   │   │   ├── exception/        # Global exception handler
│   │   │   ├── model/            # JPA entities
│   │   │   ├── repository/       # Spring Data repositories
│   │   │   ├── service/          # Business logic
│   │   │   └── util/             # Stat calculator
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html    # Full frontend (single file)
│   └── test/
├── application-local.properties.example
├── pom.xml
└── README.md
```

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/pokemon` | List all Pokémon |
| `GET` | `/api/pokemon/{id}` | Get Pokémon by ID |
| `GET` | `/api/pokemon/import` | Import Gen 1 data from PokeAPI |
| `GET` | `/api/pokemon/ranking` | Pokémon ranked by base stat total |
| `POST` | `/api/users` | Create a user |
| `GET` | `/api/users/{id}/teams` | Get user's teams |
| `POST` | `/api/teams` | Create a team |
| `POST` | `/api/teams/{id}/pokemon` | Add Pokémon to a team |

---

## License

MIT
