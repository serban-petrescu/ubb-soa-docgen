package ro.am.docgen.manager.model;

import lombok.Data;
import ro.am.docgen.manager.entity.Company;

import java.math.BigDecimal;

@Data
public class ExtendedCompany {
	private String name;
	private String identificationNumber;
	private String registrationNumber;
	private String headquarters;
	private String office;
	private String bank;
	private String iban;
	private String email;
	private BigDecimal capital;
	private boolean vat;

	public static ExtendedCompany ofEntity(Company company) {
		ExtendedCompany extendedCompany = new ExtendedCompany();
		extendedCompany.setName(company.getName());
		extendedCompany.setIdentificationNumber(company.getIdentification().getNumber());
		extendedCompany.setRegistrationNumber(company.getIdentification().getRegistration());
		extendedCompany.setHeadquarters(String.valueOf(company.getHeadquarters()));
		extendedCompany.setOffice(String.valueOf(company.getOffice()));
		extendedCompany.setBank(company.getBankAccount().getBank());
		extendedCompany.setIban(company.getBankAccount().getIban());
		extendedCompany.setEmail(company.getEmail());
		extendedCompany.setCapital(company.getCapital());
		extendedCompany.setVat(company.isVat());
		return extendedCompany;

	}
}
