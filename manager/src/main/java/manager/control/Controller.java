package manager.control;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.squareup.moshi.JsonAdapter;
import manager.model.Crawler;
import manager.model.Data;
import manager.model.Globals;
import manager.model.Profiles;
import manager.model.SearchResults;
import manager.model.SteamApp;
import manager.model.Updater;
import manager.view.Gui;
import manager.view.Settings;
import mslinks.ShellLink;
import okio.BufferedSink;
import okio.Okio;
import org.tinylog.Logger;

import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static manager.model.Globals.moshi;

public final class Controller implements Gui.Listener, Settings.Listener{
    private final Gui.Controller gui = Gui.create(this);
    private Data data;

    public Controller(){
        gui.initialize();
        initializeData();
        gui.dataLoaded();
        checkUpdates();
    }

    @Override
    public void search(final String query){
        CompletableFuture.runAsync(() -> {
            try{
                gui.setSearchResults(Crawler.search(query));
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
            gui.setSearchResults(new SearchResults(SearchResults.Status.ERROR));
        });
    }

    @Override
    public void close(){
        try{
            data.serialize();
        }catch(IOException ex){
            Logger.error(ex, "serialization failed");
            gui.writeError(Globals.dataFile);
        }
    }

    private void initializeData(){
        try{
            data = Data.loadData();
        }catch(NullPointerException | IOException ex){
            Logger.info(ex, "serialized data not found or unreadable");
            Optional<File> file = gui.folderChooser(null);
            if(file.isPresent()){
                data = Data.createData(file.get());
            }else{
                System.exit(0);
            }
        }
        gui.setProfiles(data.getLastProfile(), data.profiles);
    }

    private void checkUpdates(){
        CompletableFuture.runAsync(() -> Updater.checkUpdates().ifPresent((newVersion) -> {
            switch(gui.updateAvailable(newVersion, !newVersion.equals(data.getIgnoreVersion()))){
                case JOptionPane.YES_OPTION:
                    if(Desktop.isDesktopSupported()){
                        try{
                            Desktop.getDesktop().browse(new URI(Globals.gitHubLatestRelease));
                        }catch(IOException | URISyntaxException ex){
                            Logger.error(ex, "failed to open hyperlink");
                        }
                    }else{
                        Logger.error("unsupported Desktop class");
                    }
                    break;
                case JOptionPane.NO_OPTION:
                    data.setIgnoreVersion(newVersion);
                    break;
            }
        }));
    }

    public void settings(){
        Settings.Controller settings = gui.createSettings(this);
        settings.populate(data.getOutputDir());
        settings.initialize();
    }

    // ↓ EDT ONLY ↓

    @Override
    public void addGames(Profiles.Profile profile, SearchResults searchResults, int[] indices){
        Arrays.stream(indices).mapToObj(searchResults::getSteamApp).forEach(profile::addSteamApp);
    }

    @Override
    public void delGames(Profiles.Profile profile, int[] indices){
        Arrays.stream(indices).forEachOrdered(profile::removeSteamApp);
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
    public void setOutputFile(Settings.Controller settings){
        Optional<File> file = gui.folderChooser(data.getOutputDir());
        file.ifPresent((value) -> {
            data.setOutputDir(value);
            settings.populate(data.getOutputDir());
        });
    }

    @Override
    public void importGLRMProfile(Settings.Controller settings){
        gui.fileChooser(null, ".json").ifPresent((file) -> {
            final JsonAdapter<Profiles.Profile.GLRMProfileJson> jsonAdapter = moshi.adapter(Profiles.Profile.GLRMProfileJson.class)
                                                                                   .nonNull();
            try{
                Profiles.Profile.GLRMProfileJson glrmProfileJson = Objects.requireNonNull(jsonAdapter.fromJson(Okio.buffer(
                        Okio.source(file))));
                data.profiles.addProfile(new Profiles.Profile(glrmProfileJson));
            }catch(NullPointerException | IOException ex){
                Logger.error(ex, "failed to import GLRM profile");
                gui.readError(file);
            }
        });
    }

    @Override
    public void createShortcut(Settings.Controller settings){
        final File workingDir = new File(".");
        final File logo = new File("logo.ico");
        gui.fileSave(new File("GreenLuma Manager.lnk")).ifPresent((shortcut) -> {
            if(!logo.exists()){
                try{
                    Globals.copyResource(Controller.class.getResourceAsStream("/logo.ico"), logo);
                }catch(IOException ex){
                    Logger.error("failed to copy icon to {}", logo.getAbsolutePath());
                    gui.writeError(logo);
                    return;
                }
            }
            try{
                final File jar = new File(Controller.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                final ShellLink sl = ShellLink.createLink(jar.getAbsolutePath())
                                              .setWorkingDir(workingDir.getAbsolutePath())
                                              .setIconLocation(logo.getAbsolutePath());
                sl.saveTo(shortcut.getAbsolutePath());
            }catch(URISyntaxException ex){
                Logger.error(ex, "failed to get current jar path");
                throw new RuntimeException(ex);
            }catch(IOException ex){
                Logger.error(ex, "failed to write shortcut file to {}", shortcut.getAbsolutePath());
                gui.writeError(shortcut);
            }
        });
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
                try(BufferedSink sink = Okio.buffer(Okio.sink(new File(outputPath, +i + ".txt")))){
                    sink.writeUtf8(steamApp.id + "");
                    i++;
                }
            }
        }catch(IOException ex){
            Logger.error(ex, "generation failed");
            gui.writeError(outputDir);
        }
        data.setLastProfile(profile);
    }
}
