package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;


import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstructionFragment extends BaseViewPagerFragment {

    private static final String ARG_INSTRC_KEY = "instruction_id";
    @BindView(R.id.textViewInstructionFragment)
    TextView textViewInstructionFragment;
    Unbinder unbinder;
    private int instrctionTextId;

    public InstructionFragment() {
        // Required empty public constructor
    }


    public static InstructionFragment newInstance(int position, int choreNum, @StringRes int instrcId) {
        InstructionFragment fragment = new InstructionFragment();
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
        View view = inflater.inflate(R.layout.fragment_instruction, container, false);
        unbinder = ButterKnife.bind(this, view);
        textViewInstructionFragment.setText(instrctionTextId);
        return view;
    }

    @Override
    protected boolean hasResult() {
        return true;
    }

    @Override
    protected void saveToDb() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
