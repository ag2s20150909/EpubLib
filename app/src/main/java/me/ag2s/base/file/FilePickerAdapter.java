package me.ag2s.base.file;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.ag2s.base.APP;
import me.ag2s.book.R;

public class FilePickerAdapter extends BaseAdapter {
    private List<CachingDocumentFile> mData;

    public FilePickerAdapter(List<CachingDocumentFile> data) {
        this.mData = data;
    }

    public void update(List<CachingDocumentFile> cachingDocumentFiles) {
        this.mData = cachingDocumentFiles;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CachingDocumentFile data = mData.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(APP.getContext()).inflate(
                    R.layout.file_picker_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvFileName = convertView.findViewById(R.id.tv_file_name);
            viewHolder.ivFileType = convertView.findViewById(R.id.iv_file_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvFileName.setText(data.getName());
        if (data.isDirectory()) {
            viewHolder.ivFileType.setImageResource(R.drawable.ic_dir);
        } else {
            viewHolder.ivFileType.setImageResource(R.drawable.ic_file);
        }

        return convertView;
    }


    public class ViewHolder {
        TextView tvFileName;
        ImageView ivFileType;
    }
}
