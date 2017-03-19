package gr.aegean.com.samostrails;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by phantomas on 3/13/2017.
 */

public class PopUpFragment extends DialogFragment {
       SeekBar seek;
    TextView seekbarprogress;
    SeekBar seekinternal;
    TextView seekbarprogressinternal;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_layout, container, false);
        getDialog().setTitle("Simple Dialog");
        seekbarprogress= (TextView) view.findViewById(R.id.seekbarprogress);
        Button dismiss = (Button) view.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendBackResult();
            }
        });
        seek=(SeekBar)view.findViewById(R.id.seekBar);
        seekinternal = (SeekBar) view.findViewById(R.id.seekBarinterval);
        seekbarprogressinternal = (TextView) view.findViewById(R.id.seekbarprogressinterval);
       seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               seekbarprogress.setText(String.valueOf(progress));
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });
        seekinternal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarprogressinternal.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

    public interface EditNameDialogListener {

        void onFinishEditDialog(int displacment,int inteval);

    }


    // Call this method to send the data back to the parent fragment

    public void sendBackResult() {

        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed

        EditNameDialogListener listener = (EditNameDialogListener) getTargetFragment();

        listener.onFinishEditDialog(seek.getProgress(),seekinternal.getProgress());

        dismiss();

    }
    public void onPause(){
        super.onPause();
    }
    public void onStart(){
        super.onStart();
    }
    public void onResume(){
        super.onResume();
    }
    public void onStop(){super.onStop();}
}
