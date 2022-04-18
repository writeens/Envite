package interfaces;

public interface VolleyCallbackForAdapters {
    void onSuccess(String status);
    void onError(String message, String type, String status);
}
