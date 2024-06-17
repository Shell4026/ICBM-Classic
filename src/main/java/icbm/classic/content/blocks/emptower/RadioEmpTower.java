package icbm.classic.content.blocks.emptower;

import icbm.classic.api.actions.cause.IActionCause;
import icbm.classic.api.radio.IRadioMessage;
import icbm.classic.api.radio.IRadioReceiver;
import icbm.classic.api.radio.IRadioSender;
import icbm.classic.api.radio.messages.ITriggerActionMessage;
import icbm.classic.content.missile.logic.source.cause.EntityCause;
import icbm.classic.lib.radio.imp.RadioTile;
import icbm.classic.lib.radio.messages.TextMessage;
import icbm.classic.prefab.FakeRadioSender;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class RadioEmpTower extends RadioTile<TileEMPTower> implements IRadioReceiver, INBTSerializable<CompoundNBT> {

    public static final String SUCCESS = "tile.emptower.radio.success";

    public RadioEmpTower(TileEMPTower host) {
        super(host);
    }

    @Override
    public void onMessage(IRadioSender sender, IRadioMessage packet) {
        if (canReceive(sender, packet)) {

            // Fire emp tower
            if(packet instanceof ITriggerActionMessage) {

                IActionCause cause = null;

                if(sender instanceof FakeRadioSender) {
                    //TODO add radio cause before player, pass in item used
                    cause =  new EntityCause(((FakeRadioSender) sender).player);
                }

                if(host.fire(cause)) {
                    sender.onMessageCallback(this, new TextMessage(getChannel(), SUCCESS));
                }
            }
        }
    }
}
