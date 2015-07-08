package me.nikosgram.oglofus.protection;

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.nikosgram.oglofus.protection.api.CommandExecutor;
import me.nikosgram.oglofus.protection.api.command.Owner;
import me.nikosgram.oglofus.protection.api.command.Staff;
import me.nikosgram.oglofus.protection.api.entity.User;
import me.nikosgram.oglofus.protection.api.message.MessageColor;
import me.nikosgram.oglofus.protection.api.region.ProtectionRank;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OglofusCommands {
    @Command(aliases = {"version", "ver"}, desc = "Get the version")
    @Require("oglofus.protection.version")
    public void version(CommandExecutor sender) {
        sender.sendMessage(
                MessageColor.YELLOW + "OglofusProtection " +
                        OglofusProtection.getPlugin().getVersionName() + " v" +
                        OglofusProtection.getPlugin().getVersion(),
                MessageColor.YELLOW + "by nikosgram13"
        );
    }

    @Command(aliases = {"reload", "rel"}, desc = "Reload the configuration")
    @Require("oglofus.protection.reload")
    public void reload(CommandExecutor sender) {
        //TODO
    }

    //Real time update...
//    @Command(aliases = {"push"}, desc = "Force push to database")
//    @Require("oglofus.protection.push")
//    public void push(CommandExecutor sender) {
//        //TODO
//    }

    @Command(aliases = {"invite", "inv"}, desc = "Invite a player to your region")
    @Require("oglofus.protection.invite")
    public void invite(CommandExecutor sender, User user, @Owner @Optional ProtectionRegion region) {
        //TODO
    }

    @Command(aliases = {"kick", "remove"}, desc = "Kick a user from your region")
    @Require("oglofus.protection.kick")
    public void kick(CommandExecutor sender, @Staff User user, @Owner @Optional ProtectionRegion region) {
        //TODO
    }

    @Command(aliases = {"promote", "prom"}, desc = "Promote a user from your region")
    @Require("oglofus.protection.promote")
    public void promote(CommandExecutor sender, @Staff User user, @Owner @Optional ProtectionRegion region) {
        //TODO
    }

    @Command(aliases = {"demote", "dem"}, desc = "Demote a user from your region")
    @Require("oglofus.protection.demote")
    public void demote(CommandExecutor sender, @Staff User user, @Owner @Optional ProtectionRegion region) {
        //TODO
    }

    @Command(aliases = {"rank"}, desc = "Get or set the rank from a user in your region")
    @Require("oglofus.protection.rank")
    public void rank(CommandExecutor sender, @Staff User user, @Optional ProtectionRank rank, @Optional ProtectionRegion region) {
        //TODO
    }

    @Command(aliases = {"rename"}, desc = "Rename your region")
    @Require("oglofus.protection.rename")
    public void rename(CommandExecutor sender, String name, @Owner @Optional ProtectionRegion region) {
        //TODO
    }

    @Command(aliases = "info", desc = "Get info from a region")
    @Require("oglofus.protection.info")
    public void info(CommandExecutor sender, @Optional ProtectionRegion region) {
        //TODO
    }
}
