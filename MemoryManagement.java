import java.util.*;
import java.io.*;
import java.lang.*;

class Job {
	int job_number = 0;
	int job_time = 0;
	int job_size = 0;
	int memoryIndex = -1;
	boolean isDone = false;
	boolean isAllocated = false;

	public Job(int job_number, int time, int job_size) {
		this.job_number = job_number;
		this.job_time = time;
		this.job_size = job_size;
		this.isDone = false;
		this.isAllocated = false;
	}
	// public Job() {
	// 	this.job_number = 0;
	// 	this.job_time = 0;
	// 	this.job_size = 0;
	// 	this.isDone = false;
	// 	this.isAllocated = false;
	// }
//getters
	public int getJobNumber() {
		return this.job_number;
	} 

	public int getJobTime() {
		return this.job_time;
	}

	public int getJobSize() {
		return this.job_size;
	} 

	public boolean isJobDone() {
		return this.isDone;
	} 

	public boolean isJobAllocated() {
		return this.isAllocated;
	} 

	public int getMemoryIndex() {
		return this.memoryIndex;
	}
//setters
	public void setJobNumber(int job_number) {
		this.job_number = job_number;
	} 

	public void setJobTime(int time) {
		this.job_time = time;
	}

	public void setJobSize(int job_size) {
		this.job_size = job_size;
	}

	public void setJobToDone() {
		this.isDone = true;
	}

	public void setAllocatedToTrue() {
		this.isAllocated = true;
	}

	public void setMemoryIndex(int memoryIndex) {
		this.memoryIndex = memoryIndex;
	}
}

class MemoryBlock {
	int memory_number;
	int memory_size;
	int fragmentation = 0;
	boolean isUsed;
	Job job = null;
//constructor
	public MemoryBlock(int memory_number, int memory_size) {
		this.memory_number = memory_number;
		this.memory_size = memory_size;
		this.fragmentation = 0;
		this.isUsed = false;
		this.job = null;
		// this.job.setJobNumber(0);
		// this.job.setJobSize(0);
		// this.job.setJobTime(0);
	}
//getters
	public int getMemoryNumber() {
		return this.memory_number;
	} 

	public int getMemorySize() {
		return this.memory_size;
	}  

	public int getFragmentation() {
		return this.fragmentation;
	}

	public boolean isMemoryUsed() {
		return this.isUsed;
	}

	public Job getJob() {
		if (this.job == null) {
			return null;
		}
		return this.job;
	}
//setters
	public void setMemoryNumber(int memory_number) {
		this.memory_number = memory_number;
	} 

	public void setoMemorySize(int memory_size) {
		this.memory_size = memory_size;
	}

	private void setFragmentation() {
		this.fragmentation = this.memory_size - this.job.job_size;
	}

	public void setMemoryToUsed() {
		this.isUsed = true;
	}

	public void setMemoryToFree() {
		this.isUsed = false;
	}

	public void setJob(Job job) {
		if (job == null) {
			this.job.setJobNumber(0);
			this.job.setJobSize(0);
			this.job.setJobTime(0);
		} else {
			this.job = job;	
		}
		setFragmentation();
	}
}

class MemoryManagement {
	final int JOBLIST_LENGTH = 25;
	final int MEMORYLIST_LENGTH = 10;

	public static void main(String[] args) throws InterruptedException{
		MemoryManagement mm = new MemoryManagement();
		mm.clearScreenForMain();
		int choice = mm.showMainPage();
		
		while (choice < 5 && choice > 0) {
			switch (choice) {
				case 1: //First-fit Algorithm
					mm.firstFit();
					break;
				case 2: //Worst-fit Algorithm
					mm.worstFit();
					break;
				case 3: //Best-fit Algorithm
					mm.bestFit();
					break;
				case 4:
					System.exit(0);
					break;
			}
			choice = mm.showMainPage();
		}
	}

