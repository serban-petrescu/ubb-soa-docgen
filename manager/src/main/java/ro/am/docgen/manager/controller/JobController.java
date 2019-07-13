package ro.am.docgen.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ro.am.docgen.manager.entity.GeneratedDocument;
import ro.am.docgen.manager.entity.Job;
import ro.am.docgen.manager.exception.NotFoundException;
import ro.am.docgen.manager.repository.GeneratedDocumentRepository;
import ro.am.docgen.manager.repository.JobRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
public class JobController {
	private static final String DOCX_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	private static final String ZIP_TYPE = "application/zip";

	private final JobRepository jobRepository;
	private final GeneratedDocumentRepository documentRepository;

	@GetMapping("/jobs/{id}")
	public Job readOne(@PathVariable String id) {
		return jobRepository.findById(id).orElseThrow(NotFoundException::new);
	}

	@GetMapping("/jobs/{id}/download")
	public ResponseEntity<byte[]> readGeneratedDocument(@PathVariable String id) throws IOException {
		Job job = jobRepository.findById(id).orElseThrow(NotFoundException::new);
		if (job instanceof Job.Composite) {
			return readCompositeJobDocument((Job.Composite) job);
		} else if (job instanceof Job.Simple) {
			return readSimpleJobDocument((Job.Simple) job);
		} else {
			throw new IllegalStateException();
		}
	}

	private ResponseEntity<byte[]> readSimpleJobDocument(Job.Simple job) {
		GeneratedDocument document = documentRepository.findByJobId(job.getId()).orElseThrow(NotFoundException::new);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(DOCX_TYPE));
		headers.setContentDisposition(ContentDisposition.builder("attachment")
				.filename(document.getFileName() + ".docx")
				.build());
		return new ResponseEntity<>(document.getData(), headers, HttpStatus.OK);
	}

	private ResponseEntity<byte[]> readCompositeJobDocument(Job.Composite job) throws IOException {
		List<GeneratedDocument> documents = documentRepository.findByJobIdIn(job.getChildren().stream()
				.map(Job::getId)
				.collect(Collectors.toList()));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(ZIP_TYPE));
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename("invoices.zip").build());
		return new ResponseEntity<>(generateZipFromDocuments(documents), headers, HttpStatus.OK);
	}

	private byte[] generateZipFromDocuments(List<GeneratedDocument> documents) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			for (GeneratedDocument document : documents) {
				zos.putNextEntry(new ZipEntry(document.getFileName() + ".docx"));
				zos.write(document.getData());
				zos.closeEntry();
			}
		}
		return baos.toByteArray();
	}
}
