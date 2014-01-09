package com.censoredsoftware.censoredlib.commands;

import com.censoredsoftware.censoredlib.CensoredLib;
import com.censoredsoftware.censoredlib.helper.CommandManager;
import com.censoredsoftware.censoredlib.language.Symbol;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.List;

public class MainCommands extends CommandManager
{
	private static final GitHubClient GITHUB_CLIENT;
	private static final RepositoryService REPOSITORY_SERVICE;
	private static final CommitService COMMIT_SERVICE;
	private static String oauthToken;

	static
	{
		// Set the OAuth from config
		oauthToken = CensoredLib.plugin().getConfig().getString("github_oauth");
		if("".equals(oauthToken)) oauthToken = null;

		// Define GitHub client and set OAuth token
		GITHUB_CLIENT = new GitHubClient();
		if(oauthToken != null) GITHUB_CLIENT.setOAuth2Token(oauthToken);

		// Register GitHub API services with client
		REPOSITORY_SERVICE = new RepositoryService(GITHUB_CLIENT);
		COMMIT_SERVICE = new CommitService(GITHUB_CLIENT);
	}

	@Override
	public ImmutableSet<String> getCommandNames()
	{
		return ImmutableSet.of("repo");
	}

	private static final String NO_PERMS = ChatColor.RED + "You do not have permission to use that command.";
	private static final String ORGANIZATION = "CensoredSoftware";
	private static final Sub repoSetOAuth, repoList, repoInfo;

	static
	{
		repoSetOAuth = new Sub()
		{
			@Override
			public String getName()
			{
				return "setoauth";
			}

			@Override
			public String getParentCommand()
			{
				return "repo";
			}

			@Override
			public int getMinArgs()
			{
				return 2;
			}

			@Override
			public String getFailMessage()
			{
				return NO_PERMS;
			}

			@Override
			public Setting allowSender(CommandSender sender)
			{
				if(sender.hasPermission("censoredlib.admin")) return Setting.ALLOW;
				return Setting.FAIL;
			}

			@Override
			public boolean execute(CommandSender sender, String label, String[] args)
			{
				// Set the token and save the config
				oauthToken = args[1];
				CensoredLib.plugin().getConfig().set("github_oauth", oauthToken);
				CensoredLib.plugin().saveConfig();

				// Verify it
				GITHUB_CLIENT.setOAuth2Token(oauthToken);

				// It works
				sender.sendMessage(ChatColor.GREEN + "OAuth token set successfully. Please reload the server.");

				return true;
			}
		};

		repoList = new Sub()
		{
			@Override
			public String getName()
			{
				return "list";
			}

			@Override
			public String getParentCommand()
			{
				return "repo";
			}

			@Override
			public int getMinArgs()
			{
				return 1;
			}

			@Override
			public boolean execute(CommandSender sender, String label, String[] args)
			{
				try
				{
					// Display repos
					sender.sendMessage(ChatColor.AQUA + "[" + CensoredLib.plugin().getName() + "] " + ChatColor.RESET + "Censored Software Repositories");
					for(Repository repo : REPOSITORY_SERVICE.getOrgRepositories(ORGANIZATION))
						sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW_HOLLOW + ChatColor.YELLOW + " " + repo.getName());
				}
				catch(IOException ignored)
				{
					sender.sendMessage(ChatColor.RED + "An API connection could not be established.");
				}
				return true;
			}
		};

