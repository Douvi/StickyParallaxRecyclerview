package com.dovi.stickyparallaxrecyclerview.src.decoration;

import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.dovi.stickyparallaxrecyclerview.src.Section;
import com.dovi.stickyparallaxrecyclerview.src.adapter.ParallaxRecyclerAdapter;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderSection;

public class ViewHolderSectionDecoration extends RecyclerView.ItemDecoration {

    protected int headerHeight = 0;

    protected ParallaxRecyclerAdapter mAdapter;
    protected int orientation = -1;
    protected final LongSparseArray<View> mHeaderViews = new LongSparseArray<View>();
    protected  LinearLayoutManager layoutManager;

    public ViewHolderSectionDecoration(ParallaxRecyclerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        getOrientation(parent);

        if (parent.getChildCount() > 0 && mAdapter.getItemCount() > 0) {

            int positionTop = layoutManager.findFirstVisibleItemPosition();

            final Section section = getHeader(positionTop);

            if (section.getHeaderPosition() >= 0 && section.isShowSection()) {
                View firstHeader = getHeaderView(parent, section);

                RecyclerView.ViewHolder nextViewHolder = getNextView(parent, positionTop);

//                switch (curentScroll) {
//                    case UP:{
//                        do {
//                            nextViewHolder = getNextView(parent, positionTop);
//                            positionTop++;
//                        } while (nextViewHolder.itemView.getHeight() + nextViewHolder.itemView.getTop() < firstHeader.getHeight() + firstHeader.getTranslationY());
//                        break;
//                    }
//                    case DOWN:
//                    {
//                        do {
//                            nextViewHolder = getNextView(parent, positionTop);
//                            positionTop++;
//                        } while (nextViewHolder.itemView.getHeight() + nextViewHolder.itemView.getTop() > firstHeader.getHeight() + firstHeader.getTranslationY());
//                        break;
//                    }
//                }


                int translationX = parent.getScrollX();
                int translationY = parent.getScrollY();

                if (nextViewHolder instanceof ViewHolderSection) {
                    final View secondHeader = nextViewHolder.itemView;

                    if (orientation == LinearLayoutManager.VERTICAL && (secondHeader.getTop() - (firstHeader.getTop() + firstHeader.getHeight()) <= 0)) {
                        translationY -= (firstHeader.getHeight() - secondHeader.getTop());
                    } else if (orientation == LinearLayoutManager.HORIZONTAL && (secondHeader.getLeft()- (firstHeader.getLeft() + firstHeader.getWidth()) <= 0)){
                        translationX -= (firstHeader.getWidth() - secondHeader.getLeft());
                    }
                }

                canvas.save();
                canvas.translate(translationX, translationY);
                firstHeader.draw(canvas);
                firstHeader.setTranslationY(translationY);

                canvas.restore();

                mHeaderViews.remove(section.getHeaderPosition());
                mHeaderViews.put(section.getHeaderPosition(), firstHeader);
            }
        }
    }

    /**
     * Returns the first item currently in the recyclerview that's not obscured by a header.
     * @param parent
     * @return
     */
    protected RecyclerView.ViewHolder getNextView(RecyclerView parent, int position) {
        return parent.findViewHolderForPosition(position + 1);
    }

    protected int getOrientation(RecyclerView parent) {

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



    /**
     * Gets the header view for the associated position.  If it doesn't exist yet, it will be
     * created, measured, and laid out.
     * @param parent
     * @param section
     * @return Header view
     */
    public View getHeaderView(RecyclerView parent, Section section) {

        View header = mHeaderViews.get(section.getHeaderPosition());


        if (header == null) {
            //TODO - recycle views
            ViewHolderSection viewHolder = mAdapter.onCreateViewHolderSection(parent);
            mAdapter.onBindViewHolderSection(viewHolder, section.getHeaderPosition(), section, -1);
            header = viewHolder.itemView;

            if (header.getLayoutParams() == null) {
                header.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            int widthSpec;
            int heightSpec;

            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);
            } else {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.UNSPECIFIED);
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.EXACTLY);
            }

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);
            header.measure(childWidth, childHeight);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
            mHeaderViews.put(section.getHeaderPosition(), header);
        }
        return header;
    }

    protected boolean hasNewHeader(int position) {
        Section mSection = null;
        for (int i = 0; i < mAdapter.getSectionList().size(); i++) {
            mSection = (Section)mAdapter.getSectionList().get(i);
            if (position == mSection.getHeaderPosition()) {
                return true;
            }
        }
        return false;
    }

    protected Section getHeader(int position){
        Section mSection = null;
        for (int i = 0; i < mAdapter.getSectionList().size(); i++) {
            mSection = (Section)mAdapter.getSectionList().get(i);
//            position >= mSection.getHeaderPosition() &&
            if (position < mSection.getEndRow()) {
                break;
            }
            mSection = null;
        }
        return mSection;
    }

    /**
     * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
     * calling this method.
     */
    public void invalidateHeaders() {
        mHeaderViews.clear();
    }
}