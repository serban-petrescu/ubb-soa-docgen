package ro.am.docgen.manager.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "application_user")
public class User {
	@Id
	private String username;
	private String password;
	private boolean enabled;
}
