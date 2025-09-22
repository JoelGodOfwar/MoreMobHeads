# MoreMobHeads
This plugin adds all the Minecraft Mob Heads of the MoreMobHeads 
datapack hosted by Xisumavoid from the Hermitcraft server, and expands 
those heads to all Mobs in the game with textures available at the time 
of release, including all Villager professions and types.

All 22 named Tropical Fish have been added.

ALSO DOES PLAYER HEADS

To view the source for MorMobHeadsLib

https://github.com/JoelGodOfwar/MoreMobHeadsLib

## Developer API

MoreMobHeads exposes a custom Bukkit event, `MobHeadDropEvent`, allowing other plugins to listen for head drops, modify properties (e.g., lore, texture, name), or cancel them entirely. This is useful for integrations like custom head shops, analytics, or conditional drops.

### Event Overview
- **Package**: `com.github.joelgodofwar.mmh.events`
- **Extends**: `org.bukkit.event.Event` (implements `Cancellable`)
- **When Fired**: Just before a mob head is dropped/given to a player (e.g., on mob kill). Vanilla heads (e.g., Creeper Head) are supported, but custom player-textured heads provide more details.
- **Key Properties**:
    - `getEntity()`: The killed entity (e.g., Zombie).
    - `getPlayer()`: The killer (Player or null for environmental kills).
    - `getHead()` / `setHead(ItemStack)`: The head ItemStack (modifiable).
    - `getDisplayName()` / `setDisplayName(String)`: Custom name (null for vanilla).
    - `getTexture()` / `setTexture(String)`: Base64 texture value (null for vanilla).
    - `getUuid()` / `setUuid(String)`: Associated UUID (null if N/A).
    - `getLore()` / `setLore(List<String>)`: Head lore (modifiable).
    - `getNoteblockSound()` / `setNoteblockSound(String)`: Note block sound key (e.g., "entity.zombie.hurt").
    - `isVanilla()`: True if it's a vanilla Material (e.g., `CREEPER_HEAD`).
    - `getSkinURL()`: Decodes Base64 texture to original skin URL (null if invalid/no texture).
    - `isCancelled()` / `setCancelled(boolean)`: Cancel the drop.

### Listening for the Event
Register in your plugin's `onEnable()`:

```java
import com.github.joelgodofwar.mmh.events.MobHeadDropEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// In your main class
getServer().getPluginManager().registerEvents(new YourListener(), this);

public class YourListener implements Listener {
    @EventHandler
    public void onMobHeadDrop(MobHeadDropEvent event) {
        // Log details (example)
        String texture = event.getTexture() != null ? event.getTexture().substring(0, Math.min(event.getTexture().length(), 20)) + "..." : "None";
        String playerName = event.getPlayer() != null ? event.getPlayer().getName() : "None";
        String displayName = event.getDisplayName() != null ? event.getDisplayName() : "None";
        String uuid = event.getUuid() != null ? event.getUuid() : "None";
        String lore = event.getLore() != null ? event.getLore().toString() : "None";
        String noteblockSound = event.getNoteblockSound() != null ? event.getNoteblockSound() : "None";
        String url = event.getSkinURL();
        getLogger().info("MobHeadDropEvent fired: " +
                "Entity=" + event.getEntity().getType() +
                ", Player=" + playerName +
                ", Head=" + event.getHead().getType() +
                ", DisplayName=" + displayName +
                ", Texture=" + texture +
                ", UUID=" + uuid +
                ", Lore=" + lore +
                ", NoteblockSound=" + noteblockSound +
                ", Cancelled=" + event.isCancelled() +
                ", URL=" + url
        );

        // Example: Cancel drops for creepers
        if (event.getEntity().getType() == EntityType.CREEPER) {
            event.setCancelled(true);
        }

        // Example: Modify lore for zombies
        if (event.getEntity().getType() == EntityType.ZOMBIE && event.getLore() != null) {
            event.getLore().add("§cCustom: Infected!");
        }
    }
}