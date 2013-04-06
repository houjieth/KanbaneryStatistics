package net.jiehou.tools;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class FinishedTask {
	public long taskId;
	public String title;
	public String typeName;
	public float point;
	public DateTime deadline;
	public DateTime finishDate;
	public int spill;
	
	public FinishedTask(long taskId, String title, String typeName, float point, DateTime deadline, DateTime finishDate) {
		this.taskId = taskId;
		this.title = title;
		this.typeName = typeName;
		this.point = point;
		this.deadline = deadline;
		this.finishDate = finishDate;
		if(deadline != null)
			this.spill = Days.daysBetween(deadline, finishDate).getDays();
		else
			this.spill = Days.MAX_VALUE.getDays();
	}
}
