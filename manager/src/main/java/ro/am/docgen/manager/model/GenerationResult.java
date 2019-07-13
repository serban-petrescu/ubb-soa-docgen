package ro.am.docgen.manager.model;

import lombok.Data;

@Data
public class GenerationResult {
	private String jobId;
	private String documentId;
	private boolean success;
}
