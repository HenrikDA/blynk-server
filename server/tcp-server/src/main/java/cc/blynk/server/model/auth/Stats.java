package cc.blynk.server.model.auth;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/11/2015.
 */
//todo think about migration
public class Stats {

    private final int[] commands;
    private final int[] exceptions;

    public Stats() {
        //21 - is because Commands max value is 20;
        commands = new int[21];
        exceptions = new int[20];
    }

    //do not expect to be incremented from different threads
    public void incr(short cmd) {
        commands[cmd]++;
    }

    public void incrException(int exceptionCode) {
        exceptions[exceptionCode]++;
    }

}