	public void firstFit() throws InterruptedException {
		Job[] jobList = createJobList();
		MemoryBlock[] memoryList = createMemoryList();
		ArrayList<Job> queue = new ArrayList<Job>();
		int time = 0;
		while (!isOver(jobList)) {
			clearScreen();
			System.out.println("\t\t\t\t\t\tFIRST-FIT ALGORITHM");
			System.out.println("\n\t\t\t\t\t\t    Time: (" + time + ")");
			
			for (int i = 0; i < JOBLIST_LENGTH; i++) {
				Job job = jobList[i];

				if (!job.isJobAllocated() && !job.isJobDone()) {
					int j = 0;
					for (j = 0; j < MEMORYLIST_LENGTH; j++) {
						if (!memoryList[j].isMemoryUsed()) {
							if (job.getJobSize() <= memoryList[j].getMemorySize()) {
								jobList[i].setAllocatedToTrue();
								jobList[i].setJobTime(job.getJobTime()-1);
								jobList[i].setMemoryIndex(j+1);
								memoryList[j].setMemoryToUsed();
								memoryList[j].setJob(jobList[i]);

								// if (jobList[i].getJobTime() == 0) {
								// 	jobList[i].setJobToDone();
								// }

								if (queue.contains(jobList[i])) {
									queue.remove(queue.indexOf(jobList[i]));
								}
								break;
							}
						}
					}
					if (!jobList[i].isJobAllocated()) {
						if (!queue.contains(jobList[i])) {
							queue.add(jobList[i]);
						}
					}
				} else if (job.isJobAllocated() && !job.isJobDone() && job.getJobTime() > 0) {
					jobList[i].setJobTime(job.getJobTime()-1);
				} else if (job.isJobAllocated() && !job.isJobDone() && job.getJobTime() == 0) {
					jobList[i].setJobToDone();
					memoryList[jobList[i].getMemoryIndex()-1].setMemoryToFree();
					memoryList[jobList[i].getMemoryIndex()-1].setJob(null);	
					if (queue.contains(jobList[i])) {
						queue.remove(queue.indexOf(jobList[i]));
					}
				}
			}

			printTable(jobList, memoryList, queue);
			time++;
			Thread.sleep(1500);
		}
		promptEnterKey();
	}

	public void worstFit() throws InterruptedException {
		Job[] jobList = createJobList();
		MemoryBlock[] memoryList = createMemoryList();
		ArrayList<Job> queue = new ArrayList<Job>();
		int time = 0;

		while (!isOver(jobList)) {
			clearScreen();
			System.out.println("\t\t\t\t\t\tWORST-FIT ALGORITHM");
			System.out.println("\n\t\t\t\t\t\t    Time: (" + time + ")");

			for (int i = 0; i < JOBLIST_LENGTH; i++) {
				Job job = jobList[i];

				if (!job.isJobAllocated() && !job.isJobDone()) {
					int worstMemorySize = -1;
					int worstMemoryIndex = -1;
					int j = 0;
					for (j = 0; j < MEMORYLIST_LENGTH; j++) {
						if (!memoryList[j].isMemoryUsed()) {
							if (job.getJobSize() <= memoryList[j].getMemorySize()) {
								if (worstMemorySize == -1) {
									worstMemorySize = memoryList[j].getMemorySize();
									worstMemoryIndex = j;
								} else {
									if (memoryList[j].getMemorySize() > worstMemorySize) {
										worstMemorySize = memoryList[j].getMemorySize();
										worstMemoryIndex = j;
									}
								}
							}
						}
					}

					if (worstMemorySize != -1) {
						jobList[i].setAllocatedToTrue();
						jobList[i].setJobTime(job.getJobTime()-1);
						jobList[i].setMemoryIndex(worstMemoryIndex+1);
						memoryList[worstMemoryIndex].setMemoryToUsed();
						memoryList[worstMemoryIndex].setJob(jobList[i]);
						

						// if (jobList[i].getJobTime() == 0) {
						// 	jobList[i].setJobToDone();
						// }

						if (queue.contains(jobList[i])) {
							queue.remove(queue.indexOf(jobList[i]));
						}
					}
					if (!jobList[i].isJobAllocated()) {
						if (!queue.contains(jobList[i])) {
							queue.add(jobList[i]);
						}
					}

				} else if (job.isJobAllocated() && !job.isJobDone() && job.getJobTime() > 0) {
					jobList[i].setJobTime(job.getJobTime()-1);
				} else if (job.isJobAllocated() && !job.isJobDone() && job.getJobTime() == 0) {
					jobList[i].setJobToDone();
					memoryList[jobList[i].getMemoryIndex()-1].setMemoryToFree();
					memoryList[jobList[i].getMemoryIndex()-1].setJob(null);	
					if (queue.contains(jobList[i])) {
						queue.remove(queue.indexOf(jobList[i]));
					}
				}
			}
			printTable(jobList, memoryList, queue);
			time++;
			Thread.sleep(8000);
		}
		promptEnterKey();
	}

