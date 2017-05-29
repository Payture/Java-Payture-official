# Java-Payture-official
This is Offical Payture API for Java. We're try to make this as simple as possible for you! Explore tutorial and get started. Please note, you will need a Merchant account,  contact our support to get one. 
Here you can explore how to use our API functions!


## Payture API tutorial
Before fall into the deep, we're need to provide you general conception of working with our API function. See picture: 
![](paytureAPIJava.png)

## Steps

 * [Creating merchant account](#newMerchant)
 * [Get access to required API](#accessToAPI)
 * [Expand transaction](#expandTransaction)
 * [Send request](#sendRequest)

Now, let's walk through the steps from the picture above


## First Step - Creating Merchant Account <a id="newMerchant"></a>
To get access for API usage just create the instance of Merchant object, pass in the constructor the name of the host, name of your account and your account password.  Suppose that you have Merchant account with  name: Key = "MyMerchantAccount" and password Password = "MyPassword".

Pass the 'https://sandbox.payture.com' for test as the name of Host (first parameter).
```java
Merchant merchant = new Merchant("https://sandbox.payture.com", "MyMerchantAccount", "MyPassword");
```
We're completed the first step! Go next!
***
Please note, that  Key = "'MyMerchantAccount" and Password = "MyMerchantAccount"  - fake, [our support](http://payture.com/kontakty/) help you to get one!
***

## Second Step - Get access to required API <a id="accessToAPI"></a>
At this step you just call one of following methods on Merchant object (which provide proper API type for you) and pass in the PaytureCommands [see description here](#PaytureCommands): 
* Api (this is PaytureAPI)
```java
merchant.Api( PaytureCommands.Pay );
```
* InPay (this is PaytureInPay)
```java
merchant.InPay( PaytureCommands.Pay );
```
* EWallet (this is PaytureEWallet)
```java
merchant.EWallet( PaytureCommands.Init );
```
* Apple (this is PaytureApplePay)
And pass in the [PaytureCommands](#PaytureCommands).
```java
merchant.Apple( PaytureCommands.Pay );
```
* Android (this is PaytureApplePay)
And pass in the [PaytureCommands](#PaytureCommands).
```java
merchant.Android( PaytureCommands.Pay );
```
Result of this methods is the instanse of Transaction object which you expand in the next step. 

 [See this table](#PaytureCommandsTable) for explore what PaytureCommands received  theese methods.

## Third Step - Expand transaction <a id="extpandTransaction"></a>
This is the most difficult step, but you can do it!
In the previous step we get the Transaction object [see here that is it](#Transaction). You need expand it, below you find detailed description how do this for every type of api.

At this step we're call only one method: ExpandTransaction(...). But there are more overload exist!
### expandTransaction ( String orderId, Integer amount )
This overload available in any of the API type

Call this for following PaytureCommands:
* Unblock
* Refund
* Charge
* GetState (PaytureAPI)
* PayStatus (PaytureEWallet, PaytureInPay)

| Parameter's name | Definition                                                        |
| ---------------- | ----------------------------------------------------------------- |
| orderId          | Payment identifier in your service system.                        |
| amount           | Amount of payment kopec. (in case of GetState or PayStatus pass null)                                          |


### ExpandTransaction Methods for PaytureAPI
#### expandTransaction( PayInfo info, HashMap<String, String> customFields, String customerKey, String paytureId  )
This overload you call for api **Pay** or **Block** methods ( PaytureCommands.Pay or PaytureCommands.Block respectively )
Description of provided params.

| Parameter's name | Definition                                                                             |
| ---------------- | -------------------------------------------------------------------------------------- |
| info             | Params for transaction processings [see here for explore PayInfo object](#PayInfo)     |
| customerKey      | Customer identifier in Payture AntiFraud system.                                       |
| customFields     | Addition fields for processing (especially for AntiFraud system).                      |
| paytureId        | Payments identifier in Payture AntiFraud system.                                       |


### ExpandTransaction Methods for PaytureInPay
#### expandTransaction( Data data )
This overload you call for api **Init** method ( PaytureCommands.Init )
Full description of recieved [data see here](#Data).
You must specify following fields of Data object then call Init api method of PaytureInPay:
* SessionType
* OrderId
* Amount
* IP
Other fields is optional.


### ExpandTransaction Methods for PaytureEWallet
#### expandTransaction( Customer customer, Card card, Data data, boolean regCard ) 
This overload you call for api **Init**, **Pay** (on merchant side for registered or no registered cards);

#### expandTransaction( Customer customer, Card card )
This overload you call for api **Add** method ( PaytureCommand.Add ) on merchant side.

#### expandTransaction( Customer customer )
This overload is called for following api methods: **Register** (PaytureCommands.Register), **Update** (PaytureCommands.Update), **Delete** (PaytureCommands.Delete), **Check** (PaytureCommands.Check), **GetList** (PaytureCommands.GetList)
Description of recieved [Customer data see here](#Customer).

#### expandTransaction( Customer customer, String cardId, Integer amount, String orderId )
This overload is called for api methods: **SendCode** (PaytureCommands.SendCode), **Activate** (PaytureCommands.Activate), **Remove** (PaytureCommands.Remove)

### ExpandTransaction Methods for PaytureApplePay and PaytureAndroidPay
#### expandTransaction(String payToken, String orderId, Integer amount)
This overload you call for api **Pay** (PaytureCommands.Pay) and **Block** (PaytureCommands.Block) methods.
Description of provided params.

| Parameter's name | Definition                                                                             |
| ---------------- | -------------------------------------------------------------------------------------- |
| payToken         | PayToken for current transaction.   |
| orderId          | Current transaction OrderId, if you miss this value (if pass null) - it will be generate on Payture side.    |
| amount           | Current transaction amount in kopec (pass null for ApplePay).                      |


## Last Step - Send request <a id="sendRequest"></a>
After transaction is expanded you can send request to the Payture server via one of two methods:
* processOperation(); - this is sync method. The executed thread will be block while waiting response from the server - return the PaytureResponse object
* ProcessOperationAsync(); - this async method, return Task<PaytureResponse> object;


## Base Types:
* [PayInfo](#PayInfo)
* [Card](#Card)
* [Data](#Data)
* [PaytureCommands](#PaytureCommands)
* [Customer](#Customer)
* [PaytureResponse](#PaytureResponse)
* [CardInfo](#CardInfo)

### PayInfo <a id="PayInfo"></a>
This object used for PaytureAPI and consist of following fields:

| Fields's name    | Field's type | Definition                                      |
| ---------------- | ------------ | ----------------------------------------------- |
| OrderId          | String       | Payment identifier in your service system.      |
| Amount           | long         | Amount of payment kopec.                        |
| PAN              | String       | Card's number.                                  |
| EMonth           | Integer      | The expiry month of card.                       |
| EYear            | Integer      | The expiry year of card.                        |
| CardHolder       | String       | Card's holder name.                             |
| SecureCode       | Integer      | CVC2/CVV2.                                      |

Example of creation instence of PayInfo, only one constructor is available:
```java
PayInfo info = new PayInfo( "4111111111111112", 10, 20, "Test Test", 123, "TestOrder0000000000512154545", 580000  );
```

### Card <a id="Card"></a>
This object used for PaytureEWallet and consist of following fields:

| Fields's name    | Field's type | Definition                                      |
| ---------------- | ------------ | ----------------------------------------------- |
| CardId           | String       | Card identifier in Payture system.              |
| CardNumber       | String       | Card's number.                                  |
| EMonth           | Integer      | The expiry month of card.                       |
| EYear            | Integer      | The expiry year of card.                        |
| CardHolder       | String       | Card's holder name.                             |
| SecureCode       | Integer      | CVC2/CVV2.                                      |

Examples of creation instance of Card:
```java
Card card = new Card( "4111111111111112", 10, 20, "Test Test", 123, null ); //create card with CardId = null
Card card2 = new Card( "4111111111111112", 10, 20, "Test Test", 123, "40252318-de07-4853-b43d-4b67f2cd2077" ); //create card with CardId = "40252318-de07-4853-b43d-4b67f2cd2077"
Card card3 = new Card( null, null, null, null, 123, "40252318-de07-4853-b43d-4b67f2cd2077" );  //this used in PaytureCommand.Pay on merchant side
```
### Data <a id="Data"></a>

### PaytureCommands <a id="PaytureCommands"></a>
This is enum of **all** available commands for Payture API.

PaytureCommands list and availability in every api type

| Command      | Api | InPay | EWallet | Apple | Android | Description                                                                                                            |
| ------------ | --- | ----- | ------- | ----- | ------- | ---------------------------------------------------------------------------------------------------------------------- |
| Pay          |  +  |   +   |    +    |       |         | Command for pay transaction. In InPay and EWallet can be used for Block operation                                      |
| Block        |  +  |       |         |       |         | Block of funds on customer card. You can write-off of funds by Charge command or unlocking of funds by Unblock command |
| Charge       |  +  |   +   |    +    |       |         | Write-off of funds from customer card                                                                                  |
| Refund       |  +  |   +   |    +    |       |         | Operation for refunds                                                                                                  |
| Unblock      |  +  |   +   |    +    |       |         | Unlocking of funds  on customer card                                                                                   |
| GetState     |  +  |       |         |       |         | Get the actual state of payments in Payture processing system                                                          |
| Init         |     |   +   |    +    |       |         | Payment initialization, customer will be redirected on Payture payment gateway page for enter card's information       |
| PayStatus    |     |   +   |    +    |       |         | Get the actual state of payments in Payture processing system                                                          |
| Add          |     |       |    +    |       |         | Register new card in Payture system                                                                                    |
| Register     |     |       |    +    |       |         | Register new customer account                                                                                          |
| Update       |     |       |    +    |       |         | Update customer account                                                                                                |
| Check        |     |       |    +    |       |         | Check for existing customer account in Payture system                                                                  |
| Delete       |     |       |    +    |       |         | Delete customer account from Payture system                                                                            |
| Activate     |     |       |    +    |       |         | Activate registered card in Payture system                                                                             |
| Remove       |     |       |    +    |       |         | Delete card from Payture system                                                                                        |
| GetList      |     |       |    +    |       |         | Return list of registered cards for the customer existed in Payture system                                             |
| SendCode     |     |       |    +    |       |         | Additional authentication for customer payment                                                                         |
| ApplePay     |     |       |         |       |         | Command for one-stage charge for Apple                                                                                 |
| AppleBlock   |     |       |         |       |         | Block of funds on customer card attached in Apple Wallet                                                               |
| Pay3DS       |  +  |       |         |   +   |    +    | Command for one-stage charge from card with 3-D Secure                                                                 |
| Block3DS     |  +  |       |         |   +   |    +    | Block of funds on customer card with 3-D Secure                                                                        |
| PaySubmit3DS |     |       |    +    |       |         | Commands for completed charging funds from card with 3-D Secure                                                        |


### Customer <a id="Customer"></a>
This object used for PaytureEWallet and consist of following fields:

| Fields's name    | Field's type | Definition                                                       |
| ---------------- | ------------ | ---------------------------------------------------------------- |
| VWUserLgn        | String       | Customer's identifier in Payture system. (Email is recommended). |
| VWUserPsw        | String       | Customer's password in Payture system.                           |
| PhoneNumber      | String       | Customer's phone number.                                         |
| Email            | String       | Customer's email.                                                |

```java
Customer customer = new Customer( "testLogin@mail.com", "customerPassword", null, null ); //create customer without phone and email
Customer customer2 = new Customer( "testLogin@mail.com", "customerPassword", "77125141212", "testLogin@mail.com" ); //customer with all fields
```


### PaytureResponse <a id="PaytureResponse"></a>
This object is response from the Payture server and consist of following fields:

| Fields's name    | Field's type                | Definition                                                                                       |
| ---------------- | --------------------------- | ------------------------------------------------------------------------------------------------ |
| APIName          | PaytureCommands             | Name of commands that was called.                                                                |
| Success          | Boolean                     | Determines the success of processing request.                                                    |
| ErrCode          | String                      | Will be contain code of error if one occur during process the transaction on the Payture server. | 
| RedirectURL      | String                      | Will be contain the new location for redirect. (for PaytureCommands.Init).                       |
| Attributes       | HashMap<String, String>     | Addition attributes from the response.                                                           |
| RedirectURL      | String                      | Will be contain the new location for redirect. (for PaytureCommands.Init).                       |
| ListCards        | List<CardInfo>              | List of cards, theese registered for current Customer (this field filled for PaytureCommands.GetList)  |
| ResponseBodyXML  | String                      | String representation received from Payture server in XML format                                 |


### CardInfo <a id="CardInfo"></a>
Special object for containing Customer card's information, that we're received from PaytureCommands.GetList command

| Fields's name    | Field's type  | Definition                                                             |
| ---------------- | ------------- | ---------------------------------------------------------------------- |
| CardNumber       | String        | The masked card's number.                                              |
| CardId           | String        | Card identifier in Payture system.                                     |
| CardHolder       | String        | Name of card's holder                                                  | 
| ActiveStatus     | String        | Indicate of card's active status in Payture system                     |
| Expired          | Boolean       | Indicate whether the card expired on the current date                  |
| NoCVV            | Boolean       | Indicate whether or not payment without CVV/CVC2                       |

Visit our [site](http://payture.com/) for more information.
You can find our contact [here](http://payture.com/kontakty/).