package bca.co.id.mini_internet_banking;

import android.graphics.Color;

//checking secret code strength (min char, alfanumerik, and not include birthday), min requirement strength --> MEDIUM
public enum CodeStrength {
    WEAK(0, Color.RED), MEDIUM(1, Color.argb(255, 220, 185, 0)), STRONG(2, Color.GREEN), VERY_STRONG(3, Color.BLUE);

    //--------REQUIREMENTS--------
    static int REQUIRED_LENGTH = 6;
    static boolean REQUIRE_DIGITS = true;
    static boolean REQUIRE_ALPHA = true;
    static boolean REQUIRE_SPECIAL_CHARACTERS = false;

    int resId;
    int color;

    CodeStrength(int resId, int color){
        this.resId = resId;
        this.color = color;
    }

    public int getValue()
    {
        return resId;
    }

    public int getColor()
    {
        return color;
    }

    public static CodeStrength calculateStrength(String code){
        int currentScore = 0;
        boolean sawAlpha = false;
        boolean sawDigit = false;
        boolean sawSpecial = false;

        for (int i = 0; i < code.length(); i++){
            char c = code.charAt(i);
            if (!sawSpecial && !Character.isLetterOrDigit(c)){
                currentScore -= 1;
                sawSpecial = true;
            } else {
                if (!sawDigit && Character.isDigit(c)) {
                    currentScore += 1;
                    sawDigit = true;
                }
                if ((!sawAlpha && Character.isUpperCase(c)) || (!sawAlpha && Character.isLowerCase(c))) {
                    sawAlpha = true;
                    currentScore += 1;
                }
            }
        }

        if (code.length() > REQUIRED_LENGTH || code.length() < REQUIRED_LENGTH) {
            currentScore = 0;
        } else {
            if (!REQUIRE_SPECIAL_CHARACTERS && !sawSpecial) {
                if ((REQUIRE_ALPHA && sawAlpha) && (REQUIRE_DIGITS && sawDigit)) {
                    currentScore = 2;
                } else {
                    currentScore = 0;
                }
            } else{
                currentScore = 0;
            }
        }

        //date[0] = yyyy, date[1] = MM, date[2] = dd;
        String[] date = Nasabah.birthday.split("-");
        String yy = "";
        for (int i = 0; i < date[0].length(); i++){
            char d = date[0].charAt(i);
            if (i == 2 || i == 3){
                yy += d;
            }
        }

        String format1 = date[0]+date[1]+date[2]; //yyyyMMdd
        String format2 = yy+date[1]+date[2]; //yyMMdd
        String format3 = date[2]+date[1]+date[0]; //ddMMyyyy
        String format4 = date[2]+date[1]+yy; //ddMMyy
        String format5 = date[1]+date[2]+date[0]; //MMddyyyy
        String format6 = date[1]+date[2]+yy; //MMddyy
        String format7 = date[0]+date[2]+date[1]; //yyyyddMM
        String format8 = yy+date[2]+date[1]; //yyddMM
        String format9 = date[1]+date[0]+date[2]; //MMyyyydd
        String format10 = date[1]+yy+date[2]; //MMyydd
        String format11 = date[2]+date[0]+date[1]; //ddyyyyMM
        String format12 = date[2]+yy+date[1]; //ddyyMM

        //Log.e(CodeStrength.class.getSimpleName(), format1 + " - " + format2 + " - " + format3 + " - " + format4);

        if (code.contains(format1) || code.contains(format2) || code.contains(format3) || code.contains(format4) || code.contains(format5) || code.contains(format6) ||
                code.contains(format7) || code.contains(format8) || code.contains(format9) || code.contains(format10) || code.contains(format11) || code.contains(format12)){
            currentScore = 0;
        }

        switch (currentScore) {
            case 0:
                return WEAK;
            case 1:
                return MEDIUM;
            case 2:
                return STRONG;
            case 3:
                return VERY_STRONG;
            default:
        }

        return VERY_STRONG;
    }

}

