package cavernsProgramProject;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class CavernsProgramClass {
	
		// take in the first argument from the .bat file and read the contents
		public static String inputFile(String fileName) throws FileNotFoundException{
			String dataFromFile = "";
			File cavernsFile = new File(fileName + ".cav");
			Scanner fileReader = new Scanner(cavernsFile);
			dataFromFile = fileReader.nextLine();
			fileReader.close();
			return dataFromFile;
		}

		
		public static double euclidian(double x, double y, double goalx, double goaly) {
			return Math.sqrt((goaly - y) * (goaly - y) + (goalx - x) * (goalx - x));
		}
		
				
		// sets the variables of the caverns file before applying algorithm
		public static String algorithm(String dataFromFile){
			String[] temp = dataFromFile.split(",", dataFromFile.length());
			
			int numberOfCaverns = Integer.parseInt(temp[0]);
							
			double[][] caverns = new double[numberOfCaverns][6];
			int name = 1;
			int a = 0;
			int b = 0;
			double g = 0;
			double h = 0;
			for (int i = 1; i < numberOfCaverns * 2 + 1; ++i) {
				caverns[a][b] = name;  //set cavern name
				++name;   ++b;
				caverns[a][b] = Integer.parseInt(temp[i]);  //set cavern x
				++i;	++b;
				caverns[a][b] = Integer.parseInt(temp[i]);  //set cavern y
				++b;
				caverns[a][b] = g;  //set cavern g
				++b;
				caverns[a][b] = h;  //set cavern h
				++b;
				caverns[a][b] = 0;
				--b;	--b;	--b;	--b;    --b;	++a;

			}
			
			double[] goal = {caverns[caverns.length-1][0] , caverns[caverns.length-1][1] , caverns[caverns.length-1][2]};
			
			int[] cavernConnectivity = new int[numberOfCaverns*numberOfCaverns];
			int j = 0;
			for (int i = numberOfCaverns * 2 + 1; i < temp.length; ++i) {
				cavernConnectivity[j] = Integer.parseInt(temp[i]);
				++j;
			}
						
			
			// get and set the start node h
			caverns[0][4] = euclidian(caverns[0][1], caverns[0][2], goal[1], goal[2]); 
			
			
			// ========= ALGORITHM ====================================
			// goes through the caverns applying the A* algorithm
			ArrayList<Integer> openList = new ArrayList<Integer>();
			ArrayList<Integer> closedList = new ArrayList<Integer>();
			openList.add((int) caverns[0][0]);	//add start node name

						
			int pathFound = 0;
			int currentNode = 0;
			while(pathFound == 0) {
				double openListLowestF = 0;
				try {
					openListLowestF =  (caverns[openList.get(0)-1][3] + caverns[openList.get(0)-1][4] + 1);    // f(n) value lowest in openList
				}
				catch(IndexOutOfBoundsException e) {
					return "0";
				}
				for(int i=0; i<caverns.length; ++i) {   // go through openList to find lowest f
					if(openList.contains((int) caverns[i][0])) {   // in openList
						if(caverns[i][3] + caverns[i][4] < openListLowestF) {      // compare f's
							openListLowestF = (caverns[i][3] + caverns[i][4]);
							currentNode = (int) caverns[i][0];
						}
					}
				}
				openList.remove(openList.indexOf(currentNode));
				closedList.add((int) (currentNode));
				
				
				if(currentNode == (int) goal[0]) {
					String path = "";
					String[] tempPath = new String[numberOfCaverns];   // 431
					int q = 0;
					while(currentNode!=1) {
						tempPath[q] =  Integer.toString(currentNode);   //  add the node
						currentNode = (int) caverns[currentNode-1][5];  //  go to parent
						++q;
					}
					tempPath[q] = Integer.toString(1);
					
					for(int i = tempPath.length-1; i>=0; --i) {
						if(tempPath[i] != null) {
							path = path + tempPath[i] + " ";
						}
					}
					return path.substring(0, path.length()-1);
				}
				
													
				 
				for (double[] neighbour : caverns) {
					if( closedList.contains((int)neighbour[0])) {    // neighbour in closed
						continue;
					}
					if((int) neighbour[0] == currentNode ) {    // neighbour is currentNode
						continue;
					}
					if( cavernConnectivity[(int) (neighbour[0]*numberOfCaverns-numberOfCaverns + currentNode -1)] == 0 ) {   // neighbour is not reachable
						continue;
					}

					
					
					// if the neighbours new path is shorter than previously AND neighbour not in openList OR no parent yet 
					if(!(openList.contains((int) neighbour[0]))  &&  (  caverns[currentNode-1][3] + euclidian(neighbour[1], neighbour[2], caverns[currentNode-1][1], caverns[currentNode-1][2]) < neighbour[3]  || neighbour[3] == 0)) {
						neighbour[3] = caverns[currentNode-1][3] + euclidian(neighbour[1], neighbour[2], caverns[currentNode-1][1], caverns[currentNode-1][2]);        // neighbour g  - currentNode g + distance from currentNode
						neighbour[4] = euclidian(neighbour[1], neighbour[2], goal[1], goal[2]);       // h
						neighbour[5] = currentNode;
						if(!(openList.contains((int) neighbour[0]))) {
							openList.add((int) neighbour[0]);
						}
						//set the neighbours details in caverns
						caverns[(int) neighbour[0]-1][3] = neighbour[3];  // g
						caverns[(int) neighbour[0]-1][4] = neighbour[4];  // h
						caverns[(int) neighbour[0]-1][5] = neighbour[5];  // parent
					}
					
				}
				
							
			}

			// if no route found
			return "0";
		}
		
		
		
																
		public static void main(String[] args) throws IOException {
			String fileName = args[0];	  
			
			// input file
			String dataFromFile = inputFile(fileName);
			
			// process file and apply algorithm
			String bestRoute = algorithm(dataFromFile);
						
			// make the output file and output shortest route found or '0'
			String outputFileName = fileName.concat(".csn");
			Files.write(Paths.get(outputFileName), bestRoute.getBytes());
		}
	}