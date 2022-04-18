package interfaces;

import entities.Envite;
import entities.EnviteRequest;
import entities.User;

public interface VolleyCallbackForEnviteDetails {
    void onSuccess(String status,Envite envite, User createdByUser, EnviteRequest enviteRequest );
    void onError(String message, String type, String status);
}
