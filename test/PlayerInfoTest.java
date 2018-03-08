import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class PlayerInfoTest {

    // Test two conflicting queries
    @Test
    public void testResolvePlayerInfo() {
        UUID uuid = UUID.fromString("3657b9cc-2518-4265-ad69-323e11286ce2");
        PlayerStat statType = PlayerStat.ARROWS_SHOT;

        PlayerInfo playerInfo = new PlayerInfo(uuid);
        PlayerInfo playerInfo2 = new PlayerInfo(uuid);

        Query queryA = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 5);

        Query queryB = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 6);

        playerInfo.addRow(statType, queryA);
        playerInfo2.addRow(statType, queryB);

        PlayerInfo nonConflictingPlayerInfo = playerInfo.resolveConflicts(playerInfo2);

        List<Query> nonConflictingQueries = nonConflictingPlayerInfo.getDataOfPlayerStat(statType);

        // Verify that size is correct
        Assert.assertEquals(1, nonConflictingQueries.size());

        // Verify that there is only 1 row
        Assert.assertEquals(1, nonConflictingPlayerInfo.getNumberOfRows(statType));

        // Verify that there is only 1 statistic stored
        Assert.assertEquals(1, nonConflictingPlayerInfo.getNumberOfStatistics());

        // Verify that value is correct
        Assert.assertEquals(11, nonConflictingPlayerInfo.getTotalValue(statType), 0);
    }

    // Test with two conflicting and one non-conflicting query
    @Test
    public void testResolvePlayerInfo2() {
        UUID uuid = UUID.fromString("3657b9cc-2518-4265-ad69-323e11286ce2");
        PlayerStat statType = PlayerStat.ARROWS_SHOT;

        PlayerInfo playerInfo = new PlayerInfo(uuid);
        PlayerInfo playerInfo2 = new PlayerInfo(uuid);

        Query queryA = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 5);

        Query queryB = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 6);

        Query queryC = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName2",
                "value", 8);

        playerInfo.addRow(statType, queryA);
        playerInfo2.addRow(statType, queryB);
        playerInfo2.addRow(statType, queryC);

        PlayerInfo nonConflictingPlayerInfo = playerInfo.resolveConflicts(playerInfo2);

        // Verify that there is only 1 row
        Assert.assertEquals(2, nonConflictingPlayerInfo.getNumberOfRows(statType));

        // Verify that there is only 1 statistic stored
        Assert.assertEquals(1, nonConflictingPlayerInfo.getNumberOfStatistics());

        // Verify that value is correct
        Assert.assertEquals(19, nonConflictingPlayerInfo.getTotalValue(statType), 0);

        System.out.println(nonConflictingPlayerInfo);
    }

    // Test two conflicting (of same statType) and one non conflicting (of another statType).
    @Test
    public void testResolvePlayerInfo3() {
        UUID uuid = UUID.fromString("3657b9cc-2518-4265-ad69-323e11286ce2");
        PlayerStat statType = PlayerStat.ARROWS_SHOT;
        PlayerStat statType2 = PlayerStat.KILLS_MOBS;

        PlayerInfo playerInfo = new PlayerInfo(uuid);
        PlayerInfo playerInfo2 = new PlayerInfo(uuid);

        Query queryA = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 5);

        Query queryB = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 6);

        Query queryC = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName2",
                "value", 8);

        playerInfo.addRow(statType, queryA);
        playerInfo2.addRow(statType, queryB);
        playerInfo2.addRow(statType2, queryC);

        PlayerInfo nonConflictingPlayerInfo = playerInfo.resolveConflicts(playerInfo2);

        // Verify the number of rows
        Assert.assertEquals(1, nonConflictingPlayerInfo.getNumberOfRows(statType));
        Assert.assertEquals(1, nonConflictingPlayerInfo.getNumberOfRows(statType2));

        // Verify the number of statistics stored.
        Assert.assertEquals(2, nonConflictingPlayerInfo.getNumberOfStatistics());

        // Verify that value is correct
        Assert.assertEquals(11, nonConflictingPlayerInfo.getTotalValue(statType), 0);
        Assert.assertEquals(8, nonConflictingPlayerInfo.getTotalValue(statType2), 0);
    }

    // Test two conflicting (of same statType) and two non conflicting (of another statType, that conflict with each
    // other).
    @Test
    public void testResolvePlayerInfo4() {
        UUID uuid = UUID.fromString("3657b9cc-2518-4265-ad69-323e11286ce2");
        PlayerStat statType = PlayerStat.ARROWS_SHOT;
        PlayerStat statType2 = PlayerStat.KILLS_MOBS;

        PlayerInfo playerInfo = new PlayerInfo(uuid);
        PlayerInfo playerInfo2 = new PlayerInfo(uuid);

        Query queryA = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 5);

        Query queryB = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 6);

        Query queryC = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName2",
                "value", 8);

        Query queryD = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName2",
                "value", 45);

        playerInfo.addRow(statType, queryA);
        playerInfo2.addRow(statType, queryB);
        playerInfo2.addRow(statType2, queryC);
        playerInfo.addRow(statType2, queryD);

        PlayerInfo nonConflictingPlayerInfo = playerInfo.resolveConflicts(playerInfo2);

        // Verify the number of rows
        Assert.assertEquals(1, nonConflictingPlayerInfo.getNumberOfRows(statType));
        Assert.assertEquals(1, nonConflictingPlayerInfo.getNumberOfRows(statType2));

        // Verify the number of statistics stored.
        Assert.assertEquals(2, nonConflictingPlayerInfo.getNumberOfStatistics());

        // Verify that value is correct
        Assert.assertEquals(11, nonConflictingPlayerInfo.getTotalValue(statType), 0);
        Assert.assertEquals(53, nonConflictingPlayerInfo.getTotalValue(statType2), 0);
    }

    // Test two conflicting (of same statType) and two non conflicting (of another statType, that conflict with each
    // other).
    @Test
    public void testResolvePlayerInfo5() {
        UUID uuid = UUID.fromString("3657b9cc-2518-4265-ad69-323e11286ce2");
        PlayerStat statType = PlayerStat.ARROWS_SHOT;
        PlayerStat statType2 = PlayerStat.KILLS_MOBS;

        PlayerInfo playerInfo = new PlayerInfo(uuid);
        PlayerInfo playerInfo2 = new PlayerInfo(uuid);

        Query queryA = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 5);

        Query queryB = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName1",
                "value", 6);

        Query queryC = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName2",
                "value", 8);

        Query queryD = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName2",
                "value", 45);

        Query queryE = StatzUtil.makeQuery(
                "UUID", uuid,
                "World", "worldName3",
                "value", 100);

        playerInfo.addRow(statType, queryA);
        playerInfo2.addRow(statType, queryB);
        playerInfo2.addRow(statType2, queryC);
        playerInfo.addRow(statType2, queryD);
        playerInfo.addRow(statType, queryE);

        PlayerInfo nonConflictingPlayerInfo = playerInfo.resolveConflicts(playerInfo2);

        // Verify the number of rows
        Assert.assertEquals(2, nonConflictingPlayerInfo.getNumberOfRows(statType));
        Assert.assertEquals(1, nonConflictingPlayerInfo.getNumberOfRows(statType2));

        // Verify the number of statistics stored.
        Assert.assertEquals(2, nonConflictingPlayerInfo.getNumberOfStatistics());

        // Verify that value is correct
        Assert.assertEquals(111, nonConflictingPlayerInfo.getTotalValue(statType), 0);
        Assert.assertEquals(53, nonConflictingPlayerInfo.getTotalValue(statType2), 0);
    }
}
