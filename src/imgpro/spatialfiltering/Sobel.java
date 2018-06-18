/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.spatialfiltering;

import imgpro.MainWindow;

/**
 *
 * @author ToddNguyen
 */
public class Sobel {
	private static Sobel instance;
	private Sobel() {};
	
	private static final double[][] MASK_HORIZ = {{-1,0,1},{-2,0,2},{-1,0,1}};
	private static final double[][] MASK_VERT  = {{1,2,1},{0,0,0},{-1,-2,-1}};
	
	public static Sobel getInstance() {
		if (instance == null) {
			instance = new Sobel();
		}
		return instance;
	}
	
	/**
	 * 
	 * @param initImage
	 * @param isHorizontal
	 * @return 
	 */
	public double[][] sobelFilter(int initImage[][], boolean isHorizontal) {
		double convertedImg[][];
		// Horizontal gradient
		if (isHorizontal) {
			convertedImg = MainWindow.convolveOrCorrelate(initImage, MASK_HORIZ, true);
		}
		// Vertical gradient
		else {
			convertedImg = MainWindow.convolveOrCorrelate(initImage, MASK_VERT, true);
		}
		return convertedImg;
	}
	
	public double[][] sobelFilter(double initImage[][], boolean isHorizontal) {
		double convertedImg[][];
		// Horizontal gradient
		if (isHorizontal) {
			convertedImg = MainWindow.convolveOrCorrelate(initImage, MASK_HORIZ, true);
		}
		// Vertical gradient
		else {
			convertedImg = MainWindow.convolveOrCorrelate(initImage, MASK_VERT, true);
		}
		return convertedImg;
	}
}
