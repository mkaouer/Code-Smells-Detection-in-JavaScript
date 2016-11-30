package net.filebot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.filebot.util.FileUtilities;

public enum StandardRenameAction implements RenameAction {

	MOVE {

		@Override
		public File rename(File from, File to) throws Exception {
			return FileUtilities.moveRename(from, to);
		}
	},

	COPY {

		@Override
		public File rename(File from, File to) throws Exception {
			return FileUtilities.copyAs(from, to);
		}
	},

	KEEPLINK {

		@Override
		public File rename(File from, File to) throws Exception {
			File destionation = FileUtilities.resolveDestination(from, to, true);

			// move file and the create a symlink to the new location via NIO.2
			try {
				Files.move(from.toPath(), destionation.toPath());
				FileUtilities.createRelativeSymlink(from, destionation, true);
			} catch (LinkageError e) {
				throw new Exception("Unsupported Operation: move, createSymbolicLink");
			}

			return destionation;
		}
	},

	SYMLINK {

		@Override
		public File rename(File from, File to) throws Exception {
			File destionation = FileUtilities.resolveDestination(from, to, true);

			// create symlink via NIO.2
			try {
				return FileUtilities.createRelativeSymlink(destionation, from, true);
			} catch (LinkageError e) {
				throw new Exception("Unsupported Operation: createSymbolicLink");
			}
		}
	},

	HARDLINK {

		@Override
		public File rename(File from, File to) throws Exception {
			File destionation = FileUtilities.resolveDestination(from, to, true);

			// create hardlink via NIO.2
			try {
				return FileUtilities.createHardLinkStructure(destionation, from);
			} catch (LinkageError e) {
				throw new Exception("Unsupported Operation: createLink");
			}
		}
	},

	DUPLICATE {

		@Override
		public File rename(File from, File to) throws Exception {
			try {
				return HARDLINK.rename(from, to);
			} catch (Exception e) {
				return COPY.rename(from, to);
			}
		}
	},

	RENAME {

		@Override
		public File rename(File from, File to) throws Exception {
			// rename only the filename
			File dest = new File(from.getParentFile(), to.getName());

			if (!from.renameTo(dest))
				throw new IOException("Rename failed: " + dest);

			return dest;
		}
	},

	TEST {

		@Override
		public File rename(File from, File to) throws IOException {
			return FileUtilities.resolveDestination(from, to, false);
		}
	};

	public String getDisplayName() {
		switch (this) {
		case MOVE:
			return "Rename";
		case COPY:
			return "Copy";
		case KEEPLINK:
			return "Keeplink";
		case SYMLINK:
			return "Symlink";
		case HARDLINK:
			return "Hardlink";
		default:
			return null;
		}
	}

	public static StandardRenameAction forName(String action) {
		for (StandardRenameAction it : values()) {
			if (it.name().equalsIgnoreCase(action))
				return it;
		}

		throw new IllegalArgumentException("Illegal rename action: " + action);
	}

}
