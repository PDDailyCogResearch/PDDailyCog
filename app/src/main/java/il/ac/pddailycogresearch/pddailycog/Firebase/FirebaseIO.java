package il.ac.pddailycogresearch.pddailycog.Firebase;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import il.ac.pddailycogresearch.pddailycog.BuildConfig;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFireBasLoginEventListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseErrorListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseKeyValueListeners;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseQuestionnaireListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseResaveImage;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseSaveImageListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnSuccessListener;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

import static il.ac.pddailycogresearch.pddailycog.utils.Consts.FIREBASE_LOGIN_STATE_IN;
import static il.ac.pddailycogresearch.pddailycog.utils.Consts.FIREBASE_LOGIN_STATE_NOT_AVAILBLE;

/**
 * Created by User on 15/01/2018.
 */

public class FirebaseIO {
    private static final String TAG = FirebaseIO.class.getSimpleName();

    private static FirebaseIO sInstance;

    // DB..
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mUserReference;

    // AUTH..
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private int mCurrentUserLoginState = FIREBASE_LOGIN_STATE_NOT_AVAILBLE;

    private IOnFireBasLoginEventListener mIOnFireBasLoginEventListener;
    private StorageReference mStorageReference;

    private FirebaseIO() {
        database.setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        initAuthListener();
        initUserDatabaseReference();
    }


    public static FirebaseIO getInstance() {
        if (sInstance == null)
            sInstance = new FirebaseIO();

        return sInstance;
    }

