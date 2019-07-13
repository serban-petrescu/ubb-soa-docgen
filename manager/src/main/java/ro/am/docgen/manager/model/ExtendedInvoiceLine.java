package ro.am.docgen.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.am.docgen.manager.entity.InvoiceLine;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedInvoiceLine {
	private int index;
	private String name;
	private BigDecimal quantity;
	private BigDecimal tax;
	private BigDecimal unitPrice;
	private BigDecimal price;

	public static ExtendedInvoiceLine ofEntity(InvoiceLine entity) {
		ExtendedInvoiceLine extendedInvoiceLine = new ExtendedInvoiceLine();
		extendedInvoiceLine.setName(entity.getName());
		extendedInvoiceLine.setQuantity(entity.getQuantity());
		extendedInvoiceLine.setTax(entity.getVatRate().multiply(entity.getUnitPrice()).multiply(entity.getQuantity()));
		extendedInvoiceLine.setUnitPrice(entity.getUnitPrice());
		extendedInvoiceLine.setPrice(entity.getUnitPrice().multiply(entity.getQuantity()));
		return extendedInvoiceLine;
	}
}
