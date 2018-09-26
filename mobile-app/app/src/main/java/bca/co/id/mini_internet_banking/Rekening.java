package bca.co.id.mini_internet_banking;

import com.google.gson.annotations.SerializedName;

public class Rekening {
    @SerializedName("rekeningNum")
    private String rekeningNum;

    @SerializedName("secretCode")
    private String secretCode;

    @SerializedName("saldo")
    private float saldo;

    @SerializedName("kode_cabang")
    private String kode_cabang;

    public Rekening(String rekNum, String code, float saldo, String cabang){
        this.setRekeningNum(rekNum);
        this.setSecretCode(code);
        this.setSaldo(saldo);
        this.setKode_cabang(cabang);
    }


    public String getRekeningNum() {
        return rekeningNum;
    }

    public void setRekeningNum(String rekeningNum) {
        this.rekeningNum = rekeningNum;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public String getKode_cabang() {
        return kode_cabang;
    }

    public void setKode_cabang(String kode_cabang) {
        this.kode_cabang = kode_cabang;
    }
}
