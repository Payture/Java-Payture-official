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

import java.util.HashMap;
import java.util.Map;
import payture.paytureEnums.*;
import payture.typesForEncoding.PayInfo;

public class TransactionAPI extends Transaction {
    
    public TransactionAPI( PaytureCommands command, Merchant merchant ) { 
        super(command, merchant);
        _apiType = PaytureAPIType.api;
    }


    /** Expand transaction for API Methods: Pay/Block
     * @param info - PayInfo object
     * @param customFields - ...
     * @param customerKey - ...
     * @param paytureId - ...
     * @return current expanded transaction
    */
    public Transaction expandTransaction( PayInfo info, HashMap<String, Object> customFields, String customerKey, String paytureId ) throws IllegalArgumentException, IllegalAccessException
    {
        if ( info == null )
            return this;
        _requestKeyValuePair.put( PaytureParams.PayInfo, info.getPropertiesString() );
        _requestKeyValuePair.put( PaytureParams.OrderId, info.OrderId );
        _requestKeyValuePair.put( PaytureParams.Amount, info.Amount );
        if( customFields != null && !customFields.isEmpty() )
        {
            String aggregateStr = "";
            for(Map.Entry<String, Object> entry: customFields.entrySet())
            {
                aggregateStr += String.format("%s=%s;", entry.getKey(), entry.getValue());
            }
            _requestKeyValuePair.put( PaytureParams.CustomFields, aggregateStr );
        }
        if ( customerKey != null && !customerKey.isEmpty())
            _requestKeyValuePair.put( PaytureParams.CustomerKey, customerKey );
        if ( paytureId != null && !paytureId.isEmpty() )
            _requestKeyValuePair.put( PaytureParams.PaytureId, paytureId );
        expandTransaction( true,false );
        _expanded = true;
        return this;
    }
}

