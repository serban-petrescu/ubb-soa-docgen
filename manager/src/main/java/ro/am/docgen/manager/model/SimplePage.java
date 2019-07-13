package ro.am.docgen.manager.model;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class SimplePage<E> {
	private final int page;
	private final int size;
	private final int totalPages;
	private final List<E> content;

	public SimplePage(Page<E> page){
		this.page = page.getNumber();
		this.size = page.getSize();
		this.totalPages = page.getTotalPages();
		this.content = page.getContent();
	}
}
