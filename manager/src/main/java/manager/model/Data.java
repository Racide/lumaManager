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
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final JsonAdapter<Data> jsonAdapter = moshi.adapter(Data.class).nonNull();
    private String ignoreVersion;
    public File outputDir;
    public final Profiles profiles;

    public static Data loadData() throws IOException{
        return jsonAdapter.fromJson(Okio.buffer(Okio.source(Globals.dataFile)));
    }

    public static Data createData(File outputDir){
        Data data = new Data();
        data.setOutputDir(outputDir);
        return data;
    }

    private Data(){
        ignoreVersion = Globals.version;
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

    public void setIgnoreVersion(String ignoreVersion){
        wLock();
        this.ignoreVersion = ignoreVersion;
        wUnlock();
    }

    public void setOutputDir(File outputDir){
        String outputPath = outputDir.getAbsolutePath();
        if(!outputDir.getName().equalsIgnoreCase(Globals.lumaDirName)){
            outputPath += File.separator + Globals.lumaDirName;
        }
        wLock();
        this.outputDir = new File(outputPath);
        wUnlock();
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
