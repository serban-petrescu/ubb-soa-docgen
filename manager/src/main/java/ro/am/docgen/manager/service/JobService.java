package ro.am.docgen.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ro.am.docgen.manager.entity.Job;
import ro.am.docgen.manager.entity.JobStatus;
import ro.am.docgen.manager.model.GenerationResult;
import ro.am.docgen.manager.repository.JobRepository;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@RabbitListener(queues = "results")
public class JobService {
	private final JobRepository repository;
	private final ObjectMapper objectMapper;

	@RabbitHandler
	public void handleResult(@Payload byte[] payload) throws IOException {
		GenerationResult result = objectMapper.readValue(payload, GenerationResult.class);
		Optional<Job> optional = repository.findById(result.getJobId());
		if (optional.isPresent()) {
			Job job = optional.get();
			job.setStatus(result.isSuccess() ? JobStatus.FINISHED : JobStatus.ERROR);
			repository.save(job);
		}
	}
}
