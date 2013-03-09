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
public class BlobField extends Field {
    private Config cfg = Config.GetInstance();
    private byte[] byteValue;
    
    /*
     * @param value: You may pass null.
     */
    BlobField(String name, int size, byte[] value) {
        this.name = name;       
        this.size = size;
        
        byteValue = new byte[size];
        if(null != value)
        {
            // Todo: Overridable method call in constructor - problem?
            SetValue(value);
        }
    }
    
    @Override
    public void SetValue(Object newValue) {
        byte[] newByteValue = (byte[])newValue;
        /*
         * blobs have a fixed size.
         * The data may be bigger than the space so we check for that
         * arraycopy is used (instead of clone or smth) to not resize the array
         */
        int bytesToCopy = Math.min(size, newByteValue.length);
        System.arraycopy(newByteValue, 0, this.byteValue, 0, bytesToCopy);
    }
    
    @Override
    public boolean compare(Object value) {
        byte[] compareByteValue = (byte[])value;
        /* As we don't support variable sized datatypes yet
         * all blobs are padded up to this.size with 0s.
         * The comparsion value probably isn't -> Create a temp array sized 
         * this.size (prefilled with 0) and copy value into it
         */
        byte[] paddedValue = new byte[this.size];
        System.arraycopy(compareByteValue, 0, paddedValue, 0, compareByteValue.length);

        return Arrays.equals(byteValue, paddedValue);
    }
    
    @Override
    public Object GetValue() {
        return byteValue;
    }
    
    @Override
    public String GetType() {
        return cfg.blobType;
    }    
    
    @Override   
    public boolean compareGreater(Object value) {
       byte[] compareByteValue = (byte[])value;
        /* As we don't support variable sized datatypes yet
         * all blobs are padded up to this.size with 0s.
         * The comparsion value probably isn't -> Create a temp array sized 
         * this.size (prefilled with 0) and copy value into it
         */
        byte[] paddedValue = new byte[this.size];
        System.arraycopy(compareByteValue, 0, paddedValue, 0, compareByteValue.length);
        
        for(int i = 0; i < paddedValue.length; i++) {
            if(paddedValue[i] > byteValue[i])
                return true;
        }
            
        // If they are equal the requirement is not met
        return false;
    }
    
    @Override
    public boolean compareLower(Object value) {
        byte[] compareByteValue = (byte[])value;
        /* As we don't support variable sized datatypes yet
         * all blobs are padded up to this.size with 0s.
         * The comparsion value probably isn't -> Create a temp array sized 
         * this.size (prefilled with 0) and copy value into it
         */
        byte[] paddedValue = new byte[this.size];
        System.arraycopy(compareByteValue, 0, paddedValue, 0, compareByteValue.length);
        
        for(int i = 0; i < paddedValue.length; i++) {
            if(paddedValue[i] < byteValue[i])
                return true;
        }
            
        // If they are equal the requirement is not met
        return false;
    }
}
