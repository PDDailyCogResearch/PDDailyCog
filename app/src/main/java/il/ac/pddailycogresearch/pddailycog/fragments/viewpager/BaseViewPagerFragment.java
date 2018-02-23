package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Arrays;
import java.util.List;

import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.stepdetector.StepCounter;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RadioQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class BaseViewPagerFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_POSITION = "position";
    protected static final String ARG_CHORE_NUM = "chore_num";
    protected static List<Integer> DETECT_STEPS_CHORES = Arrays.asList(1);

    protected final FirebaseIO firebaseIO = FirebaseIO.getInstance();

    protected int position;
    protected int choreNum;

    protected OnFragmentInteractionListener mListener;

    private long currentSessionStartTime;

    protected StepCounter stepCounter = StepCounter.getInstance();
    private long currentSessionStartSteps = -1;

    protected static Bundle putBaseArguments(Bundle args, int position, int choreNum) {
        if (args != null) {
            args.putInt(ARG_POSITION, position);
            args.putInt(ARG_CHORE_NUM, choreNum);
        }
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//throw runtim exeption if child didnt implement appropriate factory method
        if (getArguments() == null || !getArguments().containsKey(ARG_POSITION) || !getArguments().containsKey(ARG_CHORE_NUM)) {
            throw new RuntimeException("please implement factory method and call putBaseArguments");//TODO vomit
        }

        position = getArguments().getInt(ARG_POSITION);
        choreNum = getArguments().getInt(ARG_CHORE_NUM);

        if (savedInstanceState != null) {
            restoreFromSavedInstanceState(savedInstanceState);
        }
    }

    protected void restoreFromSavedInstanceState(Bundle savedInstanceState) {
        //do nothing if no need
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
        if(DETECT_STEPS_CHORES.contains(choreNum)){
            stepCounter.registerSensors(getContext());
        }

        if (getUserVisibleHint()) {
            onPageChanged(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getUserVisibleHint() && !isResumed()) {
            onPageChanged(false);
        }
        saveToDb();
    }


    protected void onGotResult() {
        if(getUserVisibleHint()&&isResumed()){
            if(hasResult()) {
                mListener.enableNext();
            } else {
                mListener.unenableNext();
            }
        }
    }

    public void onPageChanged(boolean isVisible) {

        CommonUtils.hideKeyboard(getActivity());
        // CommonUtils.showMessage(getContext(),"onPage "+ position+ " changed "+ isVisible);
        if (isVisible) {
            currentSessionStartTime = System.currentTimeMillis();
            if(DETECT_STEPS_CHORES.contains(choreNum)) {
                currentSessionStartSteps = stepCounter.getStepsNum();
            }
            if (hasResult()) {
                mListener.enableNext();
            } else {
                mListener.unenableNext();
            }
        } else {
            addTimeToDb();
            if(DETECT_STEPS_CHORES.contains(choreNum)){
                addStepsToDb();
            }
        }

    }


    protected abstract boolean hasResult();

    private void addStepsToDb() {
        long elapsedSteps = stepCounter.getStepsNum() - currentSessionStartSteps;
        addStepsToDb(elapsedSteps);
        currentSessionStartSteps = stepCounter.getStepsNum();
    }

    protected void addStepsToDb(long elapsedSteps) {
        firebaseIO.saveIncrementalKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.STEPS_KEY_PREFIX + position, elapsedSteps);
    }

    private void addTimeToDb() {
        long elapsedTime = System.currentTimeMillis() - currentSessionStartTime;
        addTimeToDb(elapsedTime);
        currentSessionStartTime = System.currentTimeMillis();
    }

    protected void addTimeToDb(long elapsedTime) {
        firebaseIO.saveIncrementalKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.TIME_KEY_PREFIX + position, elapsedTime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(DETECT_STEPS_CHORES.contains(choreNum)) {
            stepCounter.unregisterSensors();
        }
    }

    protected abstract void saveToDb();


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void enableNext();

        void unenableNext();
    }

}
