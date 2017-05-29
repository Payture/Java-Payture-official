/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.commonTypes;

import payture.paytureEnums.*;
import payture.typesForEncoding.Data;

/**
 *
 * @author Soloveva Elena
 */

public class TransactionInPay extends Transaction {
    public TransactionInPay( PaytureCommands command, Merchant merchant ) {
        super ( command, merchant );
        _apiType = PaytureAPIType.apim;
    }

    /** Expand transaction for InPay Methods: Init
     * @param data - ....
     * @return current expanded transaction
    */
    public Transaction expandTransaction( Data data ) throws IllegalArgumentException, IllegalAccessException
    {
        if ( data == null )
            return this;
        _sessionType = SessionType.Pay;
        _requestKeyValuePair.put( PaytureParams.Data, data.getPropertiesString() );
        expandTransaction(true, false);
        _expanded = true;
        return this;
    }


    /** Expand transaction for InPay Methods: Pay
     * @param sessionId - value return in the Init method
     * @return current expanded transaction
    */
    public Transaction expandTransaction( String sessionId )
    {
        if ( sessionId == null || sessionId.isEmpty() )
            return this;
        _requestKeyValuePair.put( PaytureParams.SessionId, sessionId );
        _expanded = true;
        return this;
    }
}
