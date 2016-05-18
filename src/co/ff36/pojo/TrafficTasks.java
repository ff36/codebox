package co.ff36.pojo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * A singleton instance handling a collection of all the traffic tasks in their respective threads.
 *
 * Created by tarka on 15/05/2016.
 */
public class TrafficTasks {

    private static ObservableList<Traffic> trafficList;

    private static TrafficTasks ourInstance = new TrafficTasks();

    /**
     * Get the single instance of TrafficTasks.
     * @return The application wide single instance of TrafficTasks
     */
    public static TrafficTasks getInstance() {
        if (trafficList == null) trafficList =  FXCollections.observableArrayList();
        return ourInstance;
    }

    private TrafficTasks() {
    }

    /**
     * Add a new traffic event to the collection.
     * @param traffic The traffic event to add,
     * @throws Exception
     */
    public void addTraffic(Traffic traffic) throws Exception {
        trafficList.add(traffic);
        traffic.run();
    }

    /**
     * Remove a traffic event from the collection
     * @param traffic The traffic event to remove
     */
    public void deleteTraffic(Traffic traffic) {
        for (Iterator<Traffic> iter = trafficList.listIterator(); iter.hasNext(); ) {
            Traffic t = iter.next();
            if (traffic.getId() == t.getId() && traffic.getStatus() != Traffic.State.Current) {
                iter.remove();
            }
        }
    }

    /**
     * Remove all traffic events that are complete from the collection.
     */
    public void deleteDoneTraffic() {
        for (Iterator<Traffic> iter = trafficList.listIterator(); iter.hasNext(); ) {
            Traffic t = iter.next();
            if (t.isDone()) {
                iter.remove();
            }
        }
    }

    /**
     * Get all the traffic events in the collection
     * @return The traffic event collection.
     */
    public ObservableList<Traffic> getTraffic() {
        return trafficList;
    }
}
