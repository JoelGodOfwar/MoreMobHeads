package com.github.joelgodofwar.mmh.enums;

public enum MobHeads117 {
	AXOLOTL_BLUE(
			"Blue Axolotl",	"axolotl.blue",	"dc5e0570-adec-c952-5460-0f2218873388",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQyNzU3MTcwN2UzZTlkMTNmMmRhYTNiMmQ2YWU3MGYwMjIxYjFiMzIyNTAwOTkxYmEyYjkxNTY4NzgyNzhhMSJ9fX0="
			),
	AXOLOTL_CYAN(
			"Cyan Axolotl",	"axolotl.cyan",	"fa14b469-d635-bc05-a2e6-0429d78ae290",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI5MjMyZTFiMjNiNWM5NTU0YTcxNzUzZjgyNGVmYTc5ZWJlOTlmYzBkNjNiNDY4NjcxNWM2ZjdhYmE5NTdiMiJ9fX0="
			),
	AXOLOTL_GOLD(
			"Gold Axolotl",	 "axolotl.gold",	"2a1a8d2f-2bdd-4d0b-b2ca-8e24aad71177",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRiN2I4MDlmMmQxZWRlYTJhODQxMjMzMzc0YWZmNGE2N2U5Yzk5NzJlODg3YTI3OGQ2MDBmZmY3OGZkZmYyMiJ9fX0="
			),
	AXOLOTL_LUCY(
			"Lucy Axolotl",	"axolotl.lucy",	"e27a6db3-e34a-45f3-b624-a1ad5c5d1dde",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmU0ODIxY2ZmZTgwMjhjMzNjYjliNTYyZDU0M2RhOGQ0NjVmMmJkMjk3YWI5M2Q3ZWU0N2FhNmU1MGI0NjM5ZiJ9fX0="
			),
	AXOLOTL_WILD(
			"Wild Axolotl",	"axolotl.wild",	"6623686e-3911-43d9-bb7f-f2791e9757f9",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU5M2ZmMWJhN2U1YThmMTY0NjkxNzZjM2VmNzYyODBlMDZiYjQ5YjA4YWVhOWFhMDUwNzcxZDE4Y2Q3MDk3NSJ9fX0="
			),
	GOAT_NORMAL(
			"Goat",	"goat.normal",	"e184fc80-6ba6-4a04-9a05-fb407abd254e",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY0ZWE5NjQ5YWE3YTk3MzdlMjI4YjY1MTUxZGJkOWVjNWU2OTU0M2QwODgyYzRiNWFkOTlhN2Y0YTFlZmExNiJ9fX0="
			),
	GOAT_SCREAMING(
			"Screaming Goat",	"goat.screaming",	"d0aa4129-8c01-46e5-ab55-9816be6ab4aa",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQ5YzdiZWU5MDUzMjY4NDRlNjA1OTNkYmJlZjk5Mjk3MjNkYTJhZmNhN2Q0YzNmZDcyMjgwZWI0YTVmMzMwYyJ9fX0="
			),


	/**LLAMA(
            "",
            ""
    ),*/
	;

	private final String owner;
	private final String texture;
	private final String name;
	private final String nameString;

	MobHeads117(String name, String nameString, String ownerUUID, String texture){
		this.texture = texture;
		this.name = name;
		this.owner = ownerUUID;
		this.nameString = nameString;
	}

	public static final String getNameFromTexture(String texture)
	{
		//MoreMobHeads.logger.info("texture=" + texture);
		for(MobHeads117 head : MobHeads117.values()){
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
		for (MobHeads117 head : MobHeads117.values()) {
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
		return owner;
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
		return name;
	}

	/**
	 * @return the name
	 */
	public String getNameString() {
		return nameString;
	}

}
