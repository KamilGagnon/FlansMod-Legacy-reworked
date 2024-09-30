package com.flansmod.apocalypse.common.world.buildings;

import com.flansmod.apocalypse.common.FlansModApocalypse;
import com.flansmod.common.driveables.DriveableData;
import com.flansmod.common.driveables.DriveableType;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EnumDriveablePart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenVehicule extends WorldGenerator {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int x = blockPos.getX();
		int z = blockPos.getZ();
		int yHeight = 108;
		
		//Spawn a plane
		DriveableType type = FlansModApocalypse.getLootGenerator().getRandomDriveable(random);
		NBTTagCompound tags = new NBTTagCompound();
		tags.setString("Engine", FlansModApocalypse.getLootGenerator().getRandomEngine(type, random).shortName);
		tags.setString("Type", type.shortName);
		for(EnumDriveablePart part : EnumDriveablePart.values())
		{
			tags.setInteger(part.getShortName() + "_Health", type.health.get(part) == null ? 0 : random.nextInt(type.health.get(part).health));
			tags.setBoolean(part.getShortName() + "_Fire", false);
		}

		EntityDriveable entity = type.createDriveable(world, x + 8, yHeight + 3, z + 8, new DriveableData(tags));

		entity.setRotation(0F, 0, 0);

		world.spawnEntity(entity);
		
		return false;
	}
}
