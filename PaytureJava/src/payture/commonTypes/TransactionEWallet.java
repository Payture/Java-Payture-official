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
            _requestKeyValuePair.put( PaytureParams.DATA, customer.getPropertiesString() + card.getPropertiesString() );
            
            _expanded = true;
            return this;
        }

        
        /** Expand transaction for EWallet Methods: Register/Update/Delete/Check/Getlist 
         * @param customer - Customer object
         * @return current expanded transaction
        */
        public Transaction expandTransaction( Customer customer ) throws IllegalArgumentException, IllegalAccessException
        {
            if ( _expanded )
                return this;
            if ( Command == PaytureCommands.Delete )
                _requestKeyValuePair.put( PaytureParams.DATA, String.format("%s=%s;%s=%s;", PaytureParams.VWUserLgn,  customer.VWUserLgn, PaytureParams.Password,_merchant.getPassword()) );
            else
                _requestKeyValuePair.put( PaytureParams.DATA, customer.getPropertiesString() );
            expandTransaction(true, false);
            _expanded = true;
            return this;
        }

        /** Expand transaction for EWallet Methods: Init/Pay (Merchant side reg/noreg card) 
         * @param customer - Customer object
         * @param card - Card object
         * @param data - DATA object
         * @param regCard - pass false if use not registered card in carrent transaction
         * @return current expanded transaction
        */
        public Transaction expandTransaction( Customer customer, Card card, DATA data, boolean regCard ) throws IllegalArgumentException, IllegalAccessException 
        {
            if ( customer == null || card == null || data == null )
                return this;
           // _sessionType = 
            Customer newCustom = new Customer( customer.VWUserLgn, customer.VWUserPsw, null, null );
            DATA newData = new DATA();
            newData.SessionType = data.SessionType;
            newData.OrderId = data.OrderId;
            newData.Amount = data.Amount;
            newData.IP = data.IP;
            newData.ConfirmCode = data.ConfirmCode;
            Card newCard = new Card();
            if ( regCard )
            {
                newCard = new Card();
                newCard.SecureCode = card.SecureCode;
                newCard.CardId = card.CardId;
            }
            else
            {
                card.CardId = "FreePay";
            }
            if(Command == PaytureCommands.Init)
            {
                newCustom.PhoneNumber = customer.PhoneNumber;
                newCustom.Email = customer.Email;
                newData = new DATA();
                newData.SessionType = data.SessionType;
                newData.IP = data.IP;
                newData.TemplateTag = data.TemplateTag;
                newData.Language = data.Language;
                newData.Total = data.Total;    
                newData.Product = data.Product;
                newData.Amount = data.Amount;
                if( data.SessionType == null ? SessionType.Add.toString() != null : !data.SessionType.equals(SessionType.Add.toString()) )
                {
                    newCard = new Card();
                    newCard.CardId = card.CardId;
                    newData.OrderId = data.OrderId;
                }

            }
            String str = newCustom.getPropertiesString() + card.getPropertiesString() + newData.getPropertiesString() + data.CustomFields;
            _requestKeyValuePair.put( PaytureParams.DATA, str );

            expandTransaction(true, false);
            _expanded = true;
            return this;
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

            _requestKeyValuePair.put( PaytureParams.DATA, customer.getPropertiesString() + String.format("%s=%s;", PaytureParams.CardId, cardId ) 
                + ( amount != null && Command == PaytureCommands.Activate ? String.format("%s=%s;", PaytureParams.Amount, amount)  : "" )
                    + (orderId == null ? "" : String.format("%s=%s;", PaytureParams.OrderId, orderId)));

            expandTransaction(true, false);
            _expanded = true;
            return this;
        }

        /** Expand transaction for InPay EWallet: Pay/Add (on Payture side)
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