	public ItemStack fixHeads(ItemStack offHand, ItemStack mainHand){
		net.minecraft.server.v1_16_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack.asNMSCopy(offHand);
        NBTTagCompound itemcompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        Set<String> SkullKeys = itemcompound.getKeys();
        int damage = itemcompound.getInt("Damage");
        NBTTagCompound display = itemcompound.getCompound("display");
        NBTTagCompound SkullOwner = itemcompound.getCompound("SkullOwner");
        
        net.minecraft.server.v1_16_R2.ItemStack nmsItem2 = org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack.asNMSCopy(mainHand);
        NBTTagCompound itemcompound2 = (nmsItem2.hasTag()) ? nmsItem2.getTag() : new NBTTagCompound();
        Set<String> SkullKeys2 = itemcompound2.getKeys();
        int damage2 = itemcompound2.getInt("Damage");
        NBTTagCompound display2 = itemcompound2.getCompound("display");
        NBTTagCompound SkullOwner2 = itemcompound2.getCompound("SkullOwner");
        
        if( display.equals(display2) && SkullOwner.equals(SkullOwner2) && damage != damage2){
        	ItemStack is = new ItemStack(Material.PLAYER_HEAD);
            net.minecraft.server.v1_16_R2.ItemStack nmsItem3 = org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack.asNMSCopy(is);
            NBTTagCompound itemcompound3 = (nmsItem3.hasTag()) ? nmsItem3.getTag() : new NBTTagCompound();
            itemcompound3.set("display", display);
            itemcompound3.set("SkullOwner", SkullOwner);
            nmsItem2.setTag(itemcompound3);
            ItemStack is2 = org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack.asBukkitCopy(nmsItem2);
    		return is2;
        }else if( display.equals(display2) && SkullOwner.equals(SkullOwner2) && damage == damage2){
        	return mainHand;
        }
        return null;
	}
