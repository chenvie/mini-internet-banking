package bca.co.id.mini_internet_banking;

import java.util.ArrayList;
import java.util.List;

//local storage of nasabah data when application run
public class Nasabah {
    protected static String id;
    protected static String name;
    protected static String username;
    protected static String email;
    protected static String password;
    protected static String ktpNum;
    protected static String birthday;
    protected static String address;
    protected static List<Rekening> rekenings = new ArrayList<Rekening>();
}
