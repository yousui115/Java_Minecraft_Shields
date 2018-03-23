package yousui115.shields.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldState;
import yousui115.shields.util.SUtils;

public class MsgKeyHndl implements IMessageHandler<MsgKey, IMessage>
{
    /**
     * ■Client -> Server
     */
    @Override
    public IMessage onMessage(MsgKey message, MessageContext ctx)
    {
        EntityPlayer player = ctx.getServerHandler().player;

        ItemStack stack = player.getHeldItem(EnumHand.OFF_HAND);

        if (!SUtils.isEmptyStack(stack) && stack.getItem() instanceof ItemShields)
        {
            EnumShieldState state = ItemShields.getShieldState(stack);
            if (state == EnumShieldState.WOOD) { return null; }
            int states = ItemShields.getShieldStates(stack);

            int next = state.bit;
            do
            {
                next = next << 1;

                if (next == 0) { next = 0x1;}
            }
            while((next & states) != next);

            EnumShieldState stateNext = EnumShieldState.getStateFromBit(next);
            if (stateNext != null)
            {
                ItemShields.setShieldState(stack, stateNext);
                player.resetActiveHand();
            }
        }
        return null;
    }

}
