package com.censoredsoftware.censoredlib.helper;

public abstract class CensoredCentralizedClass
{
	public abstract CensoredJavaPlugin inst();

	protected abstract int loadWorlds();

	protected abstract void loadListeners();

	protected abstract void loadCommands();

	protected abstract void loadPermissions(boolean load);
}
