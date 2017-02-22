package yousui115.shields.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shields.util.SItems;
import yousui115.shields.util.SUtils;

public class FirstLoginGiveItemEvent
{
    @SubscribeEvent
    public void giveSoulItem(PlayerEvent.LoadFromFile event)
    {
        boolean isFirst = true;

        NBTTagCompound nbt_entitydata = event.getEntityPlayer().getEntityData();
        NBTTagCompound nbt_persisted = null;

        //■PERSISTED : 永続化 のタグを持ってる
        if (nbt_entitydata.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
        {
            nbt_persisted = nbt_entitydata.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

            //■MOD独自のNBTタグを持ってる
            if (nbt_persisted.hasKey(SUtils.SHIELDS_NBT_TAG))
            {
                isFirst = false;
            }
        }

        //■初回プレイヤー
        if (isFirst)
        {
            if (nbt_persisted == null)
            {
                nbt_persisted = new NBTTagCompound();
                nbt_entitydata.setTag(EntityPlayer.PERSISTED_NBT_TAG, nbt_persisted);
            }

            NBTTagCompound nbt_soul = new NBTTagCompound();
            nbt_soul.setInteger(SUtils.NBT_REPAIR_COUNT, 0);

            nbt_persisted.setTag(SUtils.SHIELDS_NBT_TAG, nbt_soul);

            //■アイテム贈与
            InventoryPlayer inv = event.getEntityPlayer().inventory;
//            inv.mainInventory[0] = new ItemStack(SItems.SWORD);
            inv.offHandInventory[0] = new ItemStack(SItems.SHIELD);
        }
    }
}
