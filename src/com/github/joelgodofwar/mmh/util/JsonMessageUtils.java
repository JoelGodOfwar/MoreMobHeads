package com.github.joelgodofwar.mmh.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class JsonMessageUtils {

    private static final boolean IS_FOLIA;
    private static final Method GET_SCHEDULER_METHOD;
    private static final Method RUN_METHOD;

    static {
        boolean isFolia;
        Method getSchedulerMethod = null;
        Method runMethod = null;

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;

            // Reflectively get EntityScheduler from Player
            Class<?> entitySchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            getSchedulerMethod = Player.class.getMethod("getScheduler");

            // Find the run method: run(Object plugin, Consumer<ScheduledTask> task, Runnable retired)
            for (Method method : entitySchedulerClass.getMethods()) {
                if (method.getName().equals("run") && method.getParameterCount() == 3) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params[0].equals(Object.class) && params[1].equals(Consumer.class) && params[2].equals(Runnable.class)) {
                        runMethod = method;
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            isFolia = false;
        }

        IS_FOLIA = isFolia;
        GET_SCHEDULER_METHOD = getSchedulerMethod;
        RUN_METHOD = runMethod;
    }

    /**
     * Sends a pre-processed JSON string as a TextComponent to a player, handling Spigot/Paper/Folia compatibility.
     *
     * @param player The player to send the message to.
     * @param json   The JSON string representing the message (with all placeholders replaced).
     * @param plugin The plugin instance (for Folia scheduling).
     */
    public static void sendJsonMessage(Player player, String json, Object plugin) {
        try {
            // Parse JSON and convert to TextComponent
            TextComponent component = jsonToTextComponent(json);

            // Send message based on server type
            if (IS_FOLIA && GET_SCHEDULER_METHOD != null && RUN_METHOD != null) {
                // Use reflection to call player.getScheduler().run(plugin, task, null)
                Object scheduler = GET_SCHEDULER_METHOD.invoke(player);
                Consumer<Object> task = (scheduledTask) -> player.spigot().sendMessage(component);
                RUN_METHOD.invoke(scheduler, plugin, task, null);
            } else {
                player.spigot().sendMessage(component);
            }
        } catch (Exception e) {
            // Log error for malformed JSON or reflection issues
            String pluginName = plugin.getClass().getSimpleName();
            System.err.println("Failed to send JSON message to " + player.getName() + " in plugin " + pluginName + ": " + e.getMessage());
        }
    }

    /**
     * Converts a JSON string to a TextComponent.
     *
     * @param json The JSON string.
     * @return The resulting TextComponent.
     */
    private static TextComponent jsonToTextComponent(String json) {
        TextComponent result = new TextComponent();
        JsonElement jsonElement = JsonParser.parseString(json);

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                result.addExtra(parseJsonElement(element));
            }
        } else {
            result.addExtra(parseJsonElement(jsonElement));
        }

        return result;
    }

    /**
     * Parses a JSON element (object or primitive) into a TextComponent.
     *
     * @param element The JSON element.
     * @return The parsed TextComponent.
     */
    private static TextComponent parseJsonElement(JsonElement element) {
        TextComponent component = new TextComponent();

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();

            // Handle text
            if (jsonObject.has("text")) {
                component.setText(jsonObject.get("text").getAsString());
            }

            // Handle color
            if (jsonObject.has("color")) {
                String color = jsonObject.get("color").getAsString();
                component.setColor(ChatColor.of(color.toUpperCase()));
            }

            // Handle formatting (bold, italic, etc.)
            if (jsonObject.has("bold") && jsonObject.get("bold").getAsBoolean()) {
                component.setBold(true);
            }
            if (jsonObject.has("italic") && jsonObject.get("italic").getAsBoolean()) {
                component.setItalic(true);
            }
            if (jsonObject.has("underlined") && jsonObject.get("underlined").getAsBoolean()) {
                component.setUnderlined(true);
            }
            if (jsonObject.has("strikethrough") && jsonObject.get("strikethrough").getAsBoolean()) {
                component.setStrikethrough(true);
            }
            if (jsonObject.has("obfuscated") && jsonObject.get("obfuscated").getAsBoolean()) {
                component.setObfuscated(true);
            }

            // Handle clickEvent
            if (jsonObject.has("clickEvent")) {
                JsonObject clickEvent = jsonObject.get("clickEvent").getAsJsonObject();
                String action = clickEvent.get("action").getAsString();
                String value = clickEvent.get("value").getAsString();
                component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(action.toUpperCase()), value));
            }

            // Handle hoverEvent
            if (jsonObject.has("hoverEvent")) {
                JsonObject hoverEvent = jsonObject.get("hoverEvent").getAsJsonObject();
                String action = hoverEvent.get("action").getAsString();
                String contents = hoverEvent.has("contents") ? hoverEvent.get("contents").getAsString() : "";
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(action.toUpperCase()), new Text(contents)));
            }

            // Handle nested components (extra)
            if (jsonObject.has("extra")) {
                JsonArray extra = jsonObject.get("extra").getAsJsonArray();
                for (JsonElement extraElement : extra) {
                    component.addExtra(parseJsonElement(extraElement));
                }
            }
        } else if (element.isJsonPrimitive()) {
            component.setText(element.getAsString());
        }

        return component;
    }
}