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

package org.mov.ui;

/**
 * This model is used for providing alphabetically sorted displays to JList gadgests
 */
import javax.swing.*;
import java.util.*;

public class SortedListModel extends AbstractListModel {

    // Define a SortedSet
    SortedSet model;

    public SortedListModel() {
        // Create a TreeSet
        // Store it in SortedSet variable
        model = new TreeSet();
    }

    // ListModel methods
    public int getSize() {
        // Return the model size
        return model.size();
    }

    public java.lang.Object getElementAt(int index) {
        // Return the appropriate element
        if (index == -1)
            index = 0;
        return model.toArray()[index];
    }

    // Other methods
    public void addElement(Object element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    public void addAll(Object elements[]) {
        Collection c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(Object element) {
        return model.contains(element);
    }

    public Object firstElement() {
        // Return the appropriate element
        return model.first();
    }

    public Iterator iterator() {
        return model.iterator();
    }

    public Object lastElement() {
        // Return the appropriate element
        return model.last();
    }

    public boolean removeElement(Object element) {
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;   
    }
}
