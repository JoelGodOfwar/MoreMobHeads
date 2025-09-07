package com.github.joelgodofwar.mmh.util;

import java.io.Serializable;
import java.util.Objects;

import lib.github.joelgodofwar.coreutils.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import org.jetbrains.annotations.NotNull;


public class Version implements Comparable<Version>, Serializable {

	/**
	 * The latest release version of minecraft.
	 */
	public static final Version LATEST = new Version("1.21.7");
	/**
	 * The minimum version Plugin has been tested with.
	 */
	public static final String MINIMUM_MINECRAFT_VERSION = "1.14";
	/**
	 * The maximum version Plugin has been tested with.
	 */
	public static final String MAXIMUM_MINECRAFT_VERSION = "1.21.7";
	public static final Version MC_1_14 = new Version("1.14");

	private static final long serialVersionUID = 6815052987222483030L;

	/**
	 * The current version of minecraft, lazy initialized by MinecraftVersion.currentVersion()
	 */
	private static Version currentVersion;
	private int major; // 1
	private int minor; // 16
	private int patch; // 1
	private int build; // ?
	private volatile Boolean atCurrentOrAbove;

	/**
	 * Determine the current Minecraft version.
	 *
	 * @param server - the Bukkit server that will be used to examine the MC version.
	 */
	public Version(Server server) {
		this(extractVersion(server.getVersion()));
	}

	private String[] string2 = {"0","0","0","0"};
	public Version(String string){
		string2 = string.split("\\.");
		this.major = NumberUtils.toInt(string2[0]);
		this.minor = NumberUtils.toInt(string2[1]);
		this.patch = string2.length > 2 ? NumberUtils.toInt(string2[2]) : 0;
		this.build = string2.length >= 4 ? NumberUtils.toInt(string2[3]) : 0;
	}

	/**
	 * Creates a Version object from a string representation.
	 * The string should be in the format "x.y.z" for a three-component version
	 * or "x.y.z.w" for a four-component version, where x, y, z, and w are integers.
	 *
	 * @param string the version string to parse
	 * @return a Version object with either 3 or 4 components
	 * @throws IllegalArgumentException if the version string does not have 3 or 4 components
	 * @throws NumberFormatException if any component cannot be parsed as an integer
	 */
	public static Version fromString(String string) {
		String[] parts = string.split("\\.");

		if (parts.length == 3) {
			return new Version(
					NumberUtils.toInt(parts[0]),
					NumberUtils.toInt(parts[1]),
					NumberUtils.toInt(parts[2])
					);
		} else if (parts.length == 4) {
			return new Version(
					NumberUtils.toInt(parts[0]),
					NumberUtils.toInt(parts[1]),
					NumberUtils.toInt(parts[2]),
					NumberUtils.toInt(parts[3])
					);
		}

		throw new IllegalArgumentException("Version string must have 3 or 4 components");
	}

	/**
	 * Construct a version object directly.
	 *
	 * @param major - major version number.
	 * @param minor - minor version number.
	 * @param patch - patch version number.
	 */
	public Version(int major, int minor, int patch) {
		this(major, minor, patch, 0);
	}

