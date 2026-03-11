# tsFly Bukkit/Spigot Plugin 1.8

**tsFly** is a lightweight, configurable plugin that adds flight toggling to your server. It provides a simple `/fly` command for players, plus a suite of administrative tools, customizable messages, sound effects, and optional PlaceholderAPI support.

---

## 🚀 Features

- `/fly` command toggles player flight (allowFlight + flying).
- Global **enable/disable switch** in configuration.
- **World whitelist**: restrict use to specific worlds.
- Configurable **messages** (color codes supported).
- **Sound effects** on enable/disable, world-denied, and when plugin is disabled.
- **Admin command** (`/tsfly`) with `toggle` and `help` subcommands.
- Configurable **header/info lines** shown in `/tsfly help`.
- Optional **PlaceholderAPI placeholders** for flight status.
- Permissions:
  - `tsfly.use` – use `/fly` (default: all players).
  - `tsfly.admin` – access admin utilities (default: ops).

---

## ⚙️ Configuration
A `config.yml` is generated automatically on first run. Below is a fully annotated example.

```yaml
# global toggle for all commands
enabled: true

# world whitelist (empty = all worlds)
allowed-worlds: []

# informational header for /tsfly help
tsinfo:
  - "&6tsFly by Tashhq"
  - "&7Lightweight flight toggle with admin tools"

sounds:
  toggle:
    enabled: true
    sound: "ENTITY_PLAYER_LEVELUP"
    volume: 1.0
    pitch: 1.0
  world-disabled:
    enabled: true
    sound: "ENTITY_VILLAGER_NO"
    volume: 1.0
    pitch: 1.0
  plugin-disabled:
    enabled: true
    sound: "BLOCK_ANVIL_LAND"
    volume: 1.0
    pitch: 1.0

messages:
  enabled: "&6&lFLY &bYou have started flying!"
  disabled: "&6&lFLY &cYou are no longer flying!"
  no-permission: "&cYou do not have permission to fly."
  only-players: "&6&lFLY &cOnly players can use this command."
  plugin-disabled: "&6&lFLY &cThis feature is currently disabled."
  world-disabled: "&6&lFLY &cYou cannot fly in this world."
  admin-help: "&eCommands:\n &7fly &f- toggle your own flight\n &7/tsfly help &f- this message\n &7/tsfly toggle <player> &f- toggle someone else's flight"
  player-not-found: "&cPlayer {player} is not online."
  admin-toggle: "&aFlight for {player} has been {state}."

placeholders:
  fly_ph: "Flying"
  nofly_ph: "Not flying"
```

- **sounds**: enable/disable and configure sound name, volume, and pitch for each event.
- **console**: lists of lines sent to the server console when the plugin starts or stops; supports `&` color codes and `%version%`.
- **placeholders**: used by the optional PlaceholderAPI integration. Replace `%tsfly_status%` in messages or configs.
- **tsinfo**: lines displayed at top of `/tsfly help`. Add branding, version, etc.

> Tip: edit color codes with `&` and they will convert to Minecraft colors in-game.

---

## 📥 Installation

1. Build the project (Maven/Gradle or plain `javac` and `jar`).
2. Drop the generated `tsFly.jar` into your server's `plugins/` folder.
3. Start or reload the server. `config.yml` will appear in `plugins/tsFly`.

> Compatible with Spigot/Bukkit 1.8 and later using the 1.8 API.

---

## 📌 Usage

### Player commands
- `/fly` – toggle your own flying ability (requires `tsfly.use`).

### Admin commands (`tsfly.admin` permission)
- `/tsfly help` – shows configured header text and available subcommands.
- `/tsfly toggle <player>` – force toggle another player's flight state.

> If the target player is offline, a configurable "player not found" message is sent.

### Placeholders
If you have [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/), the following placeholder becomes available:

| Placeholder | Description |
|-------------|-------------|
| `%tsfly_status%` | Returns the configured `fly_ph` or `nofly_ph` depending on player state |

---

## 📝 Permissions

| Permission     | Default | Description                        |
|----------------|---------|------------------------------------|
| `tsfly.use`     | `op`    | Allows use of `/fly` (not granted by default; assign to ranks) |
| `tsfly.admin`   | `op`    | Allows use of `/tsfly` admin tools |

---

## 💡 Notes

- Messages and sounds can be adjusted live by editing `config.yml` and reloading the plugin (requires server `/reload` or similar).
- The plugin checks for PlaceholderAPI on startup and registers placeholders only if the plugin is present.
- Sound names must match enum constants in the server version (Minecraft 1.8 list).

---

Enjoy flying! ⚙️🕊️
