/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.commonTypes;

/**
 *
 * @author Soloveva Elena
 */
public class CardInfo {
    public String CardNumber;
    public String CardId;
    public String CardHolder;
    public String ActiveStatus;
    public Boolean Expired;
    public Boolean NoCVV;
    
    public CardInfo(String cardNumber, String cardId, String cardHolder, String activeStatus, Boolean expired, Boolean noCVV ){
        CardNumber = cardNumber;
        CardId = cardId;
        CardHolder = cardHolder;
        ActiveStatus = activeStatus;
        Expired = expired;
        NoCVV = noCVV;
    }
}
