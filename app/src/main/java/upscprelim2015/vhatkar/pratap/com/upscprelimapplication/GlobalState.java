package upscprelim2015.vhatkar.pratap.com.upscprelimapplication;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by pratap on 5/5/15.
 */
public class GlobalState extends Application {

    private static final String PROPERTY_ID = "UA-54976552-2";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public GlobalState() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {

        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            if (trackerId == TrackerName.GLOBAL_TRACKER) {
                mTrackers.put(trackerId, analytics.newTracker(R.xml.global_tracker));
            }

        }

        return mTrackers.get(trackerId);
    }
}
