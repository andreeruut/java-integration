package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.ExcludePayments;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.INVOICETYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTPLANTYPE;

public class HostedPaymentTest {

    @Test
    public void testCalculateRequestValuesNullExtraRows() throws Exception {
    
    	CreateOrderBuilder order = WebPay.createOrder()    	
            .addOrderRow(Item.orderRow()
            		.setAmountExVat(4)
                    .setVatPercent(25)
                    .setQuantity(1))            
            
	        .addFee(Item.shippingFee())
	        .addDiscount(Item.fixedDiscount())
	        .addDiscount(Item.relativeDiscount());
	    	
        HostedPayment payment = new FakeHostedPayment(order);        
        payment.calculateRequestValues();
        
        assertTrue(500L == payment.getAmount());
    }

    @Test
    public void testVatPercentAndAmountIncVatCalculation() {        
        CreateOrderBuilder order = WebPay.createOrder()        		
            .addOrderRow(Item.orderRow()
            		.setAmountIncVat(5)
                    .setVatPercent(25)
                    .setQuantity(1));            
        
        order.setShippingFeeRows(null);
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        HostedPayment payment = new FakeHostedPayment(order);
        payment.calculateRequestValues();
        
        assertEquals(500L,payment.getAmount(), 0);
    }
    
    
    @Test
    public void testAmountIncVatAndvatPercentShippingFee() {        
      CreateOrderBuilder order = WebPay.createOrder()        		
            .addOrderRow(Item.orderRow()
            		.setAmountIncVat(5)
                    .setVatPercent(25)
                    .setQuantity(1))            
        
            .addFee(Item.shippingFee()
            		.setAmountIncVat(5)
            		.setVatPercent(25));
      
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        HostedPayment payment = new FakeHostedPayment(order);
        payment.calculateRequestValues();
        
        assertEquals(1000L,payment.getAmount(), 0);
    }
    
    @Test
    public void testAmountIncVatAndAmountExVatCalculation() {
    	 CreateOrderBuilder order = WebPay.createOrder()        		
    			 .addOrderRow(Item.orderRow()
                		.setAmountExVat(4)
                        .setAmountIncVat(5)
                        .setQuantity(1));
    	 
        order.setShippingFeeRows(null);
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        HostedPayment payment = new FakeHostedPayment(order);
        payment.calculateRequestValues();
        
        assertTrue(500L == payment.getAmount());
    }
    
    @Test
    public void testCreatePaymentForm() throws Exception {
    	 CreateOrderBuilder order = WebPay.createOrder()        		
                .addOrderRow(Item.orderRow()
                		.setAmountExVat(4)
                        .setVatPercent(25)
                        .setQuantity(1)); 
      
    	HostedPayment payment = new FakeHostedPayment(order);
        PaymentForm form;
        
        try {
            form = payment.getPaymentForm();
        } catch (Exception e) {
            throw e;
        }
        
        Map<String, String> formHtmlFields = form.getFormHtmlFields();        
        assertTrue(formHtmlFields.get("form_end_tag").equals("</form>"));
    }

    @Test
    public void testExcludeInvoicesAndAllInstallmentsAllCountries() {
        HostedPayment payment = new FakeHostedPayment(null);
        ExcludePayments exclude = new ExcludePayments();       
        List<String> excludedPaymentMethods = payment.getExcludedPaymentMethods();
        excludedPaymentMethods.addAll(exclude.excludeInvoicesAndPaymentPlan());
        
        assertEquals(14, excludedPaymentMethods.size());
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICESE.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_SE.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_DE.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_DK.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_FI.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_NL.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_NO.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLANSE.getValue()));        
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_SE.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_DE.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_DK.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_FI.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_NL.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_NO.getValue()));
    }
 
}
