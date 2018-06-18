/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.gaussianblur;

import imgpro.MainWindow;

/**
 *
 * @author ToddNguyen
 */
public class GaussianBlur {
	private static GaussianBlur instance;
	private GaussianBlur() {};
	
	public static GaussianBlur getInstance() {
		if (instance == null) {
			instance = new GaussianBlur();
		}
		return instance;
	}
	
	public double[][] gaussianBlur1D(double initImage[][], double sigma, boolean isXDir) {
		int row  = initImage.length;
		int col = initImage[0].length;
		double convertedImage[][] = new double[row][col];
		double default_truncate = 4.0;
		double std_dev = (double) sigma;
		int radius = (int) Math.floor(default_truncate * std_dev + 0.5);
				
		double weights[] = new double[2 * radius + 1];
		weights[radius] = 1.0; // the center of bell-shaped distribution is 1.0
		double total_sum = 1.0;
		std_dev = std_dev * std_dev; // sigma squared
		
		// Calculate the kernel
		for (int i = 1; i < radius + 1; i++) {
			double temp = Math.exp(-1.0 * (i * i) / (std_dev * 2.0));
			weights[radius + i] = temp;
			weights[radius - i] = temp;
			total_sum += (2.0 * temp);
		}
		// Normalize the kernel
		for (int i = 0; i < 2 * radius + 1; i++) {
			weights[i] /= total_sum;
		}
		
		// If performing convolution in the y-direction,
		// transpose the rows and col
		if (!isXDir) {
			int temp = row;
			row = col;
			col = temp;
		}
		
		for (int i = 0; i < row; i++) {
			int maxLen = col + (radius * 2);			
			// Pad the image
			double paddedSlice[] = new double[maxLen];
			for (int j = 0; j < maxLen; j++) {
				if (j >= radius && j < col + radius) {
					// Convolution in the x direction
					if (isXDir) {
						paddedSlice[j] = initImage[i][j - radius];
					}
					else {
						paddedSlice[j] = initImage[j - radius][i];
					}
				}
				else {
					paddedSlice[j] = 0;
				}
			}			
			
			// Actual convolution process
			for (int j = 0; j < col; j++) {
				double total = 0.0;
				// Per local area
				for (int k = j, w_index = 0; k < (j + radius * 2); k++, w_index++) {
					double temp = weights[w_index] * paddedSlice[k];
					total += temp;
				}
				if (isXDir) {
					convertedImage[i][j] = total;
				} else {
					convertedImage[j][i] = total;
				}
			}
		}
		return convertedImage;
	}
	
	public double[][] gaussianBlur(int initImage[][], double sigma) {
		int row = initImage.length;
		int col = initImage[0].length;
		double convertedImage[][] = new double[row][col];
		
		// Convert image to double integer array
		for(int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				convertedImage[i][j] = initImage[i][j];
			}
		}
		
		// Gaussian in the x direction
		convertedImage = gaussianBlur1D(convertedImage, sigma, true);
		// Gaussian in the y direction
		convertedImage = gaussianBlur1D(convertedImage, sigma, false);
				
		return convertedImage;
	}
}
