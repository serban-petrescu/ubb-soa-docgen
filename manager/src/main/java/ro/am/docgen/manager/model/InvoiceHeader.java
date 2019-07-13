package ro.am.docgen.manager.model;

import lombok.Data;
import ro.am.docgen.manager.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceHeader {
	private final int id;
	private final String series;
	private final int number;
	private final LocalDate issueDate;
	private final BigDecimal total;

	public static InvoiceHeader ofEntity(Invoice entity) {
		return new InvoiceHeader(entity.getId(), entity.getSeries(), entity.getNumber(), entity.getIssueDate(),
				entity.getLines().stream()
						.map(l -> l.getQuantity().multiply(l.getUnitPrice()).multiply(BigDecimal.ONE.add(l.getVatRate())))
						.reduce(BigDecimal.ZERO, BigDecimal::add));
	}
}
