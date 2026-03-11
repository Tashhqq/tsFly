package club.tashhq.fly;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Main extends JavaPlugin {
    private boolean pluginEnabled;
    private List<String> allowedWorlds;
    private List<String> tsInfo;
    private List<String> consoleStartup;
    private List<String> consoleShutdown;

    // sound configurations
    private SoundConfig soundToggle;
    private SoundConfig soundWorldDisabled;
    private SoundConfig soundPluginDisabled;

    // placeholder text
    private String placeholderFlying;
    private String placeholderNotFlying;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        registerPlaceholders();
        // send colored messages to console
        sendConsoleMessages(consoleStartup);
        getLogger().info("tsFly plugin enabled");
    }

    @Override
    public void onDisable() {
        sendConsoleMessages(consoleShutdown);
        getLogger().info("tsFly plugin disabled");
    }

    private void loadConfig() {
        pluginEnabled = getConfig().getBoolean("enabled", true);
        allowedWorlds = getConfig().getStringList("allowed-worlds");
        tsInfo = getConfig().getStringList("tsinfo");
        consoleStartup = getConfig().getStringList("console.startup");
        consoleShutdown = getConfig().getStringList("console.shutdown");

        // sounds
        soundToggle = new SoundConfig(getConfig(), "sounds.toggle");
        soundWorldDisabled = new SoundConfig(getConfig(), "sounds.world-disabled");
        soundPluginDisabled = new SoundConfig(getConfig(), "sounds.plugin-disabled");

        // placeholders
        placeholderFlying = getConfig().getString("placeholders.fly_ph", "Flying");
        placeholderNotFlying = getConfig().getString("placeholders.nofly_ph", "Not flying");
    }

    private void registerPlaceholders() {
        PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("PlaceholderAPI") != null) {
            try {
                // ensure the class is available at compile time
                new me.clip.placeholderapi.expansion.PlaceholderExpansion() {
                    @Override
                    public boolean canRegister() {
                        return true;
                    }

                    @Override
                    public String getIdentifier() {
                        return "tsfly";
                    }

                    @Override
                    public String getAuthor() {
                        return "Tashhq";
                    }

                    @Override
                    public String getVersion() {
                        // refer to outer plugin instance for version
                        return Main.this.getDescription().getVersion();
                    }

                    @Override
                    public String onPlaceholderRequest(Player player, String identifier) {
                        if (player == null) return "";
                        if (identifier.equalsIgnoreCase("status")) {
                            return player.isFlying() ? placeholderFlying : placeholderNotFlying;
                        }
                        return null;
                    }
                }.register();
                getLogger().info("PlaceholderAPI detected; tsFly placeholders registered");
            } catch (NoClassDefFoundError ex) {
                // PAPI not available at runtime despite plugin presence
                getLogger().warning("PlaceholderAPI classes not found, skipping placeholder registration");
            }
        }
    }

    private String msg(String path) {
        return getConfig().getString("messages." + path, "").replace('&', '§');
    }

    private void sendConsoleMessages(List<String> lines) {
        if (lines == null) return;
        for (String line : lines) {
            String colored = line.replace('&', '§').replace("%version%", getDescription().getVersion());
            getServer().getConsoleSender().sendMessage(colored);
        }
    }

    private boolean worldAllowed(String worldName) {
        if (allowedWorlds == null || allowedWorlds.isEmpty()) {
            return true;
        }
        return allowedWorlds.contains(worldName);
    }

    private void playSound(Player player, SoundConfig cfg) {
        if (cfg != null && cfg.enabled && player != null) {
            try {
                Sound s = Sound.valueOf(cfg.sound);
                player.playSound(player.getLocation(), s, cfg.volume, cfg.pitch);
            } catch (IllegalArgumentException ignored) {
                // invalid sound name in config
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("fly")) {
            // player toggle
            if (!pluginEnabled) {
                sender.sendMessage(msg("plugin-disabled"));
                if (sender instanceof Player) playSound((Player) sender, soundPluginDisabled);
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(msg("only-players"));
                return true;
            }
            Player player = (Player) sender;
            if (!worldAllowed(player.getWorld().getName())) {
                player.sendMessage(msg("world-disabled"));
                playSound(player, soundWorldDisabled);
                return true;
            }
            if (!player.hasPermission("tsfly.use")) {
                player.sendMessage(msg("no-permission"));
                return true;
            }
            boolean allow = player.getAllowFlight();
            player.setAllowFlight(!allow);
            player.setFlying(!allow);
            player.sendMessage(!allow ? msg("enabled") : msg("disabled"));
            playSound(player, soundToggle);
            return true;
        } else if (cmd.equals("tsfly")) {
            // admin utility command
            if (!sender.hasPermission("tsfly.admin")) {
                sender.sendMessage(msg("no-permission"));
                return true;
            }
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                // display information header
                if (tsInfo != null) {
                    for (String line : tsInfo) {
                        sender.sendMessage(line.replace('&', '§'));
                    }
                }
                sender.sendMessage(msg("admin-help"));
                return true;
            }
            if (args[0].equalsIgnoreCase("toggle") && args.length == 2) {
                Player target = getServer().getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(msg("player-not-found").replace("{player}", args[1]));
                    return true;
                }
                boolean allow = target.getAllowFlight();
                target.setAllowFlight(!allow);
                target.setFlying(!allow);
                sender.sendMessage(msg("admin-toggle").replace("{player}", target.getName()).replace("{state}", !allow ? "enabled" : "disabled"));
                target.sendMessage(!allow ? msg("enabled") : msg("disabled"));
                playSound(target, soundToggle);
                return true;
            }
            // unknown subcommand
            sender.sendMessage(msg("admin-help"));
            return true;
        }
        return false;
    }

    private static class SoundConfig {
        boolean enabled;
        String sound;
        float volume;
        float pitch;

        SoundConfig(org.bukkit.configuration.file.FileConfiguration cfg, String basePath) {
            enabled = cfg.getBoolean(basePath + ".enabled", true);
            sound = cfg.getString(basePath + ".sound", "ENTITY_PLAYER_LEVELUP");
            volume = (float) cfg.getDouble(basePath + ".volume", 1.0);
            pitch = (float) cfg.getDouble(basePath + ".pitch", 1.0);
        }
    }
}

