package me.ag2s.base.file;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;
import java.util.List;

public class FilePickerViewModel extends AndroidViewModel {
    private static final String TAG = FilePickerViewModel.class.getName();

    public FilePickerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<CachingDocumentFile>> documents = new MutableLiveData<>();
    public MutableLiveData<CachingDocumentFile> openDirectory = new MutableLiveData<>();
    public MutableLiveData<CachingDocumentFile> openDocument = new MutableLiveData<>();
    //private MutableLiveData<Event<CachingDocumentFile>> openDirectory;
    // val openDirectory = _openDirectory


    //val openDocument = _openDocument

    public void loadDirectory(Uri directoryUri) {
        Log.d(TAG, directoryUri.toString());
        DocumentFile documentsTree = DocumentFile.fromTreeUri(getApplication(), directoryUri);
        if (documentsTree == null) {
            return;
        }
        Log.d(TAG, String.valueOf(documentsTree.listFiles().length));
        List<CachingDocumentFile> childDocuments = CachingDocumentFile.toCachingList(Arrays.asList(documentsTree.listFiles()));

        documents.postValue(childDocuments);

    }

    public void documentClicked(CachingDocumentFile clickedDocument) {
        if (clickedDocument.isDirectory()) {
            openDirectory.postValue(clickedDocument);
        } else {
            openDocument.postValue(clickedDocument);
        }
    }


}
