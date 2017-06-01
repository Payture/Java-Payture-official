/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.typesForEncoding;

import java.lang.reflect.Field;

/**
 *
 * @author Soloveva Elena
 */
public class EncodeString {
    
    /** Method created string representation of all fields in object that has some value via concatinating Field=Value pairs with ';' as delimiter.
    * @return  String. Sample Field1=Value1;Field2=Value2;Field3=Value3;
    */   
    public String getPropertiesString() throws IllegalArgumentException, IllegalAccessException        
    {           
        Field[] fields = this.getClass().getFields();            
        String result = "";            
        for (Field field : fields) {                
            Object val = field.get(this);                
            if (val != null) {                    
                result += String.format("%s=%s;", field.getName(), val);                
            }            
        }            
        return result;        
    }
}
