package yousui115.shields.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yousui115.shields.Shields;
import yousui115.shields.item.ItemShields;

public class SUtils
{
    public static final String SHIELDS_NBT_TAG = "shields";
    public static final String NBT_REPAIR_COUNT = "repair_count";

    public static boolean checkDamageShield(EntityLivingBase blockerIn, DamageSource sourceIn, float damageIn)
    {
        boolean isBreak = false;

        if (damageIn > 0.0F && SUtils.canBlockDamageSource(blockerIn, sourceIn))
        {
            if (blockerIn instanceof EntityPlayer)
            {
                ItemStack shield = blockerIn.getActiveItemStack();

                //■盾の乙女 効果
                if (EnchantmentHelper.getEnchantmentLevel(SEnchants.ENCH_MAIDEN, shield) == 1)
                {
                    damageIn -= 1;
                }

                isBreak = SUtils.damageShield((EntityPlayer)blockerIn, damageIn);
            }
        }
        return isBreak;

    }

    private static boolean canBlockDamageSource(EntityLivingBase livingIn, DamageSource dsIn)
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

    public static boolean damageShield(EntityLivingBase livingIn, float damageIn)
    {
        boolean isBreak = false;

        ItemStack shield = livingIn.getActiveItemStack();

        int offset = 1;
        if (!Shields.isInstShield) { offset = 2; }

        if (damageIn >= 3.0F && shield != null && shield.getItem() instanceof ItemShields)
        {
            int damage = 1 + MathHelper.floor_float(damageIn);

            shield.damageItem(damage / offset, livingIn);

            if (shield.stackSize <= 0)
            {
                EnumHand enumhand = livingIn.getActiveHand();

                if (livingIn instanceof EntityPlayer)
                {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((EntityPlayer)livingIn, livingIn.getActiveItemStack(), enumhand);
                }

                if (enumhand == EnumHand.MAIN_HAND)
                {
                    livingIn.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, (ItemStack)null);
                }
                else
                {
                    livingIn.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, (ItemStack)null);
                }

                livingIn.resetActiveHand();
                livingIn.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + livingIn.worldObj.rand.nextFloat() * 0.4F);
                isBreak = true;
            }
        }

        return isBreak;
    }

}
