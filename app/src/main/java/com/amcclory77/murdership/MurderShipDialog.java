package com.amcclory77.murdership;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.os.Bundle;

public class MurderShipDialog extends DialogFragment {

    static MurderShipDialog newInstance(int id, String title, String text, boolean criticalText, Bitmap bitmap) {
        MurderShipDialog f = new MurderShipDialog();

        // Supply dialog attributes as arguments.
        Bundle args = new Bundle();

        args.putInt("id", id);
        args.putString("title", title);
        args.putString("text", text);
        args.putBoolean("criticalText", criticalText);

        args.putParcelable("bitmap", bitmap);

        f.setArguments(args);
        return f;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int id = getArguments().getInt("id");
        String title = getArguments().getString("title");
        String text = getArguments().getString("text");
        boolean criticalText = getArguments().getBoolean("criticalText");
        Bitmap bitmap = getArguments().getParcelable("bitmap");

        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        View v;

        // Choose the layout associated with the dialog's ID
        switch(id)
        {
            case MurderShip.CHARACTER_DIALOG: {
                v = inflater.inflate(R.layout.character, container, false);

                Button introduceButton = (Button) v.findViewById(R.id.Introduce);
                if (introduceButton != null) {
                    introduceButton.setOnClickListener((OnClickListener) getActivity());
                }

                Button questionButton = (Button) v.findViewById(R.id.Question);
                if (questionButton != null) {
                    questionButton.setOnClickListener((OnClickListener) getActivity());
                }

                Button accuseButton = (Button) v.findViewById(R.id.Accuse);
                if (accuseButton != null) {
                    accuseButton.setOnClickListener((OnClickListener) getActivity());
                }
            }
                break;
            case MurderShip.ITEM_DIALOG: {
                v = inflater.inflate(R.layout.item, container, false);

                Button examineButton = (Button) v.findViewById(R.id.Examine);
                if (examineButton != null) {
                    examineButton.setOnClickListener((OnClickListener) getActivity());
                }

                Button pickupButton = (Button) v.findViewById(R.id.PickUp);
                if (pickupButton != null) {
                    pickupButton.setOnClickListener((OnClickListener) getActivity());
                }

            }
                break;
            case MurderShip.OBJECT_DIALOG: {
                v = inflater.inflate(R.layout.object, container, false);

                Button examineButton = (Button) v.findViewById(R.id.Examine);
                if (examineButton != null) {
                    examineButton.setOnClickListener((OnClickListener) getActivity());
                }

            }
                break;
            case MurderShip.RESPONSE_DIALOG:
            default: {
                v = inflater.inflate(R.layout.response, container, false);

                TextView textView = (TextView) v.findViewById(R.id.text);
                textView.setText(text);

                if (criticalText) {
                    textView.setTextColor(Color.rgb(255, 0, 0));
                    textView.setTextSize(20);
                } else {
                    textView.setTextColor(Color.rgb(255, 255, 255));
                    textView.setTextSize(14);
                }

            }
                break;
        }

        ImageView imageView = (ImageView) v.findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);

        TextView titleView = (TextView) v.findViewById(R.id.title);
        titleView.setText(title);

        Button cancelButton = (Button) v.findViewById(R.id.Cancel);
        if (cancelButton != null) {
            cancelButton.setOnClickListener((OnClickListener) getActivity());
        }

        return v;
    }
}
