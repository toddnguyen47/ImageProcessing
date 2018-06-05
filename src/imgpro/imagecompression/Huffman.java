/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.imagecompression;

import imgpro.MainWindow;
import java.util.PriorityQueue;

/**
 *
 * @author ToddNguyen
 */
public class Huffman {
	public static final int NOT_A_GRAY_VALUE = -1;
	private HuffmanNode rootNode = null;
	public static Huffman instance;
	private Huffman() {};
	public static Huffman getInstance() {
		if (instance == null) {
			instance = new Huffman();
		}
		return instance;
	}
	
	public String[] huffmanCode(int initImage[][]) {		
		String huffmanCodes[] = getHuffmanLengths(initImage);
		
		int width = initImage.length;
		int height = initImage[0].length;
		String encoded[] = new String[width + 1]; // each width will have its own encoded string
		// First width will have width and height
		encoded[0] = String.valueOf(initImage.length) + "," + String.valueOf(initImage[0].length);
		
		for (int i = 0; i < width; i++) {
			StringBuilder temp = new StringBuilder();
			for (int j = 0; j < height; j++) {
				int curGrayValue = initImage[i][j];
				temp.append(huffmanCodes[curGrayValue]);
			}
			encoded[i + 1] = temp.toString();
		}
		
		return encoded;
	}
	
	/**
	 * Get the total Huffman length of all the encoded bits
	 * @param encoded The encoded string array, with each index representing a row in an image
	 * @return The total bits needed by the encoded[] array
	 */
	public final int getTotalHuffmanLengths(String encoded[]) {
		int counter = 0;
		for (String s : encoded) {
			counter += s.length();
		}
		
		return counter;
	}
	
	
	/**
	 * Take an image and convert the grayscale frequencies into Variable Length
	 * Coding using the Huffman algorithm.
	 * @param initImage The initial image, in array form
	 * @return A String array of all the bit codes, with index representing the
	 * corresponding gray value.
	 */
	public String[] getHuffmanLengths(int initImage[][]) {
		int frequency[] = frequencyCoding(initImage);
		PriorityQueue<HuffmanNode> pQueue = new PriorityQueue<>();
		
		// Add the frequency to a priority queue
		for (int i = 0; i < frequency.length; i++) {
			if (frequency[i] > 0) {
				pQueue.add(new HuffmanNode(i, frequency[i]));
			}
		}
		
		// Initialize huffman nodes
		HuffmanNode hNodes[] = new HuffmanNode[MainWindow.MAX_GRAYSCALE_VALUE + 1];
		for (int i = 0; i < hNodes.length; i++) {
			hNodes[i] = null;
		}
		
		// Huffman algorithm
		while (pQueue.size() > 1) {
			// Take the two smallest current node out
			HuffmanNode nodeLeft = pQueue.poll();
			HuffmanNode nodeRight = pQueue.poll();
			
			nodeLeft.setBitCode(0);
			nodeRight.setBitCode(1);
			
			// Store the nodes with actual gray values in an ArrayList
			int tempGrayValue;
			if ((tempGrayValue = nodeLeft.getGrayValue()) != NOT_A_GRAY_VALUE) {
				hNodes[tempGrayValue] = nodeLeft;
			}
			if ((tempGrayValue = nodeRight.getGrayValue()) != NOT_A_GRAY_VALUE) {
				hNodes[tempGrayValue] = nodeRight;
			}
			
			int newFrequency = nodeLeft.getFrequency() + nodeRight.getFrequency();
			// All "combined" nodes will have a gray value of NOT_A_GRAY_VALUE
			HuffmanNode newNode = new HuffmanNode(NOT_A_GRAY_VALUE, newFrequency);
			newNode.setLeftChild(nodeLeft);
			newNode.setRightChild(nodeRight);
			
			nodeLeft.setParentNode(newNode);
			nodeRight.setParentNode(newNode);
			
			pQueue.add(newNode); // Add in the new node
		}
		
		// The root node is the last remaining node
		this.rootNode = pQueue.poll();
		// If you need to see the gray values with their codes, uncomment the line below
		// printNodes(rootNode, "");
		
		
		String bitCodes[] = new String[MainWindow.MAX_GRAYSCALE_VALUE + 1];
		for (int i = 0; i < bitCodes.length; i++) {
			if (hNodes[i] != null) {
				String bitCodeStr = getBitCode(hNodes[i]);
				bitCodes[i] = bitCodeStr;
			}
			// If the gray value did not appear in the image
			else {bitCodes[i] = "";}
		}
		
		return bitCodes;
	}
	
	
	private int[] frequencyCoding(int initImage[][]) {
		int frequency[] = new int[MainWindow.MAX_GRAYSCALE_VALUE + 1];
		for (int i = 0; i < frequency.length; i++) {
			frequency[i] = 0;
		}
		
		int width = initImage.length;
		int height = initImage[0].length;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int curGrayValue = initImage[i][j];
				frequency[curGrayValue] += 1;
			}
		}
		
		return frequency;
	}
	
	/**
	 * Get the bit code of the current Huffman node.
	 * @param curNode The current leaf node to look at
	 * @return The binary code of the gray value in String form
	 */
	private String getBitCode(HuffmanNode curNode) {
		StringBuilder bitCode = new StringBuilder();
		
		// We will loop until we hit the root node.
		// We will NOT consider the root node's bit code in our code length
		while (curNode.getParentNode() != null) {
			bitCode.append(String.valueOf(curNode.getBitCode()));
			curNode = curNode.getParentNode();
		}
		
		bitCode = bitCode.reverse();
		
		return bitCode.toString();
	}
	

	private void printNodes(HuffmanNode root, String str) {
		if (root != null) {
			if (root.getGrayValue() != NOT_A_GRAY_VALUE) {
				System.out.println(root.getGrayValue() + ": " + str);
			}
			
			// Go left
			printNodes(root.getLeftChild(), str + "0");
			// Go right
			printNodes(root.getRightChild(), str + "1");
		}
	}
	
	
	public int[][] decodeHuffman(String input[], HuffmanNode rootNode) {
		String widthheight = input[0];
		String temp[] = widthheight.split(",");
		int width = Integer.parseInt(temp[0]);
		int height = Integer.parseInt(temp[1]);
		int convertedImage[][] = new int[width][height];
		
		for (int i = 0; i < width; i++) {
			convertedImage[i] = decodeHuffmanHelper(input[i + 1], rootNode, height);
		}
		
		return convertedImage;
	}
	
	/**
	 * Search a library for a suitable grayscale value
	 * @param input
	 * @param library[]
	 * @param totalLength
	 * @return An int array with grayscale values for the current row
	 */
	private int[] decodeHuffmanHelper(String input, HuffmanNode rootNode, int totalLength) {
		int output[] = new int[totalLength];
		int outputIndex = 0;
		HuffmanNode curNode = rootNode;
		
		for (int i = 0; i < input.length(); i++) {
			char curChar = input.charAt(i);
			// If leaf node
			if (curNode.isLeafNode()) {
				int grayValue = curNode.getGrayValue();
				output[outputIndex++] = grayValue;
				
				// Reset
				curNode = rootNode;
			}
			
			// Go left
			if (curChar == '0') {
				curNode = curNode.getLeftChild();
			}
			// Go right
			else {
				curNode = curNode.getRightChild();
			}
		}
		
		return output;
	}
	
	
	public HuffmanNode getRootNode() {return this.rootNode;}
}
