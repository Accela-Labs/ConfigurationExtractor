package Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class AccelaExportUtils {

	public static String fixStringForFileName(String s, boolean space) {
		s = s.replace(":", ";");
		s = s.replace("/", "!");
		s = s.replace("*", "~");

		if (space) {
			s = s.replace(" ", "");
		}

		return s;
	}

	public static String fixStringForDirectoryName(String s) {
		s = s.replaceAll("\\\\", "/");
		s = s.replaceAll("\\s", "");
		s = s.replaceAll("\\*", "");
		s = s.replace(":", ";");
		s = s.replace("///", "/");
		s = s.replace("//", "/");
		if (!s.endsWith("/")) {
			s = s + "/";
		}

		return s;
	}

	public static void writeFile(String fileString, File f) throws IOException {
		f.createNewFile();
		System.out.println("wrote file:" + f.getPath());

		FileOutputStream nwFile = new FileOutputStream(f);
		byte[] myBytes = fileString.getBytes();
		nwFile.write(myBytes);
		nwFile.close();
	}

	public static void compareFiles(String fileString, File f) throws IOException {
		String contents = "";

		File ft = null;
		ft = new File("" + f + "");

		if (ft.exists()) {
			FileReader fr = null;
			fr = new FileReader(f);
			char[] template = new char[(int) f.length()];
			fr.read(template);
			contents = new String(template);

			// System.out.println("trying to do an
			// array test");
			char[] oldfileArray = contents.toCharArray();
			char[] newfileArray = fileString.toCharArray();

			boolean retval = Arrays.equals(oldfileArray, newfileArray);
			// System.out.println(retval);
			if (retval == true) {
				System.out.println(f + " : The Subversioned copy is the same... skipping...");

			} else {
				System.out.println("The Subversioned copy is the different... Updating file:" + f.getPath());
				FileOutputStream updFile = new FileOutputStream(f);
				byte[] myBytes = fileString.getBytes();
				updFile.write(myBytes);
				updFile.close();
			}

			if (fr != null) {
				fr.close();
			}
		}
	}

}
