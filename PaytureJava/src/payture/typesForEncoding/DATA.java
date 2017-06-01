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
public class Data extends EncodeString {
        public String SessionType;
        public String IP;
        public String TemplateTag;
        public String Language;
        public String OrderId;
        public long Amount;
        public String Url;
        public String Product;
        public Integer Total;
        public String ConfirmCode;
        public String CustomFields;
        
        public Data(SessionType sessionType, String orderId, long amount, String ip, String product, Integer total, String url,String template, String lang ){
            this( sessionType, orderId, amount, ip );
            TemplateTag = template;
            Language = lang;
            Url = url;
            Product = product;
            Total = total;
        }
        
        
        public Data( SessionType sessionType, String orderId, long amount, String ip, String product, Integer total, String confirmCode,  String[] customFields, String template, String lang ) {
            this( sessionType, orderId, amount, ip, product, total, null,template, lang );
            ConfirmCode = confirmCode;
            int i = 1;
            String resultStr = "";
            for(int j = 0; j < customFields.length; j++)
            {
                resultStr += String.format("CustomField%s=%s;", j, customFields[j]);
            }
            CustomFields = ( customFields == null ? null : resultStr );
        }
        
        public Data( SessionType sessionType, String orderId, long amount, String ip ) {
            SessionType = ( "None".equals(sessionType.toString())  ? null : sessionType.toString() );
            OrderId = orderId;
            Amount = amount;
            IP = ip;
        }
        
        public Data( SessionType sessionType, String ip, String templateTag, String language ){
            SessionType = ("None".equals(sessionType.toString())  ? null : sessionType.toString());
            TemplateTag = templateTag;
            Language = language;
            IP = ip;
        }
        
        public Data() {}
}