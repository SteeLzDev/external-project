package com.zetra.econsig.enomina.soap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class Base64Utils {

	public static String encodeFileToBase64Binary(File file) throws IOException {
		byte[] bytes = loadFile(file);
		byte[] encoded = Base64.getEncoder().encode(bytes);
		String encodedString = new String(encoded);

		return encodedString;
	}

	private static byte[] loadFile(File file) throws IOException {
		byte[] bytes;
		try (InputStream is = new FileInputStream(file)) {
			long length = file.length();
			if (length > Integer.MAX_VALUE) {
				throw new IOException("File to large " + file.getName());
			}
			bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}
		}
		return bytes;
	}
}
