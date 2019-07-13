package ro.am.docgen.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.am.docgen.manager.entity.Invoice;
import ro.am.docgen.manager.entity.Job;
import ro.am.docgen.manager.entity.JobStatus;
import ro.am.docgen.manager.exception.NotFoundException;
import ro.am.docgen.manager.model.*;
import ro.am.docgen.manager.repository.CompanyRepository;
import ro.am.docgen.manager.repository.InvoiceRepository;
import ro.am.docgen.manager.repository.JobRepository;
import ro.am.docgen.manager.repository.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {
	private final InvoiceRepository invoiceRepository;
	private final PersonRepository personRepository;
	private final CompanyRepository companyRepository;
	private final RabbitTemplate rabbitTemplate;
	private final JobRepository jobRepository;

	public OutboundInvoice findOne(int id) {
		return OutboundInvoice.ofEntity(invoiceRepository.findById(id).orElseThrow(NotFoundException::new));
	}

	public OutboundInvoice create(OutboundInvoice inbound) {
		return OutboundInvoice.ofEntity(invoiceRepository.save(fromInbound(inbound)));
	}

	public OutboundInvoice update(int id, OutboundInvoice inbound) {
		Invoice invoice = fromInbound(inbound);
		invoice.setId(id);
		return OutboundInvoice.ofEntity(invoiceRepository.save(invoice));
	}

	public Page<InvoiceHeader> findAllByCompany(Pageable pageable, int companyId) {
		return invoiceRepository.findAllByCompanyId(pageable, companyId).map(InvoiceHeader::ofEntity);
	}

	public Job generateInvoiceDocument(int id) {
		Invoice invoice = invoiceRepository.findById(id).orElseThrow(NotFoundException::new);
		ExtendedInvoice extended = ExtendedInvoice.ofEntity(invoice);
		String jobId = UUID.randomUUID().toString();
		String fileName = invoice.getSeries() + "-" + invoice.getNumber() + "-" + invoice.getIssueDate().getYear();
		rabbitTemplate.convertAndSend("documents", new GenerateRequest(jobId, "invoice", fileName, extended));
		Job job = new Job.Simple(jobId, JobStatus.OPEN);
		jobRepository.save(job);
		return job;
	}

	public Job generateInvoiceDocuments(List<Integer> ids) {
		String jobId = UUID.randomUUID().toString();
		Job job = new Job.Composite(jobId, ids.stream().map(this::generateInvoiceDocument).collect(Collectors.toList()));
		jobRepository.save(job);
		return job;
	}

	private Invoice fromInbound(OutboundInvoice inbound) {
		Invoice invoice = new Invoice();
		invoice.setSeries(inbound.getSeries());
		invoice.setNumber(inbound.getNumber());
		invoice.setIssueDate(inbound.getIssueDate());
		invoice.setDueDate(inbound.getDueDate());
		invoice.setCompany(Optional.ofNullable(inbound.getCompany())
				.map(CompanyHeader::getId)
				.flatMap(companyRepository::findById)
				.orElseThrow(NotFoundException::new));
		invoice.setCustomer(Optional.ofNullable(inbound.getCustomer())
				.map(CompanyHeader::getId)
				.flatMap(companyRepository::findById)
				.orElseThrow(NotFoundException::new));
		if (inbound.getDelegate() != null) {
			invoice.setDelegate(Optional.ofNullable(inbound.getDelegate())
					.map(PersonHeader::getId)
					.flatMap(personRepository::findById)
					.orElse(null));
		}
		invoice.setLines(inbound.getLines());
		return invoice;
	}
}
