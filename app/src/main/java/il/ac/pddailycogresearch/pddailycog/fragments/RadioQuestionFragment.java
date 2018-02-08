package il.ac.pddailycogresearch.pddailycog.fragments;
//hi rotem:)

import android.content.Context;
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
public class RadioQuestionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String ARG_CHORE_NUM = "chore_num";
    private static final String SELECTION_TAG = "selection";
    private static final String TOTAL_TIME_TAG = "total-time";
    @BindView(R.id.textViewQuestionRadioFragment)
    TextView textViewQuestionRadioFragment;
    Unbinder unbinder;
    @BindView(R.id.textViewInstructionRadioFragment)
    TextView textViewInstructionRadioFragment;
    @BindView(R.id.radioGroupRadioFragment)
    RadioGroup radioGroupRadioFragment;

    private int position;
    private int choreNum;

    private OnFragmentInteractionListener mListener;

    private int selection = -1;
    private long currentSessionStartTime;
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
    // TODO: Rename and change types and number of parameters
    public static RadioQuestionFragment newInstance(int position, int choreNum) {
        RadioQuestionFragment fragment = new RadioQuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_CHORE_NUM, choreNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            choreNum = getArguments().getInt(ARG_CHORE_NUM);
        }
        if (savedInstanceState != null) {
            selection = savedInstanceState.getInt(SELECTION_TAG);
        }

        //TODO take selection from db?
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
        question = ReadJsonUtil.readRadioJsonFile(getActivity(), Consts.DRINK_CHORE_QUESTION_ASSETS_PREFIX + position);
        if (question == null) {
            textViewQuestionRadioFragment.setText("not availble"); //TODO put error msg
            mListener.enableNext();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getUserVisibleHint()) {
            currentSessionStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getUserVisibleHint() && !isResumed()) {
            addTimeToDb();
        }
        saveToDb();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            if (isVisibleToUser) {
                currentSessionStartTime = System.currentTimeMillis();
                if (mListener != null && (selection != -1 || question == null)) {
                    mListener.enableNext();
                }
            } else {
                addTimeToDb();
            }
        }
    }


    private void addTimeToDb() {
        long elapsedTime = System.currentTimeMillis() - currentSessionStartTime;
        FirebaseIO.getInstance().saveIncrementalKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.TIME_KEY_PREFIX + position, elapsedTime);
        currentSessionStartTime = System.currentTimeMillis();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // saveToDb();
        mListener = null;
    }

    private void saveToDb() {
        if (selection >= 0) {
            FirebaseIO.getInstance().saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, selection);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void enableNext();
    }

}
