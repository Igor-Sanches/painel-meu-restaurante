package app.birdsoft.painelmeurestaurante.manager;

public interface Loader {
    void load();
    void setRefreshing(boolean refreshing);
    void finishMode();
}
