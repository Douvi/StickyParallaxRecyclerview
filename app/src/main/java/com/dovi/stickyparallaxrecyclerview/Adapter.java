package com.dovi.stickyparallaxrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        return 10;
    }

    @Override
    public int numberOfViewHolderBySection(int section) {
        return 9;
    }

    @Override
    public boolean isSectionWillBeShow(int section) {
        return true;
    }

    @Override
    public ViewHolderType getViewHolderType(int section, int positionInSection, int position) {

        if (positionInSection % 3 == 0) {
            return ViewHolderType.PARALLAX;
        }

        return ViewHolderType.NORMAL;
    }

    @Override
    public ViewHolderNormal onCreateViewHolderNormal(ViewGroup viewGroup, int type) {
        View mView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_normal, viewGroup, false);
        return new MyViewHolderNormal(mView);
    }

    @Override
    public void onBindViewHolderNormal(ViewHolderNormal viewHolder, int position, Section section, int positionInSection) {
        viewHolder.bind(section.getSectionId(), positionInSection);
    }

    @Override
    public ViewHolderParallax onCreateViewHolderParallax(ViewGroup viewGroup) {
        View mView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_parallax, viewGroup, false);
        return new MyViewHolderParallax(mView);
    }

    @Override
    public void onBindViewHolderParallax(ViewHolderParallax viewHolder, int position, Section section, int positionInSection) {
        viewHolder.bind(section.getSectionId(), positionInSection);
    }

    @Override
    public ViewHolderSection onCreateViewHolderSection(ViewGroup viewGroup) {
        View mView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_section, viewGroup, false);
        return new MyViewHolderSection(mView);
    }

    @Override
    public void onBindViewHolderSection(ViewHolderSection viewHolder, int position, Section section, int positionInSection) {
        viewHolder.bind(section.getSectionId(), position);
    }


    class MyViewHolderNormal extends ViewHolderNormal {

        TextView mText;

        MyViewHolderNormal(View itemView) {
            super(itemView);

            mText = (TextView)itemView.findViewById(R.id.text);
        }

        public void bind(int section, int position){
            mText.setText("Row Normal | section : "+section + " - position :"+position);
        }
    }

    class MyViewHolderParallax extends ViewHolderParallax {

        TextView mText;

        MyViewHolderParallax(View itemView) {
            super(itemView);
            mText = (TextView)itemView.findViewById(R.id.text);
        }

        public void bind(int section, int position){
            mText.setText("Row Parallax | section : "+section + " - position :"+position);
        }
    }

    class MyViewHolderSection extends ViewHolderSection {

        TextView mText;

        MyViewHolderSection(View itemView) {
            super(itemView);
            mText = (TextView)itemView.findViewById(R.id.text);
        }

        public void bind(int section, int position){
            if (position == 20) {
                mText.setText("Row Section | blablba blkbaiub position blablba blkbaiub position blablba blkbaiub position blablba blkbaiub position :"+position);
            } else {
                mText.setText("Row Section | position :"+position);
            }
        }
    }

}
