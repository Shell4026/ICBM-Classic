package icbm.classic.datafix;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.refs.ICBMEntities;
import icbm.classic.api.refs.ICBMExplosives;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.config.missile.ConfigMissile;
import icbm.classic.content.missile.logic.flight.BallisticFlightLogicOld;
import icbm.classic.content.missile.logic.flight.DeadFlightLogic;
import icbm.classic.content.missile.logic.targeting.BallisticTargetingData;
import icbm.classic.content.missile.logic.targeting.BasicTargetData;
import icbm.classic.lib.NBTConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.datafix.IFixableData;

public class EntityMissileDataFixer implements IFixableData
{
    private static final String ENTITY_ID = "id";

    public static final EntityMissileDataFixer INSTANCE = new EntityMissileDataFixer();

    // TODO wrap everything in optionals in case any field is null

    @Override
    public CompoundNBT fixTagCompound(CompoundNBT existingSave)
    {
        // Missile rewrite [v4.2.0] changed registry name and with it the entire save/load, save is based on [v4.0.1] code
        if (existingSave.hasKey(ENTITY_ID) && existingSave.getString(ENTITY_ID).equalsIgnoreCase(ICBMClassicAPI.ID + ":missile"))
        {
            // Update registry name
            existingSave.setString("id", ICBMEntities.MISSILE_EXPLOSIVE.toString());

            // EntityProjectile v4 to missileRewrite
            convertProjectileTags(existingSave);

            // EntityMissile
            convertEntityMissileTags(existingSave);

            // EntityExplosiveMissile
            convertExplosiveData(existingSave);

            // Remove missile data
            DataFixerHelpers.removeTags(existingSave, "missileType");
            DataFixerHelpers.removeTags(existingSave, "target", "targetHeight");
            DataFixerHelpers.removeTags(existingSave, "launcherPos");
            DataFixerHelpers.removeTags(existingSave, "acceleration", "lockHeight", "preLaunchSmokeTimer");
            DataFixerHelpers.removeTags(existingSave, "additionalMissileData", "explosiveID");
            DataFixerHelpers.removeTags(existingSave, "sourcePos", "Shooter-UUID");
            DataFixerHelpers.removeTags(existingSave, "xTilePos", "yTilePos", "zTilePos");
            DataFixerHelpers.removeTags(existingSave, "sideTilePos", "inTileState");
            DataFixerHelpers.removeTags(existingSave, "life", "ticksInAir");
        }
        else if (existingSave.hasKey(ENTITY_ID) && existingSave.getString(ENTITY_ID).equalsIgnoreCase(ICBMEntities.MISSILE_EXPLOSIVE.toString())) {

            // Move hypersonic to sonic
            if(existingSave.hasKey("explosive")) {

                final CompoundNBT stackSave = existingSave.getCompoundTag(NBTConstants.EXPLOSIVE_STACK);
                if(stackSave.hasKey("Damage")) {
                    final int damage = stackSave.getInteger("Damage");

                    if(damage == ICBMExplosives.HYPERSONIC.getRegistryID()) {

                        // Change to sonic id
                        stackSave.setInteger("Damage", ICBMExplosives.SONIC.getRegistryID());

                        // Wipe out custom data, shouldn't exist but could crash a 3rd-party's code
                        stackSave.removeTag("tag");
                        stackSave.removeTag("ForgeCaps");
                    }
                }
            }
        }
        return existingSave;
    }
    private void convertEntityMissileTags(CompoundNBT existingSave) {

        // missileType -> int
        // 0 -> launcher
        // 1 -> cruise
        // 2 -> rpg
        // 3 -> homing
        // 4 -> dead_aim
        final int missileType = existingSave.getInteger("missileType");

        final CompoundNBT missile = new CompoundNBT();
        existingSave.setTag("missile", missile);

        // Set doFlight to true as it can be assumed any missile in world is moving
        final CompoundNBT missileFlags = new CompoundNBT();
        missile.setTag("flags", missileFlags);
        missileFlags.setByte("doFlight", (byte)1);

        convertTargetData(existingSave, missile, missileType);
        convertFlightLogic(existingSave, missile, missileType);
        convertMissileSource(existingSave, missile, missileType);
    }

    private void convertExplosiveData(CompoundNBT existingSave) {
        //additionalMissileData -> compound
        //explosiveID -> int
        final int explosiveId = existingSave.getInteger("explosiveID");
        final IExplosiveData data = ICBMClassicAPI.EXPLOSIVE_REGISTRY.getExplosiveData(explosiveId);
        final ItemStack stack = ICBMClassicAPI.EX_MISSILE_REGISTRY.getDeviceStack(data);

        existingSave.setTag("explosive", stack.serializeNBT());
    }

    private void convertTargetData(CompoundNBT existingSave, CompoundNBT missile, int missileType) {
        if(existingSave.hasKey("target")) {
            final CompoundNBT targetData = new CompoundNBT();
            missile.setTag("target", targetData);

            final CompoundNBT data = new CompoundNBT();
            targetData.setTag("data", data);

            // Set id, old saves would have used missile type for this
            targetData.setString("id", missileType == 0 ? BallisticTargetingData.REG_NAME.toString() : BasicTargetData.REG_NAME.toString());

            // target -> compound with xyz
            data.setDouble("x", existingSave.getCompoundTag("target").getDouble("x"));
            data.setDouble("y", existingSave.getCompoundTag("target").getDouble("y"));
            data.setDouble("z", existingSave.getCompoundTag("target").getDouble("z"));

            // targetHeight -> int
            if (missileType == 0) {
                data.setDouble("impact_height", existingSave.getInteger("targetHeight"));
            }
        }
    }