		repoInfo = new Sub()
		{
			@Override
			public String getName()
			{
				return "info";
			}

			@Override
			public String getParentCommand()
			{
				return "repo";
			}

			@Override
			public int getMinArgs()
			{
				return 2;
			}

			@Override
			public boolean execute(CommandSender sender, String label, String[] args)
			{
				try
				{
					sender.sendMessage(ChatColor.AQUA + "[" + CensoredLib.plugin().getName() + "] " + ChatColor.RESET + "Repository Information " + ChatColor.GRAY.toString() + ChatColor.ITALIC + "loading...");

					// Try to grab the repo and its information
					Repository repo = REPOSITORY_SERVICE.getRepository(ORGANIZATION, args[1]);
					RepositoryCommit latestCommit = COMMIT_SERVICE.getCommits(repo).get(0);

					// Define contributors
					List<String> contributors = Lists.newArrayList(Collections2.transform(REPOSITORY_SERVICE.getContributors(repo, false), new Function<Contributor, String>()
					{
						@Override
						public String apply(Contributor contributor)
						{
							return contributor.getLogin();
						}
					}));

					// Display information
					sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW_HOLLOW + " Name: " + ChatColor.YELLOW + repo.getName());
					sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW_HOLLOW + " Description: " + ChatColor.YELLOW + (repo.getDescription() != null ? StringUtils.abbreviate(repo.getDescription(), 42) : "No description listed."));
					sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW_HOLLOW + " URL: " + ChatColor.YELLOW + repo.getHtmlUrl());
					sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW_HOLLOW + " Open Issues: " + ChatColor.YELLOW + repo.getOpenIssues());
					sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW_HOLLOW + " Contributors: " + ChatColor.YELLOW + StringUtils.join(contributors, ChatColor.GRAY + ", " + ChatColor.YELLOW));
					sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW_HOLLOW + " Last Update: " + ChatColor.YELLOW + repo.getUpdatedAt());

					// Display latest commit information
					sender.sendMessage(ChatColor.GRAY + "   " + Symbol.RIGHTWARD_ARROW_SWOOP + " SHA: " + ChatColor.YELLOW + latestCommit.getSha());
					sender.sendMessage(ChatColor.GRAY + "   " + Symbol.RIGHTWARD_ARROW_SWOOP + " Committer: " + ChatColor.YELLOW + latestCommit.getCommitter().getLogin());
					if(latestCommit.getStats() != null) sender.sendMessage(ChatColor.GRAY + "   " + Symbol.RIGHTWARD_ARROW_SWOOP + " Changed Files: " + ChatColor.YELLOW + latestCommit.getStats().getTotal() + ChatColor.GRAY + "(" + ChatColor.GREEN + latestCommit.getStats().getAdditions() + ChatColor.GRAY + " additions / " + ChatColor.RED + latestCommit.getStats().getDeletions() + ChatColor.GRAY + " deletions)");
					sender.sendMessage(ChatColor.GRAY + "   " + Symbol.RIGHTWARD_ARROW_SWOOP + " Message: " + ChatColor.YELLOW + latestCommit.getCommit().getMessage());
				}
				catch(IOException ignored)
				{
					sender.sendMessage(ChatColor.RED + "That repository could not be accessed.");
				}

				return true;
			}
		};
	}

	@Override
	public ImmutableList<Sub> getSubCommands()
	{
		return ImmutableList.of(repoSetOAuth, repoList, repoInfo);
	}

    @Override
    public boolean always(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("repo"))
        {
            // First check for OAuth
            if(oauthToken == null)
            {
                if(sender.hasPermission("censoredlib.admin"))
                {
                    sender.sendMessage(ChatColor.RED + "You have not set a GitHub OAuth token or the token is invalid.");
                    sender.sendMessage(ChatColor.RED + "To do so, use " + ChatColor.YELLOW + "/repo setoauth <token>");
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "An administrator has not set the necessary GitHub OAuth token.");
                    sender.sendMessage(ChatColor.RED + "Please advise one to do so.");
                }
            }
            else if(args.length == 0)
            {
                sender.sendMessage(ChatColor.AQUA + "[" + CensoredLib.plugin().getName() + "] " + ChatColor.RESET + "Repository Information");
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + " /repo list");
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + " /repo info <repo name>");
				if(sender.hasPermission("censoredlib.admin")) sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + " /repo setoauth <token>");
			}
		}
		return true;
	}
}