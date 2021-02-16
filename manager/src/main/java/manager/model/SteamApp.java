package manager.model;

public class SteamApp{
    public final long id;
    public final String title;
    public final Type type;

    SteamApp(final long id, final String title, final Type type){
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public SteamApp(AbstractSteamApp abstractSteamApp){
        id = abstractSteamApp.getId();
        title = abstractSteamApp.getTitle();
        type = abstractSteamApp.getType();
    }

    public String toString(){
        return title;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        final SteamApp steamApp = (SteamApp) o;
        return id == steamApp.id;
    }

    @Override
    public int hashCode(){
        return Long.hashCode(id);
    }

    public enum Type{
        GAME, DLC;

        public static boolean contains(final String s){
            for(Type value : Type.values()){
                if(s.equalsIgnoreCase(value.name())){
                    return true;
                }
            }
            return false;
        }

        public static Type fromString(String s){
            for(Type value : Type.values()){
                if(s.equalsIgnoreCase(value.name())){
                    return value;
                }
            }
            throw new EnumConstantNotPresentException(Type.class, s);
        }
    }

    private interface AbstractSteamApp{
        long getId();

        String getTitle();

        Type getType();
    }

    public static class GLRMSteamAppJson implements AbstractSteamApp{
        private long id;
        private String name;
        private String type;

        @Override
        public long getId(){
            return id;
        }

        @Override
        public String getTitle(){
            return name;
        }

        @Override
        public Type getType(){
            if(Type.contains(type)){
                return Type.fromString(type);
            }else{
                return Type.GAME;
            }
        }
    }
}
