package me.ag2s.book.activity;



import android.content.Intent;
import android.os.Bundle;

import me.ag2s.book.R;
import me.ag2s.book.base.BaseActivity;

public class EpubReadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_read);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}