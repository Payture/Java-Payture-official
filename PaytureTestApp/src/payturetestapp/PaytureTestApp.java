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
import java.util.Random;
import java.util.Scanner;
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
    static String _host = "http://sasha:7080";
    static String _merchantKey = "elena_Test";
    static String _merchantPassword = "789555";
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
            //allFields.put(PaytureParams.Url, ""); url is needed


            System.out.println( String.format("Merchant account settings:\n\tMerchantName=%s\n\tMerchantPassword=%s\n\tHOST=%s\n", _merchantKey, _merchantPassword, _host) );
            System.out.println("Press space for change Merchant account settings" );
            char chInpt = (char) System.in.read();

            if ( chInpt == 32 )
            {

                changeMerchant();
            }

            while(true)
            {

                System.out.println("\nPress backspase for exit" );
                char ch = (char) System.in.read();
                if ( ch == 8 )
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
       String print = String.format("\nResponse Result\n%s Success=%s; Attribute=%s", response.APIName, response.Success, response.Attributes);
       // System.out.println("\nResponse Result\n{response.APIName} Success={response.Success}; Attribute=[{response.Attributes.Aggregate( "", ( a, c ) => a += $"{c.Key}={c.Value}; " )}]" );
       System.out.println(print);
    }
        
        
        
        
    //Router
        
    public static void Router ( ) throws IllegalArgumentException,  IOException
    {
        try{
        System.out.println( "Type the command:" );
        String[] command = input.nextLine().split(" ");
        if ( command.length < 1)
            return;
        PaytureAPIType apiType = PaytureAPIType.api;//api type
        String sessionType = "";
        String transactionSide = "";
        String regOrNoRegCard = "";
        String cmd = command[0].toUpperCase();  //main command
        String noPayCmd = "FIELDS COMMANDS CHANGEFIELDS";
        if ( noPayCmd.indexOf(cmd) == -1 )
        {
            try{
            apiType = PaytureAPIType.valueOf(command[1]);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        if ( command.length > 2 )
        {
            if (  "I INIT P PAY".indexOf(cmd) != -1 )
            {
                sessionType = command[ 2 ].toUpperCase().subSequence(0, 1).toString(); //session type
                if ( ( cmd == "PAY" || cmd == "P" ) && command.length >= 3 && apiType == PaytureAPIType.vwapi )
                {
                    transactionSide = command[3].toUpperCase().subSequence( 0, 1 ).toString();
                    if(transactionSide == "M" && command.length > 3)
                        regOrNoRegCard = command[ 4 ].toUpperCase().subSequence( 0, 1 ).toString();
                }
                else if("INIT".equals(cmd) || "I".equals(cmd))
                    { }
                else return;
            }
            else if("ADD".equals(cmd))
                transactionSide = command[2].toUpperCase().subSequence( 0, 1 ).toString();
        }

        switch ( cmd )
        {
            case "PAY":
                {
                    if ( apiType == PaytureAPIType.api )
                    {
                        payOrBlockAPI( PaytureCommands.Pay );
                        break;
                    }
                    switch( transactionSide )
                    {
                        case "P":
                            {
                                payturePayOrAdd( PaytureCommands.Pay );
                                break;
                            }
                        case "M":
                            {
                                Customer customer = getCustomer();
                                DATA data = dataForInit(sessionType == "P" ? SessionType.Pay : SessionType.Block);          

                                if(regOrNoRegCard != "R")
                                {
                                    Card card = getCard();
                                    response = _merchant.EWallet( PaytureCommands.Pay ).expandTransaction(customer, card, data, false).processOperation();
                                    break;

                                }

                                String cardId = allFields.get( PaytureParams.CardId );
                                String secureCode = allFields.get( PaytureParams.SecureCode );
                                System.out.println(String.format("CardId=%s; SecureCode=%s;", cardId, secureCode) );
                                circleChanges( "CardId and SecureCode" );

                                Card card = new Card();
                                card.CardId = allFields.get(PaytureParams.CardId );
                                card.SecureCode = Integer.getInteger(allFields.get( PaytureParams.SecureCode ));
                                response = _merchant.EWallet( PaytureCommands.Pay ).expandTransaction(customer, card, data, true).processOperation();

                                break;
                            }
                    }
                    System.out.println(response);
                    break;
                }
            case "BLOCK":
                {
                    payOrBlockAPI( PaytureCommands.Block );
                    break;
                }
            case "CHARGE":
                {
                    chargeUnblockRefundGetState( PaytureCommands.Charge, apiType );
                    break;
                }
            case "REFUND":
                {
                    chargeUnblockRefundGetState( PaytureCommands.Refund, apiType );
                    break;
                }
            case "UNBLOCK":
                {
                    chargeUnblockRefundGetState( PaytureCommands.Unblock, apiType );
                    break;
                }
            case "GETSTATE":
                {
                    chargeUnblockRefundGetState( PaytureCommands.GetState, apiType );
                    break;
                }
            case "PAYSTATUS":
                {
                    chargeUnblockRefundGetState( PaytureCommands.PayStatus, apiType );
                    break;
                }
            case "INIT":
                {
                    SessionType t = "P".equals(sessionType) ? SessionType.Pay : "B".equals(sessionType)? SessionType.Block : SessionType.Add;
                    DATA data = dataForInit(t);


                    if ( apiType == PaytureAPIType.vwapi )
                    {
                        Customer customer = getCustomer();
                        response = _merchant.EWallet( PaytureCommands.Init ).expandTransaction( customer, new Card(), data, false ).processOperation();
                    }
                    else
                        response = _merchant.InPay( PaytureCommands.Init ).expandTransaction( data ).processOperation();
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(new URL(response.RedirectURL).toURI());
                    break;
                }
            case "ACTIVATE":
                {
                    customerAndCardAPI( PaytureCommands.Activate );
                    break;
                }
            case "REMOVE":
                {
                    customerAndCardAPI(  PaytureCommands.Remove );
                    break;
                }
            case "GETLIST":
                {
                    customerAndCardAPI( PaytureCommands.GetList );
                    break;
                }
            case "REGISTER":
                {
                    customerAndCardAPI( PaytureCommands.Register );
                    break;
                }
            case "UPDATE":
                {
                    customerAndCardAPI( PaytureCommands.Update );
                    break;
                }
            case "DELETE":
                {
                    customerAndCardAPI( PaytureCommands.Delete );
                    break;
                }
            case "CHECK":
                {
                    customerAndCardAPI( PaytureCommands.Check );
                    break;
                }
             case "ADD":
                {
                    switch(transactionSide)
                    {
                        case "P":
                            {
                                payturePayOrAdd( PaytureCommands.Add );
                                break;
                            }
                        case "M":
                            {
                                Customer customer = getCustomer();
                                Card card = getCard();

                                response = _merchant.EWallet( PaytureCommands.Add ).expandTransaction(customer, card).processOperation();    
                                break;
                            }
                    }
                    break;
                }
            case "FIELDS":
                {
                    //String aggrStr = allFields.Aggregate( $"{Environment.NewLine}Current value of fields:{Environment.NewLine}", ( a, c ) => a += $"\t{c.Key} = {c.Value}{Environment.NewLine}" );
                    //System.out.println( aggrStr );
                    break;
                }
            case "CHANGEFIELDS":
                {
                    circleChanges(null);
                    break;
                }
            case "COMMANDS":
                {
                    listCommands();
                    break;
                }
            case "CHANGEMERCHANT":
                {
                    changeMerchant();
                    break;
                }
        }
        if ( "FIELDS CHANGEFIELDS COMMANDS CHANGEMERCHANT".indexOf(cmd ) == -1 )
            WriteResult( response );
        }
        catch (Exception ex)
        {
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
      /* for (Map.Entry<PaytureParams, String> entry: allFields.entrySet())
            System.out.println(entry.getKey() + " = " + entry.getValue());*/

        String pan = allFields.get( PaytureParams.PAN );
        System.out.println("PAN: " + pan);
        Integer em = Integer.parseInt( allFields.get( PaytureParams.EMonth ) );
        System.out.println("eMonth: " + em);
        Integer ye = Integer.parseInt( allFields.get( PaytureParams.EYear ));
        System.out.println("eYear: " + ye);
        String holder = allFields.get( PaytureParams.CardHolder );
        System.out.println("CardHolder: " + holder);
        Integer secCode = Integer.parseInt( allFields.get( PaytureParams.SecureCode ) );
        System.out.println("SecureCode: " + secCode);
        String orId = allFields.get( PaytureParams.OrderId );
        System.out.println("OrderId: " + orId);
        Integer amount = Integer.parseInt( allFields.get( PaytureParams.Amount ) );
        System.out.println("Amount: " + amount);
        PayInfo info = new PayInfo( pan, em, ye, holder,  secCode, orId,  amount );
        return info;
    }

    static Customer customerFromCurrentSettings()
    {
        return new Customer( allFields.get( PaytureParams.VWUserLgn ), allFields.get( PaytureParams.VWUserPsw ), allFields.get( PaytureParams.PhoneNumber ), allFields.get( PaytureParams.Email ) );
    }

    static DATA dataFromCurrentSettings()
    {
        DATA data = new DATA();
        data.Amount = allFields.get( PaytureParams.Amount ) == null ? null : Integer.parseInt( allFields.get( PaytureParams.Amount ) );
        data.IP = allFields.get( PaytureParams.IP );
        data.Language = allFields.get( PaytureParams.Language );
        data.OrderId = allFields.get( PaytureParams.OrderId );
        data.SessionType = allFields.get( PaytureParams.SessionType );
        data.TemplateTag = allFields.get( PaytureParams.TemplateTag );
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
        circleChanges(null);
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
            Integer i = command == PaytureCommands.Activate ? 101 : null;
            response = _merchant.EWallet( command ).expandTransaction( customer, allFields.get(PaytureParams.CardId), i , null).processOperation();
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
    private static void  chargeUnblockRefundGetState( PaytureCommands command, PaytureAPIType api )
    {
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

    private static DATA dataForInit(SessionType type) throws  IllegalArgumentException, IllegalAccessException
    {
        generateAmount();
        generateOrderId();
        allFields.replace(PaytureParams.SessionType, type.toString());
        DATA data = dataFromCurrentSettings();
        String propsDataDefault = String.format("Data params:\n%s", getPropertiesString(data)); 
        System.out.println( "Default settings for request:" );
        System.out.println( propsDataDefault + "\n" );
        circleChanges(null);
        return dataFromCurrentSettings();
    }


    static void circleChanges(String message) 
    {
        try{
        if(message == null || message.isEmpty())
        {
            message = "default settings";
        }
        System.out.println( String.format("Please enter <1> if you wanna change %s:", message) );
        int val = 0;
        try {
            val = input.nextInt();
        }
        catch( Exception ex) {
            val = 0;
        }
        if ( val == 1 )
            while ( true ) {
                System.out.println( "Press Backspace if you completed changes" );
                char ch = (char) System.in.read();
                if ( /*ch == 8*/ true )
                    break;

                changeFields();
            }
        }
        catch( IOException e)
        {
            e.printStackTrace();
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
            PaytureParams param = PaytureParams.valueOf(strs[0]);
            allFields.replace( param, strs[1]);
        }
    }

    public static void listCommands()
    {
        System.out.println( "Common commands:\n\tfields\n\tchangefields\n\tcommands\n\n" );
        System.out.println( "Payments commands:\n\tpay\n\tblock\n\tunblock\n\tcharge\n\trefund\n\tgetstate\n\tpaystatus\n\tregister\n\tcheck\n\tupdate\n\tremove\n\tadd\n\tactivate\n\tdelete\n\tgetlist\n\tsendcode\n\n\n" );
    }


    static void payOrBlockAPI( PaytureCommands command ) throws IllegalAccessException
    {
        PayInfo payInfo = getPayInfo();
        String paytureId = allFields.get(PaytureParams.PaytureId);
        String custKey = allFields.get(PaytureParams.CustomerKey);
        String custFields = allFields.get(PaytureParams.CustomFields);
        String propsPayInfo = String.format("PayInfo params:\n%s", getPropertiesString(payInfo));
        System.out.println( "Additional settings for request:" );
        System.out.println(String.format( "%s\nPaytureId = %s\nCustomerKey = %s\nCustomFields = %s\n", 
                propsPayInfo, paytureId, custKey, custFields ));
        circleChanges(null);
        response = _merchant.Api( command ).expandTransaction( payInfo, null, allFields.get(PaytureParams.CustomerKey), allFields.get(PaytureParams.PaytureId) ).processOperation();
    }

    static void changeMerchant()
    {
        System.out.println( "Type Merchant account name:" );
        _merchantKey = input.nextLine();
        System.out.println( "Type Merchant account password" );
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

    
}
