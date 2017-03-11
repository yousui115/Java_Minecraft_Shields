package yousui115.shields.event;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shield.event.BashEvent;
import yousui115.shield.event.GuardEvent;
import yousui115.shield.event.GuardMeleeEvent;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldAction;
import yousui115.shields.item.ItemShields.EnumShieldState;
import yousui115.shields.util.SEnchants;
import yousui115.shields.util.SUtils;


public class ShieldHndls
{
    /**
     * ■ガード可能な攻撃をガードした
     * @param event
     */
    @SubscribeEvent
    public void doGuard(GuardEvent event)
    {
        if (event.blocker == null || event.source == null) { return; }

        //■サーバ側だけで動かす
        if (event.blocker.worldObj.isRemote) { return; }

        ItemStack stack = event.blocker.getActiveItemStack();
        if (stack == null) { return; }

        ItemShields.EnumShieldState state = ItemShields.getShieldState(stack);

        if (state != null)
        {
            //■JG出来ない盾
            if (state.canJG() == false &&
                stack.getItem() instanceof ItemShields)
            {
                event.isJG = false;
            }

            //■炎の盾
            if (state == EnumShieldState.FLAME && event.source.isFireDamage())
            {
                event.amount -= 1;
                event.blocker.extinguish();
            }
        }

        //■盾にダメージを与えるだけの処理
        EntityLivingBase blocker = event.blocker;
        DamageSource source = event.source;

        //■盾の乙女 効果
        if (event.isGuard && EnchantmentHelper.getEnchantmentLevel(SEnchants.ENCH_MAIDEN, stack) == 1)
        {
            event.amount -= 1;
        }

        if (!event.isJG && blocker instanceof EntityPlayer)
        {
            SUtils.damageShield((EntityPlayer)blocker, event.amount);
        }
    }

    /**
     * ■直接攻撃をガードした
     * @param event
     */
    @SubscribeEvent
    public void doJG(GuardMeleeEvent event)
    {
        if (event.blocker == null || event.attacker == null) { return; }
        if (event.blocker.worldObj.isRemote) { return ; }

        ItemStack stack = event.blocker.getActiveItemStack();
        if (stack == null) { return; }

        ItemShields.EnumShieldState state = ItemShields.getShieldState(stack);
        if (state == null) { return; }

        if (event.isJG                  //ジャストガード時
            && event.attacker != null   //敵が存在する
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
        if (event.basher == null || event.victim == null) { return; }
        if (event.basher.worldObj.isRemote) { return; }

        ItemStack stack = event.basher.getActiveItemStack();
        if (stack == null) { return; }

        ItemShields.EnumShieldState state = ItemShields.getShieldState(stack);

        if (state == null) { return; }

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
