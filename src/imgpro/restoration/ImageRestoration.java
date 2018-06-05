/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.restoration;

import imgpro.MainWindow;
import imgpro.spatialfiltering.SpatialFilter;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author ToddNguyen
 */
public class ImageRestoration {
	private final int MAX_FILTER = 0;
	private final int MIN_FILTER = 1;
	private final int MIDPOINT_FILTER = 2;
	private static ImageRestoration instance;
	private ImageRestoration(){};
	private SpatialFilter spatial = SpatialFilter.getInstance();
	
	public static ImageRestoration getInstance() {
		if (instance == null) {
			instance = new ImageRestoration();
		}
		return instance;
	}
	
	public int[][] getRestoredImage(Frame frame, int inputArray[][], String algo,
		JTextField tfMaskSize, JTextField tfExtraOption) {
		int maskSize = 0;
		int extraOption = 0;
		
		try {
			maskSize = Integer.valueOf(tfMaskSize.getText());
			extraOption = Integer.valueOf(tfExtraOption.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame, "Mask size or high boost constant is "
					+ "not a digit.");
			e.printStackTrace(System.out);
		}
		int convertedImage[][] = null;
				
		// Arithmetic mean
		if (algo.equalsIgnoreCase(MainWindow.RESTORATION_CHOICES[0])) {
			convertedImage = arithmeticMean(inputArray, maskSize);
		}
		// Geometric mean
		else if (algo.equalsIgnoreCase(MainWindow.RESTORATION_CHOICES[1])) {
			convertedImage = geometricMean(inputArray, maskSize);
		}
		// Harmonic mean filter
		else if (algo.equalsIgnoreCase(MainWindow.RESTORATION_CHOICES[2])) {
			convertedImage = harmonicMean(inputArray, maskSize);
		}
		// Contraharmonic mean filter
		else if (algo.equalsIgnoreCase(MainWindow.RESTORATION_CHOICES[3])) {
			convertedImage = contraharmonicMean(inputArray, maskSize, extraOption);
		}
		// Max filter
		else if (algo.equalsIgnoreCase(MainWindow.RESTORATION_CHOICES[4])) {
			convertedImage = minMaxMidptFilter(inputArray, maskSize, MAX_FILTER);
		}
		// Min filter
		else if (algo.equalsIgnoreCase(MainWindow.RESTORATION_CHOICES[5])) {
			convertedImage = minMaxMidptFilter(inputArray, maskSize, MIN_FILTER);
		}
		// Midpoint filter
		else if (algo.equalsIgnoreCase(MainWindow.RESTORATION_CHOICES[6])) {
			convertedImage = minMaxMidptFilter(inputArray, maskSize, MIDPOINT_FILTER);
		}
		// Alpha-trimmed mean filter
		else if (algo.equalsIgnoreCase(MainWindow.RESTORATION_CHOICES[7])) {
			convertedImage = alphaTrimmed(inputArray, maskSize, extraOption);
		}
		
		return convertedImage;
	}
	
	private int[][] arithmeticMean(int[][] inputArray, int maskSize) {
		int initWidth = inputArray.length;
		int initHeight = inputArray[0].length;
		int convertedImage[][] = new int[initWidth][initHeight];
		
		int paddedImage[][] = MainWindow.paddedImage(inputArray, maskSize);
		int maskAllOnes[][] = spatial.generateMaskAllOnes(maskSize, true);
		
		for (int row = 0; row < initWidth; row++) {
			for (int col = 0; col < initHeight; col++) {
				int localArea[][] = MainWindow.getLocalArea(paddedImage, row,
						col, maskSize);
				double total = spatial.getLocalAreaSum(localArea, maskAllOnes);
				total = total / (maskSize * maskSize);
				convertedImage[row][col] = MainWindow.customRound(total);
			}
		}
		
		return convertedImage;
	}
	
