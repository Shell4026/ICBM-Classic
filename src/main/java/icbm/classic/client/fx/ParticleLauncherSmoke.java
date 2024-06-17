package icbm.classic.client.fx;

import icbm.classic.content.reg.BlockReg;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ParticleLauncherSmoke extends SmokeParticle
{
    public static Set<Block> blocksToIgnoreCollisions = new HashSet();

    public ParticleLauncherSmoke(World worldIn, double x, double y, double z, double vx, double vy, double vz, float scale, IAnimatedSprite sprite)
    {
        super(worldIn, x, y, z, vx, vy, vz, scale, sprite);
    }

    public ParticleLauncherSmoke setAge(int age)
    {
        this.maxAge = age;
        return this;
    }

    public ParticleLauncherSmoke setColor(float r, float g, float b, boolean addColorVariant)
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