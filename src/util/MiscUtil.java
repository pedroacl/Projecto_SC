package util;

import javax.xml.bind.DatatypeConverter;

public class MiscUtil {
	public static String bytesToHex(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}
	
	public static byte[] hexToBytes(String string) {
		return DatatypeConverter.parseHexBinary(string);
	}
	
	/**
	 * Função que obtem o nome do ficheiro presente num path
	 * 
	 * @param absolutePath
	 *            Caminho absoluto
	 * @return Devolve o nome do ficheiro
	 */
	public static String extractName(String absolutePath) {
		String[] splitName = absolutePath.split("/");
		return splitName[splitName.length - 1];
	}
}
