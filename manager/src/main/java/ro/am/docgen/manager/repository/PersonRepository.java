package ro.am.docgen.manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.am.docgen.manager.entity.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {

	Optional<Person> findById(int id);

	List<Person> findTop10ByNameContainingIgnoreCase(String name);

	Page<Person> findAll(Pageable pageable);
}
