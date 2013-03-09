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
    private Config cfg = Config.GetInstance();
    private List<Field> fields = new ArrayList<Field>();
    
    public void AddField(String name, int value) {
        Field f = new IntField(name, value);
        
        fields.add(f);      
    }
    
    public void AddField(String name, int size, byte[] value) {  
        fields.add(new BlobField(name, size, value));
    } 
    
    public int GetNumberOfFields() {
        return fields.size();
    }
    
    public byte[] GetBlobField(String name){      
      for(Field f : fields) {
          // Correct name and Correct type?
          if(f.equals(name, cfg.blobType)) {
              return (byte[])f.GetValue();
          }
      }      
      return null; 
    }
    
    public int GetIntField(String name){       
      for(Field f : fields) {
          // Right name and right type?
          if(f.equals(name, cfg.intType)) {
              return Integer.parseInt(f.GetValue().toString());
          }
      }        
      return 0;
    }
    
    public Object GetFieldValue(String name) {
      for(Field f : fields) {
          if(f.GetName().equalsIgnoreCase(name))
              return f.GetValue();
      }        
      return null;  
    }
    
    public Field GetField(String name) {
      for(Field f : fields) {
          if(f.GetName().equalsIgnoreCase(name))
          {
              return f;
          }
      }        
      return null;  
    }
}
