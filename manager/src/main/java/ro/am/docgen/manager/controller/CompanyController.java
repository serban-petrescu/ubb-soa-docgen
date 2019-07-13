package ro.am.docgen.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ro.am.docgen.manager.entity.Company;
import ro.am.docgen.manager.model.CompanyHeader;
import ro.am.docgen.manager.model.SimplePage;
import ro.am.docgen.manager.model.SimpleResult;
import ro.am.docgen.manager.model.SuggestCriteria;
import ro.am.docgen.manager.service.CompanyService;

@RestController
@RequiredArgsConstructor
public class CompanyController {
	private final CompanyService service;

	@GetMapping("/company-suggestions")
	public SimpleResult<CompanyHeader> readSuggestions(SuggestCriteria criteria) {
		return new SimpleResult<>(service.suggestByName(criteria.getName()));
	}

	@GetMapping("/companies")
	public SimplePage<CompanyHeader> readPage(@PageableDefault(sort = "id") Pageable pageable,
											  @RequestParam(name = "only-managed", defaultValue = "false")
													  boolean onlyManaged) {
		return new SimplePage<>(service.findAll(pageable, onlyManaged));
	}

	@GetMapping("/companies/{id}")
	public Company readOne(@PathVariable int id) {
		return service.findOne(id);
	}

	@PostMapping("/companies")
	public Company create(@RequestBody Company company) {
		return service.create(company);
	}

	@PutMapping("/companies/{id}")
	public Company update(@PathVariable int id, @RequestBody Company company) {
		company.setId(id);
		return service.update(company);
	}
}
