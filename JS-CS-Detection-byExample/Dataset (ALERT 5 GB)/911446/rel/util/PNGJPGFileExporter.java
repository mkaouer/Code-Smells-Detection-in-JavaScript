/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package nz.org.venice.util;

/**
 * Writes a PNG or JPG file from a BufferedImage using javax.imageio.ImageIO
 * 
 * @author Guillermo Bonvehi
 * 
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PNGJPGFileExporter implements IImageExporter {
	private String writerFormat;
	public PNGJPGFileExporter(String writerFormat) {
		this.writerFormat = writerFormat;
	}
	public void export(String parFilename, BufferedImage parImage) {
		ImageExporterUI exportUI = new ImageExporterUI();
		try {
			exportUI.setMaximum(1);
			exportUI.display();
			javax.imageio.ImageIO.write(parImage, this.writerFormat, new File(parFilename));
			exportUI.finish();
		} catch (IOException e) {
			exportUI.error(e.getLocalizedMessage());
		}
	}

}
