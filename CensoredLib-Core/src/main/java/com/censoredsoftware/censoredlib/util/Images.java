package com.censoredsoftware.censoredlib.util;

import com.censoredsoftware.censoredlib.language.Symbol;
import com.censoredsoftware.censoredlib.schematic.BlockData;
import com.censoredsoftware.censoredlib.schematic.Schematic;
import com.censoredsoftware.censoredlib.schematic.Selection;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
				ChatColor color = Colors.getChatColor(new Color(image.getRGB(j, i)));

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
						MaterialData material = Colors.getMaterial(average, new Color(image.getRGB(j, i)));

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
		catch(Exception errored)
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
		catch(Exception errored)
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