	public void bestFit() throws InterruptedException {
		Job[] jobList = createJobList();
		MemoryBlock[] memoryList = createMemoryList();
		ArrayList<Job> queue = new ArrayList<Job>();
		int time = 0;

		while (!isOver(jobList)) {
			clearScreen();
			System.out.println("\t\t\t\t\t\tBEST-FIT ALGORITHM");
			System.out.println("\n\t\t\t\t\t\t   Time: (" + time + ")");

			for (int i = 0; i < JOBLIST_LENGTH; i++) {
				Job job = jobList[i];

				if (!job.isJobAllocated() && !job.isJobDone()) {
					int bestMemorySize = -1;
					int bestMemoryIndex = -1;
					int j = 0;
					for (j = 0; j < MEMORYLIST_LENGTH; j++) {
						if (!memoryList[j].isMemoryUsed()) {
							if (job.getJobSize() <= memoryList[j].getMemorySize()) {
								if (bestMemorySize == -1) {
									bestMemorySize = memoryList[j].getMemorySize();
									bestMemoryIndex = j;
								} else {
									if (memoryList[j].getMemorySize() < bestMemorySize) {
										bestMemorySize = memoryList[j].getMemorySize();
										bestMemoryIndex = j;
									}
								}
							}
						}
					}

					if (bestMemorySize != -1) {
						jobList[i].setAllocatedToTrue();
						jobList[i].setJobTime(job.getJobTime()-1);
						jobList[i].setMemoryIndex(bestMemoryIndex+1);
						memoryList[bestMemoryIndex].setMemoryToUsed();
						memoryList[bestMemoryIndex].setJob(jobList[i]);
						
						// if (jobList[i].getJobTime() == 0) {
						// 	jobList[i].setJobToDone();
						// }

						if (queue.contains(jobList[i])) {
							queue.remove(queue.indexOf(jobList[i]));
						}
					}
					if (!jobList[i].isJobAllocated()) {
						if (!queue.contains(jobList[i])) {
							queue.add(jobList[i]);
						}
					}

				} else if (job.isJobAllocated() && !job.isJobDone() && job.getJobTime() > 0) {
					jobList[i].setJobTime(job.getJobTime()-1);
				} else if (job.isJobAllocated() && !job.isJobDone() && job.getJobTime() == 0) {
					jobList[i].setJobToDone();
					memoryList[jobList[i].getMemoryIndex()-1].setMemoryToFree();
					memoryList[jobList[i].getMemoryIndex()-1].setJob(null);	
					if (queue.contains(jobList[i])) {
						queue.remove(queue.indexOf(jobList[i]));
					}
				}
			}
			printTable(jobList, memoryList, queue);
			time++;
			Thread.sleep(1500);
		}
		promptEnterKey(); 
	}

	public void printTable(Job[] jobList, MemoryBlock[] memoryList, ArrayList<Job> queue) {
		System.out.println("\n+---------------------------------------------------------------------------------------------------------------+");
		System.out.println("| Memory Block #\t| Memory Size\t| Job #\t| Job Size\t| Internal Fragmentation\t| Time Left\t|");
		System.out.println("+---------------------------------------------------------------------------------------------------------------+");
		
		for (int i = 0; i < MEMORYLIST_LENGTH; i++) {
			MemoryBlock memoryBlock = memoryList[i];
			int memoryNumber = memoryBlock.getMemoryNumber();
			int memorySize = memoryBlock.getMemorySize();
			Job job = memoryBlock.getJob();
			String jobNumber = "-";
			String jobSize = "-";
			String timeLeft = "-";
			String fragmentation = "-";
			int job_number;
			int job_size;
			int job_time;
			if (job != null) {
				if (job.getJobNumber() != 0) {
					job_number = job.getJobNumber();
					job_size = job.getJobSize();
					job_time = job.getJobTime();
					jobNumber = "" + job_number;
					jobSize = "" + job_size;
					timeLeft = "" + job_time;
					fragmentation = "" + memoryBlock.getFragmentation();
				}
			}

			System.out.println("|\t" + memoryNumber + "\t\t| " + memorySize 
				+ "\t\t| " + jobNumber + "\t| " + jobSize + "\t\t|\t\t" + fragmentation
				+ "\t\t|\t" + timeLeft + "\t|");
		}

		System.out.println("+---------------------------------------------------------------------------------------------------------------+");
	 	
		System.out.print("\nWaiting Queue\t= | ");
		for (int i = 0; i < queue.size(); i++) {
			Job currentJob = queue.get(i);
			int jobNumber = currentJob.getJobNumber();
			int jobTime = currentJob.getJobTime();
			System.out.print(jobNumber + "(" + jobTime + ") | ");
		}

		System.out.println("\n\nThroughput\t\t=\t" + getThroughput(memoryList) + "%"	);
		System.out.println("Storage Utilization\t=\t" + getUtilization(memoryList) + "%");
		System.out.println("Waiting queue length\t=\t" + queue.size());
		System.out.println("\n\n\n\n\n\n\n\n\n");
	}

