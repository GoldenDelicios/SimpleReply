package org.slabserver.plugin.simplereply;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public class SimpleReply extends JavaPlugin {
	Map<String, String> lastReceivedFrom = new HashMap<>();
	
	public SimpleReply() {
		
	}

	public SimpleReply(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}

	@Override
	public void onEnable() {
		this.getCommand("msg").setTabCompleter((sender, command, alias, args) -> {
			if (args.length == 1) {
				String query = args[0].toLowerCase();
				List<String> playerNames = new ArrayList<>();
				for (Player player : this.getServer().getOnlinePlayers()) {
					if (player.getName().toLowerCase().startsWith(query))
						playerNames.add(player.getName());
				}
				return playerNames;
			}
			else {
				return Collections.emptyList();
			}
		});
		this.getCommand("r").setTabCompleter((sender, command, alias, args) -> Collections.emptyList());
	}

	@Override
	public void onDisable() {
		lastReceivedFrom.clear();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("msg")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
			}
			else if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Please include a message");
			}
			else {
				String recipientName = args[0];
				Player recipient = this.getServer().getPlayer(recipientName);
				if (recipient != null && recipient.isOnline()) {
					String command = "minecraft:msg " + String.join(" ", args);
					this.getServer().dispatchCommand(sender, command);
					lastReceivedFrom.put(recipient.getName(), sender.getName());
				}
				else {
					sender.sendMessage(ChatColor.RED + "No online player with name '" + recipientName + "'");
				}
			}
		}
		
		else if (cmd.getName().equals("r")) {
			String recipientName = lastReceivedFrom.get(sender.getName());
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Usage: /r <message>");
			}
			else if (recipientName == null) {
				sender.sendMessage(ChatColor.RED + "No player to reply to");
			}
			else {
				Player recipient = this.getServer().getPlayer(recipientName);
				if (recipient != null && recipient.isOnline()) {
					String command = "minecraft:msg " + recipient.getName() + " " + String.join(" ", args);
					this.getServer().dispatchCommand(sender, command);
					lastReceivedFrom.put(recipient.getName(), sender.getName());
				}
				else {
					sender.sendMessage(ChatColor.RED + "No online player with name '" + recipientName + "'");
				}
			}
		}
		
		return true;
	}

}
