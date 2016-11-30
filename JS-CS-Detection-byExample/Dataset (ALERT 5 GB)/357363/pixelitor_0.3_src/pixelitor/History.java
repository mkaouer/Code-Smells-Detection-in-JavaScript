/*
 * Copyright 2009 L�szl� Bal�zs-Cs�ki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor;

import java.awt.image.BufferedImage;

/**
 * Manages history and undo for all open images
 */
public final class History {
    private static ImageChangeReason previousReason;

	// for the undo
	private static BufferedImage backupBufferedImage = null;
    // the owner of the undo
    private static ImageComponent undoOwner = null;

    // this is a utility class with static methods, it should not be instantiated
    private History() {}

    public static void undo() {
        ImageChangeReason.UNDO.setPreviousReason(previousReason);
		undoOwner.changeActiveLayerImage(backupBufferedImage, ImageChangeReason.UNDO);
	}

	public static boolean isUndoAvailable(ImageComponent activeImage) {
        if(backupBufferedImage != null) {
            if(activeImage == undoOwner) {
                return true;
            }
        }
		return false;
	}

	public static void setBackup(BufferedImage newBackupBufferedImage, ImageComponent ic, ImageChangeReason changeReason) {
		backupBufferedImage = newBackupBufferedImage;
        undoOwner = ic;
        previousReason = changeReason;
	}

    // used for fade
	public static BufferedImage getBackupBufferedImage() {
		return backupBufferedImage;
	}

    public static ImageChangeReason getPreviousReason() {
        return previousReason;
    }
}
