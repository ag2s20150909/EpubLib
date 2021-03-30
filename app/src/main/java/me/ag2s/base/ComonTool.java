package me.ag2s.base;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ComonTool {
    public static float dpToPx(Context context, float dps) {
        return Math.round(context.getResources().getDisplayMetrics().density * dps);
    }

    public static float getHeightInPx(Context context) {
        final float height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    public static float getWidthInPx(Context context) {
        final float width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }


    /**
     * unicode转中文
     *
     * @param str
     * @return
     * @author yutao
     * @date 2017年1月24日上午10:33:25
     */
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }


    public static String regFind(String reg, String line) {
        line = line.replace("&amp;", " ");
        String s = "";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(reg);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        if (m.find()) {
            s = m.group(1);
        } else {
            s = "NOT MATCH";
        }
        return s;
    }


    /**
     * 真正的加密过程
     * 1.通过密钥得到一个密钥专用的对象SecretKeySpec
     * 2.Cipher 加密算法，加密模式和填充方式三部分或指定加密算 (可以只用写算法然后用默认的其他方式)Cipher.getInstance("AES");
     *
     * @param key
     * @param src
     * @return
     * @throws Exception
     */
    public static byte[] AESencrypt(byte[] key, byte[] src) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(src);
        return encrypted;
    }

    /**
     * 真正的解密过程
     *
     * @param key
     * @param encrypted
     * @return
     * @throws Exception
     */
    public static byte[] AESdecrypt(byte[] key, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }


    public static String getFileName(String name, int chapter, int index) {
        DecimalFormat df1 = new DecimalFormat("0000");
        DecimalFormat df2 = new DecimalFormat("00");
        String result = name + "/[" + name + "][" + df1.format(chapter) + "][" + df2.format(index) + "].jpg";
        return result;
    }


    //url
    private static final String ENCODE = "utf-8";

    public static String getURLDecoderString(String paramString) {
        String str = "";
        if (paramString == null) {
            return "";
        }
        try {
            paramString = URLDecoder.decode(paramString, "utf-8");
            return paramString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getURLEncoderString(String paramString) {
        String str = "";
        if (paramString == null) {
            return "";
        }
        try {
            paramString = URLEncoder.encode(paramString, "utf-8");
            return paramString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getURLEncoderString(String paramString, String cs) {
        String str = "";
        if (paramString == null) {
            return "";
        }
        try {
            paramString = URLEncoder.encode(paramString, cs);
            return paramString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    //md5
    protected static char[] hexDigits = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    private static void appendHexPair(byte paramByte, StringBuffer paramStringBuffer) {
        char c1 = hexDigits[((paramByte & 0xF0) >> 4)];
        char c2 = hexDigits[(paramByte & 0xF)];
        paramStringBuffer.append(c1);
        paramStringBuffer.append(c2);
    }

    private static String bufferToHex(byte[] paramArrayOfByte) {
        return bufferToHex(paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    private static String bufferToHex(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        StringBuffer localStringBuffer = new StringBuffer(paramInt2 * 2);
        int i = paramInt1;
        while (i < paramInt1 + paramInt2) {
            appendHexPair(paramArrayOfByte[i], localStringBuffer);
            i += 1;
        }
        return localStringBuffer.toString();
    }

    public static boolean checkPassword(String paramString1, String paramString2) {
        return getMD5String(paramString1).equals(paramString2);
    }

    /**
     * 返回文件的md5值
     *
     * @param path 要加密的文件的路径
     * @return 文件的md5值
     */
    public static String getFileMD5String(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            //获取MD5加密器
            MessageDigest md = MessageDigest.getInstance("md5");
            //类似读取文件
            byte[] bytes = new byte[10240];//一次读取写入10k
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {//从原目的地读取数据
                //把数据写到md加密器，类比fos.write(bytes, 0, len);
                md.update(bytes, 0, len);
            }
            //读完整个文件数据，并写到md加密器中
            byte[] digest = md.digest();//完成加密，得到md5值，但是是byte类型的。还要做最后的转换
            for (byte b : digest) {//遍历字节，把每个字节拼接起来
                //把每个字节转换成16进制数
                int d = b & 0xff;//只保留后两位数
                String herString = Integer.toHexString(d);//把int类型数据转为16进制字符串表示
                //如果只有一位，则在前面补0.让其也是两位
                if (herString.length() == 1) {//字节高4位为0
                    herString = "0" + herString;//拼接字符串，拼成两位表示
                }
                sb.append(herString);
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 对传递过来的字符串进行md5加密
     *
     * @param str 待加密的字符串
     * @return 字符串Md5加密后的结果
     */
    public static String getMD5String(String str) {
        StringBuilder sb = new StringBuilder();//字符串容器
        try {
            //获取md5加密器.public static MessageDigest getInstance(String algorithm)返回实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes();//把要加密的字符串转换成字节数组
            byte[] digest = md.digest(bytes);//使用指定的 【byte 数组】对摘要进行最后更新，然后完成摘要计算。即完成md5的加密

            for (byte b : digest) {
                //把每个字节转换成16进制数
                int d = b & 0xff;//只保留后两位数
                String herString = Integer.toHexString(d);//把int类型数据转为16进制字符串表示
                //如果只有一位，则在前面补0.让其也是两位
                if (herString.length() == 1) {//字节高4位为0
                    herString = "0" + herString;//拼接字符串，拼成两位表示
                }
                sb.append(herString);
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                // 当前所连接的网络可用
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }


    public static void copy2System(Context context, String cmd) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", cmd);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "复制成功。", Toast.LENGTH_LONG).show();
    }
}
