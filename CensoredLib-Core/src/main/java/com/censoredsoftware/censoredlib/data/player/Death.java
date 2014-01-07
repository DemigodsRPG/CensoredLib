package com.censoredsoftware.censoredlib.data.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class Death implements ConfigurationSerializable
{
	private UUID id;
	private long deathTime;
	private UUID killed, attacking;

	public Death(UUID killed)
	{
		deathTime = System.currentTimeMillis();
		id = UUID.randomUUID();
		this.killed = killed;
		save();
	}

	public Death(UUID killed, UUID attacking)
	{
		deathTime = System.currentTimeMillis();
		id = UUID.randomUUID();
		this.killed = killed;
		this.attacking = attacking;
		save();
	}

	public Death(UUID id, ConfigurationSection conf)
	{
		this.id = id;
		deathTime = conf.getLong("deathTime");
		killed = UUID.fromString(conf.getString("killed"));
		if(conf.isString("attacking")) attacking = UUID.fromString(conf.getString("attacking"));
	}

	protected abstract void save();

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deathTime", deathTime);
		map.put("killed", killed.toString());
		if(attacking != null) map.put("attacking", attacking.toString());
		return map;
	}

	public UUID getId()
	{
		return id;
	}

	public long getDeathTime()
	{
		return deathTime;
	}

	public UUID getKilled()
	{
		return killed;
	}

	public UUID getAttacking()
	{
		return attacking;
	}
}
