package ro.am.docgen.manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import ro.am.docgen.manager.entity.Invoice;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	Optional<Invoice> findById(int id);

	Page<Invoice> findAllByCompanyId(Pageable pageable, int companyId);
}
