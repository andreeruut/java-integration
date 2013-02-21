package se.sveaekonomi.webpay.integration.order.validator;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;

public class HostedOrderValidatorTest {
    
    private OrderValidator orderValidator;
    
    public HostedOrderValidatorTest() {
        orderValidator = new HostedOrderValidator();
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testFailOnNullClientOrderNumber() throws ValidationException {
        String expectedMessage = "MISSING VALUE - OrgNumber is required for company customers when countrycode is SE, NO, DK or FI. Use function setCompanyIdNumber().\n"
                    + "MISSING VALUE - ClientOrderNumber is required. Use function setClientOrderNumber().\n"
                    + "MISSING VALUE - Currency is required. Use function setCurrency().\n"
                    + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"));
        order.setValidator(new VoidValidator());
        order.build();
        assertEquals(orderValidator.validate(order), expectedMessage);      
    }
    
    @Test
    public void testFailOnEmptyClientOrderNumber() throws ValidationException {
        String expectedMessage = "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use function setClientOrderNumber().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(Item.orderRow().setQuantity(1).setAmountExVat(100).setVatPercent(25));
        order.setCurrency("SEK");
        order.setClientOrderNumber("");
        order.addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")                
                .setCompanyName("TestCompagniet")
                .setCompanyIdNumber("1222"));
        order.setValidator(new VoidValidator());
        order.build();
        orderValidator = new HostedOrderValidator();
        assertEquals(orderValidator.validate(order), expectedMessage);
      
    }
    
    @Test
    public void succeedOnGoodValuesSe() throws ValidationException {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.setValidator(new VoidValidator());
        order.setClientOrderNumber("1");
        order.addOrderRow(Item.orderRow()
            .setAmountExVat(5.0)
            .setVatPercent(25)
            .setQuantity(1));
        order.addCustomerDetails(Item.companyCustomer()
            .setVatNumber("2345234")
            .setCompanyName("TestCompagniet"));
        orderValidator = new HostedOrderValidator();
        orderValidator.validate(order);
    }
}