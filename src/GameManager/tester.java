package GameManager;

import AcademicsInterface.IGameEngine;
import Common.DataModel.Tournament;

/**
 * Created by nsifniotis on 7/09/15.
 */
public class tester {

    public static void main(String[] args) {

        Tournament [] tourneys = Tournament.LoadAll();

        if (tourneys.length == 0)
            return;

        IGameEngine engine = null;
        try {
            engine = (IGameEngine)tourneys[0].GameEngineClass().newInstance();
        }
        catch (Exception e)
        {
            System.out.println (e);
        }

        if (engine == null)
            return;

        System.out.println (engine.InitialiseGame(4));
    }
}
