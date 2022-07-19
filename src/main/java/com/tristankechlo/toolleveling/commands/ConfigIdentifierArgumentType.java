package com.tristankechlo.toolleveling.commands;

import com.tristankechlo.toolleveling.config.util.ConfigIdentifier;
import net.minecraft.command.argument.EnumArgumentType;

public class ConfigIdentifierArgumentType extends EnumArgumentType<ConfigIdentifier> {

    protected ConfigIdentifierArgumentType() {
        super(ConfigIdentifier.CODEC, ConfigIdentifier::values);
    }

    public static EnumArgumentType<ConfigIdentifier> get() {
        return new ConfigIdentifierArgumentType();
    }

}
