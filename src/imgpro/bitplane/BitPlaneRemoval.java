/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.bitplane;

import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 *
 * @author ToddNguyen
 */
public class BitPlaneRemoval {
	private static BitPlaneRemoval instance;
	private BitPlaneRemoval(){};
	public static BitPlaneRemoval getInstance() {
		if (instance == null) {
			instance = new BitPlaneRemoval();
		}
		return instance;
	}
	
	public int[][] bitPlaneRemoval(java.awt.event.ActionEvent evt, JFrame frame, 
			int inputArray[][], int startPlane, int endPlane) {
		int height = inputArray.length;
		int width = inputArray[0].length;
		String binaryRep[][] = new String[height][width];
		int convertedImage[][] = new int[height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				String bin = String.format("%8s", Integer.toBinaryString(inputArray[i][j]));
				bin = bin.replace(' ', '0');
								
				String replacement  = "";
				for (int start = startPlane; start <= endPlane; start++) {
					replacement += "0";
				}
				bin = bin.substring(0, startPlane) + replacement + bin.substring(endPlane + 1);
				convertedImage[i][j] = Integer.parseInt(bin, 2);				
			}
		}
		
		return convertedImage;
	}
}
