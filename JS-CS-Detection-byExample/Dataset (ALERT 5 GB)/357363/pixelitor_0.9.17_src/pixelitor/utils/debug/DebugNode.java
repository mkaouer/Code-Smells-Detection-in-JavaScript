/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.utils.debug;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 *
 */
public class DebugNode extends DefaultMutableTreeNode {
    private String name;

    DebugNode(String name, Object userObject) {
        super(userObject);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toDetailedString() {
        if (userObject == null) {
            return name + " = null";
        }
        Enumeration<DefaultMutableTreeNode> childrenEnum = children();
        StringBuilder sb = new StringBuilder();

        addIndent(sb, getLevel());
        sb.append(name).append(" {");

        while (childrenEnum.hasMoreElements()) {
            Object o = childrenEnum.nextElement();
            addIndent(sb, getLevel() + 1);

            DefaultMutableTreeNode t = (DefaultMutableTreeNode) o;

            String info;
            if (t instanceof DebugNode) {
                DebugNode dn = (DebugNode) t;
                info = dn.toDetailedString();
            } else {
                info = t.toString();
            }
            sb.append(info);
        }

        addIndent(sb, getLevel());
        sb.append('}');

        return sb.toString();
    }

    void addStringChild(String name, String s) {
        add(new DefaultMutableTreeNode(name + " = " + s));
    }

    void addQuotedStringChild(String name, String s) {
        add(new DefaultMutableTreeNode(name + " = \"" + s + '\"'));
    }

    void addIntChild(String name, int i) {
        add(new DefaultMutableTreeNode(name + " = " + i));
    }

    void addFloatChild(String name, float f) {
        add(new DefaultMutableTreeNode(name + " = " + f));
    }

    void addBooleanChild(String name, boolean b) {
        add(new DefaultMutableTreeNode(name + " = " + b));
    }

    void addClassChild() {
        add(new DefaultMutableTreeNode("class = " + userObject.getClass().getName()));
    }

    private static void addIndent(StringBuilder sb, int indentLevel) {
        sb.append('\n');
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
    }
}
