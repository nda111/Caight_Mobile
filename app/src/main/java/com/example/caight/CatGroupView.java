package com.example.caight;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;
import com.mindorks.placeholderview.annotations.expand.SingleTop;

@Parent
@SingleTop
@Layout(R.layout.view_cat_group_view)
public class CatGroupView
{
    private CatGroup group = null;

    @View(R.id.groupTextView)
    private TextView groupTextView;
    @View(R.id.idTextView)
    private TextView idTextView;
    @View(R.id.toggleLayout)
    private LinearLayout toggleLayout;

    private Context mContext = null;

    @ParentPosition
    private int mParentPosition;

    public CatGroupView(Context context, CatGroup group)
    {
        mContext = context;
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

    @Resolve
    private void updateInfo()
    {
        groupTextView.setText(group.getName());

        String idString = Integer.toString(group.getId(), 16).toUpperCase();
        StringBuilder idBuilder = new StringBuilder();
        int cnt0 = 8 - idString.length();
        idBuilder.append('#');
        while (cnt0-- > 0)
        {
            idBuilder.append('0');
        }
        idBuilder.append(idString);

        idTextView.setText(idBuilder.toString());
    }
}
