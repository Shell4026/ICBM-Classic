package icbm.classic.client.mapper;

import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.content.blocks.explosive.BlockExplosive;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.util.Direction;

import java.util.Map;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/21/2019.
 */
public class BlockModelMapperExplosive extends DefaultStateMapper
{
    private final Map<IExplosiveData, Map<Direction, net.minecraft.client.renderer.model.ModelResourceLocation>> models;
    private final ModelResourceLocation fallBack;

    public BlockModelMapperExplosive(Map<IExplosiveData, Map<Direction,ModelResourceLocation>> models, net.minecraft.client.renderer.model.ModelResourceLocation fallBack)
    {
        this.models = models;
        this.fallBack = fallBack;
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(BlockState state)
    {
        if(state.getPropertyKeys().contains(BlockExplosive.EX_PROP))
        {
            final IExplosiveData explosiveData = state.getValue(BlockExplosive.EX_PROP);
            if (explosiveData != null)
            {
                final Map<Direction,ModelResourceLocation> facingModelMap = models.get(explosiveData);
                if (facingModelMap != null)
                {
                    final net.minecraft.client.renderer.model.ModelResourceLocation modelResourceLocation = facingModelMap.get(state.getValue(BlockExplosive.ROTATION_PROP));
                    if (modelResourceLocation != null)
                    {
                        return modelResourceLocation;
                    }
                }
            }
        }
        return fallBack;
    }
}
