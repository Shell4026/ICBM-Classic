package icbm.classic.content.blocks.launcher.network;

import icbm.classic.api.actions.status.IActionStatus;
import icbm.classic.api.launcher.IMissileLauncher;
import icbm.classic.content.blocks.launcher.FiringPackage;
import lombok.Data;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

@Data
public class LauncherEntry {
    private final IMissileLauncher launcher;
    private final TileEntity host;
    private final Direction side;

    // Internal data, don't use in mods as this will be moved to launcher itself and exposed as an event list
    private IActionStatus lastFiringStatus;
    private FiringPackage lastFiringPackage;
    private long lastFiringTime;
}
