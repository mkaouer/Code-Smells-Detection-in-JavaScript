/*
 * Copyright 2009 László Balázs-Csíki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package pixelitor.filters;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 */
public class ParamSet implements Iterable<LinearIntParam> {
    private LinearIntParam[] params;
    private Iterator iterator = new InternalIterator();

    public ParamSet(LinearIntParam[] params) {
        this.params = params;
    }

    public ParamSet(LinearIntParam param) {
        this.params = new LinearIntParam[]{param};
    }

    @Override
    public Iterator iterator() {
        return iterator;
    }

    public void reset() {
        for (LinearIntParam param : params) {
            param.setValueIsAdjusting(true);
            param.reset();
            param.setValueIsAdjusting(false);
        }
    }

    class InternalIterator implements Iterator<LinearIntParam> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return (index < params.length);
        }

        @Override
        public LinearIntParam next() {
            if (index < params.length) {
                return params[index++];
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
