package com.example.examplemod.commands;

import com.example.examplemod.gui.CustomConfigGui;
import com.example.examplemod.helper.ChatUtils;
import com.example.examplemod.helper.HotbarUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;


public class ModGuiCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "maxi";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/maxi - Opens config screen";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();

        if (args.length < 1) {
            //.addChatMessage(new ChatComponentText("§cUsage: " + getCommandUsage(sender)));
            if (mc.currentScreen != null) {
                mc.currentScreen = null; // close any open gui to avoid conflict
            }

            CustomConfigGui renderer = CustomConfigGui.getInstance();
            if (renderer != null) {
                boolean newVisible = !renderer.isVisible();
                renderer.setVisible(newVisible);
                //ChatUtils.hideChatGUI();

                sender.addChatMessage(new ChatComponentText("Custom UI Overlay " + (newVisible ? "opened" : "closed")));
            } else {
                sender.addChatMessage(new ChatComponentText("OverlayRenderer instance not found."));
            }
        } else {
            sender.addChatMessage(new ChatComponentText("§cInvalid subcommand."));
        }
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("Maxi");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true; // All players can use it
    }
}
