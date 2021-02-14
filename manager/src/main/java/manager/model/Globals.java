package manager.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

public interface Globals{
    String version = "v1.0.0";
    String lumaDirName = "AppList";
    File dataFile = new File("data.json");
    int maxSteamApps = 140;
    String gitHubUrl = "https://api.github.com/repos/Racide/lumaManager/releases";
    Moshi moshi = new Moshi.Builder().add(new Globals.FileAdapter())
                                     .add(new Profiles.ProfilesAdapter())
                                     .add(new Profiles.Profile.ProfileAdapter())
                                     .build();
    Font defaultFont = new Font("Segoe UI", Font.PLAIN, 15);
    Color DARK_CHARCOAL = new Color(51, 51, 51);

    class FileAdapter{
        @ToJson
        String toJson(File file){
            return file.getAbsolutePath();
        }

        @FromJson
        File fromJson(String path){
            return new File(path);
        }
    }
}
