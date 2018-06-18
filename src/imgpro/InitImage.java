/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author ToddNguyen
 */
public class InitImage {
	private int currentBit;
	private BufferedImage bufferedImage;
	private int imageArrayForm[][];
	
	//**************************************************************
	// Getters and setters
	//**************************************************************

	public int[][] getImageArrayForm() {
		return imageArrayForm;
	}
	
	public int getCurrentBit() {
		return currentBit;
	}

	public void setCurrentBit(int currentBit) {
		this.currentBit = currentBit;
	}

	public int getWidth() {
		return bufferedImage.getWidth();
	}

	public int getHeight() {
		return bufferedImage.getHeight();
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}
	
	public void setInitBufferedImage(BufferedImage loadedImg) {
		int width = loadedImg.getWidth();
		int height = loadedImg.getHeight();
		this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.imageArrayForm = new int[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = new Color(loadedImg.getRGB(i, j));
				int r = c.getRed();
				int g = c.getGreen();
				int b = c.getBlue();

				int gr = MainWindow.customRound((r + g + b) / 3.0);
				imageArrayForm[i][j] = gr;

				Color gColor = new Color(gr, gr, gr);						
				this.bufferedImage.setRGB(i, j, gColor.getRGB());
			}
		}
	}
	
	public void setImageFromArray(int arrayForm[][]) {
		int width = arrayForm.length;
		int height = arrayForm[0].length;
		BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.imageArrayForm = new int[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int gr = arrayForm[i][j];
				this.imageArrayForm[i][j] = gr;

				Color gColor = new Color(gr, gr, gr);
				temp.setRGB(i, j, gColor.getRGB());
			}
		}
		
		this.bufferedImage = temp;
	}

	public ImageIcon getImageIcon() {
		return new ImageIcon(this.bufferedImage);
	}	
}
