package cc.blynk.server.model.widgets;

import cc.blynk.server.model.enums.PinType;
import cc.blynk.server.model.enums.State;
import cc.blynk.server.model.widgets.controls.*;
import cc.blynk.server.model.widgets.inputs.Accelerometer;
import cc.blynk.server.model.widgets.inputs.GPS;
import cc.blynk.server.model.widgets.inputs.Gyroscope;
import cc.blynk.server.model.widgets.inputs.Microphone;
import cc.blynk.server.model.widgets.others.*;
import cc.blynk.server.model.widgets.outputs.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:08
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({

        //controls
        @JsonSubTypes.Type(value = Button.class, name = "BUTTON"),
        @JsonSubTypes.Type(value = Slider.class, name = "SLIDER"),
        @JsonSubTypes.Type(value = SliderLarge.class, name = "SLIDER_LARGE"),
        @JsonSubTypes.Type(value = Knob.class, name = "KNOB"),
        @JsonSubTypes.Type(value = RotaryKnob.class, name = "ROTARY_KNOB"),
        @JsonSubTypes.Type(value = RGB.class, name = "RGB"),
        @JsonSubTypes.Type(value = TwoWayArrow.class, name = "TWO_WAY_ARROW"),
        @JsonSubTypes.Type(value = FourWayArrow.class, name = "FOUR_WAY_ARROW"),
        @JsonSubTypes.Type(value = OneAxisJoystick.class, name = "ONE_AXIS_JOYSTICK"),
        @JsonSubTypes.Type(value = TwoAxisJoystick.class, name = "TWO_AXIS_JOYSTICK"),
        @JsonSubTypes.Type(value = Gamepad.class, name = "GAMEPAD"),
        @JsonSubTypes.Type(value = Keypad.class, name = "KEYPAD"),

        //outputs
        @JsonSubTypes.Type(value = LED.class, name = "LED"),
        @JsonSubTypes.Type(value = Digit4Display.class, name = "DIGIT4_DISPLAY"),
        @JsonSubTypes.Type(value = Gauge.class, name = "GAUGE"),
        @JsonSubTypes.Type(value = LCDDisplay.class, name = "LCD_DISPLAY"),
        @JsonSubTypes.Type(value = Graph.class, name = "GRAPH"),
        @JsonSubTypes.Type(value = LevelDisplay.class, name = "LEVEL_DISPLAY"),

        //inputs
        @JsonSubTypes.Type(value = Microphone.class, name = "MICROPHONE"),
        @JsonSubTypes.Type(value = Gyroscope.class, name = "GYROSCOPE"),
        @JsonSubTypes.Type(value = Accelerometer.class, name = "ACCELEROMETER"),
        @JsonSubTypes.Type(value = GPS.class, name = "GPS"),

        //others
        @JsonSubTypes.Type(value = Terminal.class, name = "TERMINAL"),
        @JsonSubTypes.Type(value = Twitter.class, name = "TWITTER"),
        @JsonSubTypes.Type(value = Email.class, name = "EMAIL"),
        @JsonSubTypes.Type(value = Notification.class, name = "NOTIFICATION"),
        @JsonSubTypes.Type(value = SDCard.class, name = "SD_CARD"),
        @JsonSubTypes.Type(value = Eventor.class, name = "EVENTOR"),
        @JsonSubTypes.Type(value = RCT.class, name = "RCT"),
        @JsonSubTypes.Type(value = Timer.class, name = "TIMER")

})
public abstract class Widget {

    public long id;

    public int x;

    public int y;

    public Integer width;

    public Integer height;

    public String label;

    public PinType pinType;

    public Byte pin;

    //todo is it used?
    public State state;

    //todo is it used?
    public String value;

}
