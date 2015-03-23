# Blynk server
Is Netty based Java server responsible for message forwarding between mobile application and any hardware (e.g. Arduino, Raspberry Pi for now).
Please read more detailed description [here](https://www.kickstarter.com/projects/167134865/blynk-build-an-app-for-your-arduino-project-in-5-m/description).

# CI status
[ ![Codeship Status for theblynk/blynk-server](https://codeship.com/projects/58524b50-9b60-0132-68a2-42ce92b0d790/status?branch=master)](https://codeship.com/projects/64160)

# Requirements
Java 8 required. (OpenJDK, Oracle)

# GETTING STARTED
Right now server uses 2 ports. 1 port is used for hardware and second one for the mobile applications. This is done due to the lack of security mechanism and low resources on microcontroller boards (e.g. Arduino UNO).
By default, mobile application uses 8443 port and is based on SSL/TLS sockets. Default hardware port is 8442 and is based on plain TCP/IP sockets.

## Server

+ Run the server on default 'hardware port 8442' and default 'application port 8443' (SSL port)

        java -jar server-{PUT_LATEST_VERSION_HERE}.jar

+ Run the server on custom ports

        java -jar server-{PUT_LATEST_VERSION_HERE}.jar -hardPort 8442 -appPort 8443

## Client

+ Run the application client

        java -jar client-${PUT_LATEST_VERSION_HERE}.jar -mode app -host localhost -port 8443


+ In this client: register new user and/or login with the same credentials

        register username@example.com UserPassword
        login username@example.com UserPassword


+ Save profile with simple dashboard

        saveProfile {"dashBoards":[{"id":1, "name":"My Dashboard", "boardType":"UNO"}]}

+ Activate dashboard

        activate 1

+ Get the token for hardware (e.g Arduino)

        getToken 1


+ You will get server response similar to this:

    	00:05:18.100 TRACE  - Incomming : GetTokenMessage{id=30825, command=GET_TOKEN, length=32, body='33bcbe756b994a6768494d55d1543c74'}

Where `33bcbe756b994a6768494d55d1543c74` is your Auth Token.

+ Start another client (simulates hardware (e.g Arduino)) and use received token to login

    	java -jar client-${PUT_LATEST_VERSION_HERE}.jar -mode hardware -host localhost -port 8442
    	login 33bcbe756b994a6768494d55d1543c74
   

You can run as many clients as you want.

Clients with the same credentials and Auth Token are grouped into one Session and can send messages to each other.
All client’s commands are human-friendly, so you don't have to remember the codes.

Before sending to hardware any read/write commands, application must first send “init” command.
Init command is 'hardware' command that sets all pin modes. Example of init command:

    	hardware pm 1 in 13 out 9 out 8 in

// TODO: take description about pin modes from Blynk Arduino library readme
// TODO Describe separation with Zeroes in pinmode command

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
TODO

### Behind wifi router
In case you need to run Blynk server behind wifi-router and want it to be accessible from internet you have to add port-forwarding rule
on your router. This is required in order to forward all of the requests that come to the router within the local network to Blynk server, 
Im my router it look like this: {image here}

## Licensing
[MIT license](https://bitbucket.org/theblynk/blynk-server/src/c1b06bca3183aba9ea9ed1fad37b856d25cd8a10/license.txt?at=master)