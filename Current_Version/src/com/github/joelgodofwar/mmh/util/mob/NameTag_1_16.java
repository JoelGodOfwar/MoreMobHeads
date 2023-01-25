package com.github.joelgodofwar.mmh.util.mob;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;

public class NameTag_1_16 {

	public static boolean canWearHead(LivingEntity mob) {
		if(mob instanceof Piglin) {
			return true;
		}

		return false;
	}

}
