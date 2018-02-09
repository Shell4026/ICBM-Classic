package icbm.classic.content.explosive.ex;

import icbm.classic.content.explosive.blast.BlastRegen;
import icbm.classic.prefab.EnumTier;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExRejuvenation extends Explosion
{
    public ExRejuvenation()
    {
        super("rejuvenation", EnumTier.TWO);
    }

    @Override
    public void doCreateExplosion(World world, BlockPos pos, Entity entity)
    {
        new BlastRegen(world, entity, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 16).doExplode();
    }
}
