package com.example.caight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;
import com.mindorks.placeholderview.annotations.expand.SingleTop;

@Parent
@SingleTop
@Layout(R.layout.view_cat_group_view)
public class CatGroupView extends EntityListItemViewBase
{
    private final AnimatedVectorDrawable ExpandAnimation = (AnimatedVectorDrawable)ContextCompat.getDrawable(getContext(), R.drawable.ic_anim_expand);
    private final AnimatedVectorDrawable CollapseAnimation = (AnimatedVectorDrawable)ContextCompat.getDrawable(getContext(), R.drawable.ic_anim_collapse);

    private CatGroup group = null;

    @View(R.id.rootLayout)
    private ConstraintLayout rootLayout;
    @View(R.id.arrImageView)
    private ImageView arrImageView;
    @View(R.id.groupTextView)
    private TextView groupTextView;
    @View(R.id.idTextView)
    private TextView idTextView;
    @View(R.id.toggleLayout)
    private LinearLayout toggleLayout;
    @View(R.id.deleteImageButton)
    private ImageButton deleteImageButton;
    @View(R.id.editButton)
    private ImageButton editButton;
    @View(R.id.qrButton)
    private ImageButton qrButton;

    @ParentPosition
    private int mParentPosition;

    private OnEntityListItemTouchListener onTouchListener = null;
    private OnEntityListItemTouchListener onDeleteListener = null;
    private OnEntityListItemTouchListener onEditListener = null;
    private OnEntityListItemTouchListener onShowQrListener = null;

    public CatGroupView(Context context, CatGroup group)
    {
        super(context);
        setGroup(group);
    }

    public CatGroup getGroup()
    {
        return group;
    }

    public void setGroup(CatGroup group)
    {
        this.group = group;
    }

    public void setOnTouchListener(OnEntityListItemTouchListener l)
    {
        this.onTouchListener = l;
    }

    public void setOnDeleteListener(OnEntityListItemTouchListener l)
    {
        this.onDeleteListener = l;
    }

    public void setOnEditListener(OnEntityListItemTouchListener l)
    {
        this.onEditListener = l;
    }

    public void setOnShowQrListener(OnEntityListItemTouchListener l)
    {
        this.onShowQrListener = l;
    }

    @Override
    protected void setGuiComponents()
    {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setEventListeners()
    {
        rootLayout.setOnTouchListener(new android.view.View.OnTouchListener()
        {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event)
            {
                if (onTouchListener != null)
                {
                    return onTouchListener.onTouch(getThisEntity(), event);
                }
                return false;
            }
        });

        deleteImageButton.setOnTouchListener(new android.view.View.OnTouchListener()
        {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event)
            {
                if (onDeleteListener != null)
                {
                    return onDeleteListener.onTouch(getThisEntity(), event);
                }
                return false;
            }
        });

        editButton.setOnTouchListener(new android.view.View.OnTouchListener()
        {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event)
            {
                if (onEditListener != null)
                {
                    return onEditListener.onTouch(getThisEntity(), event);
                }
                return false;
            }
        });

        qrButton.setOnTouchListener(new android.view.View.OnTouchListener()
        {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event)
            {
                if (onShowQrListener != null)
                {
                    return onShowQrListener.onTouch(getThisEntity(), event);
                }
                return false;
            }
        });
    }

    @Override
    @Resolve
    protected void onResolve()
    {
        super.onResolve();

        groupTextView.setText(group.getName());
        idTextView.setText(StringResources.toHexId(group.getId()));
    }

    @Override
    @Expand
    protected void onExpand()
    {
        arrImageView.setImageDrawable(ExpandAnimation);
        ExpandAnimation.start();
    }

    @Override
    @Collapse
    protected void onCollapse()
    {
        arrImageView.setImageDrawable(CollapseAnimation);
        CollapseAnimation.start();
    }
}
