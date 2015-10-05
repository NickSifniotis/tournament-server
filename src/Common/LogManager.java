package Common;

import Services.LogService;
import Services.Logs.LogType;
import Services.Messages.LogMessage;
import Services.Messages.TerminateMessage;

/**
 * Created by nsifniotis on 5/10/15.
 *
 * Static class that contains the methods for adding messages to the system log thread.
 */
public class LogManager
{
    private static LogService service;
    private static boolean service_active = false;


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * StartService() and EndService() are called by TournamentManager and no other thing.
     *
     */
    public static void StartService()
    {
        service = new LogService();
        service.start();
        service_active = true;
        Log (LogType.TOURNAMENT, "Log service started!");
    }

    public static void EndService()
    {
        Log (LogType.TOURNAMENT, "Stopping log service. This will be the last message from me this session. Goodbye!");
        service.MessageQueue().add(new TerminateMessage());
        service_active = false;
    }


    /**
     * Nick Sifniotis u5809912
     * 05/10/2015
     *
     * These publicly accessible methods allow any part of the system to access the system log resource
     * If the service is inactive, the methods do nothing.
     *
     * @param type - the type of message
     * @param message - the message itself
     */
    public static void Log (LogType type, String message)
    {
        if (service_active)
            service.MessageQueue().add(new LogMessage(type, message));
    }

    public static void GameLog (int game_id, String message)
    {
        if (service_active)
            service.MessageQueue().add(new LogMessage(LogType.GAME, game_id, message));
    }
}
