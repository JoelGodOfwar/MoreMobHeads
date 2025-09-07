package com.github.joelgodofwar.mmh.util;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class MMHDLC {
	private String filename;
	private int numberOfFiles;
	private double price;
	private String markerFile;
	private boolean installed;
	private JavaPlugin plugin;

	public MMHDLC(String filename, int numberOfFiles, double price, String markerFile, JavaPlugin plugin) {
		this.filename = filename;
		this.numberOfFiles = numberOfFiles;
		this.price = price;
		this.markerFile = markerFile;
		this.plugin = plugin;
		this.installed = checkInstallation();
	}

	private boolean checkInstallation() {
		if (plugin == null) {
			return false; // Fallback if plugin instance is not provided
		}
		return new File(plugin.getDataFolder(), markerFile).exists();
	}

	public String getFilename() { return filename; }
	public int getNumberOfFiles() { return numberOfFiles; }
	public double getPrice() { return price; }
	public String getMarkerFile() { return markerFile; }
	public boolean isInstalled() { return installed; }

	@Override
	public String toString() {
		return String.format("%s (%d, $%.2f) - %s %s",
				filename, numberOfFiles, price, markerFile,
				installed ? "(Installed)" : "(Not Installed)");
	}
}