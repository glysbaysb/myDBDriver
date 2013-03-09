/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myDBDriver;

/**
 *
 * @author 5.7.2012
 */
public abstract class Field {
    protected String name;
    
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

    public abstract String GetType();

    public boolean equals(String name, String type) {
       return this.name.equalsIgnoreCase(name) && 
                this.GetType().equals(type);
    }
    
    public abstract void SetValue(Object value);
    public abstract Object GetValue();
    
    public abstract boolean compare(Object value);
    public abstract boolean compareGreater(Object value);
    public abstract boolean compareLower(Object value);
}
