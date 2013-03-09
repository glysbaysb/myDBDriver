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
    //Singleton pattern
    private Config() {   }  
    private static Config instance = new Config();
    public static Config GetInstance() {
        return instance;
    }  
}
