package cc.blynk.server.model;

import cc.blynk.server.model.enums.WidgetType;

import java.util.*;

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

    private Map<String, String> settings;

    public Set<Widget> getTimerWidgets() {
        if (widgets == null || widgets.length == 0) {
            return Collections.emptySet();
        }

        Set<Widget> timerWidgets = new HashSet<>();
        for (Widget widget : widgets) {
            if (widget.getType() == WidgetType.TIMER) {
                timerWidgets.add(widget);
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
            if (widget.getType() == WidgetType.GRAPH) {
                graphPins.add(widget.getPin());
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

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
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
        if (settings != null ? !settings.equals(dashBoard.settings) : dashBoard.settings != null) return false;
        if (!Arrays.equals(widgets, dashBoard.widgets)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (widgets != null ? Arrays.hashCode(widgets) : 0);
        result = 31 * result + (settings != null ? settings.hashCode() : 0);
        return result;
    }
}
