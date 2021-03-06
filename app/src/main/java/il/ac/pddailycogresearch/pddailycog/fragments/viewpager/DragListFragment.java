package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.adapters.RecyclerListAdapter;
import il.ac.pddailycogresearch.pddailycog.adapters.draghelpers.SimpleItemTouchHelperCallback;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.ReadJsonUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DragListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DragListFragment extends BaseViewPagerFragment {


    private static final String TAG = DragListFragment.class.getSimpleName();
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.textViewDragFragment)
    TextView textViewDragFragment;
    private RecyclerListAdapter adapter;
    private List<String> tasksList;

    public DragListFragment() {
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
    public static DragListFragment newInstance(int position, int choreNum) {
        DragListFragment fragment = new DragListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(putBaseArguments(args, position, choreNum));
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drag_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        String instrc = ReadJsonUtil.readInstruction(getActivity(),choreNum,position);
        textViewDragFragment.setText(instrc);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (tasksList != null) {
            initRecyclerAdapter();
        } else {
            initFromDb();
        }
    }

    private void initRecyclerAdapter() {
        adapter = new RecyclerListAdapter(tasksList);

       // recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected boolean hasResult() {
        return true;
    }

    private void initFromDb() {
        firebaseIO.retreieveStringListValueByKey(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position,
                new IOnFirebaseKeyValueListeners.OnStringListValueListener() {
                    @Override
                    public void onValueRetrieved(List<String> value) {
                        if (value != null && value.size() != 0) {
                            tasksList = value;
                        } else {
                            initListFromResources();
                        }
                        initRecyclerAdapter();
                    }

                    @Override
                    public void onError(Exception e) {
                        CommonUtils.onGeneralError(e, TAG);
                    }
                });
    }

    private void initListFromResources() {
        String[] tasksArray = getResources().getStringArray(
                getResources().getIdentifier(Consts.DRAG_TASKS_LIST_PREFIX + String.valueOf(choreNum), "array", getActivity().getPackageName()));
        tasksList = Arrays.asList(tasksArray);
        Collections.shuffle(tasksList);
    }

    @Override
    protected void saveToDb() {
        tasksList = adapter.getItems();
        firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, tasksList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
