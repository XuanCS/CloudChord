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
	
	public static void encrypt(String fileName, String dirName, String encodedFileName) {
		// readFile from FNAME
		try {
			byte[] plainTextArr = FileUtils.readFile(fileName, dirName);
			byte[] AESK = genStoreKey(encodedFileName, dirName);

			// encrypt plainTextArr
			Cipher cipherAES = Cipher.getInstance("AES");
			Key key128AES = new SecretKeySpec(AESK, "AES");
			cipherAES.init(Cipher.ENCRYPT_MODE, key128AES);
			byte[] finalEncFile = cipherAES.doFinal(plainTextArr);

			// save to file
			FileUtils.writeFile(encodedFileName, dirName, finalEncFile);
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

	public static void decrpt(String encFileName, String dirName, String oriFileName) {
		try {
			String downloadFolder = dirName + Helper.DOWNLOADS;
			byte[] encodedFileByte = FileUtils.readFile(encFileName, downloadFolder);	
			String literalFileName = getBaseFileName(encFileName);
			byte[] fileAESKbyte = FileUtils.readFile(literalFileName + KeyFileName, dirName);

			// decipher AES
			Cipher deCipherAES2 = Cipher.getInstance("AES");
			Key key128AES2 = new SecretKeySpec(fileAESKbyte, "AES");
			deCipherAES2.init(Cipher.DECRYPT_MODE, key128AES2);
			byte[] fileTarget = deCipherAES2.doFinal(encodedFileByte);
			
			FileUtils.writeFile(oriFileName, downloadFolder, fileTarget);
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


	
	private static byte[] genStoreKey(String fileName, String dirName) {
		// randomly pick AES key
		SecureRandom sr = new SecureRandom();
		byte[] AESK = new byte[16];
		sr.nextBytes(AESK);
		String baseFileName = getBaseFileName(fileName);
		FileUtils.writeFile(baseFileName + KeyFileName, dirName, AESK);
		return AESK;
	}
	
	private static String getBaseFileName(String fileName) {
		String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
		return tokens[0];
	}

}
