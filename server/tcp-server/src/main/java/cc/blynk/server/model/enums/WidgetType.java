package cc.blynk.server.model.enums;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:12
 */
public enum WidgetType {

    //controls
    BUTTON,
    SLIDER,
    SLIDER_LARGE,
    KNOB,
    ROTARY_KNOB,
    RGB,
    TWO_WAY_ARROW,
    FOUR_WAY_ARROW,
    ONE_AXIS_JOYSTICK,
    TWO_AXIS_JOYSTICK,
    GAMEPAD,
    KEYPAD,

    //outputs
    LED,
    DIGIT4_DISPLAY, //same as NUMERICAL_DISPLAY
    GAUGE,
    LCD_DISPLAY,
    GRAPH,
    LEVEL_DISPLAY,

    //inputs
    MICROPHONE,
    GYROSCOPE,
    ACCELEROMETER,
    GPS,

    //other
    TERMINAL,
    TWITTER,
    EMAIL,
    NOTIFICATION,
    SD_CARD,
    EVENTOR,
    RCT,
    TIMER

}
