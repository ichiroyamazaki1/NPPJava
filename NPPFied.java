package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class NPPFied extends Application {

	private Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.setPrimaryStage(primaryStage);
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("----------------------------------------");
			System.out.println("     Non-Preemptive Priority (NPP)      ");
			System.out.println("----------------------------------------");

			System.out.print("Enter the number of processes: ");
			int numProcesses = scanner.nextInt();

			ArrayList<Process> processes = new ArrayList<>();
			for (int i = 0; i < numProcesses; i++) {
				System.out.print("Enter Arrival Time for Process " + (i + 1) + ": ");
				int arrivalTime = scanner.nextInt();
				System.out.print("Enter Burst Time for Process " + (i + 1) + ": ");
				int burstTime = scanner.nextInt();
				System.out.print("Enter Priority for Process " + (i + 1) + ": ");
				int priority = scanner.nextInt();

				processes.add(new Process(i + 1, arrivalTime, burstTime, priority));
			}

			Collections.sort(processes, Process::compareTo);
			System.out.println("");
			System.out.println("Process Table:");
			System.out.println("|---------------------------------------------------------------------------------|");
			System.out.println("|Process|Arrival Time|Burst Time|Priority|Finish Time|Turnaround Time|Waiting Time|");
			System.out.println("|---------------------------------------------------------------------------------|");

			int totalTime = 0;
			int totalWaitingTime = 0;
			int totalTurnaroundTime = 0;

			ArrayList<GanttTask> ganttTasks = new ArrayList<>();
			for (Process process : processes) {
				process.setCompleteTime(totalTime + process.getBurstTime());
				process.setTurnaroundTime(process.getCompleteTime() - process.getArrivalTime());
				process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());

				totalWaitingTime += process.getWaitingTime();
				totalTurnaroundTime += process.getTurnaroundTime();

				System.out.printf("| %-5d | %-10d | %-8d | %-6d | %-9d | %-13d | %-10d |%n", process.getPid(),
						process.getArrivalTime(), process.getBurstTime(), process.getPriority(),
						process.getCompleteTime(), process.getTurnaroundTime(), process.getWaitingTime());

				ganttTasks.add(new GanttTask("P" + process.getPid(), totalTime, process.getBurstTime()));
				totalTime = process.getCompleteTime();
			}

			System.out.println("|---------------------------------------------------------------------------------|");
			System.out.printf("%nThe Total Time is: %d%n", totalTime);
			System.out.printf("The Average Waiting Time is: %.2fms%n", (double) totalWaitingTime / numProcesses);
			System.out.printf("The Average Turnaround Time is: %.2fms%n", (double) totalTurnaroundTime / numProcesses);

			displayGanttChart(ganttTasks, totalTime);
			System.out.println("");
			System.out.print("Processes is completed. Do you want to restart? (Y/N): ");
			String restartChoice = scanner.next().toLowerCase();
			if (restartChoice.equals("y")) {
				start(primaryStage);
			} else {
				System.out.println("Program terminated.");
				System.out.println("");
				System.out.println("Made by: John Jason C. Domingo");
				System.out.println("TP for Operating Systems Finals");
				primaryStage.close();
			}
		}
	}

	private void displayGanttChart(ArrayList<GanttTask> ganttTasks, int totalTime) {
		Stage ganttStage = new Stage();
		Pane root = new Pane();
		double scale = 50.0;

		Text title = new Text("NPP Gantt Chart Table");
		title.setFont(new Font("Times New Roman", 20));
		title.setX(30);
		title.setY(30);

		root.getChildren().add(title);

		double xPos = 0;
		for (GanttTask task : ganttTasks) {
			double width = task.getDuration() * scale;

			Rectangle rect = new Rectangle(xPos, 50, width, 20);
			rect.setStroke(Color.BLACK);
			rect.setFill(Color.TRANSPARENT);

			Text text = new Text(xPos + 5, 65, task.getName());

			root.getChildren().addAll(rect, text);
			xPos += width;
		}

		for (int i = 0; i <= totalTime; i++) {
			Text timeText = new Text(scale * i - (i < 10 ? 3 : 7), 85, String.valueOf(i));
			root.getChildren().add(timeText);
		}

		ScrollPane scrollPane = new ScrollPane(root);
		scrollPane.setPrefViewportWidth(900);
		scrollPane.setPrefViewportHeight(500);
		scrollPane.setPannable(true);

		HBox hbox = new HBox(scrollPane);
		Scene scene = new Scene(hbox);

		ganttStage.setTitle("NPP Gantt Chart");
		ganttStage.setScene(scene);
		ganttStage.show();
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	static class Process implements Comparable<Process> {
		private int pid;
		private int arrivalTime;
		private int burstTime;
		private int completeTime;
		private int turnaroundTime;
		private int waitingTime;
		private int priority;

		public Process(int pid, int arrivalTime, int burstTime, int priority) {
			this.pid = pid;
			this.arrivalTime = arrivalTime;
			this.burstTime = burstTime;
			this.priority = priority;
		}

		public int getPid() {
			return pid;
		}

		public int getArrivalTime() {
			return arrivalTime;
		}

		public int getBurstTime() {
			return burstTime;
		}

		public int getPriority() {
			return priority;
		}

		public int getCompleteTime() {
			return completeTime;
		}

		public void setCompleteTime(int completeTime) {
			this.completeTime = completeTime;
		}

		public int getTurnaroundTime() {
			return turnaroundTime;
		}

		public void setTurnaroundTime(int turnaroundTime) {
			this.turnaroundTime = turnaroundTime;
		}

		public int getWaitingTime() {
			return waitingTime;
		}

		public void setWaitingTime(int waitingTime) {
			this.waitingTime = waitingTime;
		}

		@Override
		public int compareTo(Process other) {
			return Integer.compare(this.getPriority(), other.getPriority());
		}
	}

	static class GanttTask {
		private String name;
		private int startTime;
		private int duration;

		public GanttTask(String name, int startTime, int duration) {
			this.name = name;
			this.startTime = startTime;
			this.duration = duration;
		}

		public String getName() {
			return name;
		}

		public int getStartTime() {
			return startTime;
		}

		public int getDuration() {
			return duration;
		}
	}
}
