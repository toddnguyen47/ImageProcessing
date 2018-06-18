/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.gaussianblur;

import imgpro.MainWindow;
import imgpro.spatialfiltering.Sobel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author ToddNguyen
 */
public class EdgeDetection {
	private static EdgeDetection instance;
	private EdgeDetection() {};
	private int edges[][];
	private int keyS[][];
	
	public static EdgeDetection getInstance() {
		if (instance == null) {
			instance = new EdgeDetection();
		}
		return instance;
	}
	
	public int[][] edgeDetect(int initImage[][], double sigma, int lowThreshold, int highThreshold) {
		int row = initImage.length;
		int col = initImage[0].length;
		double convertedImage[][];
		
		// Gaussian blur to reduce noise
		convertedImage = GaussianBlur.getInstance().gaussianBlur(initImage, sigma);
		// Use sobel filters to get horizontal and vertical gradients
		double img_h[][] = Sobel.getInstance().sobelFilter(convertedImage, true);
		double img_v[][] = Sobel.getInstance().sobelFilter(convertedImage, false);
				 
		// Threshold
		this.edges = doubleThreshold(img_h, img_v, highThreshold, lowThreshold);
		return this.edges;
	}
	
	public double[][] getGradient(double horizGradient[][], double vertGradient[][]) {
		int row = horizGradient.length;
		int col = horizGradient[0].length;
		// Get gradient
		double gradient[][] = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				double h_squared = Math.pow(horizGradient[i][j], 2.0);
				double v_squared = Math.pow(vertGradient[i][j], 2.0);
				gradient[i][j] = Math.pow(h_squared + v_squared, 0.5);
			}
		}
		return gradient;
	}
	
	public int[][] doubleThreshold(double horizGradient[][],
			double vertGradient[][], int highThreshold,	int lowThreshold) {
		int row = horizGradient.length;
		int col = horizGradient[0].length;
		
		double gradient[][] = getGradient(horizGradient, vertGradient);
		
		// Get direction
		double theta[][] = new double[row][col];
		// Quantize the direction
		int thetaQuan[][] = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				theta[i][j] = Math.atan2(vertGradient[i][j], horizGradient[i][j]);
				thetaQuan[i][j] = (int) (MainWindow.evenRound(theta[i][j] * (5.0 / Math.PI)) + 5) % 5;
			}
		}
		
		// Non-maximum suppression
		double gradCopy[][] = gradient.clone();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				// Suppress the pixels at image edge
				if (i == 0 || i == row - 1 || j == 0 || j == col - 1) {
					gradCopy[i][j] = 0;
					continue;
				}
				
				int thetaq = thetaQuan[i][j] % 4;
				switch (thetaq) {
					// E or W
					case 0:
						if (gradient[i][j] <= gradient[i][j - 1] ||
								gradient[i][j] <= gradient[i][j + 1]) {
							gradCopy[i][j] = 0;
						}	break;
					// NE or SW
					case 1:
						if (gradient[i][j] <= gradient[i-1][j+1] ||
								gradient[i][j] <= gradient[i+1][j-1]) {
							gradCopy[i][j] = 0;
						}	break;
					// N or S
					case 2:
						if (gradient[i][j] <= gradient[i-1][j] ||
								gradient[i][j] <= gradient[i+1][j]) {
							gradCopy[i][j] = 0;
						}	break;
					// NW or SE
					case 3:
						if (gradient[i][j] <= gradient[i-1][j-1] ||
								gradient[i][j] <= gradient[i+1][j+1]) {
							gradCopy[i][j] = 0;
						}	break;
					default:
						break;
				}
			}
		}
				
		// Double threshold
		int strongEdges[][] = new int[row][col];
		int thresholdedEdges[][] = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				// Strong has a value of 2
				// Weak has a value of 1
				boolean tempHigh = gradCopy[i][j] > highThreshold;
				int tempIntHigh = tempHigh ? 1 : 0;
				strongEdges[i][j] = tempIntHigh;
				
				boolean tempLow = gradCopy[i][j] > lowThreshold;
				int tempInt = tempLow ? 1 : 0;
				thresholdedEdges[i][j] += (tempIntHigh + tempInt);
			}
		}
		
		int finalEdges[][] = strongEdges.clone();
		ArrayList<int[]> currentPixels = new ArrayList<>();
		// Tracing edges with hysteresis
		// Find weak edge pixels near strong edge pixels
		for (int i = 1; i < row - 1; i++) {
			for (int j = 1; j < col - 1; j++) {
				if (thresholdedEdges[i][j] != 1) {
					continue; // not a weak pixel
				}
				// Get a 3x3 patch
				int patchMax = -(100000);
				for (int ii = 0; ii < 3; ii++) {
					for (int jj = 0; jj < 3; jj++) {
						int patchTemp = thresholdedEdges[i-1+ii][j-1+jj];
						if (patchTemp > patchMax) patchMax = patchTemp;
					}
				}
				if (patchMax == 2) {
					currentPixels.add(new int[] {i, j});
					finalEdges[i][j] = 1;
				}
			}
		}
		
		// Extend strong edges based on current pixels
		while (currentPixels.size() > 0) {
			ArrayList<int[]> newPix = new ArrayList<>();
			for (int[] coordinates : currentPixels) {
				int curRow = coordinates[0];
				int curCol = coordinates[1];
				// Delta r and delta c
				for (int dr = -1; dr < 2; dr++) {
					for (int dc = -1; dc < 2; dc++) {
						if (dr == 0 && dc == 0) continue;
						int r2 = curRow + dr;
						int c2 = curCol + dc;
						if (thresholdedEdges[r2][c2] == 1 &&
							finalEdges[r2][c2] == 0) {
							newPix.add(new int[] {r2, c2});
							finalEdges[r2][c2] = 1;
						}
					}
				}
			}
			currentPixels = newPix;
		}
		
		// Scaling. 0 = 0, 1 = 255
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				finalEdges[i][j] *= MainWindow.MAX_GRAYSCALE_VALUE;
			}
		}
		
		return finalEdges;
	}
	
	public int[][] getEdges() {return edges;}
	public int[][] getKeyS() {return this.keyS;}
	
	public int[][] getRandomEdgeCells(int edges[][], int L) {
		int numberOfPtsChosen = L; // Choose L^2 locations
		int row = edges.length;
		int col = edges[0].length;
		int threshold = 200;
		
		// Obtain only edges of image
		ArrayList<int[]> whiteEdges = new ArrayList<>();
		// We skip the first row
		for (int i = 1; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (edges[i][j] >= threshold) {
					whiteEdges.add(new int[] {i, j});
				}
			}
		}
		
		keyS = new int[numberOfPtsChosen][2];
		Random random = new Random();
		
		Set<String> generated = new HashSet<>();
		while (generated.size() < L) {
			int randIndex = random.nextInt(whiteEdges.size());
			int coordsTemp[] = whiteEdges.get(randIndex);
			String coords = String.valueOf(coordsTemp[0]) + "," + String.valueOf(coordsTemp[1]);
			generated.add(coords);
		}
		
		Iterator<String> iter = generated.iterator();
		int index = 0;
		while (iter.hasNext()) {
			String coords = iter.next();
			String split[] = coords.split(",");
			int rrow = Integer.valueOf(split[0]);
			int rcol = Integer.valueOf(split[1]);
			keyS[index++] = new int[] {rrow, rcol};
		}
				
		return keyS;
	}
}
