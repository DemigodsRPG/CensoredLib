package com.censoredsoftware.censoredlib.util;

import com.censoredsoftware.censoredlib.language.Symbol;
import com.censoredsoftware.censoredlib.schematic.BlockData;
import com.censoredsoftware.censoredlib.schematic.Schematic;
import com.censoredsoftware.censoredlib.schematic.Selection;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Images
{
	/**
	 * Linking ChatColor objects to the corresponding Color.
	 */
	public static final ImmutableBiMap<ChatColor, Color> CHAT_COLOR;
	static
	{
		Map<ChatColor, Color> chatColorColor = Maps.newHashMap();
		chatColorColor.put(ChatColor.BLACK, new Color(0, 0, 0));
		chatColorColor.put(ChatColor.DARK_BLUE, new Color(0, 0, 170));
		chatColorColor.put(ChatColor.DARK_GREEN, new Color(0, 170, 0));
		chatColorColor.put(ChatColor.DARK_AQUA, new Color(0, 170, 170));
		chatColorColor.put(ChatColor.DARK_RED, new Color(170, 0, 0));
		chatColorColor.put(ChatColor.DARK_PURPLE, new Color(170, 0, 170));
		chatColorColor.put(ChatColor.GOLD, new Color(255, 170, 0));
		chatColorColor.put(ChatColor.GRAY, new Color(170, 170, 170));
		chatColorColor.put(ChatColor.DARK_GRAY, new Color(85, 85, 85));
		chatColorColor.put(ChatColor.BLUE, new Color(85, 85, 255));
		chatColorColor.put(ChatColor.GREEN, new Color(85, 255, 85));
		chatColorColor.put(ChatColor.AQUA, new Color(85, 255, 255));
		chatColorColor.put(ChatColor.RED, new Color(255, 85, 85));
		chatColorColor.put(ChatColor.LIGHT_PURPLE, new Color(255, 85, 255));
		chatColorColor.put(ChatColor.YELLOW, new Color(255, 255, 85));
		chatColorColor.put(ChatColor.WHITE, new Color(255, 255, 255));
		CHAT_COLOR = ImmutableBiMap.copyOf(chatColorColor);
	}

	/**
	 * Linking block materials to their corresponding Color.
	 */
	public static ImmutableBiMap<MaterialData, Color> BLOCK_COLOR;
	static
	{
		Map<MaterialData, Color> blockColor = Maps.newHashMap();

		// WOOL
		blockColor.put(new MaterialData(Material.WOOL, (byte) 0), new Color(238, 238, 238));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 1), new Color(213, 116, 50));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 2), new Color(168, 65, 177));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 3), new Color(113, 141, 201));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 4), new Color(193, 181, 45));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 5), new Color(66, 171, 55));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 6), new Color(203, 125, 146));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 7), new Color(72, 72, 72));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 8), new Color(127, 135, 135));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 9), new Color(39, 94, 117));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 10), new Color(120, 55, 173));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 11), new Color(39, 45, 116));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 12), new Color(74, 46, 29));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 13), new Color(36, 48, 19));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 14), new Color(153, 35, 38));
		blockColor.put(new MaterialData(Material.WOOL, (byte) 15), new Color(25, 25, 25));

		// HARD CLAY
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 0), new Color(221, 176, 152));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 1), new Color(185, 82, 36));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 2), new Color(163, 83, 98));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 3), new Color(121, 106, 129));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 4), new Color(206, 132, 35));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 5), new Color(102, 115, 58));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 6), new Color(179, 75, 73));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 7), new Color(64, 44, 39));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 8), new Color(141, 102, 88));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 9), new Color(80, 86, 82));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 10), new Color(124, 67, 79));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 11), new Color(77, 63, 86));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 12), new Color(83, 55, 39));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 13), new Color(73, 82, 46));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 14), new Color(166, 61, 46));
		blockColor.put(new MaterialData(Material.HARD_CLAY, (byte) 15), new Color(44, 32, 19));

		// SAND
		blockColor.put(new MaterialData(Material.SAND, (byte) 0), new Color(247, 233, 163));
		blockColor.put(new MaterialData(Material.SANDSTONE, (byte) 0), new Color(213, 201, 140));

		// STONE
		blockColor.put(new MaterialData(Material.STONE, (byte) 0), new Color(144, 144, 144));
		blockColor.put(new MaterialData(Material.SMOOTH_BRICK, (byte) 0), new Color(117, 117, 117));

		// REDSTONE
		blockColor.put(new MaterialData(Material.REDSTONE_BLOCK, (byte) 0), new Color(255, 0, 0));

		// ICE
		blockColor.put(new MaterialData(Material.PACKED_ICE, (byte) 0), new Color(160, 160, 255));
		blockColor.put(new MaterialData(Material.ICE, (byte) 0), new Color(138, 138, 220));

		// DIRT
		blockColor.put(new MaterialData(Material.DIRT, (byte) 0), new Color(73, 58, 35));

		// NETHER
		blockColor.put(new MaterialData(Material.NETHER_BRICK, (byte) 0), new Color(112, 2, 0));

		// OBSIDIAN
		blockColor.put(new MaterialData(Material.OBSIDIAN, (byte) 0), new Color(21, 20, 31));

		// COAL
		blockColor.put(new MaterialData(Material.COAL_BLOCK, (byte) 0), new Color(12, 12, 12));

		// EMERALD
		blockColor.put(new MaterialData(Material.EMERALD_BLOCK, (byte) 0), new Color(0, 217, 58));

		BLOCK_COLOR = ImmutableBiMap.copyOf(blockColor);
	}

	/**
	 * Color distance formula.
	 * 
	 * @param c1 Color one.
	 * @param c2 Color two.
	 * @return The 'distance' between the two colors.
	 */
	public static double getColorDistance(Color c1, Color c2)
	{
		double rmean = (c1.getRed() + c2.getRed()) / 2;
		int r = c1.getRed() - c2.getRed();
		int g = c1.getGreen() - c2.getGreen();
		int b = c1.getBlue() - c2.getBlue();
		double weightR = 2 + rmean / 256;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256;
		return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
	}

	/**
	 * Get a ChatColor that is best related to a given Color.
	 * 
	 * @param color Color to be matched.
	 * @return Best ChatColor found.
	 */
	public static ChatColor getChatColor(final Color color)
	{
		Color nearestColor = Color.WHITE;
		double bestDistance = Double.MAX_VALUE;

		for(Color chatColor : CHAT_COLOR.values())
		{
			double distance = getColorDistance(chatColor, color);
			if(distance < bestDistance)
			{
				nearestColor = chatColor;
				bestDistance = distance;
			}
		}

		return CHAT_COLOR.inverse().get(nearestColor);
	}

	/**
	 * Get MaterialData that is best related to a given Color.
	 * 
	 * @param color Color to be matched.
	 * @return Best MaterialData found.
	 */
	public static MaterialData getMaterial(Color average, final Color color)
	{
		Color nearestColor = average;
		double bestDistance = Double.MAX_VALUE;

		for(Color theColor : BLOCK_COLOR.values())
		{
			double distance = getColorDistance(theColor, color);
			if(distance < bestDistance)
			{
				nearestColor = theColor;
				bestDistance = distance;
			}
		}

		return BLOCK_COLOR.inverse().get(nearestColor);
	}

	/**
	 * Convert an image a list of String objects.
	 * 
	 * @param image The image to be converted.
	 * @param symbol The symbol being used by the string.
	 * @return The converted list.
	 */
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

	private static Map<Integer, List<Schematic>> schematics = Collections.synchronizedMap(new HashMap<Integer, List<Schematic>>());
	private static int lastTask = -1;

	/**
	 * Get the list of schematics created from the task with a certain id.
	 * 
	 * @param taskId The mentioned task id.
	 * @return The list of schematics.
	 */
	public static List<Schematic> getConvertedSchematics(int taskId)
	{
		if(!schematics.containsKey(taskId)) return null;
		return schematics.get(taskId);
	}

	/**
	 * Remove the schematic list associated with a certain task id.
	 * 
	 * @param taskId The mentioned task id.
	 */
	public static void removeSchematicList(int taskId)
	{
		if(schematics.containsKey(taskId)) schematics.remove(taskId);
	}

	/**
	 * Convert an image to a schematic.
	 * 
	 * @param plugin The plugin requesting this conversion.
	 * @param image The image to be converted.
	 * @param splitSize the max size of each schematic
	 * @return the async task id integer
	 */
	public static int convertImageToSchematic(Plugin plugin, final BufferedImage image, final int splitSize)
	{
		return lastTask = Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				int thisTask = lastTask;
				lastTask = -1;

				List<Schematic> schematicSet = Lists.newArrayList();

				// Working list.
				Schematic schematic = new Schematic("", "", 0);

				// Get the image's height and width.
				int width = image.getWidth();
				int height = image.getHeight();

				int count = 0;

				int progress = 0, total = width * height;

				Color average = Color.WHITE;

				// Iterate through the image, pixel by pixel.
				for(int i = 0; i < height; i++)
				{
					for(int j = 0; j < width; j++)
					{
						progress++;
						if(progress % 20 == 0) Bukkit.getLogger().info("Conversion progress: " + progress + " / " + total);
						if(count >= splitSize)
						{
							count = 0;
							schematicSet.add(schematic);
							schematic = new Schematic("", "", 0);
						}
						// Get the color for each pixel.
						MaterialData material = getMaterial(average, new Color(image.getRGB(j, i)));

						// Make new selection.
						schematic.add(new Selection(j, -10, i, new BlockData(material.getItemType(), material.getData())));
						count++;
					}
				}

				// Add the last schematic, and then the list itself goes into the map
				schematicSet.add(schematic);

				schematics.put(thisTask, schematicSet);

				Bukkit.getLogger().info(" Done! ");
			}
		}, 40);
	}

	/**
	 * Send an image to a player, in the form of an in-game Map.
	 * 
	 * @param player The player to receive the map.
	 * @param image The image to be converted.
	 * @return The MapView the player receives.
	 */
	public static MapView sendMapImage(Player player, BufferedImage image)
	{
		MapView map = Bukkit.createMap(player.getWorld());
		map = ImageRenderer.applyToMap(map, image);
		player.sendMap(map);
		return map;
	}

	static class ImageRenderer extends MapRenderer
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

		static MapView applyToMap(MapView map, BufferedImage image)
		{
			for(MapRenderer renderer : map.getRenderers())
				map.removeRenderer(renderer);

			map.addRenderer(new ImageRenderer(image));

			return map;
		}
	}

	/**
	 * Retrieve the image of a player's head.
	 * 
	 * @param playerName The player who owns the head.
	 * @return The mentioned image.
	 * @throws NullPointerException
	 */
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

	/**
	 * Retrieve the image of a player's head and make it suitable for chat.
	 * 
	 * @param player The player who owns the head.
	 * @return A list of strings to be sent in order to a player.
	 * @throws NullPointerException
	 */
	public static java.util.List<String> getPlayerHead(OfflinePlayer player) throws NullPointerException
	{
		// Get the player's name.
		String playerName = player.getName();

		try
		{
			// Find.
			BufferedImage image = getPlayerHead(playerName);

			// Resize.
			image = scaleImage(image, 16, 16);

			// Convert.
			java.util.List<String> convertedImage = convertImage(image, Symbol.FULL_BLOCK);

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
	 * Scale an image.
	 * 
	 * @param image The image to manipulate.
	 * @param width The desired width.
	 * @param height The desired height.
	 * @return The new image.
	 * @throws IOException
	 */
	public static BufferedImage scaleImage(BufferedImage image, int width, int height) throws IOException
	{
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
	}
}
