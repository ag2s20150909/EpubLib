package me.ag2s.book;


import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.List;


import me.ag2s.book.base.BaseActivity;
import me.ag2s.book.viewmodel.TestViewModel;
import me.ag2s.epublib.domain.Author;
import me.ag2s.epublib.domain.Book;

import me.ag2s.epublib.domain.MediaType;
import me.ag2s.epublib.domain.Metadata;
import me.ag2s.epublib.domain.Resource;

import me.ag2s.epublib.domain.TOCReference;
import me.ag2s.epublib.domain.TableOfContents;
import me.ag2s.epublib.epub.EpubReader;
import me.ag2s.epublib.epub.EpubWriter;

import me.ag2s.epublib.util.IOUtil;
import me.ag2s.epublib.util.ResourceUtil;

public class MainActivity extends BaseActivity {
    TextView tv;
    TestViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        viewModel = new ViewModelProvider(this).get(TestViewModel.class);
        viewModel.msg.observe(this, msg -> {
            tv.setText(msg);
            //tv.setText(Html.fromHtml(msg));
        });

    }


    private void readEPUB(String name) {
        try {
            EpubReader reader = new EpubReader();
            InputStream in = getAssets().open(name);
            Book book = reader.readEpub(in);
            Metadata metadata = book.getMetadata();
            String bookInfo = "作者："+metadata.getAuthors()+
                    "\n出版社："+metadata.getPublishers()+
                    "\n出版时间：" +metadata.getDates()+
                    "\n书名："+metadata.getTitles()+
                    "\n简介："+metadata.getDescriptions()+
                    "\n语言："+metadata.getLanguage()+
                    "：";
            StringBuilder ss = new StringBuilder(bookInfo);

            //String ss="";
            //获取到书本的目录资源
            TableOfContents tableOfContents = book.getTableOfContents();
            System.out.println("目录资源数量为：" + tableOfContents.size());
            //获取到目录对应的资源数据
            List<TOCReference> tocReferences = tableOfContents.getTocReferences();
            for (TOCReference tocReference : tocReferences) {
                Resource resource = tocReference.getResource();
                //data就是资源的内容数据，可能是css,html,图片等等
                byte[] data = resource.getData();
                // 获取到内容的类型  css,html,还是图片
                ss.append(resource.getHref()).append(tocReference.getTitle()).append("\n");
                MediaType mediaType = resource.getMediaType();
                if (tocReference.getChildren().size() > 0) {
                    //子目录
                   for(TOCReference r:tocReference.getChildren()) {
                       ss.append(r.getResource().getHref()).append(r.getTitle()).append("\n");
                   }
                }
            }

            viewModel.msg.postValue(ss.toString());

        } catch (Exception e) {
            Log.e("APP", e.getLocalizedMessage());
        }
    }

    public void readEPUB2(View view) {
        readEPUB("epub2.epub");
    }

    public void readEPUB3(View view) {
        readEPUB("epub3.epub");
    }


    public void createEPUB(View view) {
        try {
            String path = this.getExternalFilesDir("book").getPath();
            // Create new Book
            Book book = new Book();
            Metadata metadata = book.getMetadata();

            // Set the title

            metadata.addTitle("大奉打更人");

            // Add an Author
            metadata.addAuthor(new Author("卖报小郎君"));

            metadata.addType("仙侠");
            metadata.addType("幻想修仙");
            metadata.addPublisher("Ag2SEpubLib");
            metadata.addDescription("这个世界，有儒；有道；有佛；有妖；有术士。\n" +
                    "警校毕业的许七安幽幽醒来，发现自己身处牢狱之中，三日后流放边陲.....\n" +
                    "他起初的目的只是自保，顺便在这个没有人权的社会里当个富家翁悠闲度日。\n" +
                    "......\n" +
                    "多年后，许七安回首前尘，身后是早已逝去的敌人，以及累累白骨。\n" +
                    "滚滚长江东逝水，浪花淘尽英雄，是非成败转头空。\n" +
                    "青山依旧在，几度夕阳红。");
//
//             Set cover image
            InputStream inputStream=getAssets().open("test.jpg");
            book.setCoverImage(new Resource(inputStream,"cover.jpg"));

            // Add Chapter 1
            String txt = "";
            txt = IOUtil.Stream2String(getAssets().open("test.txt"));
            book.addSection("第一章",
                    ResourceUtil.createHTMLResource("第一章", txt));
            book.addSection("第二章",
                    ResourceUtil.createHTMLResource("第一章", txt));
            book.addSection("第三章",
                    ResourceUtil.createHTMLResource("第一章", txt));


            // Add css file
            book.getResources().add(new Resource("h1 {color: blue;}p {text-indent:2em;}".getBytes(), "css/style.css"));
//
//            // Add Chapter 2
//            TOCReference chapter2 = book.addSection("Second Chapter",
//                    getResource("/book1/chapter2.html", "chapter2.html"));
//
//            // Add image used by Chapter 2
//            book.getResources().add(
//                    getResource("/book1/flowers_320x240.jpg", "flowers.jpg"));
//
//            // Add Chapter2, Section 1
//            book.addSection(chapter2, "Chapter 2, section 1",
//                    getResource("/book1/chapter2_1.html", "chapter2_1.html"));
//
//            // Add Chapter 3
//            book.addSection("Conclusion",
//                    getResource("/book1/chapter3.html", "chapter3.html"));

            // Create EpubWriter
            EpubWriter epubWriter = new EpubWriter();

            // Write the Book as Epub
            epubWriter.write(book, new FileOutputStream(path + "/test.epub"));
            viewModel.msg.postValue("生成EPUB完成");
        } catch (Exception e) {
            e.printStackTrace();
            viewModel.msg.postValue(e.getMessage());
        }
    }


}