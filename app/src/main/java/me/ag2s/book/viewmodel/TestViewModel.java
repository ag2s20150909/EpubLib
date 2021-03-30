package me.ag2s.book.viewmodel;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import me.ag2s.book.tool.TextBook;

public class TestViewModel extends ViewModel {
    public MutableLiveData<String> msg = new MutableLiveData<>();
    public MutableLiveData<Uri> uri = new MutableLiveData<>();

    public TextBook getTbook() {
        return tbook.getValue();
    }

    public void setTbook(MutableLiveData<TextBook> tbook) {
        this.tbook = tbook;
    }

    public MutableLiveData<TextBook> tbook = new MutableLiveData<>();
}
