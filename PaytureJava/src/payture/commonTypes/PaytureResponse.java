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
import payture.paytureEnums.*;

public class PaytureResponse {
        public PaytureCommands APIName;
        public boolean Success;
        public String ErrCode;
        public String RedirectURL;
        public String SessionId;
        public HashMap<String, String> Attributes;
        public Object InternalElements;

            
        public static PaytureResponse errorResponse(Transaction transaction, String message)
        {
            PaytureResponse response = new PaytureResponse();
            response.APIName = transaction.Command;
            response.Success = false;
            response.ErrCode = message;
            return response;
        }
        
        
}
