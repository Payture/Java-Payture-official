/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.commonTypes;

import payture.paytureEnums.*;
import payture.typesForEncoding.*;

/**
 *
 * @author Soloveva Elena
 */

public class TransactionEWallet extends Transaction {
         
        public TransactionEWallet( PaytureCommands command, Merchant merchant ) {
            super(command, merchant);
            _apiType = PaytureAPIType.vwapi;
        }

        /** Expand transaction for EWallet Methods: Add (on Merchant side)
         * @param customer - Customer object
         * @param card - Card object
         * @return current expanded transaction
        */
        public Transaction expandTransaction( Customer customer, Card card ) throws IllegalArgumentException, IllegalAccessException
        {
            if ( customer == null || card == null )
                return this;
            String str =  customer.getPropertiesString() + card.getPropertiesString();
            return expandInternal( PaytureParams.DATA, str );
        }

        
        /** Expand transaction for EWallet Methods: Register/Update/Delete/Check/Getlist 
         * @param customer - Customer object
         * @return current expanded transaction
        */
        public Transaction expandTransaction( Customer customer ) throws IllegalArgumentException, IllegalAccessException
        {
            if ( _expanded )
                return this;
            String str;
            if ( Command == PaytureCommands.Delete )
                str = String.format("%s=%s;%s=%s;", PaytureParams.VWUserLgn,  customer.VWUserLgn, PaytureParams.Password,_merchant.getPassword());
            else
                str = customer.getPropertiesString();
            return expandInternal( PaytureParams.DATA, str );
        }

        /** Expand transaction for EWallet Methods: Pay (Merchant side for NOT REGISTERED card)
         * @param customer - Customer object
         * @param card - Card object. Specify in it all fields exclude CardId.
         * @param data - Data object. SessionType and IP fields are required. Optional ConfimCode and CustomFields.
         * @return current expanded transaction
        */
        public Transaction expandTransaction( Customer customer, Card card, Data data ) throws IllegalArgumentException, IllegalAccessException
        {
            if ( customer == null || card == null || data == null )
                return this;
            _sessionType = SessionType.valueOf( data.SessionType );
            card.CardId = "FreePay";
            String str = customer.getPropertiesString() + card.getPropertiesString() + data.getPropertiesString() + data.CustomFields;
            return expandInternal( PaytureParams.DATA, str );
        }


        /** Expand transaction for EWallet Methods: Pay (Merchant side for REGISTERED card) 
         * @param customer - Customer object
         * @param cardId - CardId identifier in Payture system.
         * @param secureCode CVC2/CVV2.
         * @param data - Data object. SessionType and IP fields are required. Optional ConfimCode and CustomFields.
         * @return current expanded transaction
        */
        public Transaction ExpandTransaction( Customer customer, String cardId, int secureCode, Data data ) throws IllegalArgumentException, IllegalAccessException
        {
            if ( customer == null || cardId == null || cardId.isEmpty() || data == null )
                return this;
            _sessionType = SessionType.valueOf( data.SessionType );
            String str = customer.getPropertiesString() + String.format("%s=%s;", PaytureParams.CardId, cardId ) + String.format( "%s=%s;", PaytureParams.SecureCode, secureCode ) +  data.getPropertiesString()  + data.CustomFields;
            return expandInternal( PaytureParams.DATA, str );
        }
        
        /** Expand transaction for EWallet Methods: Init
         * @param customer - Customer object
         * @param cardId - CardId identifier in Payture system.
         * @param data - Data object. SessionType and IP fields are required. Optional TamplateTag and Language.
         * @return current expanded transaction
        */
        public Transaction ExpandTransaction( Customer customer, String cardId, Data data ) throws IllegalArgumentException, IllegalAccessException
        {
            if ( customer == null || data == null )
                return this;
            _sessionType = SessionType.valueOf( data.SessionType );
            String str = customer.getPropertiesString() + ( cardId == null ? "" : String.format("%s=%s;", PaytureParams.CardId, cardId )) + data.getPropertiesString() + data.CustomFields;
            return expandInternal( PaytureParams.DATA, str );
        }        
        
        /** Expand transaction for EWallet Methods: SendCode/Activate/Remove
         * @param customer - Customer object
         * @param cardId - pass CardId field from Card object
         * @param amount - pass null for Remove method, otherwise pass amount for current transaction in kopec
         * @param orderId - pass null for Remove method, in over cases pass current transaction OrderId
         * @return current expanded transaction
        */
        public Transaction expandTransaction( Customer customer, String cardId, Integer amount, String orderId ) throws IllegalArgumentException, IllegalAccessException
        {
            if ( customer == null || cardId == null || cardId.isEmpty() )
                return this;
            
            String str = customer.getPropertiesString() + String.format("%s=%s;", PaytureParams.CardId, cardId ) 
                + ( amount != null && Command == PaytureCommands.Activate ? String.format("%s=%s;", PaytureParams.Amount, amount)  : "" )
                + (orderId == null ? "" : String.format("%s=%s;", PaytureParams.OrderId, orderId));
            return expandInternal( PaytureParams.DATA, str );
        }

        /** Expand transaction for InPay EWallet: Pay/Add (on Payture side)
         * @param sessionId - Payment's identifier from Init response.
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
        
            private Transaction expandInternal( PaytureParams field, String data )
        {
            _requestKeyValuePair.put( field, data );
            expandTransaction( true, false );
            _expanded = true;
            return this;
        }
}