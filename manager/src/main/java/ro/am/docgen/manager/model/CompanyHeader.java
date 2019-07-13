package ro.am.docgen.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.am.docgen.manager.entity.Company;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyHeader {
	private int id;
	private String name;

	public static CompanyHeader ofEntity(Company entity) {
		return new CompanyHeader(entity.getId(), entity.getName());
	}

	public static CompanyHeader ofId(int id) {
		return new CompanyHeader(id, null);
	}
}
