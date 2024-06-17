package icbm.classic.lib.saving.nodes;

import net.minecraft.nbt.IntNBT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SaveNodeIntegerTest
{
    final SaveNodeInteger<IntSaveThing> node = new SaveNodeInteger<IntSaveThing>("i",
        (o) -> o.i,
        (o, i) -> o.i = i
    );

    @Test
    void save() {
       final IntSaveThing thing = new IntSaveThing();
       thing.i = 245;

       final IntNBT save = node.save(thing);

        Assertions.assertEquals(245, save.getInt());
    }

    @Test
    void load() {
        final IntSaveThing thing = new IntSaveThing();
        final IntNBT save = new IntNBT(123);

        node.load(thing, save);

        Assertions.assertEquals(123, thing.i);
    }

    static class IntSaveThing {
        public int i;
    }
}