	private int[][] geometricMean(int inputArray[][], int maskSize) {
		int width = inputArray.length;
		int height = inputArray[0].length;
		int convertedImage[][] = new int[width][height];
		
		int paddedImage[][] = MainWindow.paddedImage(inputArray, maskSize);
		int maskAllOnes[][] = spatial.generateMaskAllOnes(maskSize, true);
		
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				int localArea[][] = MainWindow.getLocalArea(paddedImage, row, col, maskSize);
				double total = 1.0;
				for (int i = 0; i < maskSize; i++) {
					for (int j = 0; j < maskSize; j++) {
						double temp = localArea[i][j] * maskAllOnes[i][j];
						if (temp != 0) {total = temp * total;}
					}
				}
				double tempPower = 1.0 / (maskSize * maskSize);
				total = Math.pow(total, tempPower);
				convertedImage[row][col] = MainWindow.customRound(total);
			}
		}
		
		return convertedImage;
	}
	
	private int[][] harmonicMean(int inputArray[][], int maskSize) {
		int width = inputArray.length;
		int height = inputArray[0].length;
		int convertedImg[][] = new int[width][height];
		int paddedImage[][] = MainWindow.paddedImage(inputArray, maskSize);
		
		int maskAllOnes[][] = spatial.generateMaskAllOnes(maskSize, true);
		double maskSizeSquared = maskSize * maskSize;
		int minValue = 1073741824;
		int maxValue = -(minValue);
		
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				int localArea[][] = MainWindow.getLocalArea(paddedImage, row, col,
						maskSize);
				int sum = spatial.getLocalAreaSum(localArea, maskAllOnes);
				double temp = maskSizeSquared / (1.0 / sum);
				
				int intTemp = MainWindow.customRound(temp);
				if (intTemp > maxValue) maxValue = intTemp;
				if (intTemp < minValue) minValue = intTemp;
				
				convertedImg[row][col] = intTemp;
			}
		}
		
		convertedImg = spatial.rgbScaledImage(convertedImg, minValue, maxValue);
		return convertedImg;
	}
	
	private int[][] contraharmonicMean(int inputArray[][],
			int maskSize, int contraOrder) {
		int width = inputArray.length;
		int height = inputArray[0].length;
		int convertedImg[][] = new int[width][height];
		
		int paddedImage[][] = MainWindow.paddedImage(inputArray, maskSize);
		int maskAllOnes[][] = spatial.generateMaskAllOnes(maskSize, true);
		
		int minValue = 1073741824;
		int maxValue = -(minValue);
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int localArea[][] = MainWindow.getLocalArea(paddedImage,
						i, j, maskSize);
				double numerator = 0;
				double denominator = 0;
				// Obtain the harmonic mean
				for (int i2 = 0; i2 < maskSize; i2++) {
					for (int j2 = 0; j2 < maskSize; j2++) {
						double temp = localArea[i2][j2] * maskAllOnes[i2][j2];
						numerator += Math.pow(temp, contraOrder + 1);
						denominator += Math.pow(temp, contraOrder);
					}
				}				
				
				int result = MainWindow.customRound(numerator / denominator);
				if (result > maxValue) maxValue = result;
				if (result < minValue) minValue = result;
				convertedImg[i][j] = result;
			}
		}
		convertedImg = spatial.rgbScaledImage(convertedImg, minValue, maxValue);
		return convertedImg;
	}
	
	/**
	 * Return the max/min value of a loacl area.
	 * @param inputArray The non-padded input array
	 * @param maskSize The mask size
	 * @param isMaxFilter True if this is a max filter, False for a min filter
	 * @return 
	 */
	private int[][] minMaxMidptFilter(int inputArray[][], int maskSize, int filterChoice) {
		int width = inputArray.length;
		int height = inputArray[0].length;
		int convertedImage[][] = new int[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int min = 1073741824;
				int max = -(min);
				int localAreaNoMask[][] = MainWindow.getLocalAreaNoMask(inputArray, i, j, maskSize);
				for (int i2 = 0; i2 < localAreaNoMask.length; i2++) {
					for (int j2 = 0; j2 < localAreaNoMask[i2].length; j2++) {
						int result = localAreaNoMask[i2][j2];
						if (result > max) max = result;
						if (result < min) min = result;
					}
				}
				switch (filterChoice) {
					case MAX_FILTER:
						convertedImage[i][j] = max;
						break;
					case MIN_FILTER:
						convertedImage[i][j] = min;
						break;
					case MIDPOINT_FILTER:
						double temp = 1.0/2.0 * (max + min);
						convertedImage[i][j] = MainWindow.customRound(temp);
						break;
				}
			}
		}
		
		return convertedImage;
	}
	
	private int[][] alphaTrimmed(int inputArray[][], int maskSize, int dInt) {
		int width = inputArray.length;
		int height = inputArray[0].length;
		int convertedImage[][] = new int[width][height];
		int numOfPixelValToDel = dInt;
		if (dInt > 0)
			 numOfPixelValToDel = (dInt + 1) >> 1; // dividing by half
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				TreeSet<Integer> sorted = new TreeSet<>();
				int localArea[][] = MainWindow.getLocalAreaNoMask(inputArray, i, j, maskSize);
				int tempWidth = localArea.length;
				int tempHeight = localArea[0].length;
				// Add all the pixel values to a sorted set
				for (int values[] : localArea) {
					for (int value : values) {
						sorted.add(value);
					}
				}
				
				// Obtain the lowest and highest pixel values
				ArrayList<Integer> lowestPixelValues = new ArrayList<>();
				ArrayList<Integer> highestPixelValues = new ArrayList<>();
				for (int k = 0; k < numOfPixelValToDel; k++) {
					if (!sorted.isEmpty())
						lowestPixelValues.add(sorted.pollFirst());
					if (!sorted.isEmpty())
						highestPixelValues.add(sorted.pollLast());
					
					if (sorted.isEmpty()) break; // break early if the set is empty
				}
				
				// Deleting the d/2 lowest and highest pixel values
				int numDeletes = 0;
				int sum = 0;
				for (int i2 = 0; i2 < tempWidth; i2++) {
					for (int j2 = 0; j2 < tempHeight; j2++) {
						// Delete the lowest pixel values
						for (int value : lowestPixelValues) {
							if (localArea[i2][j2] == value) {
								localArea[i2][j2] = 0;
								numDeletes++;
							}
						}
						// Delete the highest pixel values
						for (int value : highestPixelValues) {
							if (localArea[i2][j2] == value) {
								localArea[i2][j2] = 0;
								numDeletes++;
							}
						}
						sum += localArea[i2][j2];
					}
				}
				// Obtain the mean of the remaining values
				double denumerator = (tempWidth * tempHeight) - numDeletes;
				if (denumerator <= 0) denumerator = 1.0; // to prevent dividing by zero
				double temp = 1.0 / denumerator;
				convertedImage[i][j] = MainWindow.customRound(temp * sum);
			}
		}
		return convertedImage;
	}
}
