package com.censoredsoftware.library.error;

import com.google.common.base.Objects;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorContext
{
	private static final Pattern STACK_TRACE_PATTERN = Pattern.compile("\\((.*?)\\)");
	private static final Logger LOGGER = Logger.getLogger("Minecraft");

	private Plugin PLUGIN;
	private String PLUGIN_NAME;
	private String CLASS_NAME;
	private Integer LINE_NUM;

	private String ERROR_DESC;

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public ErrorContext(Exception ex, String customDescription)
	{
		this(ex);
		ERROR_DESC = customDescription;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public ErrorContext(Exception ex)
	{
		StackTraceElement stackTrace = ex.getStackTrace()[0];

		Class clazz;
		try
		{
			clazz = Class.forName(stackTrace.getClassName(), false, null);
		}
		catch(ClassNotFoundException classNotFound)
		{
			ex.printStackTrace();
			return;
		}
		Plugin plugin = JavaPlugin.getProvidingPlugin(clazz);

		PLUGIN = plugin;
		PLUGIN_NAME = plugin.getName();

		Matcher matcher = STACK_TRACE_PATTERN.matcher(stackTrace.toString());
		matcher.start();
		String part = matcher.group(0);
		LINE_NUM = Integer.parseInt(part.split(":")[1]);
		CLASS_NAME = stackTrace.getClassName();

		ERROR_DESC = ex.getMessage();
	}

	public String getPluginName()
	{
		return PLUGIN_NAME;
	}

	public String getErrorDescription()
	{
		return ERROR_DESC;
	}

	public Integer getLineNumber()
	{
		return LINE_NUM;
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public Plugin getPlugin()
	{
		return PLUGIN;
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(ErrorContext.class).add("plugin", PLUGIN_NAME).add("desc", ERROR_DESC).add("line", LINE_NUM).add("class", CLASS_NAME).toString();
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(PLUGIN_NAME, CLASS_NAME, LINE_NUM, ERROR_DESC);
	}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof ErrorContext && ((ErrorContext) other).CLASS_NAME.equals(CLASS_NAME) && ((ErrorContext) other).LINE_NUM.equals(LINE_NUM) && ((ErrorContext) other).PLUGIN_NAME.equals(PLUGIN_NAME);
	}

	public String[] getErrorReport()
	{
		String pluginTag = "[" + PLUGIN_NAME + "] ";
		return new String[]
				{
						pluginTag + "ERROR REPORT:",
						pluginTag + "Description: " + ERROR_DESC,
						pluginTag + "Line Number: " + LINE_NUM,
						pluginTag + "Class Name: " + CLASS_NAME
				};
	}

	public void printErrorReport()
	{
		for(String line : getErrorReport())
			LOGGER.warning(line);
	}
}
