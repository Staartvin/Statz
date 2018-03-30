package me.staartvin.statz.tasks;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.DatabaseConnector;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.update.UpdatePoolManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This task runs regularly and updates the database with the queries that are in the pool. Before the queries are
 * actually send to the database, they are compressed.
 */
public class UpdateDatabaseTask implements Runnable {

    private Statz plugin;

    public UpdateDatabaseTask(Statz instance) {
        this.plugin = instance;
    }

    @Override
    public void run() {

        if (UpdatePoolManager.isForcingPool) {
            // Skip call, as we are still busy.
            System.out.println("Skip database sync as there is still one running.");
            return;
        }

        System.out.println("-----------------------------------");

        System.out.println("Grabbing updates that need to be synced.");

        // Set lock so we can't accidentally run two sync tasks at the same time.
        UpdatePoolManager.isForcingPool = true;

        for (PlayerStat statType : PlayerStat.values()) {
            // Grab updates that have happened since the last sync.
            List<Query> updates = plugin.getUpdatePoolManager().getUpdateQueriesCopy(statType);

            if (updates.isEmpty()) {
                continue;
            }

            // Store queries that have already been converted
            List<Query> convertedQueries = new ArrayList<>();

            // Queries that should be send to the database
            List<Query> resultingQueries = new ArrayList<>();

            // Loop over all queries and remove duplicate queries.
            for (Iterator<Query> iterator = updates.iterator(); iterator.hasNext(); ) {

                // Get a query.
                Query query = iterator.next();

                // If we've already converted it, skip it.
                if (convertedQueries.contains(query)) {
                    continue;
                }

                // Remove the current query so it doesn't conflict with itself.
                iterator.remove();

                // Find queries that conflict with the current query
                List<Query> conflictingQueries = query.findConflicts(updates);

                // Add queries that were conflicting to converted queries, so we don't count them again.
                convertedQueries.addAll(conflictingQueries);

                // Calculate sum of all conflicting queries.
                Query sumQuery = query.resolveConflicts(conflictingQueries);

                // Store the final query
                resultingQueries.add(sumQuery);

                System.out.println(String.format("Gathered queries for %s and build: %s", statType, sumQuery));
            }

            // Update database with new data.
            plugin.getDatabaseConnector().setBatchObjects(DatabaseConnector.getTable(statType),
                    resultingQueries, 2);

            plugin.getUpdatePoolManager().clearUpdateQueries(statType);
        }

        // Release lock
        UpdatePoolManager.isForcingPool = false;

    }

}
