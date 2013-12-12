package com.censoredsoftware.censoredlib.util;

import com.censoredsoftware.censoredlib.data.Cache;
import com.censoredsoftware.censoredlib.language.Symbol;
import com.censoredsoftware.censoredlib.schematic.BlockData;
import com.censoredsoftware.censoredlib.schematic.Schematic;
import com.censoredsoftware.censoredlib.schematic.Selection;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class Images
{
	private static final ImmutableBiMap<ChatColor, Color> CHAT_COLOR_COLOR;
	static
	{
		Map<ChatColor, Color> chatColorColor = Maps.newHashMap();
		chatColorColor.put(ChatColor.BLACK, Color.decode("#000000"));
		chatColorColor.put(ChatColor.DARK_BLUE, Color.decode("#0000AA"));
		chatColorColor.put(ChatColor.DARK_GREEN, Color.decode("#00AA00"));
		chatColorColor.put(ChatColor.DARK_AQUA, Color.decode("#00AAAA"));
		chatColorColor.put(ChatColor.DARK_RED, Color.decode("#AA0000"));
		chatColorColor.put(ChatColor.DARK_PURPLE, Color.decode("#AA00AA"));
		chatColorColor.put(ChatColor.GOLD, Color.decode("#FFAA00"));
		chatColorColor.put(ChatColor.GRAY, Color.decode("#AAAAAA"));
		chatColorColor.put(ChatColor.DARK_GRAY, Color.decode("#555555"));
		chatColorColor.put(ChatColor.BLUE, Color.decode("#5555FF"));
		chatColorColor.put(ChatColor.GREEN, Color.decode("#55FF55"));
		chatColorColor.put(ChatColor.AQUA, Color.decode("#55FFFF"));
		chatColorColor.put(ChatColor.RED, Color.decode("#FF5555"));
		chatColorColor.put(ChatColor.LIGHT_PURPLE, Color.decode("#FF55FF"));
		chatColorColor.put(ChatColor.YELLOW, Color.decode("#FFFF55"));
		chatColorColor.put(ChatColor.WHITE, Color.decode("#FFFFFF"));
		CHAT_COLOR_COLOR = ImmutableBiMap.copyOf(chatColorColor);
	}
	private static ImmutableBiMap<DyeColor, Color> DYE_COLOR_COLOR;
	static
	{
		Map<DyeColor, Color> dyeColorColor = Maps.newHashMap();
		dyeColorColor.put(DyeColor.BLACK, Color.decode("#191919"));
		dyeColorColor.put(DyeColor.BLUE, Color.decode("#334CB2"));
		dyeColorColor.put(DyeColor.BROWN, Color.decode("#664C33"));
		dyeColorColor.put(DyeColor.CYAN, Color.decode("#4C7F99"));
		dyeColorColor.put(DyeColor.GRAY, Color.decode("#4C4C4C"));
		dyeColorColor.put(DyeColor.GREEN, Color.decode("#667F33"));
		dyeColorColor.put(DyeColor.LIGHT_BLUE, Color.decode("#6699D8"));
		dyeColorColor.put(DyeColor.LIME, Color.decode("#7FCC19"));
		dyeColorColor.put(DyeColor.MAGENTA, Color.decode("#B24CD8"));
		dyeColorColor.put(DyeColor.ORANGE, Color.decode("#D87F33"));
		dyeColorColor.put(DyeColor.PINK, Color.decode("#F27FA5"));
		dyeColorColor.put(DyeColor.PURPLE, Color.decode("#7F3FB2"));
		dyeColorColor.put(DyeColor.RED, Color.decode("#993333"));
		dyeColorColor.put(DyeColor.SILVER, Color.decode("#999999"));
		dyeColorColor.put(DyeColor.WHITE, Color.decode("#FFFFFF"));
		dyeColorColor.put(DyeColor.YELLOW, Color.decode("#E5E533"));
		DYE_COLOR_COLOR = ImmutableBiMap.copyOf(dyeColorColor);
	}

	/**
	 * @deprecated
	 */
	public static ChatColor fromColor(Color color)
	{
		return getChatColor(color);
	}

	/**
	 * @author CompuPhase (http://www.compuphase.com/cmetric.htm)
	 */
	public static double getColorDistance(Color color1, Color color2)
	{
		long rmean = ((long) color1.getRed() + (long) color2.getRed()) / 2;
		long r = (long) color1.getRed() - (long) color2.getRed();
		long g = (long) color1.getGreen() - (long) color2.getGreen();
		long b = (long) color1.getBlue() - (long) color2.getBlue();
		return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
	}

	public static ChatColor getChatColor(final Color color)
	{
		Color nearestColor = Color.decode("#FFFFFF");
		double nearestDistance = -1.0;

		for(Color chatColor : CHAT_COLOR_COLOR.values())
		{
			double distance = getColorDistance(chatColor, color);
			if(nearestDistance == -1.0 || distance < nearestDistance)
			{
				nearestColor = color;
				nearestDistance = distance;
			}
		}

		return CHAT_COLOR_COLOR.inverse().get(nearestColor);
	}

	public static DyeColor getDyeColor(final Color color)
    {
        Color nearestColor = Color.decode("#FFFFFF");
        double nearestDistance = -1.0;

		for(Color chatColor : DYE_COLOR_COLOR.values())
		{
			double distance = getColorDistance(chatColor, color);
			if(nearestDistance == -1.0 || distance < nearestDistance)
			{
				nearestColor = color;
				nearestDistance = distance;
			}
		}

		return DYE_COLOR_COLOR.inverse().get(nearestColor);
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
				ChatColor color = getChatColor(new Color(image.getRGB(j, i)));

				// Append to the line.
				line.append(color.toString()).append(symbol);
			}

			// Add to working list.
			convertedImage.add(line.toString());
		}

		// Return finalized list.
		return convertedImage;
	}

	public static Schematic convertImageToSchematic(BufferedImage image)
    {
        // Working list.
        Schematic schematic = new Schematic("", "", 0);

        // Get the image's height and width.
        int width = image.getWidth();
		int height = image.getHeight();

		// Iterate through the image, pixel by pixel.
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				// Get the color for each pixel.
				DyeColor color = getDyeColor(new Color(image.getRGB(j, i)));

				// Make new selection.
				schematic.add(new Selection(j, width - i, 0, new BlockData(Material.WOOL, color.getWoolData())));
			}
		}

		// Return finalized list.
		return schematic;
	}

	public static MapView sendMapImage(Player player, BufferedImage image)
	{
		MapView map = Bukkit.createMap(player.getWorld());
		map = ImageRenderer.applyToMap(map, image);
		player.sendMap(map);

		return map;
	}

	public static class ImageRenderer extends MapRenderer
	{
		private BufferedImage image;

		public ImageRenderer(BufferedImage image)
		{
			this.image = MapPalette.resizeImage(image);
		}

		@Override
		public void render(MapView mapView, MapCanvas mapCanvas, Player player)
		{
			mapCanvas.drawImage(0, 0, image);
		}

		public static MapView applyToMap(MapView map, BufferedImage image)
		{
			for(MapRenderer renderer : map.getRenderers())
				map.removeRenderer(renderer);

			map.addRenderer(new ImageRenderer(image));

			return map;
		}
	}

	public static BufferedImage getPlayerHead(String playerName) throws NullPointerException
	{
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

			return finalImage;
		}
		catch(Throwable errored)
		{
			errored.printStackTrace();
			Bukkit.getServer().getLogger().warning("[CensoredLib] " + "Something went wrong during an image conversion process.");
		}

		return null;
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
			BufferedImage image = getPlayerHead(playerName);

			// Convert.
			java.util.List<String> convertedImage = convertImage(image, Symbol.FULL_BLOCK);

			// Put the player's head in the cache.
			Cache.saveTimedDay(playerName, "playerHead", convertedImage);

			// Return the converted head.
			return convertedImage;
		}
		catch(Throwable errored)
		{
			errored.printStackTrace();
			Bukkit.getServer().getLogger().warning("[CensoredLib] " + "Something went wrong during an image conversion process.");
		}

		// Something went wrong.
		return null;
	}

	/**
	 * @author Jörn Horstmann (http://stackoverflow.com/a/3967988)
	 */
	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException
	{
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
	}

    public static BufferedImage getGrayscaleImage(BufferedImage image)
    {
        BufferedImage gray = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return gray;
    }
}
