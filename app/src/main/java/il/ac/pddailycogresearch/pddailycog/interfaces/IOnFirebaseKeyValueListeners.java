package il.ac.pddailycogresearch.pddailycog.interfaces;

import java.util.List;

/**
 * Created by User on 11/02/2018.
 */

public interface IOnFirebaseKeyValueListeners {
     interface OnStringValueListener{
         void onValueRetrieved(String value);
         void onError(Exception e);
     }
    interface OnIntValueListener{
        void onValueRetrieved(Integer value);
        void onError(Exception e);
    }
    interface OnLongValueListener{
        void onValueRetrieved(Long value);
        void onError(Exception e);
    }

     interface OnStringListValueListener {
         void onValueRetrieved(List<String> value);
         void onError(Exception e);
     }
}
