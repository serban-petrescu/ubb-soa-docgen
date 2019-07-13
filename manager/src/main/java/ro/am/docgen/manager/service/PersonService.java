package ro.am.docgen.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.am.docgen.manager.entity.Person;
import ro.am.docgen.manager.exception.NotFoundException;
import ro.am.docgen.manager.model.PersonHeader;
import ro.am.docgen.manager.repository.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
	private final PersonRepository repository;

	public Person findOne(int id) {
		return repository.findById(id).orElseThrow(NotFoundException::new);
	}

	public Person create(Person person) {
		return repository.save(person);
	}

	public Person update(Person person) {
		return repository.save(person);
	}

	public List<PersonHeader> suggestByName(String name) {
		return repository.findTop10ByNameContainingIgnoreCase(name).stream()
				.map(PersonHeader::ofEntity)
				.collect(Collectors.toList());
	}

	public Page<PersonHeader> findAll(Pageable pageable) {
		return repository.findAll(pageable).map(PersonHeader::ofEntity);
	}
}
