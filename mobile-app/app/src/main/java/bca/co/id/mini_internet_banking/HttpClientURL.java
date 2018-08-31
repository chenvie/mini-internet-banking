package bca.co.id.mini_internet_banking;

//list of API URL
public class HttpClientURL {
    private static final String BASE_URL = "http://10.0.2.2/mini-internet-banking/API/";

    public static final String urlWriteLog = BASE_URL + "log_mobile/write-log.php";

    public static final String urlLogin = BASE_URL + "nasabah/login.php";
    public static final String urlReadOne = BASE_URL + "nasabah/read-one.php";
    public static final String urlCreateNasabah = BASE_URL + "nasabah/create.php";
    public static final String urlUpdateCode = BASE_URL + "nasabah/update_kode_rahasia.php";
    public static final String urlUpdatePassword = BASE_URL + "nasabah/update_password.php";

    public static final String urlBuying = BASE_URL + "pulsa/create.php";

    public static final String urlReadHistory = BASE_URL + "transaksi/read-history.php";
    public static final String urlReadMutation = BASE_URL + "transaksi/read-mutasi.php";

    public static final String urlCheckRekNum = BASE_URL + "transfer/cek-no-rek.php";
    public static final String urlTransfer = BASE_URL + "transfer/create.php";
}
