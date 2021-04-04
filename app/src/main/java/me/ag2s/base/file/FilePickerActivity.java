package me.ag2s.base.file;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import me.ag2s.base.tools.FileTools;
import me.ag2s.book.R;
import me.ag2s.book.tool.SharedPreferencesUtil;

public class FilePickerActivity extends AppCompatActivity {
    FilePickerAdapter adapter;
    private ListView lvFiles;
    private TextView tvFilePath;
    private Button btnFileHome;
    FilePickerViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        lvFiles = findViewById(R.id.file_lv);
        tvFilePath = findViewById(R.id.tv_file_path);
        btnFileHome = findViewById(R.id.btn_file_home);
        btnFileHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
        viewModel = new FilePickerViewModel(getApplication());
        viewModel.documents.observe(this, documents -> {
            adapter.update(documents);
        });
        viewModel.openDocument.observe(this, open -> {

        });
        viewModel.openDirectory.observe(this, open -> {
            showDirectoryContents(open);
        });
        adapter = new FilePickerAdapter(new ArrayList<>());
        lvFiles.setAdapter(adapter);
        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CachingDocumentFile item = (CachingDocumentFile) adapter.getItem(position);
                viewModel.documentClicked(item);

            }
        });
        init();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == FileTools.REUEST_FILE_DIR && resultCode == AppCompatActivity.RESULT_OK) {
            try {
                //viewModel.msg.postValue(data.getData().toString());
                Uri uri = data.getData();
                getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                viewModel.loadDirectory(uri);
                SharedPreferencesUtil.putBoolean(this, uri.toString(), true);
                //DocumentFile df = DocumentFile.fromSingleUri(this, uri);


            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                SharedPreferencesUtil.putBoolean(this, data.getData().getPath(), false);
                //viewModel.msg.postValue(e.getLocalizedMessage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void init() {
        Uri uri = FileTools.pathToTreeUri("");
        if (SharedPreferencesUtil.getBoolean(this, uri.toString(), false)) {
            viewModel.loadDirectory(uri);
        } else {
            FileTools.RequestDirPermision(this, "");
        }
    }

    public void openDirectory() {

    }

    public void showDirectoryContents(CachingDocumentFile c) {
        Uri uri = c.getUri();
        if (SharedPreferencesUtil.getBoolean(this, uri.toString(), false)) {
            viewModel.loadDirectory(uri);
        } else {
            FileTools.RequestDirPermision(this, uri);
        }
    }

}