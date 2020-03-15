package com.example.caight;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;

public abstract class EntityListItemViewBase
{
    private Context mContext;

    public interface OnEntityListItemTouchListener
    {
        boolean onTouch(EntityListItemViewBase sender, MotionEvent e);
    }

    public EntityListItemViewBase(Context context)
    {
        mContext = context;
    }

    public Context getContext()
    {
        return mContext;
    }

    protected EntityListItemViewBase getThisEntity()
    {
        return this;
    }

    @Resolve
    protected void onResolve()
    {
        setGuiComponents();
        setEventListeners();
    }

    protected abstract void setGuiComponents();
    protected abstract void setEventListeners();

    @Expand
    protected abstract void onExpand();

    @Collapse
    protected abstract void onCollapse();
}
