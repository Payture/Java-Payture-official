/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.typesForEncoding;

/**
 *
 * @author Soloveva Elena
 */
public class Customer extends EncodeString {
    public String VWUserLgn;
    public String VWUserPsw;
    public String PhoneNumber;
    public String Email;
    
    public Customer( String login, String password, String phone, String email ){
        this( login, password );
        PhoneNumber = phone;
        Email = email;
    }
    
    public Customer( String login, String password ){
        VWUserLgn = login;
        VWUserPsw = password;
    }
}