package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;


/**
 * Created by ggrot on 09/02/2018.
 */

public class TextInputFragment extends BaseViewPagerFragment {
    private static final String TAG = TextInputFragment.class.getSimpleName();
    private static final String ARG_INSTRC_KEY = "instruction_id";
//    private static List<Integer> MINUTES_VISIBLE_INSTRUCTIONS = Arrays.asList(R.string.drink_time_valuat_text_instrc);
//    private static List<Integer> INPUT_TYPE_TEXT_INSTRUCTIONS = Arrays.asList(R.string.text_input_instrc);
    private boolean isMinutesVisible;
    private boolean isInputTypeText;

    @BindView(R.id.EditTextInputFragment)
    EditText editTextInputFragment;
    @BindView(R.id.textViewInstrc)
    TextView textViewInstrc;
    @BindView(R.id.textViewMinutes)
    TextView textViewMinutes;
    Unbinder unbinder;
    private int instrctionTextId;
    private String inputText;

    public TextInputFragment() {
        // Required empty public constructor
    }

    public static TextInputFragment newInstance(int position, int choreNum, @StringRes int instrcId) {
        TextInputFragment fragment = new TextInputFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INSTRC_KEY, instrcId);
        fragment.setArguments(putBaseArguments(args, position, choreNum));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instrctionTextId = getArguments().getInt(ARG_INSTRC_KEY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text_input, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState == null) {
            retrieveFromDb();
        }

        textViewInstrc.setText(instrctionTextId);

        initViews();
        return view;
    }

    private void initViews() {
        if (isMinutesVisible) {
            textViewMinutes.setVisibility(View.VISIBLE);
        } else {
            textViewMinutes.setVisibility(View.INVISIBLE);
        }
        if (isInputTypeText) {
            editTextInputFragment.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            editTextInputFragment.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    private void retrieveFromDb() {
        firebaseIO.retreieveStringValueByKey(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, new IOnFirebaseKeyValueListeners.OnStringValueListener() {
            @Override
            public void onValueRetrieved(String value) {
                if (value != null) {
                    inputText = value;
                    editTextInputFragment.setText(inputText);
                    onGotResult();
                }
            }

            @Override
            public void onError(Exception e) {
                CommonUtils.onGeneralError(e,TAG);
            }
        });
    }

    @OnTextChanged(value = R.id.EditTextInputFragment,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChanged(CharSequence text) {
//        if (text.toString().isEmpty()) {
//            mListener.unenableNext();
//        } else {
//            if (!text.toString().equals(inputText)) {//prevent enable when initilize
//                mListener.enableNext();
//            }
//        }
        onGotResult();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected boolean hasResult() {
        if (!editTextInputFragment.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    protected void saveToDb() {
        firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, editTextInputFragment.getText().toString());
    }

    public TextInputFragment setMinutesVisible(boolean minutesVisible) {
        isMinutesVisible = minutesVisible;
        return this;
    }

    public TextInputFragment setInputTypeText(boolean inputTypeText) {
        isInputTypeText = inputTypeText;
        return this;
    }
}
