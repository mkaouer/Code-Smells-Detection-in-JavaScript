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

/*
 * TODO: Replace with java.util.logging when time is available to make it output to file, not blast stderr. */

package nz.org.venice.util;

import java.io.*;

public class VeniceLog
{

    private static VeniceLog instance = null;
    private FileWriter writer = null;
    private BufferedWriter bwriter = null;
    private boolean enabled = true;

    public static synchronized VeniceLog getInstance() {
	if (instance == null) {
	    instance = new VeniceLog();
	}
	return instance;
    }

    private VeniceLog() {
      try {
          writer = new FileWriter("venice.log");
          bwriter = new BufferedWriter(writer);
          
      } catch (IOException e) {
          System.err.println("Couldn't open venice.log: " + e);	    
      }
    }

    public void log(String mesg) {
	if (!enabled) {
	    return;
	}
	try {
	    bwriter.write(mesg);
	    bwriter.newLine();
      bwriter.flush();
	} catch (IOException e) {
	    System.err.println("Couldn't write message: " + e);
	}
    }

    public void close() {
	if (!enabled) {
	    return;
	}
	try {
	    bwriter.close();
	} catch (IOException e) {
	    
	}
    }
    
  protected void finalize() throws Throwable {
      try {
          this.close();
      } finally {
          super.finalize();
      }
  }   
    
}
