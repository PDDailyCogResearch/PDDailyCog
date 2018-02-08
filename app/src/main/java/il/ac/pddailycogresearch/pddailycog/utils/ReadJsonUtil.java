package il.ac.pddailycogresearch.pddailycog.utils;

import android.app.Activity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import il.ac.pddailycogresearch.pddailycog.model.JsonRadioButton;

/**
 * Created by ggrot on 07/02/2018.
 */

public class ReadJsonUtil {


    public static JsonRadioButton readRadioJsonFile(Activity activity, String filename) {
        Gson gson = new Gson();
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(
//                    new FileReader(path));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        String json = loadJSONFromAsset(activity, filename + ".json");
        if (json != null) {
            return gson.fromJson(json, JsonRadioButton.class);
        }
        return null;
    }

    private static String loadJSONFromAsset(Activity activity, String filename) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}
