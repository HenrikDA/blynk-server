package cc.blynk.server.model;

import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.model.widgets.Widget;
import cc.blynk.server.utils.JsonParser;

import java.util.*;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class UserProfile {

    private DashBoard[] dashBoards;

    private TwitterAccessToken twitterAccessToken;

    private Map<Integer, Set<Byte>> graphPins;

    //@JsonIgnore
    private transient Integer activeDashId;

    public void validateDashId(int dashBoardId, int msgId) {
        if (dashBoards == null) {
            throw new IllegalCommandException(String.format("Requested token for non-existing '%d' dash id.", dashBoardId), msgId);
        }
        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.getId() == dashBoardId) {
                return;
            }
        }

        throw new IllegalCommandException(String.format("Requested token for non-existing '%d' dash id.", dashBoardId), msgId);
    }

    public DashBoard[] getDashBoards() {
        return dashBoards;
    }

    public void setDashBoards(DashBoard[] dashBoards) {
        this.dashBoards = dashBoards;
    }

    public Set<Widget> getDashboardTimerWidgets() {
        if (dashBoards == null || dashBoards.length == 0) {
            return Collections.emptySet();
        }

        Set<Widget> timers = new HashSet<>();
        for (DashBoard dashBoard : dashBoards) {
            timers.addAll(dashBoard.getTimerWidgets());
        }

        return timers;
    }

    public void calcGraphPins() {
        if (dashBoards == null || dashBoards.length == 0) {
            graphPins = Collections.emptyMap();
            return;
        }

        graphPins = new HashMap<>();

        for (DashBoard dashBoard : dashBoards) {
            graphPins.put(dashBoard.getId(), dashBoard.getGraphWidgetPins());
        }
    }

    public boolean hasGraphPin(Integer dashId, Byte pin) {
        Set<Byte> pins = graphPins.get(dashId);
        return pins != null && pins.contains(pin);
    }

    public Map<Integer, Set<Byte>> getGraphPins() {
        return graphPins;
    }

    public void setGraphPins(Map<Integer, Set<Byte>> graphPins) {
        this.graphPins = graphPins;
    }

    public TwitterAccessToken getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public void setTwitterAccessToken(TwitterAccessToken twitterAccessToken) {
        this.twitterAccessToken = twitterAccessToken;
    }

    public Integer getActiveDashId() {
        return activeDashId;
    }

    public void setActiveDashId(Integer activeDashId) {
        this.activeDashId = activeDashId;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfile that = (UserProfile) o;

        if (!Arrays.equals(dashBoards, that.dashBoards)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dashBoards != null ? Arrays.hashCode(dashBoards) : 0;
    }
}
