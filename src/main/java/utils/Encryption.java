package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

	public static final String KeyFileName = "AESK.txt";
	public static void encrypt(String fileName) {
		// readFile from FNAME
		try {
			byte[] plainTextArr = readFile(fileName);
			byte[] AESK = genStoreKey();


			// encrypt plainTextArr
			Cipher cipherAES = Cipher.getInstance("AES");
			Key key128AES = new SecretKeySpec(AESK, "AES");
			cipherAES.init(Cipher.ENCRYPT_MODE, key128AES);
			byte[] finalEncFile = cipherAES.doFinal(plainTextArr);

			// save to file
			String encodedFileName = "enc_" + fileName;
			saveFile(encodedFileName, finalEncFile);
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

	public static void decrpt(String fileName) {
		try {
			byte[] encodedFileByte = readFile(fileName);			
			byte[] fileAESKbyte = readFile(KeyFileName);

			// decipher AES
			Cipher deCipherAES2 = Cipher.getInstance("AES");
			Key key128AES2 = new SecretKeySpec(fileAESKbyte, "AES");
			deCipherAES2.init(Cipher.DECRYPT_MODE, key128AES2);
			byte[] fileTarget = deCipherAES2.doFinal(encodedFileByte);
			
			String preFileName = fileName.split("_")[1];
			saveFile(preFileName, fileTarget);
			System.out.println("finish decrypt " + fileName);

			
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

	private static void saveFile(String fileName, byte[] byteArr) {
		FileOutputStream encFile;
		try {
			encFile = new FileOutputStream(fileName);
			encFile.write(byteArr);
			encFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static byte[] readFile(String fileName) {
		File plainText = new File(fileName);
		byte[] plainTextArr = null;
		FileInputStream fisPlainText;
		try {
			fisPlainText = new FileInputStream(plainText);

		plainTextArr = new byte[(int) plainText.length()];
		fisPlainText.read(plainTextArr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plainTextArr;
	}
	
	private static byte[] genStoreKey() {
		// randomly pick AES key
		SecureRandom sr = new SecureRandom();
		byte[] AESK = new byte[16];
		sr.nextBytes(AESK);

		// store the key
		FileOutputStream fosAESK;
		try {
			fosAESK = new FileOutputStream(KeyFileName);
	
		fosAESK.write(AESK);
		fosAESK.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return AESK;
	}

}
