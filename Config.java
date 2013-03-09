/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myDBDriver;

/**
 *
 * @author 5.7.2012
 */
// Todo: Better name
public class Config {
    public final String fieldDelimiter = ";",
                        typeDelimitier = ":",
                        // Do not change: The layout is assumed is end at linebreak
                        layoutTerminator = "\n"; 
    
    public final String blobType = "blob", 
                         intType  = "int";
    
    public final String blobSizeStart = "(",
                        blobSizeEnd = ")";
    
    public final String compareEqual = "==",
                        compareNotEqual = "!=",
                        compareLower = "<",
                        compareGreater = ">",
                        compareLowerEqual = "<=",
                        compareGreaterEqual = ">=";
    
    public boolean IsValidCompareFunction(String c) {
        return c.equals(compareEqual) || c.equals(compareNotEqual) || 
                c.equals(compareLower) || c.equals(compareLowerEqual) || 
                c.equals(compareGreater) || c.equals(compareGreaterEqual);
    }
    
    //Singleton pattern
    private Config() {   }  
    private static Config instance = new Config();
    public static Config GetInstance() {
        return instance;
    }  
}
