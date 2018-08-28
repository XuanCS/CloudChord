import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.FileContent;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.GenericUrl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;

public class Gcloud {
	/** Application name. */
	private static final String APPLICATION_NAME = "Drive API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static java.io.File DATA_STORE_DIR;

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	private static Drive service;
	private static String dirName;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/drive-java-quickstart
	 */

	private static final java.util.Collection<String> SCOPES = DriveScopes.all();

	public Gcloud(String folderName) throws IOException {
		dirName = folderName;

		DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials_"+ folderName +"/drive-java-quickstart");
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
			System.out.println("Constructor Data Factory: " + DATA_STORE_FACTORY);

		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		service = getDriveService();

		System.out.println("successfully connect to Google Drive");

	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public Credential authorize() throws IOException {

		String filename = "client_secret.json";

		java.io.File file = new java.io.File(dirName + "/" + filename);
		System.out.println("current dir: " + dirName);

		InputStream in = new FileInputStream(file);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * 
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	public Drive getDriveService() throws IOException {
		Credential credential = authorize();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	public String uploadTextFile(String title) throws IOException {

		String filePath = dirName + "/" + title;
		File body = new File();
		body.setName(title);
		body.setDescription("A test document");
		body.setMimeType("text/plain");
		java.io.File fileContent = new java.io.File(filePath);
		FileContent mediaContent = new FileContent("text/plain", fileContent);
		if (service.files() == null) {
			System.out.println("service is null");
		}

		File file = service.files().create(body, mediaContent).execute();

		String res = file.getId();
		System.out.println("fileID: " + res);

		if (res != null) {
			System.out.println("successfully upload");
		}

		java.io.File propFile = new java.io.File(dirName + "/config.properties");
		if (propFile.exists()) {
			updateProp(title, res);
		} else {
			writeProp(title, res);
		}
		return res;
	}

	public void downLoadFile(String targetFN) throws IOException {
		String res = readProp(targetFN);
		if (res == null) {
			System.out.println("cannot download the target file");
			return;
		}

		File file = service.files().get(res).execute();

		OutputStream out = new FileOutputStream(dirName + "/" + file.getName());
		Drive.Files.Get request = service.files().get(res);
		request.executeMediaAndDownloadTo(out);
		System.out.println("successfully download file to " + dirName);
		deleteFile(targetFN);

	}

	public void deleteFile(String targetFN) {
		try {
			// String name = service.files().get(fileId).
			String res = readProp(targetFN);
			if (res == null) {
				System.out.println("cannot download the target file");
				return;
			}

			service.files().delete(res).execute();

			System.out.println("succesfully delte target file");
		} catch (IOException e) {
			System.out.println("An error occurred: " + e);
		}
	}

	private void listFiles() throws IOException {
		FileList result = service.files().list().execute();
		List<File> files = result.getFiles();

		// list files
		if (files == null || files.size() == 0) {
			System.out.println("No files found.");
		} else {
			System.out.println("Files:");
			for (File file : files) {
				System.out.printf("%s (%s)\n", file.getName(), file.getId());
			}
		}
	}

	private void writeProp(String key, String value) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(dirName + "/config.properties");
			// set the properties value
			prop.setProperty(key, value);
			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		System.out.println("successfully written to local config.property");

	}

	private String readProp(String key) {
		Properties prop = new Properties();
		InputStream input = null;
		String res = null;

		try {

			input = new FileInputStream(dirName + "/config.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			res = prop.getProperty(key);
			System.out.println(res);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	private void updateProp(String key, String value) throws IOException {
		FileInputStream in = new FileInputStream(dirName + "/config.properties");
		Properties props = new Properties();
		props.load(in);
		in.close();

		FileOutputStream out = new FileOutputStream(dirName + "/config.properties");
		props.setProperty(key, value);
		props.store(out, null);
		out.close();
		System.out.println("successfully update to local config.property");
	}

	private String seekProp(String target) throws IOException {
		FileInputStream in = new FileInputStream(dirName + "/config.properties");
		Properties props = new Properties();
		props.load(in);
		in.close();

		// Iterating properties using For-Each

		Set<String> keys = props.stringPropertyNames();
		for (String key : keys) {
			if (key.equals(target)) {
				return props.getProperty(target);
			}
		}
		System.out.println("cannot file in local record");
		return null;
	}

}
