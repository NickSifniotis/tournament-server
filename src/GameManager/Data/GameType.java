package GameManager.Data;

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


    public int ID() { return this.data_source.id; }
    public String Name() { return this.data_source.name; }
    public String EngineClassName() { return this.data_source.engine_class; }
    public String ViewerClassName() { return this.data_source.viewer_class; }
    public boolean UsesViewer() { return this.data_source.uses_viewer; }
    public int MinPlayers() { return this.data_source.min_players; }
    public int MaxPlayers() { return this.data_source.max_players; }
}
