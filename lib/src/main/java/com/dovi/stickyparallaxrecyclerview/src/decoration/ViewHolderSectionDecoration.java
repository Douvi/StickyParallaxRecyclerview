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

    private int headerHeight = 0;
    private int currentPosition;

    private ParallaxRecyclerAdapter mAdapter;
    private int orientation = -1;
    private final LongSparseArray<View> mHeaderViews = new LongSparseArray<View>();
    private  LinearLayoutManager layoutManager;

    public ViewHolderSectionDecoration(ParallaxRecyclerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }


    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        getOrientation(parent);

        if (parent.getChildCount() > 0 && mAdapter.getItemCount() > 0) {

            final int positionTop = layoutManager.findFirstVisibleItemPosition();
            final Section section = getHeader(positionTop);

            if (section.getHeaderPosition() >= 0 && section.isShowSection()) {
                View firstHeader = getHeaderView(parent, positionTop);

                RecyclerView.ViewHolder nextViewHolder = getNextView(parent, positionTop);

                int translationX = parent.getScrollX();
                int translationY = parent.getScrollY();

                if (nextViewHolder instanceof ViewHolderSection) {
                    final View secondeHeader = nextViewHolder.itemView;

                    if (orientation == LinearLayoutManager.VERTICAL && (secondeHeader.getTop() - (firstHeader.getTop() + firstHeader.getHeight()) <= 0)) {
                        translationY -= (firstHeader.getHeight() - secondeHeader.getTop());
                    } else if (orientation == LinearLayoutManager.HORIZONTAL && (secondeHeader.getLeft()- (firstHeader.getLeft() + firstHeader.getWidth()) <= 0)){
                        translationX -= (firstHeader.getWidth() - secondeHeader.getLeft());
                    }
                }

                canvas.save();
                canvas.translate(translationX, translationY);
                firstHeader.draw(canvas);

                canvas.restore();
            }
        }
    }

    /**
     * Returns the first item currently in the recyclerview that's not obscured by a header.
     * @param parent
     * @return
     */
    private RecyclerView.ViewHolder getNextView(RecyclerView parent, int position) {
        return parent.findViewHolderForPosition(position + 1);
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



    /**
     * Gets the header view for the associated position.  If it doesn't exist yet, it will be
     * created, measured, and laid out.
     * @param parent
     * @param position
     * @return Header view
     */
    public View getHeaderView(RecyclerView parent, int position) {
        Section section = getHeader(position);
        View header = mHeaderViews.get(section.getHeaderPosition());


        if (header == null) {
            //TODO - recycle views
            ViewHolderSection viewHolder = mAdapter.onCreateViewHolderSection(parent);
            mAdapter.onBindViewHolderSection(viewHolder, position, section);
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

    private boolean hasNewHeader(int position) {
        Section mSection = null;
        for (int i = 0; i < mAdapter.getSectionList().size(); i++) {
            mSection = (Section)mAdapter.getSectionList().get(i);
            if (position == mSection.getHeaderPosition()) {
                return true;
            }
        }
        return false;
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

    /**
     * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
     * calling this method.
     */
    public void invalidateHeaders() {
        mHeaderViews.clear();
    }
}