/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.commonTypes;

import payture.paytureEnums.PaytureCommands;

/**
 *
 * @author Soloveva Elena
 */

public class Merchant {
    //region private fields
    private String _key;
    private String _password;
    private String _host;

    //region properties
    public String getMerchantName()
    { 
        return _key; 
    }
    public String getPassword() 
    { 
        return _password; 
    }
    public String getHOST()
    {
        return _host;  
    }

    public Merchant( String accountName, String password, String host )
    {
        _key = accountName;
        _password = password;
        _host = host;
    }

    public TransactionAPI Api(PaytureCommands command)
    {
        return new TransactionAPI( command, this );
    }

    public TransactionInPay InPay( PaytureCommands command )
    {
        return new TransactionInPay( command, this );
    }

    public TransactionEWallet EWallet( PaytureCommands command )
    {
        return new TransactionEWallet( command, this );
    }

  /*  public PaytureAPI GETPaytureApplePay( PaytureCommands command )
    {
        return new PaytureAPI( this, PaytureAPIType.api );
    }*/

 }

