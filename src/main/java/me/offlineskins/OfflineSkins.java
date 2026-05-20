package me.offlineskins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class OfflineSkins extends JavaPlugin implements CommandExecutor {

    private final File skinFolder = new File(getDataFolder(), "skins");
    private final File dataFile = new File(getDataFolder(), "data.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Map<String, String> skinMap = new HashMap<>();

    @Override
    public void onEnable() {
        skinFolder.mkdirs();
        loadData();

        getCommand("setskin").setExecutor(this);

        Bukkit.getScheduler().runTaskLater(this, this::reapply, 40L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length != 2) {
            sender.sendMessage("/setskin <player> <file>");
            return true;
        }

        Player p = Bukkit.getPlayer(args[0]);
        if (p == null) {
            sender.sendMessage("Player not found");
            return true;
        }

        File file = new File(skinFolder, args[1] + ".png");
        if (!file.exists()) {
            sender.sendMessage("File not found: " + file.getName());
            return true;
        }

        try {
            String texture = SkinUtil.toBase64(file);

            skinMap.put(p.getName(), texture);
            saveData();

            SkinUtil.apply(this, p, texture);

            sender.sendMessage("Skin applied!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void saveData() {
        try (Writer w = new FileWriter(dataFile)) {
            gson.toJson(skinMap, w);
        } catch (Exception ignored) {}
    }

    private void loadData() {
        if (!dataFile.exists()) return;

        try (Reader r = new FileReader(dataFile)) {
            skinMap = gson.fromJson(r, HashMap.class);
            if (skinMap == null) skinMap = new HashMap<>();
        } catch (Exception e) {
            skinMap = new HashMap<>();
        }
    }

    private void reapply() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            String t = skinMap.get(p.getName());
            if (t != null) {
                SkinUtil.apply(this, p, t);
            }
        }
    }
}