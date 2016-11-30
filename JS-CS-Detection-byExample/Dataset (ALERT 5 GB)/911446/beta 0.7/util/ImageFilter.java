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

package org.mov.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Image filter for dialog boxes.
 * 
 * @author Mark Hummel
 
 */

public class ImageFilter extends FileFilter {

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String bmp = "bmp";
        
    public boolean accept(File f) {

	boolean rv = false;

	if (f.isDirectory()) {
	    return false;
	}

	String extension = getExtension(f);

	if (extension != null) {
	    if (extension.equals(tiff) ||
		extension.equals(tif) ||
		extension.equals(gif) ||
		extension.equals(jpeg) ||
		extension.equals(jpg) ||
		extension.equals(bmp) ||
		extension.equals(png)) {		
                return true;
	    } else {
		return false;        
	    }
	} 
	return false;
	
    }
    
    private String getExtension(File f) {
	String ext = null;
	String s = f.getName();
	int i = s.lastIndexOf(".");
	
	if (i > 0 &&  i < s.length() - 1) {
	    ext = s.substring(i+1).toLowerCase();
	}
	return ext;
    }
    
    public String getDescription() {
	return Locale.getString("GRAPH_EXPORT_DIALOG");
    }

}