package net.jiehou.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class DatabaseExecutor {
	
	private Connection conn;
	
	public DatabaseExecutor(String filepath) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + filepath);
	}
	
	public void close() throws SQLException {
		conn.close();
	}
	
	public void appendSumsTable(SumsTableEntry e) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Statement stat = conn.createStatement();
		stat.executeUpdate("create table if not exists sums(" +
				"statTime date," +
				"A float," +
				"S float," +
				"T float," +
				"P float," +
				"I float," +
				"O float," +
				"M float," +
				"MM float," +
				"GW float," +
				"LI float," +
				"BC float," +
				"SA float," +
				"RM float);");
		
		PreparedStatement stat2 = conn.prepareStatement("insert into sums values(" +
				"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
		stat2.setDate(1, new java.sql.Date(new Date().getTime()));
		stat2.setFloat(2, e.sumAll);
		stat2.setFloat(3, e.sumS);
		stat2.setFloat(4, e.sumT);
		stat2.setFloat(5, e.sumP);
		stat2.setFloat(6, e.sumI);
		stat2.setFloat(7, e.sumO);
		stat2.setFloat(8, e.sumM);
		stat2.setFloat(9, e.sumMM);
		stat2.setFloat(10, e.sumGW);
		stat2.setFloat(11, e.sumLI);
		stat2.setFloat(12, e.sumBC);
		stat2.setFloat(13, e.sumSA);
		stat2.setFloat(14, e.sumRM);
		stat2.execute();
	}
	
	public void updateTasksTable(List<FinishedTask> tasks) throws SQLException {
		Statement stat = conn.createStatement();
		stat.executeUpdate("create table if not exists tasks(" +
				"taskId bigint," +
				"title varchar(255)," +
				"type varchar(255)," +
				"point float," +
				"deadline date," +
				"finishDate date," +
				"spill integer);");
		PreparedStatement stat2 = conn.prepareStatement("insert into tasks values(?, ?, ?, ?, ?, ?, ?);");
		for(FinishedTask task : tasks) {
			ResultSet result = stat.executeQuery("select * from tasks where taskId = " + task.taskId);
			if(result.next())
				continue; // don't change existing task
			stat2.setLong(1, task.taskId);
			stat2.setString(2, task.title);
			stat2.setString(3, task.typeName);
			stat2.setFloat(4, task.point);
			stat2.setDate(5, new java.sql.Date(task.deadline.toDate().getTime()));
			stat2.setDate(6, new java.sql.Date(task.finishDate.toDate().getTime()));
			stat2.setInt(7, task.spill);
			stat2.execute();
		}
	}
	
	public float getAllTimeSum(String columnName) throws SQLException {
		Statement stat = conn.createStatement();
		ResultSet result = stat.executeQuery("select * from sums order by " +
				"statTime desc limit 1");
		return result.getFloat(columnName);
	}
	
	public float getOneDaySpeed(String columnName) throws SQLException {
		Statement stat = conn.createStatement();
		ResultSet result = stat.executeQuery("select * from sums order by " +
				"statTime desc limit 2");
		result.next(); // doesn't move iterator when next() is called in the first time
		float todayPoints = result.getFloat(columnName);
		result.next();
		float yesterdayPoints = result.getFloat(columnName);
		return todayPoints - yesterdayPoints;
	}
	
	public float getSevenDaySpeed(String columnName) throws SQLException {
		Statement stat = conn.createStatement();
		ResultSet result = stat.executeQuery("select * from sums order by " +
				"statTime desc limit 8");
		result.next(); // doesn't move iterator when next() is called in the first time
		float todayPoints = result.getFloat(columnName);
		float oneWeekAgoPoints = 0;
		while(result.next()) {
			oneWeekAgoPoints = result.getFloat(columnName);
		}
		return (todayPoints - oneWeekAgoPoints) / 7;
	}
	
	public float getAllTimeSpeed(String columnName) throws SQLException {
		Statement stat = conn.createStatement();
		ResultSet result = stat.executeQuery("select * from sums order by " +
				"statTime desc limit 1");
		result.next(); // doesn't move iterator when next() is called in the first time
		float todayPoints = result.getFloat(columnName);
		int days = Days.daysBetween(new DateTime(2013, 2, 21, 0, 0), new DateTime()).getDays();
		return todayPoints / days;
	}
}
