package ro.am.docgen.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.am.docgen.manager.entity.Person;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonHeader {
	private int id;
	private String name;

	public static PersonHeader ofEntity(Person entity) {
		return new PersonHeader(entity.getId(), entity.getName());
	}
}
