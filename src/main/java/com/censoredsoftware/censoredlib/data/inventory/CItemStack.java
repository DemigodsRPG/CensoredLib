package com.censoredsoftware.censoredlib.data.inventory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class CItemStack implements ConfigurationSerializable {
    private UUID id;
    private ItemStack item;

    public CItemStack() {
    }

    public CItemStack(UUID id, ConfigurationSection conf) {
        this.id = id;
        if (conf.getValues(true) != null) item = ItemStack.deserialize(conf.getValues(true));
    }

    @Override
    public Map<String, Object> serialize() {
        return item.serialize();
    }

    public void generateId() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * Returns the DItemStack as an actual, usable ItemStack.
     *
     * @return ItemStack
     */
    public ItemStack toItemStack() {
        return item;
    }
}
