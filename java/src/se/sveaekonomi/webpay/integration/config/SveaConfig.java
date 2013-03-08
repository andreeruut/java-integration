package se.sveaekonomi.webpay.integration.config;

import java.net.URL;

import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaAuth;

public class SveaConfig {
    
    private String userName;
    private String password;
    private int invoiceClientNumber;
    private int paymentPlanClientNumber;
    private String merchantId;
    private String secret;
    private URL payPageUrl;
    private URL soapUrl;
    
    public SveaConfig() {
        this.userName = "sverigetest";
        this.password = "sverigetest";
        this.invoiceClientNumber = 79021;
        this.paymentPlanClientNumber = 59999;
        this.merchantId = "1175";
        this.secret = "d153477288051d6001adf0648405e0fcfaa3ee2a8dc90dd3151341a1d68b1a4388616585fe7bc15cd06882070b0d92aa92de6cde1e7a21dc7e65e81cee6af43f";
    } 
    
    public SveaConfig(String userName, String password,
			int invoiceClientNumber, int paymentPlanClientNumber,
			String merchantId, String secret, URL payPageUrl, URL soapUrl) {
		this.userName = userName;
		this.password = password;
		this.invoiceClientNumber = invoiceClientNumber;
		this.paymentPlanClientNumber = paymentPlanClientNumber;
		this.merchantId = merchantId;
		this.secret = secret;
		this.payPageUrl = payPageUrl;
		this.soapUrl = soapUrl;
	}

/*	public static SveaConfig createCompleteProductionConfig(String userName, String password,
			int invoiceClientNumber, int paymentPlanClientNumber,
			String merchantId, String secret) {
    	return new SveaConfig();
    }

	public static SveaConfig createSoapProductionConfig(String userName, String password,
			int invoiceClientNumber, int paymentPlanClientNumber,
			String merchantId, String secret) {
    	return new SveaConfig();
    }

    public static SveaConfig createTestConfig() {
    	return new SveaConfig();
    }*/

    public static Config createProductionConfig() {
        return new ProductionConfig();
       }

       public static Config createTestConfig() {
        return new TestConfig();
       }
    
    public SveaAuth getAuthorizationForWebServicePayments(String type) {
        SveaAuth auth = new SveaAuth();
        auth.Username = this.userName;
        auth.Password = this.password;
        if(type=="PaymentPlan")
            auth.ClientNumber = this.paymentPlanClientNumber;        
        else
            auth.ClientNumber = this.invoiceClientNumber;
        return auth;
    }

    public void setPasswordBasedAuthorization(String userName, String password, int clientNumber, String orderType) {
        this.userName = userName;
        this.password = password;
        
        if (orderType == "Invoice")
            this.invoiceClientNumber = clientNumber;
        else
            this.paymentPlanClientNumber = clientNumber;        
    }
    
    public String getMerchantId() {
        return this.merchantId;
    }
    
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
    
    public String getSecretWord() {
        return this.secret;
    }
    
    public void setSecretWord(String secret) {
        this.secret = secret;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setInvoiceClientNumber(int invoiceClientNumber) {
        this.invoiceClientNumber = invoiceClientNumber;
    }
    
    public int getInvoiceClientNumber() {
        return this.invoiceClientNumber;
    }
    
    public void setPaymentPlanClientNumber(int paymentPlanClientNumber) {
        this.paymentPlanClientNumber = paymentPlanClientNumber;
    }
}
