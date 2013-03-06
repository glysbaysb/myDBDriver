/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myDBDriver;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author g.ijewski
 */
public class Record {
    private List<Field> fields = new ArrayList<Field>();
    
    public void AddField(String name, int value) {
        Field f = new IntField(name, value);
        
        fields.add(f);      
    }
    
    public void AddField(String name, int size, byte[] value) {        
        Field f = new BlobField(name, size, value);
        
        fields.add(f);
    } 
    
    public int GetNumberOfFields() {
        return fields.size();
    }
    
    public byte[] GetBlobField(String name){      
      for(Field f : fields) {
          if(f.equals(name, "blob")) {
              return (byte[])f.GetValue();
          }
      }      
      return null; 
    }
    
    public int GetIntField(String name){       
      for(Field f : fields) {
          if(f.equals(name, "int")) {
              return Integer.parseInt(f.GetValue().toString());
          }
      }        
      return 0;
    }
    
    public Object GetFieldValue(String name) {
      for(Field f : fields) {
          if(f.GetName().equals(name))
              return f.GetValue();
      }        
      return null;  
    }
    
    public Field GetField(String name) {
      for(Field f : fields) {
          if(f.GetName().equals(name))
              return f;
      }        
      return null;  
    }
}
