/*
 * Written by Ashley Romualdo for fun.
 * Based on the Sphere problem to and fro
 * 
 * Description:
 * Takes in a coded string array. In order to decode the string
 * must be read into a 2D array in left to right, then right to left 
 * order like a backwards S. You then must read the array from left to 
 * right going up to down. In order to fill out the rectangle you fill in
 * the empty spaces with x's
 * 
 * EG. "toioynnkpheleaigshareconhtomesnlewx"
 * Put into a 2D Array
 * t o i o y
   h p k n n
   e l e a i
   r a h s g
   e c o n h
   s e m o t
   n l e w x
 * 
 * Then read in downwards, left to right order
 * "theresnoplacelikehomeonasnowynightx"
 * 
 * There is no place like home on a snowy night
 * 
 */


import java.io.*;
import java.util.*;

public class JDecoder {
	//GLOBAL VARIABLES
	static int Columns = 0;
	static String Input = "";
	static String Result = "";
	
	
	/*
	 *	Main Method
	 *	Takes in input, and outputs the result to a .out file
	 */
	public static void main(String[] args) throws IOException{
		final Scanner r = new Scanner(System.in);
		final Writer w = new PrintWriter(System.out);
		solve(r,w);
		
		
		//Test Cases
		//boolean done = test(w);
		//if(done) System.out.println("\nDone");
	}
	
	/*
	 * Read Method
	 * Takes in the scanner
	 * The number of columns goes into Columns
	 * The read in string goes into Result
	 */
	private static boolean read(Scanner r){
		if(r.hasNext("0")){
			return false;
		}
		Columns = r.nextInt();
		Input = r.next();
		return true;
	}
	
	/*
	 * Solve Method
	 * Calls read()
	 * Calls zigzag()
	 * Calls print()
	 */
	private static boolean solve(Scanner r, Writer w) throws IOException{
		while(read(r)){
			zigzag();
			print(w);
		}
		return true;
	}
	
	/*
	 * Writes the input into a 2D array in zigzag order
	 * Writes it back into result
	 */
	private static char[][] zigzag(){
		int rows = Input.length() / Columns;
		char [][] tmp = new char[rows][Columns];
		
		
		//Fill 2D Array
		int index = 0;
		for(int row = 0; row < rows ; row++){
			for(int col = 0; col < Columns; col++){ //Go down rows
				tmp[row][col] = Input.charAt(index);
				index++;
			}
			row++;
			for(int col = Columns - 1; (row <  rows) && col >= 0; col--){
				tmp[row][col] = Input.charAt(index);
				index++;
			}
		}
		
		//Fill Result string
		for(int col = 0; col < Columns; col++){
			for(int row = 0; row < rows; row++){
				Result = Result + tmp[row][col];
			}
		}
		
		return tmp;
	}
	
	/*
	 * Print a 2D array. Used for testing
	 */
	
	private static void printArray(char[][] tmp, int rows){
		System.out.println();
		for(int row = 0; row < rows; row++){
			for(int col = 0; col < Columns; col++){
				System.out.print(tmp[row][col] + " ");
			}
			System.out.println();
		}
	}
	
	/*
	 * Print Method
	 * writes result to an out file
	 */
	private static void print(Writer w) throws IOException{
		w.write(Result);
		w.flush();
	}
	
	/*
	 * Testing method
	 */
	public static boolean test(Writer w) throws IOException{
		//Check read method
		Scanner r   = new Scanner("5\ntoioynnkpheleaigshareconhtomesnlewx\n0");
		boolean in = read(r);
		
		//Test 1
		if(in && (Columns == 5) && Input.toString().equals("toioynnkpheleaigshareconhtomesnlewx"))
			System.out.print("T") ;
		else 
			System.out.print("F");
		
		char[][] tmp = zigzag();
		char[][] tmp2 = new char[][]{{'t','o','i','o','y'},
									{'h','p','k','n','n'},
									{'e','l','e','a','i'},
									{'r','a','h','s','g'},
									{'e','c','o','n','h'},
									{'s','e','m','o','t'},
									{'n','l','e','w','x'}};
		if(Arrays.equals(tmp, tmp2))
			System.out.print("T2") ;
		else 
			System.out.print("F2");

		if(Result.equals("theresnoplacelikehomeonasnowynightx"))
			System.out.print("T") ;
		else 
			System.out.print("F");
		
		Scanner s   = new Scanner("5\ntoioynnkpheleaigshareconhtomesnlewx\n3\nttyohhieneesiaabss\n0");
		
		
		return true;
	}
}
