package com.github.joelgodofwar.mmh.enums;

public enum MobHeads119 {
    ALLAY(
    		"Allay",	"allay",	"a975dd11-542b-4c4a-9e3e-2a254b0eb6b7",
    		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2IyMjViYjQ2MWQ2MTEwMmNhMzhlOGY3NzI2MzljYTA4YTM1OTY1MjM3OWNjZTRhZThlZGNjNTBjZTUzYjllNyJ9fX0="
    ),
    FROG_COLD(
    		"Cold Frog",	"frog.cold",	"633dcca1-b60b-4d59-91b1-146a4a566eca",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2ExZmE2NjEwN2ZkYzkyZTk2ZGM3MDdhMWI5NDdmZGRjODZiYjUyMDJmODU2MzZkNTNlNzA3ZTIwYzVjZTYwZiJ9fX0="
    ),
    FROG_TEMPERATE(
    		"Temperate Frog",	 "frog.temperate",	"2dcdf931-a67b-442c-a68e-6f78232b52a1",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzhmMmRmZDRkZDdiYWMwN2Q4MWNjNjgyODc5NDkzYTdhNDcyYTgzNmNjY2U4NmY4ODZhY2MyZjIwZmNkN2I3MCJ9fX0="
    ),
    FROG_WARM(
    		"Warm Frog",	"frog.warm",	"d8a80665-fa41-487d-9205-4895eb34b7c4",
    		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRiMWFiYWM4YjU0YWNjZjU5YWNmM2JkY2FiNTRmMmI3YWJkYzg0Zjc4MmIzNzgzZDRlOTZlOTcwNTkzNjJhZSJ9fX0="
    ),
    WARDEN(
    		"Warden",	"warden",	"8dac825b-3820-491f-b5a4-8f6c6021248b",
    		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1ZmQxOWI3NTIxZDgzMWY3MWEzNmQ1MzM1MDNlOWE5MGZmNDlmMGQzOGRlMjRhMmRmMWFjYWI3NzczZGExNCJ9fX0="
    ),
    TADPOLE(
    		"Tadpole",	"tadpole",	"52c22c74-f079-4b5d-b296-ce289355ea45",
    		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdiNjU5NWQ4OWMyZmIwYjliZjAxYzVlOTI3YjkzZTZiYjM1OWJiYjgwZTUwZThjNzI5M2JjZjY2MDY4NDQ1NyJ9fX0="
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
    
    MobHeads119(String name, String nameString, String ownerUUID, String texture){
    	this.texture = texture;
        this.name = name;
        this.owner = ownerUUID;
        this.nameString = nameString;
    }
    
    public static final String getNameFromTexture(String texture)
    {
    	//MoreMobHeads.logger.info("texture=" + texture);
      for(MobHeads119 verbosity : MobHeads119.values()){
         //MoreMobHeads.logger.info(verbosity.getName() + "=" + verbosity.getTexture());
    	  if(verbosity.getTexture().contains(texture) )
             return verbosity.getNameString() ;
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
