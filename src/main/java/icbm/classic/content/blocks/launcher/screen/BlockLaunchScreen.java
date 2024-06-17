package icbm.classic.content.blocks.launcher.screen;

import icbm.classic.ICBMClassic;
import icbm.classic.api.ICBMClassicHelpers;
import icbm.classic.api.caps.IGPSData;
import icbm.classic.content.blocks.launcher.network.ILauncherComponent;
import icbm.classic.content.blocks.launcher.network.LauncherNetwork;
import icbm.classic.lib.capability.gps.GPSDataHelpers;
import icbm.classic.prefab.tile.BlockICBM;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 *
 * Created by Dark(DarkGuardsman, Robin) on 1/16/2018.
 */
public class BlockLaunchScreen extends BlockICBM
{
    public BlockLaunchScreen()
    {
        super("launcherscreen");
        this.dropInventory = true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            final TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileLauncherScreen)
            {
                final TileLauncherScreen screen = (TileLauncherScreen) tileEntity;
                final ItemStack stack = player.getHeldItem(hand);
                final IGPSData gpsData = ICBMClassicHelpers.getGPSData(stack);
                if (GPSDataHelpers.handlePlayerInteraction(gpsData, player, screen::setTarget))
                {
                    return true;
                }
                else if(stack.getItem() == Items.STONE_AXE) {
                    final LauncherNetwork network = screen.getNetworkNode().getNetwork();
                    player.sendMessage(new StringTextComponent("Network: " + network));
                    player.sendMessage(new StringTextComponent("L: " + network.getLaunchers().size()));
                }
                else
                {
                    player.openGui(ICBMClassic.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileLauncherScreen();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ILauncherComponent)
        {
            ((ILauncherComponent) tile).getNetworkNode().onTileRemoved();
        }
        super.breakBlock(world, pos, state);
    }
}
