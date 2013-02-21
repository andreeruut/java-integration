package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;

public class HostedRowFormatterTest {
    
    @Test
    public void testFormatOrderRows() {
        OrderRowBuilder row = new OrderRowBuilder();
        row.setArticleNumber("0")
            .setName("Tess")
            .setDescription("Tester")
            .setAmountExVat(4)
            .setVatPercent(25)
            .setQuantity(1)
            .setUnit("st");
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(row);
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder)newRows.get(0);

        assertTrue("0".equals(newRow.getSku()));
        assertTrue("Tess".equals(newRow.getName()));
        assertTrue("Tester".equals(newRow.getDescription()));
        assertTrue(500L == newRow.getAmount());
        assertTrue(100 == newRow.getVat());
        assertEquals(1, newRow.getQuantity());
        assertTrue("st".equals(newRow.getUnit()));
    }
    
    @Test
    public void testFormatShippingFeeRows() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order = (CreateOrderBuilder) order.addFee(Item.shippingFee()
                .setShippingId("0")
                .setName("Tess")
                .setDescription("Tester")
                .setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder)newRows.get(0);

        assertTrue("0".equals(newRow.getSku()));
        assertTrue("Tess".equals(newRow.getName()));
        assertTrue("Tester".equals(newRow.getDescription()));
        assertTrue(1 == newRow.getQuantity());
        assertTrue("st".equals(newRow.getUnit()));
    }
    
    @Test
    public void testFormatShippingFeeRowsVat() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order = (CreateOrderBuilder) order.addFee(Item.shippingFee().setAmountExVat(4).setVatPercent(25));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(0);

        assertTrue(500L == newRow.getAmount());
        assertTrue(100L == newRow.getVat());
    }
    
    @Test
    public void testFormatFixedDiscountRows() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addDiscount(Item.fixedDiscount().setDiscountId("0").setName("Tess").setDescription("Tester").setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder)newRows.get(0);

        assertTrue("0".equals(newRow.getSku()));
        assertTrue("Tess".equals(newRow.getName()));
        assertTrue("Tester".equals(newRow.getDescription()));
        assertTrue(1 == newRow.getQuantity());
        assertTrue("st".equals(newRow.getUnit()));
    }
    
    @Test
    public void testFormatFixedDiscountRowsAmount() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addDiscount(Item.fixedDiscount().setDiscount(4));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(0);
        
        assertTrue(-400L == newRow.getAmount());
    }
    
    @Test
    public void testFormatFixedDiscountRowsVat() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.setTestmode();
        order.addOrderRow(Item.orderRow().setAmountExVat(4).setVatPercent(25).setQuantity(1));
        order.addDiscount(Item.fixedDiscount().setDiscount(1));
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(1);
        
        assertTrue(-100L == newRow.getAmount());
        assertTrue(-20L == newRow.getVat());
    }
    
    @Test
    public void testFormatRelativeDiscountRows() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.setTestmode();
        order.addDiscount(Item.relativeDiscount().setDiscountId("0").setName("Tess").setDescription("Tester").setUnit("st"));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder)newRows.get(0);

        assertTrue("0".equals(newRow.getSku()));
        assertTrue("Tess".equals(newRow.getName()));
        assertTrue("Tester".equals(newRow.getDescription()));
        assertTrue(1 == newRow.getQuantity());
        assertTrue("st".equals(newRow.getUnit()));
    }
    
    @Test
    public void testFormatRelativeDiscountRowsAmount() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(Item.orderRow().setAmountExVat(4).setVatPercent(25).setQuantity(1));
        order.addDiscount(Item.relativeDiscount().setDiscountPercent(10));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(1);
        
        assertTrue(-50L == newRow.getAmount());
    }
    
    @Test
    public void testFormatRelativeDiscountRowsVat() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(Item.orderRow().setAmountExVat(4).setVatPercent(25).setQuantity(1));
        order.addDiscount(Item.relativeDiscount().setDiscountPercent(10));
        
        ArrayList<HostedOrderRowBuilder> newRows = new HostedRowFormatter().formatRows(order);
        HostedOrderRowBuilder newRow = (HostedOrderRowBuilder) newRows.get(1);
        
        assertTrue(-50L == newRow.getAmount());
        assertTrue(-10L == newRow.getVat());
    }
    
    @Test
    public void testFormatTotalAmount() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setAmount(100L)
            .setQuantity(2);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertTrue(200L == new HostedRowFormatter().formatTotalAmount(rows));
    }
    
    @Test
    public void testFormatTotalVat() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setVat(100L)
            .setQuantity(2);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertTrue(200L == new HostedRowFormatter().formatTotalVat(rows));
    }
    
    @Test
    public void testFormatTotalVatNegative() {
        HostedOrderRowBuilder row = new HostedOrderRowBuilder();
        row.setVat(-100L)
            .setQuantity(2);
        ArrayList<HostedOrderRowBuilder> rows = new ArrayList<HostedOrderRowBuilder>();
        rows.add(row);
        
        assertTrue(-200L == new HostedRowFormatter().formatTotalVat(rows)); 
    }
}