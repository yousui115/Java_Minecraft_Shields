package yousui115.shields.event;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldState;
import yousui115.shields.util.SEnchants;
import yousui115.shields.util.SUtils;

public class ShieldsHndls
{
    /**
     * ■Anvil GUI iput -> output
     */
    @SubscribeEvent
    public void updateAnvilItemSlot(AnvilUpdateEvent event)
    {
        ItemStack left = event.getLeft();
        ItemStack output = event.getOutput();

        boolean isEnchSM = EnchantmentHelper.getEnchantmentLevel(SEnchants.ENCH_MAIDEN, left) == 1 ? true : false;

        //■「特定の盾」かつ「金床修理OK」かつ「盾の乙女、付与済み」
        if (SUtils.isShieldsOrVShield(left) &&
            event.getLeft().getItem().getIsRepairable(event.getLeft(), event.getRight()) &&
            isEnchSM == true)
        {
            //■コストを補正
            int cost = getRepairCost(left);
            left.setRepairCost(cost < event.getCost() ? cost : event.getCost());
        }

        //■「追加盾」同士の合成はさせない。
        ItemStack right = event.getRight();
        if (left.getItem() == right.getItem() && left.getItem() instanceof ItemShields)
        {
            //■以降の処理をさせない。
            event.setCanceled(true);
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

        //■「特定の盾」かつ「盾の乙女、未付与」かつ「リネームを含む」
        if (SUtils.isShieldsOrVShield(result) &&
            !enchMap.containsKey(SEnchants.ENCH_MAIDEN) &&
            !input.getDisplayName().equals(result.getDisplayName()))
        {
            //■「盾の乙女」付与
            enchMap.put(SEnchants.ENCH_MAIDEN, 1);
            EnchantmentHelper.setEnchantments(enchMap, result);

            //■コストリセット
            result.setRepairCost(0);
        }
    }

    private static int getRepairCost(ItemStack stackIn)
    {
        int cost = 2;

        EnumShieldState state = ItemShields.getShieldState(stackIn);

        if (state != null)
        {
            cost = state.getRepairCost();
        }

        return cost;
    }

    /**
     * ■プレイヤーに死が訪れた瞬間
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void a(LivingDeathEvent event)
    {
        //■プレイヤーがやられた
        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();

            //■オフハンドをまさぐる
            for (ItemStack stack : player.inventory.offHandInventory)
            {
                //■追加盾 or バニラ盾 を持ってる
                if (!SUtils.isEmptyStack(stack) && SUtils.isShieldsOrVShield(stack))
                {
                    //■エンチャント「盾の乙女」がついてる
                    if (EnchantmentHelper.getEnchantmentLevel(SEnchants.ENCH_MAIDEN, stack) == 1)
                    {
                        //■「盾の乙女」の付いた「盾」を「オフハンド」に持ってたよ。と刻印する。
                        ItemShields.setShieldeOHand(stack, true);
                        break;
                    }
                }
            }
        }

    }

    /**
     * ■プレイヤーがパーンした時の、アイテムの在り方
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void b(PlayerDropsEvent event)
    {
        Iterator<EntityItem> itr = event.getDrops().iterator();

        while (itr.hasNext())
        {
            ItemStack stack = itr.next().getItem();

            //■刻印されている盾であるか否か
            if (ItemShields.getShieldeOHand(stack) == true)
            {
                //■パーンしません。
                itr.remove();

                //■オフハンドに戻します。
                event.getEntityPlayer().inventory.offHandInventory.set(0, stack);

                //■刻印は消されます。
                ItemShields.setShieldeOHand(stack, false);
            }
        }
    }

    @SubscribeEvent
    public void c(PlayerEvent.Clone event)
    {
        ItemStack stack = event.getOriginal().inventory.offHandInventory.get(0);

        if (!SUtils.isEmptyStack(stack) && SUtils.isShieldsOrVShield(stack))
        {
            event.getEntityPlayer().inventory.offHandInventory.set(0, stack);
        }
    }
}
