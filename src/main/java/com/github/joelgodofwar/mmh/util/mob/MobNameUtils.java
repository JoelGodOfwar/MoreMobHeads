package com.github.joelgodofwar.mmh.util.mob;

import java.lang.reflect.Method;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import lib.github.joelgodofwar.coreutils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;

public final class MobNameUtils {
    
    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    private static DetailedErrorReporter reporter = MoreMobHeads.reporter;
    static MoreMobHeads plugin = MoreMobHeads.getInstance();

    private MobNameUtils() {
        // private constructor – utility class
    }

    /**
     * Returns a variant-aware name for certain mob types.
     * For most mobs this simply returns the original name.
     */
    public static String getName(String originalName, Entity entity) {
        switch (originalName) {
            case "CAT":
                return getCatType(entity);
            case "FROG":
                return getFrogVariant(entity);
            case "COPPER_GOLEM":
                return getCopperGolemWeatherState(entity);
            case "WOLF":
                return getWolfVariant(entity);
            case "CHICK":
            case "CHICKEN":
                return getChickenVariant(entity);
            case "COW":
                return getCowVariant(entity);
            case "PIG":
                return getPigVariant(entity);
            case "VILLAGER":
                return getVillagerName(entity);
            case "ZOMBIE_VILLAGER":
                return getZombieVillagerName(entity);
            case "ZOMBIE_NAUTILUS":
                return getZombieNautilusVariant(entity);
            default:
                return originalName;
        }
    }

    private static String getZombieNautilusVariant(Entity entity) {
        try {
            Class<?> craftCraftZombieNautilusClass = Class.forName(cbClass("entity.CraftZombieNautilus"));
            Object craftCraftZombieNautilus = craftCraftZombieNautilusClass.cast(entity);
            Method getVariantMethod = craftCraftZombieNautilusClass.getMethod("getVariant");
            Object CraftZombieNautilusVar = getVariantMethod.invoke(craftCraftZombieNautilus);
            Method getKeyMethod = CraftZombieNautilusVar.getClass().getMethod("getKey");
            NamespacedKey variantKey = (NamespacedKey) getKeyMethod.invoke(CraftZombieNautilusVar);

            String key = variantKey.getKey(); // "temperate" or "warm"

            // Special case: zombie nautilus "warm" → "coral" for naming
            if ("warm".equals(key)) {
                return "CORAL";
            }
            return key.toUpperCase(); // "TEMPERATE"
        } catch (Exception exception) {
            log("Failed to get CraftZombieNautilus variant using reflection: " + exception.getMessage());
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "TEMPERATE";
        }
    }

    private static String getCatType(Entity entity) {
        try {
            Class<?> craftCatClass = Class.forName(cbClass("entity.CraftCat"));
            Object craftCat = craftCatClass.cast(entity);
            Method getVariantMethod = craftCatClass.getMethod("getCatType");
            Object catVar = getVariantMethod.invoke(craftCat);
            if(!catVar.toString().contains("minecraft:")){
                plugin.logDebug("catVar=" + catVar);
                return "CAT." + catVar;
            }
            Method getKeyMethod = catVar.getClass().getMethod("getKey");
            NamespacedKey variantKey = (NamespacedKey) getKeyMethod.invoke(catVar);

            String key = variantKey.getKey(); // e.g. "black", "all_black", "tabby", etc.
            plugin.logDebug("key=" + key);

            // All others: uppercase for consistency
            return "CAT." + key.toUpperCase();
        } catch (Exception exception) {
            log("Failed to get Cat variant using reflection: " + exception.getMessage());
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "CAT";
        }
    }

    private static String getFrogVariant(Entity entity) {
        try {
            Class<?> craftFrogClass = Class.forName(cbClass("entity.CraftFrog"));
            Object craftFrog = craftFrogClass.cast(entity);
            Method getVariantMethod = craftFrogClass.getMethod("getVariant");
            Object frogVar = getVariantMethod.invoke(craftFrog);
            Method getKeyMethod = frogVar.getClass().getMethod("getKey");
            NamespacedKey variantKey = (NamespacedKey) getKeyMethod.invoke(frogVar);

            return "FROG." + variantKey.getKey().toUpperCase();
        } catch (Exception exception) {
            log("Failed to get Frog variant using reflection: " + exception.getMessage());
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "FROG";
        }
    }

    private static String getZombieVillagerName(Entity entity) {
        try {
            Class<?> craftZombieVillagerClass = Class.forName(cbClass("entity.CraftVillagerZombie"));
            Object craftZombie = craftZombieVillagerClass.cast(entity);

            Method getTypeMethod = craftZombieVillagerClass.getMethod("getVillagerType");
            Object typeObj = getTypeMethod.invoke(craftZombie);

            Method getKeyMethod = typeObj.getClass().getMethod("getKey");
            NamespacedKey typeKey = (NamespacedKey) getKeyMethod.invoke(typeObj);
            String type = typeKey.getKey().toUpperCase();

            Method getProfMethod = craftZombieVillagerClass.getMethod("getVillagerProfession");
            Object profObj = getProfMethod.invoke(craftZombie);

            getKeyMethod = profObj.getClass().getMethod("getKey");
            NamespacedKey profKey = (NamespacedKey) getKeyMethod.invoke(profObj);
            String profession = profKey.getKey().toUpperCase();

            return "ZOMBIE_VILLAGER." + type + "." + profession;

        } catch (Exception exception) {
            log( "Failed to get Zombie Villager type/profession via reflection: " + exception.getMessage());
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "ZOMBIE_VILLAGER";
        }
    }
    private static String getVillagerName(Entity entity) {
        try {
            Class<?> craftVillagerClass = Class.forName(cbClass("entity.CraftVillager"));
            Object craftVillager = craftVillagerClass.cast(entity);

            // getVillagerType() → returns Villager.Type (registry entry)
            Method getTypeMethod = craftVillagerClass.getMethod("getVillagerType");
            Object typeObj = getTypeMethod.invoke(craftVillager);

            Method getKeyMethod = typeObj.getClass().getMethod("getKey");
            NamespacedKey typeKey = (NamespacedKey) getKeyMethod.invoke(typeObj);
            String type = typeKey.getKey().toUpperCase(); // "plains", "desert" → "PLAINS", "DESERT"

            // getProfession() → returns Villager.Profession (registry entry)
            Method getProfMethod = craftVillagerClass.getMethod("getProfession");
            Object profObj = getProfMethod.invoke(craftVillager);

            getKeyMethod = profObj.getClass().getMethod("getKey");
            NamespacedKey profKey = (NamespacedKey) getKeyMethod.invoke(profObj);
            String profession = profKey.getKey().toUpperCase(); // "farmer", "nitwit" → "FARMER", "NITWIT"

            return "VILLAGER." + type + "." + profession;

        } catch (Exception exception) {
            log( "Failed to get Villager type/profession via reflection: " + exception.getMessage() );
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "VILLAGER";
        }
    }

