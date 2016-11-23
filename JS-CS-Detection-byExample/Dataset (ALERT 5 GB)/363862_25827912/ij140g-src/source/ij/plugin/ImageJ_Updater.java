package ij.plugin;
import ij.*;
import ij.gui.*;
import ij.util.Tools;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;


/** This plugin implements the Plugins/Utilities/Update ImageJ command. */
public class ImageJ_Updater implements PlugIn {

	public void run(String arg) {
		if (IJ.getApplet()!=null) return;
		File file = new File(Prefs.getHomeDir() + File.separator + "ij.jar");
		if (isMac() && !file.exists())
			file = new File(Prefs.getHomeDir() + File.separator + "ImageJ.app/Contents/Resources/Java/ij.jar");
		if (!file.exists()) {
			error("File not found: "+file.getPath());
			return;
		}
		if (!file.canWrite()) {
			error("No write access: "+file.getPath());
			return;
		}
		String[] list = openUrlAsList("http://rsb.info.nih.gov/ij/download/jars/list.txt");
		int count = list.length + 2;
		String[] versions = new String[count];
		String[] urls = new String[count];
		String uv = getUpgradeVersion();
		if (uv==null) return;
		versions[0] = "v"+uv+" (latest version)";
		urls[0] = "http://rsb.info.nih.gov/ij/upgrade/ij.jar";
		if (versions[0]==null) return;
		for (int i=1; i<count-1; i++) {
			versions[i] = list[i-1];
			urls[i] = "http://rsb.info.nih.gov/ij/download/jars/ij"
				+versions[i].substring(1,2)+versions[i].substring(3,6)+".jar";
		}
		versions[count-1] = "daily build";
		urls[count-1] = "http://rsb.info.nih.gov/ij/ij.jar";
		int choice = showDialog(versions);
		if (choice==-1) return;
		if (!versions[choice].startsWith("daily") && versions[choice].compareTo("v1.39s")<0
		&& Menus.getCommands().get("ImageJ Updater")==null) {
			String msg = "This command is not available in versions of ImageJ prior\n"+
			"to 1.39s so you will need to install the plugin version at\n"+
			"<http://rsb.info.nih.gov/ij/plugins/imagej-updater.html>.";
			if (!IJ.showMessageWithCancel("Update ImageJ", msg))
				return;
		}
		byte[] jar = getJar(urls[choice]);
		//file.renameTo(new File(file.getParent()+File.separator+"ij.bak"));
		if (version().compareTo("1.37v")>=0)
			Prefs.savePreferences();
		// if (!renameJar(file)) return; // doesn't work on Vista
		saveJar(file, jar);
		System.exit(0);
	}

	int showDialog(String[] versions) {
		GenericDialog gd = new GenericDialog("ImageJ Updater");
		gd.addChoice("Upgrade To:", versions, versions[0]);
		String msg = 
			"You are currently running v"+version()+".\n"+
			" \n"+
			"If you click \"OK\", ImageJ will quit\n"+
			"and you will be running the upgraded\n"+
			"version after you restart ImageJ.\n";
		gd.addMessage(msg);
		gd.showDialog();
		if (gd.wasCanceled())
			return -1;
		else
			return gd.getNextChoiceIndex();
	}

	String getUpgradeVersion() {
		String url = "http://rsb.info.nih.gov/ij/notes.html";
		String notes = openUrlAsString(url, 20);
		if (notes==null) {
			error("Unable to open release notes at "+url);
			return null;
		}
		int index = notes.indexOf("Version ");
		if (index==-1) {
			error("Release notes are not in the expected format");
			return null;
		}
		String version = notes.substring(index+8, index+13);
		return version;
	}

	String openUrlAsString(String address, int maxLines) {
		StringBuffer sb;
		try {
			URL url = new URL(address);
			InputStream in = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			sb = new StringBuffer();
			int count = 0;
			String line;
			while ((line=br.readLine())!=null && count++<maxLines)
				sb.append (line + "\n");
			in.close ();
		} catch (IOException e) {sb = null;}
			return sb!=null?new String(sb):null;
	}

	byte[] getJar(String address) {
		byte[] data;
		boolean gte133 = version().compareTo("1.33u")>=0;
		try {
			URL url = new URL(address);
			URLConnection uc = url.openConnection();
			int len = uc.getContentLength();
			String  name = address.endsWith("ij/ij.jar")?"daily build":"ij.jar";
			IJ.showStatus("Downloading ij.jar ("+IJ.d2s((double)len/1048576,1)+"MB)");
			InputStream in = uc.getInputStream();
			data = new byte[len];
			int n = 0;
			while (n < len) {
				int count = in.read(data, n, len - n);
				if (count<0)
					throw new EOFException();
	   			 n += count;
				if (gte133) IJ.showProgress(n, len);
			}
			in.close();
		} catch (IOException e) {
			return null;
		}
		return data;
	}

	/*Changes the name of ij.jar to ij-old.jar
	boolean renameJar(File f) {
		File backup = new File(Prefs.getHomeDir() + File.separator + "ij-old.jar");
		if (backup.exists()) {
			if (!backup.delete()) {
				error("Unable to delete backup: "+backup.getPath());
				return false;
			}
		}
		if (!f.renameTo(backup)) {
			error("Unable to rename to ij-old.jar: "+f.getPath());
			return false;
		}
		return true;
	}
	*/

	void saveJar(File f, byte[] data) {
		try {
			FileOutputStream out = new FileOutputStream(f);
			out.write(data, 0, data.length);
			out.close();
		} catch (IOException e) {
		}
	}

	String[] openUrlAsList(String address) {
		Vector v = new Vector();
		try {
			URL url = new URL(address);
			InputStream in = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while (true) {
				line = br.readLine();
				if (line==null) break;
				if (!line.equals("")) v.addElement(line);
			}
			br.close();
		} catch(Exception e) { }
		String[] lines = new String[v.size()];
		v.copyInto((String[])lines);
		return lines;
	}

	// Use reflection to get version since early versions
	// of ImageJ do not have the IJ.getVersion() method.
	String version() {
		String version = "";
		try {
			Class ijClass = ImageJ.class;
			Field field = ijClass.getField("VERSION");
			version = (String)field.get(ijClass);
		}catch (Exception ex) {}
		return version;
	}

	boolean isMac() {
		String osname = System.getProperty("os.name");
		return osname.startsWith("Mac");
	}
	
	void error(String msg) {
		IJ.error("ImageJ Updater", msg);
	}

}
