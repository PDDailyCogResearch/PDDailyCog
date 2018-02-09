package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;


/**
 * Created by ggrot on 09/02/2018.
 */

public class TextFragment extends BaseViewPagerFragment {
    private static final String PREVIOUS_TEXT_INPUT_LENGTH = "previous_text_input_length";
    @BindView(R.id.EditTextInputFragment)
    EditText EditTextInputFragment;
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
        return view;
    }

//TODO: after changing- if deleted unenableText
    @OnTextChanged(value = R.id.EditTextInputFragment,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
     void onTextChanged(CharSequence text) {
        mListener.enableNext();
        if(text.equals("")){
            mListener.unenableNext();
        }
    }


      @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected void saveToDb() {
    }
}
