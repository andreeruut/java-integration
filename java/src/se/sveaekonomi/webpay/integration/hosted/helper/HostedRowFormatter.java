package se.sveaekonomi.webpay.integration.hosted.helper;

import java.util.ArrayList;

import se.sveaekonomi.webpay.integration.hosted.HostedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.RowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;

public class HostedRowFormatter {

	private double totalAmount;
	private double totalVat;

	private double totalShippingAmount;
	private double totalShippingVat;

	private ArrayList<HostedOrderRowBuilder> newRows;

	public HostedRowFormatter() {
		totalAmount = 0.0;
		totalVat = 0.0;

		totalShippingAmount = 0.0;
		totalShippingVat = 0.0;

		newRows = new ArrayList<HostedOrderRowBuilder>();
	}

	public ArrayList<HostedOrderRowBuilder> formatRows(CreateOrderBuilder order) {
		formatOrderRows(order);
		formatShippingFeeRows(order);
		formatFixedDiscountRows(order);
		formatRelativeDiscountRows(order);

		return newRows;
	}

	private <T extends OrderBuilder<T>> void formatOrderRows(OrderBuilder<T> orderBuilder) {
		for (OrderRowBuilder row : orderBuilder.getOrderRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, row.getArticleNumber() == null ? "" : row.getArticleNumber());

			double vatFactor = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;

			double amountExVat = row.getAmountExVat() != null ? row.getAmountExVat() : 0;
			double amountIncVat = row.getAmountIncVat() != null ? row.getAmountIncVat() : 0;

			double tempAmount;
			double tempVat;
			double quantity = row.getQuantity();

			if (row.getAmountExVat() != null && row.getVatPercent() != null) {
				tempAmount = amountExVat * vatFactor;
				tempVat = tempAmount - amountExVat;
			}
			else if (row.getAmountIncVat() != null && row.getVatPercent() != null) {
				tempAmount = amountIncVat;
				tempVat = tempAmount - (tempAmount / vatFactor);
			}
			else {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - amountExVat;
			}

			tempRow.setAmount(MathUtil.convertFromDecimalToCentesimal(tempAmount));
			tempRow.setVat(MathUtil.convertFromDecimalToCentesimal(tempVat));
			tempRow.setQuantity(quantity);

			totalAmount += tempAmount * quantity;
			totalVat += tempVat * quantity;

			newRows.add(tempRow);
		}
	}

	private <T extends OrderBuilder<T>> void formatShippingFeeRows(OrderBuilder<T> orderBuilder) {
		if (orderBuilder.getShippingFeeRows() == null) {
			return;
		}

		for (ShippingFeeBuilder row : orderBuilder.getShippingFeeRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, row.getShippingId());

			double plusVatCounter = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;

			double amountExVat = row.getAmountExVat() != null ? row.getAmountExVat() : 0;
			double amountIncVat = row.getAmountIncVat() != null ? row.getAmountIncVat() : 0;

			double tempAmount;
			double tempVat;
			double quantity = row.getQuantity();

			if (row.getAmountExVat() != null && row.getVatPercent() != null) {
				tempAmount = amountExVat * plusVatCounter;
				tempVat = tempAmount - amountExVat;
			}
			else if (row.getAmountIncVat() != null && row.getVatPercent() != null) {
				tempAmount = amountIncVat;
				tempVat = tempAmount - (tempAmount / plusVatCounter);
			}
			else {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - amountExVat;
			}

			tempRow.setAmount(MathUtil.convertFromDecimalToCentesimal(tempAmount));
			tempRow.setVat(MathUtil.convertFromDecimalToCentesimal(tempVat));
			tempRow.setQuantity(quantity);

			totalShippingAmount = tempAmount * quantity;
			totalShippingVat = tempVat * quantity;

			totalAmount += totalShippingAmount;
			totalVat += totalShippingVat;

			newRows.add(tempRow);
		}
	}

	private void formatFixedDiscountRows(CreateOrderBuilder orderBuilder) {
		if (orderBuilder.getFixedDiscountRows() == null) {
			return;
		}

		for (FixedDiscountBuilder row : orderBuilder.getFixedDiscountRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, row.getDiscountId());

			double vatFactor = row.getVatPercent() != null ? (row.getVatPercent() * 0.01) + 1 : 0;

			double tempAmount;
			double tempVat;
			double amountExVat = row.getAmountExVat() != null ? row.getAmountExVat() : 0;
			double amountIncVat = row.getAmountIncVat() != null ? row.getAmountIncVat() : 0;

			if (row.getAmountExVat() != null && row.getVatPercent() != null) {
				tempAmount = amountExVat * vatFactor;
				tempVat = amountExVat * (row.getVatPercent() / 100);
			}
			else if (row.getAmountIncVat() != null && row.getVatPercent() != null) {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - (amountIncVat / vatFactor);
			}
			else if (row.getAmountIncVat() != null && row.getAmountExVat() != null) {
				tempAmount = amountIncVat;
				tempVat = amountIncVat - amountExVat;
			}
			else {
				tempAmount = amountIncVat;
				tempVat = totalAmount * totalVat == 0 ? amountIncVat : amountIncVat / totalAmount * totalVat;
			}

			double discountedAmount = -tempAmount;
			tempRow.setAmount(MathUtil.convertFromDecimalToCentesimal(discountedAmount));

			totalAmount += discountedAmount;

			if (totalVat > 0) {
				double discountedVat = -tempVat;
				tempRow.setVat(MathUtil.convertFromDecimalToCentesimal(discountedVat));
				totalVat += discountedVat;
			}

			newRows.add(tempRow);
		}
	}

	private void formatRelativeDiscountRows(CreateOrderBuilder orderBuilder) {
		if (orderBuilder.getRelativeDiscountRows() == null) {
			return;
		}

		for (RelativeDiscountBuilder row : orderBuilder.getRelativeDiscountRows()) {
			HostedOrderRowBuilder tempRow = getNewTempRow(row, row.getDiscountId());

			double discountFactor = row.getDiscountPercent() / 100;

			double discountAmount = (totalAmount - totalShippingAmount) * discountFactor;
			double discountVat = 0;

			totalAmount = totalAmount - discountAmount;

			if (totalVat > 0) {
				discountVat = (totalVat - totalShippingVat) * discountFactor;
				totalVat = totalVat - discountVat;
			}

			tempRow.setAmount(-MathUtil.convertFromDecimalToCentesimal(discountAmount));
			tempRow.setVat(-MathUtil.convertFromDecimalToCentesimal(discountVat));

			newRows.add(tempRow);
		}
	}

	private HostedOrderRowBuilder getNewTempRow(RowBuilder row, String sku) {
		HostedOrderRowBuilder tempRow = new HostedOrderRowBuilder();

		if (row.getName() != null) {
			tempRow.setName(row.getName());
		}

		if (row.getDescription() != null) {
			tempRow.setDescription(row.getDescription());
		}

		tempRow.setQuantity(1.0);
		tempRow.setUnit(row.getUnit());
		tempRow.setSku(sku);

		return tempRow;
	}

	/**
	 * This method is considered imprecise and may return values effected by
	 * rounding errors.
	 * 
	 * @deprecated Use {@link getTotalAmount()} instead...
	 */
	public long formatTotalAmount(ArrayList<HostedOrderRowBuilder> rows) {
		long amount = 0L;

		for (HostedOrderRowBuilder row : rows) {
			amount += (long)(row.getAmount() * (row.getQuantity() == null ? 0.0 : row.getQuantity()));
		}

		return amount;
	}

	/**
	 * This method is considered imprecise and may return values effected by
	 * rounding errors.
	 * 
	 * @deprecated Use {@link getTotalVat()} instead...
	 */
	public long formatTotalVat(ArrayList<HostedOrderRowBuilder> rows) {
		long vat = 0L;

		for (HostedOrderRowBuilder row : rows) {
			vat += (long)(row.getVat() * (row.getQuantity() == null ? 0.0 : row.getQuantity()));
		}

		return vat;
	}

	public long getTotalAmount() {
		return MathUtil.convertFromDecimalToCentesimal(totalAmount);
	}

	public long getTotalVat() {
		return MathUtil.convertFromDecimalToCentesimal(totalVat);
	}
}
