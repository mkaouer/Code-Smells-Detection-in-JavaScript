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

import java.util.EventListener;

/**
 * Interface for classes that are listening for module events. Module
 * events are triggered when modules are added, removed or renamed.
 *
 * @author Andrew Leppard
 */
public interface ModuleListener extends EventListener {
   
    /**
     * Called when a module has been added
     *
     * @param	moduleEvent	the module event
     */
    public void moduleAdded(ModuleEvent moduleEvent);

    /**
     * Called when a module has been renamed
     *
     * @param	moduleEvent	the module event
     */
    public void moduleRenamed(ModuleEvent moduleEvent);

    /**
     * Called when a module has been removed
     *
     * @param	moduleEvent	the module event
     */
    public void moduleRemoved(ModuleEvent moduleEvent);
}
