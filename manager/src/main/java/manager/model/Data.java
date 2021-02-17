package manager.model;

import com.squareup.moshi.JsonAdapter;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static manager.model.Globals.moshi;

public class Data{
    private static final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private static final JsonAdapter<Data> jsonAdapter = moshi.adapter(Data.class).nonNull().indent("    ");
    private String lastProfile = "Default";
    private String ignoreVersion = Globals.version;
    private File outputDir;
    public final Profiles profiles;

    public static Data loadData() throws NullPointerException, IOException{
        return jsonAdapter.fromJson(Okio.buffer(Okio.source(Globals.dataFile)));
    }

    public static Data createData(File outputDir){
        Data data = new Data();
        data.setOutputDir(outputDir);
        return data;
    }

    private Data(){
        profiles = new Profiles();
    }

    public static void rLock(){
        lock.readLock().lock();
    }

    public static void rUnlock(){
        lock.readLock().unlock();
    }

    public static void wLock(){
        lock.writeLock().lock();
    }

    public static void wUnlock(){
        lock.writeLock().unlock();
    }

    public void setLastProfile(Profiles.Profile profile){
        wLock();
        lastProfile = profile.name;
        wUnlock();
    }

    public void setIgnoreVersion(String ignoreVersion){
        wLock();
        this.ignoreVersion = ignoreVersion;
        wUnlock();
    }

    public void setOutputDir(File outputDir){
        if(!outputDir.getName().equalsIgnoreCase(Globals.lumaDirName)){
            outputDir = new File(outputDir, Globals.lumaDirName);
        }
        wLock();
        this.outputDir = outputDir;
        wUnlock();
    }

    public Profiles.Profile getLastProfile(){
        wLock();
        final Profiles.Profile profile = profiles.getFromName(this.lastProfile).orElseGet(() -> {
            final Profiles.Profile p = profiles.getElementAt(0);
            setLastProfile(p);
            return p;
        });
        wUnlock();
        return profile;
    }

    public String getIgnoreVersion(){
        rLock();
        String ignoreVersion = this.ignoreVersion;
        rUnlock();
        return ignoreVersion;
    }

    public File getOutputDir(){
        rLock();
        File outputDir = this.outputDir;
        rUnlock();
        return outputDir;
    }

    public void serialize() throws IOException{
        rLock();
        try(BufferedSink buffer = Okio.buffer(Okio.sink(Globals.dataFile))){
            jsonAdapter.toJson(buffer, this);
        }
        rUnlock();
    }
}
