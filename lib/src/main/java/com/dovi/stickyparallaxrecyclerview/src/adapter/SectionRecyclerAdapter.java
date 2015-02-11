package com.dovi.stickyparallaxrecyclerview.src.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.dovi.stickyparallaxrecyclerview.src.Section;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderNormal;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderParallax;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderSection;

import java.util.ArrayList;
import java.util.List;


public abstract class SectionRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final float SCROLL_MULTIPLIER = 0.5f;
    private Context mContext;
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

    @Override
    public int getItemCount() {
        getNumberOfSection();

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
            return getViewHolderType(0, 0, position).getValue();
        }

        for(int i = 0; i < sectionList.size(); i++) {
            final Section mSection = sectionList.get(i);

            if (mSection.getStartRow() > position && mSection.isShowSection()) {
                return ViewHolderType.SECTION.getValue();
            } else if (position >= mSection.getStartRow() && position < mSection.getEndRow()) {
                return getViewHolderType(i, position - mSection.getStartRow(), position).getValue();
            }
        }

        return -1;
    }

    public void getNumberOfSection(){
        final int sections = numberOfSection();

        sectionList.clear();
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
        final Section mSection = getHeaderId(position);

        if (viewHolder instanceof ViewHolderParallax) {
            onBindViewHolderParallax((ViewHolderParallax) viewHolder, position, mSection, position - mSection.getStartRow());
            viewHolder.itemView.setTag(-9999, position);
            translateParallaxReset(viewHolder.itemView);
        } else if (viewHolder instanceof ViewHolderSection) {
            onBindViewHolderSection((ViewHolderSection) viewHolder, position, mSection, position - mSection.getStartRow());
            viewHolder.itemView.setTag(-9999, position);
        } else {
            onBindViewHolderNormal((ViewHolderNormal) viewHolder, position, mSection, position - mSection.getStartRow());
            viewHolder.itemView.setTag(-9999, position);
        }


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

    public abstract ViewHolderType getViewHolderType(int section, int positionInSection, int position);

    public abstract ViewHolderNormal onCreateViewHolderNormal(ViewGroup viewGroup, int type);
    public abstract void onBindViewHolderNormal(ViewHolderNormal viewHolder, int position, Section section, int positionInSection);

    public abstract ViewHolderParallax onCreateViewHolderParallax(ViewGroup viewGroup);
    public abstract void onBindViewHolderParallax(ViewHolderParallax viewHolder, int position, Section section, int positionInSection);

    public abstract ViewHolderSection onCreateViewHolderSection(ViewGroup viewGroup);
    public abstract void onBindViewHolderSection(ViewHolderSection viewHolder, int position, Section section, int positionInSection);


    public Section getHeaderId(int position){
        Section mSection = null;
        for (int i = 0; i < getSectionList().size(); i++) {
            mSection = (Section)getSectionList().get(i);
            if (position >= mSection.getHeaderPosition() && position < mSection.getEndRow()) {
                return mSection;
            }
        }
        return null;
    }

    public boolean isHeader(int position) {
        Section mSection;
        for (int i = 0; i < getSectionList().size(); i++) {
            mSection = getSectionList().get(i);
            if (position == mSection.getHeaderPosition()) {
                return true;
            }
        }
        return false;
    }


    public List<Section> getSectionList() {
        return sectionList;
    }

    public Context getContext() {
        return mContext;
    }
}
