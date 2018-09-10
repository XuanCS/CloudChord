package utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

	public static final String KeyFileName = "_AESK.txt";
	public static final String EncodePrefix = "enc_";
	
	public static void encrypt(String fileName, String encodedFileName) {
		// readFile from FNAME
		try {
			byte[] plainTextArr = FileUtils.readFile(fileName);
			byte[] AESK = genStoreKey(encodedFileName);

			// encrypt plainTextArr
			Cipher cipherAES = Cipher.getInstance("AES");
			Key key128AES = new SecretKeySpec(AESK, "AES");
			cipherAES.init(Cipher.ENCRYPT_MODE, key128AES);
			byte[] finalEncFile = cipherAES.doFinal(plainTextArr);

			// save to file
			FileUtils.writeFile(encodedFileName, finalEncFile);
			System.out.println("finish encrypt " + fileName);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void decrpt(String encFileName, String oriFileName) {
		try {
			byte[] encodedFileByte = FileUtils.readFile(encFileName);			
			byte[] fileAESKbyte = FileUtils.readFile(encFileName + KeyFileName);

			// decipher AES
			Cipher deCipherAES2 = Cipher.getInstance("AES");
			Key key128AES2 = new SecretKeySpec(fileAESKbyte, "AES");
			deCipherAES2.init(Cipher.DECRYPT_MODE, key128AES2);
			byte[] fileTarget = deCipherAES2.doFinal(encodedFileByte);
			
//			String preFileName = fileName.split("_")[1];
			FileUtils.writeFile(oriFileName, fileTarget);
			System.out.println("finish decrypt " + oriFileName);

			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	private static byte[] genStoreKey(String fileName) {
		// randomly pick AES key
		SecureRandom sr = new SecureRandom();
		byte[] AESK = new byte[16];
		sr.nextBytes(AESK);
		FileUtils.writeFile(fileName + KeyFileName, AESK);
		return AESK;
	}

}
