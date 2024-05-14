package com.github.joelgodofwar.mmh.util;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;

public class GetMCVersion {
	private String Version;
	private int Major;
	private int Minor;
	private int Patch;
	private int Build;
	public GetMCVersion(){
		Version = Bukkit.getVersion();
		Version = Version.substring(Version.indexOf("MC: "), Version.length()).replace("MC: ", "").replace(")", "");
		String[] strArray = Version.split(".");
		Major = NumberUtils.toInt(strArray[0]);
		Minor = NumberUtils.toInt(strArray[1]);
		Patch = NumberUtils.toInt(strArray[2], 0);
	}
	
	public String VERSION(){
		return Version;
	}
	public int MAJOR(){
		return Major;
	}
	public int MINOR(){
		return Minor;
	}
	public int PATCH(){
		return Patch;
	}
	public int BUILD(){
		return Build;
	}
}