	public double getThroughput(MemoryBlock[] memoryList) {
		double throughput = 0;
		for (int i = 0; i < memoryList.length; i++) {
			MemoryBlock currentMemory = memoryList[i];
			Job jobInMemory = currentMemory.getJob();
			if (jobInMemory != null) {
				if (!jobInMemory.isJobDone()) {
					throughput++;
				}
			}
		}
		return ((double)throughput/10) * 100;
	}

	public double getUtilization(MemoryBlock[] memoryList) {
		double utilization = 0;
		int sizeOfJobs = 0;
		int totalMemory = 50000;
		for (int i = 0; i < memoryList.length; i++) {
			MemoryBlock currentMemory = memoryList[i];
			Job jobInMemory = currentMemory.getJob();
			if (jobInMemory != null) {
				if (!jobInMemory.isJobDone()) {
					sizeOfJobs += jobInMemory.getJobSize();
				}
			}
		}
		utilization = ((double)sizeOfJobs/totalMemory) * 100;
		return utilization;
	}

	public Job[] createJobList() {
		Job[] jobList = new Job[JOBLIST_LENGTH];
		int[][] joblistArray = {{  1,  5, 5760},
														{  2,  4, 4190},
														{  3,  8, 3290},
														{  4,  2, 2030},
														{  5,  2, 2550},
														{  6,  6, 6990},
														{  7,  8, 8940},
														{  8, 10, 740 },
														{  9,  7, 3930},
														{ 10,  6, 6890},
														{ 11,  5, 6580},
														{ 12,  8, 3820},
														{ 13,  9, 9140},
														{ 14, 10, 420 },
														{ 15, 10, 220 },
														{ 16,  7, 7540},
														{ 17,  3, 3210},
														{ 18,  1, 1380},
														{ 19,  9, 9850},
														{ 20,  3, 3610},
														{ 21,  7, 7540},
														{ 22,  2, 2710},
														{ 23,  8, 8390},
														{ 24,  5, 5950},
														{ 25, 10, 760 }};

		for (int i = 0; i < JOBLIST_LENGTH; i++) {
			Job job = new Job(joblistArray[i][0], //job number
												joblistArray[i][1], //time
												joblistArray[i][2]);//job size
			jobList[i] = job;
		}

		return jobList;
	}

	public MemoryBlock[] createMemoryList() {
		MemoryBlock[] memoryList = new MemoryBlock[MEMORYLIST_LENGTH];
		int[][] memorylistArray = {{ 1, 9500},
															 { 2, 7000},
															 { 3, 4500},
															 { 4, 8500},
															 { 5, 3000},
															 { 6, 9000},
															 { 7, 1000},
															 { 8, 5500},
															 { 9, 1500},
															 {10, 500 }};

		for (int i = 0; i < MEMORYLIST_LENGTH; i++) {
			MemoryBlock memoryBlock = new MemoryBlock(memorylistArray[i][0], //job number
												memorylistArray[i][1]); //size
			memoryList[i] = memoryBlock;
		}

		return memoryList;
	}

	boolean isOver(Job[] jobList) {
		for (int i = 0; i < JOBLIST_LENGTH; i++) {
			Job currentJob = jobList[i];
			if (!currentJob.isJobDone()) {
				if (currentJob.getJobSize() <= 9500) {
					return false;
				}
			}
		}
		return true;
	}

	public int showMainPage() {
		//clearScreen();
		int n = 0;
		Scanner reader = new Scanner(System.in);

		while (n != 1 && n != 2 && n != 3 && n != 4) {
			//clearScreen();
			System.out.println("\n\n\n\n\n\n\n\n\t\t\t\t\t\tMEMORY MANAGEMENT & ALLOCATION STRATEGIES\n\n\n");
			System.out.println("\t\t\t\t\t\t\t[1] First-Fit Algorithm\n\n");
			System.out.println("\t\t\t\t\t\t\t[2] Worst-Fit Algorithm\n\n");
			System.out.println("\t\t\t\t\t\t\t[3] Best-Fit Algorithm\n\n");
			System.out.println("\t\t\t\t\t\t\t[4] EXIT\n\n");
			System.out.println("\n\n\n\n\t\t\t\t\t\tPlease enter the number of your choice: ");
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			reader = new Scanner(System.in);  // Reading from System.in
			n = reader.nextInt(); // Scans the next token of the input as an int.

		}
		//once finished
		return n;
	}

	public void promptEnterKey(){
	   System.out.println("\t\t\t\t\tPress \"ENTER\" to continue...");
	   Scanner scanner = new Scanner(System.in);
	   scanner.nextLine();
	}

	public static void clearScreen() {
	    for (int i = 0; i < 50; i++) {
	        System.out.println();
	    }
	}

	public static void clearScreenForMain() {
	    for (int i = 0; i < 20; i++) {
	        System.out.println();
	    }
	}
}