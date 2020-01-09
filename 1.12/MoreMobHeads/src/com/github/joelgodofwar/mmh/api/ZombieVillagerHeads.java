package com.github.joelgodofwar.mmh.api;

import java.util.UUID;

public enum ZombieVillagerHeads {
	// Zombie Villager variants
    ZOMBIE_VILLAGER_BUTCHER(
			"Zombie Butcher",
            "6c399981-91ff-4d93-b283-ca9af1228382",
            "eyJ0aW1lc3RhbXAiOjE1Nzg0NjA3NTk1NDYsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjRkZWZmMjA4YTlhYTBmODk1M2E1Zjc1MmRiYmY3MTA4ODAzMjFlYWM3OTg4OTg2NDQyNTJmYzIxZmI2YzA2ZiJ9fX0="
    ),
    ZOMBIE_VILLAGER_PRIEST(
			"Zombie Priest",
            "26a97cfb-4cbc-4f75-b847-8d41201abd49",
            "eyJ0aW1lc3RhbXAiOjE1Nzg0NjA5NjM1NDIsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzE1NTZmOTc2MzAzMGExNzdiMGY4NmViYzVjMmRhMDZmNjAyMmYyZDNkNmE5YmU5N2UwNjFkOTVmNjZkMDc4MSJ9fX0="
    ),
    ZOMBIE_VILLAGER_FARMER(
			"Zombie Farmer",
            "469641b7-ec99-4a62-b597-c7da85426aae",
            "eyJ0aW1lc3RhbXAiOjE1Nzg0NjEwODYzNjUsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzE1NTZmOTc2MzAzMGExNzdiMGY4NmViYzVjMmRhMDZmNjAyMmYyZDNkNmE5YmU5N2UwNjFkOTVmNjZkMDc4MSJ9fX0="
    ),
    ZOMBIE_VILLAGER_LIBRARIAN(
			"Zombie Librarian",
            "2069d306-ad23-4bb9-a6d0-d9e2f57757e6",
            "eyJ0aW1lc3RhbXAiOjE1Nzg0NjEyMTU4ODgsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzE1NTZmOTc2MzAzMGExNzdiMGY4NmViYzVjMmRhMDZmNjAyMmYyZDNkNmE5YmU5N2UwNjFkOTVmNjZkMDc4MSJ9fX0="
    ),
    ZOMBIE_VILLAGER_NITWIT(
			"Zombie Nitwit",
            "87b57113-d8ca-4fa4-8214-ea6896e2ce4f",
            "eyJ0aW1lc3RhbXAiOjE1Nzg0NjEzNDI5NDUsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzE1NTZmOTc2MzAzMGExNzdiMGY4NmViYzVjMmRhMDZmNjAyMmYyZDNkNmE5YmU5N2UwNjFkOTVmNjZkMDc4MSJ9fX0="
    ),
    ZOMBIE_VILLAGER_NORMAL(
			"Zombie Villager",
            "87b57113-d8ca-4fa4-8214-ea6896e2ce4f",
            "eyJ0aW1lc3RhbXAiOjE1Nzg0NjEzNDI5NDUsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzE1NTZmOTc2MzAzMGExNzdiMGY4NmViYzVjMmRhMDZmNjAyMmYyZDNkNmE5YmU5N2UwNjFkOTVmNjZkMDc4MSJ9fX0="
    ),
    ZOMBIE_VILLAGER_BLACKSMITH(
			"Zombie Blacksmith",
            "87b57113-d8ca-4fa4-8214-ea6896e2ce4f",
            "eyJ0aW1lc3RhbXAiOjE1Nzg0NjE0NjQ4MDQsInByb2ZpbGVJZCI6IjVlNmUzNDRkYmY3ODRiZmU5NjQ2YWRkNjc1YjcyMTVmIiwicHJvZmlsZU5hbWUiOiJKb2VsR29kT2ZXYXIiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzE1NTZmOTc2MzAzMGExNzdiMGY4NmViYzVjMmRhMDZmNjAyMmYyZDNkNmE5YmU5N2UwNjFkOTVmNjZkMDc4MSJ9fX0="
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
    
    ZombieVillagerHeads(String name, String ownerUUID, String texture){
    	if(ownerUUID == null){
    		ownerUUID = UUID.randomUUID().toString();
    	}
    	this.owner = UUID.fromString(ownerUUID);
        this.texture = texture;
        this.name = name;
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


}
