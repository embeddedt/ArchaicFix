package org.embeddedt.archaicfix;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldServer;

import java.util.TreeSet;

public class CommandDebugUpdateQueue extends CommandBase {
    @Override
    public String getCommandName() {
        return "debugupdatequeue";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/debugupdatequeue";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] p_71515_2_) {
        MinecraftServer server = MinecraftServer.getServer();
        if(server != null) {
            sender.addChatMessage(new ChatComponentText("Update queue sizes:"));
            for(WorldServer world : server.worldServers) {
                @SuppressWarnings("unchecked")
                TreeSet<NextTickListEntry> ticks = world.pendingTickListEntriesTreeSet;
                if(!ticks.isEmpty())
                    sender.addChatMessage(new ChatComponentText("Dimension " + world.provider.dimensionId + ": " + ticks.size()));
            }
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No server found."));
        }
    }
}
