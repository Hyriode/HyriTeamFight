package fr.hyriode.teamfight.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.mongodb.MongoSerializer;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriStatistics;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.game.TFGameType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 16/05/2023 at 21:14
 */
public class TFStatistics implements IHyriStatistics {

    private final Map<TFGameType, Data> dataMap = new HashMap<>();

    public Map<TFGameType, Data> getData() {
        return this.dataMap;
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<TFGameType, Data> entry : this.dataMap.entrySet()) {
            document.append(entry.getKey().name(), MongoSerializer.serialize(entry.getValue()));
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final MongoDocument dataDocument = MongoDocument.of((Document) entry.getValue());
            final Data data = new Data();

            data.load(dataDocument);

            this.dataMap.put(TFGameType.valueOf(entry.getKey()), data);
        }
    }

    public Data getData(TFGameType gameType) {
        return this.dataMap.merge(gameType, new Data(), (oldValue, newValue) -> oldValue);
    }

    public void update(IHyriPlayer account) {
        account.getStatistics().add(HyriTeamFight.ID, this);
        account.update();
    }

    public static TFStatistics get(IHyriPlayer account) {
        TFStatistics statistics = account.getStatistics().read(HyriTeamFight.ID, new TFStatistics());

        if (statistics == null) {
            statistics = new TFStatistics();
        }
        return statistics;
    }

    public static TFStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    public static class Data implements MongoSerializable {

        private long kills;
        private long deaths;
        private long victories;
        private long gamesPlayed;
        private long roundsWon;

        @Override
        public void save(MongoDocument document) {
            document.append("kills", this.kills);
            document.append("deaths", this.deaths);
            document.append("victories", this.victories);
            document.append("gamesPlayed", this.gamesPlayed);
            document.append("roundsWon", this.roundsWon);
        }

        @Override
        public void load(MongoDocument document) {
            this.kills = document.getLong("kills");
            this.deaths = document.getLong("deaths");
            this.victories = document.getLong("victories");
            this.gamesPlayed = document.getLong("gamesPlayed");
            this.roundsWon = document.getLong("roundsWon");
        }

        public long getKills() {
            return this.kills;
        }

        public void addKills(long kills) {
            this.kills += kills;
        }

        public long getDeaths() {
            return this.deaths;
        }

        public void addDeaths(long deaths) {
            this.deaths += deaths;
        }

        public long getVictories() {
            return this.victories;
        }

        public void addVictories(long victories) {
            this.victories += victories;
        }

        public long getGamesPlayed() {
            return this.gamesPlayed;
        }

        public void addGamesPlayed(long gamesPlayed) {
            this.gamesPlayed += gamesPlayed;
        }

        public long getDefeats() {
            return this.gamesPlayed - this.victories;
        }

        public long getRoundsWon() {
            return this.roundsWon;
        }

        public void addRoundsWon(long roundsWon) {
            this.roundsWon += roundsWon;
        }

    }

}
