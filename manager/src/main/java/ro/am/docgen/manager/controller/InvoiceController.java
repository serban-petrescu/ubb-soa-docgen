package ro.am.docgen.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ro.am.docgen.manager.entity.Job;
import ro.am.docgen.manager.model.CompanyHeader;
import ro.am.docgen.manager.model.InvoiceHeader;
import ro.am.docgen.manager.model.OutboundInvoice;
import ro.am.docgen.manager.model.SimplePage;
import ro.am.docgen.manager.service.InvoiceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvoiceController {
	private final InvoiceService service;

	@GetMapping("/companies/{companyId}/invoices")
	public SimplePage<InvoiceHeader> readPage(@PathVariable int companyId, @PageableDefault(sort = "id") Pageable pageable) {
		return new SimplePage<>(service.findAllByCompany(pageable, companyId));
	}

	@PostMapping("/companies/{companyId}/invoices")
	public OutboundInvoice create(@PathVariable int companyId, @RequestBody OutboundInvoice invoice) {
		invoice.setCompany(CompanyHeader.ofId(companyId));
		return service.create(invoice);
	}

	@PostMapping("/invoices/generate-batch")
	public Job generateBatch(@RequestBody List<Integer> ids) {
		return service.generateInvoiceDocuments(ids);
	}

	@PostMapping("/companies/{companyId}/invoices/{invoiceId}/generate")
	public Job generate(@PathVariable int invoiceId) {
		return service.generateInvoiceDocument(invoiceId);
	}

	@PutMapping("/companies/{companyId}/invoices/{invoiceId}")
	public OutboundInvoice update(@PathVariable int companyId, @PathVariable int invoiceId, @RequestBody OutboundInvoice invoice) {
		invoice.setCompany(CompanyHeader.ofId(companyId));
		return service.update(invoiceId, invoice);
	}

	@GetMapping("/companies/{companyId}/invoices/{invoiceId}")
	public OutboundInvoice readOne(@PathVariable int invoiceId) {
		return service.findOne(invoiceId);
	}
}
