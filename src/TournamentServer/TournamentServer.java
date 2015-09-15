package TournamentServer;


import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nsifniotis on 9/09/15.
 *
 * The main application in this suite - at long last, it's written! And look,
 * it only needed twenty odd lines of code!
 *
 */
public class TournamentServer
{

    private static void main_loop () {
        // begin by firing up the child process that manages all the game work.
        TournamentThread child = new TournamentThread();
        BlockingQueue<Hermes> messenger = child.GetHermes();
        Scanner in = new Scanner(System.in);

        child.start();

        boolean finished = false;
        while (!finished) {
            System.out.println("Command:");
            String command = in.nextLine();

            Hermes message = new Hermes();
            switch (command)
            {
                case "Q":
                    message.message = Caduceus.END;
                    break;
                case "+":
                    message.message = Caduceus.INC_THREAD_POOL;
                    break;
                case "-":
                    message.message = Caduceus.DEC_THREAD_POOL;
                    break;
                
            }

            messenger.add(message);

            if (command.equals("Q"))
                finished = true;
        }

        // yeah, but you gotta wait for the child process to terminate too.
        while (!child.Finished())
            try
            {
                Thread.sleep(500);
            }
            catch (Exception e)
            {
                // haha, do nothing
            }

        System.out.println ("Child thread terminated ..");
    }

    public static void main(String[] args)
    {
        main_loop();
    }
}
