package ro.am.docgen.manager.entity;

import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Address {
	private String country;
	private String region;
	private String streetAddress;
	private String postalCode;

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (!StringUtils.isEmpty(streetAddress)) {
			builder.append(streetAddress);
		}
		if (!StringUtils.isEmpty(region)) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(region);
		}
		if (!StringUtils.isEmpty(postalCode)) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(postalCode);
		}
		if (!StringUtils.isEmpty(country)) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(country);
		}
		return builder.toString();
	}
}
