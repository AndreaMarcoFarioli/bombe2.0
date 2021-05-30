package bombe2.core;

import bombe2.core.data.ISessionReference;
import bombe2.core.data.Storage;
import bombe2.exceptions.NoSuchSessionException;
import bombe2.exceptions.SessionException;

public abstract class SessionReference implements ISessionReference {
    private boolean opened = true;
    private final SessionManager sessionManager;
    private final String sessionId;
    private Storage storage;

    protected SessionReference(SessionManager sessionManager, String sessionId, Storage storage){
        this.sessionManager = sessionManager;
        this.sessionId = sessionId;
        this.storage = storage;
    }

    protected void setStorage(Storage storage) {
        if (getReference().storage == null)
            this.storage = storage;
    }

    @Override
    public void destroy(){
        getReference().sessionManager.destroySession(sessionId);
        opened = false;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

    @Override
    public final SessionReference getReference(){
        if (!isOpened() || !sessionManager.available(sessionId))
            throw new SessionException("Session " + sessionId + " is closed or does not exist anymore");
        return this;
    }

    @Override
    public Storage getStorage() {
        return getReference().storage;
    }

    @Override
    public String toString() {
        return "SessionReference{" +
                "sessionId='" + sessionId + '\'' +
                '}';
    }

}
