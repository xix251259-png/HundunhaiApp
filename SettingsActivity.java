package com.hundunhai.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "HundunhaiPrefs";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_FONT_SIZE = "font_size";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // 应用主题
        applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }

        // 初始化主题设置
        initThemeSettings();

        // 初始化语言设置
        initLanguageSettings();

        // 初始化字体大小设置
        initFontSizeSettings();

        // 内容管理按钮
        Button contentManagerBtn = findViewById(R.id.contentManagerBtn);
        contentManagerBtn.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, ContentManagerActivity.class));
        });

        // 版本号
        TextView versionName = findViewById(R.id.versionName);
        versionName.setText(BuildConfig.VERSION_NAME);
    }

    private void applyTheme() {
        String theme = prefs.getString(KEY_THEME, "default");
        switch (theme) {
            case "dark":
                setTheme(R.style.Theme_Hundunhai_Dark);
                break;
            case "blue":
                setTheme(R.style.Theme_Hundunhai_Blue);
                break;
            case "green":
                setTheme(R.style.Theme_Hundunhai_Green);
                break;
            case "gold":
                setTheme(R.style.Theme_Hundunhai_Gold);
                break;
            default:
                setTheme(R.style.Theme_Hundunhai);
                break;
        }
    }

    private void initThemeSettings() {
        RadioGroup themeRadioGroup = findViewById(R.id.themeRadioGroup);
        String currentTheme = prefs.getString(KEY_THEME, "default");

        switch (currentTheme) {
            case "dark":
                ((RadioButton) findViewById(R.id.themeDark)).setChecked(true);
                break;
            case "blue":
                ((RadioButton) findViewById(R.id.themeBlue)).setChecked(true);
                break;
            case "green":
                ((RadioButton) findViewById(R.id.themeGreen)).setChecked(true);
                break;
            case "gold":
                ((RadioButton) findViewById(R.id.themeGold)).setChecked(true);
                break;
            default:
                ((RadioButton) findViewById(R.id.themeDefault)).setChecked(true);
                break;
        }

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String theme = "default";
            
            if (checkedId == R.id.themeDark) {
                theme = "dark";
            } else if (checkedId == R.id.themeBlue) {
                theme = "blue";
            } else if (checkedId == R.id.themeGreen) {
                theme = "green";
            } else if (checkedId == R.id.themeGold) {
                theme = "gold";
            }

            prefs.edit()
                    .putString(KEY_THEME, theme)
                    .putBoolean("theme_changed", true)
                    .apply();
            
            recreate();
        });
    }

    private void initLanguageSettings() {
        RadioGroup languageRadioGroup = findViewById(R.id.languageRadioGroup);
        String currentLang = prefs.getString(KEY_LANGUAGE, "zh");

        if ("en".equals(currentLang)) {
            ((RadioButton) findViewById(R.id.langEn)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.langZh)).setChecked(true);
        }

        languageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String lang = "zh";
            if (checkedId == R.id.langEn) {
                lang = "en";
            }

            prefs.edit()
                    .putString(KEY_LANGUAGE, lang)
                    .apply();

            // 切换语言
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            // 标记主题变化以重启MainActivity
            prefs.edit().putBoolean("theme_changed", true).apply();
            
            recreate();
        });
    }

    private void initFontSizeSettings() {
        RadioGroup fontSizeRadioGroup = findViewById(R.id.fontSizeRadioGroup);
        int currentFontSize = prefs.getInt(KEY_FONT_SIZE, 16);

        if (currentFontSize <= 14) {
            ((RadioButton) findViewById(R.id.fontSmall)).setChecked(true);
        } else if (currentFontSize <= 16) {
            ((RadioButton) findViewById(R.id.fontMedium)).setChecked(true);
        } else if (currentFontSize <= 18) {
            ((RadioButton) findViewById(R.id.fontLarge)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.fontXlarge)).setChecked(true);
        }

        fontSizeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int fontSize = 16;
            
            if (checkedId == R.id.fontSmall) {
                fontSize = 14;
            } else if (checkedId == R.id.fontMedium) {
                fontSize = 16;
            } else if (checkedId == R.id.fontLarge) {
                fontSize = 18;
            } else if (checkedId == R.id.fontXlarge) {
                fontSize = 20;
            }

            prefs.edit()
                    .putInt(KEY_FONT_SIZE, fontSize)
                    .putBoolean("theme_changed", true)
                    .apply();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
