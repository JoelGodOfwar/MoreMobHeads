package com.github.joelgodofwar.mmh.enums;

public enum MobHeads120 {
	CAMEL(
			"Camel",	"camel",	"a57bccc1-abdc-47f8-8ea8-a3452cc73f80",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThjMzgzM2EyNGQzN2YxNDMyOTI1NjFhNWViNWQ1NjcwOTc4YjcyNjk5MTZjNGRiN2M4YTliOTFiMmVhMWUwMSJ9fX0="
			),
	SNIFFER(
			"Sniffer",	"sniffer",	"f699a97c-84bb-44cf-b66c-f31bdc69568e",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk3YzVjNjg2Y2M0YWM0YmUwNGYyMWIyMzYzMTgwNGE1Y2FjMzBmZjUzN2Y1YjUzMjQ0MGM4ZTRlMjU0ODFiMyJ9fX0="
			),
	BREEZE(
			"Breeze",	"breeze",	"3fec298d-1578-481e-a190-09e2d38d6d59",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA0NzVhYmUyOGY2NGYxNzVlYTY2ODkxY2MyNzgwMGRiYmVkMzQwNDc1Y2YzMmU1ZTZkNzM5OWRhNjAyOThiNCJ9fX0="
			),
	BOGGED(
			"Bogged",	"bogged",	"4feb2b3e-5de0-45d2-84b8-1f6968317736",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzI5YjRjMDNhNWEyYTIzMWFiMjQwZTZkZjFkMTdjOTA4YjFlZTZhOWUyMzFhNjM4Y2RlYmQ3ZjAxZDU4MGE2YiJ9fX0="
			),


	/**LLAMA(
            "",
            ""
    ),*/
	;

	private final String uuid;
	private final String texture;
	private final String displayName;
	private final String langName;

	MobHeads120(String displayName, String langName, String uuid, String texture){
		this.texture = texture;
		this.displayName = displayName;
		this.uuid = uuid;
		this.langName = langName;
	}

	public static final String getNameFromTexture(String texture)
	{
		//MoreMobHeads.logger.info("texture=" + texture);
		for(MobHeads120 head : MobHeads120.values()){
			//MoreMobHeads.logger.info(verbosity.getName() + "=" + verbosity.getTexture());
			if(head.getTexture().contains(texture) ) {
				return head.getNameString() ;
			}
		}
		return null;
	}
	/**
	 * Returns the enum name for the given texture string.
	 *
	 * @param texture the texture string to search for
	 * @return the enum name, or null if not found
	 */
	public static String getEnumNameFromTexture(String texture) {
		for (MobHeads120 head : MobHeads120.values()) {
			if (head.getTexture().equals(texture)) {
				return head.name();
			}
		}
		return null;
	}
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return uuid;
	}

	/**
	 * @return the texture
	 */
	public String getTexture() {
		return texture;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return displayName;
	}

	/**
	 * @return the name
	 */
	public String getNameString() {
		return langName;
	}

}
