package yousui115.shields.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shield.event.BashEvent;
import yousui115.shield.event.GuardEvent;
import yousui115.shield.event.GuardMeleeEvent;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldAction;
import yousui115.shields.item.ItemShields.EnumShieldState;


public class ShieldHndls
{
    /**
     * ■ガード可能な攻撃をガードした
     * @param event
     */
    @SubscribeEvent
    public void doGuard(GuardEvent event)
    {
        //■サーバ側だけで動かす
        if (event.blocker.worldObj.isRemote) { return; }

        ItemStack stack = event.blocker.getActiveItemStack();
        ItemShields.EnumShieldState state = ItemShields.getShieldState(stack);

        //■JG出来ない盾
        if (state != null && state.canJG == false)
        {
            event.isJG = false;
        }

        //■炎の盾
        if (state == EnumShieldState.FLAME &&
            event.source.isFireDamage())
        {
//            ObfuscationReflectionHelper.setPrivateValue(DamageSource.class, event.source, false, 18);
            event.amount -= 1;
            event.blocker.extinguish();
        }
    }

    /**
     * ■直接攻撃をガードした
     * @param event
     */
    @SubscribeEvent
    public void doJG(GuardMeleeEvent event)
    {
        if (event.blocker.worldObj.isRemote) { return ; }

        ItemStack stack = event.blocker.getActiveItemStack();
        ItemShields.EnumShieldState state = ItemShields.getShieldState(stack);

        if (event.isJG                  //ジャストガード時
            && event.attacker != null   //敵が存在する
            && state != null            //盾の性能が「反射」
            && state.action == ItemShields.EnumShieldAction.REFLECT)
        {
            if (!event.attacker.attackEntityAsMob(event.attacker))
            {
                //■特殊攻撃が無いMOBはこちら。
                event.attacker.attackEntityFrom(event.source, event.amount);
            }
        }
    }

    /**
     * ■バッシュが成功した
     * @param event
     */
    @SubscribeEvent
    public void doBash(BashEvent event)
    {
        if (event.basher.worldObj.isRemote) { return; }

        ItemStack stack = event.basher.getActiveItemStack();
        ItemShields.EnumShieldState state = ItemShields.getShieldState(stack);

        if (state.action == EnumShieldAction.FIRE)
        {
            event.victim.setFire(5);
        }
        else if (state.action == EnumShieldAction.FREEZE)
        {
            //■停止時間を倍に
            event.power *= 2;

            //■炎無効の敵に倍ダメージ
            if (event.victim.isImmuneToFire())
            {
                event.amount *= 2;
            }
        }

    }
}
