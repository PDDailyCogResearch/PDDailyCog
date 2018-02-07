package il.ac.pddailycogresearch.pddailycog.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

import il.ac.pddailycogresearch.pddailycog.model.JsonRadioButton;

/**
 * Created by ggrot on 07/02/2018.
 */

public class ReadJsonUtil {


    public static JsonRadioButton readJsonFile(String path) {
        Gson gson = new Gson();
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return gson.fromJson(br, JsonRadioButton.class);
    }


}
