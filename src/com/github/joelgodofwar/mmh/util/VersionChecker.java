package com.github.joelgodofwar.mmh.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.joelgodofwar.mmh.MoreMobHeads;

@SuppressWarnings("unused")
public class VersionChecker {
	private String pluginName;
	private JavaPlugin plugin;
	private int projectID;
	private String currentVersion;
	private String githubURL; // = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/1.15/versions.xml";
	String versionType;
	private List<String> releaseList = new ArrayList<>();
	private List<String> developerList = new ArrayList<>();
	private List<MMHDLC> dlcList = new ArrayList<>();
	private String recommendedVersion = "uptodate";
	private MoreMobHeads mmh;

	public VersionChecker(MoreMobHeads plugin, int projectID, String githubURL) {
		this.mmh = plugin;
		this.projectID = projectID;
		this.currentVersion = plugin.getDescription().getVersion();
		this.githubURL = githubURL;
	}

	public VersionChecker(String plugin, int projectID, String githubURL) {
		this.currentVersion = plugin;
		this.projectID = projectID;
		this.githubURL = githubURL;
	}

	public String getReleaseUrl() {return "https://dev.bukkit.org/projects/moremobheads2";}

	public boolean checkForUpdates() throws Exception {
		URL url = new URL(githubURL);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		URLConnection connection = url.openConnection();
		connection.setUseCaches(false);
		connection.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
		Document doc = db.parse(connection.getInputStream());
		doc.getDocumentElement().normalize();
		NodeList releaseNodes = doc.getElementsByTagName("release");
		for (int i = 0; i < releaseNodes.getLength(); i++) {
			Node node = releaseNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				releaseList.add(element.getElementsByTagName("version").item(0).getTextContent().replace("<version>", "").replace("</version>", ""));
				releaseList.add(element.getElementsByTagName("notes").item(0).getTextContent().replace("<notes>", "").replace("</notes>", ""));
				releaseList.add(element.getElementsByTagName("link").item(0).getTextContent().replace("<link>", "").replace("</link>", ""));
			}
		}
		NodeList developerNodes = doc.getElementsByTagName("developer");
		for (int i = 0; i < developerNodes.getLength(); i++) {
			Node node = developerNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				developerList.add(element.getElementsByTagName("version").item(0).getTextContent().replace("<version>", "").replace("</version>", ""));
				developerList.add(element.getElementsByTagName("notes").item(0).getTextContent().replace("<notes>", "").replace("</notes>", ""));
				developerList.add(element.getElementsByTagName("link").item(0).getTextContent().replace("<link>", "").replace("</link>", ""));
			}
		}

		// MoreMobHeads DLC related check start
		NodeList dlcNodes = doc.getElementsByTagName("dlcs");
		if (dlcNodes.getLength() > 0) {
			Node dlcNode = dlcNodes.item(0);
			if (dlcNode.getNodeType() == Node.ELEMENT_NODE) {
				Element dlcElement = (Element) dlcNode;
				String dlcString = dlcElement.getTextContent().trim();
				String[] dlcParts = dlcString.split(",");
				for (String part : dlcParts) {
					String[] dlcDetails = part.split(";");
					if (dlcDetails.length == 4) {
						String filename = dlcDetails[0];
						int numberOfFiles = Integer.parseInt(dlcDetails[1]);
						double price = Double.parseDouble(dlcDetails[2].replace("$", ""));
						String markerFile = dlcDetails[3];
						dlcList.add(new MMHDLC(filename, numberOfFiles, price, markerFile, mmh));
					}
				}
				mmh.logDebug(ChatColor.YELLOW + "DLCs stored: " + dlcList);
			}
		} // MoreMobHeads DLC related check end

		if (connection != null) {
			connection.getInputStream().close();
		}
		String releaseVersion = releaseList.get(0);
		String developerVersion = developerList.get(0);
		mmh.logDebug(ChatColor.RED + "currentVersion=" + currentVersion);
		mmh.logDebug(ChatColor.RED + "releaseVersion=" + releaseVersion);
		mmh.logDebug(ChatColor.RED + "developerVersion=" + developerVersion);
		if (currentVersion.compareTo(releaseVersion) < 0) {
			recommendedVersion = "release";
			return true;
		} else if (currentVersion.equals(releaseVersion)) {
			recommendedVersion = "uptodate";
			return false;
		} else if (currentVersion.contains(".D")) {
			String[] splitCurrentVersion = currentVersion.split("\\.D");
			String currentReleaseVersion = splitCurrentVersion[0];
			String[] splitDeveloperVersion = developerVersion.split("\\.D");
			String developerReleaseVersion = splitDeveloperVersion[0];
			if (currentReleaseVersion.equals(releaseVersion) && (developerReleaseVersion.compareTo(currentReleaseVersion) <= 0)) {
				recommendedVersion = "release";
				return true;
			} else if ((developerReleaseVersion.compareTo(releaseVersion) < 0) && developerReleaseVersion.equals(currentReleaseVersion)) {
				recommendedVersion = "You are Up To Date";
				return false;
			} else if (developerVersion.compareTo(currentVersion) > 0) {
				recommendedVersion = "developer";
				return true;
			}
		}

		return false;
	}
	public List<String> getReleaseList() {
		return releaseList;
	}
	public List<String> getDeveloperList() {
		return developerList;
	}
	public String getRecommendedVersion() {
		return recommendedVersion;
	}
	public String oldVersion() {
		return currentVersion;
	}
	public String newVersion() {
		if(recommendedVersion.equalsIgnoreCase("release")) {
			return releaseList.get(0);
		}else if(recommendedVersion.equalsIgnoreCase("developer")) {
			return developerList.get(0);
		}else {
			return "UpToDate";
		}
	}
	public String newVersionNotes() {
		if (recommendedVersion.equalsIgnoreCase("release")) {
			return releaseList.get(1);
		} else if (recommendedVersion.equalsIgnoreCase("developer")) {
			return developerList.get(1);
		} else {
			return "UpToDate";
		}
	}
	public String getDownloadLink() {
		if (recommendedVersion.equalsIgnoreCase("release")) {
			return releaseList.get(2);//"https://www.spigotmc.org/resources/no-enderman-grief2.71236/history";
		} else if (recommendedVersion.equalsIgnoreCase("developer")) {
			return developerList.get(2);//"https://discord.com/channels/654087250665144321/654444482372173875";
		} else {
			return "UpToDate";
		}
	}
	// MoreMobHeads DLC related List
	public List<MMHDLC> getDLCList() {
		return dlcList;
	}
}
