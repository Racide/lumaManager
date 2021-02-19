package manager.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public interface Globals{
    String version = "v1.3.1";
    String lumaDirName = "AppList";
    File dataFile = new File("data.json");
    int maxSteamApps = 140;
    String gitHubUrl = "https://api.github.com/repos/Racide/lumaManager/releases";
    Moshi moshi = new Moshi.Builder().add(new Globals.FileAdapter())
                                     .add(new Profiles.ProfilesAdapter())
                                     .add(new Profiles.Profile.ProfileAdapter())
                                     .add(new SteamApp.GLRMSteamAppAdapter())
                                     .build();

    /**
     * Copy a file from source to destination.
     *
     * @param source the source
     * @param dest   the destination
     * @return True if succeeded , False if not
     */
    static boolean copyResource(InputStream source, File dest){
        try{
            Files.copy(source, Paths.get(dest.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException ex){
            Logger.error(ex, "failed to write icon to {}", dest.getAbsolutePath());
            return false;
        }
        return true;
    }

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
