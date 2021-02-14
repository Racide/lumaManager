package manager.model;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Types;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static manager.model.Globals.moshi;

public class Updater{
    private static final OkHttpClient HttpClient = new OkHttpClient();
    private static final JsonAdapter<List<Release>> jsonAdapter = moshi.adapter(Types.newParameterizedType(List.class,
            Release.class));

    private Updater(){
    }

    public static Optional<String> checkUpdates(){
        final List<Release> releases;
        Request request = new Request.Builder().url(Globals.gitHubUrl)
                                               .addHeader("Accept", "application/vnd.github.v3+json")
                                               .build();

        try(final Response response = HttpClient.newCall(request).execute()){
            if(!response.isSuccessful()){
                Logger.error("http request failed with code {}", response.code());
                return Optional.empty();
            }
            releases = requireNonNull(jsonAdapter.fromJson(requireNonNull(response.body()).source()));
        }catch(NullPointerException ex){
            Logger.error(ex, "response parsing failed");
            return Optional.empty();
        }catch(IOException ex){
            Logger.error(ex, "connection failed");
            return Optional.empty();
        }
        if(releases.size() < 1){
            Logger.error("github returned no releases");
            return Optional.empty();
        }
        if(Globals.version.equals(releases.get(0).version)){
            return Optional.empty();
        }
        return Optional.of(releases.get(0).version);
    }

    private static class Release{
        @Json(name = "tag_name")
        private String version;
    }
}
