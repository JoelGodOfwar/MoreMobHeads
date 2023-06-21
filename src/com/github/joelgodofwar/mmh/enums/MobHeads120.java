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
    
    
    /**LLAMA(
            "",
            ""
    ),*/
    ;
	
	private final String owner;
    private final String texture;
    private final String name;
    private final String nameString;
    
    MobHeads120(String name, String nameString, String ownerUUID, String texture){
    	this.texture = texture;
        this.name = name;
        this.owner = ownerUUID;
        this.nameString = nameString;
    }
    
    public static final String getNameFromTexture(String texture)
    {
    	//MoreMobHeads.logger.info("texture=" + texture);
      for(MobHeads120 verbosity : MobHeads120.values()){
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
