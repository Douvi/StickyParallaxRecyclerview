package com.dovi.stickyparallaxrecyclerview.src.decoration;

import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dovi.stickyparallaxrecyclerview.src.Section;
import com.dovi.stickyparallaxrecyclerview.src.adapter.ParallaxRecyclerAdapter;
import com.dovi.stickyparallaxrecyclerview.src.holder.ViewHolderSection;

public class ViewHolderSectionDecoration extends RecyclerView.ItemDecoration {

    enum SCROLL_TYPE{
        DOWN, UP;
    }

    protected ParallaxRecyclerAdapter mAdapter;
    protected int orientation = -1;
    protected final LongSparseArray<View> mHeaderViews = new LongSparseArray<View>();
    protected LinearLayoutManager layoutManager;
    private SCROLL_TYPE curentScroll = SCROLL_TYPE.UP;
    private String TAG = ViewHolderSectionDecoration.class.getSimpleName();
    private float initialX, initialY;

    public ViewHolderSectionDecoration(ParallaxRecyclerAdapter mAdapter, RecyclerView parent) {
        this.mAdapter = mAdapter;

        parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getActionMasked();

                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        initialY = event.getY();

                        Log.d(TAG, "Action was DOWN");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float finalY = event.getY();

                        if (initialY < finalY) {
                            curentScroll = SCROLL_TYPE.DOWN;
                        }

                        if (initialY > finalY) {
                            Log.d(TAG, "Down to Up swipe performed");
                            curentScroll = SCROLL_TYPE.UP;
                        }

                        initialX = event.getX();
                        initialY = event.getY();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG,"Action was CANCEL");
                        break;

                }


                return false;
            }
        });
    }


    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        getOrientation(parent);

        if (parent.getChildCount() > 0 && mAdapter.getItemCount() > 0) {

            int positionTop = layoutManager.findFirstVisibleItemPosition();
            Section section = getHeader(positionTop);
            RecyclerView.ViewHolder nextViewHolder = getNextView(parent, positionTop);

            if (section.getHeaderPosition() >= 0 && section.isShowSection()) {

                boolean isNewSection = false;

                View firstHeader = getHeaderView(parent, section);

                switch (curentScroll) {
                    case DOWN:
                    {

                        if (!(nextViewHolder instanceof ViewHolderSection)) {

                            if (section.getHeaderPosition() == 0) {
                                nextViewHolder = getNextView(parent, 0);
                            } else if (getNextView(parent, getHeader(section.getEndRow()+1).getHeaderPosition()) != null){
                                nextViewHolder = getNextView(parent, getHeader(section.getEndRow()+1).getHeaderPosition());
                            }
                        }

                        if (nextViewHolder == null) {
                            Log.i("", "");
                        }

                        if ((nextViewHolder != null && orientation == LinearLayoutManager.VERTICAL && nextViewHolder.itemView.getY() >= 0 && nextViewHolder.itemView.getY() < firstHeader.getHeight()) || (nextViewHolder != null && orientation == LinearLayoutManager.HORIZONTAL && nextViewHolder.itemView.getX() >= 0 && nextViewHolder.itemView.getX() < firstHeader.getWidth())) {
                            isNewSection = true;
                            break;
                        } else {
                            isNewSection = false;
                        }
                    }
                    case UP:
                        do {
                            positionTop++;
                            nextViewHolder = getNextView(parent, positionTop);
                        } while (nextViewHolder.itemView.getHeight() + nextViewHolder.itemView.getTop() < firstHeader.getHeight() + firstHeader.getTranslationY());
                        break;
                }

                int translationX = parent.getScrollX();
                int translationY = parent.getScrollY();

                if (nextViewHolder instanceof ViewHolderSection) {

                    if (isNewSection) {

                        if (orientation == LinearLayoutManager.VERTICAL) {
                            translationY =+ ((int)nextViewHolder.itemView.getY() - firstHeader.getHeight());
                        } else if (orientation == LinearLayoutManager.HORIZONTAL){
                            translationX =+ (int)nextViewHolder.itemView.getX();
                        }

                    } else {
                        final View secondHeader = nextViewHolder.itemView;

                        if (orientation == LinearLayoutManager.VERTICAL && (secondHeader.getTop() - (firstHeader.getTop() + firstHeader.getHeight()) <= 0)) {
                            translationY -= (firstHeader.getHeight() - secondHeader.getTop());
                        } else if (orientation == LinearLayoutManager.HORIZONTAL && (secondHeader.getLeft()- (firstHeader.getLeft() + firstHeader.getWidth()) <= 0)){
                            translationX -= (firstHeader.getWidth() - secondHeader.getLeft());
                        }
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
        return parent.findViewHolderForPosition(position);
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

            RelativeLayout mRelativeLayout = new RelativeLayout(parent.getContext());

            RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            if (header.getLayoutParams() != null) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) header.getLayoutParams();
                mLayoutParams.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin);
            }

            header.setLayoutParams(mLayoutParams);

            int widthSpec;
            int heightSpec;

            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);
            } else {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.UNSPECIFIED);
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.EXACTLY);
            }

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) header.getLayoutParams();
            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);

            mRelativeLayout.addView(header);
            mRelativeLayout.measure(childWidth, childHeight);
            mRelativeLayout.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());

            return mRelativeLayout;
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