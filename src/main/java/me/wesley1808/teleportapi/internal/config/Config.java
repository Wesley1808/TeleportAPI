package me.wesley1808.teleportapi.internal.config;

public class Config {
    protected static Config instance = new Config();

    public String defaultTeleportEffect = "default";
    public boolean showMessagesOnOverlay = true;
    public int defaultWarmupTicks = 60;

    public Message messages = new Message();

    public static Config instance() {
        return instance;
    }

    public static class Message {
        public String teleportCountdown = "<yellow>Teleporting in ${seconds}...";
        public String teleportCountdownWithLabel = "<yellow>Teleporting to ${label} in ${seconds}...";
        public String teleportSuccess = "<green>Teleported";
        public String teleportSuccessWithLabel = "<green>Teleported to ${label}";
        public String teleportCancelled = "<red>Teleport cancelled.. ${reason}";
    }
}