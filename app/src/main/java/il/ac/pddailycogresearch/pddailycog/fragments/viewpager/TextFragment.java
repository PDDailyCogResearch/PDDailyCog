package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
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

public class TextFragment extends BaseViewPagerFragment {
    private static final String TAG = TextFragment.class.getSimpleName();
    private static final String ARG_INSTRC_KEY = "instruction_id";
    private static List<Integer> MINUTES_VISIBLE_INSTRUCTIONS = Arrays.asList(R.string.drink_time_valuat_text_instrc);

    @BindView(R.id.EditTextInputFragment)
    EditText EditTextInputFragment;
    @BindView(R.id.textViewInstrc)
    TextView textViewInstrc;
    @BindView(R.id.textViewMinutes)
    TextView textViewMinutes;
    Unbinder unbinder;
    private int instrctionTextId;
    private String inputText;

    public TextFragment() {
        // Required empty public constructor
    }

    public static TextFragment newInstance(int position, int choreNum, @StringRes int instrcId) {
        TextFragment fragment = new TextFragment();
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
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState == null) {
            retrieveFromDb();
        }

        textViewInstrc.setText(instrctionTextId);

        if (MINUTES_VISIBLE_INSTRUCTIONS.contains(instrctionTextId)) {
            textViewMinutes.setVisibility(View.VISIBLE);
        } else {
            textViewMinutes.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private void retrieveFromDb() {
        firebaseIO.retreieveStringValueByKey(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, new IOnFirebaseKeyValueListeners.OnStringValueListener() {
            @Override
            public void onValueRetrieved(String value) {
                if (value != null) {
                    inputText = value;
                    EditTextInputFragment.setText(inputText);
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
        if (text.toString().isEmpty()) {
            mListener.unenableNext();
        } else {
            if (!text.toString().equals(inputText)) {//prevent enable when initilize
                mListener.enableNext();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected boolean hasResult() {
        if (!EditTextInputFragment.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    protected void saveToDb() {
        firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, EditTextInputFragment.getText().toString());
    }
}
