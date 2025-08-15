package com.example.examplemod.commands;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.utils.BlockedUserInit;
import com.example.examplemod.utils.BlockedUserManager;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;


public class BlockCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "block";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/block <add|remove|list|help|removeall> [args]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();

        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText("§cUsage: " + getCommandUsage(sender)));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("add") && args.length >= 2) {
            String username = args[1];
            BlockedUserInit.add(username);
            mc.thePlayer.sendChatMessage("/block add " + username);
            BlockedUserManager.loadJson("config/" + ExampleMod.MODID + "/blockedList.json");
        } else if (sub.equals("remove") && args.length >= 2) {
            String username = args[1];
            BlockedUserInit.remove(username);
            mc.thePlayer.sendChatMessage("/block remove " + username);
            BlockedUserManager.loadJson("config/" + ExampleMod.MODID + "/blockedList.json");
        } else if (sub.equals("list")) {
            int page = 1;
            if (args.length >= 2) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {}
            }
            mc.thePlayer.sendChatMessage("/block list " + page);
        } else if (sub.equals("help")) {
            mc.thePlayer.sendChatMessage("/block help");
        } else if (sub.equals("removeall")) {
            mc.thePlayer.sendChatMessage("/block removeall");
            BlockedUserManager.loadJson("config/" + ExampleMod.MODID + "/blockedList.json");
        } else if (args.length == 1) {
            String username = args[1];
            BlockedUserInit.add(username);
            mc.thePlayer.sendChatMessage("/block " + username);
            BlockedUserManager.loadJson("config/" + ExampleMod.MODID + "/blockedList.json");
        } else {
            sender.addChatMessage(new ChatComponentText("§cInvalid subcommand."));
        }
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("blk");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true; // All players can use it
    }
}
