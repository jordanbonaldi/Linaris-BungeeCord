package net.neferett.linaris;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;
import net.md_5.bungee.api.config.ServerInfo;
import net.neferett.linaris.DataBase.rabbitmq.CommandExecutorRabbitMQ;
import net.neferett.linaris.DataBase.rabbitmq.MoveToServerRabbitMQ;
import net.neferett.linaris.DataBase.rabbitmq.QueuesMessaging;
import net.neferett.linaris.DataBase.rabbitmq.RPCServersManager;
import net.neferett.linaris.DataBase.rabbitmq.ReturnToHubRabbitMQ;
import net.neferett.linaris.DataBase.redis.DatabaseConnector;
import net.neferett.linaris.DataBase.redis.RedisServer;
import net.neferett.linaris.DataBase.redis.SingleDatabaseConnector;
import net.neferett.linaris.api.PlayerDataManager;
import net.neferett.linaris.api.QueuesManagement;
import net.neferett.linaris.api.friends.FriendsManagement;
import net.neferett.linaris.api.party.PartiesManagement;
import net.neferett.linaris.api.ranks.RankManager;
import net.neferett.linaris.commands.administration.*;
import net.neferett.linaris.commands.moderation.ChangepassCommand;
import net.neferett.linaris.commands.moderation.ConnectCommand;
import net.neferett.linaris.commands.moderation.GhostConnect;
import net.neferett.linaris.commands.moderation.PlayerInfo;
import net.neferett.linaris.commands.moderation.TrackCommand;
import net.neferett.linaris.commands.moderation.bans.BanCommands;
import net.neferett.linaris.commands.moderation.bans.DCCommands;
import net.neferett.linaris.commands.moderation.bans.IPBanCommands;
import net.neferett.linaris.commands.moderation.bans.KickCommand;
import net.neferett.linaris.commands.moderation.bans.MuteCommands;
import net.neferett.linaris.commands.moderation.bans.UnBanCommand;
import net.neferett.linaris.commands.moderation.bans.UnMuteCommands;
import net.neferett.linaris.commands.player.*;
import net.neferett.linaris.commands.player.login.ChangePWCommand;
import net.neferett.linaris.commands.player.login.LoginCommand;
import net.neferett.linaris.commands.player.login.RegisterCommand;
import net.neferett.linaris.listeners.ChatEvents;
import net.neferett.linaris.listeners.ConnectToServerEvent;
import net.neferett.linaris.listeners.JoinLeaveEvents;
import net.neferett.linaris.listeners.KickEvent;
import net.neferett.linaris.listeners.LoginEvents;
import net.neferett.linaris.listeners.PingEvent;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.DoubleAccount;
import net.neferett.linaris.managers.others.AutoMessageManager;
import net.neferett.linaris.managers.others.ConfigManager;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.chat.AntiSpam;
import net.neferett.linaris.managers.player.chat.AntiSwear;
import net.neferett.linaris.managers.servers.ServersManager;
import net.neferett.linaris.managers.servers.socket.SocketReceiveMessages;
import net.neferett.linaris.utils.database.RabbitMQUtils;
import net.neferett.linaris.utils.tasks.TasksExecutor;
import net.neferett.linaris.utils.time.TimeUtils;
import net.neferett.socket.SockServer;
import net.neferett.socket.packet.Packet;
import net.neferett.socket.packet.PacketAction;

public class GameServers extends ConfigurablePlugin {

	private static GameServers instance;

	public static GameServers get() {
		return instance;
	}

	AutoMessageManager	am;

	AntiSpam			antiSpamListener;

	AntiSwear			antiSwear;

	BanManager			bm;

	ConfigManager		cm;

	DatabaseConnector	connector;

	DoubleAccount		dc;

	FriendsManagement	friendsManager;

	BPlayerHandler		h;

	List<ServerInfo>	loginsserver	= new ArrayList<>();

	PartiesManagement	partiesManagement;

	PlayerDataManager	playerDataManager;

	QueuesManagement	queuesManagement;

	@Getter
	RankManager			ranksmanager;

	SockServer			s;

	ServersManager		serversManager;

	TasksExecutor		tasksManager;

	public AutoMessageManager getAm() {
		return this.am;
	}

	public AntiSpam getAntiSpamListener() {
		return this.antiSpamListener;
	}

	public AntiSwear getAntiSwear() {
		return this.antiSwear;
	}

	public BanManager getBm() {
		return this.bm;
	}

	public ConfigManager getConfigManager() {
		return this.cm;
	}

	public DatabaseConnector getConnector() {
		return this.connector;
	}

	public DoubleAccount getDc() {
		return this.dc;
	}

	public FriendsManagement getFriendsManager() {
		return this.friendsManager;
	}

	public BPlayerHandler getH() {
		return this.h;
	}

	public List<ServerInfo> getLoginsserver() {
		return this.loginsserver;
	}

	public PartiesManagement getPartiesManagement() {
		return this.partiesManagement;
	}

	public PlayerDataManager getPlayerDataManager() {
		return this.playerDataManager;
	}

	public QueuesManagement getQueuesManagement() {
		return this.queuesManagement;
	}

	public ServersManager getServersManager() {
		return this.serversManager;
	}

	public TasksExecutor getTasksManager() {
		return this.tasksManager;
	}

