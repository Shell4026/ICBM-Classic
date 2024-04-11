package icbm.classic.lib.explosive.reg;

import icbm.classic.api.EnumTier;
import icbm.classic.api.actions.data.ActionFields;
import icbm.classic.api.actions.data.IActionFieldProvider;
import icbm.classic.api.actions.cause.IActionSource;
import icbm.classic.api.explosion.IBlastFactory;
import icbm.classic.api.explosion.IBlastInit;
import icbm.classic.api.reg.IExplosiveData;
import icbm.classic.api.reg.content.IExplosiveContentRegistry;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles storing data about an explosive in the {@link ExplosiveRegistry}
 *
 *
 * Created by Dark(DarkGuardsman, Robert) on 1/4/19.
 */
@ToString(of={"regName", "id"})
@RequiredArgsConstructor
@Data
public class ExplosiveData implements IExplosiveData
{
    @Nonnull
    public final ResourceLocation regName;
    /** Will be removed in 1.13 */
    @Deprecated
    private final int id;
    @Nonnull
    private  final EnumTier tier;
    @Nonnull
    private final IBlastFactory blastCreationFactory;

    private final Set<ResourceLocation> enabledContent = new HashSet();

    private boolean enabled = true;

    @Override
    public ResourceLocation getRegistryKey()
    {
        return regName;
    }

    @Override
    public int getRegistryID()
    {
        return id;
    }

    @Override
    @Nonnull
    public IBlastInit create(World world, double x, double y, double z, @Nonnull IActionSource source, @Nullable IActionFieldProvider fieldAccessors) {
        final IBlastInit blast = blastCreationFactory.create(world, x, y, z)
            .setExplosiveData(this)
            .setActionSource(source);

        if(fieldAccessors != null && fieldAccessors.hasField(ActionFields.BLAST_SIZE)) {
            blast.setBlastSize(fieldAccessors.getValue(ActionFields.BLAST_SIZE));
        }

        return blast;
    }

    @Override
    public boolean onEnableContent(ResourceLocation contentID, IExplosiveContentRegistry registry)
    {
        enabledContent.add(contentID);
        return true;
    }

    @Override
    public boolean equals(Object object)
    {
        if(object instanceof ExplosiveData)
        {
            return ((ExplosiveData) object).id == id;
        }
        return false;
    }

    @Override
    public int compareTo(IExplosiveData o)
    {
        return Integer.compare(getRegistryID(), o.getRegistryID());
    }
}
