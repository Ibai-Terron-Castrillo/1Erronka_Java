package services;

import com.google.gson.Gson;
import DB.ApiClient;
import Klaseak.Erabiltzailea;

public class ErabiltzaileaService {

    private static final Gson gson = new Gson();

    public static Erabiltzailea getByLangile(int langileId) {
        try {
            var res = ApiClient.get("/api/erabiltzailea/langile/" + langileId);
            return gson.fromJson(res.body(), Erabiltzailea.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean saveOrUpdate(Erabiltzailea e) {
        try {
            var json = gson.toJson(e);
            ApiClient.post("/api/erabiltzailea", json);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean deleteByLangile(int langileId) {
        try {
            ApiClient.delete("/api/erabiltzailea/langile/" + langileId);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
