package com.example.caight;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

public class DeletionConfirmDialog extends DialogFragment
{
    public interface OnDeletionConfirmListener
    {
        void onConfirm();

        void onCancel();
    }

    private int titleId;
    private String confirmKey;

    private TextView confirmKeyTextView;
    private EditText confirmKeyEditText;

    private OnDeletionConfirmListener listener = null;

    public DeletionConfirmDialog(@StringRes int titleId, @NonNull String confirmKey)
    {
        this.titleId = titleId;
        this.confirmKey = confirmKey;
    }

    @SuppressLint({ "SetTextI18n", "ClickableViewAccessibility" })
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view;
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(titleId)
                .setView(view = inflater.inflate(R.layout.dialog_deletion_confirm, null))
                .setPositiveButton(R.string.act_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (listener != null)
                        {
                            if (confirmKeyEditText.getText().toString().equals(confirmKey))
                            {
                                listener.onConfirm();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.act_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (listener != null)
                        {
                            listener.onCancel();
                        }
                    }
                })
                .setCancelable(true)
                .create();

        Resources resources = getResources();

        /*
         * Initialize GUI Components
         */
        confirmKeyTextView = view.findViewById(R.id.confirmKeyTextView);
        confirmKeyEditText = view.findViewById(R.id.confirmKeyEditText);

        // confirmKeyTextView
        confirmKeyTextView.setText('\'' + confirmKey + '\'');

        // confirmKeyEditText
        confirmKeyEditText.setHint(confirmKey);

        return dialog;
    }

    public void setListener(OnDeletionConfirmListener l)
    {
        listener = l;
    }

    public OnDeletionConfirmListener getListener()
    {
        return listener;
    }
}
