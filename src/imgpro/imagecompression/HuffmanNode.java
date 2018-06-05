/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.imagecompression;

/**
* Huffman Node used for priority queue.
*/
public class HuffmanNode implements Comparable<HuffmanNode> {
	private final int grayValue;
	private final int frequency;
	private int bitCode = Huffman.NOT_A_GRAY_VALUE; // either 0 or 1
	private HuffmanNode parentNode = null;
	private HuffmanNode leftChild = null;
	private HuffmanNode rightChild = null;

	public HuffmanNode(int grayValue, int frequency) {
		this.grayValue = grayValue;
		this.frequency = frequency;
	}

	@Override
	public int compareTo(HuffmanNode o) {
		int freq1 = this.getFrequency();
		int freq2 = o.getFrequency();

		// Sort by frequency, then sort by gray value
		if (freq1 != freq2) {
			return this.getFrequency() - o.getFrequency();
		}
		else {
			return this.getGrayValue() - o.getGrayValue();
		}
	}

	/**
	 * Check if the node is a leaf node
	 * @return True if the node has no children, false otherwise
	 */
	public boolean isLeafNode() {
		return (this.leftChild == null && this.rightChild == null);
	}

	public void setBitCode(int bitCode) {this.bitCode = bitCode;}
	public void setParentNode(HuffmanNode parentNode) {this.parentNode = parentNode;}
	public void setLeftChild(HuffmanNode leftChild) {this.leftChild = leftChild;}
	public void setRightChild(HuffmanNode rightChild) {this.rightChild = rightChild;}

	public int getGrayValue() {return this.grayValue;}
	public int getFrequency() {return this.frequency;}
	public int getBitCode() {return this.bitCode;}
	public HuffmanNode getParentNode() {return parentNode;}
	public HuffmanNode getLeftChild() {return leftChild;}
	public HuffmanNode getRightChild() {return rightChild;}

	@Override
	public String toString() {
		//String temp = "Gray: " + this.grayValue + ", Freq: " + this.frequency;
		String temp = "Gray: " + this.grayValue + ", Bit code: " + this.bitCode;
		return temp;
	}
}
