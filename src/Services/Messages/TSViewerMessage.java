package Services.Messages;

import Services.GameViewer.GameViewer;


/**
 * Created by nsifniotis on 13/10/15.
 *
 * Passes the IViewer stage along to the tournament server.
 */
public class TSViewerMessage extends TSMessage
{
    public GameViewer the_stage;


    public TSViewerMessage(GameViewer s, int tourney_id)
    {
        super(TSMessageType.ADD_VIEWER, tourney_id);
        the_stage = s;
    }
}
