package co.ff36.pojo;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * Created by tarka on 15/05/2016.
 */
public class TrafficTasks {

    private static ObservableList<Traffic> trafficList;

    private static TrafficTasks ourInstance = new TrafficTasks();

    public static TrafficTasks getInstance() {
        if (trafficList == null) trafficList =  FXCollections.observableArrayList();
        return ourInstance;
    }

    private TrafficTasks() {
    }

    public void addTraffic(Traffic traffic) throws Exception {
        trafficList.add(traffic);
        traffic.run();
    }

    public void deleteTraffic(Traffic traffic) {
        for (Iterator<Traffic> iter = trafficList.listIterator(); iter.hasNext(); ) {
            Traffic t = iter.next();
            if (traffic.getId() == t.getId() && traffic.getStatus() != Traffic.State.Current) {
                iter.remove();
            }
        }
    }

    public void deleteDoneTraffic() {
        for (Iterator<Traffic> iter = trafficList.listIterator(); iter.hasNext(); ) {
            Traffic t = iter.next();
            if (t.isDone()) {
                iter.remove();
            }
        }
    }

    public ObservableList<Traffic> getTraffic() {
        return trafficList;
    }
}
