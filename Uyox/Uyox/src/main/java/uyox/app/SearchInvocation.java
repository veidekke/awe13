package uyox.app;

import android.util.Log;

import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;

/**
 * Created by eikebehrends on 21.07.14.
 */
public class SearchInvocation extends ActionInvocation {
    private static String TAG = "SearchInvocation";

    SearchInvocation(Service service, String searchCriteria) {
        super(service.getAction("Search"));
        try {
            setInput("ContainerID", "0");
            setInput("SearchCriteria", searchCriteria);
            setInput("Filter", "*");
            setInput("StartingIndex", "0");
            setInput("RequestedCount", "0");
            setInput("SortCriteria", null);
        } catch (InvalidValueException ex) {
            Log.d(TAG, "error calling action");
        }
    }
}