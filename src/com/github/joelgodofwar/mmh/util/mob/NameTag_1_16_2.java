package com.github.joelgodofwar.mmh.util.mob;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PiglinBrute;

public class NameTag_1_16_2 {

	public static boolean canWearHead(LivingEntity mob) {
		if(mob instanceof PiglinBrute) {
			return true;
		}

		return false;
	}

}
