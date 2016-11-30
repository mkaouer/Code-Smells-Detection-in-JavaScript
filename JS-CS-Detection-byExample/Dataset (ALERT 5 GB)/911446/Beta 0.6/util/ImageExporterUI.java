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
 * Wrapper Class for Graph exporters. Handles the progress bar of image exports
   as well as user events. 

   * @author Mark Hummel

*/


package org.mov.util;

import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;

 public class ImageExporterUI {
     private ProgressDialog progress = null;
     private Thread mainThread;

     public ImageExporterUI() {
	 mainThread = Thread.currentThread();
	 progress = ProgressDialogManager.getProgressDialog();
	 progress.setIndeterminate(false);
	 progress.setProgress(0);	

     }

     public void setMaximum(int max) {
	 progress.setMaximum(max);
	 
     }

     public void display() {
	 progress.show(Locale.getString("GRAPH_EXPORTING"));	 
     }

     public void update() {
	 progress.increment();
     }

     public boolean isActive() {
	 return (mainThread.isInterrupted()) ? false : true;	 
     }

     public void finish() {
	 ProgressDialogManager.closeProgressDialog(progress);
     }

 }
