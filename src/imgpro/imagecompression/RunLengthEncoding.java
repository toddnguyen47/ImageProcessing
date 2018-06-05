/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.imagecompression;

import java.util.ArrayList;

/**
 *
 * @author ToddNguyen
 */
public class RunLengthEncoding {
	private static RunLengthEncoding instance;
	private RunLengthEncoding() {};
	public static RunLengthEncoding getInstance() {
		if (instance == null) {
			instance = new RunLengthEncoding();
		}
		return instance;
	}
	
	
	public ArrayList<Integer> runLengthEncodingGrayscale(int initImage[][]) {
		// Odd indices = number of occurences, even indices = grayscale values
		ArrayList<Integer> encoded = new ArrayList<> ();
		int width = initImage.length;
		int height = initImage[0].length;
		encoded.add(width);
		encoded.add(height);
		int counter = 0; // initialize counter to 0
		int prevGrayValue = initImage[0][0];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int curGrayValue = initImage[i][j];
				
				if (curGrayValue != prevGrayValue) {
					encoded.add(counter);  // adding into the encoded list
					encoded.add(prevGrayValue);  // adding the gray value into the encoded list
					
					prevGrayValue = curGrayValue; // change the current prevGrayValue
					counter = 1; // reset counter to 1
				}
				else {
					counter++; // increment counter
				}
			}
		}
		
		return encoded;
	}
	
	
	public int[][] decodeRLEGrayscale(ArrayList<Integer> encoded) {
		int size = encoded.size();
		int halfSize = size >> 1;
		int width = encoded.get(0); // we stored the width and height in the first 2 bytes
		int height = encoded.get(1); // we stored the width and height in the first 2 bytes
		int ii = 0; // i index
		int jj = 0; // j index
		int decodedImage[][] = new int[width][height];
		
		for (int i = 1; i < halfSize; i++) {
			int occurrences = encoded.get(i * 2);
			int grayValue = encoded.get((i * 2) + 1);
			for (int j = 0; j < occurrences; j++) {
				decodedImage[ii][jj] = grayValue;
				jj += 1;
				if (jj >= height) {
					jj = 0;
					ii += 1;
				}
			}
		}
		
		return decodedImage;
	}
	
	
	public ArrayList<Integer> rleBits(int initImage[][]) {
		// Odd indices = number of occurences, even indices = grayscale values
		ArrayList<Integer> encoded = new ArrayList<> ();
		int width = initImage.length;
		int height = initImage[0].length;
		encoded.add(width);
		encoded.add(height);
		ArrayList<String> binaryRep = convertToBinary(initImage);
		char prevChar = binaryRep.get(0).charAt(0);
		int counter = 0; // initialize counter to 0
		
		for (String binary : binaryRep) {
			for (int j = 0; j < binary.length(); j++) {
				char curChar = binary.charAt(j);
				
				if (curChar != prevChar) {
					encoded.add(counter);
					encoded.add(prevChar == '0' ? 0 : 1);
					// Reset
					counter = 1;
					prevChar = curChar;
				}
				else {
					counter++;
				}
			}
		}
		
		return encoded;
	}
	
	
	public int[][] decodeRLEBits(ArrayList<Integer> encoded, int numBits) {
		int size = encoded.size();
		int halfSize = size >> 1;
		int width = encoded.get(0); // we stored the width and height in the first 2 bytes
		int height = encoded.get(1); // we stored the width and height in the first 2 bytes
		int ii = 0; // i index
		int jj = 0; // j index
		int decodedImage[][] = new int[width][height];
		String curBitSequence = "";
		
		for (int i = 1; i < halfSize; i++) {
			int occurrences = encoded.get(i * 2);
			int bitValue = encoded.get((i * 2) + 1);
			for (int j = 0; j < occurrences; j++) {
				curBitSequence += String.valueOf(bitValue);
				if (curBitSequence.length() >= numBits) {
					String tempBit = curBitSequence.substring(0, numBits);
					curBitSequence = curBitSequence.substring(numBits); // reduce the length of curBitSequence
					int grayValue = RunLengthEncoding.convertBitToDecimal(tempBit);
					decodedImage[ii][jj] = grayValue;
					
					jj += 1;
					if (jj >= height) {
						jj = 0;
						ii += 1;
					}
				}
				
			}
		}
		
		return decodedImage;
	}
	
	/**
	 * Convert a binary string into a decimal
	 * @param binaryString
	 * @return 
	 */
	public static final int convertBitToDecimal(String binaryString) {
		int length = binaryString.length() - 1;
		int decimal = 0;
		for (int i = length; i >= 0; i--) {
			int curBit = Character.getNumericValue(binaryString.charAt(i));
			int bitShifted = length - i;
			decimal += (curBit << bitShifted);
		}
		
		return decimal;
	}
	
	
	public static ArrayList<String> convertToBinary(int initImage[][]) {
		int width = initImage.length;
		int height = initImage[0].length;
		ArrayList<String> binaryImage = new ArrayList<> (width * height);
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				String temp = Integer.toBinaryString(initImage[i][j]);
				String binaryRep = String.format("%08d", Integer.parseInt(temp)); // pad left string with 0s
				binaryImage.add(binaryRep);
			}
		}
		
		return binaryImage;
	}
	
	public String convertToString(ArrayList<Integer> encoded) {
		StringBuilder encodedStr = new StringBuilder();
		for (Integer i : encoded) {
			encodedStr.append(String.valueOf(i));
		}
		
		return encodedStr.toString();
	}
}
