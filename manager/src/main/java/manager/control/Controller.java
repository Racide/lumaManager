package manager.control;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import manager.model.Crawler;
import manager.model.Data;
import manager.model.Profiles;
import manager.model.SearchResults;
import manager.model.SteamApp;
import manager.model.Updater;
import manager.view.Gui;
import okio.BufferedSink;
import okio.Okio;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Controller implements Gui.Listener{
    private final Gui.Controller gui = Gui.create(this);
    private Data data;

    public Controller(){
        gui.initialize();
        gui.setLoading(true);
        initializeData();
        gui.setLoading(false);
        checkUpdate();
    }

    @Override
    public void search(final String query){
        CompletableFuture.runAsync(() -> {
            try{
                gui.setSearchResults(new SearchResults(Crawler.search(query)));
                return;
            }catch(FailingHttpStatusCodeException ex){
                Logger.error(ex, "page responded with error {}", ex.getStatusCode());
                gui.failingHttpStatusCodeError(ex.getStatusCode());
            }catch(ElementNotFoundException ex){
                Logger.error(ex, "result table not found");
                gui.elementNotFoundError();
            }catch(IOException ex){
                Logger.error(ex, "connection error");
                gui.connectionError();
            }
            gui.setSearchResults(new SearchResults());
        });
    }

    @Override
    public void serialize(){
        try{
            data.serialize();
        }catch(IOException ex){
            Logger.error(ex, "serialization failed");
            gui.serializationError();
        }
    }

    private void initializeData(){
        try{
            data = Data.loadData();
        }catch(IOException ex){
            Logger.info(ex, "serialized data not found or unreadable");
            Optional<File> file = gui.folderChooser(null);
            if(file.isPresent()){
                data = Data.createData(file.get());
            }else{
                System.exit(0);
            }
        }
        gui.setProfiles(data.profiles);
    }

    private void checkUpdate(){
        CompletableFuture.runAsync(() -> Updater.checkUpdates().ifPresent((newVersion) -> {
            if(gui.oldVersion(newVersion, !newVersion.equals(data.getIgnoreVersion()))){
                data.setIgnoreVersion(newVersion);
            }
        }));
    }

    // ↓ EDT ONLY ↓

    @Override
    public void addGames(Profiles.Profile profile, Stream<SteamApp> steamAppStream){
        steamAppStream.forEach(profile::addSteamApp);
    }

    @Override
    public void delGames(Profiles.Profile profile, IntStream steamAppIndexStream){
        steamAppIndexStream.forEachOrdered(profile::removeSteamApp);
    }

    @Override
    public void addProfile(String name){
        data.profiles.addProfile(new Profiles.Profile(name));
    }

    @Override
    public void delProfile(int index){
        data.profiles.removeProfile(index);
    }

    @Override
    public void setOutputFile(){
        Optional<File> file = gui.folderChooser(data.getOutputDir());
        file.ifPresent(value -> data.setOutputDir(value));
    }

    public void generate(Profiles.Profile profile){
        File outputDir = data.getOutputDir();
        String outputPath = outputDir.getAbsolutePath();
        try{
            File[] oldFiles = outputDir.listFiles();
            if(oldFiles != null){
                for(File file : oldFiles){
                    if(!file.isDirectory()){
                        if(!file.delete()){
                            IOException ex = new IOException("Failed to delete " + file.getName());
                            Logger.error(ex, "failed to delete old file {}", file.getAbsolutePath());
                            throw ex;
                        }
                    }
                }
            }else{
                Logger.info("path is inaccessible, required folders will be created");
                if(!outputDir.mkdirs()){
                    IOException ex = new IOException("Failed to create required folders");
                    Logger.error(ex, "failed to create required output folders");
                    throw ex;
                }
            }
            int i = 0;
            for(SteamApp steamApp : profile.getSteamApps()){
                try(BufferedSink sink = Okio.buffer(Okio.sink(new File(outputPath + File.separator + i + ".txt")))){
                    sink.writeUtf8(steamApp.id + "");
                    i++;
                }
            }
        }catch(IOException ex){
            Logger.error(ex, "generation failed");
            gui.generationError(outputPath);
        }
    }
}
