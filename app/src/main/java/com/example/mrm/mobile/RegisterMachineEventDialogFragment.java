package com.example.mrm.mobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RegisterMachineEventDialogFragment extends DialogFragment {
    // Interface for allowing calle activity to process the events
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Instance of the interface to deliver the events
    NoticeDialogListener listener;

    // Indicators of selected options
    int SelectedEventOption = -1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO: Dialog must be shown full screen to accommodate needs maintenance check and comment
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.registerEventDialogTitle)
                .setSingleChoiceItems(R.array.registerEventDialogOptions, 0,
                        (dialog, which) -> SelectedEventOption = which)
                .setPositiveButton(R.string.registerEventDialogConfirm, (dialog, id) -> {
                    // Send the positive button event back to the host activity
                    listener.onDialogPositiveClick(RegisterMachineEventDialogFragment.this);
                })
                .setNegativeButton(R.string.registerEventDialogCancel, (dialog, id) -> {
                    // Send the negative button event back to the host activity
                    listener.onDialogNegativeClick(RegisterMachineEventDialogFragment.this);
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NoticeDialogListener");
        }
    }
}
