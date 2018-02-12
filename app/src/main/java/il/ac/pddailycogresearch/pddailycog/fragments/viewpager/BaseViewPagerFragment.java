package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
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

    protected final FirebaseIO firebaseIO = FirebaseIO.getInstance();

    protected int position;
    protected int choreNum;

    protected boolean secondPhoto = false;
    protected boolean secondText = false;

    protected OnFragmentInteractionListener mListener;

    private long currentSessionStartTime;

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param position Parameter 1.
//     * @param choreNum Parameter 2.
//     * @return A new instance of fragment RadioQuestionFragment.
//     */
//    // TODO: Rename and change types and number of parameters
    // public abstract static  BaseViewPagerFragment newInstance(int position, int choreNum) {
//        RadioQuestionFragment fragment = new RadioQuestionFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_POSITION, position);
//        args.putInt(ARG_CHORE_NUM, choreNum);
//        fragment.setArguments(args);
//        return fragment;
//    }

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

        if (getArguments() == null || !getArguments().containsKey(ARG_POSITION) || !getArguments().containsKey(ARG_CHORE_NUM)) {
            throw new RuntimeException("please implement factory method and call putBaseArguments");//TODO vomit
        }

        position = getArguments().getInt(ARG_POSITION);
        choreNum = getArguments().getInt(ARG_CHORE_NUM);

        secondPhoto = position == 10; //TODO replace with factory argument in the fragments
        secondText = position == 12;

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
                if (hasResult()) {
                    mListener.enableNext();
                }
            } else {
                addTimeToDb();
            }
        }
    }

    protected abstract boolean hasResult();


    private void addTimeToDb() {
        long elapsedTime = System.currentTimeMillis() - currentSessionStartTime;
        firebaseIO.saveIncrementalKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.TIME_KEY_PREFIX + position, elapsedTime);
        currentSessionStartTime = System.currentTimeMillis();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected abstract void saveToDb();


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void enableNext();

        void unenableNext();
    }

}
