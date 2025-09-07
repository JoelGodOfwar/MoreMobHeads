package com.github.joelgodofwar.mmh.util;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
* Utility for resolving the {@link EquipmentSlot} containing a player's weapon for an
* {@link EntityDamageByEntityEvent}. This is used since that event type doesn't provide a
* way for finding out the equipment slot.
*/
public class EquipmentSlotResolver {

    public static Optional<ItemStack> resolveDamagingWeapon(PlayerInventory playerInventory, DamageCause damageCause) {
    	//System.out.println("damageCause=" + damageCause.toString());
        switch (damageCause) {
            case ENTITY_ATTACK:
                // Check if the player is holding any item in the main hand
                ItemStack mainHandItem = playerInventory.getItemInMainHand();
                //System.out.println("mainHandItem=" + mainHandItem.getType().toString());
                if (!mainHandItem.getType().equals(Material.AIR)) {
                    return Optional.of(mainHandItem);
                }
                break;
            case PROJECTILE:
                // Ranged damage
                Optional<ItemStack> bowItem = getWeaponItem(playerInventory, Material.BOW);
                if (bowItem.isPresent()) {
                    return bowItem;
                }
                Optional<ItemStack> crossbowItem = getWeaponItem(playerInventory, Material.CROSSBOW);
                if (crossbowItem.isPresent()) {
                    return crossbowItem;
                }
                Optional<ItemStack> tridentItem = getWeaponItem(playerInventory, Material.TRIDENT);
                if (tridentItem.isPresent()) {
                    return tridentItem;
                }
                Optional<ItemStack> snowItem = getWeaponItem(playerInventory, Material.SNOWBALL);
                if (snowItem.isPresent()) {
                    return snowItem;
                }
                Optional<ItemStack> eggItem = getWeaponItem(playerInventory, Material.EGG);
                if (eggItem.isPresent()) {
                    return eggItem;
                }
                break;
            case MAGIC:
                // Potion
                return getWeaponItem(playerInventory, Material.SPLASH_POTION);
                
		default:
			break;
        }

        return Optional.empty();
    }

    private static Optional<ItemStack> getWeaponItem(PlayerInventory playerInventory, Material material) {
        ItemStack mainHandItem = playerInventory.getItemInMainHand();
        ItemStack offHandItem = playerInventory.getItemInOffHand();

        if (mainHandItem.getType() == material) {
            return Optional.of(mainHandItem);
        } else if (offHandItem.getType() == material) {
            return Optional.of(offHandItem);
        }

        return Optional.empty();
    }
}
