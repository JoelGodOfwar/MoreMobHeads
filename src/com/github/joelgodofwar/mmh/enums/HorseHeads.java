package com.github.joelgodofwar.mmh.enums;

import java.util.UUID;

public enum HorseHeads {
	HORSE(
			"Horse", "horse",
            "dc1293f0-c0cb-4a1e-973f-bd36d70a3de9",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWRlMTYwNDQ0ODVkMzQwOWE2ZTg4MmJjOWM5MDRjNDQyNWI5Mjg4YWFjZTYwMzg4NWQ1NGM1YWRjNWM3ZWVjYyJ9fX0="
    ),
    HORSE_BLACK(
    		"Black Horse", "horse.black",
            "4083cd58-1325-4bfa-9efb-5b8b93a02493",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZjE0ZjU4ZmRmMWM0OGU0YzI4YWZiZWI0YzY4YmE4MGJhMTc1MDVlNTYwYjUwNTk0NTdjN2I3MWMyM2EzZiJ9fX0="
    ),
    HORSE_BROWN(
    		"Brown Horse", "horse.brown",
            "2dcb4f55-ab85-48ba-b7d2-7b57d3ec7bfa",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjI1OWUwOWRjYzMwMTg3OTY1YWQxZmZkODRhM2ZjMWQ2ODZhMGQxMTI0NDFiMjA4NWZlNGIxNjMxMWY4OTE1NCJ9fX0="
    ),
    HORSE_CHESTNUT(
    		"Chestnut Horse", "horse.chestnut",
            "5a2546e1-2213-4f2a-8cbe-5ffddf3e7269",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTExMzFhZTlmNmNmNzk1NjBlMDgzMjY4Mjc1MTE0NTdjOTljMjg2NmJmYTI1ZDY4Mjg0ZDRhMTY3OWMzZTYyNiJ9fX0="
    ),
    HORSE_CREAMY(
    		"Creamy Horse", "horse.creamy",
            "65e8bd32-6f48-4b92-ab48-fe1add6b67d1",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhjNjRlMTQ5Mzg5Y2JmNTliZTIzNzkyZjE4YWMzOTkxNDI2NThmN2QyZTY2NGY3NTE0N2NhN2FjMjlkODE5OCJ9fX0="
    ),
    HORSE_DARK_BROWN(
    		"Dark Brown Horse", "horse.dark_brown",
            "c6abc94e-a5ff-45fe-a0d7-4e479f290a6f",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkZjI4MGJmYjI0YmFjZDA2YzJlZjU3OWMxNDlhYTVmZDNkNzIzZWY2NWZhNmE5M2I4MzM0YjdmMjkxMzU5YSJ9fX0="
    ),
    HORSE_GRAY(
    		"Gray Horse", "horse.gray",
            "b600f9c3-9c3f-4e3c-828c-3ebb6414eaa7",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNlZGNmOTNmY2ZhYWEwZWQ1OThjNTA5YmZhNmY2ODFjYTIyYzc4MGVmYTFmNDA3ZjBlYTlmOTkxNzE4YWVjMiJ9fX0="
    ),
    HORSE_WHITE(
    		"White Horse", "horse.white",
            "d941cb68-8842-4f78-bdf8-5f1d3c6e7ac0",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGE3MWFiNTMwZGY4ZGZjZmQ2MjljODFkMDFlNjU0YTEyZTcyN2VmYjY1YzU1YzkwNjg1ODliOTE2MDgzNGU1ZCJ9fX0="
    ),
    ;
	
	private final UUID owner;
    private final String texture;
    private final String name;
    private final String nameString;
    
    
    HorseHeads(String name, String nameString, String ownerUUID, String texture){
    	this.owner = UUID.fromString(ownerUUID);
        this.texture = texture;
        this.name = name;
        this.nameString = nameString;
    }
    
    public static final String getNameFromTexture(String texture)
    {
    	//MoreMobHeads.logger.info("texture=" + texture);
      for(HorseHeads verbosity : HorseHeads.values())
      {
         //MoreMobHeads.logger.info(verbosity.getName() + "=" + verbosity.getTexture());
    	  if(verbosity.getTexture().contains(texture) )
             return verbosity.getNameString() ;
      }

      return null;
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
