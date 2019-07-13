package ro.am.docgen.manager.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	@Embedded
	private Identification identification;

	@Embedded
	private Address headquarters;

	@Embedded
	private Address office;

	@Embedded
	private BankAccount bankAccount;

	private boolean vat;
	private BigDecimal capital;
	private String email;
	private boolean managed;

}
