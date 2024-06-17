package icbm.classic.lib.capability.ex;

import com.builtbroken.mc.testing.junit.TestManager;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.junit.jupiter.api.*;

class CapabilityExplosiveEntityTest
{

    static TestManager testManager = new TestManager("missile", Assertions::fail);

    final World world = testManager.getWorld();

    @AfterAll
    public static void afterAllTests()
    {
        testManager.tearDownTest();
    }

    @AfterEach
    public void afterEachTest()
    {
        testManager.cleanupBetweenTests();
    }

    @Test
    void testEquals_sameItem_true()
    {
        final CapabilityExplosiveEntity capA = new CapabilityExplosiveEntity(new ZombieEntity(world));
        capA.setStack(new ItemStack(net.minecraft.item.Items.STONE_AXE));

        final CapabilityExplosiveEntity capB = new CapabilityExplosiveEntity(new ZombieEntity(world));
        capB.setStack(new ItemStack(net.minecraft.item.Items.STONE_AXE));

        Assertions.assertEquals(capA, capB);
    }

    @Test
    void testEquals_air_true()
    {
        final CapabilityExplosiveEntity capA = new CapabilityExplosiveEntity(new ZombieEntity(world));
        final CapabilityExplosiveEntity capB = new CapabilityExplosiveEntity(new ZombieEntity(world));
        Assertions.assertEquals(capA, capB);
    }

    @Test
    void testEquals_diffItem_false()
    {
        final CapabilityExplosiveEntity capA = new CapabilityExplosiveEntity(new ZombieEntity(world));
        capA.setStack(new ItemStack(net.minecraft.item.Items.STONE_AXE));

        final CapabilityExplosiveEntity capB = new CapabilityExplosiveEntity(new ZombieEntity(world));
        capB.setStack(new ItemStack(Items.STONE_PICKAXE));

        Assertions.assertNotEquals(capA, capB);
    }
}