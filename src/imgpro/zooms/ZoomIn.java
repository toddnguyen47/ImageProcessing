 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.zooms;

import imgpro.MainWindow;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author ToddNguyen
 */
public class ZoomIn {
	private static ZoomIn instance;
	private ZoomIn() {} // private constructor required for a Singleton
	public static ZoomIn getInstance() {
		if (instance == null) {
			instance = new ZoomIn();
		}
		return instance;
	}
	
	/**
	 * Return a zoomed in version of the image based on the user-inputted width and height.
	 * @param evt The Action Event that called this function.
	 * @param frame The current frame. Used for displaying errors.
	 * @param textFieldWidth The user-inputted width.
	 * @param textFieldHeight The user-inputted height.
	 * @param labelInitImage The label of the current image.
	 * @param initImage The initial image.
	 * @return 
	 */
	public int[][] zoomInAction(java.awt.event.ActionEvent evt, JFrame frame, JTextField textFieldWidth,
		JTextField textFieldHeight, JLabel labelInitImage, int initImage[][]) {
		int newWidth = 0;
		int newHeight = 0;
		try {
			newWidth = Integer.valueOf(textFieldWidth.getText());
			newHeight = Integer.valueOf(textFieldHeight.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame, "Width or Height is not a digit");
		}

		int tempIntArr[][] = new int[newWidth][newHeight];
		int widthFactor = 0;
		int heightFactor = 0;
		int curWidth = labelInitImage.getIcon().getIconWidth();
		int curHeight = labelInitImage.getIcon().getIconHeight();
		
		// Cannot zoom in on a bigger picture!
		if (newWidth > curWidth) {
			JOptionPane.showMessageDialog(frame, "Cannot zoom in with a larger width and height.");
			return null;
		}
		
		if (newWidth != 0) {
			double tempW = (double) newWidth;
			widthFactor = (int) Math.floor((curWidth / tempW));
			
			double tempH = (double) newHeight;
			heightFactor = (int) Math.floor((curHeight / tempH));
		}
		
		for (int i = 0; i < newWidth; i++) {
			for (int j = 0; j < newHeight; j++) {
				tempIntArr[i][j] = initImage[widthFactor * i][heightFactor * j];
			}
		}

		return tempIntArr;
	}
}
