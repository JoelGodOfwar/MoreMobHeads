package com.github.joelgodofwar.mmh.api;

import java.util.UUID;

public enum CatHeads {
	BLACK_CAT(//used for unknown player heads
			"Black Cat", // Name
            "f89934e4-99a0-4dab-9151-7b63831e5fd1", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJjMWU4MWZmMDNlODJhM2U3MWUwY2Q1ZmJlYzYwN2UxMTM2MTA4OWFhNDdmMjkwZDQ2YzhhMmMwNzQ2MGQ5MiJ9fX0="
			),
    BRITISH_SHORTHAIR(//used for unknown player heads
			"British Shorthair Cat", // Name
            "4332ff48-8a0e-4164-ae55-2d16caf68190", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTM4OWUwZDVkM2U4MWY4NGI1NzBlMjk3ODI0NGIzYTczZTVhMjJiY2RiNjg3NGI0NGVmNWQwZjY2Y2EyNGVlYyJ9fX0="
    		),
    CALICO(//used for unknown player heads
			"Calico Cat", // Name
            "024560fb-84a5-40cf-b6a1-c8f9d9db2fe9", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQwMDk3MjcxYmI2ODBmZTk4MWU4NTllOGJhOTNmZWEyOGI4MTNiMTA0MmJkMjc3ZWEzMzI5YmVjNDkzZWVmMyJ9fX0="
    		),
    JELLIE(//used for unknown player heads
			"Jellie Cat", // Name
            "f0aaa05b-0283-4663-9b57-52dbf2ca2750", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBkYjQxMzc2Y2E1N2RmMTBmY2IxNTM5ZTg2NjU0ZWVjZmQzNmQzZmU3NWU4MTc2ODg1ZTkzMTg1ZGYyODBhNSJ9fX0="
    		),
    PERSIAN(//used for unknown player heads
			"Persian Cat", // Name
            "701fa2a8-ef2b-46cd-b9d3-6cd16be17bb4", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY0MGM3NDYyNjBlZjkxYzk2YjI3MTU5Nzk1ZTg3MTkxYWU3Y2UzZDVmNzY3YmY4Yzc0ZmFhZDk2ODlhZjI1ZCJ9fX0="
    		),
    RAGDOLL(//used for unknown player heads
			"Ragdoll Cat", // Name
            "b65e722b-5a35-4561-a8df-db9c7a52041f", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM3YTQ1ZDI1ODg5ZTNmZGY3Nzk3Y2IyNThlMjZkNGU5NGY1YmMxM2VlZjAwNzk1ZGFmZWYyZTgzZTBhYjUxMSJ9fX0="
    		),
    RED_CAT(//used for unknown player heads
			"Red Cat", // Name
            "11d2442b-0bc1-4475-a499-f07dcc2aa40d", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjExM2RiZDNjNmEwNzhhMTdiNGVkYjc4Y2UwN2Q4MzZjMzhkYWNlNTAyN2Q0YjBhODNmZDYwZTdjYTdhMGZjYiJ9fX0="
    		),
    SIAMESE_CAT(//used for unknown player heads
			"Siamese Cat", // Name
            "7d487214-5276-49af-bbb1-019b49384d69", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDViM2Y4Y2E0YjNhNTU1Y2NiM2QxOTQ0NDk4MDhiNGM5ZDc4MzMyNzE5NzgwMGQ0ZDY1OTc0Y2M2ODVhZjJlYSJ9fX0="
    		),
    TABBY(//used for unknown player heads
			"Tabby Cat", // Name
            "18d071ee-a17c-46eb-866c-304a4823ac05", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUyOGQzMGRiM2Y4YzNmZTUwY2E0ZjI2ZjMwNzVlMzZmMDAzYWU4MDI4MTM1YThjZDY5MmYyNGM5YTk4YWUxYiJ9fX0="
    		),
    WHITE(//used for unknown player heads
			"White Cat", // Name
            "db9474c0-f11e-47d3-a6dc-2ebcdd5f37e0", // UUID
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFkMTVhYzk1NThlOThiODlhY2E4OWQzODE5NTAzZjFjNTI1NmMyMTk3ZGQzYzM0ZGY1YWFjNGQ3MmU3ZmJlZCJ9fX0="
    		),
    WILD_OCELOT(
    		"Ocelot",
    		"7f372b3d-c0fb-46df-ae19-4a9ee7584ae5",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19"
    		),
    ;
	private final UUID owner;
    private final String texture;
    private final String name;
    
    CatHeads(String name, String ownerUUID, String texture){
    	this.name = name;
    	this.owner = UUID.fromString(ownerUUID);
        this.texture = texture;
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

}