    private void convertFlightLogic(CompoundNBT existingSave, CompoundNBT missile, int missileType) {
        final CompoundNBT targetData = new CompoundNBT();
        missile.setTag("flight", targetData);

        final CompoundNBT data = new CompoundNBT();
        targetData.setTag("data", data);

        // Set id, old saves would have used missile type for this
        targetData.setString("id", missileType == 0 ? BallisticFlightLogicOld.REG_NAME.toString() : DeadFlightLogic.REG_NAME.toString()); //TODO verify for cruise and hand held

        // Ballistic missile
        if(missileType == 0) {

            int preLauncherTime = existingSave.getInteger("preLaunchSmokeTimer");
            double lockHeight = existingSave.getDouble("lockHeight");

            // Assume always false so we force reset calculations
            final CompoundNBT flags = new CompoundNBT();
            data.setTag("flags", flags);
            flags.setByte("flight_started",  (byte)0);


            final CompoundNBT inputs = new CompoundNBT();
            data.setTag("inputs", inputs);

            // Use vanilla Pos as start to keep missiles from completely missing targets
            // ...will still have odd results due to math changes
            inputs.setDouble("start_x", existingSave.getTagList("Pos", 6).getDoubleAt(0));
            inputs.setDouble("start_y", existingSave.getTagList("Pos", 6).getDoubleAt(1));
            inputs.setDouble("start_z", existingSave.getTagList("Pos", 6).getDoubleAt(2));

            // target -> compound with xyz, using it for end
            inputs.setDouble("end_x", existingSave.getCompoundTag("target").getDouble("x"));
            inputs.setDouble("end_y", existingSave.getCompoundTag("target").getDouble("y"));
            inputs.setDouble("end_z", existingSave.getCompoundTag("target").getDouble("z"));

            // lockHeight -> double
            // preLaunchSmokeTimer -> int
            final CompoundNBT timers = new CompoundNBT();
            data.setTag("timers", timers);
            timers.setInteger("engine_warm_up", preLauncherTime);
            timers.setDouble("lock_height", lockHeight);
        }
        // Cruise
        else if(missileType == 1) {
            data.setInteger("fuel", ConfigMissile.CRUISE_FUEL);
        }
        // RPG
        else if(missileType == 2) {
            data.setInteger("fuel", ConfigMissile.HANDHELD_FUEL);
        }
    }

    private void convertMissileSource(CompoundNBT existingSave, CompoundNBT missile, int missileType) {

        // Ballistic
        if(missileType == 0) {

            final CompoundNBT missileSource = new CompoundNBT();
            missile.setTag("source", missileSource);

            final CompoundNBT pos = new CompoundNBT();
            missileSource.setTag("pos", pos);

            // launcherPos -> compound with x y z
            pos.setDouble("x", existingSave.getCompoundTag("launcherPos").getDouble("x"));
            pos.setDouble("y", existingSave.getCompoundTag("launcherPos").getDouble("y"));
            pos.setDouble("z", existingSave.getCompoundTag("launcherPos").getDouble("z"));

            // old save didn't store source dimension, this might cause odd event messages in the future
        }
        // cruise missiles || rpg missile
        else  if(missileType == 1 || missileType == 2) {
            final CompoundNBT missileSource = new CompoundNBT();
            missile.setTag("source", missileSource);

            final CompoundNBT pos = new CompoundNBT();
            missileSource.setTag("pos", pos);

            // launcherPos -> compound with x y z
            pos.setDouble("x", existingSave.getCompoundTag("sourcePos").getDouble("x"));
            pos.setDouble("y", existingSave.getCompoundTag("sourcePos").getDouble("y"));
            pos.setDouble("z", existingSave.getCompoundTag("sourcePos").getDouble("z"));

            // old save didn't store source dimension, this might cause odd event messages in the future
        }
    }

    private void convertProjectileTags(CompoundNBT existingSave) {

        // inGround -> byte
        final CompoundNBT flags = new CompoundNBT();
        existingSave.setTag("flags", flags);
        flags.setByte("ground", existingSave.getByte("inGround"));
        existingSave.removeTag("inGround");

        // inTileState -> int
        // xTilePos -> int
        // yTilePos -> int
        // zTilePos -> int
        // sideTilePos -> byte
        final CompoundNBT ground = new CompoundNBT();
        existingSave.setTag("ground", ground);

        final CompoundNBT tilePos = new CompoundNBT();
        tilePos.setInteger("x", existingSave.getInteger("xTilePos"));
        tilePos.setInteger("y", existingSave.getInteger("yTilePos"));
        tilePos.setInteger("z", existingSave.getInteger("zTilePos"));
        ground.setTag("pos", tilePos);

        ground.setByte("side", existingSave.getByte("sideTilePos"));
        final int oldBlockState = existingSave.getInteger("inTileState");
        final BlockState blockState = Block.getStateById(oldBlockState);
        ground.setTag("state", NBTUtil.writeBlockState(new CompoundNBT(), blockState));

        // life -> short
        // ticksInAir -> int
        final CompoundNBT ticks = new CompoundNBT();
        existingSave.setTag("ticks", ticks);
        ticks.setInteger("air", existingSave.getInteger("ticksInAir"));
        ticks.setInteger("ground", existingSave.getInteger("life"));
    }

    @Override
    public int getFixVersion()
    {
        return 2;
    }
}
