package ro.am.docgen.manager.repository;

import ro.am.docgen.manager.entity.Job;

import java.util.Optional;

public interface JobRepository {
	void save(Job job);

	Optional<Job> findById(String id);
}
