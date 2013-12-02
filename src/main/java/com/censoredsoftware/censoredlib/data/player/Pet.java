package com.censoredsoftware.censoredlib.data.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Pet implements ConfigurationSerializable {
    private UUID id;
    private String entityType;
    private String animalTamer;
    private boolean PvP;
    private UUID entityUUID;
    private UUID owner;

    public Pet() {
    }

    public Pet(UUID id, ConfigurationSection conf) {
        this.id = id;
        entityType = conf.getString("entityType");
        if (conf.getString("animalTamer") != null) animalTamer = conf.getString("animalTamer");
        PvP = conf.getBoolean("PvP");
        entityUUID = UUID.fromString(conf.getString("entityUUID"));
        if (conf.getString("owner") != null) owner = UUID.fromString(conf.getString("owner"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entityType", entityType);
        if (animalTamer != null) map.put("animalTamer", animalTamer);
        map.put("PvP", PvP);
        map.put("entityUUID", entityUUID.toString());
        if (owner != null) map.put("owner", owner.toString());
        return map;
    }

    protected abstract void save();

    public void generateId() {
        id = UUID.randomUUID();
    }

    public void remove() {
        getEntity().remove();
        delete();
    }

    public abstract void delete();

    public void setTamable(LivingEntity tameable) {
        this.entityType = tameable.getType().name();
        this.entityUUID = tameable.getUniqueId();
    }

    public void setOwnerId(String playerName, UUID owner) {
        this.animalTamer = playerName;
        this.owner = owner;
        save();
    }

    public boolean canPvp() {
        return this.PvP;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getAnimalTamer() {
        return animalTamer;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public LivingEntity getEntity() {
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity pet : world.getLivingEntities()) {
                if (!(pet instanceof Tameable)) continue;
                if (pet.getUniqueId().equals(this.entityUUID)) return (LivingEntity) pet;
            }
        }
        delete();
        return null;
    }

    public UUID getOwnerId() {
        return owner;
    }

    public UUID getId() {
        return this.id;
    }

    public Location getCurrentLocation() {
        try {
            return getEntity().getLocation();
        } catch (Exception ignored) {
        }
        return null;
    }

    public void disownPet() {
        if (this.getEntity() == null) return;
        ((Tameable) this.getEntity()).setOwner(new AnimalTamer() {
            @Override
            public String getName() {
                return "Disowned";
            }
        });
    }
}
