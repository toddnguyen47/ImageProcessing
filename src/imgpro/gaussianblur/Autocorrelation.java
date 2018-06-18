/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.gaussianblur;

import imgpro.MainWindow;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author ToddNguyen
 */
public class Autocorrelation {
	private static Autocorrelation instance;
	private Autocorrelation() {};
	private int keyR[][] = null;
	
	public static Autocorrelation getInstance() {
		if (instance == null) {
			instance = new Autocorrelation();
		}
		return instance;
	}
	
	public int[][] rplusmn(int sn[]) {
		int L = sn.length;
		int row = (L >> 1) + 1; // divide by 2, add 1
		int resultMatrix[][] = new int[row][L];
		int index1 = 0;
		int index2 = 0;
		
		// Initialize the result matrix with all 0s
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < L; j++) {
				if (j < i) {
					resultMatrix[i][j] = 0;
				}
				else {
					int indexDiff = 1 << i;
					index1 = j;
					index2 = index1 + indexDiff;
					resultMatrix[i][j] = sn[index1] * sn[index2];
				}
			}
		}
		
		MainWindow.printImageArray(resultMatrix);
		
		return resultMatrix;
	}
	
	public int[][] getKeyR() {return keyR;}
	
	public int[][] getRandomLPoints(int edges[][], double sigma, int pointsL) {
		EdgeDetection eDet = EdgeDetection.getInstance();
		int keyS[][] = eDet.getRandomEdgeCells(edges, pointsL);
		int L = keyS.length  >> 1; // Choose L locations among the L^2 chosen locations
		
		keyR = new int[L][2];
		Random random = new Random();
		Set<Integer> generated = new HashSet<>();
		while (generated.size() < L) {
			int index = random.nextInt(keyS.length);
			generated.add(index);
		}
		
		// Add the randomly chosen points into our keyS
		Iterator<Integer> iter = generated.iterator();
		int keyRIndex = 0;
		while (iter.hasNext()) {
			int index = iter.next();
			keyR[keyRIndex++] = keyS[index];
		}
		
		return keyR;
	}
}
