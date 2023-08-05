package com.tristankechlo.toolleveling.config;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.AbstractConfig;
import com.tristankechlo.toolleveling.config.values.AbstractConfigValue;
import com.tristankechlo.toolleveling.config.values.BooleanValue;

import java.util.List;

public final class CommandConfig extends AbstractConfig {

    private final BooleanValue allowWrongEnchantments;
    private final BooleanValue allowIncompatibleEnchantments;
    private final List<AbstractConfigValue<?>> values;
    public static final CommandConfig INSTANCE = new CommandConfig();

    private CommandConfig() {
        super("command_config.json", ToolLeveling.CONFIG_INFO_COMMANDS);

        allowWrongEnchantments = new BooleanValue("allow_wrong_enchantments", true);
        allowIncompatibleEnchantments = new BooleanValue("allow_incompatible_enchantments", true);

        values = List.of(allowWrongEnchantments, allowIncompatibleEnchantments);
    }

    @Override
    public JsonObject serialize() {
        JsonObject superEnchant = new JsonObject();
        superEnchant.addProperty("__comment", "This config is for the /superenchant command");
        allowWrongEnchantments.serialize(superEnchant);
        allowIncompatibleEnchantments.serialize(superEnchant);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("__comment", this.getComment());
        jsonObject.add("superenchant", superEnchant);
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject json) {
        if (json.has("superenchant")) {
            JsonObject superEnchant = json.getAsJsonObject("superenchant");
            allowWrongEnchantments.deserialize(superEnchant);
            allowIncompatibleEnchantments.deserialize(superEnchant);
        }
    }

    public boolean allowWrongEnchantments() {
        return allowWrongEnchantments.get();
    }

    public boolean allowIncompatibleEnchantments() {
        return allowIncompatibleEnchantments.get();
    }

    @Override
    protected List<AbstractConfigValue<?>> getValues() {
        return values;
    }

    @Override
    protected String getComment() {
        return "Checkout '" + ToolLeveling.CONFIG_INFO_COMMANDS + "' for more information about this config";
    }

}