	/**
	 * Construct a version object directly.
	 *
	 * @param major - major version number.
	 * @param minor - minor version number.
	 * @param patch - patch version number.
	 * @param build - build version number.
	 */
	public Version(int major, int minor, int patch, int build) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.build = build;
	}

	/**
	 * Extract the Minecraft version from CraftBukkit itself.
	 *
	 * @param strVersion - the server version in text form.
	 * @return The underlying MC version.
	 * @throws IllegalStateException If we could not parse the version string.
	 */
	public static String extractVersion(String strVersion) {
		//String strVersion = Bukkit.getVersion().toString();
		/*strVersion = strVersion.substring(strVersion.indexOf("MC: "), strVersion.length());
		strVersion = strVersion.replace("MC: ", "").replace(")", "");
		return strVersion;//*/
		int startIndex = strVersion.indexOf("MC: ") + "MC: ".length();
		int endIndex = strVersion.indexOf(")", startIndex);
		if ((startIndex == -1) || (endIndex == -1)) {
			throw new IllegalStateException("Could not parse the version string.");
		}
		return strVersion.substring(startIndex, endIndex).trim();
	}

	/**
	 * Parse the given server version into a Minecraft version.
	 *
	 * @param serverVersion - the server version.
	 * @return The resulting Minecraft version.
	 */
	public static Version fromServerVersion(String serverVersion) {
		return new Version(extractVersion(serverVersion));
	}

	public static Version getCurrentVersion() {
		if (currentVersion == null) {
			currentVersion = fromServerVersion(Bukkit.getVersion());
		}

		return currentVersion;
	}

	public static void setCurrentVersion(Version version) {
		currentVersion = version;
	}

	public boolean atOrAbove(Version version) {
		return getCurrentVersion().isAtLeast(version);
	}

	public int[] parseVersion(String version) {
		String[] elements = version.split("\\.");
		int[] numbers = new int[3];

		// Make sure it's even a valid version
		if (elements.length < 1) {
			throw new IllegalStateException("Corrupt MC version: " + version);
		}

		// The String 1 or 1.2 is interpreted as 1.0.0 and 1.2.0 respectively.
		for (int i = 0; i < Math.min(numbers.length, elements.length); i++) {
			numbers[i] = Integer.parseInt(elements[i].trim());
		}
		return numbers;
	}

	public int getMajor(){return major;}
	public int getMinor(){return minor;}
	public int getPatch(){return patch;}
	public int getBuild(){return build;}

	/**
	 * Checks if this version is at or above the current version the server is running.
	 *
	 * @return true if this version is equal or newer than the server version, false otherwise.
	 */
	public boolean atOrAbove() {
		if (this.atCurrentOrAbove == null) {
			this.atCurrentOrAbove = atOrAbove(this);
		}

		return this.atCurrentOrAbove;
	}
	/**
	 * Retrieve the version String (major.minor.build) only.
	 *
	 * @return A normal version string.
	 */
	public String getVersion() {
		if (this.getBuild() == 0) {
			return String.format("%s.%s.%s", this.getMajor(), this.getMinor(), this.getPatch());
		} else {
			return String.format("%s.%s.%s-%s", this.getMajor(), this.getMinor(), this.getPatch(),
					this.getBuild());
		}
	}
	@Override
	public int compareTo(@NotNull Version o) {
        return ComparisonChain.start()
				.compare(this.getMajor(), o.getMajor())
				.compare(this.getMinor(), o.getMinor())
				.compare(this.getPatch(), o.getPatch())
				.compare(this.getBuild(), o.getBuild(), Ordering.natural().nullsLast())
				.result();
	}

	/**
	 * Checks if this Version is equal to another object.
	 *
	 * @param obj the object to compare with, which can be a Version or a String in format "x.y.z" or "x.y.z.w"
	 * @return true if the objects are equal, false otherwise
	 * @throws IllegalArgumentException if the String input is invalid
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		Version other;
		if (obj instanceof Version) {
			other = (Version) obj;
		} else if (obj instanceof String) {
			try {
				other = Version.fromString((String) obj);
			} catch (IllegalArgumentException e) {
				return false;
			}
		} else {
			return false;
		}
		return this.compareTo(other) == 0;
	}

	/**
	 * Helper method to compare this Version with a version string.
	 *
	 * @param versionString the version string to compare with
	 * @return true if the versions are equal, false otherwise
	 */
	public boolean equalsString(String versionString) {
		if (versionString == null) {
			return false;
		}
		try {
			return this.compareTo(Version.fromString(versionString)) == 0;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Checks if the current version is between the specified lower and upper version bounds (inclusive).
	 *
	 * @param lowerBound The lower version bound to check against (inclusive).
	 * @param upperBound The upper version bound to check against (inclusive).
	 * @return {@code true} if the current version is between {@code lowerBound} and {@code upperBound} (inclusive),
	 *         {@code false} otherwise.
	 * @throws NullPointerException if either {@code lowerBound} or {@code upperBound} is null.
	 */
	public boolean isBetween(Version lowerBound, Version upperBound) {
		if ((lowerBound == null) || (upperBound == null)) {
			throw new NullPointerException("Version bounds cannot be null");
		}
		return isAtLeast(lowerBound) && isAtMost(upperBound);
	}

	/**
	 * Checks if the current version is between the specified lower Version and upper version string bounds (inclusive).
	 *
	 * @param lowerBound The lower version bound to check against (inclusive).
	 * @param upperVersion The upper version bound as a string to check against (inclusive).
	 * @return {@code true} if the current version is between {@code lowerBound} and {@code upperVersion} (inclusive),
	 *         {@code false} otherwise.
	 * @throws IllegalArgumentException if {@code upperVersion} is not a valid version string.
	 * @throws NullPointerException if {@code lowerBound} is null.
	 */
	public boolean isBetween(Version lowerBound, String upperVersion) {
		return isBetween(lowerBound, new Version(upperVersion));
	}

	/**
	 * Checks if the current version is between the specified lower version string and upper Version bounds (inclusive).
	 *
	 * @param lowerVersion The lower version bound as a string to check against (inclusive).
	 * @param upperBound The upper version bound to check against (inclusive).
	 * @return {@code true} if the current version is between {@code lowerVersion} and {@code upperBound} (inclusive),
	 *         {@code false} otherwise.
	 * @throws IllegalArgumentException if {@code lowerVersion} is not a valid version string.
	 * @throws NullPointerException if {@code upperBound} is null.
	 */
	public boolean isBetween(String lowerVersion, Version upperBound) {
		return isBetween(new Version(lowerVersion), upperBound);
	}

	/**
	 * Checks if the current version is between the specified lower and upper version strings bounds (inclusive).
	 *
	 * @param version1 The lower version bound to check against (inclusive).
	 * @param version2 The upper version bound to check against (inclusive).
	 * @return {@code true} if the current version is between {@code version1} and {@code version2} (inclusive),
	 *         {@code false} otherwise.
	 * @throws IllegalArgumentException if either {@code version1} or {@code version2} is not a valid version string.
	 */
	public boolean isBetween(String version1, String version2) {
		return isBetween(new Version(version1), new Version(version2));
	}

	/**
	 * Checks if the current version is at least the specified minimum version.
	 * <p>
	 * This method compares the current version to the provided minimum version string and returns
	 * {@code true} if the current version is equal to or greater than the minimum version according
	 * to the natural ordering defined by {@link #compareTo(Version)}.
	 * </p>
	 *
	 * @param minVersion The minimum version string to compare against (e.g., "1.20.4").
	 * @return {@code true} if the current version is greater than or equal to {@code minVersion},
	 *         {@code false} otherwise.
	 * @throws IllegalArgumentException if {@code minVersion} is not a valid version string
	 *         (e.g., null, empty, or malformed).
	 * @see #compareTo(Version)
	 */
	public boolean isAtLeast(String minVersion) {

		return this.compareTo(new Version(minVersion)) >= 0;
	}

	/**
	 * Checks if the current version is at most the specified maximum version.
	 * <p>
	 * This method compares the current version to the provided maximum version string and returns
	 * {@code true} if the current version is equal to or less than the maximum version according
	 * to the natural ordering defined by {@link #compareTo(Version)}.
	 * </p>
	 *
	 * @param maxVersion The maximum version string to compare against (e.g., "1.20.4").
	 * @return {@code true} if the current version is less than or equal to {@code maxVersion},
	 *         {@code false} otherwise.
	 * @throws IllegalArgumentException if {@code maxVersion} is not a valid version string
	 *         (e.g., null, empty, or malformed).
	 * @see #compareTo(Version)
	 */
	public boolean isAtMost(String maxVersion) {
		return this.compareTo(new Version(maxVersion)) <= 0;
	}

	/**
	 * Checks if the current version is at least the specified minimum version.
	 *
	 * @param minVersion The minimum version to compare against.
	 * @return {@code true} if the current version is greater than or equal to {@code minVersion},
	 *         {@code false} otherwise.
	 * @throws NullPointerException if {@code minVersion} is null.
	 */
	public boolean isAtLeast(Version minVersion) {
		if (minVersion == null) {
			throw new NullPointerException("Minimum version cannot be null");
		}
		return this.compareTo(minVersion) >= 0;
	}

	/**
	 * Checks if the current version is at most the specified maximum version.
	 *
	 * @param maxVersion The maximum version to compare against.
	 * @return {@code true} if the current version is less than or equal to {@code maxVersion},
	 *         {@code false} otherwise.
	 * @throws NullPointerException if {@code maxVersion} is null.
	 */
	public boolean isAtMost(Version maxVersion) {
		if (maxVersion == null) {
			throw new NullPointerException("Maximum version cannot be null");
		}
		return this.compareTo(maxVersion) <= 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getMajor(), this.getMinor(), this.getPatch());
	}
	@Override
	public String toString() {
		// Convert to a String that we can parse back again
		return String.format("%s", this.getVersion());
	}
}
