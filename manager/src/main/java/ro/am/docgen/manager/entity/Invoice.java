package ro.am.docgen.manager.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String series;
	private int number;
	private LocalDate issueDate;
	private LocalDate dueDate;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Company company;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Company customer;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Person delegate;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "invoice_id")
	private List<InvoiceLine> lines;
}
