package com.example.haotian.tutorial32;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.google.android.gms.maps.model.Marker;

public class MarkerTitleSnippetDialogFragment extends DialogFragment {
    private Marker marker;
    private EditText titleText;
    private EditText snippetText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setMessage(R.string.marker_message);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_set_title_snippet, null));
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        titleText = (EditText)getDialog().findViewById(R.id.titleText);
                        snippetText = (EditText)getDialog().findViewById(R.id.snippetText);
                        String title = titleText.getText().toString();
                        String snippet = snippetText.getText().toString();
                        marker.setTitle(title);
                        marker.setSnippet(snippet);
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MarkerTitleSnippetDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void setMarker (Marker marker){
        this.marker = marker;
    }
}
