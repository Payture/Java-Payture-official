/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payturetestapp;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import payture.commonTypes.*;
import payture.paytureEnums.*;
import payture.typesForEncoding.*;

/**
 *
 * @author Soloveva Elena
 */
public class PaytureTestApp {

    /**
     * @param args the command line arguments
     */
    
    static Random Random = new Random();

    static String _host = " https://sandbox.payture.com";
    static String _merchantKey = "accountName";
    static String _merchantPassword = "accountPassword";
    static Merchant _merchant = new Merchant( _merchantKey, _merchantPassword, _host );
    static PaytureResponse response = null;
    static HashMap<PaytureParams, String> allFields = new HashMap<PaytureParams, String>();
    static Scanner input = new Scanner(System.in);
    
    public static void main(String[] args) {
        try {
            allFields.put(PaytureParams.VWUserLgn, "testCustomer@test.com");
            allFields.put(PaytureParams.VWUserPsw, "pass123");
            allFields.put(PaytureParams.CardId, "");
            allFields.put(PaytureParams.IP, "127.0.0.1");
            allFields.put(PaytureParams.SessionId, "");
            allFields.put(PaytureParams.EMonth, "10");
            allFields.put(PaytureParams.EYear, "21");
            allFields.put(PaytureParams.CardHolder, "Test Customer");
            allFields.put(PaytureParams.SecureCode, "123");
            allFields.put(PaytureParams.PhoneNumber, "7845693211");
            allFields.put(PaytureParams.Email, "testCustomer@test.com");
            allFields.put(PaytureParams.OrderId, "");
            allFields.put(PaytureParams.SessionType, SessionType.None.toString());
            allFields.put(PaytureParams.PAN, "4111111111111112");
            allFields.put(PaytureParams.CustomerKey, "testCustomer");
            allFields.put(PaytureParams.PaytureId, "");
            allFields.put(PaytureParams.CustomFields, "");
            allFields.put(PaytureParams.Description, "");
            allFields.put(PaytureParams.PaRes, "");
            allFields.put(PaytureParams.Amount, "100");
            allFields.put(PaytureParams.MD, "");
            allFields.put(PaytureParams.PayToken, "");
            allFields.put(PaytureParams.Method, "");
            allFields.put(PaytureParams.Language, "");
            allFields.put(PaytureParams.TemplateTag, "");
            allFields.put(PaytureParams.Url, ""); 
            allFields.put(PaytureParams.Total, "1");
            allFields.put(PaytureParams.Product, "Something"); 

            System.out.println("Type 'help' for get description of commands for this console program.");
            String help = input.nextLine();
            if(help.equalsIgnoreCase("help"))
            {
                help();
                System.out.println( "Type 'commands' for get command's list." );
                String commands = input.nextLine();
                if ( commands.equalsIgnoreCase("commands") )
                    listCommands();
                
                System.out.println("Press enter for continue.");
                input.nextLine();
            }
            
            System.out.println( String.format("Merchant account settings:\n\tMerchantName=%s\n\tMerchantPassword=%s\n\tHOST=%s\n", _merchantKey, _merchantPassword, _host) );
            System.out.println("Type space for change Merchant account settings" );
            char chInpt = (char) System.in.read();

            if ( chInpt == 32 )
            {
                input.nextLine();
                changeMerchant();
            }

            while(true)
            {
                System.out.println("Type 'end' for exit" );
                String end = input.nextLine();
                if ( end.equalsIgnoreCase("end") )
                    break;
                Router( );
            }

            input.nextLine();
        } catch( Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static void WriteResult(PaytureResponse response)
    {
       String print = String.format("\nResponse Result\n%s Success=%s; ErrCode=%s; Attribute=%s", response.APIName, response.Success, response.ErrCode, response.Attributes);
       System.out.println(print);
    }
        
        
        
        
    //Router
        
    public static void Router ( ) throws IllegalArgumentException,  IOException
    {
        try{
        System.out.println( "Type the command:" );
        String cmd = input.nextLine().toUpperCase();
        PaytureAPIType apiType = PaytureAPIType.api;//api type
        
        switch ( cmd ){
            case "PAY":{
                apiType = promtService( PaytureCommands.Pay );
                if ( apiType == PaytureAPIType.api ){
                    payOrBlockAPI( PaytureCommands.Pay );
                    break;
                }
                if( promtForUseSessionId( PaytureCommands.Pay ) ) {
                    payturePayOrAdd( PaytureCommands.Pay );
                    break;
                }
                
                if( apiType == PaytureAPIType.apim ){
                    System.out.println("PaytureCommands.Pay for PaytureInPay can be process only with SessionId payment's identifier after PaytureCommands.Init api method.\nCall Init and set SessionId parameter.");
                    break;
                }
                    
                //Only EWallet here
                Customer customer = getCustomer();
                Data data = dataForInit( promtSessionType( PaytureCommands.Pay, apiType ) );

                boolean regCard = promtForUseRegCard();
                if( !regCard ){
                    //not registered card 
                    Card card = getCard();
                    response = _merchant.EWallet( PaytureCommands.Pay ).expandTransaction(customer, card, data).processOperation();
                    break;
                }
                
                String cardId = allFields.get( PaytureParams.CardId );
                String secureCode = allFields.get( PaytureParams.SecureCode );
                System.out.println(String.format("CardId=%s; SecureCode=%s;", cardId, secureCode) );
                circleChanges( "CardId and SecureCode" );

                
                response = _merchant.EWallet( PaytureCommands.Pay ).expandTransaction(customer, data, allFields.get(PaytureParams.CardId),
                        Integer.parseInt(allFields.get(PaytureParams.SecureCode))).processOperation();

                break;
            }
            case "BLOCK":{
                payOrBlockAPI( PaytureCommands.Block );
                break;
            }
            case "CHARGE":{
                chargeUnblockRefundGetState( PaytureCommands.Charge );
                break;
            }
            case "REFUND":{
                chargeUnblockRefundGetState( PaytureCommands.Refund );
                break;
            }
            case "UNBLOCK":{
                chargeUnblockRefundGetState( PaytureCommands.Unblock );
                break;
            }
            case "GETSTATE":{
                chargeUnblockRefundGetState( PaytureCommands.GetState );
                break;
            }
            case "PAYSTATUS":{
                chargeUnblockRefundGetState( PaytureCommands.PayStatus );
                break;
            }
            case "INIT":{
                apiType = promtService( PaytureCommands.Init );
                SessionType sessionType = promtSessionType( PaytureCommands.Init, apiType );
                Data data = dataForInit( sessionType );
                
                if ( apiType == PaytureAPIType.vwapi ){
                    Customer customer = getCustomer();
                    String cardId = allFields.get( PaytureParams.CardId );
                    
                    response = _merchant.EWallet( PaytureCommands.Init ).expandTransaction(customer, cardId, data).processOperation();
                }
                else
                    response = _merchant.InPay( PaytureCommands.Init ).expandTransaction( data ).processOperation();
                
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URL(response.RedirectURL).toURI());
                break;
            }
            case "ACTIVATE":{
                customerAndCardAPI( PaytureCommands.Activate );
                break;
            }
            case "REMOVE":{
                customerAndCardAPI(  PaytureCommands.Remove );
                break;
            }
            case "GETLIST":{
                customerAndCardAPI( PaytureCommands.GetList );
                break;
            }
            case "REGISTER":{
                customerAndCardAPI( PaytureCommands.Register );
                break;
            }
            case "UPDATE":{
                customerAndCardAPI( PaytureCommands.Update );
                break;
            }
            case "DELETE":{
                customerAndCardAPI( PaytureCommands.Delete );
                break;
            }
            case "CHECK":{
                customerAndCardAPI( PaytureCommands.Check );
                break;
            }
            case "ADD": {
                if ( promtForUseSessionId( PaytureCommands.Add ) ){
                    payturePayOrAdd( PaytureCommands.Add );
                    break;
                }

                //EWallet add card on Merchant side
                Customer customer = getCustomer();
                Card card = getCard();
                response = _merchant.EWallet( PaytureCommands.Add ).expandTransaction( customer, card ).processOperation();    
                break;
            }
            case "FIELDS":{
                System.out.println();
                for(Entry<PaytureParams, String> pair : allFields.entrySet()){
                    System.out.println(pair.getKey() + " = " + pair.getValue());
                }
                System.out.println();
                break;
            }
            case "CHANGEFIELDS":{
                circleChanges(null);
                break;
            }
            case "COMMANDS":{
                listCommands();
                break;
            }
            case "CHANGEMERCHANT":{
                changeMerchant();
                break;
            }
            case "HELP":{
                help();
            }
        }
        if ( "FIELDS CHANGEFIELDS COMMANDS CHANGEMERCHANT HELP".indexOf(cmd ) == -1 ){
            if( response != null )
                WriteResult( response );
        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void generateOrderId()
    {
        allFields.replace(PaytureParams.OrderId, String.format("ORD_%s_TEST", Random.nextInt(1000000000)));
    }

    static void generateAmount()
    {
        allFields.replace(PaytureParams.Amount, String.format("%s", Random.nextInt(10000)));
    }

    static Card getCard() throws IllegalArgumentException, IllegalAccessException
    {
        Card card = cardFromCurrentSettings();
        String propsDataDefault = String.format("Card params:\n %s", getPropertiesString(card)); 
        System.out.println( "Default settings for Card:" );
        System.out.println( propsDataDefault + "\n" );
        circleChanges(null);
        return cardFromCurrentSettings();
    }
    static void payturePayOrAdd( PaytureCommands command ) 
    {
        String sessionId = allFields.get(PaytureParams.SessionId);
        System.out.println( String.format("SessionId: %s", sessionId));
        circleChanges( "SessionId" );
        response = _merchant.EWallet( command ).expandTransaction( allFields.get(PaytureParams.SessionId), null ).processOperation();
    }

    static Card cardFromCurrentSettings()
    {
        Card card =  new Card( allFields.get( PaytureParams.PAN ), Integer.getInteger(allFields.get( PaytureParams.EMonth )), Integer.getInteger(allFields.get(PaytureParams.EYear )), allFields.get( PaytureParams.CardHolder ), Integer.getInteger(allFields.get( PaytureParams.SecureCode ) ), null);
        return card;
    }

    static PayInfo payInfoFromCurrentSettings()
    {
        String pan = allFields.get( PaytureParams.PAN );
        Integer em = Integer.parseInt( allFields.get( PaytureParams.EMonth ) );
        Integer ye = Integer.parseInt( allFields.get( PaytureParams.EYear ));
        String holder = allFields.get( PaytureParams.CardHolder );
        Integer secCode = Integer.parseInt( allFields.get( PaytureParams.SecureCode ) );
        String orId = allFields.get( PaytureParams.OrderId );
        Integer amount = Integer.parseInt( allFields.get( PaytureParams.Amount ) );
        PayInfo info = new PayInfo( pan, em, ye, holder,  secCode, orId,  amount );
        return info;
    }

    static Customer customerFromCurrentSettings()
    {
        return new Customer( allFields.get( PaytureParams.VWUserLgn ), allFields.get( PaytureParams.VWUserPsw ), allFields.get( PaytureParams.PhoneNumber ), allFields.get( PaytureParams.Email ) );
    }

    static Data dataFromCurrentSettings()
    {
        Data data = new Data(SessionType.valueOf( allFields.get( PaytureParams.SessionType )),
        allFields.get( PaytureParams.OrderId ),
        allFields.get( PaytureParams.Amount ).isEmpty() ? 0 : Integer.parseInt(allFields.get( PaytureParams.Amount ) ),
        allFields.get( PaytureParams.IP ),
        allFields.get( PaytureParams.Product ),
        allFields.get( PaytureParams.Amount ).isEmpty() ? 0 : Integer.parseInt(allFields.get( PaytureParams.Amount ) ),
        allFields.get( PaytureParams.Url ),
        allFields.get( PaytureParams.TemplateTag ),
        allFields.get( PaytureParams.Language ));
        return  data;
    }

    static PayInfo getPayInfo() throws  IllegalArgumentException, IllegalAccessException
    {
        generateAmount();
        generateOrderId();
        PayInfo payInfo = payInfoFromCurrentSettings();
        String propsPayInfo = String.format("PayInfo params:\n%s", getPropertiesString(payInfo)); 
        System.out.println( "Default settings PayInfo:" );
        System.out.println( propsPayInfo + "\n" );
        circleChanges("PayInfo data");
        return payInfoFromCurrentSettings();
    }


    static void customerAndCardAPI( PaytureCommands command) throws IllegalArgumentException, IllegalAccessException
    {
        Customer customer = getCustomer();
        if ( command == PaytureCommands.Activate || command == PaytureCommands.Remove )
        {
            String cardId = allFields.get( PaytureParams.CardId );
            System.out.println( "CardId: " + cardId );
            circleChanges( "CardId" );
            Integer amountForActivate = command == PaytureCommands.Activate ? 101 : null;
            response = _merchant.EWallet( command ).expandTransaction( customer, allFields.get(PaytureParams.CardId), amountForActivate, null).processOperation();
        }
        response = _merchant.EWallet( command ).expandTransaction( customer ).processOperation();
    }
    static Customer getCustomer() throws IllegalArgumentException, IllegalAccessException
    {
        Customer customer = customerFromCurrentSettings();
        String propsDataDefault = String.format("Customer params:\n%s", getPropertiesString(customer)); 
        System.out.println( "Default settings for Customer:" );
        System.out.println( propsDataDefault + "\n" );
        circleChanges( "Customers fields" );

        return customerFromCurrentSettings();
    }
    private static void  chargeUnblockRefundGetState( PaytureCommands command )
    {
        PaytureAPIType api = promtService( command );
        Transaction trans;
        String orderId = allFields.get(PaytureParams.OrderId);
        String amount = allFields.get( PaytureParams.Amount );
        circleChanges(null);

        if ( api == PaytureAPIType.api )
            trans = _merchant.Api( command );
        else if ( api == PaytureAPIType.vwapi )
            trans = _merchant.EWallet( command );
        // else if()
        else
            trans = _merchant.InPay( command );
        response = trans.expandTransaction( orderId, Integer.parseInt( amount) ).processOperation();
    }

    private static Data dataForInit(SessionType type) throws  IllegalArgumentException, IllegalAccessException
    {
        generateAmount();
        generateOrderId();
        allFields.replace(PaytureParams.SessionType, type.toString());
        Data data = dataFromCurrentSettings();
        String propsDataDefault = String.format("Data params:\n%s", getPropertiesString(data)); 
        System.out.println( "Default settings for request:" );
        System.out.println( propsDataDefault + "\n" );
        circleChanges(null);
        return dataFromCurrentSettings();
    }


    static void circleChanges(String message) 
    {
        if( message == null || message.isEmpty()){
            message = "default settings";
        }
        System.out.println( String.format("Type '1' if you wanna change %s:", message) );
        int val = 0;
        try {
            val = Integer.parseInt( input.nextLine());
        } catch( Exception ex ) {
            val = 0;
        }
        if ( val == 1 )
            while ( true ) {
                System.out.println( "Type 'ok' if you completed changes" );
                String ok = input.nextLine();
                if ( ok.equalsIgnoreCase("ok") )
                    break;

                changeFields();
            }
        

    }
    static void changeFields()
    {
        System.out.println( "Enter your params in line separated by space, like this: key1=val1 key2=val2" );
        String line = input.nextLine();
        if ( line.isEmpty() )
            return;

        String[] keyValStrs = line.split(" ");

        for(String str : keyValStrs)
        {
            if(!str.contains("="))
                continue;
            String[] strs = str.split("=");
            String paramStr = strs[0].toLowerCase();
            paramStr = paramStr.substring(0, 1).toUpperCase() + paramStr.substring(1, paramStr.length());
            PaytureParams param = PaytureParams.valueOf(paramStr);
            allFields.replace( param, strs[1]);
        }
    }

    public static void listCommands()
    {
        System.out.println(String.format("Commands for help:%1$s%1$s" + 
                    "* fields - list current key-value pairs that used in request to Payture server.%1$s%1$s" +
                    "* changefields - command for changing current values of  key-value pairs that used in request to Payture server.%1$s%1$s" + 
                    "* commands - list avaliable commands for this console program.%1$s%1$s" + 
                    "* changemerchant - commands for changing current merchant account settings.%1$s%1$s" +
                    "* help - commands that types this text (description of commands that you can use in this console program.).%1$s%1$s%1$s", "\n" ));
        System.out.println( String.format("Commands for invoke PaytureAPI functions.%1$s" +
                    "* pay - use for one-stage payment. In EWALLET an INPAY api this command can be use for block funds - if you specify SessionType=Block.%1$s%1$s" +
                    "* block - use for block funds on Customer card. After that command the funds can be charged by Charge command or unblocked by Unblock command. This command use only for API.%1$s%1$s" + 
                    "* charge - write-off of funds from customer card.%1$s%1$s" + 
                    "* unblock - unlocking of funds on customer card.%1$s%1$s" +
                    "* refund - operation for refunds.%1$s%1$s" + 
                    "* getsstate - use for getting the actual state of payments in Payture processing system. This command use only for API.%1$s%1$s" +
                    "* paystatus - use for getting the actual state of payments in Payture processing system. This command use for EWALLET and INPAY.%1$s%1$s" + 
                    "* init - use for payment initialization, customer will be redirected on Payture payment gateway page for enter card's information.%1$s%1$s" + 
                    "* register - register new customer. This command use only for EWALLET.%1$s%1$s" +
                    "* check - check for existing customer account in Payture system. This command use only for EWALLET.%1$s%1$s" + 
                    "* update - This command use only for EWALLET.%1$s%1$s" + 
                    "* delete - delete customer account from Payture system. This command use only for EWALLET.%1$s%1$s" +
                    "* add - register new card in Payture system. This command use only for EWALLET.%1$s%1$s" +
                    "* activate - activate registered card in Payture system. This command use only for EWALLET.%1$s%1$s" +   
                    "* sendcode - provide additional authentication for customer payment. This command use only for EWALLET.%1$s%1$s" +
                    "* remove - delete card from Payture system. This command use only for EWALLET.%1$s%1$s", "\n") );
    }
    
    public static void help()
    {
        System.out.println("\n\nThen console promt you 'Type command' - you can type commands for invoke PaytureAPI functions and you can types commands for help.");
        System.out.println("After you type the command an appropriate method will be execute. If the data is not enough for execute the program promt for additional input.");
    }

    static void payOrBlockAPI( PaytureCommands command ) throws IllegalAccessException
    {
        PayInfo payInfo = getPayInfo();
        String paytureId = allFields.get(PaytureParams.PaytureId);
        String custKey = allFields.get(PaytureParams.CustomerKey);
        String custFields = allFields.get(PaytureParams.CustomFields);
        String.format("PayInfo params:\n%s", getPropertiesString(payInfo));
        System.out.println( "Additional settings for request:" );
        System.out.println(String.format( "\nPaytureId = %s\nCustomerKey = %s\nCustomFields = %s\n", 
                 paytureId, custKey, custFields ));
        circleChanges(null);
        response = _merchant.Api( command ).expandTransaction( payInfo, null, allFields.get(PaytureParams.CustomerKey), allFields.get(PaytureParams.PaytureId) ).processOperation();
        Transaction tr = _merchant.Api( command ).expandTransaction( payInfo, null, allFields.get(PaytureParams.CustomerKey), allFields.get(PaytureParams.PaytureId) );
       /* TransactionAsync trAsync = new TransactionAsync(tr);
        trAsync.processAsync();
        while(!trAsync.ResponseReseived)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        response = trAsync.Response;*/
    }

    static void changeMerchant()
    {
        System.out.println( "Type Merchant account name:" );
        _merchantKey = input.nextLine();
        
        System.out.println( "Type Merchant account password:" );
        _merchantPassword = input.nextLine();
        
        System.out.println( "Type host name:" );
        _host = input.nextLine();
        
        System.out.println( String.format("Merchant account settings:\n\tMerchantName=%s\n\tMerchantPassword=%s\n\tHOST=%s\n", 
                _merchantKey,_merchantPassword, _host ) );
        _merchant = new Merchant( _merchantKey, _merchantPassword, _host );
    }

    static String getPropertiesString(Object obj) throws IllegalArgumentException, IllegalAccessException
    {
        Field[] fields = obj.getClass().getFields();
        String result = "";
        for (Field field : fields) {
            Object val = field.get(obj);
            if (val != null) {
                String name = field.getName();

                result += String.format("\n\t%s=%s;", name, val);
            }
        }
        return result;
    }
    
    //region Simple promts methods
    static PaytureAPIType promtService( PaytureCommands command )
    {
        String outpt = "Type the service api type: ewallet, inpay";
        if( command == PaytureCommands.Pay )
            outpt += " or api";
        System.out.println( outpt );
        String service = input.nextLine();
        if ( service.equalsIgnoreCase("EWALLET") || service.equalsIgnoreCase("E") )
            return PaytureAPIType.vwapi;
        else if ( service.equalsIgnoreCase("INPAY") || service.equalsIgnoreCase("I") )
            return PaytureAPIType.apim;
        else if ( service.equalsIgnoreCase("API") || service.equalsIgnoreCase("A") )
            return PaytureAPIType.api;
        else
        {
            System.out.println( "Illegal service. Only API, EWALLET or INPAY avaliable." );
            return promtService( command );
        }
    }

    static SessionType promtSessionType( PaytureCommands command, PaytureAPIType api )
    {
        String outpt = "Pay, Block";
        if( command == PaytureCommands.Init && api == PaytureAPIType.vwapi )
            outpt += " or Add";
            
        System.out.println( "Specify The SessionType: " + outpt );
        String session = input.nextLine();
        if ( session.equalsIgnoreCase("PAY") || session.equalsIgnoreCase("P") )
            return SessionType.Pay;
        else if ( session.equalsIgnoreCase("BLOCK") || session.equalsIgnoreCase("B") )
            return SessionType.Block;
        else if ( session.equalsIgnoreCase("ADD") || session.equalsIgnoreCase("A") )
            return SessionType.Add;
        else
        {
            System.out.println( "Illegal Session Type. Only pay, block or add avaliable." );
            return promtSessionType( command, api );
        }
    }

    static boolean promtForUseRegCard()
    {
        System.out.println( "Use registered card?  Note: type yes/no or y/n:" );
        String regCard = input.nextLine();
        if ( regCard.equalsIgnoreCase("YES") || regCard.equalsIgnoreCase("Y") )
            return true;
        else if ( regCard.equalsIgnoreCase("NO") || regCard.equalsIgnoreCase("N") )
            return false;
        else
        {
            System.out.println( "Illegal input. Type yes/no or y/n for specify necessity of using registered card." );
            return promtForUseRegCard();
        }
    }


    static boolean promtForUseSessionId( PaytureCommands command )
    {
        System.out.println( String.format("Use SessionId for %s command?  Note: type yes/no or y/n:", command ) );
        String useSessionId = input.nextLine();
        if ( useSessionId.equalsIgnoreCase("YES") || useSessionId.equalsIgnoreCase("Y") )
            return true;
        else if ( useSessionId.equalsIgnoreCase("NO") || useSessionId.equalsIgnoreCase("N") )
            return false;
        else
        {
            System.out.println( String.format( "Illegal input. Type yes/no or y/n for specify necessity of using SessionId in %s operation.", command ) );
            return promtForUseSessionId( command );
        }
    }
    //endregion Simple promts methods
}
