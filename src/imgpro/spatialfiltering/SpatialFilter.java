/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.spatialfiltering;

import imgpro.MainWindow;
import java.awt.Frame;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author ToddNguyen
 */
public class SpatialFilter {
	private static SpatialFilter instance;
	private SpatialFilter() {};
	public static SpatialFilter getInstance() {
		if (instance == null) {
			instance = new SpatialFilter();
		}
		return instance;
	}
	
	public int[][] getSpatialFilter(Frame frame, int inputArray[][], String algo,
		JTextField tfMaskSize, JTextField highboostConstant) {
		int maskSize = 0;
		int hboostConstant = 0;
		try {
			maskSize = Integer.valueOf(tfMaskSize.getText());
			hboostConstant = Integer.valueOf(highboostConstant.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame, "Mask size or high boost constant is "
					+ "not a digit.");
			e.printStackTrace(System.out);
		}
		
		// Smoothing Averaging Filter
		if (algo.equals(MainWindow.SMOOTHING_CHOICES[0])) {
			return smoothingFilter(inputArray, maskSize);
		}
		// Median Filter
		else if (algo.equals(MainWindow.SMOOTHING_CHOICES[1])) {
			return medianFilter(inputArray, maskSize);
		}
		// Laplacian
		else if (algo.equals(MainWindow.SMOOTHING_CHOICES[2])) {
			return laplacianFilter(inputArray, maskSize);
		}
		// High-boost filtering
		else if (algo.equals(MainWindow.SMOOTHING_CHOICES[3])) {
			return highBoostFilter(inputArray, maskSize, hboostConstant);
		}
		
		return null;
	}
	
	private int[][] smoothingFilter(int inputArray[][], int maskSize) {
		int convertedImage[][] = new int[inputArray.length][inputArray[0].length];
		int paddedImage[][] = MainWindow.paddedImage(inputArray, maskSize);
		int oneDir = maskSize >> 1;
		
		int smoothMask[][] = generateMaskAllOnes(maskSize, true);
		double maskSizeTotal = getMaskTotal(smoothMask);
		
		int minValue = 1073741824; // to represent the smallest value for scaling purposes
		int maxValue = -(1073741824);
		
		// Obtain scaled values from mask
		for (int row = 0; row < convertedImage.length; row++) {
			for (int col = 0; col < convertedImage[row].length; col++) {
				int localArea[][] = MainWindow.getLocalArea(paddedImage, row,
						col, maskSize);
				int total = getLocalAreaSum(localArea, smoothMask);
				int newGrValue = MainWindow.customRound((double) total / maskSizeTotal);
				if (newGrValue > MainWindow.MAX_GRAYSCALE_VALUE)
					newGrValue = MainWindow.MAX_GRAYSCALE_VALUE; // to prevent RGB values going over 255
				
				if (newGrValue > maxValue) maxValue = newGrValue;
				if (newGrValue < minValue) minValue = newGrValue;
				
				convertedImage[row][col] = newGrValue;
			}
		}
		
		return convertedImage;
	}
	
