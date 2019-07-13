package ro.am.docgen.manager.repository;

import org.springframework.stereotype.Component;
import ro.am.docgen.manager.entity.Job;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryJobRepository implements JobRepository {
	private final Map<String, Job> map;

	public InMemoryJobRepository() {
		this.map = new ConcurrentHashMap<>();
	}

	@Override
	public void save(Job job) {
		map.put(job.getId(), job);
	}

	@Override
	public Optional<Job> findById(String id) {
		return Optional.ofNullable(map.get(id));
	}
}
