package co.ff36.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tarka on 15/05/2016.
 */
public class SettingUtil {

    public static final String AWS_BUCKET_KEY = "aws_bucket";
    public static final String AWS_PUBLIC_KEY = "aws_key";
    public static final String AWS_PRIVATE_KEY = "aws_secret";
    public static final String DOWNLOAD_FILE_KEY = "download_file";

    public Map<String, String> load() {
        Map<String, String> result = new HashMap<>();
        File file = new File(System.getProperty("user.home") + "/.codebox/settings");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            List<String> contents = FileUtils.readLines(file);
            for (String line : contents) {
                String[] split = line.split("=");
                result.put(split[0], split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void save(Map<String,String> settings) {
        File file = new File(System.getProperty("user.home") + "/.codebox/settings");
        List<String> contents = new ArrayList<>();
        contents.add(AWS_BUCKET_KEY + "=" + settings.get(AWS_BUCKET_KEY));
        contents.add(AWS_PUBLIC_KEY + "=" + settings.get(AWS_PUBLIC_KEY));
        contents.add(AWS_PRIVATE_KEY + "=" + settings.get(AWS_PRIVATE_KEY));
        contents.add(DOWNLOAD_FILE_KEY + "=" + settings.get(DOWNLOAD_FILE_KEY));
        try {
            FileUtils.writeLines(file, contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
