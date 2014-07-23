/*
 * Copyright 2014 Alex Bennett & Alexander Chauncey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.censoredsoftware.library.util;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageUtil {
	/**
	 * Convert an image a list of String objects.
	 *
	 * @param image  The image to be converted.
	 * @param symbol The symbol being used by the string.
	 * @return The converted list.
	 */
	public static java.util.List<String> convertImage(BufferedImage image, CommonSymbol symbol)
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
                ChatColor color = ColorUtil.getChatColor(new Color(image.getRGB(j, i)));

				// Append to the line.
				line.append(color.toString()).append(symbol);
			}

			// Add to working list.
			convertedImage.add(line.toString());
		}

		// Return finalized list.
		return convertedImage;
	}

	/**
	 * Send an image to a player, in the form of an in-game Map.
	 *
	 * @param player The player to receive the map.
	 * @param image  The image to be converted.
	 * @return The MapView the player receives.
	 */
	public static MapView sendMapImage(Player player, BufferedImage image)
	{
		MapView map = Bukkit.createMap(player.getWorld());
		map = ImageRenderer.applyToMap(map, image);
		player.sendMap(map);
		return map;
	}

	/**
     * Retrieve the image of a player's head.
     *
     * @param playerName The player who owns the head.
     * @return The mentioned image.
     * @throws NullPointerException
     */
    public static BufferedImage getPlayerHead(String playerName) throws NullPointerException {
        try {
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
        } catch (Exception errored) {
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
    public static java.util.List<String> getPlayerHead(OfflinePlayer player) throws NullPointerException {
        // Get the player's name.
        String playerName = player.getName();

		try
		{
            // Find.
            BufferedImage image = getPlayerHead(playerName);

            // Resize.
            image = scaleImage(image, 16, 16);

            // Convert.
            java.util.List<String> convertedImage = convertImage(image, CommonSymbol.FULL_BLOCK);

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
     * @param image  The image to manipulate.
     * @param width  The desired width.
     * @param height The desired height.
     * @return The new image.
     * @throws IOException
     */
    public static BufferedImage scaleImage(BufferedImage image, int width, int height) throws IOException {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
    }

    static class ImageRenderer extends MapRenderer {
        private BufferedImage image;

        public ImageRenderer(BufferedImage image) {
            this.image = MapPalette.resizeImage(image);
        }

        static MapView applyToMap(MapView map, BufferedImage image) {
            for (MapRenderer renderer : map.getRenderers())
                map.removeRenderer(renderer);

            map.addRenderer(new ImageRenderer(image));

            return map;
        }

        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
            mapCanvas.drawImage(0, 0, image);
        }
    }
}
