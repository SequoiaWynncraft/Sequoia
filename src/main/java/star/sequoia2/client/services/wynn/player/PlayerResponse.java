package star.sequoia2.client.services.wynn.player;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import star.sequoia2.accessors.TeXParserAccessor;

import java.util.List;
import java.util.Map;

public class PlayerResponse implements TeXParserAccessor {
    private String username;
    private boolean online;
    private String server;
    private String activeCharacter;
    private String nickname;
    private String uuid;
    private String rank;
    private String rankBadge;
    private LegacyRankColour legacyRankColour;
    private String shortenedRank;
    private String supportRank;
    private boolean veteran;
    private String firstJoin;
    private String lastJoin;
    private float playtime;
    private Guild guild;
    private GlobalData globalData;
    private Integer forumLink;
    private Map<String, Integer> ranking;
    private Map<String, Integer> previousRanking;
    private boolean publicProfile;
    private Map<String, Character> characters;

    public static class LegacyRankColour {
        private String main;
        private String sub;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }
    }

    public static class Guild {
        private String uuid;
        private String name;
        private String prefix;
        private String rank;
        private String rankStars;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getRankStars() {
            return rankStars;
        }

        public void setRankStars(String rankStars) {
            this.rankStars = rankStars;
        }
    }

    public class GlobalData {
        private int wars;
        private int totalLevels;
        private int killedMobs;
        private int chestsFound;
        private Dungeons dungeons;
        private Raids raids;
        private int completedQuests;
        private PvP pvp;

        public int getWars() {
            return wars;
        }

        public void setWars(int wars) {
            this.wars = wars;
        }

        public int getTotalLevels() {
            return totalLevels;
        }

        public void setTotalLevels(int totalLevels) {
            this.totalLevels = totalLevels;
        }

        public int getKilledMobs() {
            return killedMobs;
        }

        public void setKilledMobs(int killedMobs) {
            this.killedMobs = killedMobs;
        }

        public int getChestsFound() {
            return chestsFound;
        }

        public void setChestsFound(int chestsFound) {
            this.chestsFound = chestsFound;
        }

        public Dungeons getDungeons() {
            return dungeons;
        }

        public void setDungeons(Dungeons dungeons) {
            this.dungeons = dungeons;
        }

        public Raids getRaids() {
            return raids;
        }

        public void setRaids(Raids raids) {
            this.raids = raids;
        }

        public int getCompletedQuests() {
            return completedQuests;
        }

        public void setCompletedQuests(int completedQuests) {
            this.completedQuests = completedQuests;
        }

        public PvP getPvp() {
            return pvp;
        }

        public void setPvp(PvP pvp) {
            this.pvp = pvp;
        }

        public static class Dungeons {
            private int total;
            private Map<String, Integer> list;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public Map<String, Integer> getList() {
                return list;
            }

            public void setList(Map<String, Integer> list) {
                this.list = list;
            }

        }

        public class Raids {
            private static final Map<String, String> RAID_NAME_TO_RAID_LEADERBOARD_MAP = Map.of(
                    "The Canyon Colossus", "colossusCompletion",
                    "Orphion's Nexus of Light", "orphionCompletion",
                    "The Nameless Anomaly", "namelessCompletion",
                    "Nest of the Grootslangs", "grootslangCompletion");

            private int total;
            private Map<String, Integer> list;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public Map<String, Integer> getList() {
                return list;
            }

            public void setList(Map<String, Integer> list) {
                this.list = list;
            }

            public MutableText toPrettyMessage(Map<String, Integer> ranking) {
                MutableText result = Text.empty();

                if (list == null || list.isEmpty()) {
                    return result;
                }

                list.forEach((raidName, count) -> {
                    boolean raidHasCompletionLeaderboard = RAID_NAME_TO_RAID_LEADERBOARD_MAP.containsKey(raidName);
                    result.append(teXParser().parseMutableText(I18n.translate("sequoia.command.playerRaids.showingRaidLeaderboards",
                            raidName, count,
                            raidHasCompletionLeaderboard ? String.format("\\ranking{%d}",
                                    ranking.get(RAID_NAME_TO_RAID_LEADERBOARD_MAP.get(raidName))) : ""))).append("\n");
                });

                if (!result.getSiblings().isEmpty()) {
                    result.getSiblings().removeLast();
                }

                return result;
            }

        }

        public static class PvP {
            private int kills;
            private int deaths;

            public int getKills() {
                return kills;
            }

            public void setKills(int kills) {
                this.kills = kills;
            }

            public int getDeaths() {
                return deaths;
            }

            public void setDeaths(int deaths) {
                this.deaths = deaths;
            }
        }
    }

    public static class Character {
        private String nickname;
        private String type;
        private int level;
        private long xp;
        private int xpPercent;
        private int totalLevel;
        private int wars;
        private float playtime;
        private long mobsKilled;
        private int chestsFound;
        private long blocksWalked;
        private int itemsIdentified;
        private int logins;
        private int deaths;
        private int discoveries;
        private PvP pvp;
        private List<String> gamemode;
        private SkillPoints skillPoints;
        private Map<String, Profession> professions;
        private Dungeons dungeons;
        private Raids raids;
        private List<String> quests;

        public static class PvP {
            private int kills;
            private int deaths;

            public int getKills() {
                return kills;
            }

            public void setKills(int kills) {
                this.kills = kills;
            }

            public int getDeaths() {
                return deaths;
            }

            public void setDeaths(int deaths) {
                this.deaths = deaths;
            }
        }

        public static class SkillPoints {
            private int strength;
            private int dexterity;
            private int intelligence;
            private int defence;
            private int agility;

            public int getStrength() {
                return strength;
            }

            public void setStrength(int strength) {
                this.strength = strength;
            }

            public int getDexterity() {
                return dexterity;
            }

            public void setDexterity(int dexterity) {
                this.dexterity = dexterity;
            }

            public int getIntelligence() {
                return intelligence;
            }

            public void setIntelligence(int intelligence) {
                this.intelligence = intelligence;
            }

            public int getDefence() {
                return defence;
            }

            public void setDefence(int defence) {
                this.defence = defence;
            }

            public int getAgility() {
                return agility;
            }

            public void setAgility(int agility) {
                this.agility = agility;
            }
        }

        public static class Profession {
            private int level;
            private int xpPercent;

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public int getXpPercent() {
                return xpPercent;
            }

            public void setXpPercent(int xpPercent) {
                this.xpPercent = xpPercent;
            }
        }

        public static class Dungeons {
            private int total;
            private Map<String, Integer> list;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public Map<String, Integer> getList() {
                return list;
            }

            public void setList(Map<String, Integer> list) {
                this.list = list;
            }
        }

        public static class Raids {
            private int total;
            private Map<String, Integer> list;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public Map<String, Integer> getList() {
                return list;
            }

            public void setList(Map<String, Integer> list) {
                this.list = list;
            }
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public long getXp() {
            return xp;
        }

        public void setXp(int xp) {
            this.xp = xp;
        }

        public int getXpPercent() {
            return xpPercent;
        }

        public void setXpPercent(int xpPercent) {
            this.xpPercent = xpPercent;
        }

        public int getTotalLevel() {
            return totalLevel;
        }

        public void setTotalLevel(int totalLevel) {
            this.totalLevel = totalLevel;
        }

        public int getWars() {
            return wars;
        }

        public void setWars(int wars) {
            this.wars = wars;
        }

        public float getPlaytime() {
            return playtime;
        }

        public void setPlaytime(float playtime) {
            this.playtime = playtime;
        }

        public long getMobsKilled() {
            return mobsKilled;
        }

        public void setMobsKilled(int mobsKilled) {
            this.mobsKilled = mobsKilled;
        }

        public int getChestsFound() {
            return chestsFound;
        }

        public void setChestsFound(int chestsFound) {
            this.chestsFound = chestsFound;
        }

        public long getBlocksWalked() {
            return blocksWalked;
        }

        public void setBlocksWalked(int blocksWalked) {
            this.blocksWalked = blocksWalked;
        }

        public int getItemsIdentified() {
            return itemsIdentified;
        }

        public void setItemsIdentified(int itemsIdentified) {
            this.itemsIdentified = itemsIdentified;
        }

        public int getLogins() {
            return logins;
        }

        public void setLogins(int logins) {
            this.logins = logins;
        }

        public int getDeaths() {
            return deaths;
        }

        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }

        public int getDiscoveries() {
            return discoveries;
        }

        public void setDiscoveries(int discoveries) {
            this.discoveries = discoveries;
        }

        public PvP getPvp() {
            return pvp;
        }

        public void setPvp(PvP pvp) {
            this.pvp = pvp;
        }

        public List<String> getGamemode() {
            return gamemode;
        }

        public void setGamemode(List<String> gamemode) {
            this.gamemode = gamemode;
        }

        public SkillPoints getSkillPoints() {
            return skillPoints;
        }

        public void setSkillPoints(SkillPoints skillPoints) {
            this.skillPoints = skillPoints;
        }

        public Map<String, Profession> getProfessions() {
            return professions;
        }

        public void setProfessions(Map<String, Profession> professions) {
            this.professions = professions;
        }

        public Dungeons getDungeons() {
            return dungeons;
        }

        public void setDungeons(Dungeons dungeons) {
            this.dungeons = dungeons;
        }

        public Raids getRaids() {
            return raids;
        }

        public void setRaids(Raids raids) {
            this.raids = raids;
        }

        public List<String> getQuests() {
            return quests;
        }

        public void setQuests(List<String> quests) {
            this.quests = quests;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getActiveCharacter() {
        return activeCharacter;
    }

    public void setActiveCharacter(String activeCharacter) {
        this.activeCharacter = activeCharacter;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRankBadge() {
        return rankBadge;
    }

    public void setRankBadge(String rankBadge) {
        this.rankBadge = rankBadge;
    }

    public LegacyRankColour getLegacyRankColour() {
        return legacyRankColour;
    }

    public void setLegacyRankColour(LegacyRankColour legacyRankColour) {
        this.legacyRankColour = legacyRankColour;
    }

    public String getShortenedRank() {
        return shortenedRank;
    }

    public void setShortenedRank(String shortenedRank) {
        this.shortenedRank = shortenedRank;
    }

    public String getSupportRank() {
        return supportRank;
    }

    public void setSupportRank(String supportRank) {
        this.supportRank = supportRank;
    }

    public boolean isVeteran() {
        return veteran;
    }

    public void setVeteran(boolean veteran) {
        this.veteran = veteran;
    }

    public String getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(String firstJoin) {
        this.firstJoin = firstJoin;
    }

    public String getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(String lastJoin) {
        this.lastJoin = lastJoin;
    }

    public float getPlaytime() {
        return playtime;
    }

    public void setPlaytime(float playtime) {
        this.playtime = playtime;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public GlobalData getGlobalData() {
        return globalData;
    }

    public void setGlobalData(GlobalData globalData) {
        this.globalData = globalData;
    }

    public Integer getForumLink() {
        return forumLink;
    }

    public void setForumLink(Integer forumLink) {
        this.forumLink = forumLink;
    }

    public Map<String, Integer> getRanking() {
        return ranking;
    }

    public void setRanking(Map<String, Integer> ranking) {
        this.ranking = ranking;
    }

    public Map<String, Integer> getPreviousRanking() {
        return previousRanking;
    }

    public void setPreviousRanking(Map<String, Integer> previousRanking) {
        this.previousRanking = previousRanking;
    }

    public boolean isPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(boolean publicProfile) {
        this.publicProfile = publicProfile;
    }

    public Map<String, Character> getCharacters() {
        return characters;
    }

    public void setCharacters(Map<String, Character> characters) {
        this.characters = characters;
    }

    public MutableText rankingPrettyMessage(Map<String, Integer> ranking, Integer results) {
        StringBuilder TeX = new StringBuilder();
        if (ranking == null || ranking.isEmpty()) return Text.empty();

        int limit = results == null ? Integer.MAX_VALUE : results;

        ranking.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limit)
                .forEach(e -> {
                    String type     = e.getKey();
                    int    position = e.getValue();

                    TeX.append(String.format("\\n\\+{%s}\\-{:} \\2{#%d}",
                            I18n.translate("wynncraft.leaderboard." + type), position));
                });

        return teXParser().parseMutableText(TeX.toString());
    }
}