	public void load() throws IOException {
		this.connector = new SingleDatabaseConnector(this, RedisServer.host + ":" + RedisServer.port, RedisServer.auth);
		this.tasksManager = new TasksExecutor();
		new Thread(this.tasksManager, "ExecutorThread").start();

		RabbitMQUtils.inits();

		this.h = new BPlayerHandler();
		this.dc = new DoubleAccount();
		this.bm = new BanManager();
		this.am = new AutoMessageManager();
		this.cm = new ConfigManager();
		this.playerDataManager = new PlayerDataManager(this);
		this.friendsManager = new FriendsManagement(this);
		this.partiesManagement = new PartiesManagement(this);
		this.antiSwear = new AntiSwear();

		try {
			new RPCServersManager();
			new ReturnToHubRabbitMQ();
			new MoveToServerRabbitMQ();
			new QueuesMessaging();
			new CommandExecutorRabbitMQ();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		try {
			this.serversManager = new ServersManager(this);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.queuesManagement = new QueuesManagement(this);

		this.antiSpamListener = new AntiSpam();

		this.loginsserver = new HashSet<>(this.getProxy().getServers().entrySet()).stream()
				.filter(e -> e.getKey().toLowerCase().startsWith("login")).map(e -> e.getValue())
				.collect(Collectors.toList());

		this.getProxy().getPluginManager().registerListener(this, new ChatEvents());
		this.getProxy().getPluginManager().registerListener(this, new ConnectToServerEvent());
		this.getProxy().getPluginManager().registerListener(this, new KickEvent());
		this.getProxy().getPluginManager().registerListener(this, new LoginEvents());
		this.getProxy().getPluginManager().registerListener(this, new JoinLeaveEvents());
		this.getProxy().getPluginManager().registerListener(this, new PingEvent());

		this.getProxy().getPluginManager().registerCommand(this, new YTMembers());
//		this.getProxy().getPluginManager().registerCommand(this, new BanCommands());
//		this.getProxy().getPluginManager().registerCommand(this, new KickCommand());
//		this.getProxy().getPluginManager().registerCommand(this, new DCCommands());
//		this.getProxy().getPluginManager().registerCommand(this, new MuteCommands());
//		this.getProxy().getPluginManager().registerCommand(this, new UnMuteCommands());
//		this.getProxy().getPluginManager().registerCommand(this, new IPBanCommands());
//		this.getProxy().getPluginManager().registerCommand(this, new UnBanCommand());
		this.getProxy().getPluginManager().registerCommand(this, new StopCommand());
		this.getProxy().getPluginManager().registerCommand(this, new CommandShop());
		this.getProxy().getPluginManager().registerCommand(this, new BoutiqueCommand());
		this.getProxy().getPluginManager().registerCommand(this, new CommandCoins());
		this.getProxy().getPluginManager().registerCommand(this, new ReloadConfig());
		this.getProxy().getPluginManager().registerCommand(this, new MessageCommand());
		this.getProxy().getPluginManager().registerCommand(this, new StaffMembers());
		this.getProxy().getPluginManager().registerCommand(this, new RespondCommand());
		this.getProxy().getPluginManager().registerCommand(this, new ConnectCommand());
		this.getProxy().getPluginManager().registerCommand(this, new GhostConnect());
		this.getProxy().getPluginManager().registerCommand(this, new PlayerDataConsoleCommand());
		this.getProxy().getPluginManager().registerCommand(this, new CommandReport());
		this.getProxy().getPluginManager().registerCommand(this, new CommandHelp());
		this.getProxy().getPluginManager().registerCommand(this, new CommandList(this));
		this.getProxy().getPluginManager().registerCommand(this, new VoteCommand(this));
		this.getProxy().getPluginManager().registerCommand(this, new CommandList(this));
		this.getProxy().getPluginManager().registerCommand(this, new ChangePWCommand());
		this.getProxy().getPluginManager().registerCommand(this, new CommandStaff());
		this.getProxy().getPluginManager().registerCommand(this, new CommandStaffAction());
		this.getProxy().getPluginManager().registerCommand(this, new CommandVersion());
		this.getProxy().getPluginManager().registerCommand(this, new CommandNews());
		this.getProxy().getPluginManager().registerCommand(this, new ChangepassCommand());
		this.getProxy().getPluginManager().registerCommand(this, new CommandTokens());
		this.getProxy().getPluginManager().registerCommand(this, new RegisterCommand());
		this.getProxy().getPluginManager().registerCommand(this, new LoginCommand());
		this.getProxy().getPluginManager().registerCommand(this, new AlertCommand());
		this.getProxy().getPluginManager().registerCommand(this, new PlayerInfo());
		this.getProxy().getPluginManager().registerCommand(this, new ShopManagerCommand());

		this.getProxy().getPluginManager().registerCommand(this, new TrackCommand());

		TimeUtils.scheduleAutoReboot();

		this.ranksmanager = new RankManager();

	}

	@Override
	public void onDisable() {
		try {
			RabbitMQUtils.getConnection().close();
			this.connector.disable();
		} catch (final IOException e2) {
			e2.printStackTrace();
		}
	}

	@Override
	public void onEnable() {
		instance = this;
		try {
			this.load();

			this.s = new SockServer(this.cm.getCheat(), new SocketReceiveMessages());
			this.s.addPacket(new Packet(PacketAction.RECEIVE));

		} catch (final IOException e1) {
			e1.printStackTrace();
		}
	}

}
