<div align="center">

<img src="https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=600&size=32&duration=3000&pause=1000&color=CBA6F7&center=true&vCenter=true&width=700&lines=AbyssNetwork;Custom+Minecraft+Server;Built+with+Kotlin+%26+Minestom" alt="Typing SVG" />

<br/>

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-1e1e2e?style=for-the-badge&logo=kotlin&logoColor=cba6f7)](https://kotlinlang.org)
[![Minestom](https://img.shields.io/badge/Minestom-2026.03.03-1e1e2e?style=for-the-badge&logo=minecraft&logoColor=a6e3a1)](https://minestom.net)
[![JVM](https://img.shields.io/badge/JVM-25-1e1e2e?style=for-the-badge&logo=openjdk&logoColor=fab387)](https://openjdk.org)
[![MySQL](https://img.shields.io/badge/MySQL-9.2.0-1e1e2e?style=for-the-badge&logo=mysql&logoColor=89b4fa)](https://mysql.com)

</div>

---

## 📖 Overview

**AbyssNetwork** is a custom Minecraft server built on [Minestom](https://minestom.net) — a lightweight, open-source server library that provides full control over game mechanics without vanilla overhead. Every feature, weapon, health system, and player interaction is implemented from scratch in Kotlin.

> Minestom is not a plugin platform — it is a library. There is no vanilla behaviour unless you implement it yourself.

The server runs in **online mode** with MySQL-backed persistent player data, a custom dual-bar health and shield system, two fully implemented weapons, team-based combat, and a peacetime system for safe building/exploration periods.

---

## ✨ Features

- ⚔️ **Custom Weapon System** — Assault Rifle and Rocket Launcher with raycasting, hitboxes, and cooldowns
- 🛡️ **Dual Health & Shield System** — Boss bar UI showing separate health and shield values
- 👥 **Team-Based Combat** — Red and Blue teams with friendly fire prevention
- ☮️ **Peacetime Mode** — Admin-toggled mode that disables all PvP
- 🗄️ **MySQL Persistence** — Player stats (kills, deaths, rank, team) saved to database
- 🌍 **Anvil World Loading** — Real Minecraft region files loaded via `AnvilLoader`
- 📦 **Full Block Placement Rules** — Stairs, slabs, doors, fences, signs, crops and more all behave correctly
- 📊 **MSPT Monitoring** — Server tick performance tracking
- 🎮 **Gamemode Switcher** — F3+F4 shortcut for quick gamemode switching
- 🔇 **Online Mode Auth** — Full Mojang authentication

---

## 🏗️ Architecture

```
me.totxy/
├── Main.kt                          # Server entry point, wires everything together
├── AbyssLogger.kt                   # Custom coloured console logger
│
├── database/
│   ├── DatabaseManager.kt           # MySQL connection, table creation, singleton
│   ├── PlayerData.kt                # Data class: uuid, username, kills, deaths, team, rank, isOpped
│   └── PlayerRepository.kt         # CRUD operations for player data
│
├── health/
│   └── HealthManagement.kt          # Dual boss bar health+shield system, damage, respawn logic
│
├── weapons/
│   ├── peaceTime.kt                 # PeaceTime singleton — global combat toggle
│   ├── ar/
│   │   └── ARHandler.kt             # Assault rifle: raycast, team check, hit detection, sounds
│   └── rocketlauncher/
│       └── RocketLauncherHandler.kt # Rocket: BlockDisplay entity, explosion radius, knockback, cooldown UI
│
├── commands/
│   └── peacetimeCommand.kt          # /peacetime command to toggle PeaceTime
│
└── events/
    ├── playerConfiguration.kt       # Spawning, team assignment, instance setup
    ├── playerLoaded.kt              # Health bar init on join
    ├── playerDisconnect.kt          # Cleanup on leave
    ├── tickEvent.kt                 # Per-tick health bar updates
    ├── gamemodeSwitcher.kt          # F3+F4 gamemode cycling
    ├── MSPTMonitor.kt               # Milliseconds per tick monitoring
    └── pickBlock.kt                 # Middle-click block picking
```

---

## ⚙️ Systems

### 🛡️ Health & Shield

Players have two independent stats tracked via Minestom `Tag`s and displayed as boss bars:

| Stat | Default | Bar Color |
|------|---------|-----------|
| Health | 100 | 🔴 Red |
| Shield | 100 | 🔵 Blue |

Damage hits shield first. If damage exceeds the current shield, the overflow carries over to health. At 0 health the player respawns, receives a death title screen, a brief blindness effect, and both bars reset to 100.

### ⚔️ Assault Rifle

- **Item:** `WOODEN_HOE`
- **Cooldown:** 25ms between shots
- **Damage:** 12 per hit
- **Mechanics:** Raycast fired from eye position in `0.5` block steps up to 100 blocks. Stops on non-air blocks. Detects hits by checking proximity to all player positions (including crouch height). Team hits show crit particles but deal no damage. Sounds play on hit for both shooter and target.

### 🚀 Rocket Launcher

- **Item:** `WOODEN_AXE`
- **Cooldown:** 10 seconds (shown as a `●○` action bar progress indicator)
- **Direct Hit Damage:** 80
- **Splash Damage:** Up to 40 (falls off linearly with distance)
- **Explosion Radius:** 4 blocks
- **Mechanics:** Spawns a `BLOCK_DISPLAY` entity (red wool) as the rocket visual. Moves at velocity 40 per tick. Per-tick collision detection with multi-offset sampling to prevent tunnelling. On impact: removes entity, spawns explosion particles, plays sounds for all players, applies distance-scaled damage and knockback to nearby players.

### 👥 Teams

Two teams are registered via Minestom's `TeamManager`:

| Team | Glow Color |
|------|-----------|
| `glow_red` | 🔴 Red |
| `glow_blue` | 🔵 Blue |

Team membership is stored as a player `Tag`. Both weapons check tags before dealing damage — friendly fire is silently blocked with a crit particle effect instead.

### ☮️ Peacetime

A global `companion object` boolean (`PeaceTime.isActive`) that blocks all weapon damage when active. Toggled via the `/peacetime` command. Players attempting to shoot during peacetime receive a chat warning.

---

## 🗄️ Database

### Schema

```sql
CREATE TABLE IF NOT EXISTS players (
    uuid         VARCHAR(36)  PRIMARY KEY,
    username     VARCHAR(16)  NOT NULL,
    kills        INT          DEFAULT 0,
    deaths       INT          DEFAULT 0,
    team         INT          DEFAULT -1,
    player_rank  VARCHAR(32)  DEFAULT 'default',
    is_opped     BOOLEAN      DEFAULT FALSE
);
```

### `PlayerData`

```kotlin
data class PlayerData(
    val uuid: String,
    val username: String,
    var kills: Int = 0,
    var deaths: Int = 0,
    var team: Int = -1,       // -1 = unassigned
    var rank: String = "default",
    var isOpped: Boolean = false
)
```

Connection uses `autoReconnect=true` to handle dropped connections gracefully. The `DatabaseManager` is a singleton `object` that exposes the raw `Connection` for use by repositories.

---

## 🛠️ Tech Stack

| Dependency | Version | Purpose |
|---|---|---|
| `net.minestom:minestom` | `2026.03.03-1.21.11` | Core server framework |
| `com.mysql:mysql-connector-j` | `9.2.0` | MySQL JDBC driver |
| `org.slf4j:slf4j-simple` | `2.0.13` | Logging backend |
| `rocks.minestom:placement` | `0.1.0` | Block placement rules |
| `io.github.cdimascio:dotenv-kotlin` | `6.4.1` | `.env` config loading |
| Kotlin | `2.3.10` | Primary language |
| JVM | `25` | Runtime target |
| Gradle | Wrapper | Build system |

---

## 🚀 Getting Started

### Prerequisites

- Java 25+
- MySQL server
- Git

### 1. Clone

```bash
git clone https://github.com/VardinsDev/AbyssNetwork.git
cd AbyssNetwork
```

### 2. Configure Environment

Create a `.env` file in the project root:

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=abyssnetwork
DB_USER=your_user
DB_PASSWORD=your_password
```

### 3. Set Up the Database

```sql
CREATE DATABASE abyssnetwork;
```

The `players` table is created automatically on first startup.

### 4. Build & Run

```bash
./gradlew run
```

Or build a JAR first:

```bash
./gradlew build
java --add-opens=java.base/java.lang=ALL-UNNAMED \
     -XX:+EnableDynamicAgentLoading \
     -Djdk.reflect.useDirectMethodHandle=false \
     -jar build/libs/AbyssNetwork-1.0-SNAPSHOT.jar
```

The server starts on `0.0.0.0:25565`.

---

## 📁 Project Structure

```
AbyssNetwork/
├── src/main/kotlin/       # All Kotlin source files
├── worlds/world/region/   # Minecraft Anvil region files (.mca)
├── build.gradle.kts       # Dependencies and build config
├── gradle.properties      # Gradle properties
├── .env                   # Environment config (not committed)
└── .gitignore
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit: `git commit -m "add my feature"`
4. Push: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

<div align="center">

Made with ❤️ by [VardinsDev](https://github.com/VardinsDev) • Built on [Minestom](https://minestom.net)

</div>
