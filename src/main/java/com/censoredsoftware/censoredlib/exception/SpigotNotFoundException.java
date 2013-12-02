package com.censoredsoftware.censoredlib.exception;

public class SpigotNotFoundException extends IllegalArgumentException
{
	public SpigotNotFoundException()
	{
		super("Spigot is not installed.");
	}
}
