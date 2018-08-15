package bca.co.id.mini_internet_banking;

public class Transaction {
    private String date;
    private String type;
    private String info;
    private double nominal;
    private String status;

    public Transaction(String date, String type, String info, int nominal){
        this.setDate(date);
        this.setType(type);
        this.setInfo(info);
        this.setNominal(nominal);
    }

    public Transaction(String date, String type, String info, int nominal, String status){
        this.setDate(date);
        this.setType(type);
        this.setInfo(info);
        this.setNominal(nominal);
        this.setStatus(status);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
