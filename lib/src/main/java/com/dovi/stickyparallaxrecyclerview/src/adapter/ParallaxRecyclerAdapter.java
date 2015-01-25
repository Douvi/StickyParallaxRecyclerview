package com.dovi.stickyparallaxrecyclerview.src.adapter;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.dovi.stickyparallaxrecyclerview.src.Section;
import com.dovi.stickyparallaxrecyclerview.src.decoration.ViewHolderParallaxDecoration;
import com.dovi.stickyparallaxrecyclerview.src.decoration.ViewHolderSectionDecoration;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderParallax;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderSection;

import java.util.ArrayList;
import java.util.List;

public abstract class ParallaxRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final float SCROLL_MULTIPLIER = 0.5f;

    private final RecyclerView mRecyclerView;
    private final LinearLayoutManager mLayoutManager;
    private List<T> mData;
    private int currentPosition = -1;
    private final List<Section> sectionList = new ArrayList<Section>();



    public static class ViewHolderType {
        public static final ViewHolderType PARALLAX = new ViewHolderType(0);
        public static final ViewHolderType SECTION = new ViewHolderType(1);
        public static final ViewHolderType NORMAL = new ViewHolderType(2);

        protected int value;
        public ViewHolderType(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    protected ParallaxRecyclerAdapter(RecyclerView recyclerView, List<T> data) {
        this.mRecyclerView = recyclerView;
        this.mData = data;
        this.mLayoutManager = (LinearLayoutManager)mRecyclerView.getLayoutManager();

        mRecyclerView.addItemDecoration(new ViewHolderParallaxDecoration(this));
        mRecyclerView.addItemDecoration(new ViewHolderSectionDecoration(this));

        getNumberOfSection();
    }

    @Override
    public int getItemCount() {
        if (sectionList.size() > 0 ) {
            int totalRow = 0;

            for(int i = 0; i < sectionList.size(); i++) {

                final Section mSection = sectionList.get(i);
                final int totalRowInSection = numberOfViewHolderBySection(i);

                mSection.setSectionId(i);
                mSection.setShowSection(isSectionWillBeShow(i));
                if (mSection.isShowSection()) {
                    mSection.setHeaderPosition(totalRow);
                    totalRow++;
                }
                mSection.setStartRow(totalRow);
                mSection.setNumberOfRow(totalRowInSection);


                totalRow += totalRowInSection;
            }

            return totalRow;

        } else {
            return numberOfViewHolderBySection(0);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (sectionList.size() == 0 ) {
            return getViewHolderType(0, position).getValue();
        }

        for(int i = 0; i < sectionList.size(); i++) {
            final Section mSection = sectionList.get(i);

            if (mSection.getStartRow() > position && mSection.isShowSection()) {
                return ViewHolderType.SECTION.getValue();
            } else if (position >= mSection.getStartRow() && position < mSection.getEndRow()) {
                return getViewHolderType(i, position - mSection.getStartRow()).getValue();
            }
        }

        return -1;
    }

    public void getNumberOfSection(){
        final int sections = numberOfSection();

        for(int i = 0; i < sections; i++) {
            sectionList.add(new Section());
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        final ViewHolderType mType = new ViewHolderType(type);

        if (ViewHolderType.PARALLAX.getValue() == mType.getValue()) {
            return onCreateViewHolderParallax(viewGroup);
        } else if (ViewHolderType.SECTION.getValue() == mType.getValue()){
            return onCreateViewHolderSection(viewGroup);
        } else {
            return onCreateViewHolderNormal(viewGroup, type);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ViewHolderParallax) {
            onBindViewHolderParallax((ViewHolderParallax) viewHolder, position, getHeaderId(position));
            viewHolder.itemView.setTag(-9999, position);
        } else if (viewHolder instanceof ViewHolderSection) {
            onBindViewHolderSection((ViewHolderSection) viewHolder, position, getHeaderId(position));
            viewHolder.itemView.setTag(-9999, position);
        } else {
            onBindViewHolderNormal(viewHolder, position, getHeaderId(position));
            viewHolder.itemView.setTag(-9999, position);
        }

        translateParallaxReset(viewHolder.itemView);
    }

    public void translateParallaxReset(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setTranslationY(0);
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, 0, 0, 0);
            anim.setFillAfter(true);
            anim.setDuration(0);
            view.startAnimation(anim);
        }
    }

    public abstract int numberOfSection();

    public abstract int numberOfViewHolderBySection(int section);

    public abstract boolean isSectionWillBeShow(int section);

    public abstract ViewHolderType getViewHolderType(int section, int position);

    public abstract RecyclerView.ViewHolder onCreateViewHolderNormal(ViewGroup viewGroup, int type);
    public abstract void onBindViewHolderNormal(RecyclerView.ViewHolder viewHolder, int position, Section section);

    public abstract ViewHolderParallax onCreateViewHolderParallax(ViewGroup viewGroup);
    public abstract void onBindViewHolderParallax(ViewHolderParallax viewHolder, int position, Section section);

    public abstract ViewHolderSection onCreateViewHolderSection(ViewGroup viewGroup);
    public abstract void onBindViewHolderSection(ViewHolderSection viewHolder, int position, Section section);





    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addItem(T item, int position) {
        mData.add(position, item);
        notifyItemInserted(position);
        notifyItemInserted(position + 1);
    }

    public void removeItem(T item) {
        int position = mData.indexOf(item);
        if (position < 0)
            return;
        mData.remove(item);
        notifyItemRemoved(position + 1);
        notifyItemRemoved(position);
    }

    private Section getHeaderId(int position){
        Section mSection = null;
        for (int i = 0; i < getSectionList().size(); i++) {
            mSection = (Section)getSectionList().get(i);
            if (position >= mSection.getHeaderPosition() && position < mSection.getEndRow()) {
                return mSection;
            }
        }
        return null;
    }


    public List<Section> getSectionList() {
        return sectionList;
    }
}
