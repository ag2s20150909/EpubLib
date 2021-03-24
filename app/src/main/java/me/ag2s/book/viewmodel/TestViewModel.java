package me.ag2s.book.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TestViewModel extends ViewModel {
    public MutableLiveData<String> msg=new MutableLiveData<>();
}
