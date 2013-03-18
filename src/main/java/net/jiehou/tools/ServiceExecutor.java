package net.jiehou.tools;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ServiceExecutor {
	private ServiceHelper service;
	
	public ServiceExecutor(ServiceHelper service) {
		this.service = service;
	}
	
	public float calculatePointsForFinishedTasks() throws IOException, ExecutionException, InterruptedException {
		return service.getPointsForFinishedTasks();
	}
	
	public float calculatelPointsForFinishedTasksOfType(String typeName) throws IOException, ExecutionException, InterruptedException {
		return service.getPointsForFinishedTasksOfType(typeName);
	}
	
	public List<FinishedTask> calculateAllFinishedTasks() throws IOException {
		return service.getAllFinishedTasks();
	}
}
