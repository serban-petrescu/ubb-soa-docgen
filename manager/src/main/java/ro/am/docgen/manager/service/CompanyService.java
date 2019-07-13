package ro.am.docgen.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.am.docgen.manager.entity.Company;
import ro.am.docgen.manager.exception.NotFoundException;
import ro.am.docgen.manager.model.CompanyHeader;
import ro.am.docgen.manager.repository.CompanyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
	private final CompanyRepository repository;

	public Company findOne(int id) {
		return repository.findById(id).orElseThrow(NotFoundException::new);
	}

	public Company create(Company company) {
		return repository.save(company);
	}

	public Company update(Company company) {
		return repository.save(company);
	}

	public List<CompanyHeader> suggestByName(String name) {
		return repository.findTop10ByNameContainingIgnoreCase(name).stream()
				.map(CompanyHeader::ofEntity)
				.collect(Collectors.toList());
	}

	public Page<CompanyHeader> findAll(Pageable pageable, boolean onlyManaged) {
		return (onlyManaged ? repository.findAllByManagedTrue(pageable) : repository.findAll(pageable))
				.map(CompanyHeader::ofEntity);
	}
}
