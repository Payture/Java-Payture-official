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
public class Card extends EncodeString {
    public String CardId;
    public String CardNumber;
    public Integer EMonth;
    public Integer EYear;
    public String CardHolder;
    public Integer SecureCode;

    public Card( String cardNum, Integer eMonth, Integer eYear, String cardHolder, Integer secureCode, String cardId )
    {
        CardNumber = cardNum;
        EMonth = eMonth;
        EYear = eYear;
        CardHolder = cardHolder;
        SecureCode = secureCode;
        CardId = cardId;
    }

    public Card() { }
}