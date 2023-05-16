package fr.hyriode.teamfight.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializer;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;
import fr.hyriode.teamfight.HyriTeamFight;
import org.bson.Document;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 16/05/2023 at 21:14
 */
public class TFData implements IHyriPlayerData {

    private TFHotBar hotBar = new TFHotBar();

    @Override
    public void save(MongoDocument document) {
        document.append("hotBar", MongoSerializer.serialize(this.hotBar));
    }

    @Override
    public void load(MongoDocument document) {
        this.hotBar.load(new MongoDocument(document.get("hotBar", Document.class)));
    }

    public TFHotBar getHotBar() {
        return this.hotBar == null ? this.hotBar = new TFHotBar() : this.hotBar;
    }

    public static TFData get(UUID uuid) {
        TFData data = IHyriPlayer.get(uuid).getData().read(HyriTeamFight.ID, new TFData());

        if (data == null) {
            data = new TFData();
        }
        return data;
    }

    public void update(IHyriPlayer account) {
        account.getData().add(HyriTeamFight.ID, this);
        account.update();
    }

}
