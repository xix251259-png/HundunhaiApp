package com.hundunhai.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;
    private LinearLayout searchBar;
    private EditText searchEditText;
    private Button searchCloseBtn;
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
        setContentView(R.layout.activity_main);

        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 初始化WebView
        initWebView();

        // 初始化底部导航
        initBottomNavigation();

        // 初始化搜索
        initSearch();

        // 加载内容
        loadContent();
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

    private void initWebView() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // 设置字体大小
        int fontSize = prefs.getInt(KEY_FONT_SIZE, 16);
        webSettings.setDefaultFontSize(fontSize);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("file://") || url.startsWith("javascript:")) {
                    return false;
                }
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
    }

    private void initBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                webView.loadUrl("javascript:scrollToTop()");
                return true;
            } else if (itemId == R.id.nav_toc) {
                webView.loadUrl("javascript:scrollToVol('toc')");
                return true;
            } else if (itemId == R.id.nav_search) {
                toggleSearch();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            
            return false;
        });
    }

    private void initSearch() {
        searchBar = findViewById(R.id.searchBar);
        searchEditText = findViewById(R.id.searchEditText);
        searchCloseBtn = findViewById(R.id.searchCloseBtn);

        searchCloseBtn.setOnClickListener(v -> toggleSearch());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void toggleSearch() {
        if (searchBar.getVisibility() == View.VISIBLE) {
            searchBar.setVisibility(View.GONE);
            searchEditText.setText("");
            webView.clearFocus();
        } else {
            searchBar.setVisibility(View.VISIBLE);
            searchEditText.requestFocus();
        }
    }

    private void performSearch() {
        String query = searchEditText.getText().toString();
        if (!query.isEmpty()) {
            webView.loadUrl("javascript:searchContent('" + query + "')");
        }
    }

    private void loadContent() {
        // 检查是否有自定义内容
        File customContent = new File(getFilesDir(), "custom_content.html");
        if (customContent.exists()) {
            webView.loadUrl("file://" + customContent.getAbsolutePath());
        } else {
            // 加载默认内容
            webView.loadUrl("file:///android_asset/index.html");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新加载以应用主题变化
        if (prefs.getBoolean("theme_changed", false)) {
            prefs.edit().putBoolean("theme_changed", false).apply();
            recreate();
        } else if (prefs.getBoolean("content_changed", false)) {
            prefs.edit().putBoolean("content_changed", false).apply();
            loadContent();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
