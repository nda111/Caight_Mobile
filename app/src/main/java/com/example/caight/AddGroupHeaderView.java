package com.example.caight;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;
import com.mindorks.placeholderview.annotations.expand.SingleTop;

@Parent
@SingleTop
@Layout(R.layout.view_icon_item)
public class AddGroupHeaderView
{
    private Context context = null;

    @View(R.id.iconImageView)
    private ImageView iconImageView;
    @View(R.id.nameTextView)
    private TextView titleTextView;
    @View(R.id.descriptionTextView)
    private TextView descriptionTextView;

    @ParentPosition
    private int mParentPosition;

    private int iconId = -1;
    private int titleId = -1;
    private int descriptionId = -1;

    public AddGroupHeaderView(Context context, @DrawableRes int iconId, @StringRes int nameId, @StringRes int descriptionId)
    {
        this.context = context;

        this.iconId = iconId;
        this.titleId = nameId;
        this.descriptionId = descriptionId;
    }

    @Resolve
    protected void onResolve()
    {
        iconImageView.setImageDrawable(ContextCompat.getDrawable(context, iconId));
        titleTextView.setText(context.getResources().getString(titleId));
        descriptionTextView.setText(context.getResources().getString(descriptionId));
    }
}
