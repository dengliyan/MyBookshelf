//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.monke.monkeybook.MApplication;
import com.monke.monkeybook.R;
import com.monke.monkeybook.utils.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadBookControl {
    private static final int DEFAULT_TEXT = 3;
    private static final int DEFAULT_BG = 1;

    private List<Map<String, Integer>> textKind;
    private List<Map<String, Integer>> textDrawable;

    private int speechRate;
    private boolean speechRateFollowSys;
    private int textSize;
    private int textExtra;
    private int textColor;
    private Drawable textBackground;
    private float lineMultiplier;

    private int textKindIndex;
    private int textDrawableIndex = DEFAULT_BG;

    private Boolean hideStatusBar;
    private Boolean hideNavigationBar;
    private String fontPath;
    private int textConvert;
    private Boolean textBold;
    private Boolean canClickTurn;
    private Boolean canKeyTurn;
    private Boolean keepScreenOn;
    private int clickSensitivity;
    private Boolean clickAllNext;
    private Boolean clickAnim;
    private int textColorCustom;
    private int backgroundColorCustom;
    private Boolean backgroundIsColor;
    private Boolean showTitle;
    private Boolean showTimeBattery;
    private Boolean showLine;
    private long lineChange;
    private String lastNoteUrl;
    private Boolean darkStatusIcon;

    private SharedPreferences readPreference;
    private SharedPreferences defaultPreference;

    private static ReadBookControl readBookControl;

    public static ReadBookControl getInstance() {
        if (readBookControl == null) {
            synchronized (ReadBookControl.class) {
                if (readBookControl == null) {
                    readBookControl = new ReadBookControl();
                }
            }
        }
        return readBookControl;
    }

    private ReadBookControl() {
        initTextKind();
        initTextDrawable();
        readPreference = MApplication.getInstance().getSharedPreferences("CONFIG", 0);
        defaultPreference = PreferenceManager.getDefaultSharedPreferences(MApplication.getInstance());
        this.hideStatusBar = defaultPreference.getBoolean("hide_status_bar", false);
        this.hideNavigationBar = defaultPreference.getBoolean("hide_navigation_bar", false);
        this.textKindIndex = readPreference.getInt("textKindIndex", DEFAULT_TEXT);
        this.textSize = textKind.get(textKindIndex).get("textSize");
        this.textExtra = textKind.get(textKindIndex).get("textExtra");
        this.canClickTurn = readPreference.getBoolean("canClickTurn", true);
        this.canKeyTurn = readPreference.getBoolean("canKeyTurn", true);
        this.keepScreenOn = readPreference.getBoolean("keepScreenOn", false);
        this.lineMultiplier = readPreference.getFloat("lineMultiplier", 1);
        this.clickSensitivity = readPreference.getInt("clickSensitivity", 50)>100
                ?50: readPreference.getInt("clickSensitivity", 50);
        this.clickAllNext = readPreference.getBoolean("clickAllNext", false);
        this.clickAnim = readPreference.getBoolean("clickAnim", true);
        this.textColorCustom = readPreference.getInt("textColorCustom", Color.parseColor("#383838"));
        this.backgroundColorCustom = readPreference.getInt("backgroundColorCustom", Color.parseColor("#1e1e1e"));
        this.backgroundIsColor = readPreference.getBoolean("backgroundIsColor", true);
        this.fontPath = readPreference.getString("fontPath",null);
        this.textConvert = readPreference.getInt("textConvertInt",0);
        this.textBold = readPreference.getBoolean("textBold", false);
        this.speechRate = readPreference.getInt("speechRate", 10);
        this.speechRateFollowSys = readPreference.getBoolean("speechRateFollowSys", true);
        this.showTitle = readPreference.getBoolean("showTitle", true);
        this.showTimeBattery = readPreference.getBoolean("showTimeBattery", true);
        this.showLine = readPreference.getBoolean("showLine", true);
        this.lineChange = readPreference.getLong("lineChange", System.currentTimeMillis());
        this.lastNoteUrl = readPreference.getString("lastNoteUrl", "");
        this.darkStatusIcon = defaultPreference.getBoolean("darkStatusIcon", false);

        initTextDrawableIndex();
    }

    public void initTextDrawableIndex() {
        if (getIsNightTheme()) {
            this.textDrawableIndex = readPreference.getInt("textDrawableIndexNight", 4);
        } else {
            this.textDrawableIndex = readPreference.getInt("textDrawableIndex", DEFAULT_BG);
        }
        setTextDrawable(MApplication.getInstance());
    }

    private void setTextDrawable(Context context) {
        ACache aCache = ACache.get(context);
        if (textDrawableIndex != -1) {
            this.textColor = textDrawable.get(textDrawableIndex).get("textColor");
            switch (readPreference.getInt("bgCustom" + textDrawableIndex, 0)) {
                case 2:
                    Bitmap bitmap = aCache.getAsBitmap("customBg" + textDrawableIndex);
                    if (bitmap != null) {
                        this.textBackground = new BitmapDrawable(context.getResources(), bitmap);
                    } else {
                        this.textBackground = context.getResources().getDrawable(textDrawable.get(textDrawableIndex).get("textBackground"));
                    }
                    break;
                case 1:
                    this.textBackground = new ColorDrawable(readPreference.getInt("bgColor" + textDrawableIndex, Color.parseColor("#1e1e1e")));
                default:
                    this.textBackground = context.getResources().getDrawable(textDrawable.get(textDrawableIndex).get("textBackground"));
            }
        } else {
            this.textColor = readPreference.getInt("textColorCustom", Color.parseColor("#383838"));
            if (backgroundIsColor) {
                this.textBackground = new ColorDrawable(backgroundColorCustom);
            } else {
                Bitmap bitmap = aCache.getAsBitmap("customBg");
                if (bitmap != null) {
                    this.textBackground = new BitmapDrawable(context.getResources(), bitmap);
                } else {
                    this.textBackground = context.getResources().getDrawable(textDrawable.get(1).get("textBackground"));
                }
            }
        }
    }

    public boolean getIsNightTheme() {
        return defaultPreference.getBoolean("nightTheme", false);
    }

    public boolean getImmersionStatusBar() {
        return defaultPreference.getBoolean("immersionStatusBar", false);
    }

    public String getLastNoteUrl() {
        return lastNoteUrl;
    }

    public void setLastNoteUrl(String lastNoteUrl) {
        this.lastNoteUrl = lastNoteUrl;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putString("lastNoteUrl", lastNoteUrl);
        editor.apply();
    }

    //字体大小
    private void initTextKind() {
        if (null == textKind) {
            textKind = new ArrayList<>();
            for (int i = 14; i<=30; i++) {
                Map<String, Integer> temp = new HashMap<>();
                temp.put("textSize", i);
                temp.put("textExtra", DensityUtil.dp2px(MApplication.getInstance(), i/2));
                textKind.add(temp);
            }
        }
    }

    //阅读背景
    private void initTextDrawable() {
        if (null == textDrawable) {
            textDrawable = new ArrayList<>();
            Map<String, Integer> temp1 = new HashMap<>();
            temp1.put("textColor", Color.parseColor("#3E3D3B"));
            temp1.put("textBackground", R.drawable.shape_bg_readbook_white);
            textDrawable.add(temp1);

            Map<String, Integer> temp2 = new HashMap<>();
            temp2.put("textColor", Color.parseColor("#5E432E"));
            temp2.put("textBackground", R.drawable.bg_readbook_yellow);
            textDrawable.add(temp2);

            Map<String, Integer> temp3 = new HashMap<>();
            temp3.put("textColor", Color.parseColor("#22482C"));
            temp3.put("textBackground", R.drawable.bg_readbook_green);
            textDrawable.add(temp3);

            Map<String, Integer> temp4 = new HashMap<>();
            temp4.put("textColor", Color.parseColor("#FFFFFF"));
            temp4.put("textBackground", R.drawable.bg_readbook_blue);
            textDrawable.add(temp4);

            Map<String, Integer> temp5 = new HashMap<>();
            temp5.put("textColor", Color.parseColor("#808080"));
            temp5.put("textBackground", R.drawable.bg_readbook_black);
            textDrawable.add(temp5);
        }
    }

    public int getTextSize() {
        return textSize;
    }

    public int getTextExtra() {
        return textExtra;
    }

    public int getTextColor() {
        return textColor;
    }

    public Drawable getTextBackground() {
        return textBackground;
    }

    public int getTextKindIndex() {
        return textKindIndex;
    }

    public void setTextKindIndex(int textKindIndex) {
        this.textKindIndex = textKindIndex;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putInt("textKindIndex", textKindIndex);
        editor.apply();
        this.textSize = textKind.get(textKindIndex).get("textSize");
        this.textExtra = textKind.get(textKindIndex).get("textExtra");
    }

    public int getTextDrawableIndex() {
        return textDrawableIndex;
    }

    public void setTextDrawableIndex(int textDrawableIndex) {
        this.textDrawableIndex = textDrawableIndex;
        SharedPreferences.Editor editor = readPreference.edit();
        if (getIsNightTheme()) {
            editor.putInt("textDrawableIndexNight", textDrawableIndex);
        } else {
            editor.putInt("textDrawableIndex", textDrawableIndex);
        }
        editor.apply();
        setTextDrawable(MApplication.getInstance());
    }

    public void setTextConvert(int textConvert) {
        this.textConvert = textConvert;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putInt("textConvertInt", textConvert);
        editor.apply();
    }

    public void setTextBold(boolean textBold) {
        this.textBold = textBold;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("textBold", textBold);
        editor.apply();
    }

    public void setReadBookFont(String fontPath) {
        this.fontPath = fontPath;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putString("fontPath", fontPath);
        editor.apply();
    }

    public String getFontPath() {
        return fontPath;
    }

    public int getTextConvert() {
        return textConvert;
    }

    public Boolean getTextBold() {
        return textBold;
    }

    public int getTextColorCustom() {
        return textColorCustom;
    }

    public void setTextColorCustom(int textColorCustom) {
        this.textColorCustom = textColorCustom;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putInt("textColorCustom", textColorCustom);
        editor.apply();
    }

    public int getBackgroundColorCustom() {
        return backgroundColorCustom;
    }

    public void setBackgroundColorCustom(int backgroundColorCustom) {
        this.backgroundColorCustom = backgroundColorCustom;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putInt("backgroundColorCustom", backgroundColorCustom);
        editor.apply();
    }

    public void setBackgroundIsColor(Boolean backgroundIsColor) {
        this.backgroundIsColor = backgroundIsColor;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("backgroundIsColor", backgroundIsColor);
        editor.apply();
    }

    public List<Map<String, Integer>> getTextKind() {
        return textKind;
    }

    public List<Map<String, Integer>> getTextDrawable() {
        return textDrawable;
    }

    public Boolean getCanKeyTurn() {
        return canKeyTurn;
    }

    public void setCanKeyTurn(Boolean canKeyTurn) {
        this.canKeyTurn = canKeyTurn;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("canKeyTurn", canKeyTurn);
        editor.apply();
    }

    public Boolean getCanClickTurn() {
        return canClickTurn;
    }

    public void setCanClickTurn(Boolean canClickTurn) {
        this.canClickTurn = canClickTurn;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("canClickTurn", canClickTurn);
        editor.apply();
    }

    public Boolean getKeepScreenOn() {
        return keepScreenOn;
    }

    public void setKeepScreenOn(Boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("keepScreenOn", keepScreenOn);
        editor.apply();
    }

    public float getLineMultiplier() {
        return lineMultiplier;
    }

    public void setLineMultiplier(float lineMultiplier) {
        this.lineMultiplier = lineMultiplier;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putFloat("lineMultiplier", lineMultiplier);
        editor.apply();
    }

    public int getClickSensitivity() {
        return clickSensitivity;
    }

    public void setClickSensitivity(int clickSensitivity) {
        this.clickSensitivity = clickSensitivity;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putInt("clickSensitivity", clickSensitivity);
        editor.apply();
    }

    public Boolean getClickAllNext() {
        return clickAllNext;
    }

    public void setClickAllNext(Boolean clickAllNext) {
        this.clickAllNext = clickAllNext;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("clickAllNext", clickAllNext);
        editor.apply();
    }

    public Boolean getClickAnim() {
        return clickAnim;
    }

    public void setClickAnim(Boolean clickAnim) {
        this.clickAnim = clickAnim;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("clickAnim", clickAnim);
        editor.apply();
    }

    public int getSpeechRate() {
        return speechRate;
    }

    public void setSpeechRate(int speechRate) {
        this.speechRate = speechRate;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putInt("speechRate", speechRate);
        editor.apply();
    }

    public boolean isSpeechRateFollowSys() {
        return speechRateFollowSys;
    }

    public void setSpeechRateFollowSys(boolean speechRateFollowSys) {
        this.speechRateFollowSys = speechRateFollowSys;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("speechRateFollowSys", speechRateFollowSys);
        editor.apply();
    }

    public Boolean getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(Boolean showTitle) {
        this.showTitle = showTitle;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("showTitle", showTitle);
        editor.apply();
    }

    public Boolean getShowTimeBattery() {
        return showTimeBattery;
    }

    public void setShowTimeBattery(Boolean showTimeBattery) {
        this.showTimeBattery = showTimeBattery;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("showTimeBattery", showTimeBattery);
        editor.apply();
    }

    public Boolean getHideStatusBar() {
        return hideStatusBar;
    }

    public void setHideStatusBar(Boolean hideStatusBar) {
        this.hideStatusBar = hideStatusBar;
        SharedPreferences.Editor editor = defaultPreference.edit();
        editor.putBoolean("hide_status_bar", hideStatusBar);
        editor.apply();
    }

    public Boolean getHideNavigationBar() {
        return hideNavigationBar;
    }

    public void setHideNavigationBar(Boolean hideNavigationBar) {
        this.hideNavigationBar = hideNavigationBar;
        SharedPreferences.Editor editor = defaultPreference.edit();
        editor.putBoolean("hide_navigation_bar", hideNavigationBar);
        editor.apply();
    }

    public Boolean getShowLine() {
        return showLine;
    }

    public void setShowLine(Boolean showLine) {
        this.showLine = showLine;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putBoolean("showLine", showLine);
        editor.apply();
    }

    public long getLineChange() {
        return lineChange;
    }

    public void setLineChange(long lineChange) {
        this.lineChange = lineChange;
        SharedPreferences.Editor editor = readPreference.edit();
        editor.putLong("lineChange", lineChange);
        editor.apply();
    }

    public Boolean getDarkStatusIcon() {
        return darkStatusIcon;
    }

    public void setDarkStatusIcon(Boolean darkStatusIcon) {
        this.darkStatusIcon = darkStatusIcon;
        SharedPreferences.Editor editor = defaultPreference.edit();
        editor.putBoolean("darkStatusIcon", darkStatusIcon);
        editor.apply();
    }

}
