package icbm.classic.content.items;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import icbm.classic.ICBMClassic;
import icbm.classic.TestBase;
import icbm.classic.content.entity.EntityGrenade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.*;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Dark(DarkGuardsman, Robin) on 12/25/2019.
 */
@TestWithResources
public class ItemGrenadeTest extends TestBase
{
    @GivenJsonResource("data/saves/4.0.0/itemstack_Greande_shrapnel.json")
    CompoundNBT version4save;

    private static Item item;

    @BeforeAll
    public static void beforeAllTests()
    {
        // Register block for placement
        ForgeRegistries.ITEMS.register(item = new ItemGrenade().setName("grenade").setCreativeTab(ICBMClassic.CREATIVE_TAB));
    }

    @Test
    void onItemRightClick_normal()
    {
        final PlayerEntity player = spy(testManager.getPlayer());
        final World world = testManager.getWorld();
        final ItemStack stack = new ItemStack(item, 1, 0);

        //Set player inventory and held
        player.setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);

        //Prevent normal set active logic as this will crash if run
        doNothing().when(player).setActiveHand(Hand.MAIN_HAND);

        //Trigger action
        final ActionResult<ItemStack> result = item.onItemRightClick(world, player, Hand.MAIN_HAND);

        //Main hand should be set
        verify(player, times(1)).setActiveHand(Hand.MAIN_HAND);

        //Check result
        Assertions.assertEquals(item, result.getResult().getItem());
        Assertions.assertEquals(ActionResultType.SUCCESS, result.getType());
    }

    @Test
    void onPlayerStoppedUsing_normal()
    {
        final PlayerEntity player = testManager.getPlayer();
        final World world = spy(testManager.getWorld());

        ItemStack stack = new ItemStack(item, 2, 0);

        //Trigger action
        item.onPlayerStoppedUsing(stack, world, player, ItemGrenade.MAX_USE_DURATION);

        //Check that we spawned the entity
        final ArgumentCaptor<Entity> entityArgumentCaptor = ArgumentCaptor.forClass(Entity.class);
        verify(world, times(1)).spawnEntity(entityArgumentCaptor.capture());

        //Validate we spawned entity with the right settings
        Entity entity = entityArgumentCaptor.getValue();
        Assertions.assertTrue(entity instanceof EntityGrenade);
        Assertions.assertSame(player, ((EntityGrenade)entity).getThrower());
        Assertions.assertEquals(stack.getItem(), ((EntityGrenade)entity).explosive.toStack().getItem());

        //Check that we played a sound
        verify(world, times(1)).playSound(Mockito.isNull(), eq(player.posX), eq(player.posY), eq(player.posZ), eq(SoundEvents.ENTITY_TNT_PRIMED), eq(SoundCategory.BLOCKS), Mockito.anyFloat(), Mockito.anyFloat());

        //Check that we called shrink
        Assertions.assertEquals(1, stack.getCount());
    }

    @Test
    @DisplayName("Loads from old version 4.0.0 save file")
    void loadFromVersion4() {

        // Validate we have a test file
        Assertions.assertNotNull(version4save);

        // Load stack
        final ItemStack stack = new ItemStack(version4save);

        // Create compare stack
        final ItemStack expected = new ItemStack(item, 1, 1);

        // Compare stacks
        Assertions.assertTrue(ItemStack.areItemsEqual(expected, stack));

        // Confirm capability returns the correct explosive
        assertExplosive(stack, "icbmclassic:shrapnel", new CompoundNBT());
    }
}
