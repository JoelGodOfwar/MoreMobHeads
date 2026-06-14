package com.github.joelgodofwar.mmh.util;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import lib.github.joelgodofwar.coreutils.CoreUtils;


import java.util.Date;

/**
 * Utility class to validate the build status (e.g., dev-build expiration and version compatibility).
 */
public class BuildValidator {
	private static final long EXPIRATION_DAYS = 60L;
	private static final long EXPIRATION_OFFSET = EXPIRATION_DAYS * 24 * 60 * 60 * 1000L;
	private long DEV_BUILD_START_TIME;
	public MoreMobHeads mmh;

    /**
	 * Checks if the current build is valid (not expired for dev-builds and compatible with Minecraft version).
	 * @return true if the build is valid, false otherwise
	 */
	public boolean isBuildValid() {
		this.mmh = MoreMobHeads.getInstance();
        
		String version = mmh.getDescription().getVersion();
		this.DEV_BUILD_START_TIME = MoreMobHeads.DEV_BUILD_START_TIME;
		long currentTime = System.currentTimeMillis();
		String[] parts = version.split("_");
		if (parts.length < 2) {
			CoreUtils.log("Invalid version format: " + version + ". Treating as full release.");
			return true; // Assume full release if format is invalid
		}

		String[] versionParts = parts[1].split("\\.");
		if ((versionParts.length > 3) && versionParts[3].startsWith("D")) {
			if (DEV_BUILD_START_TIME == 0L) {
				CoreUtils.warn("DEV_BUILD_START_TIME not set correctly for dev-build " + version + ". Treating as full release.");
				return true;
			}
			// 1798761600000L is Jan 1, 2027
			if (DEV_BUILD_START_TIME < MoreMobHeads.DEV_BUILD_END_TIME || DEV_BUILD_START_TIME > currentTime) {
				CoreUtils.warn("Invalid build timestamp, using current time");
				return true;
			}
			long expirationTime = DEV_BUILD_START_TIME + EXPIRATION_OFFSET;
			if (currentTime > expirationTime) {
				CoreUtils.log("Dev-build %s has expired on %s.", version, new Date(expirationTime));
				return false;
			}
			mmh.logDebug("Dev-build %s valid, expires on %s.", version, new Date(expirationTime));
		}
		return true;
	}

	public long getDaysRemaining() {
		long currentTime = System.currentTimeMillis();
		if (DEV_BUILD_START_TIME < MoreMobHeads.DEV_BUILD_END_TIME || DEV_BUILD_START_TIME > currentTime) {
			return EXPIRATION_DAYS;
		}
		long millisRemaining = (DEV_BUILD_START_TIME + EXPIRATION_OFFSET) - currentTime;
		return Math.max(0, millisRemaining / (24 * 60 * 60 * 1000L));
	}

	public boolean isFinalWeek() {
		return getDaysRemaining() <= 7;
	}

	public void sendWarnings() {
		long daysRemaining = getDaysRemaining();
		if (daysRemaining >= EXPIRATION_DAYS) {
			CoreUtils.log("[" + MoreMobHeads.THIS_NAME + "] Dev-build valid for " + EXPIRATION_DAYS + " days");
		} else if (daysRemaining <= 7) {
			CoreUtils.warn("[" + MoreMobHeads.THIS_NAME + "] Dev-build expires in " + daysRemaining + " day(s)!");
		} else if (daysRemaining % 7 == 0) {
			CoreUtils.warn("[" + MoreMobHeads.THIS_NAME + "] Dev-build expires in " + daysRemaining + " days");
		}
	}
}