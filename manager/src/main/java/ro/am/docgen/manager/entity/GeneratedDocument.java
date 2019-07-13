package ro.am.docgen.manager.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "documents")
public class GeneratedDocument {
	@Id
	private String id;
	private String jobId;
	private byte[] data;
	private String fileName;
}
