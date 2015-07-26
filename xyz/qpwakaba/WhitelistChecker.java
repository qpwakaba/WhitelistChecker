package xyz.qpwakaba;

import java.util.concurrent.Callable;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class WhitelistChecker extends JavaPlugin {
	private final static String prefix = "[WhitelistChecker] ";
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
			//help
			sender.sendMessage(command.getDescription());
			return true;
		} else if(args.length == 0) {
			//check
			//�����A����������Ƃ��ꂢ�ȏ������ł��Ȃ����ȁH
			Set<OfflinePlayer> whitelisted = (Set)((HashSet)this.getServer().getWhitelistedPlayers()).clone();
			this.getServer().getScheduler().runTaskAsynchronously(this, new CheckingTask(sender, whitelisted));
			sender.sendMessage(ChatColor.GRAY + prefix + "�m�F���ł��B");
			return true;
		}
		return false;
	}
	
	private void checkWhitelist(CommandSender sender, Set<OfflinePlayer> whitelisted) {
		Set<OfflinePlayer> offlines = new HashSet<>();
		for(OfflinePlayer player: whitelisted) 
			if(!player.isOnline())
				offlines.add(player);
		
		if(offlines.isEmpty()) {
			//�S������
			sendMessage(sender, ChatColor.GREEN + prefix + "�z���C�g���X�g�̃v���C���[�͑S���Q�����Ă��܂��B");
		} else {
			//�l���s��
			sendMessage(sender, ChatColor.RED + prefix + "�Q�����Ă��Ȃ��v���C���[�����܂��B");
			for(OfflinePlayer player: offlines) {
				sendMessage(sender, ChatColor.GOLD + prefix + " " + player.getName());
			}
		}
	}
	
	private void sendMessage(final CommandSender sender, final String message) {
		this.getServer().getScheduler().callSyncMethod(this, new SendMessageTask(sender, message));
	}
	
	private class CheckingTask extends Thread {
		private final CommandSender sender;
		private final Set<OfflinePlayer> whitelisted;
		public CheckingTask(CommandSender sender, Set<OfflinePlayer> whitelisted) {
			this.whitelisted = whitelisted;
			this.sender = sender;
		}
		@Override
		public void run() {
			checkWhitelist(sender, whitelisted);
		}
	}
	
	private class SendMessageTask implements Callable<Void> {
		private CommandSender sender;
		private String message;
		public SendMessageTask(CommandSender sender, String message) {
			this.sender = sender;
			this.message = message;
		}
		@Override
		public Void call() {
			sender.sendMessage(message);
			return null;
		}
	}
}