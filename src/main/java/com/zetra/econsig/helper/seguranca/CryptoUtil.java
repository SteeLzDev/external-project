package com.zetra.econsig.helper.seguranca;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {

	private static String SECRET_KEY;
	private static String SALT;

	@Value("${crypto.secret-key}")
	public void setSecretKey(String secretKey) {
		CryptoUtil.SECRET_KEY = secretKey;
	}

	@Value("${crypto.salt}")
	public void setSalt(String salt) {
		CryptoUtil.SALT = salt;
	}

	public static String encrypt(String data) throws Exception {
		final byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		final IvParameterSpec ivSpec = new IvParameterSpec(iv);

		final SecretKey secretKey = getSecretKey();
		final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

		final byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

		final byte[] combined = new byte[iv.length + encryptedData.length];
		System.arraycopy(iv, 0, combined, 0, iv.length);
		System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

		return Base64.getUrlEncoder().encodeToString(combined);
	}

	public static String decrypt(String encryptedData) throws Exception {
		final byte[] decoded = Base64.getUrlDecoder().decode(encryptedData);

		final byte[] iv = new byte[16];
		final byte[] encryptedBytes = new byte[decoded.length - 16];

		System.arraycopy(decoded, 0, iv, 0, iv.length);
		System.arraycopy(decoded, 16, encryptedBytes, 0, encryptedBytes.length);

		final IvParameterSpec ivSpec = new IvParameterSpec(iv);
		final SecretKey secretKey = getSecretKey();

		final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

		return new String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8);
	}

	private static SecretKey getSecretKey() throws Exception {
		final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		final KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	}
}