package yousui115.shields.event;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shields.Shields;
import yousui115.shields.item.ItemShields;
import yousui115.shields.util.SEnchants;
import yousui115.shields.util.SUtils;

import com.google.common.collect.Maps;

public class ShieldsHndls
{
    /**
     * ■盾にダメージを与えるだけの処理
     * @param event
     */
    @SubscribeEvent
    public void doDamageShield(LivingAttackEvent event)
    {
        //■盾強化が入っているなら、そちらで処理
        if (Shields.isInstShield) { return; }

        EntityLivingBase blocker = event.getEntityLiving();
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        //■サーバサイドのみ
        if (blocker.worldObj.isRemote) { return; }

        if (SUtils.checkDamageShield(blocker, source, amount))
        {
            event.setCanceled(true);
        }
    }



    /**
     * ■Anvil GUI iput -> output
     */
//    @SubscribeEvent
//    public void updateAnvilItemSlot(AnvilUpdateEvent event)
//    {
//
//    }

    /**
     * ■Anvil GUI output -> pickup from slot
     */
    @SubscribeEvent
    public void pickupAnvilItemFromSlot(AnvilRepairEvent event)
    {
        //■名前の変更で特殊エンチャント
        ItemStack result = event.getItemResult();
        ItemStack input   = event.getItemInput();

        if ((result.getItem() == Items.SHIELD || result.getItem() instanceof ItemShields) &&
            !input.getDisplayName().equals(result.getDisplayName()))
        {
            //■名前が違う -> リネームした
            Map<Enchantment, Integer> enchMap = Maps.newHashMapWithExpectedSize(1);
            enchMap.put(SEnchants.ENCH_MAIDEN, 1);
            EnchantmentHelper.setEnchantments(enchMap, result);
        }
    }

    @SubscribeEvent
    public void a(LivingDeathEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();

            for (ItemStack stack : player.inventory.offHandInventory)
            {
                if (stack != null && (stack.getItem() instanceof ItemShields || stack.getItem() == Items.SHIELD))
                {
                    if (EnchantmentHelper.getEnchantmentLevel(SEnchants.ENCH_MAIDEN, stack) == 1)
                    {
                        ItemShields.setShieldEOHand(stack, true);
                        break;
                    }
                }
            }
        }

    }
    @SubscribeEvent
    public void b(PlayerDropsEvent event)
    {
        Iterator<EntityItem> itr = event.getDrops().iterator();

        while (itr.hasNext())
        {
            ItemStack stack = itr.next().getEntityItem();

            if (ItemShields.getShieldEOHand(stack) == true)
            {
                itr.remove();

                event.getEntityPlayer().inventory.offHandInventory[0] = stack;

                ItemShields.setShieldEOHand(stack, false);
            }
        }
    }

    @SubscribeEvent
    public void c(PlayerEvent.Clone event)
    {
        ItemStack stack = event.getOriginal().inventory.offHandInventory[0];

        if (stack != null &&
            ((stack.getItem() == Items.SHIELD) || stack.getItem() instanceof ItemShields))
        {
            event.getEntityPlayer().inventory.offHandInventory[0] = stack;
        }
    }
}
