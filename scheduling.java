import java.util.*;
import java.io.*;
import java.lang.*;

class SchedulingAlgorithm {
	
	private static final String FILENAME = "C:\\Users\\Kim Morsha\\Documents\\UP IV - 1st Sem\\CMSC 125\\Lab 2\\scheduling-algorithms\\process1.txt";
	private static int SIZE;
	private static int TOTALTIME;

	//indices of the array
	final int PROCESS = 0;
	final int ARRIVAL = 1;
	final int CPU = 2;
	final int PRIORITY = 3;

	public static void main(String[] args) {
		SchedulingAlgorithm sa = new SchedulingAlgorithm();
		
		BufferedReader br = null;
		FileReader fr = null;

		try {
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;
			ArrayList<String> fromFile = new ArrayList<String>();
			
			while ((sCurrentLine = br.readLine()) != null) {
				fromFile.add(sCurrentLine);
			}
			fromFile.remove(0); //remove the headers

			SIZE = fromFile.size();

			int[][] process = new int[SIZE][4];

			for (int i = 0; i < SIZE; i++) {
				String current = fromFile.get(i);
				String[] split = current.split("\\s+", 4);
				for (int j = 0; j < 4; j++) {
					process[i][j] = Integer.parseInt(split[j]);
				}
			}

			TOTALTIME = sa.getTotalTime(process);

			sa.printProcess(process);
		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
	
		// int[][] test1 = {{1  ,0  ,15 ,4},
		// 				 {2  ,1  ,6  ,3},
		// 				 {3  ,1  ,21 ,2},
		// 				 {4  ,2  ,3  ,3},
		// 				 {5  ,3  ,1  ,1}};


		//sa.printProcess(process1, 1);
		//sa.printProcess(process2, 2);
		//sa.printProcess(test1, 1);
	}

	void printProcess(int[][] process) {
		int[] waitingTimeFCFS = new int[SIZE];
		int[] waitingTimeSJF = new int[SIZE];
		int[] waitingTimeSRPT = new int[SIZE];
		int[] waitingTimePRIORITY = new int[SIZE];
		int[] waitingTimeROUNDROBIN = new int[SIZE];

		System.out.println("\n\t\t\tLAB 2: Scheduling Algorithms & Process Management");
		System.out.println("\nWaiting Time for each process...");
		System.out.println("--------------------------------------------------------------------------------------------------------");
		System.out.println("PROCESS\t\tFCFS\t\tSJF\t\tSRPT\t\tPRIORITY\tROUND ROBIN");
		System.out.println("--------------------------------------------------------------------------------------------------------");
		
		waitingTimeFCFS = getWaitingTimeFCFS(process);
		waitingTimeSJF =  getWaitingTimeSJF(process);
		waitingTimeSRPT = getWaitingTimeSRPT(process);
		waitingTimePRIORITY = getWaitingTimePRIORITY(process);
		waitingTimeROUNDROBIN = getWaitingTimeROUNDROBIN(process);

		double averageWaitingTimeFCFS = getAverageWaitingTime(waitingTimeFCFS);
		double averageWaitingTimeSJF = getAverageWaitingTime(waitingTimeSJF);
		double averageWaitingTimeSRPT = getAverageWaitingTime(waitingTimeSRPT);
		double averageWaitingTimePRIORITY = getAverageWaitingTime(waitingTimePRIORITY);
		double averageWaitingTimeROUNDROBIN = getAverageWaitingTime(waitingTimeROUNDROBIN);

		for (int i = 0; i < SIZE; i++) {
			System.out.println((i+1) + "\t\t" + waitingTimeFCFS[i] + "ms\t\t" + waitingTimeSJF[i] + 
										"ms\t\t" + waitingTimeSRPT[i] + "ms\t\t" + waitingTimePRIORITY[i]
										+ "ms\t\t" + waitingTimeROUNDROBIN[i] + "ms"
										);
		}

		System.out.println("\nAVERAGE WAITING TIME FOR EACH ALGORITHM");
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println("FCFS\t\tSJF\t\tSRPT\t\tPRIORITY\tROUND ROBIN");
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println(averageWaitingTimeFCFS + "ms\t\t" + averageWaitingTimeSJF + "ms\t\t" + 
							averageWaitingTimeSRPT + "ms\t\t" + averageWaitingTimePRIORITY + "ms\t\t" +
							averageWaitingTimeROUNDROBIN+ "ms");
		
		System.out.println("\nSCHEDULING ALGORITHM EVALUATION: ");
		double sortedAWT = sortAWT(averageWaitingTimeFCFS, averageWaitingTimeSJF, averageWaitingTimeSRPT,
									averageWaitingTimePRIORITY, averageWaitingTimeROUNDROBIN);
		// for (int i = 0; i < sortedAWT.length; i++) {
		System.out.println("SRPT has the minimum average waiting time of " + sortedAWT + "ms");
		// }
		
		getWaitingTimeSRPT(process);
	}

	int[] getWaitingTimeFCFS(int[][] process) {
		int[] waitingTime = new int[SIZE];
		int prevWaitingTime = 0;
		for (int i = 0; i < SIZE; i++) {
			waitingTime[i] = prevWaitingTime;
			prevWaitingTime = prevWaitingTime + process[i][CPU];
		}
		return waitingTime;
	}

	int[] getWaitingTimeSJF(int[][] process) {
		int[] waitingTime = new int[SIZE];
		int prevWaitingTime = 0;
		int[][] newProcess = new int[SIZE][2];
		
		Arrays.sort(process, java.util.Comparator.comparingDouble(a -> a[CPU]));
		for (int i = 0; i < SIZE; i++) {
			newProcess[i][0] = process[i][PROCESS];
			newProcess[i][1] = prevWaitingTime;
			prevWaitingTime = prevWaitingTime + process[i][CPU];
		}
		Arrays.sort(newProcess, java.util.Comparator.comparingDouble(a -> a[PROCESS]));
		for (int i = 0; i < SIZE; i++) {
			waitingTime[i] = newProcess[i][1];
		}
		return waitingTime;
	}

	int[] getWaitingTimeSRPT(int[][] process) {
		Arrays.sort(process, java.util.Comparator.comparingDouble(a -> a[PROCESS]));

		int[][] newProcess = new int[SIZE][3]; //process no., arrival time, CPU burst time
		ArrayList<int[]> queue = new ArrayList<int[]>();
		int[] waitingTime = new int[SIZE];
		int[][] gantt = new int[TOTALTIME][3]; 

		//transfer process to newProcess
		for (int i = 0; i < SIZE; i++) {
		 	newProcess[i][PROCESS] = process[i][PROCESS];
		 	newProcess[i][ARRIVAL] = process[i][ARRIVAL]; 
		 	newProcess[i][CPU] = process[i][CPU];
		} 

		int index = 0;

		for (int time = 0; time < TOTALTIME; time++) {
			//get all process with the same arrival time;	
			if (index < SIZE) {
				queue.add(new int[]{newProcess[index][PROCESS],newProcess[index][ARRIVAL],newProcess[index][CPU]});	
			}

				while(index+1 < SIZE && newProcess[index+1][ARRIVAL] == time) {
					// System.out.println("Special case: Two or more several same arrival time cases ");
					// System.out.println(newProcess[index+1][ARRIVAL] + " = " + time);
					index++;
					queue.add(new int[]{newProcess[index][PROCESS],newProcess[index][ARRIVAL],newProcess[index][CPU]});
					//printQueue(queue);
				}
				index++;
				if (gantt.length != 0 && time != 0 && gantt[time-1][CPU] != 0) {
					//System.out.println("Add the last item in gantt in the queue");
					queue.add(new int[]{gantt[time-1][PROCESS],gantt[time-1][ARRIVAL],gantt[time-1][CPU]});
					//printQueue(queue);
				}
				//sort queue
				Collections.sort(queue, new Comparator<int[]>() {
				    public int compare(int[] a, int[] b) {
				        return (Integer)((Integer)a[PROCESS]).compareTo((Integer)b[PROCESS]);
				    }
				});
				Collections.sort(queue, new Comparator<int[]>() {
				    public int compare(int[] a, int[] b) {
				        return (Integer)((Integer)a[a.length-1]).compareTo((Integer)b[b.length-1]);
				    }
				});
				//put first from queue to gantt with deducted CPU tiime & remove first from queue

				if (!queue.isEmpty()) {
					int[] firstFromQueue = queue.get(0);
					gantt[time][PROCESS] = firstFromQueue[PROCESS];
					gantt[time][ARRIVAL] = firstFromQueue[ARRIVAL];
					gantt[time][CPU] = firstFromQueue[CPU]-1;
					queue.remove(0);
				}
				
				//printQueue(queue);
		}

		// System.out.println("GANTT CHART FOR SRPT");
		// for (int i = 0; i < gantt.length; i++) {
		// 	System.out.println("["+i+"] | P"+gantt[i][PROCESS]+" ("+gantt[i][CPU]+") |");
		// }
		//waiting time part yeheeey
		int[][] semiFinal = new int[SIZE][4]; //processNo, times in gantt chart, lastWaitingTime, arrival

		for (int i = 0; i < semiFinal.length; i++) {
			semiFinal[i][1] = 0;
			semiFinal[i][2] = -1;
		}

		for (int i = gantt.length-1; i >= 0; i--) {
			int processNo = gantt[i][0];
			semiFinal[processNo-1][0] = gantt[i][0];
			semiFinal[processNo-1][1] += 1;	
			semiFinal[processNo-1][3] = gantt[i][ARRIVAL];
			if (semiFinal[processNo-1][2] == -1) {
				semiFinal[processNo-1][2] = i;			
			}		
		}

		for (int i = 0; i < SIZE; i++) {
			int waiting = semiFinal[i][2] - (semiFinal[i][1]) - semiFinal[i][3];
			waitingTime[i] = waiting+1;
		}

		// for (int i = 0; i < SIZE; i++) {
		// 	System.out.println(waitingTime[i]);
		// }

		return waitingTime;
	}

	int[] getWaitingTimePRIORITY(int[][] process) {
		Arrays.sort(process, java.util.Comparator.comparingDouble(a -> a[PROCESS]));

		int[] waitingTime = new int[SIZE];
		int prevWaitingTime = 0;
		int[][] newProcess = new int[SIZE][2];
		
		Arrays.sort(process, java.util.Comparator.comparingDouble(a -> a[PRIORITY]));
		for (int i = 0; i < SIZE; i++) {
			newProcess[i][0] = process[i][PROCESS];
			newProcess[i][1] = prevWaitingTime;
			prevWaitingTime = prevWaitingTime + process[i][CPU];
		}
		Arrays.sort(newProcess, java.util.Comparator.comparingDouble(a -> a[PROCESS]));
		for (int i = 0; i < SIZE; i++) {
			waitingTime[i] = newProcess[i][1];
		}
		return waitingTime;
	}

	int[] getWaitingTimeROUNDROBIN(int[][] process) {
		Arrays.sort(process, java.util.Comparator.comparingDouble(a -> a[PROCESS]));

		int[][] newProcess = new int[SIZE][4]; //process no, waiting time, quantum, times it has been allocated CPU
		int[] waitingTime = new int[SIZE];
		int quantum = 0;
		int wait = 0;
		//transfer process numbers and respective CPU time to new process array
		//System.out.println("ROUND ROBIN \n newProcess initial");	
		for (int i = 0; i < SIZE; i++) {
		 	newProcess[i][PROCESS] = process[i][PROCESS];
		 	newProcess[i][1] = process[i][CPU]; 
		 	newProcess[i][3] = 0;
		} 

		while (quantum < TOTALTIME) {
			//System.out.println("inside while loop");
			for (int i = 0; i < SIZE; i++) {
				int burstTimeLeft = newProcess[i][1];
				if (burstTimeLeft-4 >=	 0) {
					burstTimeLeft -= 4;
					wait = quantum;
					newProcess[i][1] = burstTimeLeft;
					newProcess[i][2] = wait;
					newProcess[i][3] += 1;
					quantum += 4;
				} else {
					if (burstTimeLeft < 4 && burstTimeLeft > 0) {
						wait = quantum;
						newProcess[i][2] = wait;
						quantum += newProcess[i][1];
						newProcess[i][1] = 0;
						newProcess[i][3] += 1;
					}
				}
			//	System.out.println("QUANTUM IS = "+quantum);
			//	System.out.println(newProcess[i][0] + " | " + newProcess[i][1] + " | " + newProcess[i][2] + " | " + newProcess[i][3]);
			}
		}
		// System.out.println("Waiting Time for ROUNDROBIN");
		for (int i = 0; i < SIZE; i++) {
			waitingTime[i] = newProcess[i][2] - (4*((newProcess[i][3])-1));	
			//System.out.println(newProcess[i][2] + " - " + (4*((newProcess[i][3])-1)) + " = " + waitingTime[i]);
		}

		return waitingTime;
	}

	int getTotalTime(int[][] process) {
		int totalTime = 0;
		for (int i = 0; i < SIZE; i++) {
			totalTime += process[i][CPU];
		}

		return totalTime;
	}

	double getAverageWaitingTime(int[] waitingTime) {
		double average = 0;
		for (int i = 0; i < waitingTime.length; i++) {
			average += waitingTime[i];
		}
		average /= SIZE;
		average = (double) Math.round(average * 100) / 100;
		return average;
	}

	double sortAWT(double fcfs, double sjf, double srpt, double priority, double roundrobin) {
		double[] sortedAWT = {fcfs, sjf, srpt, priority, roundrobin};
		Arrays.sort(sortedAWT);
		return sortedAWT[0];
	}

	// void printQueue(ArrayList<int[]> queue) {
	// 	System.out.print("\nQUEUE : \n");
	// 	for (int i = 0; i < queue.size(); i++) {
	// 		int[] current = queue.get(i);
	// 		for (int j = 0; j < 3; j++) {
	// 			System.out.print("| " + current[j]);
	// 		}
	// 	}
	// }
}