package de.tum.in.tumcampusapp.ui.roomfinder;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Suggestion provider for {@link RoomFinderActivity}
 */
public class RoomFinderSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "de.tum.in.tumcampusapp.component.tumui.roomfinder.RoomFinderSuggestionProvider";

    public RoomFinderSuggestionProvider() {
        setupSuggestions(AUTHORITY, 1);
    }
}