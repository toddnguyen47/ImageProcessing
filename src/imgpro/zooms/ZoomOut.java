/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.zooms;

import imgpro.MainWindow;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author ToddNguyen
 */
public class ZoomOut {
	private static ZoomOut instance;
	private ZoomOut() {}; // private constructor used for Singleton
	
	public static ZoomOut getInstance() {
		if (instance == null) {
			instance = new ZoomOut();
		}
		return instance;
	}
	
	public int[][] zoomOutAction(java.awt.event.ActionEvent evt, JFrame frame, JTextField textFieldWidth,
		JTextField textFieldHeight, JLabel labelInitImage, int initImage[][], String algoChosen) {
		int newWidth = 0;
		int newHeight = 0;
		try {
			newWidth = Integer.valueOf(textFieldWidth.getText());
			newHeight = Integer.valueOf(textFieldHeight.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame, "Width or Height is not a digit");
		}

		int initW = labelInitImage.getIcon().getIconWidth();
		int initH = labelInitImage.getIcon().getIconHeight();

		double factor = 0;
		if (initW != 0) {
			double tempW = (double) initW;
			factor = newWidth / tempW;
		}

		// Handle nearest neighbor zoom
		if (algoChosen.equalsIgnoreCase("Nearest Neighbor")) {
			return nearestNeighbor(evt, initImage, newWidth, newHeight, factor);
		}
		// Handle linear zoom (x)
		else if (algoChosen.equalsIgnoreCase("Linear (x)")) {
			return linearZoomX(evt, initImage, newWidth, newHeight, factor);
		}
		// Handle linear zoom (y)
		else if (algoChosen.equalsIgnoreCase("Linear (y)")) {
			return linearZoomY(evt, initImage, newWidth, newHeight, factor);
		}
		// Handle bilinear zoom
		else if (algoChosen.equalsIgnoreCase("Bilinear")) {
			return bilinearZoom(evt, initImage, newWidth, newHeight, factor);
		}
		
		return null;
	}
	
	private int[][] nearestNeighbor(java.awt.event.ActionEvent evt, int[][] initImage2, int newWidth,
			int newHeight, double factor) {
		int oldW_Constant = initImage2.length;
		int oldH_Constant = initImage2[0].length;
		int zoomedOutImage[][] = new int[newWidth][newHeight];
		
		for (int i = 0; i < newWidth; i++) {
			for (int j = 0; j < newHeight; j++) {
				int oldWidth  = customRound(i / factor);
				int oldHeight = customRound(j / factor);
				
				int modWidth = customRound(i % factor);
				int modHeight = customRound(j % factor);

				if (modWidth != 0) {
					if (modWidth > customRound(factor / 2)) {
						if (oldWidth + 1 < oldW_Constant)
							oldWidth += 1;
					}
				}
				
				if (modHeight != 0) {
					if (modHeight > customRound(factor / 2)) {
						if (oldHeight + 1 < oldH_Constant)
							oldHeight += 1;
					}
				}
				
				if (oldWidth >= oldW_Constant)
					oldWidth = oldW_Constant - 1;
				if (oldHeight >= oldH_Constant)
					oldHeight = oldH_Constant - 1;
				
				zoomedOutImage[i][j] = initImage2[oldWidth][oldHeight];
			}
		}
				
		return zoomedOutImage;
	}
	
	private int[][] linearZoomX(java.awt.event.ActionEvent evt, int[][] initImage2, int newWidth,
			int newHeight, double factor) {
		int oldW_Constant = initImage2.length;
		int oldH_Constant = initImage2[0].length;
		int[][] zoomedImage = new int[newWidth][newHeight];
		
		for (int i = 0; i < newWidth; i++) {
			for (int j = 0; j < newHeight; j++) {
				int row1 = i;
				int col1 = j;
				int row2, col2, grValue1, grValue2;
				int finalGrValue = 0;
				// If the squares match with the initial image
				if (customRound(row1 % factor) == 0 && customRound(col1 % factor) == 0) {
					row1 = customRound(i / factor);
					col1 = customRound(j / factor);
					finalGrValue = initImage2[row1][col1];
				}
				// Same row
				else if (customRound(row1 % factor) == 0) {
					row1 = customRound(i / factor);
					row2 = row1;
					col1 = (int) Math.floor(col1 / factor);
					col2 = col1 + 1;
					if (col2 >= oldH_Constant) col2 = oldH_Constant - 1;
					grValue1 = initImage2[row1][col1];
					grValue2 = initImage2[row2][col2];
					int colDiff = customRound((col2 - col1) * factor);
					if (colDiff == 0) colDiff = 1;
					int colDiff2 = j - customRound(col1 * factor);
					
					// Follow the linear interpolation formula
					finalGrValue = grValue1 + (colDiff2 * (grValue2 - grValue1) / colDiff);
				}
				// Not same row nor same col
				else {
					row1 = (int) Math.floor(i / factor);
					row2 = row1;
					col1 = (int) Math.floor(col1 / factor);
					col2 = col1 + 1;
					if (col2 >= oldH_Constant) col2 = oldH_Constant - 1;
					grValue1 = initImage2[row1][col1];
					grValue2 = initImage2[row2][col2];
					int colDiff = customRound((col2 - col1) * factor);
					if (colDiff == 0) colDiff = 1;
					int colDiff2 = j - customRound(col1 * factor);
					
					// Follow the linear interpolation formula
					finalGrValue = grValue1 + (colDiff2 * (grValue2 - grValue1) / colDiff);
				}
				
				zoomedImage[i][j] = finalGrValue;
			}
		}
		
		return zoomedImage;
	}
	
