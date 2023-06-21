package com.github.joelgodofwar.mmh.enums;

import java.util.UUID;

public enum AxolotlHeads {
	BLUE(//used for unknown player heads
			"Blue Axolotl", "axolotl.blue",
            "54f66f7f-6538-4f89-a862-e5d167f75dc3", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTEzZjY0NzQ1YjAzMmNhMThlMzE1NTlmNzk4MDdiZDQ5ZTI1ZDZkYjQyYTdjNGMwNjI2ZWJjNWVmM2EwOGU2MyJ9fX0="
    ),
	CYAN(//used for unknown player heads
			"Cyan Axolotl", "axolotl.cyan",
            "8f538b36-d642-4394-9d42-af17f1939610", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA5ODI0ODc4MDk0OGU0MjBkNzEwNDg4ZTc4NmYwNWMyMDdhYjUwY2FhMGEwZmE1YTE5NGNkYzkxMzFhNGFhZSJ9fX0="
    ),
	GOLD(//used for unknown player heads
			"Gold Axolotl", "axolotl.gold",
            "0368a97c-a7a7-4f9b-9070-961f9c590063", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmIwMzhlYzBiYzEyNWFmNmVhYTNjZGNmODNmMjIyNmZmOWYxNzA5YTA5ZGI2Njk3NmM1OTkzMDNkZDQ0NzI3ZCJ9fX0="
    ),
	LUCY(//used for unknown player heads
			"Lucy Axolotl", "axolotl.lucy",
            "86b5c0db-744b-4734-9637-30b33090758d", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWIxNmM5ODIxMGExMGRhMGY4YTliMjVhNGUxMWQ5ZmMxNTdmZWQyOWEyOTBiNGM0NDMwZjliNmU2ZjM5NjVlYyJ9fX0="
    ),
	WILD(//used for unknown player heads
			"Wild Axolotl", "axolotl.wild",
            "9ff94036-05f4-4676-85ba-2e63204ea7fa", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2UzN2E5YzBkOWIwYmI3ODQ3Y2Y2ZjA3NzVmMDJiNjJkNGZjZjVmNTZjYmZkZTZhOGI0ZjRlYWIxZjdkYzRkYyJ9fX0="
    ),
	;
	private final UUID owner;
    private final String texture;
    private final String name;
    private final String nameString;
    
    AxolotlHeads(String name, String nameString, String ownerUUID, String texture){
    	this.name = name;
    	this.owner = UUID.fromString(ownerUUID);
        this.texture = texture;
        this.nameString = nameString;
    }
    
    public static final String getNameFromTexture(String texture)
    {
    	//MoreMobHeads.logger.info("texture=" + texture);
      for(AxolotlHeads axolotlhead : AxolotlHeads.values())
      {
         //MoreMobHeads.logger.info(verbosity.getName() + "=" + verbosity.getTexture());
    	  if(axolotlhead.getTexture().contains(texture) )
             return axolotlhead.getNameString() ;
      }

      return null;
    }

    
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the owner
	 */
	public UUID getOwner() {
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
	public String getNameString() {
		return nameString;
	}

}
