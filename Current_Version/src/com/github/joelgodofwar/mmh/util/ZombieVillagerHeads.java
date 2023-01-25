package com.github.joelgodofwar.mmh.util;

import java.util.UUID;

public enum ZombieVillagerHeads {
	// Zombie Villager variants
    ZOMBIE_VILLAGER_ARMORER(
			"Zombie Armorer", "zombie_villager.armorer",
            "7cfb4bb2-3205-42fb-afd6-70fd580fb8a5",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDg2Nzc3NjgsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZhYmY1OWIxMTg3MzRkMzI4ZWQ0YjkwMDVhYTRhMzI1NzE3YjRhMzkyZjM5OWViMmRhNGYxMWEwZjVlM2E4OSJ9fX0="
    ),
    ZOMBIE_VILLAGER_BUTCHER(
			"Zombie Butcher", "zombie_villager.butcher",
            "6c399981-91ff-4d93-b283-ca9af1228382",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDg3NjU5MjAsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAxZDhiNDcxNmE5ZGE0NWVlNGEzZTRjMmE4Njg0NmFkMWQ3NWE5NzNlYmVlMTgzZjE5YWRjZmI3YTRiYmM2ZiJ9fX0="
    ),
    ZOMBIE_VILLAGER_CARTOGRAPHER(
			"Zombie Cartographer", "zombie_villager.cartographer",
            "be6c92ff-fd94-4d56-b9ca-20f0050f3b41",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDg4MjU4ODgsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU3YjJmYTVmNmE4MjYxZGM0MzhmMmE0YzE1OTc3YTkxM2ZhNmNjMTRmZTk5ZjQ3ZWJmNzcxN2I3YzJiZDA4ZiJ9fX0="
    ),
    ZOMBIE_VILLAGER_CLERIC(
			"Zombie Cleric", "zombie_villager.cleric",
            "26a97cfb-4cbc-4f75-b847-8d41201abd49",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDg4ODMxMTAsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmEwNWY3ZWFmNjkxYjRhYjc0MTc0MDUyNGNjMjMwYTcwYTNlY2UxZTRjNWFjMmRjNzhlNmYwOGRmOWUzODBhNiJ9fX0="
    ),
    ZOMBIE_VILLAGER_FARMER(
			"Zombie Farmer", "zombie_villager.farmer",
            "469641b7-ec99-4a62-b597-c7da85426aae",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDg5Mzk0MzIsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZkZTg4NWMwMzFhYjkwZTcyMmFhMTNkZjg2N2E3NGI4Yzg4NTZhMjkwZWEwOTViNDJiNjQ3ZDdiYzMwZDEzOSJ9fX0="
    ),
    ZOMBIE_VILLAGER_FISHERMAN(
			"Zombie Fisherman", "zombie_villager.fisherman",
            "1812010d-e392-4c3c-b468-6c9066e26c1b",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDg5OTY4NTAsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkNGQyMjZkMzk2NWJhZWM0NTFhMTFlMDE2YzVlN2EwOTUxMTRjNDRhYmY1MmUzNGRjNDQ4YWJmZmJhYjI2ZiJ9fX0="
    ),
    ZOMBIE_VILLAGER_FLETCHER(
			"Zombie Fletcher", "zombie_villager.fletcher",
            "7d20b4d0-05c1-468d-a0fd-e4d8673f9c6e",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDkwNDgzMDgsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTgxOThjZmFmMWExOTIzY2E2MmZmYWY5OTUyOTE2YmE5MzgyNzkxZjBjN2VhZjhiNmQ4MDRlNmMxZTJmNDE0ZiJ9fX0="
    ),
    ZOMBIE_VILLAGER_LEATHERWORKER(
			"Zombie Leatherworker", "zombie_villager.leatherworker",
            "87b57113-d8ca-4fa4-8214-ea6896e2ce4f",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDkxNzQ2OTMsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzlkZjRmYjY3Yjk1OGEzNzViODg0ZWVlNjMwNTM3ZjhmYmQzOWZiY2I3MzczOTgzMzRjNTg2YTA4NDNmMjdkZiJ9fX0="
    ),
    ZOMBIE_VILLAGER_LIBRARIAN(
			"Zombie Librarian", "zombie_villager.librarian",
            "2069d306-ad23-4bb9-a6d0-d9e2f57757e6",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDkyMzU1OTAsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQzZDg0ZDdjMTk5MDBjODUzY2RkODBmZTJlNWE1YzQxZTVjM2U2ODNjNzUzYTgxZDZiOWM0ZTZiNDdkNjU4ZSJ9fX0="
    ),
    ZOMBIE_VILLAGER_MASON(
			"Zombie Mason", "zombie_villager.Mason",
            "87b57113-d8ca-4fa4-8214-ea6896e2ce4f",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0="
    ),
    ZOMBIE_VILLAGER_NITWIT(
			"Zombie Nitwit", "zombie_villager.nitwit",
            "87b57113-d8ca-4fa4-8214-ea6896e2ce4f",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDkzMDkwMzksInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk3MjlhMDgzZTQ4OGExNTEwMGIyYmIxOWVjZTVlZmYzY2MzZmVmYzIzMGI5NzI2NGI3M2U3MTk3NTkwZTI1MyJ9fX0="
    ),
    ZOMBIE_VILLAGER_NONE(
			"Zombie Villager", "zombie_villager.none",
            "87b57113-d8ca-4fa4-8214-ea6896e2ce4f",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDkzNzM2MDEsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdjZTM4Yjk5YzBlNTgxZGY3ZmJlYmYzMWM3NjM3ZjYyNDI1ZGRlMjg1MDhmZWU1OTU3ODE4MTU4OTFhZmQ2In19fQ=="
    ),
    ZOMBIE_VILLAGER_SHEPHERD(
			"Zombie Shepherd", "zombie_villager.shepherd",
            "47f729f2-a01c-46c8-982b-82d2ac59437f",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDk0NDMyNjQsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTc2NjI4ZGU3ZjUyZjhjNTM2YmZjZDJiMzAwMGEyODJiN2Q1OGY0NjI0NTgxYmEwMGVkZmYwNWRiYWRkZmFhNyJ9fX0="
    ),
    ZOMBIE_VILLAGER_TOOLSMITH(
			"Zombie Toolsmith", "zombie_villager.toolsmith",
            "87b57113-d8ca-4fa4-8214-ea6896e2ce4f",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDk1MTc1MDEsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGVlNTczMmNhMmYxYmIwYmVkYmNiYzk0ODIwYzBmMDE1OTRlNjY4ZWZiZTFjOGQ2OGM3MDEzMDE3NTMxNjcwYyJ9fX0="
    ),
    ZOMBIE_VILLAGER_WEAPONSMITH(
			"Zombie Weaponsmith", "zombie_villager.weaponsmith",
            "920e0f3f-f4f4-4b99-8eac-509a974a1393",
            "eyJ0aW1lc3RhbXAiOjE1ODQ0MDk1ODMwMDUsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWYxZmYzZTc0MDQ0NjczYzkxOTA2MDEyZTI0YzY3NmZmMjA1OGEzZWE5ZjM2NTMzN2FlOWI0YjkwZTg1OTgwIn19fQ=="
    ),
    /**
	VILLAGER_(
			"",
            "",
            ""
    ),
    */
    ;
	private final UUID owner;
    private final String texture;
    private final String name;
    private final String nameString;
    
    ZombieVillagerHeads(String name, String nameString, String ownerUUID, String texture){
    	if(ownerUUID == null){
    		ownerUUID = UUID.randomUUID().toString();
    	}
    	this.owner = UUID.fromString(ownerUUID);
        this.texture = texture;
        this.name = name;
        this.nameString = nameString;
    }
    
    public static final String getNameFromTexture(String texture)
    {
    	//MoreMobHeads.logger.info("texture=" + texture);
      for(ZombieVillagerHeads verbosity : ZombieVillagerHeads.values())
      {
         //MoreMobHeads.logger.info(verbosity.getName() + "=" + verbosity.getTexture());
    	  if(verbosity.getTexture().contains(texture) )
             return verbosity.getNameString() ;
      }

      return null;
    }
    
	public UUID getOwner() {
		return owner;
	}

	public String getTexture() {
		return texture;
	}

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
