package com.flansmod.apocalypse.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import java.util.Random;

public class ReloadChestsCommand extends CommandBase {

	@Override
	public String getName() {
		return "reloadchests";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/reloadchests";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = server.getWorld(0);
		Random rand = new Random();
		for (Chunk chunk : ((ChunkProviderServer) world.getChunkProvider()).getLoadedChunks()) {
			for (TileEntity tileEntity : chunk.getTileEntityMap().values()) {
				if (tileEntity instanceof TileEntityChest) {
					TileEntityChest oldChest = (TileEntityChest) tileEntity;
					if(rand.nextBoolean()){
						FlansModApocalypse.getLootGenerator().fillVillageChest(rand, oldChest);
					}else{
						FlansModApocalypse.getLootGenerator().fillWeaponChest(rand, oldChest);
					}
				}
			}
		}
		sender.sendMessage(new TextComponentString("Command executed successfully!"));
	}
}

