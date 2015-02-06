package cc.blynk.server.model;

import cc.blynk.server.utils.JsonParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class UserProfile {

    private DashBoard[] dashBoards;

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
