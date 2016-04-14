package util;

public class MiscUtil {
	public static String bytesToHex(byte[] bytes) {
		return String.format("%064x", new java.math.BigInteger(1, bytes));
	}
}
