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
    private String filename;
    // Used to find out the type of each field
    private List<Field> fields = new ArrayList<Field>();
    // The actual data
    private List<Record> values = new ArrayList<Record>();
    
    private boolean tableInitalized = false;
    private final String fieldDelimiter = ";",
                         otherDelimitier = ":";
    
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

    private void ReadData(RandomAccessFile o, int numberOfValues) throws Exception {
        for(int i = 0; i < numberOfValues; i++) {
            Record r = new Record();
            
            for(Field f : fields) {
                if(f.GetType().equalsIgnoreCase("int")) {
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
    
    private void ReadLayout(RandomAccessFile o) throws Exception {
        try {          
            String fieldLayout = o.readLine();

            String[] unparsedFields = fieldLayout.split(fieldDelimiter);      
            for(String singleField : unparsedFields) {
                String name = singleField.substring(0, singleField.indexOf(otherDelimitier));
                String type = singleField.substring(singleField.indexOf(otherDelimitier) + 1);       
                
                // Is a blob
                if(type.contains("(")) {
                    String sizeAsString = 
                            type.substring(type.indexOf("(") + 1, type.indexOf(")"));
                    Field f = new BlobField(name, Integer.parseInt(sizeAsString));
                    fields.add(f);
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
    
    public void AddRecord(Record r) {
        values.add(r);
    }
    
    public void AddIntField(String name) throws Exception {
        if(this.tableInitalized)
            throw new IllegalThreadStateException("Table already defined");
        
        Field f = new IntField(name, 0);
        this.fields.add(f);
    }
    
    public void AddBlobField(int size, String name) throws Exception {
        if(this.tableInitalized)
            throw new IllegalThreadStateException("Table already defined");       
        Field f = new BlobField(name, size, null);
        this.fields.add(f);
    }    
    
    private void WriteLayout(RandomAccessFile o) throws Exception {
        try {        
            // Writes the number of records
            o.writeInt(values.size());
            // Write the layout as a string
            for(Field f : fields) {                                  
                o.writeBytes(f.GetName() + ":" + f.GetType());                                   
                if(0 == f.GetType().compareToIgnoreCase("blob")) {
                    o.writeBytes("(" + f.GetSize() + ")");
                }
                o.writeBytes(";");
            }
            // Terminate layout
            o.writeBytes("\n");
        }
        catch(Exception e) {
            throw e;
        }
    }

    private void WriteRecords(RandomAccessFile o) throws Exception {
        try {           
            for(Record r : values) {
                // iterate over fields
                for(Field f : fields) {
                    if(f.GetType().equals("blob")) {
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
            RandomAccessFile o = new RandomAccessFile( this.filename, "rws" );
            tableInitalized = true; // No changes can be made
            
            WriteLayout(o);
            WriteRecords(o);
            
            o.close();
        }
        catch ( Exception e ) {
            throw e;
        }
    }
    
    public void PrintTableLayout() {
        System.out.println("CREATE TABLE (");
        for(Field f : fields) {
            if(f.GetType().equals("blob")) {
                System.out.println(f.GetName() + " blob(" + f.GetSize() + "),");
            }
            else {
                System.out.println(f.GetName() + " " + f.GetType() + ",");
            }
        }
        System.out.println(")");
    }
    
    // Todo: Allow for compare function (==, >=, <= , !=, '%a%')
    public List<Record> Query(String fieldName, Object expectedValue) {
        List<Record> results = new ArrayList<Record>();
        for(Record r : values) {
            if(r.GetField(fieldName).compare(expectedValue)) {
                results.add(r);
            }
        }
        
        return results;
    }
 
}
