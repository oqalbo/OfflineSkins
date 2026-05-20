package me.offlineskins;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class OfflineSkins extends JavaPlugin implements CommandExecutor {

    private File skinFolder;

    @Override
    public void onEnable() {
        skinFolder = new File(getDataFolder(), "skins");
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
            sender.sendMessage("Skin not found: " + file.getName());
            return true;
        }

        applySkin(target, file);
        sender.sendMessage("Skin applied!");
        return true;
    }

    private void applySkin(Player player, File file) {

        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);

            // THIS is the ONLY modern safe API
            PlayerProfile profile = player.getPlayerProfile();
            PlayerTextures textures = profile.getTextures();

            // IMPORTANT: use data URL format
            textures.setSkin(java.net.URI.create(
                    "data:image/png;base64," + base64
            ).toURL());

            profile.setTextures(textures);
            player.setPlayerProfile(profile);

            // force refresh
            Bukkit.getScheduler().runTask(this, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.hidePlayer(this, player);
                    p.showPlayer(this, player);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
