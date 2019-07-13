package ro.am.docgen.manager.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ExtendedTotal {
	private BigDecimal withTax;
	private BigDecimal withoutTax;
	private BigDecimal tax;

	public static ExtendedTotal fromLines(List<ExtendedInvoiceLine> lines) {
		ExtendedTotal total = new ExtendedTotal();
		total.setTax(lines.stream().map(ExtendedInvoiceLine::getTax)
				.reduce(BigDecimal.ZERO, BigDecimal::add));
		total.setWithoutTax(lines.stream().map(ExtendedInvoiceLine::getPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add));
		total.setWithTax(total.getWithoutTax().add(total.getTax()));
		return total;
	}
}
