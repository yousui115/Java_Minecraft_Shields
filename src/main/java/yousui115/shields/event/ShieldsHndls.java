package yousui115.shields.event;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shields.Shields;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldState;
import yousui115.shields.util.SEnchants;
import yousui115.shields.util.SItems;
import yousui115.shields.util.SUtils;

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
        if (blocker.getEntityWorld().isRemote) { return; }

        if (SUtils.checkDamageShield(blocker, source, amount))
        {
            event.setCanceled(true);
        }

        //■斧には弱い
        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)event.getEntityLiving();

            EntityLivingBase attacker = null;
            if (!(source.getImmediateSource() instanceof EntityLivingBase)) { return; }
            attacker = (EntityLivingBase)source.getImmediateSource();

            ItemStack itemstack = attacker.getHeldItemMainhand();
            ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

            if (!itemstack.isEmpty() &&
                !itemstack1.isEmpty() &&
                itemstack.getItem() instanceof ItemAxe &&
                itemstack1.getItem() instanceof ItemShields)
            {
                float f1 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(attacker) * 0.05F;

                if (entityplayer.getRNG().nextFloat() < f1)
                {
                    entityplayer.getCooldownTracker().setCooldown(SItems.SHIELD, 100);
                    entityplayer.getEntityWorld().setEntityState(entityplayer, (byte)30);
                }
            }
        }
    }



    /**
     * ■Anvil GUI iput -> output
     */
    @SubscribeEvent
    public void updateAnvilItemSlot(AnvilUpdateEvent event)
    {
        ItemStack left = event.getLeft();
        ItemStack output = event.getOutput();

        if ((left.getItem() == Items.SHIELD || left.getItem() instanceof ItemShields) &&
            event.getLeft().getItem().getIsRepairable(event.getLeft(), event.getRight()) &&
            EnchantmentHelper.getEnchantmentLevel(SEnchants.ENCH_MAIDEN, left) == 1 ? true : false)
        {
            int cost = getRepairCost(left);
            left.setRepairCost(cost);
        }
    }

    /**
     * ■Anvil GUI output -> pickup from slot
     */
    @SubscribeEvent
    public void pickupAnvilItemFromSlot(AnvilRepairEvent event)
    {
        //■名前の変更で特殊エンチャント
        ItemStack result = event.getItemResult();
        ItemStack input   = event.getItemInput();

        Map<Enchantment, Integer> enchMap = EnchantmentHelper.getEnchantments(result);

        if ((result.getItem() == Items.SHIELD || result.getItem() instanceof ItemShields) &&
            !enchMap.containsKey(SEnchants.ENCH_MAIDEN) &&
            !input.getDisplayName().equals(result.getDisplayName()))
        {
            //■盾の乙女が無い かつ リネームした
            enchMap.put(SEnchants.ENCH_MAIDEN, 1);
            EnchantmentHelper.setEnchantments(enchMap, result);

            int cost = getRepairCost(result);
            result.setRepairCost(cost);

        }
    }

    private static int getRepairCost(ItemStack stackIn)
    {
        int cost = 2;

        if (stackIn.getItem() instanceof ItemShields)
        {
            EnumShieldState state = ItemShields.getShieldState(stackIn);
            cost = state.getRepairCost();
        }

//        cost = cost < left.getRepairCost() ? cost : left.getRepairCost();

        return cost;
    }

    @SubscribeEvent
    public void a(LivingDeathEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();

            for (ItemStack stack : player.inventory.offHandInventory)
            {
                if (!SUtils.isEmptyStack(stack) && (stack.getItem() instanceof ItemShields || stack.getItem() == Items.SHIELD))
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
            ItemStack stack = itr.next().getItem();

            if (ItemShields.getShieldEOHand(stack) == true)
            {
                itr.remove();

                event.getEntityPlayer().inventory.offHandInventory.set(0, stack);

                ItemShields.setShieldEOHand(stack, false);
            }
        }
    }

    @SubscribeEvent
    public void c(PlayerEvent.Clone event)
    {
        ItemStack stack = event.getOriginal().inventory.offHandInventory.get(0);

        if (!SUtils.isEmptyStack(stack) &&
            ((stack.getItem() == Items.SHIELD) || stack.getItem() instanceof ItemShields))
        {
            event.getEntityPlayer().inventory.offHandInventory.set(0, stack);
        }
    }
}
