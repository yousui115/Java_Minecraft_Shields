package yousui115.shields.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shield.event.GuardEvent;
import yousui115.shield.event.GuardMeleeEvent;
import yousui115.shields.item.ItemSShield;


public class ShieldHndls
{
    @SubscribeEvent
    public void doGuard(GuardEvent event)
    {
        //■サーバ側だけで動かす
        if (event.blocker.worldObj.isRemote) { return; }

        ItemStack stack = event.blocker.getActiveItemStack();
        ItemSShield.EnumShieldState state = ItemSShield.getShieldState(stack);

        if (state != null && state.canJG == false)
        {
            event.isJG = false;
        }
    }

    @SubscribeEvent
    public void doJG(GuardMeleeEvent event)
    {
        if (event.blocker.worldObj.isRemote) { return ; }

        ItemStack stack = event.blocker.getActiveItemStack();
        ItemSShield.EnumShieldState state = ItemSShield.getShieldState(stack);

        if (event.isJG                  //ジャストガード時
            && event.attacker != null   //敵が存在する
            && state != null            //盾の性能が「反射」
            && state.action == ItemSShield.EnumShieldAction.REFLECT)
        {
            if (!event.attacker.attackEntityAsMob(event.attacker))
            {
                //■特殊攻撃が無いMOBはこちら。
                event.attacker.attackEntityFrom(event.source, event.amount);
            }
        }
    }
}
