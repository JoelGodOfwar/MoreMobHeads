package com.github.joelgodofwar.mmh.util;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class Utils {

	public static void sendJson(CommandSender player, String string){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() + "\" " + string);
	}

	public static void sendJson(Player player, String string){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() + "\" " + string);
	}

	public String parsePAPI(String string) {
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
			return PlaceholderAPI.setPlaceholders(null, string);
		}else {
			return string;
		}
	}

	public String parsePAPI(Player player, String string) {
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
			return PlaceholderAPI.setPlaceholders(player, string);
		}else {
			return string;
		}
	}

	public String parsePAPI(OfflinePlayer player, String string) {
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
			return PlaceholderAPI.setPlaceholders(player, string);
		}else {
			return string;
		}
	}

	/**
	 * Checks if the server version is supported based on minimum Minecraft version and build number.
	 *
	 * @param minMCVersion Minimum Minecraft version in the format "Major.Minor.BugFix.Build" (e.g., "1.20.2.3936").
	 * @return True if the server version is not supported; otherwise, false.
	 */
	public static boolean isSupportedVersion(String minMCVersion) {
		// 3936-Spigot-dba3cdc-5a2d905 (MC: 1.20.2)
		//                          0   1 2
		//String[] minVersion = minMCVersion.split("\\.");
		Version minVersion = new Version(minMCVersion);

		String serverVersion = Bukkit.getVersion(); // Get the server version string
		String[] versionParts = serverVersion.split(" "); // Split by space to get the MC version and build number
		String daVersion = versionParts[2].replace(")", ".").concat(serverVersion.split("-")[0]);
		/**System.out.println("Bukkit.getVersion()=" + Bukkit.getVersion());
		System.out.println("versionParts.length=" + versionParts.length);
		System.out.println("versionParts.0=" + versionParts[0]);
		System.out.println("versionParts.1=" + versionParts[1]);
		System.out.println("versionParts.1=" + versionParts[2]);//*/
		Version checkVersion = new Version(daVersion);

		if(minVersion.Major() > checkVersion.Major()){return true;}
		if(minVersion.Major() == checkVersion.Major()){/** same do nothing */}
		if(minVersion.Minor() > checkVersion.Minor()){return true;}
		else if(minVersion.Minor() == checkVersion.Minor()){/** same do nothing */}
		if(minVersion.Patch() > checkVersion.Patch()){return true;}
		else if(minVersion.Patch() == checkVersion.Patch()){/** same do nothing */}
		if(minVersion.Build() > checkVersion.Build()){return true;}
		else if(minVersion.Build() == checkVersion.Build()){/** same do nothing */}

		return true; // Server version is not supported
	}

	public static class Version{
		private int Major; // 1
		private int Minor; // 16
		private int Patch; // 1
		private int Build; // ?
		private boolean isDev = false;
		private String[] string2 = {"0","0","0","0"};
		public Version(String string){
			//System.out.println("string=" + string);
			string2 = string.split("\\.");
			/**for (String a : string2) {
				System.out.println(a);
				System.out.println("string2=" + string2.toString());
				System.out.println("string2.length=" + string2.length);
			}//*/
			this.Major = NumberUtils.toInt(string2[0]);
			this.Minor = NumberUtils.toInt(string2[1]);
			this.Patch = NumberUtils.toInt(string2[2]);
			if(string2.length >= 4){
				if(string2[3].toUpperCase().contains("D")){
					isDev =  true;
					string2[3] = string2[3].toUpperCase().replace("D", "");
				}
				this.Build = NumberUtils.toInt(string2[3]);
			}else{
				this.Build = 0;
			}
		}
		public int Major(){return Major;}
		public int Minor(){return Minor;}
		public int Patch(){return Patch;}
		public int Build(){return Build;}
		public boolean isDev(){return isDev;}
	}


}
