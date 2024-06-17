package icbm.classic.lib.saving;

import net.minecraft.nbt.INBT;

public interface INbtSaveNode<In, Out extends INBT>
{
    String getSaveKey();

    Out save(In objectToSave);

    void load(In objectToLoad, Out save);
}
