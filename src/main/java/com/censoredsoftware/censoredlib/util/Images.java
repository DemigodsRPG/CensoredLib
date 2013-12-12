package com.censoredsoftware.censoredlib.util;

import com.censoredsoftware.censoredlib.data.Cache;
import com.censoredsoftware.censoredlib.language.Symbol;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;

public class Images
{
	private static final ImmutableBiMap<ChatColor, Color> CHAT_COLOR_COLOR;
	static
	{
		Map<ChatColor, Color> chatColorColor = Maps.newHashMap();
		chatColorColor.put(ChatColor.BLACK, Color.decode("#000"));
		chatColorColor.put(ChatColor.DARK_BLUE, Color.decode("#00a"));
		chatColorColor.put(ChatColor.DARK_GREEN, Color.decode("#0a0"));
		chatColorColor.put(ChatColor.DARK_AQUA, Color.decode("#0aa"));
		chatColorColor.put(ChatColor.DARK_RED, Color.decode("#a00"));
		chatColorColor.put(ChatColor.DARK_PURPLE, Color.decode("#a0a"));
		chatColorColor.put(ChatColor.GOLD, Color.decode("#fa0"));
		chatColorColor.put(ChatColor.GRAY, Color.decode("#999"));
		chatColorColor.put(ChatColor.DARK_GRAY, Color.decode("#555"));
		chatColorColor.put(ChatColor.BLUE, Color.decode("#55f"));
		chatColorColor.put(ChatColor.GREEN, Color.decode("#5c5"));
		chatColorColor.put(ChatColor.AQUA, Color.decode("#5cc"));
		chatColorColor.put(ChatColor.RED, Color.decode("#f55"));
		chatColorColor.put(ChatColor.LIGHT_PURPLE, Color.decode("#f5f"));
		chatColorColor.put(ChatColor.YELLOW, Color.decode("#cc5"));
		chatColorColor.put(ChatColor.WHITE, Color.decode("#aaa"));
		CHAT_COLOR_COLOR = ImmutableBiMap.copyOf(chatColorColor);
	}

	public static ChatColor fromColor(final Color color)
	{
		try
		{
			return CHAT_COLOR_COLOR.inverse().get(Iterables.find(CHAT_COLOR_COLOR.values(), new Predicate<Color>()
			{
				@Override
				public boolean apply(Color chatColor)
				{
					return Integer.MAX_VALUE > Math.sqrt(Math.pow(color.getRed() - chatColor.getRed(), 2) - Math.pow(color.getGreen() - chatColor.getGreen(), 2) - Math.pow(color.getBlue() - chatColor.getBlue(), 2));
				}
			}));
		}
		catch(Throwable ignored)
		{}
		return ChatColor.RESET;
	}

	public static java.util.List<String> convertImage(BufferedImage image, Symbol symbol)
	{
		// Working list.
		java.util.List<String> convertedImage = Lists.newArrayList();

		// Get the image's height and width.
		int width = image.getWidth();
		int height = image.getHeight();

		// Iterate through the image, pixel by pixel.
		for(int i = 0; i < height; i++)
		{
			StringBuilder line = new StringBuilder();
			for(int j = 0; j < width; j++)
			{
				// Get the color for each pixel.
				ChatColor color = fromColor(new Color(image.getRGB(j, i)));

				// Append to the line.
				line.append(color.toString()).append(symbol);
			}

			// Add to working list.
			convertedImage.add(line.toString());
		}

		// Return finalized list.
		return convertedImage;
	}

	public static java.util.List<String> getPlayerHead(OfflinePlayer player) throws NullPointerException
	{
		// Get the player's name.
		String playerName = player.getName();

		// Check if we already have this player's head.
		if(Cache.hasTimed(playerName, ""))
		{
			Object data = Cache.getTimedValue(playerName, "playerHead");
			if(data != null && data instanceof java.util.List) return (java.util.List<String>) data;
			else Cache.remove(playerName, "playerHead");
		}

		try
		{
			// Get the image from Mojang.
			URL url = new URL("http://s3.amazonaws.com/MinecraftSkins/" + playerName + ".png");
			BufferedImage image = ImageIO.read(url);

			// Get data from the skin.
			Image head = image.getSubimage(8, 8, 8, 8);
			Image overlay = image.getSubimage(40, 8, 8, 8);

			// Render just the (front of the) head of the skin.
			BufferedImage finalImage = new BufferedImage(64, 64, Image.SCALE_SMOOTH);
			finalImage.getGraphics().drawImage(head, 0, 0, 64, 64, null);
			finalImage.getGraphics().drawImage(overlay, 0, 0, 64, 64, null);

			// Convert.
			java.util.List<String> convertedImage = convertImage(finalImage, Symbol.FULL_BLOCK);

			// Put the player's head in the cache.
			Cache.saveTimedDay(playerName, "playerHead", convertedImage);

			// Return the converted head.
			return convertedImage;
		}
		catch(Throwable ignored)
		{}

		// Something went wrong.
		return null;
	}
}
