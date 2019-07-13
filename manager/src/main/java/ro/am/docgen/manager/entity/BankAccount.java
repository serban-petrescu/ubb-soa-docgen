package ro.am.docgen.manager.entity;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class BankAccount {
	private String bank;
	private String iban;
}
