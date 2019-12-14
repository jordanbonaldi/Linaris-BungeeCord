package net.neferett.linaris.commands.player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.utils.messages.MyBuilder;

public class CommandHelp extends Command{
    public CommandHelp(){
        super("help", "", "aide", "?", "chuiunemerde", "please", "jyarrivepas", "servdemerde", "help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Aide générale sur les commandes."));
        sender.sendMessage(TextComponent.fromLegacyText(""));
        BaseComponent[] coins = new MyBuilder(ChatColor.GREEN + "/coins ")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique pour exécuter la commande.")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/coins "))
                .append(ChatColor.WHITE + "- ")
                .append(ChatColor.GRAY + "affiche votre solde en " + ChatColor.YELLOW + "Coins")
                .create();
        BaseComponent[] friends = new MyBuilder(ChatColor.GREEN + "/friend ")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique pour exécuter la commande.")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend "))
                .append(ChatColor.WHITE + "- ")
                .append(ChatColor.GRAY + "affiche les commandes de gestion des amis")
                .create();
        BaseComponent[] party = new MyBuilder(ChatColor.GREEN + "/party ")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique pour exécuter la commande.")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party "))
                .append(ChatColor.WHITE + "- ")
                .append(ChatColor.GRAY + "affiche les commandes de gestion du groupe")
                .create();
        BaseComponent[] hub = new MyBuilder(ChatColor.GREEN + "/hub ")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique pour exécuter la commande.")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hub "))
                .append(ChatColor.WHITE + "- ")
                .append(ChatColor.GRAY + "retourne au hub/lobby")
                .create();
        BaseComponent[] msg = new MyBuilder(ChatColor.GREEN + "/msg ")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique pour exécuter la commande.")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/msg "))
                .append(ChatColor.WHITE + "- ")
                .append(ChatColor.GRAY + "envoie un message privé à un joueur")
                .create();
        BaseComponent[] r = new MyBuilder(ChatColor.GREEN + "/r ")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique pour exécuter la commande.")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/r "))
                .append(ChatColor.WHITE + "- ")
                .append(ChatColor.GRAY + "répond au dernier message privé")
                .create();
        BaseComponent[] report = new MyBuilder(ChatColor.GREEN + "/report ")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique pour exécuter la commande.")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report "))
                .append(ChatColor.WHITE + "- ")
                .append(ChatColor.GRAY + "dépose une plainte contre un joueur")
                .create();
        BaseComponent[] changepw = new MyBuilder(ChatColor.GREEN + "/changepw ")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique pour rentrer la commande.")))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/changepw "))
                .append(ChatColor.WHITE + "- ")
                .append(ChatColor.GRAY + "permet de changer son mot de passe")
                .create();
        sender.sendMessage(coins);
        sender.sendMessage(friends);
        sender.sendMessage(party);
        sender.sendMessage(hub);
        sender.sendMessage(msg);
        sender.sendMessage(r);
        sender.sendMessage(report);
        sender.sendMessage(changepw);
    }
}
