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


package nz.org.venice.ui;

import java.util.HashMap;
import java.util.Vector;
import java.util.Set;
import java.util.Iterator;
import nz.org.venice.main.ModuleFrame;


/**
 *
 * FrameRegister is a collection of ModuleFrames. 
 * It is an implementation of a simple monitor and it's purpose is to
 * serialise the creation of new ModuleFrames. 
 *
 * FrameRegister was written initially for the purpose of restoring saved windows
 * as a way of accessing the new ModuleFrame objects but also as a way
 * of serialising their creation. The use of some non thread safe objects were 
 * discovered when trying to create multiple ChartModules and the decision was
 * made to serialise their creation under restoration rather than attempt to modify code that otherwise works perfectly well.

 * This class is deprecated.

 * @author Mark Humel

 */

/* For some reason, creation of multiple ChartModules isn't  a problem anymore, so this class is unnecessary.   */

/* 
  FrameRegister is a HashMap rather than a Vector because although 
  Main.restoreSavedWindows() creates and access frames serially via an index,
  that's not sufficient reason to apply that restriction for other uses.
.
  Further, a general key is necessary for the frame to remove itself from
  the register.
 */

public class FrameRegister extends HashMap {

    int frameCount; 

    public FrameRegister() {
	super();
	frameCount = 0;
    }

    /**
     *
     * Add new ModuleFrame to register, using size as key
     * 
     * @param frame A new ModuleFrame 
     */
    
    public synchronized void add(ModuleFrame frame) {

	frameCount++;
	put(String.valueOf(frameCount), frame);
    }

    /**
     *
     * Register a new ModuleFrame identified by key
     * 
     * @param key The Identifier for a ModuleFrame
     * @param frame The ModuleFrame to associate with key
     */

    public synchronized void put(String key, ModuleFrame frame) {
	super.put(key, frame);
	notifyAll();

    }

    /**
     *
     * Return the ModuleFrame identified by key
     * 
     * The method will block if there is no ModuleFrame associated with
     * the key.
     * 
     * @param key The identifier for the ModuleFrame
     *     
     */

    public synchronized ModuleFrame get(String key) {

	ModuleFrame frame = (ModuleFrame)super.get(key);

	while (frame == null) {
	    try {
		wait();
	    } catch (InterruptedException ie) {
		return null;
	    }
	    frame = (ModuleFrame)super.get(key);
	}
	notifyAll();
	return frame;    
    }

    /**
     *
     * Return true if there is a mapping to 
     */

    public synchronized boolean find(String key) {
	ModuleFrame frame = (ModuleFrame)super.get(key);
	return (frame == null) ? false : true;
    }
    
    /**
     * Return true if there is a moduleframe which has a module of key
     childkey
     * 
     * @param childKey The identifier of a module
    */
    public synchronized boolean findChild(String childKey) {
	Set set = keySet();
	Iterator iterator = set.iterator();
	boolean  rv = false;

	while (iterator.hasNext()) {
	    String parentKey = (String)iterator.next();
	    ModuleFrame f = (ModuleFrame)super.get(parentKey);
	    if (String.valueOf(f.getModule().hashCode()).equals(childKey)) {
		rv = true;
	    }
	}
	return rv;
    }

    /**
     *
     * Remove all references to the ModuleFrame
     * 
     * @param frame The ModuleFrame to delete 
     */

    public void delete(ModuleFrame frame) {
	
	Vector deleteList = new Vector(); //A list of the frame references to remove
	
	if (containsValue(frame)) {
	    Set set = keySet();
	    Iterator iterator = set.iterator();
	    while (iterator.hasNext()) {
		String key = (String)iterator.next();
		ModuleFrame f = (ModuleFrame)super.get(key);		
		if (f != null) {
		    //The deletion is postponed to avoid a concurrent modification exception
		    deleteList.add(key);
		}
	    }
	}

	//Now remove all the references in the delete list
	Iterator iterator = deleteList.iterator();
	while (iterator.hasNext()) {
	    String key = (String)iterator.next();
	    super.remove(key);
	}
    }

    /**
       Return true if there is a frame of a certain type in the register.
     */

    public ModuleFrame getFrameOfType(String type) {
	Set keySet = keySet();
	Iterator iterator = keySet.iterator();

	while (iterator.hasNext()) {
	    String key = (String)iterator.next();
	    ModuleFrame frame = (ModuleFrame)super.get(key);
	    	    	   
	    if (frame.getClass().getName().equals(type)) {
		return frame;
	    }	    
	}
	return null;
    }

}