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
public class PayInfo extends EncodeString {
    public String PAN;
    public Integer EMonth;
    public Integer EYear;
    public String CardHolder;
    public Integer SecureCode;
    public String OrderId;
    public long Amount;
    public PayInfo( String pan, Integer eMonth, Integer eYear, String cardHolder, Integer secureCode, String ordId, long amount )
    {
        PAN = pan;
        EMonth = eMonth;
        EYear = eYear;
        CardHolder = cardHolder;
        SecureCode = secureCode;
        OrderId = ordId;
        Amount = amount;
    }
}