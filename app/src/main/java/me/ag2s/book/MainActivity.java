package me.ag2s.book;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;

import me.ag2s.base.FileTool;
import me.ag2s.base.tools.FileTools;
import me.ag2s.book.base.BaseActivity;
import me.ag2s.book.tool.SharedPreferencesUtil;
import me.ag2s.book.tool.TXTCallBack;
import me.ag2s.book.tool.TextBook;
import me.ag2s.book.tool.TextBookHeaper;
import me.ag2s.book.tool.TextPrase;
import me.ag2s.book.viewmodel.TestViewModel;

public class MainActivity extends BaseActivity {
    private final String TAG = getClass().getName();

    private AlertDialog.Builder builder;
    TextView tv;
    TestViewModel viewModel;
    Uri baseuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        viewModel = new ViewModelProvider(this).get(TestViewModel.class);
        viewModel.msg.observe(this, msg -> {
            tv.setText(msg);
            //tv.setText(Html.fromHtml(msg));
        });
        viewModel.uri.observe(this, uri -> {
            baseuri = uri;
        });
        viewModel.tbook.observe(this, textBook -> {
            tv.setText(textBook.toString());
        });
        //第一步
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //第二步， 第三个参数传送唯一值即可
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            //取得权限后的业务逻辑
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == FileTools.CREATE_FILE && resultCode == AppCompatActivity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            String title = data.getStringExtra(Intent.EXTRA_TITLE);
            Uri uri = data.getData();
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));


        }
        if (requestCode == FileTools.REUEST_FILE_DIR && resultCode == AppCompatActivity.RESULT_OK) {
            try {
                viewModel.msg.postValue(data.getData().toString());
                Uri uri = data.getData();
                getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                //SharedPreferencesUtil.putBoolean(this,uri.getPath(),true);
                //DocumentFile df = DocumentFile.fromSingleUri(this, uri);


            } catch (Exception e) {
                SharedPreferencesUtil.putBoolean(this, data.getData().getPath(), false);
                viewModel.msg.postValue(e.getLocalizedMessage());
            }
        } else if (requestCode == FileTools.SELECT_TEXT && resultCode == AppCompatActivity.RESULT_OK) {
            Uri uri = data.getData();
            TextBook tbook = new TextBook();


            try {
                DocumentFile df = DocumentFile.fromSingleUri(this, uri);
                //InputStream ins = getContentResolver().openInputStream(df.getUri());
                //tbook.txtPath = df.getUri().toString();
                tbook.txtPath = df.getName();
                //tbook.temppath = file.getAbsolutePath();
                tbook.uri = df.getUri();
                Log.d(TAG, uri.toString());
                TextBookHeaper.guestBook(tbook);
                viewModel.tbook.postValue(tbook);
            } catch (Exception e) {
                viewModel.msg.postValue(FileTool.getStackTrace(e));
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //第三步
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //处理取得权限和后的业务逻辑
                } else {
                    //未取得权限的业务逻辑
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    //第一步在上面申请了

    private void readContact() {

    }


    public void readEPUB2(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] s = {""};
                TextBookHeaper.getNetBookInfo(viewModel.tbook.getValue(), new TXTCallBack() {
                    @Override
                    public void onRun(String msg) {
                        s[0] = attachString(s[0], msg);
                        viewModel.msg.postValue(s[0]);
                    }

                    @Override
                    public void OnStart(TextBook msg) {

                        s[0] = attachString(s[0], msg.toString());
                        viewModel.msg.postValue(s[0]);
                    }

                    @Override
                    public void onFinish(TextBook msg) {

                        s[0] = attachString(s[0], msg.toString());
                        viewModel.msg.postValue(s[0]);
                    }
                });
            }
        }).start();


    }

    public void readEPUB3(View view) {
        TextBook tbook = viewModel.tbook.getValue();
        Uri uri = FileTools.pathToTreeUri("ag2sapp");

        if (FileTools.hasPremistion(uri)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String[] s = {""};
                    TextPrase tp = new TextPrase(tbook.txtPath);
                    tp.prase(tbook, new TXTCallBack() {
                        @Override
                        public void onRun(String msg) {
                            s[0] = attachString(s[0], msg);
                            viewModel.msg.postValue(s[0]);
                        }

                        @Override
                        public void OnStart(TextBook msg) {
                            s[0] = attachString(s[0], msg.toString());
                            viewModel.msg.postValue(s[0]);
                        }

                        @Override
                        public void onFinish(TextBook msg) {

                            s[0] = attachString(s[0], msg.toString());
                            viewModel.msg.postValue(s[0]);
                            FileTools.getALLText();
                        }
                    });
                }
            }).start();

        } else {
            FileTools.RequestDirPermision(this, "ag2saapp");
        }


    }


    private String attachString(String old, String add) {
        add += "\n" + old;
        if (add.length() > 1000) {
            add = add.substring(0, 1000);
        }
        return add;
    }


    public void createEPUB(View view) {
        FileTools.showFileChooser(this, FileTools.SELECT_TEXT);


    }


}