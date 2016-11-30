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
 * Image exporter factory, get the correct exporter by extension when
 * exporting a graph.
 * 
 * @author Guillermo Bonvehi
 * @see IImageExporter
 */

public class ImageExporterFactory {
	public static IImageExporter get(String filename) {
		String ext = getExtension(filename);

		if (ext.equals("bmp")) {
			// TODO: Create a set method in IImageExporter to allow the program
			// set the verbose level on all exporters and remove BMPFile(bool)
			// construct.
			return new BMPFile(true);
		} else if (ext.equals("png")) {
			return new PNGJPGFileExporter("png");
		} else if (ext.equals("jpg") || ext.equals("jpeg")) {
			return new PNGJPGFileExporter("jpg");
		}
		throw new IllegalArgumentException("Supported file extension expected.");
	}
	
	public static String getExtension(String filename) {
		String ext = null;
		int i = filename.lastIndexOf(".");
		
		if (i > 0 &&  i < filename.length() - 1) {
		    ext = filename.substring(i+1).toLowerCase();
		}
		return ext;
	    }
}
