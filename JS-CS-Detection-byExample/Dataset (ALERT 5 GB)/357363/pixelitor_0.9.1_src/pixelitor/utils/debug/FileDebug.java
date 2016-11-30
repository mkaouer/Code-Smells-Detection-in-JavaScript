/*
 * Copyright 2009-2010 László Balázs-Csíki
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
package pixelitor.utils.debug;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Utility methods for writing debug information into text files
 */
public class FileDebug {
    public static void debugBufferedImage(BufferedImage bi, String descr) {
        BufferedImageNode node = new BufferedImageNode(descr, bi);
        String debugInfo = node.toDetailedString();
        String fileName = descr + ".txt";
        File f = new File(fileName);
        if (f.exists()) {
            throw new IllegalStateException(f + "already exists");
        }
        Writer output;
        try {
            output = new BufferedWriter(new FileWriter(f));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            output.write(debugInfo);
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
