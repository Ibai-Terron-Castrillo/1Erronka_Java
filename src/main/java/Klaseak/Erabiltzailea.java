package Klaseak;

public class Erabiltzailea {
    private int id;
    private int langileak_id;
    private String erabiltzailea;
    private String pasahitza;

    public Erabiltzailea(int id, int langileak_id, String erabiltzailea, String pasahitza) {
        this.id = id;
        this.langileak_id = langileak_id;
        this.erabiltzailea = erabiltzailea;
        this.pasahitza = pasahitza;
    }

    public Erabiltzailea(String erabiltzailea, String pasahitza) {
        this.erabiltzailea = erabiltzailea;
        this.pasahitza = pasahitza;
    }

    public Erabiltzailea() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLangileak_id() {
        return langileak_id;
    }

    public void setLangileak_id(int langileak_id) {
        this.langileak_id = langileak_id;
    }

    public String getErabiltzailea() {
        return erabiltzailea;
    }

    public void setErabiltzailea(String erabiltzailea) {
        this.erabiltzailea = erabiltzailea;
    }

    public String getPasahitza() {
        return pasahitza;
    }

    public void setPasahitza(String pasahitza) {
        this.pasahitza = pasahitza;
    }
}