    private static String getCopperGolemWeatherState(Entity entity) {
        try {
            // Paper first: getWeatheringState() exists only on Paper
            try {
                Method m = entity.getClass().getMethod("getWeatheringState");
                return m.invoke(entity).toString().toUpperCase();
            } catch (NoSuchMethodException e) {
                // Not Paper → must be Spigot
            }

            // Spigot fallback
            Method m = entity.getClass().getMethod("getWeatherState");
            return m.invoke(entity).toString().toUpperCase();

        } catch (Throwable t) {
            return "UNAFFECTED"; // extremely unlikely, but safe
        }
    }

    private static String getWolfVariant(Entity entity) {
        try {
            Class<?> craftWolfClass = Class.forName(cbClass("entity.CraftWolf"));
            Object craftWolf = craftWolfClass.cast(entity);
            Method getVariantMethod = craftWolfClass.getMethod("getVariant");
            Object wolfVar = getVariantMethod.invoke(craftWolf);
            if(!wolfVar.toString().contains("minecraft:")){
                return addAngry(wolfVar.toString(), entity);
            }
            Method getKeyMethod = wolfVar.getClass().getMethod("getKey");
            NamespacedKey variantKey = (NamespacedKey) getKeyMethod.invoke(wolfVar);
            String variantName = variantKey.getKey().toUpperCase();

            return "WOLF." + addAngry(variantName, entity);
        } catch (Exception exception) {
            log("Failed to get Wolf variant using reflection: " + exception.getMessage());
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "WOLF." + addAngry("ERROR", entity);
        }
    }

    private static String addAngry(String variantName, Entity entity) {
        Wolf wolf = (Wolf) entity;
        return wolf.isAngry() ? "ANGRY." + variantName : variantName;
    }

    private static String getChickenVariant(Entity entity) {
        try {
            Class<?> craftChickenClass = Class.forName(cbClass("entity.CraftChicken"));
            Object craftChicken = craftChickenClass.cast(entity);
            Method getVariantMethod = craftChickenClass.getMethod("getVariant");
            Object chickenVar = getVariantMethod.invoke(craftChicken);
            if(!chickenVar.toString().contains("minecraft:")){
                return chickenVar.toString();
            }
            Method getKeyMethod = chickenVar.getClass().getMethod("getKey");
            NamespacedKey variantKey = (NamespacedKey) getKeyMethod.invoke(chickenVar);
            return variantKey.getKey().toUpperCase();
        } catch (Exception exception) {
            log("Failed to get Chicken variant using reflection: " + exception.getMessage());
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "CHICKEN";
        }
    }

    private static String getCowVariant(Entity entity) {
        try {
            Class<?> craftCowClass = Class.forName(cbClass("entity.CraftCow"));
            Object craftCow = craftCowClass.cast(entity);
            Method getVariantMethod = craftCowClass.getMethod("getVariant");
            Object cowVar = getVariantMethod.invoke(craftCow);
            if(!cowVar.toString().contains("minecraft:")){
                return cowVar.toString();
            }
            Method getKeyMethod = cowVar.getClass().getMethod("getKey");
            NamespacedKey variantKey = (NamespacedKey) getKeyMethod.invoke(cowVar);
            return variantKey.getKey().toUpperCase();
        } catch (Exception exception) {
            log("Failed to get Cow variant using reflection: " + exception.getMessage());
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "COW";
        }
    }

    private static String getPigVariant(Entity entity) {
        try {
            Class<?> craftPigClass = Class.forName(cbClass("entity.CraftPig"));
            Object craftPig = craftPigClass.cast(entity);
            Method getVariantMethod = craftPigClass.getMethod("getVariant");
            Object pigVar = getVariantMethod.invoke(craftPig);
            if(!pigVar.toString().contains("minecraft:")){
                return pigVar.toString();
            }
            Method getKeyMethod = pigVar.getClass().getMethod("getKey");
            NamespacedKey variantKey = (NamespacedKey) getKeyMethod.invoke(pigVar);
            return variantKey.getKey().toUpperCase();
        } catch (Exception exception) {
            log("Failed to get Pig variant using reflection: " + exception.getMessage());
            reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_GETTING_MOB_NAME).error(exception));
            return "PIG";
        }
    }

    /** Helper to build CraftBukkit class names (e.g. org.bukkit.craftbukkit.v1_21_R5.entity.CraftWolf) */
    private static String cbClass(String className) {
        return CRAFTBUKKIT_PACKAGE + "." + className;
    }

    private static void log(String message) {
        CoreUtils.log( message);
    }
}