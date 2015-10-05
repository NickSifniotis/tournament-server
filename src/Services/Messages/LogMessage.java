package Services.Messages;

import Services.Logs.LogType;

/**
 * Created by nsifniotis on 5/10/15.
 *
 * Quick and dirty message object for the log service.
 *
 * This should be self explanatory
 *
 */
public class LogMessage extends Message
{
    private String msg;
    private LogType type;
    private int game_id;

    public LogMessage(LogType t, String s)
    {
        this.type = t;
        this.msg = s;
        this.game_id = -1;
    }

    public LogMessage(LogType t, int g, String s)
    {
        this(t, s);
        this.game_id = g;
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * Accessor functions.
     *
     * @return bits of data.
     */
    public String Message() { return this.msg; }
    public boolean Logging() { return this.type.Logging(); }
    public String LogPath() { return this.type.LogPath(); }
    public int Game() { return this.game_id; }
    public LogType LogType() { return this.type; }
}
