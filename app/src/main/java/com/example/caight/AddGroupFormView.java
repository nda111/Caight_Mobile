package com.example.caight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.view_add_group)
public class AddGroupFormView
{
    private Context context;

    @View(R.id.identifierTextView)
    private TextView identifierTextView;
    @View(R.id.passwordEditText)
    private TextView passwordTextView;
    @View(R.id.submitButton)
    private Button submitButton;

    private int identifierType;
    private int identifierId;
    private int actionNameId;

    public android.view.View.OnTouchListener onTouchListener = null;

    public AddGroupFormView(Context context, @StringRes int identifierId, int identifierType, @StringRes int actionNameId)
    {
        this.context = context;

        this.identifierId = identifierId;
        this.identifierType = identifierType;
        this.actionNameId = actionNameId;
    }

    public int getIdentifierLength()
    {
        return identifierTextView.getText().length();
    }

    public int getPasswordLength()
    {
        return passwordTextView.getText().length();
    }

    public boolean isValid()
    {
        return getIdentifierLength() != 0 && getPasswordLength() != 0;
    }

    public String[] getResult()
    {
        return new String[] {
                identifierTextView.getText().toString(),
                passwordTextView.getText().toString()
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    @Resolve
    protected void onResolve()
    {
        identifierTextView.setInputType(identifierType);
        identifierTextView.setHint(identifierId);

        submitButton.setText(actionNameId);
        submitButton.setOnTouchListener(onTouchListener);
    }
}