	private int[][] medianFilter(int inputArray[][], int maskSize) {
		int width = inputArray.length;
		int height = inputArray[0].length;
		int convertedImage[][] = new int[width][height];
		int paddedImage[][] = MainWindow.paddedImage(inputArray, maskSize);
		int oneDir = maskSize >> 1;
				
		int maskOnes[][] = generateMaskAllOnes(maskSize, true);
		
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				int localArea[][] = MainWindow.getLocalArea(paddedImage, row,
						col, maskSize);
				convertedImage[row][col] = getLocalMedian(localArea, maskOnes);
			}
		}
		
		return convertedImage;
	}
	
	private int[][] laplacianFilter(int inputArray[][], int maskSize) {		
		int height = inputArray.length;
		int width = inputArray[0].length;
		int convertedImage[][];
		int oneDir = maskSize >> 1;
		int paddedImage[][] = MainWindow.paddedImage(inputArray, maskSize);
		int laplacianMask[][] = generateMaskAllOnes(maskSize, false);
		laplacianMask[oneDir][oneDir] = (maskSize * maskSize) - 1;
		
		convertedImage = getLaplacianImageHelper(height, width, maskSize, laplacianMask, paddedImage);
		
		int minValue = 1073741824;
		int maxValue = -(1073741824);
		
		// Original image plus new converted image since the center
		// pixel value is positive
		for (int row = 0; row < inputArray.length; row++) {
			for (int col = 0; col < inputArray[row].length; col++) {
				int result = inputArray[row][col] + convertedImage[row][col];
				if (result < minValue) minValue = result;
				if (result > maxValue) maxValue = result;
				convertedImage[row][col] = result;
			}
		}
		convertedImage = rgbScaledImage(convertedImage, minValue, maxValue);
				
		return convertedImage;
	}
	
	private int[][] highBoostFilter(int inputArray[][], int maskSize, int hboostConstant) {
		if (hboostConstant < 0) return null;
				
		int height = inputArray.length;
		int width = inputArray[0].length;
		int convertedImage[][] = new int[height][width];
		int gmask[][] = new int[height][width];
		int paddedImage[][] = MainWindow.paddedImage(inputArray, maskSize);
		int oneDir = maskSize >> 1;
		
		 // always smooth the image by a mask of 3
		int blurredImage[][] = smoothingFilter(inputArray, 3);
				
		int minValue = 1073741824;
		int maxValue = -(1073741824);
		
		// Get the unsharp mask
		for (int row = 0; row < inputArray.length; row++) {
			for (int col = 0; col < inputArray[row].length; col++) {
				int result = (inputArray[row][col]) - blurredImage[row][col];
				if (result < minValue) minValue = result;
				if (result > maxValue) maxValue = result;
				gmask[row][col] = result;
			}
		}
		
		//gmask = rgbScaledImage(gmask, minValue, maxValue);
				
		// Scale the image now
		minValue = 1073741824;
		maxValue = -(1073741824);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int result = (inputArray[row][col]) + (gmask[row][col] * hboostConstant);
				if (result < minValue) minValue = result;
				if (result > maxValue) maxValue = result;
				convertedImage[row][col] = result;
			}
		}
				
		convertedImage = rgbScaledImage(convertedImage, minValue, maxValue);
		return convertedImage;
	}
	
	/**
	 * Generate a mask with all 1s, either positive or negative.
	 * @param maskSize The desired mask size.
	 * @param isPositive True if 1s are positive, false if 1s are negatives.
	 * @return 
	 */
	public int[][] generateMaskAllOnes(int maskSize, boolean isPositive) {
		// Create a mask of all 1s
		int mask[][] = new int[maskSize][maskSize];
		for (int row = 0; row < maskSize; row++) {
			for (int col = 0; col < maskSize; col++) {
				if (isPositive)
					mask[row][col] = 1;
				else
					mask[row][col] = -1;
			}
		}
		return mask;
	}
	
	private int[][] generateLaplacianMask4sAnd1s(int maskSize) {
		int mask[][] = new int[maskSize][maskSize];
		int oneDir = maskSize >> 1;
		
		for (int row = 0; row < maskSize; row++) {
			for (int col = 0; col < maskSize; col++) {
				if (row == oneDir || col == oneDir) {
					mask[row][col] = -1;
				}
				else {
					mask[row][col] = 0;
				}
			}
		}
		
		mask[oneDir][oneDir] = (oneDir * 4);
		
		return mask;
	}
	
	private double getMaskTotal(int mask[][]) {
		double maskSizeTotal = 0;
		int maskSize = mask.length;
		for (int row = 0; row < maskSize; row++) {
			for (int col = 0; col < maskSize; col++) {
				maskSizeTotal += mask[row][col];
			}
		}
		
		return maskSizeTotal;
	}
	
	/**
	 * Get the total multiplied value of the local area * the mask values
	 * @param localArea The current local area being looked at.
	 * @param mask The mask used to multiply by the local area
	 * @return The sum of all multiplied numbers
	 */
	public int getLocalAreaSum(int localArea[][], int mask[][]) {
		int total = 0;
		
		for (int row = 0; row < localArea.length; row++) {
			for (int col = 0; col < localArea[row].length; col++) {
				total += (localArea[row][col] * mask[row][col]);
			}
		}
		
		return total;
	}
	
	private int getLocalMedian(int localArea[][], int mask[][]) {
		int oneDimArr[] = new int[localArea.length * localArea[0].length];
		int oneDimIndex = 0;
		int nonZeroCounter = 0;
		
		for (int row = 0; row < localArea.length; row++) {
			for (int col = 0; col < localArea.length; col++) {
				int result = localArea[row][col] * mask[row][col];
				oneDimArr[oneDimIndex++] = result;
				if (result != 0) nonZeroCounter++;
			}
		}
		
		int nonZeroArr[] = new int[nonZeroCounter];
		int tempIndex = 0;
		for (int i = 0; i < nonZeroCounter; i++) {
			while (oneDimArr[tempIndex] == 0) {tempIndex++;}
			nonZeroArr[i] = oneDimArr[tempIndex++];
		}
		int result;
		
		// If there are only zeros
		if (nonZeroCounter <= 0) {
			result = 0;
		}
		else {		
			Arrays.sort(nonZeroArr);

			int halfway = nonZeroArr.length >> 1;
			// Odd number
			if (nonZeroArr.length % 2 == 1) {
				result = nonZeroArr[halfway];
			}
			else {
				double one = nonZeroArr[halfway];
				double two = nonZeroArr[halfway - 1];
				result = (int) Math.floor((one + two) / 2);
			}
		}
		if (result > MainWindow.MAX_GRAYSCALE_VALUE)
			result = MainWindow.MAX_GRAYSCALE_VALUE;
		
		return result;
	}
	
	/**
	 * Scale an image with the smallest value set to 0 grayscale while
	 * the largest value is set to 255.<br />
	 * <strong>NOTE:</strong> Please do NOT adjust any min values or max values before
	 * using this method!
	 * @param inputArr The input array
	 * @param minValue The smallest value of the array
	 * @param maxValue The largest value of the array/
	 * @return 
	 */
	public int[][] rgbScaledImage(int inputArr[][], int minValue, int maxValue) {
		int adjustedMax = maxValue - minValue;
		int scaledImage[][] = new int[inputArr.length][inputArr[0].length];
		
		// Scale the image then apply the mask
		for (int row = 0; row < inputArr.length; row++) {
			for (int col = 0; col < inputArr[row].length; col++) {
				double temp = ((double) inputArr[row][col]) - minValue;
				temp = temp / adjustedMax;
				int result = MainWindow.customRound(temp * MainWindow.MAX_GRAYSCALE_VALUE);
				if (result > MainWindow.MAX_GRAYSCALE_VALUE)
					result = MainWindow.MAX_GRAYSCALE_VALUE;
				scaledImage[row][col] = result;			
			}
		}
		
		return scaledImage;
	}
	
	/**
	 * Get the laplacian filtered image.
	 * @param initImgHeight Height of current image
	 * @param initImgWidth Width of current image
	 * @param maskSize Mask size
	 * @param laplacianMask The generated laplacian mask
	 * @param paddedImage The zero-padded image
	 * @return The laplacian-filtered image
	 */
	private int[][] getLaplacianImageHelper(int initImgHeight, int initImgWidth, int maskSize,
			int laplacianMask[][], int paddedImage[][]) {
		int convertedImage[][] = new int[initImgHeight][initImgWidth];
		int oneDir = maskSize >> 1;
		int minValue = 1073741824;
		int maxValue = -(minValue);
		
		for (int row = 0; row < convertedImage.length; row++) {
			for (int col = 0; col < convertedImage[row].length; col++) {
				int localArea[][] = MainWindow.getLocalArea(paddedImage, row,
						col, maskSize);
				int total = getLocalAreaSum(localArea, laplacianMask);
				if (total < minValue) minValue = total;
				if (total > maxValue) maxValue = total;
				
				convertedImage[row][col] = total;
			}
		}
		//convertedImage = rgbScaledImage(convertedImage, minValue, maxValue);
		return convertedImage;
	}
}
