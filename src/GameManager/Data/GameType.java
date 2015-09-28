package GameManager.Data;

import AcademicsInterface.IGameEngine;
import AcademicsInterface.IViewer;

/**
 * Created by nsifniotis on 28/09/15.
 *
 * Read/write interface for the GameType DMO
 */
public class GameType
{
    private Common.DataModelObject.GameType data_source;


    public GameType (Common.DataModelObject.GameType input)
    {
        this.data_source = input;
    }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Accessor functions.
     *
     * @return data
     */
    public int ID() { return this.data_source.id; }
    public String Name() { return this.data_source.name; }
    public String EngineClassName() { return this.data_source.engine_class; }
    public String ViewerClassName() { return this.data_source.viewer_class; }
    public boolean UsesViewer() { return this.data_source.uses_viewer; }
    public int MinPlayers() { return this.data_source.min_players; }
    public int MaxPlayers() { return this.data_source.max_players; }
    public IGameEngine GameEngineClass() { return this.data_source.GameEngine(); }
    public IViewer ViewerClass() { return this.data_source.Viewer(); }
    public String SourceFilename() { return this.data_source.SourceFilename(); }


    /**
     * Nick Sifniotis u5809912
     * 28/09/2015
     *
     * Setter functions.
     * @param name - etc, various datas
     */
    public void SetName (String name) { this.data_source.name = name; }
    public void SetGameEngineClass (String engine_class) { this.data_source.engine_class = engine_class; }
    public void SetViewerClass (String viewer_class) { this.data_source.viewer_class = viewer_class; }
    public void SetUsesViewer (boolean uses) { this.data_source.uses_viewer = uses; }
    public void SetMinPlayers (int min) { this.data_source.min_players = min; }
    public void SetMaxPlayers (int max) { this.data_source.max_players = max; }
}
