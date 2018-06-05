/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.imagecompression;

import imgpro.MainWindow;
import java.util.ArrayList;

/**
 *
 * @author ToddNguyen
 */
public class LZW {
	private static LZW instance;
	private LZW() {};
	public static LZW getInstance() {
		if (instance == null) {
			instance = new LZW();
		}
		return instance;
	}
	
	public ArrayList<Integer> getLZW(int initImage[][]) {
		int width = initImage.length;
		int height = initImage[0].length;
		int iIndex = 0;
		int jIndex = 0;
		ArrayList<Integer> totalOutputs = new ArrayList<>();
		totalOutputs.add(width);
		totalOutputs.add(height);
		
		CustomHashMap<String, Integer> dictionary = new CustomHashMap<> ();
		int curDictIndex = 0;
		// Initialize the dictionary
		for (int i = 0; i <= MainWindow.MAX_GRAYSCALE_VALUE; i++) {
			String value = String.format("%d", i);
			dictionary.put(value, curDictIndex++);
		}
		
		while (iIndex < width) {
			String curString = String.format("%d", initImage[iIndex][jIndex]);
			String prevString = ""; // to store the string before more information is added
			// If the curString is already in the dictionary
			while (dictionary.containsKey(curString)) {
				jIndex += 1;
				if (jIndex >= height) {
					jIndex = 0;
					iIndex += 1;
					if (iIndex >= width) break; // break if iIndex exceeds width
				}
				prevString = curString;
				curString += String.format("%d", initImage[iIndex][jIndex]);
			}
			
			// Put the curString in our dictionary if it is not in there already
			if (!dictionary.containsKey(curString)) {
				dictionary.put(curString, curDictIndex++);
			}
			
			String output = prevString;
			if (prevString.isEmpty()) {output = curString;} // if the prevString is empty
			
			totalOutputs.add(dictionary.get(output));
		}
		
		return totalOutputs;
	}
	
	/**
	 * Get the size required by the LZW code, in bits.
	 * @param encoded The total ArrayList of the LZW encoded bits
	 * @return The total size required in bits
	 */
	public double getSizeOfLZW(ArrayList<Integer> encoded) {
		double counter = 0.0;
		
		for (Integer i : encoded) {
			int bitsRequired = nextPowerOfTwoExponent(i);
			counter += bitsRequired;
		}
		
		return counter;
	}
	
	
	public int[][] decodeLZW(ArrayList<Integer> encoded) {
		int width = encoded.get(0);
		int height = encoded.get(1);
		int ii = 0;
		int jj = 0;
		int decodedImage[][] = new int[width][height];
		
		CustomHashMap<Integer, String> dictionary = new CustomHashMap<> ();
		int curDictIndex = 0;
		// Initialize the dictionary
		for (int i = 0; i <= MainWindow.MAX_GRAYSCALE_VALUE; i++) {
			String value = String.format("%03d", i);
			dictionary.put(curDictIndex++, value);
		}
		
		int prevKey = encoded.get(2);
		String prevStr = dictionary.get(prevKey);
		String prevFirstStr = "";
		decodedImage[ii][jj] = Integer.parseInt(prevStr);
		jj += 1;
				
		for (int i = 3; i < encoded.size(); i++) {
			String output;
			int currKey = encoded.get(i);
			
			// If current key is not in the dictionary
			if (!dictionary.containsKey(currKey)) {
				output = dictionary.get(prevKey);
				output += prevFirstStr;
			}
			// If current key is already in the dictionary
			else {
				output = dictionary.get(currKey);
			}
			
			// outputting the image
			for (int j = 0; j < output.length() / 3; j++) {
				String grayStr = output.substring(j * 3, (j + 1) * 3);
				
				int gray = Integer.parseInt(grayStr);
				decodedImage[ii][jj] = gray;
				
				jj += 1;
				if (jj  >= height) {
					jj = 0;
					ii += 1;
				}
			}
			
			prevFirstStr = output.substring(0, 3);
			prevStr = dictionary.get(prevKey) + prevFirstStr;
			dictionary.put(curDictIndex++, prevStr);
			prevKey = currKey; // reset index		
		}
		
		return decodedImage;
	}
	
	
	public static final int nextPowerOfTwoExponent(int integer) {
		int bitShifted = 1;
		int num = 1 << bitShifted;
		
		while (num <= integer) {
			bitShifted += 1;
			num = 1 << bitShifted;
		}
		
		return bitShifted;
	}
}
