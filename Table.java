/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myDBDriver;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 5.7.2012
 */
public class Table {
    private Config cfg = Config.GetInstance();
    private String filename;
    // Used to find out the type of each field
    private List<Field> fields = new ArrayList<Field>();
    // The actual data
    private List<Record> values = new ArrayList<Record>();
    
    private boolean tableInitalized = false;

    public Table(String filename) throws Exception {
      this.filename = filename;
       
      try {
        RandomAccessFile o = new RandomAccessFile(this.filename, "r"); 
        
        int numberOfValues = o.readInt();
        ReadLayout(o);
        if(numberOfValues > 0)
            ReadData(o, numberOfValues);
      }
      catch (FileNotFoundException e) {
          System.out.println("Create empty table");
          RandomAccessFile o = new RandomAccessFile(this.filename, "rws");
          
          // Writes the number of records
          o.writeInt(0);
          o.close();
          
          // Create in memory table
          fields = new ArrayList<Field>();
          values = new ArrayList<Record>();
      }
      catch ( Exception e ) {
        throw e;
      }   
    } 

    public void AddRecord(Record r) {
        tableInitalized = true; // No changes can be made
        values.add(r);
    }
    
    public void AddIntField(String name) throws Exception {
        if(this.tableInitalized)
            throw new IllegalThreadStateException("Table already defined");
        
        this.fields.add(new IntField(name, 0));
    }
    
    public void AddBlobField(int size, String name) throws Exception {
        if(this.tableInitalized)
            throw new IllegalThreadStateException("Table already defined");       
        this.fields.add(new BlobField(name, size, null));
    }    

    
    private void ReadLayout(RandomAccessFile o) throws Exception {
        try {          
            // Stops at cfg.layoutTerminator
            String fieldLayout = o.readLine();

            String[] unparsedFields = fieldLayout.split(cfg.fieldDelimiter);      
            for(String singleField : unparsedFields) {
                String name = singleField.substring(0, singleField.indexOf(cfg.typeDelimitier));
                String type = singleField.substring(singleField.indexOf(cfg.typeDelimitier) + 1);       
                
                // Is a blob (A blob has the size at the end -> use startsWith)
                if(type.startsWith(cfg.blobType)) {
                    String sizeAsString = 
                            type.substring(type.indexOf(cfg.blobSizeStart) + 1, type.indexOf(cfg.blobSizeEnd));
                    fields.add(new BlobField(name, Integer.parseInt(sizeAsString), null));
                }
                else {
                    Field f = new IntField(name, 0);
                    fields.add(f);
                }
            }
        }
        catch(Exception e)
        {
            throw e;
        }
    }
        
    private void WriteLayout(RandomAccessFile o) throws Exception {
        try {        
            // Writes the number of records
            o.writeInt(values.size());
            // Write the layout as a string
            for(Field f : fields) {                                  
                o.writeBytes(f.GetName() + cfg.typeDelimitier + f.GetType());                                   
                if(cfg.blobType.equals(f.GetType())) {
                    o.writeBytes(cfg.blobSizeStart + f.GetSize() + cfg.blobSizeEnd);
                }
                o.writeBytes(cfg.fieldDelimiter);
            }
            o.writeBytes(cfg.layoutTerminator);
        }
        catch(Exception e) {
            throw e;
        }
    }

    private void ReadData(RandomAccessFile o, int numberOfValues) throws Exception {
        for(int i = 0; i < numberOfValues; i++) {
            Record r = new Record();
            
            for(Field f : fields) {
                if(f.GetType().equals(cfg.intType)) {
                    r.AddField(f.GetName(), o.readInt());
                }
                else {
                    byte[] tmp = new byte[f.GetSize()];
                    o.read(tmp, 0, f.GetSize());
                    r.AddField(f.GetName(), f.GetSize(), tmp);
                }
            }
            
            values.add(r);
        }        
    }
    
    private void WriteData(RandomAccessFile o) throws Exception {
        try {           
            for(Record r : values) {
                // iterate over fields
                for(Field f : fields) {
                    if(f.GetType().equals(cfg.blobType)) {
                        byte[] val = r.GetBlobField(f.GetName());
                        o.write(val);
                    }
                    else {
                        int val = r.GetIntField(f.GetName());                        
                        o.writeInt(val);
                    }  
                }
                
            }
        }
        catch ( Exception e ) {
            throw e;
        }
   }
   
    public void FlushTable() throws Exception{
        try {
            tableInitalized = true; // No changes can be made
            RandomAccessFile o = new RandomAccessFile( this.filename, "rws" );
            
            WriteLayout(o);
            WriteData(o);
            
            o.close();
        }
        catch ( Exception e ) {
            throw e;
        }
    }
    
    public void PrintTableLayout() {
        System.out.println("CREATE TABLE (");
        for(Field f : fields) {
            if(f.GetType().equals(cfg.blobType)) {
                System.out.println(f.GetName() + " blob(" + f.GetSize() + "),");
            }
            else {
                System.out.println(f.GetName() + " " + f.GetType() + ",");
            }
        }
        System.out.println(")");
    }
    
    public boolean CompareSuccess(Field f, Object compareValue, String compareFunction) {
        // switch(String) only in 1.7
        if(cfg.compareEqual.equals(compareFunction)) {
            return f.compare(compareValue);
        } else if(cfg.compareNotEqual.equals(compareFunction)) {
            return !f.compare(compareValue);
        } else if(cfg.compareGreater.equals(compareFunction)) {
            return f.compareGreater(compareValue);
        } else if(cfg.compareGreaterEqual.equals(compareFunction)) {
            return f.compare(compareValue) || f.compareGreater(compareValue);
        } else if(cfg.compareLower.equals(compareFunction)) {
            return f.compareLower(compareValue);
        } else if(cfg.compareLowerEqual.equals(compareFunction)) {
            return f.compare(compareValue) || f.compareLower(compareValue);
        }
        return false;
    }
    
    public List<Record> Query(String fieldName, Object compareValue, 
            String compareFunction) {
        List<Record> results = new ArrayList<Record>();
        
        if(!cfg.IsValidCompareFunction(compareFunction))
            return results;
        
        for(Record r : values) {
            if(CompareSuccess(r.GetField(fieldName), compareValue, compareFunction)) {
                results.add(r);
            }
        }
        
        return results;
    }
    
    public List<Record> Query(String fieldName, Object expectedValue) {
        return Query(fieldName, expectedValue, cfg.compareEqual);
    }
    
    public int GetNumberOfRecords() {
        return values.size();
    }
    
    public Record GetRecord(int i) {
        if(i > values.size())
            return null;
        
        return values.get(i);
    }
}
