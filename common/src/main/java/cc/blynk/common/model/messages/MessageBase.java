package cc.blynk.common.model.messages;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public abstract class MessageBase {

    //1 + 2 + 2
    public static final int HEADER_LENGTH = 5;

    public short command;

    public int id;

    public int length;

    public MessageBase(int id, short command, int length) {
        this.command = command;
        this.id = id;
        this.length = length;
    }

    public int getByteLength() {
        return HEADER_LENGTH;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", command=" + command +
                ", length=" + length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageBase that = (MessageBase) o;

        if (command != that.command) return false;
        if (id != that.id) return false;
        if (length != that.length) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) command;
        result = 31 * result + id;
        result = 31 * result + length;
        return result;
    }
}
