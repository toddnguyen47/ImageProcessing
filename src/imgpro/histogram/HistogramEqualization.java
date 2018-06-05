/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.histogram;

import imgpro.MainWindow;
import static imgpro.MainWindow.customRound;
import javax.swing.JFrame;

/**
 *
 * @author ToddNguyen
 */
public class HistogramEqualization {	
	private static HistogramEqualization instance;
	private HistogramEqualization() {};
	public static HistogramEqualization getInstance() {
		if (instance == null) {
			instance = new HistogramEqualization();
		}
		return instance;
	}
	
	public int[][] histEqual(java.awt.event.ActionEvent evt, JFrame frame, int currentBit,
			int initImage[][], String choice) {
		// Global Histogram
		if (choice.equalsIgnoreCase(MainWindow.HIST_CHOICES[0])) {
			return globalHistogramEqualization(evt, currentBit, initImage);
		}
		// Local 3x3
		else if (choice.equalsIgnoreCase(MainWindow.HIST_CHOICES[1])) {
			return localHistogram(evt, currentBit, initImage, 3);
		}
		// Local 5x5
		else if (choice.equalsIgnoreCase(MainWindow.HIST_CHOICES[2])) {
			return localHistogram(evt, currentBit, initImage, 5);
		}
		// Local 7x7
		else if (choice.equalsIgnoreCase(MainWindow.HIST_CHOICES[3])) {
			return localHistogram(evt, currentBit, initImage, 7);
		}
		// Local 9x9
		else if (choice.equalsIgnoreCase(MainWindow.HIST_CHOICES[4])) {
			return localHistogram(evt, currentBit, initImage, 9);
		}
		
		return null;
	}
	
	private int[][] globalHistogramEqualization(java.awt.event.ActionEvent evt, int currentBit,
			int initImage[][]) {
		// Convert image
		int njCountUp[] = getNkCount(initImage, currentBit);
		int convertedImage[][] = new int[initImage.length][initImage[0].length];
		for (int i = 0; i < initImage.length; i++) {
			for (int j = 0; j < initImage[i].length; j++) {
				convertedImage[i][j] = njCountUp[initImage[i][j]];
			}
		}
		
		return convertedImage;
	}
	
	private int[][] localHistogram(java.awt.event.ActionEvent evt, int currentBit,
			int initImage[][], int maskSize) {
		int width = initImage.length;
		int height = initImage[0].length;
		int convertedImage[][] = new int[width][height];
		int paddedImage[][] = MainWindow.paddedImage(initImage, maskSize);
		int oneDir = maskSize >> 1;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int localArea[][] = MainWindow.getLocalArea(paddedImage, i, j, maskSize);
				
				int centerPixel = localArea[oneDir][oneDir];
				int nkCount[] = getNkCount(localArea, currentBit);			
				centerPixel = nkCount[centerPixel];
				
				convertedImage[i][j] = centerPixel;
			}
		}
		
		return convertedImage;
	}
	
	private int[] getNkCount(int inputArray[][], int currentBit) {
		int maxGrayscale = 2 << (currentBit - 1); //
		int totalSizeMN = inputArray.length * inputArray[0].length;
		int nkCounter[] = new int[maxGrayscale]; 
		
		for (int i = 0; i < maxGrayscale; i++) {
			nkCounter[i] = 0;
		}
		
		for (int[] initImage1 : inputArray) {
			for (int j = 0; j < initImage1.length; j++) {
				nkCounter[initImage1[j]]++;
			}
		}
		
		int njCountUp[] = new int[maxGrayscale];
		// Count the added up values and divide it by the total image size
		njCountUp[0] = nkCounter[0];
		for (int i = 1; i < maxGrayscale; i++) {
			njCountUp[i] = nkCounter[i] + njCountUp[i-1];
		}
		
		// Calculate values
		for (int i = 0; i < maxGrayscale; i++) {
			double temp = (double) njCountUp[i] / totalSizeMN * (maxGrayscale - 1);
			njCountUp[i] = customRound(temp);
		}
		
		return njCountUp;
	}
}
