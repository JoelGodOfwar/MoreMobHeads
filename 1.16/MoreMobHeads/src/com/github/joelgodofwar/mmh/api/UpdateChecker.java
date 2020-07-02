package com.github.joelgodofwar.mmh.api;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateChecker {
    private static int project;
    private URL checkURL;
    private String newVersion;
    private JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin, int projectID) {
        this.plugin = plugin;
        project = projectID;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/" + newVersion.substring(0, 4) + "/version.txt");
        }catch(MalformedURLException e) {
            Bukkit.getLogger().warning(Ansi.RED + "Could not connect to update server.");
            //Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
    public static String getResourceUrl() {return "https://spigotmc.org/resources/" + project;}
    public boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !plugin.getDescription().getVersion().equals(newVersion);
    }
}