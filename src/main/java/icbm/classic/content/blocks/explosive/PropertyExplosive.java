package icbm.classic.content.blocks.explosive;

import icbm.classic.api.ICBMClassicAPI;
import icbm.classic.api.reg.IExplosiveData;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Dark(DarkGuardsman, Robin) on 1/6/2019.
 */
public class PropertyExplosive implements IProperty<IExplosiveData>
{
    @Override
    public String getName()
    {
        return "explosive";
    }

    @Override
    public Collection<IExplosiveData> getAllowedValues()
    {
        return ICBMClassicAPI.EXPLOSIVE_REGISTRY.getExplosives();
    }

    @Override
    public Class<IExplosiveData> getValueClass()
    {
        return IExplosiveData.class;
    }

    @Override
    public Optional<IExplosiveData> parseValue(String value)
    {
        return Optional.ofNullable(ICBMClassicAPI.EXPLOSIVE_REGISTRY.getExplosiveData(new ResourceLocation(value.replace("_", ":"))));
    }

    @Override
    public String getName(IExplosiveData value)
    {
        return value.getRegistryKey().toString().replaceAll(":", "_");
    }
}