    private void initUserDatabaseReference() {
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            String username = email.substring(0, email.indexOf("@"));

            Crashlytics.setUserIdentifier(username);
            mUserReference = database.getReference(Consts.USERS_KEY).child(username);
            mUserReference.keepSynced(true);//because persistence is enable, need to make sure the data is synced with database
            mStorageReference = FirebaseStorage.getInstance().getReference().child(username);
            saveGeneralInfo();
            saveMessagingTokenFromInstance();
        }
    }

    private void saveGeneralInfo() {
        if (mAuth.getCurrentUser() != null) {
            mUserReference.child(Consts.INFO_KEY).child("sdk_int").setValue(Build.VERSION.SDK_INT);
            mUserReference.child(Consts.INFO_KEY).child("os_release").setValue(Build.VERSION.RELEASE);
            mUserReference.child(Consts.INFO_KEY).child("brand").setValue(Build.BRAND);
            mUserReference.child(Consts.INFO_KEY).child("manufacturer").setValue(Build.MANUFACTURER);
            mUserReference.child(Consts.INFO_KEY).child("model").setValue(Build.MODEL);
            mUserReference.child(Consts.INFO_KEY).child("product").setValue(Build.PRODUCT);
            mUserReference.child(Consts.INFO_KEY).child("version_name").setValue(BuildConfig.VERSION_NAME);
        }
    }


    private void initAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mCurrentUserLoginState = FIREBASE_LOGIN_STATE_IN;
                    initUserDatabaseReference();
                    if (mIOnFireBasLoginEventListener != null)
                        mIOnFireBasLoginEventListener.onUserLogin();

                } else {
                    // User is signed out
                    mCurrentUserLoginState = FIREBASE_LOGIN_STATE_NOT_AVAILBLE;

                    if (mIOnFireBasLoginEventListener != null)
                        mIOnFireBasLoginEventListener.onUserLoginError("User Is Signed Out.."); //ask Tal

                }
                // ...
            }
        };
    }

    public void retrieveLastChoreNum(final IOnFirebaseKeyValueListeners.OnIntValueListener listener) {
        Query lastChoreQuery = mUserReference.child(Consts.CHORES_KEY).orderByKey().limitToLast(1);
        lastChoreQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildren().iterator().hasNext()) {
                            DataSnapshot ds = dataSnapshot.getChildren().iterator().next();
                            listener.onValueRetrieved(Integer.valueOf(ds.getKey()));
                        } else {
                            listener.onValueRetrieved(-1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(databaseError.toException());
                    }
                }
        );
    }

    public void retrieveQuestionnaire(int choreNum, final IOnFirebaseQuestionnaireListener firebaseQuestionnaireListener) {
        String key = Consts.QUESTIONNAIRE_KEY;
        if (choreNum > 2) { //backward compatibility = before adding more questionnaires
            key = key + choreNum;
        }
        mUserReference.child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Integer> answers = new ArrayList<Integer>();
                        for (DataSnapshot valSnapshot : dataSnapshot.getChildren()) {
                            answers.add(valSnapshot.getValue(Integer.class));
                        }
                        firebaseQuestionnaireListener.onAnswersRetreived(answers);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        firebaseQuestionnaireListener.onError(databaseError.toException());

                    }
                }
        );
    }

    public void retreieveBooleanValueByKey(final String collection, final int choreNum, final String key, final IOnFirebaseKeyValueListeners.OnBooleanListValueListener listener) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Boolean value = false;
                            if (dataSnapshot.exists()) {
                                value = dataSnapshot.getValue(Boolean.class);
                            }
                            listener.onValueRetrieved(value);
                        } catch (Exception e) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(databaseError.toException());
                    }
                }
        );
    }

    public void retreieveStringValueByKey(final String collection, final int choreNum, final String key, final IOnFirebaseKeyValueListeners.OnStringValueListener listener) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            String value = dataSnapshot.getValue(String.class);
                            listener.onValueRetrieved(value);
                        } catch (Exception e) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(databaseError.toException());
                    }
                }
        );
    }

    public void retreieveIntValueByKey(final String collection, final int choreNum, final String key, final IOnFirebaseKeyValueListeners.OnIntValueListener listener) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Integer value = dataSnapshot.getValue(Integer.class);
                            listener.onValueRetrieved(value);
                        } catch (Exception e) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(databaseError.toException());
                    }
                }
        );
    }

    public void retreieveLongValueByKey(final String collection, final int choreNum, final String key, final IOnFirebaseKeyValueListeners.OnLongValueListener listener) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Long value = dataSnapshot.getValue(Long.class);
                            listener.onValueRetrieved(value);
                        } catch (Exception e) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(databaseError.toException());
                    }
                }
        );
    }

    public void retreieveStringListValueByKey(final String collection, final int choreNum, final String key, final IOnFirebaseKeyValueListeners.OnStringListValueListener listener) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            List<String> items = new ArrayList<String>();
                            for (DataSnapshot valSnapshot : dataSnapshot.getChildren()) {
                                items.add(valSnapshot.getValue(String.class));
                            }
                            listener.onValueRetrieved(items);
                        } catch (Exception e) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(databaseError.toException());
                    }
                }
        );
    }

    public void downloadImg(final String url, final IOnFirebaseKeyValueListeners.OnStringValueListener listener) {
        StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        File localFile = null;

        try {
            localFile = File.createTempFile("images", "jpg");
            final String absolutePath = localFile.getAbsolutePath();

            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    listener.onValueRetrieved(absolutePath);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    listener.onError(exception);
                }
            });
        } catch (IOException e) {
            CommonUtils.onGeneralError(e, TAG);
        }

    }

    public void saveImage(final Uri imageUri, final IOnFirebaseSaveImageListener onFirebaseSaveImageListener) {
        final StorageReference imgRef = mStorageReference.child(imageUri.getLastPathSegment());
        UploadTask uploadTask = imgRef.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return imgRef.getDownloadUrl();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        CommonUtils.onGeneralError(exception, TAG);
                        onFirebaseSaveImageListener.onError(exception.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                onFirebaseSaveImageListener.onImageSaved(uri);
            }

        });
    }

    public void resaveImageByKey(final String collection, final int choreNum, final String imageDbKey, final IOnFirebaseResaveImage iOnFirebaseResaveImage) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(imageDbKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String path = dataSnapshot.getValue(String.class);
                            if (path != null && path.split(":")[0].equals(Consts.LOCAL_URI_PREFIX)) {
                                saveImage(Uri.parse(path),
                                        new IOnFirebaseSaveImageListener() {
                                            @Override
                                            public void onImageSaved(Uri downloadUrl) {
                                                saveKeyValuePair(collection, choreNum, imageDbKey, downloadUrl.toString());
                                                mUserReference.child(collection).child(String.valueOf(choreNum))
                                                        .child(imageDbKey).setValue(downloadUrl.toString(),
                                                        new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                if (databaseError == null) {
                                                                    iOnFirebaseResaveImage.onLogEvent(
                                                                            "success chore: "+ choreNum + " key: "+ imageDbKey);
                                                                    iOnFirebaseResaveImage.onSuccess();
                                                                } else {
                                                                    iOnFirebaseResaveImage.onLogEvent(
                                                                            "failure: " +databaseError.getMessage()+" chore: "+ choreNum + " key: "+ imageDbKey);
                                                                }
                                                            }
                                                        }
                                                );
                                            }

                                            @Override
                                            public void onError(String msg) {
                                                Exception exception = new Exception(msg);
                                                CommonUtils.onGeneralError(exception, TAG);
                                                iOnFirebaseResaveImage.onLogEvent(
                                                        "failure: " +msg+" chore: "+ choreNum + " key: "+ imageDbKey);
                                            }
                                        });
                            } else {
                                iOnFirebaseResaveImage.onSuccess(); //no need to save considers "success"
                            }
                        } else {
                            iOnFirebaseResaveImage.onSuccess(); //no need to save considers "success"
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        CommonUtils.onGeneralError(databaseError.toException(), TAG);
                        iOnFirebaseResaveImage.onLogEvent(
                                "failure: " +databaseError.getMessage()+" chore: "+ choreNum + " key: "+ imageDbKey);
                    }
                }
        );
    }

    public void saveKeyValuePair(String collection, int choreNum, String key, int value) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).setValue(value);
    }

    public void saveKeyValuePair(String collection, int choreNum, String key, String value) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).setValue(value);
    }

    public void saveKeyValuePair(String collection, int choreNum, String key, Boolean value) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).setValue(value);
    }

    public void saveKeyValuePair(String collection, int choreNum, String key, List<String> value) {
        mUserReference.child(collection).child(String.valueOf(choreNum))
                .child(key).setValue(value);
    }

    public void saveIncrementalKeyValuePair(final String collection, final int choreNum, final String key, final long value) {
        mUserReference.child(collection).child(String.valueOf(choreNum)).child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long prevValue = 0;
                        if (dataSnapshot.exists()) {
                            prevValue = dataSnapshot.getValue(Long.class);
                        }
                        long nextValue = prevValue + value;
                        mUserReference.child(collection).child(String.valueOf(choreNum))
                                .child(key).setValue(nextValue);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        CommonUtils.onGeneralError(databaseError.toException(), TAG);
                    }
                }
        );
    }

    public void saveQuestionnaireAnswer(List<Integer> answers, int choreNum) {
        String key = Consts.QUESTIONNAIRE_KEY;
        if (choreNum > 2) { //backward compatibility = before adding more questionnaires
            key = key + choreNum;
        }
        mUserReference.child(key).setValue(answers);
    }


    public void signUpNewUser(Activity activity, String email, String password,
                              final IOnFirebaseErrorListener firebaseErrorListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful() && task.getException() != null) {
                            firebaseErrorListener.onError(task.getException());
                        }

                    }
                });
    }

    public void signInExistingUser(final Activity activity, String email, String password,
                                   final IOnFirebaseErrorListener firebaseErrorListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful() && task.getException() != null) {
                            firebaseErrorListener.onError(task.getException());
                        }
                        // ...
                    }
                });
    }

    public void onLoginActivityStart() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onLoginActivityStop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    public void setIOnFireBaseLoginEventListener(IOnFireBasLoginEventListener iOnFireBaseLoginEventListener) {
        if (iOnFireBaseLoginEventListener != null)
            mIOnFireBasLoginEventListener = iOnFireBaseLoginEventListener;
    }

    public int getCurrentLoginState() {
        return mCurrentUserLoginState;
    }

    public boolean isUserLogged() {
        return mAuth.getCurrentUser() != null;
    }

    public String getUsername() {
        return mAuth.getCurrentUser().getEmail();
    }

    public void logout() {
        mAuth.signOut();
    }

    public boolean isUserStaff() {
        if (mAuth.getCurrentUser().getEmail().charAt(0) == 's')
            return true;
        return false;
    }

    public void saveMessagingToken(String token) {
        mUserReference.child(Consts.INFO_KEY).child(Consts.MESSAGING_TOKEN_KEY).setValue(token);
    }

    private void saveMessagingTokenFromInstance(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            CommonUtils.onGeneralError(task.getException(),TAG);
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        saveMessagingToken(token);
                    }
                });
    }

}
