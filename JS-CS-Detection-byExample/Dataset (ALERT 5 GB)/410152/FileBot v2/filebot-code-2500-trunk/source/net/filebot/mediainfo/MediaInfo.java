package net.filebot.mediainfo;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

public class MediaInfo implements Closeable {

	static {
		try {
			// libmediainfo for linux depends on libzen
			if (Platform.isLinux()) {
				// We need to load dependencies first, because we know where our native libs are (e.g. Java Web Start Cache).
				// If we do not, the system will look for dependencies, but only in the library path.
				NativeLibrary.getInstance("zen");
			}
		} catch (Throwable e) {
			// Logger.getLogger(MediaInfo.class.getName()).warning("Failed to preload libzen");
		}
	}

	private Pointer handle;

	public MediaInfo() {
		try {
			handle = MediaInfoLibrary.INSTANCE.New();
		} catch (LinkageError e) {
			throw new MediaInfoException(e);
		}
	}

	public synchronized boolean open(File file) {
		if (!file.isFile())
			return false;

		// MacOS filesystem may require NFD unicode decomposition (forcing NFD seems to work for System.out() but passing to libmediainfo is still not working)
		String path = file.getAbsolutePath();
		if (Platform.isMac()) {
			path = Normalizer.normalize(path, Form.NFD);
		}
		return MediaInfoLibrary.INSTANCE.Open(handle, new WString(path)) > 0;
	}

	public synchronized String inform() {
		return MediaInfoLibrary.INSTANCE.Inform(handle).toString();
	}

	public String option(String option) {
		return option(option, "");
	}

	public synchronized String option(String option, String value) {
		return MediaInfoLibrary.INSTANCE.Option(handle, new WString(option), new WString(value)).toString();
	}

	public String get(StreamKind streamKind, int streamNumber, String parameter) {
		return get(streamKind, streamNumber, parameter, InfoKind.Text, InfoKind.Name);
	}

	public String get(StreamKind streamKind, int streamNumber, String parameter, InfoKind infoKind) {
		return get(streamKind, streamNumber, parameter, infoKind, InfoKind.Name);
	}

	public synchronized String get(StreamKind streamKind, int streamNumber, String parameter, InfoKind infoKind, InfoKind searchKind) {
		return MediaInfoLibrary.INSTANCE.Get(handle, streamKind.ordinal(), streamNumber, new WString(parameter), infoKind.ordinal(), searchKind.ordinal()).toString();
	}

	public String get(StreamKind streamKind, int streamNumber, int parameterIndex) {
		return get(streamKind, streamNumber, parameterIndex, InfoKind.Text);
	}

	public synchronized String get(StreamKind streamKind, int streamNumber, int parameterIndex, InfoKind infoKind) {
		return MediaInfoLibrary.INSTANCE.GetI(handle, streamKind.ordinal(), streamNumber, parameterIndex, infoKind.ordinal()).toString();
	}

	public synchronized int streamCount(StreamKind streamKind) {
		return MediaInfoLibrary.INSTANCE.Count_Get(handle, streamKind.ordinal(), -1);
	}

	public synchronized int parameterCount(StreamKind streamKind, int streamNumber) {
		return MediaInfoLibrary.INSTANCE.Count_Get(handle, streamKind.ordinal(), streamNumber);
	}

	public Map<StreamKind, List<Map<String, String>>> snapshot() {
		Map<StreamKind, List<Map<String, String>>> mediaInfo = new EnumMap<StreamKind, List<Map<String, String>>>(StreamKind.class);

		for (StreamKind streamKind : StreamKind.values()) {
			int streamCount = streamCount(streamKind);

			if (streamCount > 0) {
				List<Map<String, String>> streamInfoList = new ArrayList<Map<String, String>>(streamCount);

				for (int i = 0; i < streamCount; i++) {
					streamInfoList.add(snapshot(streamKind, i));
				}

				mediaInfo.put(streamKind, streamInfoList);
			}
		}

		return mediaInfo;
	}

	public Map<String, String> snapshot(StreamKind streamKind, int streamNumber) {
		Map<String, String> streamInfo = new LinkedHashMap<String, String>();

		for (int i = 0, count = parameterCount(streamKind, streamNumber); i < count; i++) {
			String value = get(streamKind, streamNumber, i, InfoKind.Text);

			if (value.length() > 0) {
				streamInfo.put(get(streamKind, streamNumber, i, InfoKind.Name), value);
			}
		}

		return streamInfo;
	}

	@Override
	public synchronized void close() {
		MediaInfoLibrary.INSTANCE.Close(handle);
	}

	public synchronized void dispose() {
		if (handle == null)
			return;

		// delete handle
		MediaInfoLibrary.INSTANCE.Delete(handle);
		handle = null;
	}

	@Override
	protected void finalize() {
		dispose();
	}

	public enum StreamKind {
		General, Video, Audio, Text, Chapters, Image, Menu;
	}

	public enum InfoKind {
		/**
		 * Unique name of parameter.
		 */
		Name,

		/**
		 * Value of parameter.
		 */
		Text,

		/**
		 * Unique name of measure unit of parameter.
		 */
		Measure,

		Options,

		/**
		 * Translated name of parameter.
		 */
		Name_Text,

		/**
		 * Translated name of measure unit.
		 */
		Measure_Text,

		/**
		 * More information about the parameter.
		 */
		Info,

		/**
		 * How this parameter is supported, could be N (No), B (Beta), R (Read only), W (Read/Write).
		 */
		HowTo,

		/**
		 * Domain of this piece of information.
		 */
		Domain;
	}

	public static String version() {
		return staticOption("Info_Version");
	}

	public static String parameters() {
		return staticOption("Info_Parameters");
	}

	public static String codecs() {
		return staticOption("Info_Codecs");
	}

	public static String capacities() {
		return staticOption("Info_Capacities");
	}

	public static String staticOption(String option) {
		return staticOption(option, "");
	}

	public static String staticOption(String option, String value) {
		try {
			return MediaInfoLibrary.INSTANCE.Option(null, new WString(option), new WString(value)).toString();
		} catch (LinkageError e) {
			throw new MediaInfoException(e);
		}
	}

	/**
	 * Helper for easy usage
	 */
	public static Map<StreamKind, List<Map<String, String>>> snapshot(File file) throws IOException {
		MediaInfo mi = new MediaInfo();
		try {
			if (mi.open(file)) {
				return mi.snapshot();
			} else {
				throw new IOException("Failed to open file: " + file);
			}
		} finally {
			mi.close();
		}
	}
}
