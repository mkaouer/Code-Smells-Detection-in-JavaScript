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

/**
 *  This class reads and parses an EMASTER file written by Metastock.
 *  A MSDataInfo object is generated for each entry in the file.
 *
 * @author Guillermo Bonvehi (gbonvehi)
 * @see MSDataInfo
 */

package nz.org.venice.quote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;


class MSEmasterFile {
	private static MSEmasterFile instance = new MSEmasterFile();
    public static synchronized MSEmasterFile getInstance() {  
    	return instance;
    }  

    private boolean fileLoaded;
    private File masterFile;
    private RandomAccessFile masterStream;
    private HashMap stocksByFile; // <String, MSDataInfo>
  
    public int filesNo;
    public int lastFile;
        
    public boolean load(File masterFile) throws FileNotFoundException, IOException {
    	if (this.masterFile != null && (masterFile.getPath().equals(this.masterFile.getPath())))
    		return true;
    	this.masterFile = masterFile;
	this.stocksByFile = new HashMap(); // <String, MSDataInfo>
    	this.masterStream = new RandomAccessFile(this.masterFile,"r");
        byte[] b = new byte[2];
        this.masterStream.read(b);
        this.filesNo = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort(); // (int)((0x000000FF & ((int)b[1]))  8  (0x000000FF & ((int)b[0])));
        this.masterStream.read(b);
        this.lastFile = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort(); //(int)((0x000000FF & ((int)b[1]))  8  (0x000000FF & ((int)b[0])));
        this.masterStream.skipBytes(188);
        for(int i = 0; i < this.filesNo; i++) {
            MSDataInfo sdi = new MSDataInfo(this.masterStream);
            this.stocksByFile.put("F"+sdi.getFileNum()+".DAT", sdi);
        }
        this.fileLoaded = true;
        return this.fileLoaded;
    }
    
    public HashMap getDataInfo() { // <String, MSDataInfo>
    	if (!this.fileLoaded) return null;
    	return this.stocksByFile;
    }
    
    public MSDataInfo getDataInfo(File file) {
    	if (!this.containsFile(file)) return null;
	return (MSDataInfo)this.stocksByFile.get(file.getName().toUpperCase());
    }
    
    public boolean containsFile(File file) {
    	if (this.stocksByFile == null) return false;
    	return this.stocksByFile.containsKey(file.getName().toUpperCase());
    }
}
 