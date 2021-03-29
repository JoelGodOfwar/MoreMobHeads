package com.github.joelgodofwar.mmh.util;

import com.google.common.base.Enums;

public enum TropicalFishHeads {
	BETTY(
			"",
			""
	),
	BLOCKFISH(
			"",
			""
	),
	BRINELY(
			"",
			""
	),
	CLAYFISH(
			"",
			""
	),
	DASHER(
			"",
			""
	),
	FLOPPER(
			"",
			""
	),
	GLITTER(
			"",
			""
	),
	KOB(
			"",
			""
	),
	SNOOPER(
			"",
			""
	),
	SPOTTY(
			"",
			""
	),
	STRIPEY(
			"",
			""
	),
	SUNSTREAK(
			"",
			""
	),
	;
	
	//private final UUID owner;
    private final String texture;
    private final String name;
    
    TropicalFishHeads(String name, String texture){
        this.texture = texture;
        this.name = name;
    }
    
    public static final String getNameFromTexture(String texture)
    {
    	//MoreMobHeads.logger.info("texture=" + texture);
      for(TropicalFishHeads verbosity : TropicalFishHeads.values())
      {
         //MoreMobHeads.logger.info(verbosity.getName() + "=" + verbosity.getTexture());
    	  if(verbosity.getTexture().contains(texture) )
             return verbosity.getName() ;
      }

      return null;
    }
    
	public String getTexture() {
		return texture;
	}

	public String getName() {
		return name;
	}
	
	public static TropicalFishHeads getIfPresent(String name) {
        return Enums.getIfPresent(TropicalFishHeads.class, name).orNull();
    }
}
