package Services.GameViewer;

/**
 * Created by nsifniotis on 12/10/15.
 *
 * Simple viewer GIU for the viewing of games.
 */

import AcademicsInterface.IViewer;
import Common.LogManager;
import Services.Logs.LogType;
import TournamentServer.DataModelInterfaces.Game;
import TournamentServer.DataModelInterfaces.PlayerSubmission;
import TournamentServer.DataModelInterfaces.Tournament;
import TournamentServer.GameManagerChild;
import TournamentServer.PlayerManager;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameViewer extends Application
{
    private IViewer viewer_controller;
    private GameManagerChild current_game;


    /**
     * Nick Sifniotis u5809912
     * 12/10/2015
     *
     * Create the scene, link it to the IViewer, and set up the animation.
     *
     * @param primaryStage - the stage to play on
     */
    @Override
    public void start(Stage primaryStage)
    {
        viewer_controller = new Viewer();
        viewer_controller.InitialiseViewer(primaryStage);
        viewer_controller.NewGame(null, null);
        primaryStage.show();

        current_game = get_next_game();
        this.AdvanceGame();
    }


    public GameManagerChild get_next_game()
    {
        // load the current state of the tournaments, and all playable games connected to them.
        int[] tournament_keys = Tournament.LoadKeys();
        Game[] games = Game.LoadAll(tournament_keys);
        int game_counter = 0;

        GameManagerChild success = null;
        while (success == null && game_counter < games.length)
        {
            // let's play a game
            success = launch_game(games[game_counter]);
            game_counter ++;
        }

        return success;
    }


    private GameManagerChild launch_game (Game game)
    {
        Tournament tournament = new Tournament(game.TournamentKey());
        LogManager.Log(LogType.TOURNAMENT, "Attempting to launch game " + game.PrimaryKey() + " in tournament " + tournament.Name());
        PlayerSubmission[] players = game.Players();

        // games only launch one at a time. So it's fair to assume that all players that are ready
        // to play now will still be ready to play in a couple of milliseconds.
        boolean lets_play = true;
        for (PlayerSubmission player: players)
            lets_play &= player.ReadyToPlay();

        if (!lets_play || players.length != tournament.NumPlayers())
        {
            LogManager.Log(LogType.TOURNAMENT, "Failed to launch game - not enough players reporting ready: " + lets_play + ":" + players.length);
            return null;
        }


        PlayerManager[] player_managers = new PlayerManager[players.length];
        try
        {
            for (int i = 0; i < players.length; i++)
            {
                players[i].StartGame();
                player_managers[i] = new PlayerManager(tournament, players[i]);
            }
        }
        catch (Exception e)
        {
            String error = "Error launching game " + game.PrimaryKey() + ". " + e;
            LogManager.Log(LogType.ERROR, error);
            return null;
        }

        GameManagerChild res = new GameManagerChild(game, tournament.GameEngine(), player_managers, tournament.UseNullMoves());

        LogManager.Log(LogType.TOURNAMENT, "Game started!");
        return res;
    }


    public void update_state()
    {
        if (current_game == null)
            this.viewer_controller.Update(null);
        else
            this.viewer_controller.Update(current_game.CurrentState());
    }

    /**
     * Nick Sifniotis u5809912
     * 12/10/2015
     *
     * @param v - the IViewer implementation to attach to this window.
     */
    public void SetViewer(IViewer v)
    {
        viewer_controller = v;
    }


    public void UpdateGame(GameManagerChild game_state)
    {
        LogManager.Log(LogType.ERROR, "Update called with game state :" + game_state.toString());
        current_game = game_state;
    }


    /**
     * Nick Sifniotis u5809912
     * 12/10/2015
     *
     * Animation event handler. Redraw the screen.
     */
    private void AdvanceGame()
    {
        Task<Boolean> task = new Task<Boolean>()
        {
            @Override
            public Boolean call()
            {
               // LogManager.Log(LogType.ERROR, "Updating GUI with " + current_state);
                if (current_game != null) {
                    current_game.Advance();
                    return current_game.GameOn();
                }
                else
                    return false;
            }
        };

        task.setOnSucceeded(e -> {
            Boolean result = task.getValue();
            // update UI with result
            if (result)
                viewer_controller.Update(current_game.CurrentState());

            try
            {
                Thread.sleep (2000);
            }
            catch (Exception ee)
            {
                return;
            }
            if (result)
                this.AdvanceGame();
            else {
                current_game = get_next_game();
                this.AdvanceGame();
            }
        });

        new Thread(task).start();
    }
}
