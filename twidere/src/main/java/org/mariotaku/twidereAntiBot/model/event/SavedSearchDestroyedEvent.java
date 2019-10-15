package org.mariotaku.twidereAntiBot.model.event;

import org.mariotaku.twidereAntiBot.model.UserKey;

/**
 * Created by mariotaku on 16/2/26.
 */
public class SavedSearchDestroyedEvent {
    private final UserKey mAccountKey;
    private final long searchId;

    public SavedSearchDestroyedEvent(UserKey accountKey, long searchId) {
        this.mAccountKey = accountKey;
        this.searchId = searchId;
    }

    public UserKey getAccountKey() {
        return mAccountKey;
    }

    public long getSearchId() {
        return searchId;
    }
}
