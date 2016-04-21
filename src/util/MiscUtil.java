package util;

import javax.xml.bind.DatatypeConverter;

public class MiscUtil {
	public static String bytesToHex(byte[] bytes) {
		// return String.format("%064x", new java.math.BigInteger(1, bytes));
		return DatatypeConverter.printHexBinary(bytes);
	}
	
	public static byte[] hexToBytes(String string) {
		return DatatypeConverter.parseHexBinary(string);
	}
}
