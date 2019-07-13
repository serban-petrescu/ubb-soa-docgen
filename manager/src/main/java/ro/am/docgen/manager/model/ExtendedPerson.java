package ro.am.docgen.manager.model;

import lombok.Data;
import ro.am.docgen.manager.entity.Person;

@Data
public class ExtendedPerson {
	private String name;
	private String personalNumber;
	private String address;

	public static ExtendedPerson ofEntity(Person person) {
		ExtendedPerson extendedPerson = new ExtendedPerson();
		extendedPerson.setName(person.getName());
		extendedPerson.setPersonalNumber(person.getPersonalNumber());
		extendedPerson.setAddress(String.valueOf(person.getAddress()));
		return extendedPerson;
	}
}
