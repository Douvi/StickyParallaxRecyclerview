package com.dovi.stickyparallaxrecyclerview.src.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Doudou on 2015-01-25.
 */
public abstract class ViewHolderNormal extends RecyclerView.ViewHolder {

    public ViewHolderNormal(View itemView) {
        super(itemView);
    }

    public abstract void bind(int section, int position);

}
