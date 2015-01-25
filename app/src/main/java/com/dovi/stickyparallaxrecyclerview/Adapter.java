package com.dovi.stickyparallaxrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dovi.stickyparallaxrecyclerview.sample.R;
import com.dovi.stickyparallaxrecyclerview.src.Section;
import com.dovi.stickyparallaxrecyclerview.src.adapter.ParallaxRecyclerAdapter;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderNormal;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderParallax;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderSection;

import java.util.List;

public class Adapter extends ParallaxRecyclerAdapter<RecyclerView.ViewHolder> {


    public Adapter(RecyclerView recyclerView, List<RecyclerView.ViewHolder> data) {
        super(recyclerView, data);
    }

    @Override
    public int numberOfSection() {
        return 20;
    }

    @Override
    public int numberOfViewHolderBySection(int section) {
        return 10;
    }

    @Override
    public boolean isSectionWillBeShow(int section) {
        return true;
    }

    @Override
    public ViewHolderType getViewHolderType(int section, int position) {
        
        if (position % 3 == 0) {
            return ViewHolderType.PARALLAX;
        }

        return ViewHolderType.NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolderNormal(ViewGroup viewGroup, int type) {
        View mView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_normal, viewGroup, false);
        return new ViewHolderNormal(mView);
    }

    @Override
    public void onBindViewHolderNormal(RecyclerView.ViewHolder viewHolder, int position, Section section) {

    }

    @Override
    public ViewHolderParallax onCreateViewHolderParallax(ViewGroup viewGroup) {
        View mView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_parallax, viewGroup, false);
        return new ViewHolderParallax(mView);
    }

    @Override
    public void onBindViewHolderParallax(ViewHolderParallax viewHolder, int position, Section section) {

    }

    @Override
    public ViewHolderSection onCreateViewHolderSection(ViewGroup viewGroup) {
        View mView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_section, viewGroup, false);
        return new ViewHolderSection(mView);
    }

    @Override
    public void onBindViewHolderSection(ViewHolderSection viewHolder, int position, Section section) {

    }
}
