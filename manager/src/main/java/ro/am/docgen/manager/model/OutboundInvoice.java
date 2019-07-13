package ro.am.docgen.manager.model;

import lombok.Data;
import ro.am.docgen.manager.entity.Invoice;
import ro.am.docgen.manager.entity.InvoiceLine;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OutboundInvoice {
	private int id;
	private String series;
	private int number;
	private LocalDate issueDate;
	private LocalDate dueDate;
	private CompanyHeader company;
	private CompanyHeader customer;
	private PersonHeader delegate;
	private List<InvoiceLine> lines;

	public static OutboundInvoice ofEntity(Invoice entity) {
		OutboundInvoice outboundInvoice = new OutboundInvoice();
		outboundInvoice.setId(entity.getId());
		outboundInvoice.setSeries(entity.getSeries());
		outboundInvoice.setNumber(entity.getNumber());
		outboundInvoice.setIssueDate(entity.getIssueDate());
		outboundInvoice.setDueDate(entity.getDueDate());
		outboundInvoice.setCompany(CompanyHeader.ofEntity(entity.getCompany()));
		outboundInvoice.setCustomer(CompanyHeader.ofEntity(entity.getCustomer()));
		outboundInvoice.setDelegate(entity.getDelegate() != null ? PersonHeader.ofEntity(entity.getDelegate()) : null);
		outboundInvoice.setLines(entity.getLines().stream()
				.sorted(Comparator.comparingInt(InvoiceLine::getId))
				.collect(Collectors.toList()));
		return outboundInvoice;
	}
}
