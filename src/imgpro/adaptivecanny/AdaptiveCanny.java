/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.adaptivecanny;

import imgpro.MainWindow;
import imgpro.gaussianblur.EdgeDetection;
import imgpro.spatialfiltering.Sobel;

/**
 *
 * @author ToddNguyen
 */
public class AdaptiveCanny {
	private static AdaptiveCanny instance;
	private AdaptiveCanny() {};
	public static AdaptiveCanny getInstance() {
		if (instance == null) {
			instance = new AdaptiveCanny();
		}
		return instance;
	}
	
	public int[][] getAdaptiveFilter(int initImage[][], int JNCD, double K) {
		int width = initImage.length;
		int height = initImage[0].length;
		double convertedImage[][] = adaptiveBlurring(initImage, JNCD, K);
		
		Sobel sobel = Sobel.getInstance();
		EdgeDetection edge = EdgeDetection.getInstance();
		double gradientHoriz[][] = sobel.sobelFilter(convertedImage, true);
		double gradientVert[][] = sobel.sobelFilter(convertedImage, false);
		
		double gradient[][] = edge.getGradient(gradientHoriz, gradientVert);
		
		int highThreshold = MainWindow.customRound(getHighThreshold(gradient, K));
		int lowThreshold = highThreshold >> 1; // high / 2
		
		int thresholded[][] = edge.doubleThreshold(gradientHoriz, gradientVert,
				highThreshold, lowThreshold);
		
		return thresholded;
	}
	
	public double[][] adaptiveBlurring(int initImage[][], int JNCD, double K) {
		int width = initImage.length;
		int height = initImage[0].length;
		double convertedImage[][] = new double[width][height];
		int maskSize = 5;
		int paddedImage[][] = MainWindow.paddedImage(initImage, maskSize);
				
		// Adaptive smoothing
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				maskSize = 5; // Reset mask size to 5
				int radius = maskSize >> 1;
				double sigma = 1.0;
				int localArea[][] = MainWindow.getLocalArea(paddedImage, i, j, maskSize);
				
				double diff = getDifferenceMeanChange(localArea);
				double results;
				
				// If difference is less than JNCD, smooth the image
				if (diff < JNCD) {
					double weights[][] = getWeightsLessJNCD(localArea, maskSize, sigma);
					results = MainWindow.convolveOrCorrelateLocal(localArea, weights, true);
				}
				// If the difference is greater than JNCD, minimize the filter size
				else {
					maskSize = 3;
					radius = maskSize >> 1;
					localArea = MainWindow.getLocalArea(paddedImage, i, j, maskSize);
					diff = getDifferenceMeanChange(localArea);
					
					if (diff < JNCD) {
						sigma = 1.2;
						double weights[][] = getWeightsLessJNCD(localArea, maskSize, sigma);
						results = MainWindow.convolveOrCorrelateLocal(localArea, weights, true);
					}
					else {
						sigma = 1.2;
						double weights[][] = getWeightsLessJNCD(localArea, maskSize, sigma);
						results = MainWindow.convolveOrCorrelateLocal(localArea, weights, true);
//						double mean = getMean(localArea);
//						int centerPixel = localArea[radius][radius];
//						double centerMinusMean = centerPixel - mean;
//						centerMinusMean = Math.sqrt(centerMinusMean * centerMinusMean);
//						sigma = 1.2;
//						double weights[][] = getWeightsLessJNCD(localArea, maskSize, sigma);
//						double newWeights[][] = new double[maskSize][maskSize];
//
//						for (int aa = 0; aa < maskSize; aa++) {
//							for (int bb = 0; bb < maskSize; bb++) {
//								double temp = localArea[aa][bb] - mean;
//								temp = Math.sqrt(temp * temp);
//								temp = temp * centerMinusMean;
//								temp = temp / (centerMinusMean * centerMinusMean);
//								newWeights[aa][bb] = temp * weights[aa][bb];
//							}
//						}
//						results = MainWindow.convolveOrCorrelateLocal(localArea, newWeights, false);
					}
				}
				
				convertedImage[i][j] = results;
			}	
		}
		return convertedImage;
	}
	
	public double getDifferenceMeanChange(int localArea[][]) {
		int maskSize = localArea.length;
		int radius = maskSize >> 1;
		
		// Get the mean
		double total = 0.0;
		double diff = 235000;
		for (int i = 0; i < maskSize; i++) {
			for (int j = 0; j < maskSize; j++) {
				total += localArea[i][j];
				double tempDiff = localArea[radius][radius] - localArea[i][j];
				// Obtain only the smallest difference
				if (tempDiff < diff) diff = tempDiff;
			}
		}
		double mean = total / (maskSize * maskSize);
		double result = Math.abs(mean - diff);
		
		return result;
	}
	
	private double[][] generateMask(int maskSize, double value) {
		double mask[][] = new double[maskSize][maskSize];
		for (int i = 0; i < maskSize; i++) {
			for (int j = 0; j < maskSize; j++) {
				mask[i][j] = value;
			}
		}
		return mask;
	}
	
	private double[][] getWeightsLessJNCD(int localArea[][], int maskSize, double sigma) {
		int radius = maskSize >> 1;
		double weights[][] = new double[maskSize][maskSize];
		double std_dev = sigma * sigma;
		
		for (int i = 0; i < maskSize; i++) {
			for (int j = 0; j < maskSize; j++) {
				int iDiff = radius - i;
				iDiff = iDiff * iDiff;
				int jDiff = radius - j;
				jDiff = jDiff * jDiff;
				Double temp = Math.exp(-1.0 * (iDiff + jDiff) / (2.0 * std_dev));
				
				double dividedBy = Math.sqrt(2.0 * Math.PI) * sigma;
				weights[i][j] = temp / dividedBy;
			}
		}
		
		return weights;
	}
	
	private double getMean(int localArea[][]) {
		int width = localArea.length;
		int height = localArea[0].length;
		double total = 0.0;
		
		for (int i[] : localArea) {
			for (int j : i) {
				total += j;
			}
		}
		total = total / (width * height);
		return total;
	}
	
	private double getMean(double localArea[][]) {
		int width = localArea.length;
		int height = localArea[0].length;
		double total = 0.0;
		
		for (double i[] : localArea) {
			for (double j : i) {
				total += j;
			}
		}
		total = total / (width * height);
		return total;
	}
	
	private double getHighThreshold(double initImage[][], double K) {
		int width = initImage.length;
		int height = initImage[0].length;
		
		double mean = getMean(initImage);
		double total = 0.0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double temp = initImage[i][j] - mean;
				temp = temp * temp;
				total += temp;
			}
		}
		double meanVariance = total / (width * height);
		
		double highThreshold = K * (meanVariance / mean);
		
		return highThreshold;
	}
	
	private void newWeights() {
//		double mean = getMean(localArea);
//		int centerPixel = localArea[radius][radius];
//		double centerMinusMean = centerPixel - mean;
//		sigma = 1.2;
//		double weights[][] = getWeightsLessJNCD(localArea, maskSize, sigma);
//		double newWeights[][] = new double[maskSize][maskSize];
//
//		for (int aa = 0; aa < maskSize; aa++) {
//			for (int bb = 0; bb < maskSize; bb++) {
//				double temp = localArea[aa][bb] - mean;
//				temp = temp * centerMinusMean;
//				temp = temp / (centerMinusMean * centerMinusMean);
//				newWeights[aa][bb] = temp * weights[aa][bb];
//			}
//		}
//		results = MainWindow.convolveOrCorrelateLocal(localArea, newWeights, false);
	}
}
