package com.flansmod.apocalypse.common.entity;

import com.flansmod.common.driveables.DriveableData;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.driveables.ShootPoint;
import com.flansmod.common.driveables.VehicleType;
import com.flansmod.common.vector.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityAIDriveVehicule extends EntityVehicle {

	private Entity target;
	private float targetingRange = 20F;
	private int targetAcquireInterval = 40;

	private boolean usingLeft = false;
	public EntityAIDriveVehicule(World world)
	{
		super(world);
	}

	public EntityAIDriveVehicule(World world, double x, double y, double z, VehicleType type, DriveableData data)
	{
		super(world, x, y, z, type, data);
	}

	public void onUpdate()
	{
		throttle = 1F;

		//float lookAheadDist = 20F;

		//float targetHeight = getBiomeHeight(world.getBiomeGenForCoords(new BlockPos((int)(posX + motionX * lookAheadDist), (int)(posY + motionY * lookAheadDist), (int)(posZ + motionZ * lookAheadDist))));
		//float currentTargetHeight = getBiomeHeight(world.getBiomeGenForCoords(new BlockPos((int)(posX), (int)(posY), (int)(posZ))));

		//flapsPitchLeft = flapsPitchRight += (Math.max(currentTargetHeight, targetHeight) - (float)posY) * 0.1F;


		super.onUpdate();
	}

	@Override
	public boolean processInitialInteract(EntityPlayer entityplayer, EnumHand hand)
	{
		return false;
	}

	protected void moveAI(Vector3f actualMotion)
	{
		VehicleType type = getVehicleType();
		DriveableData data = getDriveableData();
		List<ShootPoint> shootPointsPrimary = type.shootPoints(false);
		List<ShootPoint> shootPointsSecondary = type.shootPoints(true);

		//Acquire target
		if(target == null && (this.ticksExisted + this.getEntityId()) % targetAcquireInterval == 0)
		{
			double distToCurrentTarget = 999D;
			for(Object obj : world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(targetingRange, targetingRange, targetingRange)))
			{
				double distToPotentialTarget = this.getDistanceSq((Entity)obj);
				if(isBetterTarget(target, distToCurrentTarget, (Entity)obj, distToPotentialTarget))
				{
					target = (Entity)obj;
					distToCurrentTarget = distToPotentialTarget;
				}
			}
		}

		//And if we have line of sight, shoot it
		if(!world.isRemote && target != null)
		{
			Vec3d primaryGunOrigin = axes.findLocalVectorGlobally(this.getOrigin(shootPointsPrimary.get(0))).toVec3().add(posX, posY, posZ);
			Vec3d targetOrigin = new Vec3d(target.posX, target.posY + target.getEyeHeight() / 2D, target.posZ);

			double dX = targetOrigin.x - primaryGunOrigin.x;
			double dY = targetOrigin.y - primaryGunOrigin.y;
			double dZ = targetOrigin.z - primaryGunOrigin.z;

			axes.setAngles((float)Math.atan2(dZ, dX) * 180F / 3.14159F, 0F, 0F);
			if(getSeat(0) != null)
			{
				getSeat(0).looking.setAngles(0F, -(float)Math.atan2(dY, Math.sqrt(dX * dX + dZ * dZ)) * 180F / 3.14159F, 0F);
				getSeat(0).prevLooking.setAngles(0F, -(float)Math.atan2(dY, Math.sqrt(dX * dX + dZ * dZ)) * 180F / 3.14159F, 0F);
			}

			RayTraceResult hit = world.rayTraceBlocks(primaryGunOrigin, targetOrigin, false);

			if(world.isRemote)
			{
				//world.spawnEntity(new EntityDebugVector(world, new Vector3f(rightArmOrigin), new Vector3f(dX, dY, dZ), 2));
			}
			{
				double blockHitX = hit == null ? 0 : hit.hitVec.x - primaryGunOrigin.x;
				double blockHitY = hit == null ? 0 : hit.hitVec.y - primaryGunOrigin.y;
				double blockHitZ = hit == null ? 0 : hit.hitVec.z - primaryGunOrigin.z;

				//If the target is nearer than the block hit or there was no block
				if(hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK || dX * dX + dY * dY + dZ * dZ < blockHitX * blockHitX + blockHitY * blockHitY + blockHitZ * blockHitZ)
				{
					
					if(rand.nextInt(5) == 0)
						usingLeft = !usingLeft;
				}
				//Otherwise, move closer
				else
				{
//					//If we have a target, move towards it and look at it
//					moveX = (float)(target.posX - posX);
//					moveZ = (float)(target.posZ - posZ);
//					
//
//					Vector3f intent = new Vector3f(moveX, 0, moveZ);
//
//					if(Math.abs(intent.lengthSquared()) > 0.1)
//					{
//						intent.normalise();
//
//						//intent = axes.findLocalVectorGlobally(intent);
//
//						Vector3f intentOnLegAxes = legAxes.findGlobalVectorLocally(intent);
//						float intentAngle = (float)Math.atan2(intent.z, intent.x) * 180F / 3.14159265F;
//						float angleBetween = intentAngle - legAxes.getYaw();
//						if(angleBetween > 180F) angleBetween -= 360F;
//						if(angleBetween < -180F) angleBetween += 360F;
//
//						float signBetween = Math.signum(angleBetween);
//						angleBetween = Math.abs(angleBetween);
//
//						if(angleBetween > 0.1)
//						{
//							legAxes.rotateGlobalYaw(Math.min(angleBetween, type.rotateSpeed) * signBetween);
//						}
//
//						intent.scale((type.moveSpeed * data.engine.engineSpeed * speedMultiplier()) * (4.3F / 20F));
//
//						if(isPartIntact(EnumDriveablePart.hips))
//						{
//							//Move!
//							Vector3f.add(actualMotion, intent, actualMotion);
//						}
					//}
				}
			}
		}

	}
	private boolean isBetterTarget(Entity currentTarget, double distToCurrentTarget, Entity potentialTarget, double distToPotentialTarget)
	{
		if(potentialTarget instanceof EntityPlayer && distToPotentialTarget < distToCurrentTarget && distToPotentialTarget < targetingRange * targetingRange)
			return true;
		return false;
	}
	
}
