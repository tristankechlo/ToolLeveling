package com.tristankechlo.toolleveling.config.util;

public abstract class AbstractConfigValue<T> implements IConfigValue<T> {

    private final String identifier;

    public AbstractConfigValue(String identifier) {
        if (identifier == null) {
            throw new NullPointerException("identifier of the config value can't be null");
        }
        if (identifier.length() <= 1) {
            throw new IllegalArgumentException("identifier of the config value must be longer than 1 char");
        }
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

}
