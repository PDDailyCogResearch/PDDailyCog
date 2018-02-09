package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;
//shabat shalom rotem!!

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.model.JsonRadioButton;
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

    @BindView(R.id.textViewQuestionRadioFragment)
    TextView textViewQuestionRadioFragment;
    Unbinder unbinder;
    @BindView(R.id.textViewInstructionRadioFragment)
    TextView textViewInstructionRadioFragment;
    @BindView(R.id.radioGroupRadioFragment)
    RadioGroup radioGroupRadioFragment;

    private int selection = -1;
    private JsonRadioButton question;

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
    public static RadioQuestionFragment newInstance(int position, int choreNum) {
        RadioQuestionFragment fragment = new RadioQuestionFragment();
        fragment.setArguments(putBaseArguments(new Bundle(), position, choreNum));
        return fragment;
    }

    @Override
    protected void restoreFromSavedInstanceState(Bundle savedInstanceState) {
        super.restoreFromSavedInstanceState(savedInstanceState);
        selection = savedInstanceState.getInt(SELECTION_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_radio_question, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (selection != -1) {
            mListener.enableNext();
        }
        initViews();
        return view;
    }

    private void initViews() {
        int positionPlus=position+1;//TODO better sulotion
        question = ReadJsonUtil.readRadioJsonFile(getActivity(), Consts.DRINK_CHORE_QUESTION_ASSETS_PREFIX + positionPlus);
        if (question == null) {
            textViewQuestionRadioFragment.setText("not availble"); //TODO put error msg
         //   mListener.enableNext();
            return;
        }
        textViewQuestionRadioFragment.setText(question.getQuestion());
        textViewInstructionRadioFragment.setText(question.getInstruction());
        initRadioGroup(question.getAnswer());

    }

    private void initRadioGroup(ArrayList<String> answers) {
        View.OnClickListener radioButtonsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                int radioButtonID = radioGroupRatingFragment.getCheckedRadioButtonId();
                View radioButton = radioGroupRatingFragment.findViewById(radioButtonID);*/
                selection = radioGroupRadioFragment.indexOfChild(v);
                mListener.enableNext();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SELECTION_TAG, selection);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser&&isResumed()) {
            if (mListener != null && (selection != -1 || question == null)) {
                mListener.enableNext();
            }
        }
    }


    @Override
    protected void saveToDb() {
        if (selection >= 0) {
            firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, selection);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
