package ro.am.docgen.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRequest {
	private String jobId;
	private String template;
	private String fileName;
	private Object data;
}
