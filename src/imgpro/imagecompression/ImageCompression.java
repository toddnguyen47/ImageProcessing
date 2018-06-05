/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.imagecompression;

import imgpro.MainWindow;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author ToddNguyen
 */
public class ImageCompression {
    private static ImageCompression instance;
    private ImageCompression() {};
    public static ImageCompression getInstance() {    
		if (instance == null) {
			instance = new ImageCompression();
		}
		return instance;
	}
	
	public int[][] getImageCompression(Frame frame, int initImage[][], String algoChosen) {
		long startTime = 0L;
		long endTime = 0L;
		double encodedTime = 0.0;
		double decodedTime = 0.0;
		ArrayList<Integer> encoded = new ArrayList<>();
		String huffmanEncoded[];
		int width = initImage.length;
		int height = initImage[0].length;
		double origSize = width * height * 8.0;
		double compressedSize = 0.0;
		int decodedImage[][] = new int[initImage.length][initImage[0].length];
		
		
		// Run length encoding on grayscale values
		if (algoChosen.equals(MainWindow.ENCODING_CHOICES[0])) {
			RunLengthEncoding rle = RunLengthEncoding.getInstance();
			startTime = System.currentTimeMillis();
			encoded = rle.runLengthEncodingGrayscale(initImage);
			endTime = System.currentTimeMillis();
			compressedSize = encoded.size() * 8.0;
			encodedTime = endTime - startTime;
			
			// Decode time
			startTime = System.currentTimeMillis();
			decodedImage = rle.decodeRLEGrayscale(encoded);
			endTime = System.currentTimeMillis();
			decodedTime = endTime - startTime;
		}
		// Run length encoding on bit planes
		else if (algoChosen.equals(MainWindow.ENCODING_CHOICES[1])) {
			RunLengthEncoding rle = RunLengthEncoding.getInstance();
			startTime = System.currentTimeMillis();
			encoded = rle.rleBits(initImage);
			endTime = System.currentTimeMillis();
			int half = encoded.size() >> 1; // the counts will take 8 bits, but the actual bit themselves will only take 1 bit
			compressedSize = half + (half * 8.0);
			encodedTime = endTime - startTime;
			
			// Decode time
			startTime = System.currentTimeMillis();
			decodedImage = rle.decodeRLEBits(encoded, 8);
			endTime = System.currentTimeMillis();
			decodedTime = endTime - startTime;
		}
		// Huffman coding
		else if (algoChosen.equals(MainWindow.ENCODING_CHOICES[2])) {
			Huffman huffman = Huffman.getInstance();
			startTime = System.currentTimeMillis();
			huffmanEncoded = huffman.huffmanCode(initImage);
			HuffmanNode rootNode = huffman.getRootNode();
			endTime = System.currentTimeMillis();
			compressedSize = huffman.getTotalHuffmanLengths(huffmanEncoded); // each bit has a size of 1
			encodedTime = endTime - startTime;
			
			// Decode time
			startTime = System.currentTimeMillis();
			decodedImage = huffman.decodeHuffman(huffmanEncoded, rootNode);
			endTime = System.currentTimeMillis();
			decodedTime = endTime - startTime;
		}
		// LZW coding
		else if (algoChosen.equals(MainWindow.ENCODING_CHOICES[3])) {
			LZW lzw = LZW.getInstance();
			startTime = System.currentTimeMillis();
			encoded = lzw.getLZW(initImage);
			endTime = System.currentTimeMillis();
			compressedSize = lzw.getSizeOfLZW(encoded);
			encodedTime = endTime - startTime;
			
			// Decode time
			startTime = System.currentTimeMillis();
			decodedImage = lzw.decodeLZW(encoded);
			endTime = System.currentTimeMillis();
			decodedTime = endTime - startTime;
		}
				
		JTextArea msg = new JTextArea();
		msg.setLineWrap(true);
		msg.setWrapStyleWord(true);
		
		msg.append(String.format("Technique: %s\n", algoChosen));
		msg.append(String.format("Encoding time taken: %.0f ms (%.3f s)\n", encodedTime, encodedTime / 1000.0));
		msg.append(String.format("Original bytes taken: %,.0f\n", origSize / 8.0));
		msg.append(String.format("Compressed bytes taken: %,.0f\n", compressedSize / 8.0));
		msg.append(String.format("Compression ratio: %.4f (%.2f%%)\n", origSize / compressedSize,
				origSize * 100.0 / compressedSize));
		msg.append("\n");
		msg.append(String.format("Decoding time taken: %.0f ms (%.3f s)\n", decodedTime, decodedTime / 1000.0));
		
		JScrollPane scrollPane = new JScrollPane(msg);
		scrollPane.setPreferredSize(new Dimension(640, 480));
		JOptionPane.showMessageDialog(frame, scrollPane);
		
		return decodedImage;
	}
}
