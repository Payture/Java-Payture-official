/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.commonTypes;

import payture.paytureEnums.PaytureAPIType;
import payture.paytureEnums.PaytureCommands;
import payture.paytureEnums.PaytureParams;

/**
 *
 * @author Soloveva Elena
 */
public class TransactionDigitalWallet extends Transaction{
    protected PaytureCommands _specialCommand;
    
    public TransactionDigitalWallet( PaytureCommands command, Merchant merchant, PaytureCommands spetialCommand ) {
        super(command, merchant);
        _specialCommand = spetialCommand;
        _apiType = PaytureAPIType.api;
    }
    
     /** Expand transaction for ApplePay and AndroidPay Methods: Pay/Block
     * @param payToken - PaymentData from PayToken for current transaction
     * @param orderId - current transaction OrderId
     * @param amount - current transaction amount in kopec - pass null for Apple Pay
     * @return current expanded transaction
    */
    public Transaction expandTransaction(String payToken, String orderId, Integer amount){
        _requestKeyValuePair.put(PaytureParams.OrderId, orderId);
        _requestKeyValuePair.put(PaytureParams.PayToken, payToken);
        _requestKeyValuePair.put(PaytureParams.Method, ( Command == PaytureCommands.Pay ) ? "PAY" : "BLOCK" );
        expandTransaction(true, false);
        Command =  _specialCommand;
        if(_specialCommand == PaytureCommands.AndroidPay) {
            _requestKeyValuePair.put(PaytureParams.Amount, amount.toString());
        }
        _expanded = true;
        return this;
    }
}
