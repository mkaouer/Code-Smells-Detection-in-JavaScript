package net.filebot.ui.analyze;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static net.filebot.MediaTypes.*;
import static net.filebot.util.FileUtilities.*;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import net.filebot.MediaTypes;
import net.filebot.media.MediaDetection;
import net.filebot.ui.analyze.FileTree.FolderNode;
import net.filebot.ui.transfer.DefaultTransferHandler;
import net.filebot.util.ui.LoadingOverlayPane;
import net.miginfocom.swing.MigLayout;

class TypeTool extends Tool<TreeModel> {

	private FileTree tree = new FileTree();

	public TypeTool() {
		super("Types");

		setLayout(new MigLayout("insets 0, fill"));
		JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setBorder(BorderFactory.createEmptyBorder());

		add(new LoadingOverlayPane(treeScrollPane, this), "grow");

		tree.setTransferHandler(new DefaultTransferHandler(null, new FileTreeExportHandler()));
		tree.setDragEnabled(true);
	}

	@Override
	protected TreeModel createModelInBackground(File root) throws InterruptedException {
		List<File> filesAndFolders = (root != null) ? listFiles(singleton(root), FILE_WALK_MAX_DEPTH, true, true, true) : new ArrayList<File>();
		List<TreeNode> groups = new ArrayList<TreeNode>();

		for (Entry<String, FileFilter> it : getMetaTypes().entrySet()) {
			List<File> selection = filter(filesAndFolders, it.getValue());
			if (selection.size() > 0) {
				groups.add(createStatisticsNode(it.getKey(), selection));
			}
		}

		SortedMap<String, TreeNode> extensionGroups = new TreeMap<String, TreeNode>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, List<File>> it : mapByExtension(filter(filesAndFolders, FILES)).entrySet()) {
			if (it.getKey() == null)
				continue;

			extensionGroups.put(it.getKey(), createStatisticsNode(it.getKey(), it.getValue()));
		}
		groups.addAll(extensionGroups.values());

		// create tree model
		return new DefaultTreeModel(new FolderNode("Types", groups));
	}

	public Map<String, FileFilter> getMetaTypes() {
		Map<String, FileFilter> types = new LinkedHashMap<String, FileFilter>();
		types.put("Episode", new EpisodeFilter());
		types.put("Movie", new MovieFilter());
		types.put("Video", MediaTypes.VIDEO_FILES);
		types.put("Subtitle", MediaTypes.SUBTITLE_FILES);
		types.put("Audio", MediaTypes.AUDIO_FILES);
		types.put("Archive", MediaTypes.ARCHIVE_FILES);
		types.put("Verification", MediaTypes.VERIFICATION_FILES);
		types.put("Clutter", MediaDetection.getClutterFileFilter());
		types.put("Disk Folder", MediaDetection.getDiskFolderFilter());
		return types;
	}

	private static class EpisodeFilter implements FileFilter {

		private final static long MAX_SIZE = 50 * MEGA;

		@Override
		public boolean accept(File file) {
			return file.length() > MAX_SIZE && VIDEO_FILES.accept(file) && MediaDetection.isEpisode(file.getPath(), true);
		}
	}

	private static class MovieFilter implements FileFilter {

		private final static long MAX_SIZE = 500 * MEGA;

		@Override
		public boolean accept(File file) {
			try {
				return file.length() > MAX_SIZE && VIDEO_FILES.accept(file) && !MediaDetection.isEpisode(file.getPath(), true) && MediaDetection.matchMovieName(asList(file.getName(), file.getParent()), true, 0).size() > 0;
			} catch (Exception e) {
				return false;
			}
		}
	}

	@Override
	protected void setModel(TreeModel model) {
		tree.setModel(model);
	}

}
