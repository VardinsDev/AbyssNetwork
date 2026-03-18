# Contributing to AbyssNetwork

Thanks for your interest in contributing! This guide will get you set up with a local dev environment.

---

## Prerequisites

- **Java 25** — [Download OpenJDK 25](https://openjdk.org)
- **MariaDB** — [Download MariaDB](https://mariadb.org/download)
- **IntelliJ IDEA** — recommended IDE ([Download](https://www.jetbrains.com/idea))
- **Git**

---

## Setting Up the Dev Environment

### 1. Fork & Clone

Fork the repo on GitHub, then clone your fork:

```bash
git clone https://github.com/YOUR_USERNAME/AbyssNetwork.git
cd AbyssNetwork
```

### 2. Set Up MariaDB

Start MariaDB and create the database:

```sql
CREATE DATABASE abyssnetwork;
CREATE USER 'abyss'@'localhost' IDENTIFIED BY 'yourpassword';
GRANT ALL PRIVILEGES ON abyssnetwork.* TO 'abyss'@'localhost';
FLUSH PRIVILEGES;
```

The `players` table is created automatically on first server start.

### 3. Configure Environment

Copy the example env file and fill in your credentials:

```bash
cp .env.example .env
```

Edit `.env`:

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=abyssnetwork
DB_USER=abyss
DB_PASSWORD=yourpassword
```

### 4. Open in IntelliJ IDEA

1. Open IntelliJ IDEA
2. Click **Open** and select the `AbyssNetwork` folder
3. IntelliJ will detect the Gradle project and import it automatically
4. Wait for indexing to finish

### 5. Run the Server

Either use the Gradle run configuration in IntelliJ, or from the terminal:

```bash
./gradlew run
```

Connect to the server in Minecraft at `localhost:25565`.

---

## Project Structure

```
src/main/kotlin/
├── Main.kt                 # Entry point — start here
├── AbyssLogger.kt          # Logging utilities
├── database/               # MariaDB connection and player data
├── health/                 # Health and shield system
├── weapons/                # Weapon implementations
├── commands/               # Server commands
└── events/                 # Event listeners
```

---

## Making Changes

### Weapons

Weapons live in `src/main/kotlin/weapons/`. Each weapon is a class with a `register(eventHandler, instanceContainer)` method. Register your weapon in `Main.kt` alongside the existing ones.

### Events

Events live in `src/main/kotlin/events/`. Each event class has a `register(globalEventHandler)` method. Register new events in `Main.kt`.

### Database

Player data operations go in `PlayerRepository.kt`. Use `DatabaseManager.getConnection()` to get the active connection.

---

## Submitting a Pull Request

1. Create a branch: `git checkout -b feature/my-feature`
2. Make your changes
3. Test locally — make sure the server starts and your feature works in-game
4. Commit: `git commit -m "add my feature"`
5. Push: `git push origin feature/my-feature`
6. Open a Pull Request on GitHub against the `master` branch

Please keep PRs focused — one feature or fix per PR.

---

## Code Style

- Follow Kotlin naming conventions — classes in `PascalCase`, functions and variables in `camelCase`
- Keep weapon handlers self-contained in their own class
- Use `AbyssLogger` for all console output instead of `println`
- Avoid hardcoding values — use constants or config where possible
