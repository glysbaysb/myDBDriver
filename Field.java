/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myDBDriver;

import java.util.Arrays;

/**
 *
 * @author 5.7.2012
 */
public abstract class Field {
    protected String name;
    // This cannot be changed once set
    protected String type;
    protected int    size;

    public String GetName() {
        return name;
    }

    public void SetName(String name) {
        this.name = name;
    }

    public int GetSize() {
        return size;
    }

    public String GetType() {
        return type;
    }

    public boolean equals(String name, String type) {
       return this.name.equalsIgnoreCase(name) && 
                this.type.equalsIgnoreCase(type);
    }
    
    public abstract void SetValue(Object value);
    public abstract Object GetValue();
    
    public abstract boolean compare(Object value);
}
