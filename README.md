# Blynk server
Is Netty based Java server responsible for message forwarding between mobile application and any hardware (e.g. Arduino, Raspberry Pi for now).
Please read more precise project description [here](https://www.kickstarter.com/projects/167134865/blynk-build-an-app-for-your-arduino-project-in-5-m/description).

# CI status
[ ![Codeship Status for theblynk/blynk-server](https://codeship.com/projects/58524b50-9b60-0132-68a2-42ce92b0d790/status?branch=master)](https://codeship.com/projects/64160)

# Requirements
Java 8 required. (OpenJDK, Oracle)

# Protocol messages

Every message consists of 2 parts.

+ Header :
    + Protocol command (1 byte);
    + MessageId (2 bytes);
    + Body message length (2 bytes);

+ Body : string (could be up to 2^15 bytes).

Scheme:

	            	BEFORE DECODE (8 bytes)
	+------------------+------------+---------+------------------------+
	|       1 byte     |  2 bytes   | 2 bytes |    Variable length     |
	+------------------+------------+---------+------------------------+
	| Protocol Command |  MessageId |  Length |  Message body (String) |
	|       0x01       |   0x000A   |  0x0003 |         "1 2"          |
	+------------------+------------+---------+------------------------+
	                                          |        3 bytes         |
    	                                      +------------------------+

So message is always "1 byte + 2 bytes + 2 bytes + messageBody.length".

### Command field
Unsigned byte.
This is 1 byte field responsible for storing [command code](https://bitbucket.org/theblynk/blynk-server/src/a3861b0e9bcb9823bbb6dd2722500c55e197bbe6/common/src/main/java/cc/blynk/common/enums/Command.java?at=master) from client, like login, ping, etc...

### Message Id field
Unsigned short.
Is 2 bytes field for defining unique message identifier. It’s used in order to distinguish how to manage responses from hardware on mobile client. Message ID field should be generated on client’s side.
Any ‘read’ protocol command should always have same messageId for the same widget. Let's say, you have a Graph_1 widget which is configured to read data from the analog pin.
After you reconfigured Graph_1 to read another pin, load command will still look the same, and messageID will be an ID of the widget to display results at.

### Length field
Unsigned short.
Is 2 bytes field for defining body length. Could be 0 if body is empty or missing.



#### Protocol command codes

		0 - response; After every message client sends to server it retrieves response message back (exception commands are: LoadProfile, GetToken, Ping commands).
        1 - register; Must have 2 space-separated params as a content field (username and password) : “username@example.com UserPassword”
        2 - login:
            a) Mobile client must send send 2 space-separated parameters as a content field (username and password) : "username@example.com UserPassword"
            b) Hardware client must send 1 parameter, which is user Authentication Token : "6a7a3151cb044cd893a92033dd65f655"
        3 - save profile; Must have 1 parameter as content string : "{...}"
        4 - load profile; Doesn’t have any parameters
        5 - getToken; Must have 1 signed int (4 bytes) parameter, Dashboard ID : "1". NOTE : number of dashboards is limited per user by 10 and token request should request token for id of existing dashboard, that saved via saveProfile
        6 - ping; Sends request from client to server, then from server to hardware, than back to server and back to the client.
        12 - tweet; Sends tweet request from hardware to server. 140 chars max. 
        20 - hardware; Command for hardware. Every Widget forms it's own body message for hardware command.


#### Hardware command body forming rules
//todo


## Response Codes
For every command client sends to the server it will receive a response back.
For commands (register, login, saveProfile, hardware) that doesn't request any data back - 'response' (command field 0x00) message is returned.
For commands (loadProfile, getToken, ping) that request data back - message will be returned with same command code. In case you sent 'loadProfile' you will receive 'loadProfile' command back with filled body.

[Here is the class with all of the codes](https://bitbucket.org/theblynk/blynk-server/src/251d68546b2ade6651c1393017bf3d1ec4787e6b/common/src/main/java/cc/blynk/common/enums/Response.java?at=master).
Response message structure:

	    	       BEFORE DECODE
	+------------------+------------+----------------+
	|       1 byte     |  2 bytes   |     2 bytes    |
	+------------------+------------+----------------+
	| Protocol Command |  MessageId |  Response code |
	|       0x00       |   0x000A   |      0x0001    |
	+------------------+------------+----------------+
	|               always 5 bytes                   |
	+------------------------------------------------+

    200 - message was successfully processed/passed to the server
    
    2 - command is badly formed, check syntax and passed params
    3 - user is not registered
    4 - user with this name has been registered already
    5 - user haven’t made login command
    6 - user is not allowed to perform this operation (most probably is not logged or socket has been closed)
    7 - arduino board not in network
    8 - command is not supported
    9 - token is not valid
    10 - server error
    11 - user is already logged in. Happens in cases when same user tries to login for more than one time.
    12 - tweet exception, exception occurred during posting request to Twitter could be in case messages are the same in a row;
    13 - tweet body invalid exception; body is empty or larger than 140 chars;
    14 - user has no access token.
    500 - server error. something went wrong on server

## User Profile JSON structure
	{ "dashBoards" : 
		[ 
			{
			 "id":1, "name":"My Dashboard", "isActive":true, "timestamp":333333,
			 "widgets"  : [...], 
			 "settings" : {"boardType":"UNO", ..., "someParam":"someValue"}
			}
		],
		"twitterAccessToken" : {
		    "token" : "USER_TOKEN",
		    "tokenSecret" : "USER_SECRET"
		}
	}

## Widget types

    //output
    BUTTON,
    TOGGLE_BUTTON,
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

    //input
    LED,
    DIGIT4_DISPLAY, //same as NUMERICAL_DISPLAY
    GAUGE,
    LCD_DISPLAY,
    GRAPH,
    LEVEL_DISPLAY,

    //sensors
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

[Or see the class itself](https://bitbucket.org/theblynk/blynk-server/src/251d68546b2ade6651c1393017bf3d1ec4787e6b/server/tcp-server/src/main/java/cc/blynk/server/model/enums/WidgetType.java?at=master)

## Widgets JSON structure

	Button				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"BUTTON",         "pinType":"NONE", "pin":13, "value":"1"   } -- sends HIGH on digital pin 13. Possible values 1|0.
	Toggle Button ON	: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TOGGLE_BUTTON",  "pinType":"DIGITAL", "pin":18, "value":"1", "state":"ON"} -- sends 1 on digital pin 18
	Toggle Button OFF	: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TOGGLE_BUTTON",  "pinType":"VIRTUAL", "pin":18, "value":"0", "state":"OFF"} -- sends 0 on digital pin 18
	Slider				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"SLIDER",         "pinType":"ANALOG",  "pin":18, "value":"244" } -- sends 244 on analog pin 18. Possible values -9999 to 9999
	Timer				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"TIMER",          "pinType":"DIGITAL", "pin":13, "startTime" : 1111111111, "stopInterval" : 111111111} -- startTime is time in UTC when to start timer (milliseconds are ignored), stopInterval interval in milliseconds after which stop timer.

	//pin reading widgets
	LED					: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"LED",            "pinType":"DIGITAL", "pin":10} - sends READ pin to server
	Digit Display		: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"DIGIT4_DISPLAY", "pinType":"DIGITAL", "pin":10} - sends READ pin to server
	Graph				: {"id":1, "x":1, "y":1, "dashBoardId":1, "label":"Some Text", "type":"GRAPH",          "pinType":"DIGITAL", "pin":10, "readingFrequency":1000} - sends READ pin to server. Frequency in microseconds

