package yousui115.shields.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shields.item.ItemShields;

public class ShieldsHndls
{
    /**
     * ■盾にダメージを与えるだけの処理
     * @param event
     */
    @SubscribeEvent
    public void doDamageShield(LivingAttackEvent event)
    {
        EntityLivingBase blocker = event.getEntityLiving();
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        //■サーバサイドのみ
        if (blocker.worldObj.isRemote) { return; }

        if (amount > 0.0F && canBlockDamageSource(blocker, source))
        {
            if (blocker instanceof EntityPlayer)
            {
                boolean isBreak = damageShield((EntityPlayer)blocker, amount);
                if (isBreak) { event.setCanceled(true); }
            }
        }
    }

    private boolean canBlockDamageSource(EntityLivingBase livingIn, DamageSource dsIn)
    {
        if (!dsIn.isUnblockable() && livingIn.isActiveItemStackBlocking())
        {
            Vec3d posDS = dsIn.getDamageLocation();

            if (posDS != null)
            {
                Vec3d vecLivingLook = livingIn.getLook(1.0F);
                Vec3d vecLookFromDS = posDS.subtractReverse(new Vec3d(livingIn.posX, livingIn.posY, livingIn.posZ)).normalize();
                vecLookFromDS = new Vec3d(vecLookFromDS.xCoord, 0.0D, vecLookFromDS.zCoord);

                if (vecLookFromDS.dotProduct(vecLivingLook) < 0.0D)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean damageShield(EntityPlayer player, float damage)
    {
        boolean isBreak = false;

        if (damage >= 3.0F && player.getActiveItemStack() != null && player.getActiveItemStack().getItem() instanceof ItemShields)
        {
            ItemStack shield = player.getActiveItemStack();

            int i = 1 + MathHelper.floor_float(damage);
            shield.damageItem(i, player);

            if (shield.stackSize <= 0)
            {
                EnumHand enumhand = player.getActiveHand();
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, shield, enumhand);

                if (enumhand == EnumHand.MAIN_HAND)
                {
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, (ItemStack)null);
                }
                else
                {
                    player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, (ItemStack)null);
                }

//                this.activeItemStack = null;
                player.resetActiveHand();
                player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.worldObj.rand.nextFloat() * 0.4F);
                isBreak = true;
            }
        }

        return isBreak;
    }
}
