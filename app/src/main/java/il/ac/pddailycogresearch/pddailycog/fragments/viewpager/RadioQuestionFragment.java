package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;
//shabat shalom rotem!!

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.model.JsonRadioButton;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.ReadJsonUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RadioQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RadioQuestionFragment extends BaseViewPagerFragment {

    private static final String SELECTION_TAG = "selection";
    private static final String ARG_ASSET_FOLDER_KEY = "assets_folder";
    private static final String TAG = RadioQuestionFragment.class.getSimpleName();
    private static final java.lang.String FREETEXT_TAG = "freetext";

    @BindView(R.id.textViewQuestionRadioFragment)
    TextView textViewQuestionRadioFragment;
    Unbinder unbinder;
    @BindView(R.id.textViewInstructionRadioFragment)
    TextView textViewInstructionRadioFragment;
    @BindView(R.id.radioGroupRadioFragment)
    RadioGroup radioGroupRadioFragment;
    @BindView(R.id.edit_text_radio)
    EditText editTextRadio;

    private int selection = -1;
    private String freetext = "";
    private JsonRadioButton question;
    private String assetsFolder;

    public RadioQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @param choreNum Parameter 2.
     * @return A new instance of fragment RadioQuestionFragment.
     */
    public static RadioQuestionFragment newInstance(int position, int choreNum, String assetFolder) {
        RadioQuestionFragment fragment = new RadioQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ASSET_FOLDER_KEY, assetFolder);
        fragment.setArguments(putBaseArguments(args, position, choreNum));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assetsFolder = getArguments().getString(ARG_ASSET_FOLDER_KEY);
    }

    @Override
    protected void restoreFromSavedInstanceState(Bundle savedInstanceState) {
        super.restoreFromSavedInstanceState(savedInstanceState);
        selection = savedInstanceState.getInt(SELECTION_TAG);
        freetext = savedInstanceState.getString(FREETEXT_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_radio_question, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState == null) {
            initFromDb();
        } else {
            initViews();
        }
        return view;
    }

    private void initViews() {
        question = ReadJsonUtil.readRadioJsonFile(getActivity(), assetsFolder + Consts.QUESTION_ASSETS_PREFIX + position);
        if (question == null) {
            selection = 0;
            textViewQuestionRadioFragment.setText("not avaialble"); //TODO put error msg
            return;
        }
        textViewQuestionRadioFragment.setText(question.getQuestion());
        if (question.getInstruction().equals("")) {
            textViewInstructionRadioFragment.setVisibility(View.GONE);
        } else {
            textViewInstructionRadioFragment.setText(question.getInstruction());
        }

        if(isFreeTextExist()){
            editTextRadio.setVisibility(View.VISIBLE);
            if(!freetext.isEmpty()){
                editTextRadio.setText(freetext);
            }
        }
        if(isFreeTextSelected()){
            editTextRadio.setEnabled(true);
        }
        initRadioGroup(question.getAnswer());

    }

    private void initRadioGroup(ArrayList<String> answers) {
        View.OnClickListener radioButtonsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                int radioButtonID = radioGroupRatingFragment.getCheckedRadioButtonId();
                View radioButton = radioGroupRatingFragment.findViewById(radioButtonID);*/
                selection = radioGroupRadioFragment.indexOfChild(v);
                if(isFreeTextSelected()){
                    editTextRadio.setEnabled(true);
                } else{
                    editTextRadio.setEnabled(false);
                }
                onGotResult();
                //  mListener.onRatingChanged(idx + 1);
            }
        };

        for (String answer : answers) {
            // RadioButton rb = new RadioButton(getContext(),null,R.style.tryRadioButton);
            RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.template_radiobutton, null);


            rb.setText(answer);
            rb.setOnClickListener(radioButtonsListener);
            //  radioGroupRadioFragment.setId(radioGroupRadioFragment.getChildCount());

            radioGroupRadioFragment.addView(rb);
            if (radioGroupRadioFragment.indexOfChild(rb) == selection)
                radioGroupRadioFragment.check(rb.getId());
        }

        //    radioGroupRadioFragment.setOrientation(LinearLayout.HORIZONTAL);
    }

    private boolean isFreeTextExist() {
        return (question!=null&&question.getAnswer().get(question.getAnswer().size()-1).equals("אחר:"));
    }
    private boolean isFreeTextSelected() {
        return (isFreeTextExist()&&question.getAnswer().indexOf("אחר:")==selection);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SELECTION_TAG, selection);
        outState.putString(FREETEXT_TAG,freetext);
        super.onSaveInstanceState(outState);
    }

    @OnTextChanged(value = R.id.edit_text_radio,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChanged(CharSequence text) {
        freetext = editTextRadio.getText().toString();
        onGotResult();
    }

    @Override
    protected boolean hasResult() {
        if(isFreeTextSelected()&&freetext.isEmpty()){
            return false;
        }
        if (mListener != null && (selection != -1)) {
            return true;
        }
        return false;
    }


    protected void initFromDb() {
        firebaseIO.retreieveIntValueByKey(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, new IOnFirebaseKeyValueListeners.OnIntValueListener() {
            @Override
            public void onValueRetrieved(Integer value) {
                if (value == null) {
                    initViews();
                } else {
                    selection = value;
                    firebaseIO.retreieveStringValueByKey(Consts.CHORES_KEY, choreNum, Consts.FREE_RESULT_KEY_PREFIX + position
                            , new IOnFirebaseKeyValueListeners.OnStringValueListener() {
                                @Override
                                public void onValueRetrieved(String value) {
                                    if(value!=null) {
                                        freetext = value;
                                    }
                                    onGotResult();
                                    initViews();
                                }

                                @Override
                                public void onError(Exception e) {
                                    CommonUtils.onGeneralError(e,TAG);
                                }
                            });
                }
            }

            @Override
            public void onError(Exception e) {
                CommonUtils.onGeneralError(e,TAG);
            }
        });
    }

    @Override
    protected void saveToDb() {
        if (selection >= 0) {
            firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, selection);
        }
        if(isFreeTextSelected()){
            firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.FREE_RESULT_KEY_PREFIX + position, freetext);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
