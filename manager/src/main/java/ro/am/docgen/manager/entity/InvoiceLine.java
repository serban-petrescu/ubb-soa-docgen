package ro.am.docgen.manager.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class InvoiceLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private BigDecimal quantity;
	private BigDecimal vatRate;
	private BigDecimal unitPrice;
}
