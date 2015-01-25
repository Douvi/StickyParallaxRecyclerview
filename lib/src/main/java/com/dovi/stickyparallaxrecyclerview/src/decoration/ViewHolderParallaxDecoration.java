package com.dovi.stickyparallaxrecyclerview.src.decoration;

import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.dovi.stickyparallaxrecyclerview.src.Section;
import com.dovi.stickyparallaxrecyclerview.src.adapter.ParallaxRecyclerAdapter;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderParallax;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderSection;


public class ViewHolderParallaxDecoration extends RecyclerView.ItemDecoration {

    private final float SCROLL_MULTIPLIER = 0.5f;
    private ParallaxRecyclerAdapter mAdapter;
    private int headerHeight = 0;
    private int orientation = -1;
    private LinearLayoutManager layoutManager;
    private int currentPosition;

    public ViewHolderParallaxDecoration(ParallaxRecyclerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        getOrientation(parent);

        onDrawParallax(parent, layoutManager.findFirstVisibleItemPosition(), true);
    }

    private void onDrawParallax(RecyclerView parent, int position, boolean goDeeper){

        Section section = getHeader(position);
        final RecyclerView.ViewHolder viewHolder = parent.findViewHolderForPosition(position);

        if (currentPosition != -1 && currentPosition != position) {
            final RecyclerView.ViewHolder holder = parent.findViewHolderForPosition(currentPosition);
            if (holder != null && holder instanceof ViewHolderParallax) {
               translateParallaxReset(holder.itemView);
            }
        }

        currentPosition = position;

        if (viewHolder instanceof ViewHolderParallax) {
            translateParallax(parent, viewHolder.itemView, getCorrectHeight(parent, viewHolder.itemView, section.isShowSection()));
        } else if (goDeeper && viewHolder instanceof ViewHolderSection && goDeeper) {
            headerHeight = viewHolder.itemView.getHeight();
            onDrawParallax(parent, position+1, false);
        }
    }

    public void translateParallax(RecyclerView parent, View view, float position) {
        float ofCalculated = position * SCROLL_MULTIPLIER;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setTranslationY(ofCalculated);
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, 0, ofCalculated, ofCalculated);
            anim.setFillAfter(true);
            anim.setDuration(0);
            view.startAnimation(anim);
        }

    }

    public void translateParallaxReset(View view) {
        float of = 0;
        float ofCalculated = of * SCROLL_MULTIPLIER;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setTranslationY(ofCalculated);
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, 0, ofCalculated, ofCalculated);
            anim.setFillAfter(true);
            anim.setDuration(0);
            view.startAnimation(anim);
        }
    }

    private int getOrientation(RecyclerView parent) {

        if (orientation != -1) {
            return orientation;
        }

        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            orientation = layoutManager.getOrientation();
            return orientation;
        } else {
            throw new IllegalStateException("StickyListHeadersDecoration can only be used with a " + "LinearLayoutManager.");
        }
    }

    private Section getHeader(int position){
        Section mSection = null;
        for (int i = 0; i < mAdapter.getSectionList().size(); i++) {
            mSection = (Section)mAdapter.getSectionList().get(i);
            if (position >= mSection.getHeaderPosition() && position < mSection.getEndRow()) {
                return mSection;
            }
        }
        return null;
    }

    private int getCorrectHeight(RecyclerView parent, View view, boolean isSection) {
        if (isSection) {

            if (view.getTop() >= headerHeight) {
                return 0;
            } else {
                return -(view.getTop() - headerHeight);
            }

        } else {

            if (view.getTop() >= 0) {
                return 0;
            } else {
                return -view.getTop();
            }
        }
    }
}