	private int[][] linearZoomY(java.awt.event.ActionEvent evt, int[][] initImage2, int newWidth,
			int newHeight, double factor) {
		int oldW_Constant = initImage2.length;
		int oldH_Constant = initImage2[0].length;
		int[][] zoomedImage = new int[newWidth][newHeight];
		
		for (int i = 0; i < newWidth; i++) {
			for (int j = 0; j < newHeight; j++) {
				int row1 = i;
				int col1 = j;
				int row2, col2, grValue1, grValue2;
				int finalGrValue = 0;
				// If the squares match exactly
				if (customRound(row1 % factor) == 0 && customRound(col1 % factor) == 0) {
					row1 = customRound(row1 / factor);
					col1 = customRound(col1 / factor);
					finalGrValue = initImage2[row1][col1];
				}
				// Same column
				else if (customRound(col1 % factor) == 0) {
					col1 = customRound(col1 / factor);
					col2 = col1;
					row1 = (int) Math.floor(row1 / factor);
					row2 = row1 + 1;
					if (row2 >= oldH_Constant) row2 = oldH_Constant - 1;
					grValue1 = initImage2[row1][col1];
					grValue2 = initImage2[row2][col2];
					int rowDiff = customRound((row2 - row1) * factor);
					if (rowDiff == 0) rowDiff = 1;
					int rowDiff2 = i - customRound(row1 * factor);

					// Follow the linear interpolation formula
					finalGrValue = grValue1 + (rowDiff2 * (grValue2 - grValue1) / rowDiff);
				}
				// Every other pixels
				else {
					row1 = (int) Math.floor(row1 / factor);
					row2 = row1 + 1;
					if (row2 >= oldH_Constant) row2 = oldH_Constant - 1;
					col1 = (int) Math.floor(j / factor);
					col2 = col1 ;
					grValue1 = initImage2[row1][col1];
					grValue2 = initImage2[row2][col2];
					int colDiff = customRound((row2 - row1) * factor);
					if (colDiff == 0) colDiff = 1;
					int colDiff2 = i - customRound(row1 * factor);
					
					// Follow the linear interpolation formula
					finalGrValue = grValue1 + (colDiff2 * (grValue2 - grValue1) / colDiff);
				}
				zoomedImage[i][j] = finalGrValue;
			}
		}
		
		return zoomedImage;
	}
	
	private int[][] bilinearZoom(java.awt.event.ActionEvent evt, int[][] initImage2, int newWidth,
			int newHeight, double factor) {
		int oldW_Constant = initImage2.length;
		int oldH_Constant = initImage2[0].length;
		int[][] zoomedImage = new int[newWidth][newHeight];
		
		for (int i = 0; i < newWidth; i++) {
			for (int j = 0; j < newHeight; j++) {
				// Interpolate from the top row
				int row1 = i;
				int col1 = j;
				int row2, col2;
				int finalGrValue = 0;
				// If new index maps perfectly to the old index based on factor
				if (customRound(row1 % factor) == 0 && customRound(col1 % factor) == 0) {
					row1 = customRound(row1 / factor);
					col1 = customRound(col1 / factor);
					finalGrValue = initImage2[row1][col1];
				}
				// Only need to perform linear interpolation on row
				else if (customRound(row1 % factor) == 0) {
					row1 = customRound(row1 / factor);
					row2 = row1;
					col1 = (int) Math.floor(col1 / factor);
					col2 = col1 + 1;
					if (col2 >= oldW_Constant) col2 = oldW_Constant - 1;
					finalGrValue = getGrayValue(initImage2, row1, col1, row2, col2,
							j, factor);
				}
				// Only need to perform linear interpolation on columns
				else if (customRound(col1 % factor) == 0) {
					col1 = customRound(col1 / factor);
					col2 = col1;
					row1 = (int) Math.floor(row1 / factor);
					row2 = row1 + 1;
					if (row2 >= oldH_Constant) row2 = oldH_Constant - 1;
					
					finalGrValue = getGrayValue(initImage2, row1, col1, row2, col2,
							i, factor);
				}
				// Need to perform linear interpolation on x and y
				else {
					int tempGr1, tempGr2;
					// Linear interpolation - NORTH
					row1 = (int) Math.floor(i / factor);
					row2 = row1;
					col1 = (int) Math.floor(j / factor);
					col2 = col1 + 1;
					if (col2 >= oldW_Constant) col2 = oldW_Constant - 1;
					tempGr1 = getGrayValue(initImage2, row1, col1, row2, col2, j, factor);
					
					// Linear interpolation - SOUTH
					row1 = row1 + 1;
					if (row1 >= oldH_Constant) row1 = oldH_Constant - 1;
					row2 = row1;
					tempGr2 = getGrayValue(initImage2, row1, col1, row2, col2, j, factor);
					
					// Linear interpolation between two temp values
					int tempGrDiff = tempGr2 - tempGr1;
					double tempIndexDiff = i % factor;
					finalGrValue = tempGr1 + customRound(tempGrDiff * (tempIndexDiff / factor));
				}
				
				zoomedImage[i][j] = finalGrValue;
			}
		}
		
		return zoomedImage;
	}
	
	private int customRound(double input) {
		return MainWindow.customRound(input);
	}
	
	private int getGrayValue(int[][] initImage2, int row1, int col1, int row2,
			int col2, int curRowOrCol, double factor) {
		return MainWindow.getGrayValue(initImage2, row1, col1, row2, col2, curRowOrCol, factor);
	}
}
