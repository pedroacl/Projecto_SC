package security;

import java.io.FileOutputStream;

import util.PersistenceUtil;

public class ServerSecurity extends Security {

	public void saveSignature(byte[] signature, String path) {
		FileOutputStream fileOutputStream = null;
		PersistenceUtil.writeStringToFile(signature.toString(), path);
	}
}
