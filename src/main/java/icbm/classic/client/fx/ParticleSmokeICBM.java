package icbm.classic.client.fx;

import icbm.classic.lib.transform.vector.Pos;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSmokeICBM extends SmokeParticle
{
    public ParticleSmokeICBM(World worldIn, Pos pos, double vx, double vy, double vz, float scale)
    {
        super(worldIn, pos.x(), pos.y(), pos.z(), vx, vy, vz, scale);
    }

    public ParticleSmokeICBM setAge(int age)
    {
        this.particleMaxAge = age;
        return this;
    }

    public ParticleSmokeICBM setColor(float r, float g, float b, boolean addColorVariant)
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
}