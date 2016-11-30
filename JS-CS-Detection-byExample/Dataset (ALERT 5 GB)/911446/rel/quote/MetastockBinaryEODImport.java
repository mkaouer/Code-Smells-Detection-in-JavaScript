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

package nz.org.venice.quote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.org.venice.util.Converter;
import nz.org.venice.util.Locale;
import nz.org.venice.util.Report;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;

public class MetastockBinaryEODImport implements IFileEODQuoteImport {
    private Report report;

    private String filename;
    private MSDataInfo msdatainfo;
    private InputStream reader;
    private int recordno;
	
    private boolean run;
	
    public MetastockBinaryEODImport(Report report, EODQuoteFilter filter) {
        this.report = report;
        this.reset();
    }
    
    private void reset() {
        this.reader = null;
        this.msdatainfo = null;
        this.recordno = 0;
        this.run = true;
    }
	
    public boolean open(File file) {
        try {
	      if (file.getName().toUpperCase().equals("EMASTER")) {
		  MSEmasterFile.getInstance().load(file);
		  return false; // EMASTER does not contain quote values
	      } else {
		  if (!MSEmasterFile.getInstance().load(
			  new File(file.getParent() + File.separator+"EMASTER"))) {
		                   report.addError(file.getName() + ":" +
		                   Locale.getString("ERROR") + ": " +
		                   Locale.getString("ERROR_READING_FROM_FILE", "EMASTER"));
				   return false;
		  }
		  if (!MSEmasterFile.getInstance().containsFile(file))
		    return false;
		  this.msdatainfo = MSEmasterFile.getInstance().getDataInfo(file);
		  this.reader = new FileInputStream(file);
		  this.filename = file.getName();
		  this.run = true;
		  return true;
	      }
	} catch (FileNotFoundException e) {
	  report.addError(file.getName() + ":" +
			  Locale.getString("ERROR") + ": " +
			  Locale.getString("ERROR_READING_FROM_FILE", file.getName()));
	} catch (IOException e) {
	  report.addError(file.getName() + ":" +
			  Locale.getString("ERROR") + ": " +
			  Locale.getString("ERROR_READING_FROM_FILE", file.getName()));
	  this.reset();
	}
	return false;
    }

  public List importNext() { // <EODQuote>
      List quotes = new ArrayList(); // <EODQuote>
      Symbol symbol;
      try {
	    symbol = Symbol.find(this.msdatainfo.getStockSymbol());

	    byte[] b = new byte[2];
	    reader.skip(2);
	    //reader.read(b);
	    //int max_recs = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort(); // (int)((0x000000FF & ((int)b[1])) << 8 | (0x000000FF & ((int)b[0])));
	    reader.read(b);
	    int last_rec = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort(); // (int)((0x000000FF & ((int)b[1])) << 8 | (0x000000FF & ((int)b[0])));
	    reader.skip(24);
	        
	    for (int i = 2;i<=last_rec;i++) {
	      this.recordno++;
	      b = new byte[4]; // float
	      reader.read(b);
	      float datef = mbfByteToIeeeFloat(b);
	      reader.read(b);
	      double open   = mbfByteToIeeeFloat(b);
	      reader.read(b);
	      double high   = mbfByteToIeeeFloat(b);
	      reader.read(b);
	      double low    = mbfByteToIeeeFloat(b);
	      reader.read(b);
	      double close  = mbfByteToIeeeFloat(b);
	      reader.read(b);
	      long volume = new Float(mbfByteToIeeeFloat(b) / 100f).longValue();
	      reader.skip(4); // opt_int

	      String dateStr = floatToBritishDate(datef);

	      try {
		   EODQuote quote = new EODQuote(
						 symbol
						 , new TradingDate(dateStr,TradingDate.BRITISH)
						 , volume
						 , low
						 , high
						 , open
						 , close
						 );
		   quotes.add(quote);
		   verify(quote);
	      } catch (TradingDateFormatException e) {
                   report.addError(this.filename + ":" +
				   Integer.toString(this.recordno) + ":" +
				   Locale.getString("ERROR") + ": " +
				   e.getMessage());
	      }
	    }
      } catch (IOException e) {
	report.addError(this.filename + ":" +
			Locale.getString("ERROR") + ": " +
			Locale.getString("ERROR_READING_FROM_FILE", this.filename));
      } catch (SymbolFormatException e) {
	report.addError(this.filename + ":" +
			Locale.getString("ERROR") + ": " +
			Locale.getString("ERROR_READING_FROM_FILE", this.filename));
      } 
      this.run = false;
      return quotes;
  }

  public boolean isNext() {
    return this.run;
  }

  public void close() {
  }

  /**
   * Verify the quote is valid. Log any problems to the report and try to clean
   * it up the best we can.
   *
   * @param quote the quote
   */
  private void verify(EODQuote quote) {
    try {
         quote.verify();
    }
    catch (QuoteFormatException e) {
        List messages = e.getMessages(); // <String>

	for (Iterator iterator = messages.iterator(); iterator.hasNext();) { // <String>

	  String message = (String)iterator.next();

	  report.addWarning(filename + ":" + 
			    Integer.toString(recordno) + ":" +
			    Locale.getString("WARNING") + ": " +
			    message);
	}
    }
  }
	
  public static String floatToBritishDate(float f) {
      int date = (int)f;
      int year = 1900 + (date / 10000);
      int month = (date % 10000) / 100;
      int day = date % 100;

      Integer args[] = { new Integer(day), new Integer(month), new Integer(year) };
      int lengths[] = { 2, 2, 4 };

      return Converter.dateFormat(args, lengths, "-");
  }
 
  // http://j2eecode.blogspot.com/2010/03/microsoft-basic-floating-point-vs-ieee.html
  public static float mbfByteToIeeeFloat(byte[] bytes) {
      final int BYTE_MASK = 0x0ff;
      final int MANTISSA_MASK = 0x007fffff;
      final int EXPONENT_MASK = 0x0ff;
      final int SIGN_MASK = 0x080;
      int intOne = (int) (bytes[0] & BYTE_MASK);
      int intTwo = (int) (bytes[1] & BYTE_MASK);
      int intThree = (int) (bytes[2] & BYTE_MASK);
      int intFour = (int) (bytes[3] & BYTE_MASK);

      int msf = intFour << 24 | intThree << 16 | intTwo << 8 | intOne;

      int mantissa = (msf & MANTISSA_MASK);
      int exponent = ((msf >> 24) & EXPONENT_MASK) - 2;
      int sign = (msf >> 16) & SIGN_MASK;

      mantissa |= exponent << 23 | sign << 24;
      float result = Float.intBitsToFloat(mantissa);

      return result < 0 ? 0 : result;
    }
}
