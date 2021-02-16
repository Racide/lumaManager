package manager.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static manager.model.Data.*;

public class Profiles extends AbstractListModel<Profiles.Profile> implements ComboBoxModel<Profiles.Profile>{
    private final List<Profile> profiles;
    private Profile selectedProfile;

    public Profiles(){
        profiles = new ArrayList<>();
        profiles.add(new Profile());
    }

    private Profiles(List<Profile> profiles){
        this.profiles = new ArrayList<>(profiles);
    }

    // @Override
    public void addProfile(Profile profile){
        wLock();
        modelOperation(() -> {
            int index = profiles.indexOf(profile);
            if(index == -1){
                index = profiles.size();
                profiles.add(profile);
            }
            fireIntervalAdded(this, index, index);
            for(SteamApp steamApp : profile.steamApps){
                getElementAt(index).addSteamApp(steamApp);
            }
        });
        wUnlock();
    }

    // @Override
    public void removeProfile(int index){
        wLock();
        modelOperation(() -> {
            if(profiles.size() > 1){
                profiles.remove(index);
                fireIntervalRemoved(this, index, index);
            }
        });
        wUnlock();
    }

    @Override
    public Profile getElementAt(int index){
        rLock();
        Profile profile = profiles.get(index);
        rUnlock();
        return profile;
    }

    @Override
    public int getSize(){
        rLock();
        int size = profiles.size();
        rUnlock();
        return size;
    }

    // ↓ EDT ONLY ↓

    @Override
    public void setSelectedItem(Object anItem){
        if(anItem == null){
            selectedProfile = getElementAt(0);
        }else{
            selectedProfile = (Profile) anItem;
        }
    }

    @Override
    public Object getSelectedItem(){
        return selectedProfile;
    }

    public static class Profile extends AbstractListModel<SteamApp>{
        private final String name;
        private final List<SteamApp> steamApps;

        public Profile(String name){
            this.name = name;
            steamApps = new ArrayList<>();
        }

        public Profile(AbstractProfile abstractProfile){
            name = abstractProfile.getName();
            steamApps = new ArrayList<>(abstractProfile.getSteamapps());
        }

        private Profile(){
            name = "Default";
            steamApps = new ArrayList<>();
        }

        public List<SteamApp> getSteamApps(){
            return new LinkedList<>(steamApps);
        }

        public void addSteamApp(SteamApp steamApp){
            wLock();
            modelOperation(() -> {
                if(steamApps.size() < Globals.maxSteamApps && !steamApps.contains(steamApp)){
                    steamApps.add(steamApp);
                    steamApps.sort(Comparator.comparing(o -> o.title));
                    int index = steamApps.indexOf(steamApp);
                    fireIntervalAdded(this, index, index);
                }
            });
            wUnlock();
        }

        public void removeSteamApp(int index){
            wLock();
            modelOperation(() -> {
                steamApps.remove(index);
                fireIntervalRemoved(this, index, index);
            });
            wUnlock();
        }

        @Override
        public SteamApp getElementAt(int index){
            rLock();
            SteamApp steamApp = steamApps.get(index);
            rUnlock();
            return steamApp;
        }

        @Override
        public int getSize(){
            rLock();
            int size = steamApps.size();
            rUnlock();
            return size;
        }

        public String toString(){
            return name;
        }

        @Override
        public boolean equals(Object o){
            if(this == o){
                return true;
            }
            if(o == null || getClass() != o.getClass()){
                return false;
            }
            final Profile profile = (Profile) o;
            return name.equalsIgnoreCase(profile.name);
        }

        @Override
        public int hashCode(){
            return Objects.hash(name);
        }

        private interface AbstractProfile{
            String getName();

            List<SteamApp> getSteamapps();
        }

        public static class ProfileAdapter{
            @ToJson
            ProfileJson toJson(Profile profile){
                return new ProfileJson(profile);
            }

            @FromJson
            Profile fromJson(ProfileJson profileJson){
                return new Profile(profileJson);
            }

            private static class ProfileJson implements AbstractProfile{
                private final String name;
                private final List<SteamApp> steamApps;

                ProfileJson(Profile profile){
                    name = profile.name;
                    steamApps = profile.steamApps;
                }

                @Override
                public String getName(){
                    return name;
                }

                @Override
                public List<SteamApp> getSteamapps(){
                    return steamApps;
                }
            }
        }

        public static class GLRMProfileJson implements AbstractProfile{
            private String name;
            private List<SteamApp.GLRMSteamAppJson> games;

            @Override
            public String getName(){
                return name;
            }

            @Override
            public List<SteamApp> getSteamapps(){
                final List<SteamApp> steamApps = new LinkedList<>();
                for(SteamApp.GLRMSteamAppJson game : games){
                    steamApps.add(new SteamApp(game));
                }
                return steamApps;
            }
        }
    }

    private static void modelOperation(Runnable operation){
        if(SwingUtilities.isEventDispatchThread()){
            operation.run();
        }else{
            try{
                SwingUtilities.invokeAndWait(operation);
            }catch(InvocationTargetException | InterruptedException ex){
                throw new RuntimeException(ex);
            }
        }
    }

    public static class ProfilesAdapter{
        @ToJson
        List<Profile> toJson(Profiles profiles){
            return profiles.profiles;
        }

        @FromJson
        Profiles fromJson(List<Profile> profiles){
            return new Profiles(profiles);
        }
    }
}
