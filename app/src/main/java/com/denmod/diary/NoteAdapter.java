package com.denmod.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int GROUP_TYPE = 0;
    public static final int NOTE_TYPE = 1;

    private LayoutInflater inflater;
    private List<Element> elements;

    NoteAdapter(Context context, List<Group> groups) {
        inflater = LayoutInflater.from(context);
        elements = (List<Element>)(Object)groups;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == GROUP_TYPE)
            return new GroupViewHolder(inflater.inflate(R.layout.group, parent, false));
        else
            return new NoteViewHolder(inflater.inflate(R.layout.note, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (elements.get(position).getClass() == Group.class)
            return GROUP_TYPE;
        else
            return NOTE_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == GROUP_TYPE)
            ((GroupViewHolder)holder).bind(this, position);
        else
            ((NoteViewHolder)holder).bind(this, position);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public List<Element> getItems() {
        return elements;
    }
}
