package net.filebot;

import static java.util.Arrays.*;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class ResourceManager {

	public static Icon getIcon(String name) {
		return getIcon(name, null);
	}

	public static Icon getIcon(String name, String def) {
		URL resource = getImageResource(name, def);

		if (resource == null)
			return null;

		return new ImageIcon(resource);
	}

	public static List<Image> getApplicationIcons() {
		Image[] images = new Image[3];
		images[0] = ResourceManager.getImage("window.icon.small");
		images[1] = ResourceManager.getImage("window.icon.medium");
		images[2] = ResourceManager.getImage("window.icon.large");
		return asList(images);
	}

	public static List<URL> getApplicationIconURLs() {
		URL[] images = new URL[3];
		images[0] = ResourceManager.getImageResource("window.icon.small");
		images[1] = ResourceManager.getImageResource("window.icon.medium");
		images[2] = ResourceManager.getImageResource("window.icon.large");
		return asList(images);
	}

	public static Icon getFlagIcon(String languageCode) {
		return getIcon(String.format("flags/%s", languageCode));
	}

	public static Image getImage(String name) {
		try {
			return ImageIO.read(getImageResource(name));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the URL of an image resource in this jar. Image must be located in <code>resources/</code> and the file type is assumed to be png.
	 * 
	 * @param name
	 *            simple name of the resource (without extension)
	 * @return URL of the resource or null if resource does not exist
	 */
	private static URL getImageResource(String name) {
		return ResourceManager.class.getResource("resources/" + name + ".png");
	}

	private static URL getImageResource(String name, String def) {
		URL resource = getImageResource(name);

		if (resource == null)
			resource = getImageResource(def);

		return resource;
	}

	/**
	 * Dummy constructor to prevent instantiation.
	 */
	private ResourceManager() {
		throw new UnsupportedOperationException();
	}

}
