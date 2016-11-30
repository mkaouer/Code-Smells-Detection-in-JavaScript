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
 *  This class reads and parses a record inside an EMASTER file.
 *
 * @author Guillermo Bonvehi (gbonvehi)
 * @see MSEmasterFile
 */

package nz.org.venice.quote;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class MSDataInfo {
    /**
	 * 
	 */
    private int fileNum;
    private int numFields;
    private String stockSymbol;
    private String stockName;
    private String timeFrame;
    private String firstDate;
    private String lastDate;

    public int getFileNum() { return this.fileNum; }
    public int getNumFields() { return this.numFields; }
    public String getStockSymbol() { return this.stockSymbol; }
    public String getStockName() { return this.stockName; }
    public String getTimeFrame() { return this.timeFrame; }
    public String getFirstDate() { return this.firstDate; }
    public String getLastDate() { return this.lastDate; }
    
    public MSDataInfo(RandomAccessFile file) throws IOException {
		byte[] b;
        file.skipBytes(2);
        this.fileNum = file.readByte();
        file.skipBytes(3);
        this.numFields = file.readByte();
        file.skipBytes(4);
        b = new byte[14];
        file.read(b);
        this.stockSymbol = new String(b).trim();
        file.skipBytes(7);
        b = new byte[16];
        file.read(b);
        this.stockName = new String(b).trim();
        file.skipBytes(12);
        this.timeFrame = new String(new byte[] {file.readByte()});
        file.skipBytes(3);
        b = new byte[4];
        file.read(b);
        this.firstDate = MetastockBinaryEODImport.floatToBritishDate(ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        file.skipBytes(4);
        b = new byte[4];
        file.read(b);
        this.lastDate = MetastockBinaryEODImport.floatToBritishDate(ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        file.skipBytes(116);
    }
}
