package util;

import javax.xml.bind.DatatypeConverter;

public class MiscUtil {
	public static String bytesToHex(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}
	
	public static byte[] hexToBytes(String string) {
		return DatatypeConverter.parseHexBinary(string);
	}
}
