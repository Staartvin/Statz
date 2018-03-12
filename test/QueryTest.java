import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QueryTest {

    /**
     * Check if two queries conflict
     *
     * @param queryA         Query A
     * @param queryB         Query B
     * @param expectedResult whether they should conflict or not.
     */
    private void checkConflict(Query queryA, Query queryB, boolean expectedResult) {
        boolean conflicting = queryA.conflicts(queryB);

        Assert.assertEquals(expectedResult, conflicting);
    }

    // World names are different
    @Test
    public void testNonConflictingQuery1() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "World", "worldName1");

        Query queryB = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "World", "worldName2");

        checkConflict(queryA, queryB, false);
    }

    // UUIDs are different
    @Test
    public void testNonConflictingQuery2() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "World", "worldName1");

        Query queryB = StatzUtil.makeQuery(
                "UUID", "c019cc4e-e9b9-4140-9cf5-07338a21659f",
                "World", "worldName1");

        checkConflict(queryA, queryB, false);
    }

    // The number of columns are different
    @Test
    public void testNonConflictingQuery3() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "World", "worldName");

        Query queryB = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2");

        checkConflict(queryA, queryB, false);
    }

    // The number of columns are different
    @Test
    public void testNonConflictingQuery4() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2");

        Query queryB = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "World", "worldname");

        checkConflict(queryA, queryB, false);
    }

    //Not the same columns
    @Test
    public void testNonConflictingQuery5() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW");

        Query queryB = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "World", "worldname");

        checkConflict(queryA, queryB, false);
    }

    //Same number of columns and UUIDs are the same
    @Test
    public void testConflictingQuery1() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2");

        Query queryB = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2");

        checkConflict(queryA, queryB, true);
    }

    //Same number of columns and UUIDs are the same, ignoring value column
    @Test
    public void testConflictingQuery2() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "value", 5);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "value", 5498);

        checkConflict(queryA, queryB, true);
    }

    //Same number of columns, UUIDs are the same and mob is the same.
    @Test
    public void testConflictingQuery3() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 46);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 5);

        checkConflict(queryA, queryB, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRobustnessAddValue() {
        Query query = new Query();
        query.addValue("Non-existing-column", "Value");
    }

    @Test(expected = NullPointerException.class)
    public void testRobustnessAddValue2() {
        Query query = new Query();
        query.setValue("Existing-column", "Def value");
        query.addValue("Existing-column", null);
    }

    @Test
    public void testFilterCopy() {
        Query originalQuery = StatzUtil.makeQuery("UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "test", "someTestValue",
                "columnToDisappear", "byebyeValue");

        Query copy = originalQuery.getFilteredCopy("columnToDisappear");

        Assert.assertEquals(originalQuery.getData().size() - 1, copy.getData().size());
        Assert.assertTrue(copy.hasColumn("test"));
        Assert.assertTrue(copy.hasColumn("mob"));
        Assert.assertTrue(copy.hasColumn("uuid"));
    }

    //Value should add up
    @Test
    public void testResolveConflictingQueries() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 46);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 5);

        Assert.assertEquals(51, queryA.resolveConflict(queryB).getValue(), 0);
    }

    //Resolve conflict should be symmetric - value should add up
    @Test
    public void testResolveConflictingQueries2() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 46);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 5);
        // Note that query order is reversed.
        Assert.assertEquals(51, queryB.resolveConflict(queryA).getValue(), 0);
    }

    //Add up negative and positive values
    @Test
    public void testResolveConflictingQueries3() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", -6);

        Assert.assertEquals(94, queryA.resolveConflict(queryB).getValue(), 0);
    }

    //Queries are not conflicting, so cannot resolve.
    @Test(expected = IllegalArgumentException.class)
    public void testRobustnessResolveConflictingQueries() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "Chicken",
                "value", -6);

        queryA.resolveConflict(queryB).getValue();
    }

    //A query does not have value column
    @Test(expected = IllegalArgumentException.class)
    public void testRobustnessResolveConflictingQueries2() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW");

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", -6);

        queryA.resolveConflict(queryB).getValue();
    }

    //A query does not have value column
    @Test(expected = IllegalArgumentException.class)
    public void testRobustnessResolveConflictingQueries3() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW");

        PlayerInfo info = new PlayerInfo(UUID.fromString("3657b9cc-2518-4265-ad69-323e11286ce2"));
        List<Query> list = new ArrayList<>();
        list.add(queryA);
        list.add(queryB);

        info.setData(PlayerStat.KILLS_MOBS, list);
        info.setData(PlayerStat.ITEMS_DROPPED, list);

        System.out.println(info.toString());

        queryA.resolveConflict(queryB).getValue();
    }

    @Test
    public void testMultipleConflictingQueries() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryC = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 200);

        Query queryD = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 500);

        Query queryE = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        List<Query> conflictingQueries = new ArrayList<>();
        conflictingQueries.add(queryB);
        conflictingQueries.add(queryC);
        conflictingQueries.add(queryD);
        conflictingQueries.add(queryE);

        Query nonConflictingQuery = queryA.resolveConflicts(conflictingQueries);

        Assert.assertEquals(1000, nonConflictingQuery.getValue(), 0);
    }

    @Test
    public void testMultipleConflictingQueries2() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryC = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 200);

        Query queryD = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 500);

        Query queryE = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryF = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "PIG",
                "value", 100);

        List<Query> conflictingQueries = new ArrayList<>();
        conflictingQueries.add(queryB);
        conflictingQueries.add(queryC);
        conflictingQueries.add(queryD);
        conflictingQueries.add(queryE);
        conflictingQueries.add(queryF);

        Query nonConflictingQuery = queryA.resolveConflicts(conflictingQueries);

        Assert.assertEquals(1000, nonConflictingQuery.getValue(), 0);
    }

    @Test
    public void testMultipleConflictingQueries3() {
        Query queryA = StatzUtil.makeQuery(
                "UUID", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", -100);

        Query queryB = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        Query queryC = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", -200);

        Query queryD = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 500);

        Query queryE = StatzUtil.makeQuery(
                "uuid", "3657b9cc-2518-4265-ad69-323e11286ce2",
                "mob", "COW",
                "value", 100);

        List<Query> conflictingQueries = new ArrayList<>();
        conflictingQueries.add(queryB);
        conflictingQueries.add(queryC);
        conflictingQueries.add(queryD);
        conflictingQueries.add(queryE);

        Query nonConflictingQuery = queryA.resolveConflicts(conflictingQueries);

        Assert.assertEquals(400, nonConflictingQuery.getValue(), 0);
    }

}
