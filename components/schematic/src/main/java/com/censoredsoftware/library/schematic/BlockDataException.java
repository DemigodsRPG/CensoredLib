package com.censoredsoftware.library.schematic;

public class BlockDataException extends IllegalArgumentException
{
	public BlockDataException()
	{
		super("Odds must be between 1 and 100.");
	}
}
