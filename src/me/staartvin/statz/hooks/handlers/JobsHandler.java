package me.staartvin.statz.hooks.handlers;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;

import me.staartvin.statz.Statz;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;
import me.staartvin.statz.util.StatzUtil;

/**
 * Handles all connections with Jobs
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class JobsHandler implements DependencyHandler {

	private final Statz plugin;
	private JobsPlugin api;

	public JobsHandler(final Statz instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(Dependency.JOBS.getInternalString());

		// May not be loaded
		if (plugin == null || !(plugin instanceof JobsPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return api != null;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		final JobsPlugin plugin = (JobsPlugin) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.debugMessage(ChatColor.RED + Dependency.JOBS.getInternalString() + " has not been found!");
			}
			return false;
		} else {
			api = (JobsPlugin) get();

			if (api != null) {
				return true;
			} else {
				if (verbose) {
					plugin.getLogger()
							.info(Dependency.JOBS.getInternalString() + " has been found but cannot be used!");
				}
				return false;
			}
		}
	}

	public double getCurrentPoints(UUID uuid) {
		if (!this.isAvailable() || uuid == null)
			return -1;

		PlayerPoints pointInfo = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(uuid);

		if (pointInfo == null)
			return -1;

		return StatzUtil.roundDouble(pointInfo.getCurrentPoints(), 2);
	}

	public double getTotalPoints(UUID uuid) {
		if (!this.isAvailable() || uuid == null)
			return -1;

		PlayerPoints pointInfo = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(uuid);

		if (pointInfo == null)
			return -1;

		return StatzUtil.roundDouble(pointInfo.getTotalPoints(), 2);
	}

	public double getCurrentXP(Player player, String jobName) {
		if (!this.isAvailable())
			return -1;

		Job job = this.getJob(jobName);

		if (job == null)
			return -1;

		JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

		if (jobsPlayer == null)
			return -1;

		JobProgression progress = jobsPlayer.getJobProgression(job);

		if (progress == null)
			return -1;

		return StatzUtil.roundDouble(progress.getExperience(), 2);
	}

	public double getCurrentLevel(Player player, String jobName) {
		if (!this.isAvailable())
			return -1;

		Job job = this.getJob(jobName);

		if (job == null)
			return -1;

		JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

		if (jobsPlayer == null)
			return -1;

		JobProgression progress = jobsPlayer.getJobProgression(job);

		if (progress == null)
			return -1;

		return StatzUtil.roundDouble(progress.getLevel(), 2);
	}

	public Job getJob(String jobName) {
		if (!this.isAvailable())
			return null;

		return Jobs.getJob(jobName);
	}

	public List<JobProgression> getJobs(Player player) {
		if (!this.isAvailable())
			return null;

		JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

		if (jobsPlayer == null)
			return null;

		return jobsPlayer.getJobProgression();
	}
}
