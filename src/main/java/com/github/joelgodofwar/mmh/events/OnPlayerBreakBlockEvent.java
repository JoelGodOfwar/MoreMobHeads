package com.github.joelgodofwar.mmh.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnPlayerBreakBlockEvent implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block headBlock = event.getBlock();
		if( (headBlock.getType() == Material.PLAYER_HEAD) || (headBlock.getType() == Material.PLAYER_WALL_HEAD) ) {
			if(!player.hasPermission("headblocks.admin")){
				event.setCancelled(false);
			}
		}else {
			return;
		}
	}

}
