package ro.am.docgen.manager.model;

import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;
import ro.am.docgen.manager.entity.Invoice;
import ro.am.docgen.manager.entity.InvoiceLine;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ExtendedInvoice {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private String series;
	private int number;
	private String issueDate;
	private String dueDate;
	private ExtendedCompany company;
	private ExtendedCompany customer;
	private ExtendedPerson delegate;
	private List<ExtendedInvoiceLine> lines;
	private ExtendedTotal total;
	private String author;

	public static ExtendedInvoice ofEntity(Invoice invoice) {
		ExtendedInvoice extendedInvoice = new ExtendedInvoice();
		extendedInvoice.setSeries(invoice.getSeries());
		extendedInvoice.setNumber(invoice.getNumber());
		extendedInvoice.setIssueDate(invoice.getIssueDate().format(DATE_FORMATTER));
		extendedInvoice.setDueDate(invoice.getIssueDate().format(DATE_FORMATTER));
		extendedInvoice.setCompany(ExtendedCompany.ofEntity(invoice.getCompany()));
		extendedInvoice.setCustomer(ExtendedCompany.ofEntity(invoice.getCustomer()));
		extendedInvoice.setDelegate(invoice.getDelegate() != null ?
				ExtendedPerson.ofEntity(invoice.getDelegate()) : null);
		extendedInvoice.setLines(invoice.getLines().stream()
				.sorted(Comparator.comparingInt(InvoiceLine::getId))
				.map(ExtendedInvoiceLine::ofEntity)
				.collect(Collectors.toList()));
		int index = 1;
		for (ExtendedInvoiceLine line : extendedInvoice.getLines()) {
			line.setIndex(index);
			index++;
		}
		extendedInvoice.setTotal(ExtendedTotal.fromLines(extendedInvoice.getLines()));
		extendedInvoice.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
		return extendedInvoice;

	}
}
