/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.SimpleNode;

public class ImportWrapper {
    private SimpleNode node;
    private String name;
    private String fullname;

    public ImportWrapper(String fullname, String name, SimpleNode node) {
        this.fullname = fullname;
        this.name = name;
        this.node = node;
    }


    public boolean equals(Object other) {
        ImportWrapper i = (ImportWrapper) other;
        if(name == null && i.getName() == null){
            return i.getFullName().equals(fullname);
        }
        return i.getName().equals(name);
    }

    public int hashCode() {
        if(name == null){
            return fullname.hashCode();
        }
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullname;
    }

    public SimpleNode getNode() {
        return node;
    }
}

