package ro.am.docgen.manager.entity;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public interface Job {

	String getId();

	JobStatus getStatus();

	void setStatus(JobStatus status);

	void addListener(Consumer<Job> listener);

	@Getter
	class Simple implements Job {
		private String id;
		protected JobStatus status;
		private List<Consumer<Job>> listeners;

		public Simple(String id, JobStatus status) {
			this.id = id;
			this.status = status;
			this.listeners = new LinkedList<>();
		}

		@Override
		public void setStatus(JobStatus status) {
			this.status = status;
			listeners.forEach(l -> l.accept(this));
		}

		@Override
		public void addListener(Consumer<Job> listener) {
			this.listeners.add(listener);
		}
	}


	@Getter
	class Composite extends Simple {
		private final List<Job> children;

		public Composite(String id, List<Job> children) {
			super(id, JobStatus.OPEN);
			this.children = children;
			children.forEach(c -> c.addListener(this::onStatusChange));
		}

		private void onStatusChange(Job job) {
			if (job.getStatus() != JobStatus.OPEN && children.stream().allMatch(j -> j.getStatus() != JobStatus.OPEN)) {
				this.setStatus(JobStatus.FINISHED);
			}
		}
	}
}
