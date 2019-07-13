package ro.am.docgen.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ro.am.docgen.manager.entity.GeneratedDocument;

import java.util.List;
import java.util.Optional;

public interface GeneratedDocumentRepository extends MongoRepository<GeneratedDocument, String> {
	Optional<GeneratedDocument> findByJobId(String id);
	List<GeneratedDocument> findByJobIdIn(List<String> ids);
}
