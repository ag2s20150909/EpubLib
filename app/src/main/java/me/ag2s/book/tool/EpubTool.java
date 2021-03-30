package me.ag2s.book.tool;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.ag2s.book.viewmodel.TestViewModel;
import me.ag2s.epublib.domain.Author;
import me.ag2s.epublib.domain.Book;
import me.ag2s.epublib.domain.Metadata;
import me.ag2s.epublib.domain.Relator;
import me.ag2s.epublib.domain.Resource;
import me.ag2s.epublib.domain.TOCReference;
import me.ag2s.epublib.epub.EpubReader;
import me.ag2s.epublib.epub.EpubWriter;
import me.ag2s.epublib.util.IOUtil;
import me.ag2s.epublib.util.ResourceUtil;

public class EpubTool {
    private void readEPUB(String name, TestViewModel viewModel) {
        try {
            EpubReader reader = new EpubReader();
            InputStream in = new FileInputStream(new File(name));// getAssets().open(name);

            Book book = reader.readEpub(in);
            Metadata metadata = book.getMetadata();
            String bookInfo = "作者：" + metadata.getAuthors() +
                    "\n出版社：" + metadata.getPublishers() +
                    "\n出版时间：" + metadata.getDates() +
                    "\n书名：" + metadata.getTitles() +
                    "\n简介：" + metadata.getDescriptions() +
                    "\n语言：" + metadata.getLanguage() +
                    "\n";
            StringBuilder ss = new StringBuilder(bookInfo);

            ss.append("EPUB版本:").append(book.getVersion()).append("\n");

            //通过获取线性的阅读菜单
            List<Resource> spineReferences = book.getTableOfContents().getAllUniqueResources();
            for (Resource sp : spineReferences) {
                //Log.v(TAG,sp.getHref()+sp.getTitle());
                ss.append("").append(sp.getHref()).append(sp.getTitle()).append("\n");
            }

            //获取层级的菜单
            List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();
            for (TOCReference top : tocReferences) {
                Resource topres = top.getResource();
                //Log.v(TAG,"父目录"+topres.getHref()+topres.getTitle());
                //ss.append("父目录").append(topres.getHref()).append(topres.getTitle()).append("\n");
                if (top.getChildren().size() > 0) {
                    for (TOCReference child : top.getChildren()) {
                        Resource childres = child.getResource();
                        //Log.v(TAG,"子目录"+childres.getHref()+childres.getTitle());
                        //ss.append("子目录").append(childres.getHref()).append(childres.getTitle()).append("\n");
                    }
                }
            }


            //viewModel.msg.postValue(ss.toString());

        } catch (Exception e) {
            Log.e("APP", e.getLocalizedMessage());
        }
    }

    public static void creteUPUB(Context context, OutputStream outputStream) {
        try {
            String path = "";//this.getExternalFilesDir("book").getPath();
            // Create new Book
            Book book = new Book();
            book.setVersion("2.0");
            Metadata metadata = book.getMetadata();

            // Set the title
            metadata.addTitle("大奉打更人");
            //set language
            metadata.setLanguage("zh-rCH");
            // Add an Author
            metadata.addAuthor(new Author("卖报小郎君"));
            //添加贡献者
            Author aa = new Author("Ag2s Epublib", "v0.1");
            aa.setRelator(Relator.BOOK_PRODUCER);
            metadata.addContributor(aa);
            //设置书籍的主题
            ArrayList<String> subjs = new ArrayList<>();
            subjs.add("穿越");
            subjs.add("轻松");
            subjs.add("阵法");
            subjs.add("仙侠");
            subjs.add("幻想修仙");
            metadata.setSubjects(subjs);


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
            //InputStream inputStream=getAssets().open("test.jpg");
            //book.setCoverImage(new Resource(inputStream,"cover.jpg"));

            // Add Chapter 1
            String txt = "";

            txt = IOUtil.Stream2String(context.getAssets().open("test.txt"));
            book.addSection("第一章",
                    ResourceUtil.createHTMLResource("第一章", txt));
            book.addSection("第二章",
                    ResourceUtil.createHTMLResource("第一章", txt));
            book.addSection("第三章",
                    ResourceUtil.createHTMLResource("第一章", txt));


            // Add css file
            book.getResources().add(new Resource("h1 {color: blue;}p {text-indent:2em;}".getBytes(), "css/style.css"));

            // Add Chapter 2
            TOCReference chapter2 = book.addSection("Second Chapter",
                    ResourceUtil.createHTMLResource("Second Chapter", txt));

            // Add image used by Chapter 2
//            book.getResources().add(
//                    ResourceUtil.createHTMLResource("第一章", txt));

            // Add Chapter2, Section 1
            book.addSection(chapter2, "Chapter 2, section 1",
                    ResourceUtil.createHTMLResource("Chapter 2, section 1", txt));

            // Add Chapter 3
            book.addSection("Conclusion",
                    ResourceUtil.createHTMLResource("Conclusion", txt));

            // Create EpubWriter
            EpubWriter epubWriter = new EpubWriter();

            // Write the Book as Epub
            epubWriter.write(book, outputStream);
            //viewModel.msg.postValue("生成EPUB完成");
        } catch (Exception e) {
            e.printStackTrace();
            // viewModel.msg.postValue(e.getMessage());
        }
    }
}
