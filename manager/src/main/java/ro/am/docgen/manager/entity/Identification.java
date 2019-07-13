package ro.am.docgen.manager.entity;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Identification {
	private String number;
	private String registration;
}
