package com.censoredsoftware.library.serializable.yaml;

/**
 * Convenience abstract class for all YamlConvertible child classes that don't use a key-value system.
 */
public abstract class KeylessYamlConvertible implements YamlConvertible
{
    @Override
    public <K> K keyFromString(String string)
    {
        return null;
    }
}
