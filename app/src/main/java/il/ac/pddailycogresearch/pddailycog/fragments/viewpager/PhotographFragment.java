package il.ac.pddailycogresearch.pddailycog.fragments.viewpager;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.ImageUtils;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotographFragment extends BaseViewPagerFragment {
    private static final String CLICK_NUM_TAG = "take_pic_click_num";
    private static final String IMG_ABSOLUTE_PATH_TAG = "img_absolute_path";
    private static final String IMG_URI_TAG = "img_uri";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String ARG_INSTRC_KEY = "instrc_id";

    private String imgAbsolutePath;
    private Uri imgUri;
    private static int imageViewHeight;
    private static int imageViewWidth;


    @BindView(R.id.imageViewPhotographFragment)
    ImageView imageViewPhotographFragment;
    @BindView(R.id.buttonPhotographFragment)
    Button buttonPhotographFragment;
    @BindView(R.id.textViewInstrcPhotographFragment)
    TextView textViewInstrcPhotographFragment;

    Unbinder unbinder;
    private int takePicturesClickNum = 0;
    private int instrctionTextId;

    public PhotographFragment() {
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
    public static PhotographFragment newInstance(int position, int choreNum, @StringRes int instrcId) {
        PhotographFragment fragment = new PhotographFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INSTRC_KEY,instrcId);
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
        View view = inflater.inflate(R.layout.fragment_photograph, container, false);
        unbinder = ButterKnife.bind(this, view);

        //     mListener.enableNext();//TODO delete this, but aint power to take pictures all the timee
        setPictureToImageView();
        textViewInstrcPhotographFragment.setText(instrctionTextId);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(new Runnable() {
            @Override
            public void run() {
                imageViewHeight = imageViewPhotographFragment.getHeight();
                imageViewWidth = imageViewPhotographFragment.getWidth();
                retrieveAndSetImageFromDb();
            }
        });
    }

    @Override
    protected void restoreFromSavedInstanceState(Bundle savedInstanceState) {
        super.restoreFromSavedInstanceState(savedInstanceState);
        imgAbsolutePath = savedInstanceState.getString(IMG_ABSOLUTE_PATH_TAG);
        imgUri = (Uri) savedInstanceState.getParcelable(IMG_URI_TAG);
        takePicturesClickNum = savedInstanceState.getInt(CLICK_NUM_TAG);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(IMG_ABSOLUTE_PATH_TAG, imgAbsolutePath);
        outState.putParcelable(IMG_URI_TAG, imgUri);
        outState.putInt(CLICK_NUM_TAG, takePicturesClickNum);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.buttonPhotographFragment)
    public void onViewClicked() {
        takePicturesClickNum++;
        Intent takePictureIntent = ImageUtils.createTakePictureIntent(getContext());
        imgAbsolutePath = takePictureIntent.getStringExtra(ImageUtils.IMAGE_ABSOLUTE_PATH);
        Bundle extras = takePictureIntent.getExtras();
        imgUri = (Uri) extras.get(MediaStore.EXTRA_OUTPUT);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                setPictureToImageView();
                onHasPicture();
            }
        }
    }

    private void setPictureToImageView() {
        if (imgAbsolutePath != null && mListener != null) {//TODO prevent bug when download img. need to rethink this anyway because of flight mode
            ImageUtils.setPic(imageViewPhotographFragment, imgAbsolutePath, imageViewHeight, imageViewWidth);
        } else if (imgUri != null) {
            ImageUtils.setPicFromContentUri(getContext(), imageViewPhotographFragment, imgUri, imageViewHeight, imageViewWidth);

        }
    }

    protected void retrieveAndSetImageFromDb() {
        firebaseIO.retreieveStringValueByKey(Consts.CHORES_KEY, choreNum, Consts.ABSOLUTE_PATH_KEY + position, new IOnFirebaseKeyValueListeners.OnStringValueListener() {
            @Override
            public void onValueRetrieved(String value) {
                if (value != null) {
                    imgAbsolutePath = value;
                    setPictureToImageView();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

//            protected void retrieveAndSetImageFromDb() {
//        firebaseIO.retreieveStringValueByKey(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, new IOnFirebaseKeyValueListeners.OnStringValueListener() {
//            @Override
//            public void onValueRetrieved(String value) {
//                if(value==null){
//                    return;
//                }
//                if (value.split(":")[0].equals(Consts.LOCAL_URI_PREFIX)) {
//                    imgUri = Uri.parse(value);
//                    setPictureToImageView();
//                } else {
//                    firebaseIO.downloadImg(value, new IOnFirebaseKeyValueListeners.OnStringValueListener() {
//                        @Override
//                        public void onValueRetrieved(String value) {
//                            imgAbsolutePath = value;
//                            setPictureToImageView();
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            if (imgUri != null || imgAbsolutePath != null) {
                onHasPicture();
            }
        }
    }

    @Override
    protected boolean hasResult() {
        if (imgUri != null || imgAbsolutePath != null) {
            return true;
        }
        if(instrctionTextId==R.string.dring_photo_dring_done){
            return true; //if this is the second, result is not required
        }
        return false;
    }

    private void onHasPicture() {
        buttonPhotographFragment.setText(R.string.re_take_picture);
        mListener.enableNext();
    }

    @Override
    protected void saveToDb() {
        firebaseIO.saveIncrementalKeyValuePair(Consts.CHORES_KEY, choreNum, CLICK_NUM_TAG+position, takePicturesClickNum);
        takePicturesClickNum = 0;

        if (imgUri != null) {
            firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.RESULT_KEY_PREFIX + position, imgUri.toString());
        }
        if (imgAbsolutePath != null) {
            firebaseIO.saveKeyValuePair(Consts.CHORES_KEY, choreNum, Consts.ABSOLUTE_PATH_KEY + position, imgAbsolutePath);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
