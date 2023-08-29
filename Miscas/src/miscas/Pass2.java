package miscas;

import java.util.ArrayList;
import java.util.HashMap;

public class Pass2 {
	//fields
	private ArrayList<String> output = new ArrayList<String>();
	private ArrayList<String> instructLine = new ArrayList<>();
	
	//constructors
	public Pass2(ArrayList<String> lines, 
			HashMap<String, Integer> labelMap) {
		output.add("v2.0 raw"); //for logisim
		int lineNumber = 0;
		for (String line: lines) {
			int init = 0, one = 0, two = 0, three = 0, four = 0;
			String[] instructions = line.trim().split(":");
			String[] instruct;
			Boolean labelOnly = false;
			if (instructions.length == 1) {
				labelOnly = line.contains(":");
				if (!labelOnly)
					instructLine.add(instructions[0].trim());
			}
			else
				instructLine.add(instructions[1].trim());
			if (instructions.length == 2)
				instruct = instructions[1].trim().split(" ");
			else
				instruct = instructions[0].trim().split(" ");
			if (instruct.length == 4) {
				one = getBit(init, instruct[0]);
				two = getBit(one, instruct[2]);
				three = getBit(two, instruct[3]);
				four = getBit(three, instruct[1]);
				addHexString(four);
			}
			else if (instruct.length == 3) {
				one = getBit(init, instruct[0]);
				two = getBit(one, instruct[2]);
				three = two << 2;
				four = getBit(three, instruct[1]);
				if (one == 0b11) {
					one = one << 10;
					four = two | one;
				}
				addHexString(four);
			}
			else if (instruct.length == 2) {
				//Boolean isLabel = false;
				Boolean isALUOP = false;
				int address = 0;
				one = getBit(init, instruct[0]);
				if (one == 0b000101 || one == 0b000110) { //shl or asr
					isALUOP = true;
				}
				if (isALUOP) {
					two = one << 6;
					four = two | address;
				}
				else {
					one = one << 10;
					two = getBit(one, instruct[1]);
					four = two | one;
					if (two < 1) {
						int five = 0b00000000000000000000101111111111;
						four = four & five;
					}
				}
				addHexString(four);
				/*if (!isALUOP) {
					if (labelMap.get(instruct[1]) != null)
						isLabel = true;
					if (isLabel)
						address = labelMap.get(instruct[1]);
					else {
						System.out.println("Label <" + instruct[1] + "> undefined "
						+ "at line number " + lineNumber);
						System.exit(-1);
					}
				}*/
			}
			else if (instruct.length == 1) {
				//do nothing, it's a label only line
			}
			else {
				System.out.println("unrecognized instructions");
				System.exit(-1);
			}
			lineNumber++;
		}
	}
	
	//methods
	public ArrayList<String> getOutput(){
		return output;
	}
	public ArrayList<String> getLines(){
		return instructLine;
	}
	private int getBit(int set, String instruct) {
		int zero = 0b00, one = 0b01, two = 0b10, three = 0b11;
		int shift = set << 2, answer = 0;
		switch(instruct) {
			case "add": answer = 0b000000;
					break;
			case "sub": answer = 0b000001;
					break;
			case "and": answer = 0b000010;
					break;
			case "or": answer = 0b000011;
					break;
			case "not": answer = 0b000100;
					break;
			case "shl": answer = 0b000101;
					break;
			case "asr": answer = 0b000110;
					break;
			case "mov": answer = 0b001000;
					break;
			case "st": answer = 0b001100;
					break;
			case "ld": answer = 0b001101;
					break;
			case "bnz": answer = one;
					break;
			case "bcs": answer = two;
					break;
			case "mvi": answer = three;
					break;
			case "r0": answer = shift;
					break;
			case "r1": answer = shift | one;
					break;
			case "r2": answer = shift | two;
					break;
			case "r3": answer = shift | three;
					break;
			default:
				answer = Integer.parseInt(instruct);
		}
		return answer;
	}
	private void addHexString(int num) {
		String Str = Integer.toHexString(num).toUpperCase();
		String hexStr = "0";
		if (Str.length() < 2)
			hexStr += Str;
		else
			hexStr = Str;
		output.add(hexStr);
	}
}