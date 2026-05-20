package me.offlineskins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

public class SkinUtil {

    // PNG → fake Mojang texture JSON → base64
    public static String toBase64(File file) throws Exception {

        BufferedImage img = ImageIO.read(file);
        if (img == null) throw new RuntimeException("Invalid PNG");

        String json = """
        {
          "timestamp": %d,
          "profileId": "%s",
          "profileName": "offline",
          "textures": {
            "SKIN": {
              "url": "data:image/png;base64,%s"
            }
          }
        }
        """.formatted(
                System.currentTimeMillis(),
                UUID.randomUUID(),
                Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()))
        );

        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    public static void apply(JavaPlugin plugin, Player player, String texture) {

        try {
            GameProfile profile = getProfile(player);

            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", new Property("textures", texture));

            Bukkit.getScheduler().runTask(plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.hidePlayer(plugin, player);
                    p.showPlayer(plugin, player);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static GameProfile getProfile(Player player) throws Exception {
        Object handle = player.getClass().getMethod("getHandle").invoke(player);
        Field f = handle.getClass().getDeclaredField("gameProfile");
        f.setAccessible(true);
        return (GameProfile) f.get(handle);
    }
}