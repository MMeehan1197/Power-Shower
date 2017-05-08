package helloworld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ShowerTracker {

	private File highscoreFile;
	private final String filepath = "/samples/src/main/java/helloworld/times.txt";
	private final long NANO = 1000000000;
	private long bestTime;
	private int averageTime;
	
	public ShowerTracker() {
		bestTime = findHighscore();
		averageTime = findAverageTime();
		highscoreFile = new File(filepath);
	}

	public long findHighscore() {
		long newBestTime = Long.MAX_VALUE;
		try {
			FileReader fileReader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				try {
					int time = Integer.parseInt(line.trim());
					if (time < newBestTime) {
						newBestTime = time;
					}
				} catch (NumberFormatException e1) {
					System.err.println("The score: " + line + " is invalid");
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newBestTime;
	}

	public int findAverageTime() {
		long totalTimeLong = 0;
		int numOfTimes = 0;
		int average = 0;
		try {
			FileReader fileReader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				try {
					int time = Integer.parseInt(line.trim());
					totalTimeLong += time;
					numOfTimes++;
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int totalScore = (int) (totalTimeLong / NANO);
		average = totalScore / numOfTimes;
		return average;
	}

	public void writeToFile(String time) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(highscoreFile, true));
			output.newLine();
			output.append("" + time);
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		bestTime = findHighscore();
		averageTime = findAverageTime();
	}

	public long getBestTime() {
		return bestTime;
	}

	public int getAverageTime() {
		return averageTime;
	}
}
