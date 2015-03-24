package cc.blynk.server.model;

import cc.blynk.server.model.widgets.Widget;
import cc.blynk.server.model.widgets.others.Timer;
import cc.blynk.server.model.widgets.outputs.Graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class DashBoard {

    private int id;

    private String name;

    private Long timestamp;

    private Widget[] widgets;

    private String boardType;

    public Set<Timer> getTimerWidgets() {
        if (widgets == null || widgets.length == 0) {
            return Collections.emptySet();
        }

        Set<Timer> timerWidgets = new HashSet<>();
        for (Widget widget : widgets) {
            if (widget instanceof Timer && widget.value != null && !widget.value.equals("")) {
                timerWidgets.add((Timer) widget);
            }
        }

        return timerWidgets;
    }

    public Set<Byte> getGraphWidgetPins() {
        if (widgets == null || widgets.length == 0) {
            return Collections.emptySet();
        }

        Set<Byte> graphPins = new HashSet<>();
        for (Widget widget : widgets) {
            if (widget instanceof Graph) {
                graphPins.add(widget.pin);
            }
        }

        return graphPins;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Widget[] getWidgets() {
        return widgets;
    }

    public void setWidgets(Widget[] widgets) {
        this.widgets = widgets;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashBoard dashBoard = (DashBoard) o;

        if (id != dashBoard.id) return false;
        if (name != null ? !name.equals(dashBoard.name) : dashBoard.name != null) return false;
        if (!Arrays.equals(widgets, dashBoard.widgets)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (widgets != null ? Arrays.hashCode(widgets) : 0);
        return result;
    }
}
