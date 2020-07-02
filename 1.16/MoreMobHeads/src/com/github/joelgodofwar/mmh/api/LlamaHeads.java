package com.github.joelgodofwar.mmh.api;

import java.util.UUID;

public enum LlamaHeads {
	LLAMA_BROWN(
			"Brown Llama", "llama.brown",
            "75fb08e5-2419-46fa-bf09-57362138f234",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjgxNmVmOWRiNzUzNDg4ODNhZTgwMWI5Mzg2YzhlZDM2N2YzYjhkMGMzYmEwMTY3NjYyZGNhZjliMGU4MzkxMiJ9fX0="
    ),
    LLAMA_CREAMY(
    		"Creamy Llama", "llama.creamy",
            "dd0a3919-e919-428c-9298-6dcc416fec9d",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTA3ZDAyYjgyNWQ0YTQ3NmQ3OGFmNjA3ODU0MWJkZDBmY2I2ODJiMGI0MDk4YjkxYTMxNGUxYjFjNGUzYzRlMSJ9fX0="
    ),
    LLAMA_GRAY(
    		"Gray Llama", "llama.gray",
            "edca7a0d-770f-43d6-8ffc-f6a00e94e477",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTYxMjNiMzA0NzE0ZDIzZDExNGRmZTY3Nzk0NmRjYmU0ZmVlODYzNWMwZDAyMTdiODg0NmQxOTM3ODM1ZTZjYiJ9fX0="
    ),
    LLAMA_WHITE(
    		"White Llama", "llama.white",
            "60d7893f-b634-48b8-8d6e-f07fa14f5115",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZhNTgzNmQ5MTk3YTQwNGJiNzJkN2ZiYmVlY2NmOWE0MTQzYzQ1MWVjODQ0NTUzNTUyMTU5ZWQzNjdhNTYxMCJ9fX0="
    ),
    TRADER_LLAMA_BROWN(
            "Brown Trader Llama", "trader_llama.brown",
            "a957be18-324a-4984-a81b-f556a793a64a",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQyNDc4MGIzYzVjNTM1MWNmNDlmYjViZjQxZmNiMjg5NDkxZGY2YzQzMDY4M2M4NGQ3ODQ2MTg4ZGI0Zjg0ZCJ9fX0="
    ),
    TRADER_LLAMA_CREAMY(
            "Creamy Trader Llama", "trader_llama.creamy",
			"b8e21edd-c25b-4673-9602-6671007f5088",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg5YTJlYjE3NzA1ZmU3MTU0YWIwNDFlNWM3NmEwOGQ0MTU0NmEzMWJhMjBlYTMwNjBlM2VjOGVkYzEwNDEyYyJ9fX0="
    ),
    TRADER_LLAMA_GRAY(
            "Gray Trader Llama", "trader_llama.gray",
			"34bfbc2b-6c59-47df-8cf6-7457ad15165a",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmU0ZDhhMGJjMTVmMjM5OTIxZWZkOGJlMzQ4MGJhNzdhOThlZTdkOWNlMDA3MjhjMGQ3MzNmMGEyZDYxNGQxNiJ9fX0="
    ),
    TRADER_LLAMA_WHITE(
            "White Trader Llama", "trader_llama.white",
			"47dbdab5-105f-42bc-9580-c61cee9231f3",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA4N2E1NTZkNGZmYTk1ZWNkMjg0NGYzNTBkYzQzZTI1NGU1ZDUzNWZhNTk2ZjU0MGQ3ZTc3ZmE2N2RmNDY5NiJ9fX0="
    ),
    ;
	
	private final UUID owner;
    private final String texture;
    private final String name;
    private final String nameString;
    
    
    LlamaHeads(String name, String nameString, String ownerUUID, String texture){
    	//this.material = material;
    	this.owner = UUID.fromString(ownerUUID);
        this.texture = texture;
        this.name = name;
        this.nameString = nameString;
    }
    
    
    public static final String getNameFromTexture(String texture)
    {
    	//MoreMobHeads.logger.info("texture=" + texture);
      for(LlamaHeads verbosity : LlamaHeads.values())
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
