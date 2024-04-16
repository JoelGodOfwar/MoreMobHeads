package com.github.joelgodofwar.mmh.util.mob;

import org.bukkit.entity.Drowned;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;

import com.github.joelgodofwar.mmh.MoreMobHeads;

public class NameTag {

	public static boolean canWearHead(LivingEntity mob) {
		if((mob instanceof Skeleton)||(mob instanceof Zombie)||(mob instanceof PigZombie)||(mob instanceof Villager)||
				(mob instanceof Drowned)||(mob instanceof Stray)||(mob instanceof Husk)||(mob instanceof WitherSkeleton)||
				(mob instanceof ZombieVillager)||(mob instanceof Evoker)||(mob instanceof WanderingTrader)||(mob instanceof Vex)||
				(mob instanceof Illusioner)||(mob instanceof Pillager)||(mob instanceof Vindicator)){
			return true;
		}
		double vers = Double.parseDouble( MoreMobHeads.getMCVersion().substring(2, 4) ); // 1.16.2
		if (vers >= 16) {
			if(NameTag_1_16.canWearHead(mob)) {
				return true;
			}
			if (vers >= 16.2) {
				if(NameTag_1_16_2.canWearHead(mob)) {
					return true;
				}
			}
		}
		return false;
	}
}
