package de.kekz.testserver.manager.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// First testing with AES encrpytion in plugins I've made

public class CryptoManager {

	private String cipher_algo = "AES/CBC/PKCS5PADDING";

	/* Secret Key */
	private String keyEncoded = "MjY1MDUyNTdiNDRjMjRkN2IyODA1NzM3Yzc5YTZiMmE3OTI0MzNiYWQ5ODVlZjU1MjRhMjM3NDM3ZjgxMTkzOQ==";
	private String keyHex = new String(Base64.getDecoder().decode(keyEncoded.getBytes()));
	private byte[] keyBytes = convertHexToBytes(keyHex);
	private SecretKey secretKey = new SecretKeySpec(keyBytes, 0, 32, "AES");

	/* Vector */
	private String vectorEncoded = "NjdkNzY0MzY1MzgwMmQ1OGNlZjEzYTFjOTg0Y2RjYmM=";
	private String vectorHex = new String(Base64.getDecoder().decode(vectorEncoded.getBytes()));
	private byte[] vectorBytes = convertHexToBytes(vectorHex);

	public String encrypt(String data) throws Exception {
		Cipher cipher = Cipher.getInstance(cipher_algo);
		cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, new IvParameterSpec(this.vectorBytes));

		String encryptedText = Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
		return encryptedText;
	}

	public String decrypt(String data) throws Exception {
		Cipher cipher = Cipher.getInstance(cipher_algo);
		cipher.init(Cipher.DECRYPT_MODE, this.secretKey, new IvParameterSpec(this.vectorBytes));

		byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(data.getBytes()));
		return new String(plainText);
	}

	public SecretKey generateSecretKey(int keysize) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(keysize, SecureRandom.getInstanceStrong());

		return keyGenerator.generateKey();
	}

	public byte[] generateRandomBytes(int size) {
		byte[] nonce = new byte[size];
		new SecureRandom().nextBytes(nonce);

		return nonce;
	}

	public String convertBytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte b : bytes) {
			result.append(String.format("%02x", b));
		}

		return result.toString();
	}

	public byte[] convertHexToBytes(String hex) {
		int len = hex.length();
		byte[] data = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
		}

		return data;
	}
}
