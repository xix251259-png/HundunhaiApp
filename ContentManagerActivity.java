package com.hundunhai.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContentManagerActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private SharedPreferences prefs;
    private TextView contentStatus;
    private TextView contentSize;
    private EditText urlEditText;

    private static final String PREFS_NAME = "HundunhaiPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // 应用主题
        String theme = prefs.getString("theme", "default");
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
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_manager);

        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.content_manager);
        }

        // 初始化状态显示
        contentStatus = findViewById(R.id.contentStatus);
        contentSize = findViewById(R.id.contentSize);
        urlEditText = findViewById(R.id.urlEditText);

        updateStatus();

        // 从文件导入按钮
        Button importFileBtn = findViewById(R.id.importFileBtn);
        importFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/html");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(
                        Intent.createChooser(intent, "选择HTML文件"),
                        PICK_FILE_REQUEST
                );
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
            }
        });

        // 从URL更新按钮
        Button updateFromUrlBtn = findViewById(R.id.updateFromUrlBtn);
        updateFromUrlBtn.setOnClickListener(v -> {
            String url = urlEditText.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "请输入URL地址", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadAndUpdateContent(url);
        });

        // 重置内容按钮
        Button resetContentBtn = findViewById(R.id.resetContentBtn);
        resetContentBtn.setOnClickListener(v -> {
            resetToDefault();
        });
    }

    private void updateStatus() {
        File customContent = new File(getFilesDir(), "custom_content.html");
        if (customContent.exists()) {
            contentStatus.setText("当前使用：自定义内容");
            long size = customContent.length();
            String sizeStr;
            if (size > 1024 * 1024) {
                sizeStr = String.format("%.1f MB", size / (1024.0 * 1024.0));
            } else if (size > 1024) {
                sizeStr = String.format("%.1f KB", size / 1024.0);
            } else {
                sizeStr = size + " B";
            }
            contentSize.setText("内容大小：" + sizeStr);
        } else {
            contentStatus.setText("当前使用：内置默认内容");
            contentSize.setText("内容大小：约 12 MB");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                importFromUri(uri);
            }
        }
    }

    private void importFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(this, "无法读取文件", Toast.LENGTH_SHORT).show();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            inputStream.close();

            saveCustomContent(content.toString());
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
            updateStatus();

        } catch (Exception e) {
            Toast.makeText(this, R.string.update_failed + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void downloadAndUpdateContent(String urlStr) {
        Toast.makeText(this, "正在下载更新...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("HTTP " + responseCode);
                }

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                inputStream.close();
                connection.disconnect();

                saveCustomContent(content.toString());

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
                    updateStatus();
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, R.string.update_failed + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void saveCustomContent(String content) {
        try {
            File customFile = new File(getFilesDir(), "custom_content.html");
            FileOutputStream outputStream = new FileOutputStream(customFile);
            outputStream.write(content.getBytes("UTF-8"));
            outputStream.close();

            prefs.edit()
                    .putBoolean("content_changed", true)
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetToDefault() {
        try {
            File customFile = new File(getFilesDir(), "custom_content.html");
            if (customFile.exists()) {
                customFile.delete();
            }

            prefs.edit()
                    .putBoolean("content_changed", true)
                    .apply();

            Toast.makeText(this, R.string.reset_success, Toast.LENGTH_SHORT).show();
            updateStatus();

        } catch (Exception e) {
            Toast.makeText(this, "重置失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
