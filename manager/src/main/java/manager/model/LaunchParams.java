package manager.model;

import org.tinylog.Logger;

import java.util.Arrays;
import java.util.Optional;

public final class LaunchParams{
    private static volatile LaunchParams instance;
    private final String profile;
    private final boolean decorated;

    public static void createLaunchParams(String[] args){
        instance = new LaunchParams(args);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private LaunchParams(String[] args){
        if(args.length > 0){
            Logger.info("arguments: {}", Arrays.toString(args));
        }
        int i;
        for(i = 0; i < args.length && !args[i].equalsIgnoreCase("--profile"); ++i){
        }
        profile = i + 1 < args.length ? args[i + 1] : null;

        for(i = 0; i < args.length && !args[i].equalsIgnoreCase("--undecorated"); ++i){
        }
        decorated = i >= args.length;
    }

    public static Optional<String> getProfile(){
        return Optional.ofNullable(instance.profile);
    }

    public static boolean isDecorated(){
        return instance.decorated;
    }
}
