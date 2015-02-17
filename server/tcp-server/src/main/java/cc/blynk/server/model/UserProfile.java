package cc.blynk.server.model;

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

    private transient Map<Integer, Set<Byte>> graphPins;

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

    public boolean hasGraphPin(int dashId, byte pin) {
        return graphPins.get(dashId).contains(pin);
    }

    public TwitterAccessToken getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public void setTwitterAccessToken(TwitterAccessToken twitterAccessToken) {
        this.twitterAccessToken = twitterAccessToken;
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
