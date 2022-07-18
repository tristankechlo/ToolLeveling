package com.tristankechlo.toolleveling.commands;

import net.minecraft.command.argument.EnumArgumentType;

public class ConfigIdentifierArgumentType extends EnumArgumentType<ConfigIdentifier> {

    protected ConfigIdentifierArgumentType() {
        super(ConfigIdentifier.CODEC, ConfigIdentifier::values);
    }

    public static EnumArgumentType<ConfigIdentifier> get() {
        return new ConfigIdentifierArgumentType();
    }

}
