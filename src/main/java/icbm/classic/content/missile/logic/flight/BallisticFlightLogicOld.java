package icbm.classic.content.missile.logic.flight;

import icbm.classic.ICBMClassic;
import icbm.classic.ICBMConstants;
import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.missiles.IMissile;
import icbm.classic.api.missiles.parts.IMissileFlightLogic;
import icbm.classic.api.missiles.parts.IMissileTarget;
import icbm.classic.api.reg.obj.IBuilderRegistry;
import icbm.classic.config.missile.ConfigMissile;
import icbm.classic.content.missile.entity.EntityMissile;
import icbm.classic.content.missile.entity.explosive.EntityExplosiveMissile;
import icbm.classic.content.missile.tracker.MissileTrackerHandler;
import icbm.classic.lib.buildable.BuildableObject;
import icbm.classic.lib.saving.NbtSaveHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @deprecated replaced with modular flight systems, kept for loading old save data
 */
@Deprecated
public class BallisticFlightLogicOld extends BuildableObject<BallisticFlightLogicOld, IBuilderRegistry<IMissileFlightLogic>> implements IMissileFlightLogic
//TODO remove after we update beyond 1.12 or confident no more save data is using it
{
    //TODO recode to break apart movement into sub-logic
    //  Change silo startup to act as a delayed launch
    //  Have it switch to lock height flight logic next
    //  Then have it switch to the actual ballistic flight logic next
    //  Idea will be to cleanup the code and allow for better control in each version

    public static final ResourceLocation REG_NAME = new ResourceLocation(ICBMConstants.DOMAIN, "ballistic");

    /**
     * Ticks to animate slowly rising from the launcher
     */
    public static final int PAD_WARM_UP_TIME = 20 * 2; //TODO add config
    public static final double MISSILE_CLIMB_HEIGHT = 2; //TODO add config

    public boolean hasStartedFlight = false;

    /**
     * Height Y to wait before starting arc
     */
    public double lockHeight = 0;
    /**
     * Tick runtime of flight arc
     */
    private int missileFlightTime;
    /**
     * Motion Y acceleration for arc to work
     */
    private float acceleration;


    /**
     * Timer for missile to wait on pad before climbing
     */
    private int padWarmUpTimer = PAD_WARM_UP_TIME;

    /**
     * Distance to slowly climb before starting normal path
     */
    private double climbHeight = MISSILE_CLIMB_HEIGHT;

    /**
     * Difference in distance from target, used as acceleration
     */
    private double deltaPathX, deltaPathY, deltaPathZ;
    private double startX, startY, startZ;
    private double endX, endY, endZ;

    private boolean flightUpAlways = false;

    private int ticksFlight = 0;

    private boolean wasSimulationBlocked = false;

    public BallisticFlightLogicOld(int lockHeight) {
        this();
        this.lockHeight = lockHeight;
    }

    public BallisticFlightLogicOld() {
        super(REG_NAME, ICBMClassicAPI.MISSILE_FLIGHT_LOGIC_REGISTRY, SAVE_LOGIC);
    }

    @Override
    public void calculateFlightPath(final World world, double startX, double startY, double startZ, final IMissileTarget targetData)
    {
        //Record start and end position
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;

        if(targetData != null)
        {
            this.endX = targetData.getX();
            this.endY = targetData.getY();
            this.endZ = targetData.getZ();
        }
    }

    //TODO wire IMissile to connect to ILauncher so we can get launcher source and let the launcher know when we are clear
    //TODO code launcher to not reload until clear, use the all clear flag from the missile combined with collision checks and dead checks

    protected void calculatePath()
    {
        //TODO rebuild to calculate arc up to maxHeight and move at a fixed speed instead of speed of sound+++++
        //TODO once it reaches maxHeight have it fly flat to make for a smoother player riding experience
        //TODO at end of arc if we can't fix the target offset have the missile fly at an angle strait at the target to fix accuracy issues

        // Calculate the distance difference of the missile
        this.deltaPathX = endX - startX;
        this.deltaPathY = endY - startY;
        this.deltaPathZ = endZ - startZ;

        // TODO: Calculate parabola and relative out the targetHeight.
        // Calculate the power required to reach the target co-ordinates
        // Ground Displacement
        final float flatDistance = (float)Math.sqrt(deltaPathX * deltaPathX + deltaPathZ * deltaPathZ);

        if(flatDistance < 200) {

            //Path constants
            final float ticksPerMeterFlat = 2f;
            final float heightToDistanceScale = flatDistance > 1000 ? 3f : 1f;
            final float maxHeight = 1000f;
            final float initialArcHeight = flatDistance > 100 ? 160f : 0;

            // Parabolic Height
            // Ballistic flight vars
            final float arcHeightMax = Math.min(maxHeight, initialArcHeight + (flatDistance * heightToDistanceScale));

            // Flight time
            missileFlightTime = (int) Math.ceil(ticksPerMeterFlat * flatDistance);

            // Acceleration
            double heightToDistance = arcHeightMax / flatDistance;
            double heightToTime = arcHeightMax / missileFlightTime;
            double timeToDistance = missileFlightTime / flatDistance;
            this.acceleration = (float) (((arcHeightMax - heightToDistance) * heightToDistance) / (missileFlightTime / timeToDistance) / (heightToTime * flatDistance));
        }
        // If over 200 assume we will missile simulate rather than arc
        else {
            flightUpAlways = true;
        }
    }

    @Override
    public void onEntityTick(Entity entity, IMissile missile, int ticksInAir)
    {
        //Warm up on pad for nice animation
        if (padWarmUpTimer > 0) // Replaced with WarmupFlightLogic
        {
            padWarmUpTimer--;
            idleMissileOnPad(entity, ticksInAir);
        }
        //Slowly climb out of the launcher TODO get climb height from launcher
        else if (climbHeight > 0) // Replaced with MoveDirectionLogic
        {
            doSlowClimb(entity, ticksInAir);
        }
        else if (this.lockHeight > 0) // Replaced with MoveDirectionLogic
        {
            handleLockHeight(entity, ticksInAir);
        }
        //Starts the missile into normal flight
        else if (!hasStartedFlight) {

            hasStartedFlight = true;

            this.startX = entity.posX;
            this.startY = entity.posY;
            this.startZ = entity.posZ;

            calculatePath();

            if(!flightUpAlways) {

                entity.motionY = this.acceleration * ((float) missileFlightTime / 2f);

                entity.motionX = this.deltaPathX / missileFlightTime;
                entity.motionZ = this.deltaPathZ / missileFlightTime;
            }
            else {
                final float flatDistance = (float)Math.sqrt(deltaPathX * deltaPathX + deltaPathZ * deltaPathZ);
                final float hortSpeed = 0.05f;
                final float vertSpeed = 2f;
                entity.motionX = (this.deltaPathX / flatDistance) * hortSpeed;
                entity.motionZ = (this.deltaPathZ / flatDistance) * hortSpeed;
                entity.motionY = vertSpeed;
            }
        }
        //Normal path logic
        else {
            runFlightLogic(entity, ticksInAir);
        }
    }

    protected void runFlightLogic(Entity entity, int ticksInAir)
    {
        ticksFlight++;

        if (!entity.world.isRemote)
        {
            // Apply gravity
            if(!flightUpAlways) {
                entity.motionY -= this.acceleration;
            }

            // Cut off x-z motion to prevent missing target
            if(Math.abs(entity.posX - endX) <= 0.1f && Math.abs(entity.posZ - endZ) <= 0.1f) {
                entity.motionX = 0;
                entity.motionZ = 0;
            }

            // Update animate rotations
            alignWithMotion(entity);

            // Sim system
            if (entity instanceof EntityExplosiveMissile && shouldSimulate(entity))
            {
                wasSimulationBlocked = !MissileTrackerHandler.simulateMissile((EntityExplosiveMissile) entity); //TODO add ability to simulate any entity
            }
        }
    }

    @Override
    public boolean canSafelyExitLogic() {
        return hasStartedFlight;
    }

    protected void handleLockHeight(Entity entity, int ticksInAir)
    {
        entity.motionY += 0.1f;
        entity.motionX = 0;
        entity.motionZ = 0;
        this.lockHeight -= entity.motionY; //TODO fix to account for slow animation climb
    }

    protected void alignWithMotion(Entity entity)
    {
        entity.rotationPitch = (float) (Math.atan(entity.motionY / (Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ))) * 180 / Math.PI);
        // Look at the next point
        entity.rotationYaw = (float) (Math.atan2(entity.motionX, entity.motionZ) * 180 / Math.PI);
    }

    /**
     * Has the missile slow climb up before start full motion
     *
     * @param entity     representing the missile
     * @param ticksInAir the missile has been in the air
     */
    protected void doSlowClimb(Entity entity, int ticksInAir)
    {
        entity.motionY += 0.005f; //TODO add config
        lockHeight -= entity.motionY;
        climbHeight -= entity.motionY;
    }

    /**
     * Has the missile wait on the pad while it's engines start and generate a lot of smoke
     *
     * @param entity     representing the missile
     * @param ticksInAir the missile has been in the air
     */
    protected void idleMissileOnPad(Entity entity, int ticksInAir)
    {
        entity.rotationPitch = entity.prevRotationPitch = 90;
        ICBMClassic.proxy.spawnPadSmoke(entity, this, ticksInAir);
    }

    @Override
    public boolean shouldRunEngineEffects(Entity entity) {
        return padWarmUpTimer <= 0;
    }

    protected boolean shouldSimulate(Entity entity)
    {
        if (wasSimulationBlocked || EntityMissile.hasPlayerRiding(entity))
        {
            return false;
        }
        else if (entity.posY >= ConfigMissile.SIMULATION_EXIT_HEIGHT)
        {
            return true;
        }

        final BlockPos futurePos = predictPosition(entity, BlockPos::new, 2);

        //About to enter an unloaded chunk
        return !entity.world.isAreaLoaded(entity.getPosition(), futurePos);
    }

    @Override
    public <V> V predictPosition(Entity entity, VecBuilderFunc<V> builder, int ticks)
    {
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;

        double motionY = entity.motionY;

        while (ticks-- > 0)
        {
            motionY -= this.acceleration;

            x += entity.motionX;
            y += motionY;
            z += entity.motionZ;
        }

        return builder.apply(x, y, z);
    }

    @Deprecated
    public int getPadWarmUpTimer()
    {
        return padWarmUpTimer;
    }

    @Override
    public boolean shouldDecreaseMotion(Entity entity)
    {
        //Disable gravity and friction
        return false;
    }

    private static final NbtSaveHandler<BallisticFlightLogicOld> SAVE_LOGIC = new NbtSaveHandler<BallisticFlightLogicOld>()
        //Stuck in ground data
        .addRoot("flags")
        /* */.nodeBoolean("flight_started", (bl) -> bl.hasStartedFlight, (bl, data) -> bl.hasStartedFlight = data)
        /* */.nodeBoolean("block_simulation", (bl) -> bl.wasSimulationBlocked, (bl, data) -> bl.wasSimulationBlocked = data)
        .base()
        .addRoot("inputs")
        /* */.nodeDouble("start_x", (bl) -> bl.startX, (bl, i) -> bl.startX = i)
        /* */.nodeDouble("start_y", (bl) -> bl.startY, (bl, i) -> bl.startY = i)
        /* */.nodeDouble("start_z", (bl) -> bl.startZ, (bl, i) -> bl.startZ = i)
        /* */.nodeDouble("end_x", (bl) -> bl.endX, (bl, i) -> bl.endX = i)
        /* */.nodeDouble("end_y", (bl) -> bl.endY, (bl, i) -> bl.endY = i)
        /* */.nodeDouble("end_z", (bl) -> bl.endZ, (bl, i) -> bl.endZ = i)
        .base()
        .addRoot("calculated")
        /* */.nodeInteger("flight_time", (bl) -> bl.missileFlightTime, (bl, data) -> bl.missileFlightTime = data)
        /* */.nodeFloat("acceleration", (bl) -> bl.acceleration, (bl, data) -> bl.acceleration = data)
        /* */.nodeDouble("delta_x", (bl) -> bl.deltaPathX, (bl, data) -> bl.deltaPathX = data)
        /* */.nodeDouble("delta_y", (bl) -> bl.deltaPathY, (bl, data) -> bl.deltaPathY = data)
        /* */.nodeDouble("delta_z", (bl) -> bl.deltaPathZ, (bl, data) -> bl.deltaPathZ = data)
        .base()
        .addRoot("timers")
        /* */.nodeInteger("engine_warm_up", (bl) -> bl.padWarmUpTimer, (bl, data) -> bl.padWarmUpTimer = data)
        /* */.nodeDouble("climb_height", (bl) -> bl.climbHeight, (bl, data) -> bl.climbHeight = data)
        /* */.nodeDouble("lock_height", (bl) -> bl.lockHeight, (bl, i) -> bl.lockHeight = i)
        /* */.nodeInteger("flight", (bl) -> bl.ticksFlight, (bl, i) -> bl.ticksFlight = i)
        .base();

}
