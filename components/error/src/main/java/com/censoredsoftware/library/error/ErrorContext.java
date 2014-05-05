package com.censoredsoftware.library.error;

public class ErrorContext
{
	public ErrorContext(Exception ex)
	{
		System.out.print(ex.getStackTrace()[1]);
	}
}
