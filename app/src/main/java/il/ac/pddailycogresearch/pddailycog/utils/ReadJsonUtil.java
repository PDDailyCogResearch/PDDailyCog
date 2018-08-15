package il.ac.pddailycogresearch.pddailycog.utils;

import android.app.Activity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.model.JsonRadioButton;

/**
 * Created by ggrot on 07/02/2018.
 */

public class ReadJsonUtil {


    private static final String TAG = ReadJsonUtil.class.getSimpleName();

    public static JsonRadioButton readRadioJsonFile(Activity activity, String filename) {
        Gson gson = new Gson();
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(
//                    new FileReader(path));
//        } catch (FileNotFoundException e) {
//            CommonUtils.onGeneralError(e,TAG);
//        }
        String json = loadJSONFromAsset(activity, filename + ".json");
        if (json != null) {
            return gson.fromJson(json, JsonRadioButton.class);
        }
        return null;
    }

    public static String readInstruction(Activity activity, int choreNum, int position) {
        Gson gson = new Gson();
        String filepath = Consts.ASSETS_PREFIX + choreNum + Consts.INSTRUCTION_FILENAME + ".json";
        String json = loadJSONFromAsset(activity, filepath);
        if(json!=null) {
            Map<String, String> map = new HashMap<String, String>();
            map = gson.fromJson(json,map.getClass());
            String instrc = map.get(String.valueOf(position));
            if(instrc!=null) {
                return instrc;
            }
        }
        return activity.getString(R.string.some_error);
    }

    private static String loadJSONFromAsset(Activity activity, String filename) {
        String json;
        try {
            InputStream is = activity.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            CommonUtils.onGeneralError(ex,TAG);
            return null;
        }
        return json;
    }


}
