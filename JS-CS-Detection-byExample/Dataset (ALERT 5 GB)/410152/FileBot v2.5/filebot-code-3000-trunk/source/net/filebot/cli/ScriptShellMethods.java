package net.filebot.cli;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static net.filebot.MediaTypes.*;
import groovy.lang.Closure;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.filebot.MediaTypes;
import net.filebot.MetaAttributeView;
import net.filebot.media.MediaDetection;
import net.filebot.media.MetaAttributes;
import net.filebot.similarity.NameSimilarityMetric;
import net.filebot.similarity.Normalization;
import net.filebot.similarity.SimilarityMetric;
import net.filebot.util.FastFile;
import net.filebot.util.FileUtilities;
import net.filebot.web.WebRequest;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class ScriptShellMethods {

	public static File plus(File self, String name) {
		return new File(self.getPath().concat(name));
	}

	public static File div(File self, String name) {
		return new File(self, name);
	}

	public static File resolve(File self, String name) {
		return new File(self, name);
	}

	public static File resolveSibling(File self, String name) {
		return new File(self.getParentFile(), name);
	}

	public static List<File> listFiles(File self, Closure<?> closure) {
		return (List<File>) DefaultGroovyMethods.findAll(FileUtilities.getChildren(self), closure);
	}

	public static boolean isVideo(File self) {
		return VIDEO_FILES.accept(self);
	}

	public static boolean isAudio(File self) {
		return AUDIO_FILES.accept(self);
	}

	public static boolean isSubtitle(File self) {
		return SUBTITLE_FILES.accept(self);
	}

	public static boolean isVerification(File self) {
		return VERIFICATION_FILES.accept(self);
	}

	public static boolean isArchive(File self) {
		return ARCHIVE_FILES.accept(self);
	}

	public static boolean isDisk(File self) throws Exception {
		// check disk folder
		if (self.isDirectory() && MediaDetection.isDiskFolder(self))
			return true;

		// check disk image
		if (self.isFile() && MediaTypes.getDefaultFilter("video/iso").accept(self) && MediaDetection.isVideoDiskFile(self))
			return true;

		return false;
	}

	public static File getDir(File self) {
		return self.getParentFile();
	}

	public static boolean hasFile(File self, Closure<?> closure) {
		return DefaultGroovyMethods.find(FileUtilities.getChildren(self), closure) != null;
	}

	public static List<File> listTree(File self, int maxDepth) {
		return FileUtilities.listFiles(singleton(self), maxDepth, false, true, true);
	}

	public static List<File> getFiles(File self) {
		return getFiles(self, null);
	}

	public static List<File> getFiles(File self, Closure<?> closure) {
		return getFiles(singletonList(self), closure);
	}

	public static List<File> getFiles(Collection<?> self) {
		return getFiles(self, null);
	}

	public static List<File> getFiles(Collection<?> self, Closure<?> closure) {
		final List<File> roots = FileUtilities.asFileList(self.toArray());

		List<File> files = FileUtilities.listFiles(roots);
		if (closure != null) {
			files = (List<File>) DefaultGroovyMethods.findAll(files, closure);
		}

		return FileUtilities.sortByUniquePath(files);
	}

	public static List<File> getFolders(File self) {
		return getFolders(self, null);
	}

	public static List<File> getFolders(File self, Closure<?> closure) {
		return getFolders(singletonList(self), closure);
	}

	public static List<File> getFolders(Collection<?> self) {
		return getFolders(self, null);
	}

	public static List<File> getFolders(Collection<?> self, Closure<?> closure) {
		final List<File> roots = FileUtilities.asFileList(self.toArray());

		List<File> folders = FileUtilities.listFolders(roots);
		if (closure != null) {
			folders = (List<File>) DefaultGroovyMethods.findAll(folders, closure);
		}

		return FileUtilities.sortByUniquePath(folders);
	}

	public static List<File> getMediaFolders(File self) throws IOException {
		final List<File> mediaFolders = new ArrayList<File>();

		Files.walkFileTree(self.toPath(), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				File folder = dir.toFile();

				if (folder.isHidden() || !folder.canRead()) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (FileUtilities.filter(FileUtilities.getChildren(folder), VIDEO_FILES).size() > 0 || MediaDetection.isDiskFolder(folder)) {
					mediaFolders.add(folder);
					return FileVisitResult.SKIP_SUBTREE;
				}

				return FileVisitResult.CONTINUE;
			}
		});

		return FileUtilities.sortByUniquePath(mediaFolders);
	}

	public static Iterable<File> eachMediaFolder(Collection<?> folders, Closure<?> closure) throws IOException {
		List<File> mediaFolders = new ArrayList<File>();
		for (File root : FileUtilities.asFileList(folders)) {
			mediaFolders.addAll(getMediaFolders(root));
		}

		// remove duplicates
		mediaFolders = FileUtilities.sortByUniquePath(mediaFolders);

		return DefaultGroovyMethods.each(mediaFolders, closure);
	}

	public static String getNameWithoutExtension(File self) {
		return FileUtilities.getNameWithoutExtension(self.getName());
	}

	public static String getNameWithoutExtension(String self) {
		return FileUtilities.getNameWithoutExtension(self);
	}

	public static String getExtension(File self) {
		return FileUtilities.getExtension(self);
	}

	public static String getExtension(String self) {
		return FileUtilities.getExtension(self);
	}

	public static boolean hasExtension(File self, String... extensions) {
		return FileUtilities.hasExtension(self, extensions);
	}

	public static boolean hasExtension(String self, String... extensions) {
		return FileUtilities.hasExtension(self, extensions);
	}

	public static boolean isDerived(File self, File other) {
		return FileUtilities.isDerived(self, other);
	}

	public static File validateFileName(File self) {
		return FileUtilities.validateFileName(self);
	}

	public static String validateFileName(String self) {
		return FileUtilities.validateFileName(self);
	}

	public static File validateFilePath(File self) {
		return FileUtilities.validateFilePath(self);
	}

	public static FastFile memoize(File self) {
		return new FastFile(self.getPath());
	}

	public static File moveTo(File self, File destination) throws IOException {
		return FileUtilities.moveRename(self, destination);
	}

	public static File copyAs(File self, File destination) throws IOException {
		return FileUtilities.copyAs(self, destination);
	}

	public static File copyTo(File self, File destination) throws IOException {
		return FileUtilities.copyAs(self, new File(destination, self.getName()));
	}

	public static void createFileIfNotExists(File self) throws IOException {
		FileUtilities.createFileIfNotExists(self);
	}

	public static File relativize(File self, File other) throws IOException {
		return self.getCanonicalFile().toPath().relativize(other.getCanonicalFile().toPath()).toFile();
	}

	public static Map<File, List<File>> mapByFolder(Collection<?> files) {
		return FileUtilities.mapByFolder(FileUtilities.asFileList(files));
	}

	public static Map<String, List<File>> mapByExtension(Collection<?> files) {
		return FileUtilities.mapByExtension(FileUtilities.asFileList(files));
	}

	public static String normalizePunctuation(String self) {
		return Normalization.normalizePunctuation(self);
	}

	// Web and File IO helpers

	public static ByteBuffer fetch(URL self) throws IOException {
		return WebRequest.fetch(self);
	}

	public static String getText(ByteBuffer self) {
		return Charset.forName("UTF-8").decode(self.duplicate()).toString();
	}

	public static ByteBuffer get(URL self) throws IOException {
		return WebRequest.fetch(self, 0, null, null);
	}

	public static ByteBuffer get(URL self, Map<String, String> requestParameters) throws IOException {
		return WebRequest.fetch(self, 0, requestParameters, null);
	}

	public static ByteBuffer post(URL self, Map<String, ?> parameters, Map<String, String> requestParameters) throws IOException {
		return WebRequest.post(self, parameters, requestParameters);
	}

	public static ByteBuffer post(URL self, String text, Map<String, String> requestParameters) throws IOException {
		return WebRequest.post(self, text.getBytes("UTF-8"), "text/plain", requestParameters);
	}

	public static ByteBuffer post(URL self, byte[] postData, String contentType, Map<String, String> requestParameters) throws IOException {
		return WebRequest.post(self, postData, contentType, requestParameters);
	}

	public static File saveAs(ByteBuffer self, String path) throws IOException {
		return saveAs(self, new File(path));
	}

	public static File saveAs(String self, String path) throws IOException {
		return saveAs(self, new File(path));
	}

	public static File saveAs(URL self, String path) throws IOException {
		return saveAs(self, new File(path));
	}

	public static File saveAs(ByteBuffer self, File file) throws IOException {
		// resolve relative paths
		file = file.getAbsoluteFile();

		// make sure parent folders exist
		FileUtilities.createFolders(file.getParentFile());

		return FileUtilities.writeFile(self, file);
	}

	public static File saveAs(String self, File file) throws IOException {
		return saveAs(Charset.forName("UTF-8").encode(self), file);
	}

	public static File saveAs(URL self, File file) throws IOException {
		// resolve relative paths
		file = file.getAbsoluteFile();

		// make sure parent folders exist
		FileUtilities.createFolders(file.getParentFile());

		org.apache.commons.io.FileUtils.copyURLToFile(self, file);
		return file;
	}

	public static String objectToJson(Object self) throws IOException {
		return JsonWriter.objectToJson(self);
	}

	public static Object jsonToObject(String self) throws IOException {
		return JsonReader.jsonToJava(self);
	}

	public static File getStructurePathTail(File self) throws IOException {
		return MediaDetection.getStructurePathTail(self);
	}

	public static FolderWatchService watchFolder(File self, Closure<?> callback) throws IOException {
		return watchFolder(self, false, false, 1000, callback);
	}

	public static FolderWatchService watchFolder(File self, boolean watchTree, boolean commitPerFolder, long commitDelay, final Closure<?> callback) throws IOException {
		FolderWatchService watchService = new FolderWatchService(watchTree) {

			@Override
			public void processCommitSet(File[] files, File dir) {
				callback.call(asList(files));
			}
		};

		// collect updates for 500 ms and then batch process
		watchService.setCommitDelay(commitDelay);
		watchService.setCommitPerFolder(commitPerFolder);

		// start watching the given folder
		watchService.watchFolder(self);

		return watchService;
	}

	public static float getSimilarity(String self, String other) {
		return new NameSimilarityMetric().getSimilarity(self, other);
	}

	public static Collection<?> sortBySimilarity(Collection<?> self, final Object prime, final Closure<String> toStringFunction) {
		final SimilarityMetric metric = new NameSimilarityMetric();
		List<Object> values = new ArrayList<Object>(self);
		Collections.sort(values, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				String s1 = toStringFunction != null ? toStringFunction.call(o1) : o1.toString();
				String s2 = toStringFunction != null ? toStringFunction.call(o2) : o2.toString();

				return Float.compare(metric.getSimilarity(s2, prime), metric.getSimilarity(s1, prime));
			}
		});
		return values;
	}

	public static MetaAttributeView getXattr(File self) {
		try {
			return new MetaAttributeView(self);
		} catch (Exception e) {
			return null;
		}
	}

	public static Object getMetadata(File self) {
		try {
			return new MetaAttributes(self).getObject();
		} catch (Exception e) {
			return null;
		}
	}

}