## Commands order processing
Server guarantees that commands will be processed in same order in which they were send.

## GETTING STARTED

+ Run the server

        java -jar server-${PUT_LATEST_VERSION_HERE}.jar -port 8080


+ Run the client (simulates smartphone client, app client should always connect to SSL port)

        java -jar client-${PUT_LATEST_VERSION_HERE}.jar -host localhost -sslPort 8443 -mode app


+ In this client: register new user and/or login with the same credentials

        register username@example.com UserPassword
        login username@example.com UserPassword


+ Save profile with simple dashboard

        saveProfile {"dashBoards":[{"id":1, "name":"My Dashboard", "isActive":true}]}


+ Get the token for hardware (e.g Arduino)

        getToken 1


+ You will get server response similar to this:

    	00:05:18.100 TRACE  - Incomming : GetTokenMessage{id=30825, command=GET_TOKEN, length=32, body='33bcbe756b994a6768494d55d1543c74'}

Where `33bcbe756b994a6768494d55d1543c74` is your Auth Token.

+ Start another client (simulates hardware (e.g Arduino)) and use received token to login

    	java -jar client.jar -host localhost -port 8080
    	login 33bcbe756b994a6768494d55d1543c74
   

You can run as many clients as you want.

Clients with the same credentials and Auth Token are grouped into one Session and can send messages to each other.
All client’s commands are human-friendly, so you don't have to remember the codes.

Before sending to hardware any read/write commands, application must first send “init” command.
Init command is 'hardware' command that sets all pin modes. Example of init command:

    	hardware pm 1 in 13 out 9 out 8 in

// TODO: take description about pin modes from Blynk Arduino library readme

In the example above, you set pin 1 and pin 8 to 'input’ PIN_MODE. This means this pins will read values from hardware (graph, display, etc).
Pins 13 and 9 have 'output’ PIN_MODE. This means that these pins will we writable (button, slider).

Hardware commands:

+ Digital write:

    	hardware dw 9 1
    	hardware dw 9 0


+ Digital read:

    	hardware dr 9
    	You should receive response: dw 9 <val>


+ Analog write:

    	hardware aw 14 123


+ Analog read:

    	hardware ar 14
        You should receive response: aw 14 <val>


+ Virtual write:

    	hardware vw 9 1234
        hardware vw 9 string
        hardware vw 9 item1 item2 item3
        hardware vw 9 key1 val1 key2 val2


+ Virtual read:

    	hardware vr 9
    	You should receive response: vw 9 <values>

Registered users are stored locally in TMP dir of your system in file "user.db". So after the restart you don't have to register again.


## Local server setup

### Behind wifi router
In case you need to run Blynk server behind wifi-router and want it to be accessible from internet you have to add port-forwarding rule
on your router. This is required in order to forward all of the requests that come to the router within the local network to Blynk server, 
Im my router it look like this: {image here}

## Licensing
[MIT license](https://bitbucket.org/theblynk/blynk-server/src/c1b06bca3183aba9ea9ed1fad37b856d25cd8a10/license.txt?at=master)