package bca.co.id.mini_internet_banking;

//local storage for transaction for mutation and history
public class Transaction {
    private String date;
    private String goal;
    private String info;
    private float nominal;
    private String status;

    public Transaction(String date, String goal, String info, float nominal){
        this.setDate(date);
        this.setGoal(goal);
        this.setInfo(info);
        this.setNominal(nominal);
    }

    public Transaction(String date, String goal, String info, float nominal, String status){
        this.setDate(date);
        this.setGoal(goal);
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

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public float getNominal() {
        return nominal;
    }

    public void setNominal(float nominal) {
        this.nominal = nominal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
