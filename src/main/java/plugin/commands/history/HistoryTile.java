package plugin.commands.history;

import arc.struct.Seq;

public class HistoryTile {
    public Seq<HistoryObject> objectSeq = new Seq<>();
    public final float tileX;
    public final float tileY;

    public HistoryTile(float tileX, float tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }
}
