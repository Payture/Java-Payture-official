/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.typesForEncoding;

import payture.paytureEnums.SessionType;

/**
 *
 * @author Soloveva Elena
 */
public class DATA extends DataBase {
        public String ConfirmCode;
        public String CustomFields;
        
        public DATA( SessionType sessionType, String orderId, long amount, String ip, String product, Integer total, String confirmCode,  String[] customFields, String template, String lang ) {
            super( sessionType, orderId, amount, ip, product, total, null,template, lang );
            ConfirmCode = confirmCode;
            int i = 1;
            String resultStr = "";
            for(int j = 0; j < customFields.length; j++)
            {
                resultStr += String.format("CustomField%s=%s;", j, customFields[j]);
            }
            CustomFields = ( customFields == null ? null : resultStr );
        }
        public DATA(){}
}
