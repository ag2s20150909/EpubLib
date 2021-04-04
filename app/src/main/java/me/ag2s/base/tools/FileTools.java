package me.ag2s.base.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.ag2s.base.APP;

public class FileTools {
    public static final String TAG = FileTools.class.getName();
    public static int REUEST_FILE_DIR = 1001;
    public static int SELECT_TEXT = 1002;
    // Request code for creating a PDF document.
    public static final int CREATE_FILE = 1;
    // Request code for selecting a PDF document.
    public static final int PICK_PDF_FILE = 2;

    public static void createFile(AppCompatActivity activity, String name) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/epub+zip");
        intent.putExtra(Intent.EXTRA_TITLE, name);
        Uri uri = Uri.fromFile(new File("/sdcard/Download"));

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        }

        activity.startActivityForResult(intent, CREATE_FILE);
    }

    private InputStream getInputStreamForVirtualFile(Uri uri, String mimeTypeFilter)
            throws IOException {

        ContentResolver resolver = APP.getContext().getContentResolver();

        String[] openableMimeTypes = resolver.getStreamTypes(uri, mimeTypeFilter);

        if (openableMimeTypes == null ||
                openableMimeTypes.length < 1) {
            throw new FileNotFoundException();
        }

        return resolver
                .openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null)
                .createInputStream();
    }

    /**
     * 查找文件
     *
     * @return
     */
    public static ArrayList<FileEntity> getAllText(Context context) {

        ArrayList<FileEntity> texts = new ArrayList<>();

        String[] projection = new String[]{MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE
                , MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MIME_TYPE};

        //相当于我们常用sql where 后面的写法
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";

        String[] selectionArgs = new String[]{"application/msword", "text/plain", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel"};

        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
        FileEntity fileItem = new FileEntity();
        while (cursor.moveToNext()) {

            fileItem.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)));

            fileItem.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE)));

            fileItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));

            fileItem.setSize(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)));

            fileItem.setTime(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)));

            fileItem.setFileType(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)));

            texts.add(fileItem);

        }
        cursor.close();
        return texts;

    }


    public static boolean hasPremistion(Uri uri) {
        List<UriPermission> permissions = APP.getContext().getContentResolver().getPersistedUriPermissions();
        for (UriPermission permission : permissions) {

            if (uri.getPath().equals(permission.getUri().getPath())) {
                Log.d(TAG, "URI:" + uri.getPath());
                Log.d(TAG, "PERMISSION:" + permission.getUri().getPath());
                return true;
            }
        }
        return false;
    }

    public static boolean releaseUriPermission(Uri uri) {
        try {
            APP.getContext().getContentResolver().releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 请求文件夹的读写权限
     *
     * @param context
     * @param path
     */
    public static void RequestDirPermision(AppCompatActivity context, String path) {
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(context);
        alertdialogbuilder.setTitle("请求文件夹读写权限");
        alertdialogbuilder.setMessage("请打开文件夹\n" + path + "\n并授予权限");
        alertdialogbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                Uri parse = FileTools.pathToTreeUri(path);
                //intent.setDataAndType(parse,"*/*");
                intent.addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse);
                }
                context.startActivityForResult(intent, REUEST_FILE_DIR);
            }
        });
        alertdialogbuilder.setNeutralButton("取消", null);
        final AlertDialog alertdialog1 = alertdialogbuilder.create();
        alertdialog1.show();

        //context.getContentResolver().takePersistableUriPermission(parse, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    public static void RequestDirPermision(AppCompatActivity context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //Uri parse = FileTools.pathToTreeUri(path);
        //intent.setDataAndType(parse,"*/*");
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        }
        context.startActivityForResult(intent, REUEST_FILE_DIR);
    }

    private void sdcardAuth() {


    }


    /**
     * 创建文件如果成功返回true不成功返回false，需要获得父路径的读写权限。
     *
     * @param parent 文件的父路径，建议使用与 /sdcard/XXX 或者XXX
     * @param name   文件名称
     * @return 是否创建成功
     */
    public static boolean createFile(String parent, String name) {

        Context context = APP.getContext();
        try {
            DocumentFile df1 = DocumentFile.fromTreeUri(context, pathToTreeUri(parent));
            assert df1 != null;
            DocumentFile df2 = df1.findFile(name);
            if (df2 == null || !df2.exists()) {
                df2 = df1.createFile("*/*", name);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
        //return false;
    }

    /**
     * 保存文件如果成功返回true不成功返回false，需要获得父路径的读写权限。
     *
     * @param parent 保存文件的父路径，建议使用与 /sdcard/XXX 或者XXX
     * @param name   保存文件的名称
     * @param data   需要保存的数据
     * @return
     */

    public static boolean saveFile(String parent, String name, byte[] data) {
        Context context = APP.getContext();
        try {
            DocumentFile df1 = DocumentFile.fromTreeUri(context, pathToTreeUri(parent));
            DocumentFile df2 = df1.findFile(name);
            if (df2 == null || !df2.exists()) {
                df2 = df1.createFile("*/*", name);
            } else if (df2.isDirectory()) {
                df2.delete();
                df2 = df1.createFile("*/*", name);
            }
            OutputStream ops = context.getContentResolver().openOutputStream(df2.getUri(), "w");
            ops.write(data);
            ops.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean saveFile(String parent, String name, InputStream ips) {
        Context context = APP.getContext();
        try {
            DocumentFile df1 = DocumentFile.fromTreeUri(context, pathToTreeUri(parent));
            DocumentFile df2 = Objects.requireNonNull(df1).findFile(name);
            if (df2 == null || !df2.exists()) {
                df2 = df1.createFile("*/*", name);
            } else if (df2.isDirectory()) {
                df2.delete();
                df2 = df1.createFile("*/*", name);
            }
            byte[] bs = new byte[1024];
            int len;
            OutputStream ops = context.getContentResolver().openOutputStream(df2.getUri(), "w");
            while ((len = ips.read(bs)) != -1) {
                ops.write(bs, 0, len);
            }
            ops.close();
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public static OutputStream getOutStream(String parent, String name) {
        Context context = APP.getContext();
        try {
            DocumentFile df1 = DocumentFile.fromTreeUri(context, pathToTreeUri(parent));
            if (df1 == null || !df1.exists()) {
                DocumentFile documentFile = DocumentFile.fromTreeUri(context, pathToTreeUri(""));
                df1 = documentFile.createDirectory(parent);
            }
            DocumentFile df2 = df1.findFile(name);
            if (df2 == null || !df2.exists()) {
                df2 = df1.createFile("*/*", name);
            } else if (df2.isDirectory()) {
                df2.delete();
                df2 = df1.createFile("*/*", name);
            }
            byte[] bs = new byte[1024];
            int len;
            OutputStream ops = context.getContentResolver().openOutputStream(df2.getUri(), "w");

            return ops;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return null;
        }
    }

    public static InputStream getInStream(String parent, String name) {
        Context context = APP.getContext();
        try {
            DocumentFile df1 = DocumentFile.fromTreeUri(context, pathToTreeUri(parent));
            DocumentFile df2 = df1.findFile(name);
            if (df2 == null || !df2.exists()) {
                df2 = df1.createFile("*/*", name);
            } else if (df2.isDirectory()) {
                df2.delete();
                df2 = df1.createFile("*/*", name);
            }
            byte[] bs = new byte[1024];
            int len;
            InputStream ops = context.getContentResolver().openInputStream(df2.getUri());

            return ops;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return null;
        }
    }

    public static void showFileChooser(Activity a, int code) {

        Uri uri = Uri.fromFile(new File("/sdcard/Download/"));
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/*");//必须
        //intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        }
        a.startActivityForResult(intent, code);


    }


    @SuppressLint("SdCardPath")
    public static Uri pathToTreeUri(String path) {
        return Uri.parse(pathToTreeUriString(path));

    }

    @SuppressLint("SdCardPath")
    public static String pathToTreeUriString(String path) {
        if (path.startsWith("/storage/emulated/0/")) {
            path = path.replace("/storage/emulated/0/", "");
        } else if (path.startsWith("/sdcard/")) {
            path.replace("/sdcard/", "");
        }
        try {
            path = URLEncoder.encode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "content://com.android.externalstorage.documents/tree/primary%3A" + path;

    }

    public static List<String> getSubPath(String dirpath) {
        List<String> d = new ArrayList<>();
        Context context = APP.getContext();
        try {
            DocumentFile df1 = DocumentFile.fromTreeUri(context, pathToTreeUri(dirpath));
            for (DocumentFile df : df1.listFiles()) {
                d.add(df.getUri().toString());
            }

        } catch (Exception e) {

        }
        return d;
    }
}
