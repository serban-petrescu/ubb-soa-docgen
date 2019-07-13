package ro.am.docgen.manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import ro.am.docgen.manager.entity.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

	Optional<Company> findById(int id);

	List<Company> findTop10ByNameContainingIgnoreCase(String name);

	Page<Company> findAll(Pageable pageable);

	Page<Company> findAllByManagedTrue(Pageable pageable);

}
