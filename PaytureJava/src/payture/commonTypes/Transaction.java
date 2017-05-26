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
import java.awt.Desktop;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.lang.Object;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import payture.paytureEnums.*;




 public class Transaction extends RequestClient {
     
     
     ResponseHandler<PaytureResponse> rh = new ResponseHandler<PaytureResponse>() {

    @Override
    public PaytureResponse handleResponse(
            final HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        
        
        SAXBuilder saxBuilder = new SAXBuilder();
        String xml = contents;
        try {
            Document doc = saxBuilder.build(new StringReader(xml));
            String success = doc.getRootElement().getAttributeValue("Success");
            String error = doc.getRootElement().getAttributeValue("ErrCode");
            String sess  = doc.getRootElement().getAttributeValue("SessionId");
            String nameApi = doc.getRootElement().getName();
            
            PaytureResponse r = new PaytureResponse();
            r.APIName = PaytureCommands.valueOf(nameApi);
            r.Success = Boolean.parseBoolean(success);
            r.ErrCode = error;
            if(sess != null && !sess.isEmpty())
            {
                r.SessionId = sess;
                r.RedirectURL = String.format("%s/%s/%s?SessionId=%s", _merchant.getHOST(), _apiType, _sessionType == SessionType.Add ? PaytureCommands.Add : PaytureCommands.Pay, sess);
            }
            System.out.println(r);
            return r;
        } catch (JDOMException e) {
            System.out.print(e);
        } catch (IOException e) {
            System.out.print(e);
        }

        
        
        ///
       return null;
    }
};

        protected HashMap<PaytureParams, Object> _requestKeyValuePair = new HashMap<>();
        protected PaytureAPIType _apiType = PaytureAPIType.api;
        protected SessionType _sessionType;
        protected Merchant _merchant;
        protected boolean _expanded = false;
        public PaytureCommands Command;
        public SessionType  getSessionType()
        { 
            return _sessionType; 
        }
        public HashMap<PaytureParams, Object> getRequestParams()
        {  
            return _requestKeyValuePair;
        }

        public Transaction( PaytureCommands command, Merchant merchnat )
        {
            this._sessionType = SessionType.None;
            Command = command;
            _merchant = merchnat;
        }

        /** Expand transaction for API Methods: Charge/UnBlock/Refund/GetState  
         * for Ewallet Methods: Charge/UnBlock/Refund/PayStatus  
         * for Inpay Methods: Charge/UnBlock/Refund/PayStatus
         * @param orderId - current transaction OrderId value
         * @param amount - pass null for GetState and PayStatus methods, in over cases pass current transaction amount in kopec
         * @return current expanded transaction
        */
        public Transaction expandTransaction( String orderId, Integer amount )
        {
            if ( _expanded )
            {
                return this;
            }
            if ( orderId == null || orderId.isEmpty())
            {
                return this;
            }
            if (  _apiType == PaytureAPIType.vwapi )
            {
                if ( Command == PaytureCommands.PayStatus )
                {
                    _requestKeyValuePair.put( PaytureParams.DATA, PaytureParams.OrderId + "=" + orderId );
                }
                else if ( Command == PaytureCommands.Refund && amount != null )
                {
                _requestKeyValuePair.put( PaytureParams.DATA, PaytureParams.OrderId + "=" + orderId +";"+ PaytureParams.Amount +"=" + amount + ";" + PaytureParams.Password + "=" + _merchant.getPassword() );
                }   
                else
                {
                    _requestKeyValuePair.put( PaytureParams.OrderId, orderId );
                }
                if ( amount != null )
                {
                    _requestKeyValuePair.put( PaytureParams.Amount, amount );
                }
            }
            else
            {
                _requestKeyValuePair.put( PaytureParams.OrderId, orderId );
                if ( amount != null )
                {
                    _requestKeyValuePair.put( PaytureParams.Amount, amount);
                }
            }
            if ( Command == PaytureCommands.Refund || ( _apiType != PaytureAPIType.api && ( Command == PaytureCommands.Charge || Command == PaytureCommands.Unblock ) ) )
                expandTransaction( true, true );
            else
                expandTransaction( true, false );

            _expanded = true;
            return this;
        }
        
        /** Expand transaction with Merchant key and password
         * @param addKey - pass true for adding Merchant account Key
         * @param addPass - pass true for adding Merchant account password
         * @return current expanded transaction
        */
        protected Transaction expandTransaction( boolean addKey, boolean addPass )
        {
            if ( addKey )
            {
                _requestKeyValuePair.put( _apiType == PaytureAPIType.vwapi ? PaytureParams.VWID : PaytureParams.Key, _merchant.getMerchantName() );
            }
            if ( addPass )
            {
                _requestKeyValuePair.put( PaytureParams.Password, _merchant.getPassword() );
            }
            return this;
        }

        
        /** Form content for request
         * @return UrlEncodedFormEntity for request 
        */
        private UrlEncodedFormEntity formContent()
        {
            ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
            for (Map.Entry<PaytureParams, Object> entry : _requestKeyValuePair.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
            }
            UrlEncodedFormEntity entity;
            try {
                entity = new UrlEncodedFormEntity(parameters);
                return entity;
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } 
        /*
        /// <summary>
        /// Process request to Payture server synchronously
        /// </summary>
        /// <returns>PaytureResponse - response from the Payture server.</returns>

        public  FuturePaytureResponse> processOperationAsync()
        {
            if ( !_expanded )
                return PaytureResponse.errorResponse( this, "Params are not set" );
        this._sessionType = SessionType.None;
            if ( Command == PaytureCommands.Init )
                return  post( getPath(), frmContent() ).ContinueWith( r => ParseResponseInternal( r, Command, SessionType ) ).ContinueWith( r => FormRedirectURL( r ) );
            return  post( getPath(), formContent() ).ContinueWith( r => ParseResponseInternal( r, Command, SessionType ) );
        }*/
        
        
        /** Process request to Payture server synchronously
         * @return PaytureResponse for request - response from the Payture server. In case of exeption will be return PaytureResponse with exeption mesage in ErrCode field
        */
        public PaytureResponse processOperation()
        {
            if ( !_expanded )
            {
                return PaytureResponse.errorResponse( this, "Params is not setted" );
            }
            try {
                HttpResponse operationResult = post( getPath(), formContent() );
                PaytureResponse res =  rh.handleResponse(operationResult);
                
                return res;
            }
            catch( Exception ex )
            {
                String exMessage = String.format("Error occurs\nMessage:[%s]\nStackTrace: %s", ex.getMessage(), ex.getStackTrace());  //doubt point
                return PaytureResponse.errorResponse( this, exMessage );
            }
            
        }
        
        /** orm url for request
         * @return url string
        */
        protected String getPath()
        {
            return String.format( "%s/%s/%s", _merchant.getHOST(), _apiType, Command );
        }
    }
