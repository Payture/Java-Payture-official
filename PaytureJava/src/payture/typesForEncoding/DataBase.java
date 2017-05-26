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
public class DataBase extends EncodeString {
        public String SessionType;
        public String IP;
        public String TemplateTag;
        public String Language;
        public String OrderId;
        public long Amount;
        public String Url;
        
        public DataBase(SessionType sessionType, String orderId, long amount, String ip, String url,String template, String lang )
        {
            SessionType = ("None".equals(sessionType.toString())  ? null : sessionType.toString());
            OrderId = orderId;
            Amount = amount;
            IP = ip;
            TemplateTag = template;
            Language = lang;
            Url = url;
        }
        
        public DataBase(){}
}