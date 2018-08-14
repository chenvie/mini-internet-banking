package bca.co.id.mini_internet_banking;

import android.graphics.Color;

public enum CodeStrength {
    WEAK(0, Color.RED), MEDIUM(1, Color.argb(255, 220, 185, 0)), STRONG(2, Color.GREEN), VERY_STRONG(3, Color.BLUE);

    //--------REQUIREMENTS--------
    static int REQUIRED_LENGTH = 6;
    static boolean REQUIRE_DIGITS = true;
    static boolean REQUIRE_ALPHA = true;

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

        for (int i = 0; i < code.length(); i++){
            char c = code.charAt(i);
            if (!sawDigit && Character.isDigit(c)){
                currentScore += 1;
                sawDigit = true;
            }
            if ((!sawAlpha && Character.isUpperCase(c)) || (!sawAlpha && Character.isLowerCase(c))){
                sawAlpha = true;
                currentScore += 1;
            }
        }

        if (code.length() > REQUIRED_LENGTH || code.length() < REQUIRED_LENGTH) {
            currentScore = 0;
        } else {
            if ((REQUIRE_ALPHA && sawAlpha) && (REQUIRE_DIGITS && sawDigit)) {
                currentScore = 2;
            } else {
                currentScore = 0;
            }
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

