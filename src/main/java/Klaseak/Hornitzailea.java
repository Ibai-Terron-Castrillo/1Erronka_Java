package Klaseak;

public class Hornitzailea {
    private int id;
    private String cif;
    private String helbidea;
    private String izena;
    private String sektorea;
    private String telefonoa;
    private String email;

    public Hornitzailea() {
    }

    public Hornitzailea(int id, String cif, String helbidea, String izena, String sektorea, String telefonoa, String email) {
        this.id = id;
        this.cif = cif;
        this.helbidea = helbidea;
        this.izena = izena;
        this.sektorea = sektorea;
        this.telefonoa = telefonoa;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getHelbidea() {
        return helbidea;
    }

    public void setHelbidea(String helbidea) {
        this.helbidea = helbidea;
    }

    public String getIzena() {
        return izena;
    }

    public void setIzena(String izena) {
        this.izena = izena;
    }

    public String getSektorea() {
        return sektorea;
    }

    public void setSektorea(String sektorea) {
        this.sektorea = sektorea;
    }

    public String getTelefonoa() {
        return telefonoa;
    }

    public void setTelefonoa(String telefonoa) {
        this.telefonoa = telefonoa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
