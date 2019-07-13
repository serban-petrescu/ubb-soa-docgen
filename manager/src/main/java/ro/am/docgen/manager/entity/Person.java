package ro.am.docgen.manager.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;
	private String personalNumber;
	@Embedded
	private Address address;
}
