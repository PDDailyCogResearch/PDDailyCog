package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;


import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckBoxFragment extends BaseViewPagerFragment {


    private static final String ARG_INSTRC_KEY = "instruction_id";
    @BindView(R.id.check_box)
    CheckBox checkBox;
    Unbinder unbinder;
    @BindView(R.id.textViewCheckBox)
    TextView textViewCheckBox;
    private int instrctionTextId;

    public CheckBoxFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @param choreNum Parameter 2.
     * @return A new instance of fragment DragListFragment.
     */
    public static CheckBoxFragment newInstance(int position, int choreNum, @StringRes int instrcId) {
        CheckBoxFragment fragment = new CheckBoxFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_box, container, false);
        unbinder = ButterKnife.bind(this, view);
        textViewCheckBox.setText(instrctionTextId);
        if (savedInstanceState == null) {
            initFromDb();
        }
        return view;
    }

    private void initFromDb() {
        firebaseIO.retreieveStringValueByKey(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position,
                new IOnFirebaseKeyValueListeners.OnStringValueListener() {
                    @Override
                    public void onValueRetrieved(String value) {
                        if (value != null && value.equals(String.valueOf(true))) {
                            checkBox.setChecked(true);
                        } else {
                            checkBox.setChecked(false);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    @Override
    protected boolean hasResult() {
        return true;
    }

    @Override
    protected void saveToDb() {
        firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position,
                String.valueOf(checkBox.isChecked()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
