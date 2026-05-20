package me.offlineskins;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OfflineSkins extends JavaPlugin implements CommandExecutor {

    private final File skinFolder = new File(getDataFolder(), "skins");
    private final Map<String, String> cache = new HashMap<>();

    @Override
    public void onEnable() {
        skinFolder.mkdirs();
        getCommand("setskin").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length != 2) {
            sender.sendMessage("/setskin <player> <file>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found");
            return true;
        }

        File file = new File(skinFolder, args[1] + ".png");
        if (!file.exists()) {
            sender.sendMessage("Skin not found");
            return true;
        }

        applySkin(target, file);

        sender.sendMessage("Skin applied!");
        return true;
    }

    private void applySkin(Player player, File file) {

        try {
            PlayerProfile profile = player.getPlayerProfile();
            PlayerTextures textures = profile.getTextures();

            // THIS is the correct Paper 1.21 method
            textures.setSkin(file.toURI().toURL());

            profile.setTextures(textures);
            player.setPlayerProfile(profile);

            // force client refresh
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(this, player);
                p.showPlayer(this, player);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
