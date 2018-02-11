package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;




/**
 * Created by ggrot on 09/02/2018.
 */

public class TextFragment extends BaseViewPagerFragment {
    private static final String PREVIOUS_TEXT_INPUT_LENGTH = "previous_text_input_length";
    @BindView(R.id.EditTextInputFragment)
    EditText EditTextInputFragment;
    @BindView(R.id.textViewInstrc)
    TextView textViewInstrc;
    @BindView(R.id.textViewMinutes)
    TextView textViewMinutes;
    Unbinder unbinder;

    public TextFragment() {
        // Required empty public constructor
    }

    public static TextFragment newInstance(int position, int choreNum) {
        TextFragment fragment = new TextFragment();
        fragment.setArguments(putBaseArguments(new Bundle(), position, choreNum));
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        unbinder = ButterKnife.bind(this, view);
        if(savedInstanceState==null){
            retreiveFromDb();
        }
        if(secondText){
            textViewInstrc.setText(R.string.drink_time_valuate);
            textViewMinutes.setVisibility(View.VISIBLE);
        }
        else {
            textViewInstrc.setText(R.string.drink_instr);
            textViewMinutes.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private void retreiveFromDb() {
        firebaseIO.retreieveStringValueByKey(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, new IOnFirebaseKeyValueListeners.OnStringValueListener() {
            @Override
            public void onValueRetrieved(String value) {
                if (value != null) {
                    EditTextInputFragment.setText(value);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    //TODO: after changing- if deleted unenableText
    @OnTextChanged(value = R.id.EditTextInputFragment,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
     void onTextChanged(CharSequence text) {
        if(text.toString().isEmpty()){
            mListener.unenableNext();
        }else {
            mListener.enableNext();
        }
    }


      @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected boolean hasResult() {
        if(!EditTextInputFragment.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    protected void saveToDb() {
        firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, EditTextInputFragment.getText().toString());
    }
}
