package org.mt17.ctfPlugin;

import com.google.common.reflect.ClassPath;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.mt17.ctfPlugin.commands.AdminCommand;
import org.mt17.ctfPlugin.commands.TeamCommand;
import org.mt17.ctfPlugin.config.ConfigManager;
import org.mt17.ctfPlugin.config.TeamConfig;
import org.mt17.ctfPlugin.game.GameManager;
import org.mt17.ctfPlugin.game.HolyGroundZone;
import org.mt17.ctfPlugin.mechanics.DownedSystem;
import org.mt17.ctfPlugin.mechanics.ItemDropManager;
import org.mt17.ctfPlugin.player.PlayerDataManager;
import org.mt17.ctfPlugin.scoreboard.ScoreboardManager;
import org.mt17.ctfPlugin.team.TeamManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.logging.Level;

public final class CtfPlugin extends JavaPlugin {

    private static CtfPlugin instance;

    private ConfigManager configManager;
    private TeamConfig teamConfig;
    private TeamManager teamManager;
    private PlayerDataManager playerDataManager;
    private HolyGroundZone holyGroundZone;
    private GameManager gameManager;
    private DownedSystem downedSystem;
    private ItemDropManager itemDropManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // 1. 各マネージャー層の依存構築
        this.configManager = new ConfigManager(this);
        this.teamConfig = new TeamConfig(this);
        this.teamManager = new TeamManager(this);
        this.playerDataManager = new PlayerDataManager();
        this.holyGroundZone = new HolyGroundZone(this);
        this.itemDropManager = new ItemDropManager();
        this.downedSystem = new DownedSystem(this);
        this.scoreboardManager = new ScoreboardManager(this);

        this.gameManager = new GameManager(this);
        this.gameManager.startScheduleTask();

        // 2. コマンド・リスナー登録
        registerCommands();
        registerAllListeners();


        // タイマータスク開始
        this.gameManager.startScheduleTask();

        getLogger().info("[CTF] プラグインが正常に起動しました。");
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        if (this.gameManager != null) {
            this.gameManager.stopScheduleTask();
        }
        if (this.teamConfig != null) {
            this.teamConfig.save();
        }
        getLogger().info("[CTF] プラグインが無効化されました。");
    }

    private void registerCommands() {
        // AdminCommand の登録
        AdminCommand adminCmd = new AdminCommand(this);
        org.bukkit.command.defaults.BukkitCommand admin = new org.bukkit.command.defaults.BukkitCommand("admin") {
            @Override
            public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                return adminCmd.onCommand(sender, this, commandLabel, args);
            }

            @Override
            public java.util.List<String> tabComplete(org.bukkit.command.CommandSender sender, String alias, String[] args) {
                return adminCmd.onTabComplete(sender, this, alias, args);
            }
        };
        admin.setPermission("ctf.admin");
        Bukkit.getCommandMap().register(getName(), admin);

        // TeamCommand の登録
        TeamCommand teamCmd = new TeamCommand(this);
        org.bukkit.command.defaults.BukkitCommand team = new org.bukkit.command.defaults.BukkitCommand("team") {
            @Override
            public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                return teamCmd.onCommand(sender, this, commandLabel, args);
            }

            @Override
            public java.util.List<String> tabComplete(org.bukkit.command.CommandSender sender, String alias, String[] args) {
                return teamCmd.onTabComplete(sender, this, alias, args);
            }
        };
        Bukkit.getCommandMap().register(getName(), team);
    }

    private void registerAllListeners() {
        try {
            ClassPath classPath = ClassPath.from(getClass().getClassLoader());
            int count = 0;

            // リスナー専用の listeners パッケージのみを走査
            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive("org.mt17.ctfPlugin.listeners")) {
                Class<?> clazz = Class.forName(classInfo.getName());

                // Listener インターフェースを実装している具象クラスのみを対象
                if (Listener.class.isAssignableFrom(clazz)
                        && !clazz.isInterface()
                        && !Modifier.isAbstract(clazz.getModifiers())) {

                    try {
                        Listener listener;
                        try {
                            Constructor<?> constructor = clazz.getDeclaredConstructor(CtfPlugin.class);
                            listener = (Listener) constructor.newInstance(this);
                        } catch (NoSuchMethodException e) {
                            Constructor<?> constructor = clazz.getDeclaredConstructor();
                            listener = (Listener) constructor.newInstance();
                        }

                        getServer().getPluginManager().registerEvents(listener, this);
                        count++;
                    } catch (Exception e) {
                        getLogger().log(Level.SEVERE, "[CTF] " + clazz.getSimpleName() + " の登録に失敗しました。", e);
                    }
                }
            }
            getLogger().info("[CTF] 合計 " + count + " 個のリスナーを自動登録しました。");

        } catch (IOException | ClassNotFoundException e) {
            getLogger().log(Level.SEVERE, "[CTF] クラスのスキャン中に例外が発生しました。", e);
        }
    }

    public static CtfPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public TeamConfig getTeamConfig() { return teamConfig; }
    public TeamManager getTeamManager() { return teamManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public HolyGroundZone getHolyGroundZone() { return holyGroundZone; }
    public GameManager getGameManager() { return gameManager; }
    public DownedSystem getDownedSystem() { return downedSystem; }
    public ItemDropManager getItemDropManager() { return itemDropManager; }

    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
}