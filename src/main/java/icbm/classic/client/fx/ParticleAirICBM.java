package icbm.classic.client.fx;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Same as normal smoke, but doesn't move upwards on its own
 */
@OnlyIn(Dist.CLIENT)
public class ParticleAirICBM extends SmokeParticle //TODO break link to smoke particle
{
    private final IAnimatedSprite sprite;
    public ParticleAirICBM(World worldIn, double x, double y, double z, double vx, double vy, double vz, float scale, IAnimatedSprite spriteSet)
    {
        super(worldIn, x, y, z, vx, vy, vz, scale, spriteSet);
        this.sprite = spriteSet;
    }

    public ParticleAirICBM setAge(int age)
    {
        this.maxAge = age;
        return this;
    }

    public ParticleAirICBM setColor(float r, float g, float b, boolean addColorVariant)
    {
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;

        if (addColorVariant)
        {
            float colorVariant = (float) (Math.random() * 0.90000001192092896D);
            this.particleRed *= colorVariant;
            this.particleBlue *= colorVariant;
            this.particleGreen *= colorVariant;
        }
        return this;
    }

    @Override
    public void tick() // same code as in vanilla particle, but the vertical velocity acceleration is set to 0
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.age++ >= this.age)
        {
            this.setExpired();
        }

        this.selectSpriteWithAge(this.sprite);
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.95999999D;
        this.motionY *= 0.95999999D;
        this.motionZ *= 0.95999999D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
}