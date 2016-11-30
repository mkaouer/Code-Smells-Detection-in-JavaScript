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

package pixelitor.filters.lookup;

import java.awt.image.LookupTable;

public enum StaticLookupType { INVERT {
    @Override
    public String getMenuName() {
        return "Invert";
    }

    @Override
    public LookupTable getLookupTable() {
        return LookupFactory.createLookupForInvert();
    }
}, REMOVE_RED {
    @Override
    public String getMenuName() {
        return "Remove Red";
    }

    @Override
    public LookupTable getLookupTable() {
        return LookupFactory.createLookupForRemoveRed();
    }
}, RED {
    @Override
    public String getMenuName() {
        return "Red";
    }

    @Override
    public LookupTable getLookupTable() {
        return LookupFactory.createLookupForOnlyRed();
    }
}, REMOVE_GREEN {
    @Override
    public String getMenuName() {
        return "Remove Green";
    }

    @Override
    public LookupTable getLookupTable() {
        return LookupFactory.createLookupForRemoveGreen();
    }
}, GREEN {
    @Override
    public String getMenuName() {
        return "Green";
    }

    @Override
    public LookupTable getLookupTable() {
        return LookupFactory.createLookupForOnlyGreen();
    }
}, REMOVE_BLUE {
    @Override
    public String getMenuName() {
        return "Remove Blue";
    }

    @Override
    public LookupTable getLookupTable() {
        return LookupFactory.createLookupForRemoveBlue();
    }
}, BLUE {
    @Override
    public String getMenuName() {
        return "Blue";
    }

    @Override
    public LookupTable getLookupTable() {
        return LookupFactory.createLookupForOnlyBlue();
    }
};
    abstract String getMenuName();
    abstract LookupTable getLookupTable();
}
