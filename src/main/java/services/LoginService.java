package services;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginService {

    // API helbidea - aldatu behar baduzu hemen
    private static final String API_URL = "http://localhost:5000/api/login";

    public static boolean login(String erabiltzailea, String pasahitza) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String json = String.format(
                    "{\"erabiltzailea\":\"%s\",\"pasahitza\":\"%s\"}",
                    escapeJson(erabiltzailea),
                    escapeJson(pasahitza)
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            // 200 -> OK. Bestela, 401 Unauthorized edo bestelakoak
            return code == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Oso oinarrizko JSON escaping (gehienetan nahikoa)
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
