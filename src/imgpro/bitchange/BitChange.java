/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.bitchange;

import imgpro.MainWindow;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author ToddNguyen
 */
public class BitChange {
	private static BitChange instance;
	private BitChange() {};
	public static BitChange getInstance() {
		if (instance == null) {
			instance = new BitChange();
		}
		return instance;
	}
	
	public int[][] bitChangeAction(java.awt.event.ActionEvent evt, JFrame frame, int currentBit,
			int bitSelected, int initImage[][]) {
		int curWidth = initImage.length;
		int curHeight = initImage[0].length;
		int bitDiff = currentBit - bitSelected;
		int diffFromMaxBit = MainWindow.MAX_BIT - currentBit;
		boolean bitDiffPos = (bitDiff >= 0 && diffFromMaxBit >= 0); // to flag if bitdiff is positive or negative
		bitDiff = Math.abs(bitDiff);
		int maxScaledBit = (1 << bitSelected) - 1;
		
		int convertedImage[][] = new int[curWidth][curHeight];
		if (bitDiffPos) {
			for (int i = 0; i < curWidth; i++) {
				for (int j = 0; j < curHeight; j++) {
					double temp = (initImage[i][j] >> diffFromMaxBit) >> bitDiff;
					// to get a grayscale value
					convertedImage[i][j] = (int) Math.floor((temp / maxScaledBit) * MainWindow.MAX_GRAYSCALE_VALUE);

					if (convertedImage[i][j] > MainWindow.MAX_GRAYSCALE_VALUE) {
						System.out.print(temp + " ");
						if ((j+1) % 8 == 0) System.out.println("");
					}
				}
			}

			return convertedImage;
		}
		else {
			JOptionPane.showMessageDialog(frame, "WARNING: Bit selected is higher"
					+ " than number of bits of current origin image.");
			return null;
		}
	}
}
