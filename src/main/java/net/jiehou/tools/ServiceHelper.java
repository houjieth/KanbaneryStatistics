package net.jiehou.tools;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.joda.time.DateTime;

import pl.project13.janbanery.core.Janbanery;
import pl.project13.janbanery.core.JanbaneryFactory;
import pl.project13.janbanery.core.dao.Archive;
import pl.project13.janbanery.core.dao.Columns;
import pl.project13.janbanery.core.dao.Estimates;
import pl.project13.janbanery.core.dao.Projects;
import pl.project13.janbanery.core.dao.TaskTypes;
import pl.project13.janbanery.resources.Column;
import pl.project13.janbanery.resources.Project;
import pl.project13.janbanery.resources.Task;

public class ServiceHelper {
	private Janbanery janbanery;

	private static final String WORKSPACE_NAME = "jiehou";
	private static final String DONE_COLUMN_NAME = "Done";
	
	public ServiceHelper(String key) throws IOException, ExecutionException, InterruptedException {
		janbanery = new JanbaneryFactory().connectUsing(key);
		janbanery.usingWorkspace(WORKSPACE_NAME);
	}
	
	public void close() {
		janbanery.close();
	}
	
	/**
	 * Get points for all finished tasks, including the task in Done column and Archive
	 * @return points sum
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public float getPointsForFinishedTasks() throws IOException, ExecutionException, InterruptedException {
		float count = 0;
		Projects projects = janbanery.projects();
		for(Project project : projects.all()) {
			janbanery.usingProject(project.getName());
			Estimates estimates = janbanery.estimates();
			
			// count points in Done column
			Columns columns = janbanery.columns();
			Column column = columns.byName(DONE_COLUMN_NAME).get(0);
			List<Task> tasks = janbanery.tasks().allIn(column);
			for(Task task: tasks) {
				Long estimateId = task.getEstimateId();
				if(estimateId != null) {
					float point = estimates.byId(estimateId).getValue().floatValue();
					// System.out.println(task.getTitle() + point);
					count += point;
				}
			}
			// count points in Archive
			Archive archive = janbanery.archive();
			tasks = archive.all();
			for(Task task: tasks) {
				Long estimateId = task.getEstimateId();
				if(estimateId != null) {
					float point = estimates.byId(estimateId).getValue().floatValue();
					// System.out.println(task.getTitle() + point);
					count += point;
				}
			}
		}
		// System.out.println(count);
		return count;
	}
	
	/**
	 * Get points for finished tasks of specified type
	 * @param typeName
	 * @return
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public float getPointsForFinishedTasksOfType(String typeName) throws IOException, ExecutionException, InterruptedException {
		float count = 0;
		Projects projects = janbanery.projects();
		for(Project project : projects.all()) {
			janbanery.usingProject(project.getName());
			Estimates estimates = janbanery.estimates();
			TaskTypes types = janbanery.taskTypes();
			
			// count points in Done column
			Columns columns = janbanery.columns();
			Column column = columns.byName(DONE_COLUMN_NAME).get(0);
			List<Task> tasks = janbanery.tasks().allIn(column);
			for(Task task: tasks) {
				Long typeId = task.getTaskTypeId();
				if(!types.byId(typeId).getName().equals(typeName))
					continue;
				Long estimateId = task.getEstimateId();
				if(estimateId != null) {
					float point = estimates.byId(estimateId).getValue().floatValue();
					// System.out.println(task.getTitle() + point);
					count += point;
				}
			}
			// count points in Archive
			Archive archive = janbanery.archive();
			tasks = archive.all();
			for(Task task: tasks) {
				Long typeId = task.getTaskTypeId();
				if(!types.byId(typeId).getName().equals(typeName))
					continue;
				Long estimateId = task.getEstimateId();
				if(estimateId != null) {
					float point = estimates.byId(estimateId).getValue().floatValue();
					// System.out.println(task.getTitle() + point);
					count += point;
				}
			}
		}
		// System.out.println(count);
		return count;
	}
	
	public List<FinishedTask> getAllFinishedTasks() throws IOException {
		List<FinishedTask> finishedTasks = new ArrayList<FinishedTask>();
		Projects projects = janbanery.projects();
		for(Project project : projects.all()) {
			janbanery.usingProject(project.getName());
			Estimates estimates = janbanery.estimates();
			TaskTypes types = janbanery.taskTypes();
			
			// count points in Done column
			Columns columns = janbanery.columns();
			Column column = columns.byName(DONE_COLUMN_NAME).get(0);
			List<Task> tasks = janbanery.tasks().allIn(column);
			for(Task task: tasks) {
				long taskId = task.getId();
				String title = task.getTitle();
				Long estimateId = task.getEstimateId();
				float point = 0;
				DateTime deadline = null, finishDate = null;
				if(estimateId != null)
					point = estimates.byId(estimateId).getValue().floatValue();
				if(task.getDeadline() != null)
					deadline = new DateTime(task.getDeadline());
				finishDate = new DateTime();
				Long typeId = task.getTaskTypeId();
				String typeName = types.byId(typeId).getName();
				FinishedTask finishedTask = new FinishedTask(
					taskId, title, typeName, point, deadline, finishDate);
				finishedTasks.add(finishedTask);
			}
			// count points in Archive
			Archive archive = janbanery.archive();
			tasks = archive.all();
			for(Task task: tasks) {
				long taskId = task.getId();
				String title = task.getTitle();
				Long estimateId = task.getEstimateId();
				float point = 0;
				DateTime deadline = null, finishDate = null;
				if(estimateId != null) 
					point = estimates.byId(estimateId).getValue().floatValue();
				if(task.getDeadline() != null)
					deadline = new DateTime(task.getDeadline());
				finishDate = new DateTime();
				Long typeId = task.getTaskTypeId();
				String typeName = types.byId(typeId).getName();
				FinishedTask finishedTask = new FinishedTask(
					taskId, title, typeName, point, deadline, finishDate);
				finishedTasks.add(finishedTask);
			}
		}
		return finishedTasks;
	}
	
}