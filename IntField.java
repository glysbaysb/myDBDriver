/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myDBDriver;

/**
 *
 * @author 5.7.2012
 */
public class IntField extends Field  {
    private int    intValue;
    
    IntField(String name, int val) {
        this.name = name;
        intValue = val;
        
        type = "int";
    }
    
    IntField(String name) {
        this.name = name;
        
        type = "int";        
    }
    
    @Override
    public Object GetValue() {   
        return intValue;
    }

    @Override
    public void SetValue(Object value) {    
        this.intValue = Integer.parseInt(value.toString());
    }
        
    @Override
    public boolean compare(Object value) {
        return intValue == Integer.parseInt(value.toString());
    }
}
