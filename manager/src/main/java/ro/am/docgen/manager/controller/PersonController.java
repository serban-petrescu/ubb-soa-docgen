package ro.am.docgen.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ro.am.docgen.manager.entity.Person;
import ro.am.docgen.manager.model.PersonHeader;
import ro.am.docgen.manager.model.SimplePage;
import ro.am.docgen.manager.model.SimpleResult;
import ro.am.docgen.manager.model.SuggestCriteria;
import ro.am.docgen.manager.service.PersonService;

@RestController
@RequiredArgsConstructor
public class PersonController {
	private final PersonService service;

	@GetMapping("/persons/{id}")
	public Person readOne(@PathVariable int id) {
		return service.findOne(id);
	}

	@GetMapping("/person-suggestions")
	public SimpleResult<PersonHeader> readSuggestions(SuggestCriteria criteria) {
		return new SimpleResult<>(service.suggestByName(criteria.getName()));
	}

	@GetMapping("/persons")
	public SimplePage<PersonHeader> readPage(@PageableDefault(sort = "id") Pageable pageable) {
		return new SimplePage<>(service.findAll(pageable));
	}

	@PostMapping("/persons")
	public Person create(@RequestBody Person person) {
		return service.create(person);
	}

	@PutMapping("/persons/{id}")
	public Person update(@PathVariable int id, @RequestBody Person person) {
		person.setId(id);
		return service.update(person);
	}
}
