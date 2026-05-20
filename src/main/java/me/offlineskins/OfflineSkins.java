package me.offlineskins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Objects;

public final class OfflineSkins extends JavaPlugin {

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("setskin"), "Command setskin missing from plugin.yml")
                .setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("setskin")) {
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /setskin <player> <skinUrl>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        try {
            URL skinUrl = new URL(args[1]);

            PlayerProfile profile = target.getPlayerProfile().clone();
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(skinUrl);
            profile.setTextures(textures);

            target.setPlayerProfile(profile);

            sender.sendMessage("Skin applied.");
        } catch (Exception e) {
            sender.sendMessage("Failed to apply skin: " + e.getMessage());
            getLogger().warning("Skin apply failed for " + target.getName());
            e.printStackTrace();
        }

        return true;
    }
}
