package net.neferett.linaris.utils.tasks;

import java.util.concurrent.LinkedBlockingQueue;

public class TasksExecutor implements Runnable {

	private final LinkedBlockingQueue<PendingTask> pending = new LinkedBlockingQueue<>();

	public void addTask(final PendingTask message) {
		this.pending.add(message);
	}

	@Override
	public void run() {
		while (true)
			try {
				this.pending.take().run();
			} catch (final Exception e) {
				e.printStackTrace();
			}
	}
}
