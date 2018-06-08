import java.io.*;

public class FileManager {
    public static String read(final File file) {
        StringBuilder text = new StringBuilder();
        BufferedReader bufferedReader = null;
        String result = "";
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            String data;
            while ((data = bufferedReader.readLine()) != null) {
                text.append(data);
            }
            result = new String(text.toString().trim().getBytes("UTF-8"), "UTF-8");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try { bufferedReader.close(); } catch (IOException ioe) { ioe.printStackTrace(); }
            }
        }

        return result;
    }

    public static void write(final File url, final String contents) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(url.getAbsolutePath() + ".txt"), "UTF-8"));
            bufferedWriter.write(contents);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try { bufferedWriter.close(); } catch (IOException ioe) { ioe.printStackTrace(); }
            }
        }
    }
}
