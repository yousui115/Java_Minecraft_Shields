package yousui115.shields.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;

public class ItemSShield extends ItemShield
{
    @Override
    public String getItemStackDisplayName(ItemStack stackIn)
    {
        return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stackIn) + ".name")).trim();
    }

    @Override
    public String getUnlocalizedName(ItemStack stackIn)
    {
        EnumShieldState state = getShieldState(stackIn);
        return state != null ? "item.shields." + state.nameMaterial : "";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        EnumShieldState state = getShieldState(stack);
        return state != null ? state.maxDamage : 336;
    }

    /**
     * ■returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (EnumShieldState state : EnumShieldState.class.getEnumConstants())
        {
            ItemStack stack = new ItemStack(itemIn);
            setShieldState(stack, state);
            subItems.add(stack);
        }
    }

    /**
     * ■金床での修理に必要な素材の指定
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        EnumShieldState state = getShieldState(toRepair);
        if (state != null)
        {
            return state.items.contains(repair.getItem());
        }

        return false;
    }

    @Override
    public int getMetadata(ItemStack stack)
    {
        EnumShieldState state = getShieldState(stack);
        return state != null ? state.ordinal() : 0;
    }

    /* ====================================================================== */

    @Nullable
    public static EnumShieldState getShieldState(ItemStack stackIn)
    {
        EnumShieldState state = null;

        if (stackIn != null
            && stackIn.getItem() instanceof ItemSShield
//            && stackIn.hasTagCompound()
//            && stackIn.getTagCompound().hasKey("shields.state")
           )
        {
            NBTTagCompound nbt = stackIn.getSubCompound("ShieldsTag", true);

            state = EnumShieldState.getStateFromOrdinal(nbt.getInteger("state"));
        }

        return state;
    }

    public static void setShieldState(@Nonnull ItemStack stackIn, @Nonnull EnumShieldState state)
    {
//        stackIn.setTagInfo("shields.state", new NBTTagInt(state.ordinal()));
        NBTTagCompound nbt = stackIn.getSubCompound("ShieldsTag", true);
        nbt.setInteger("state", state.ordinal());
    }

    /**
     * ■
     */
    public enum EnumShieldAction
    {
        NONE,
        FIRE,
        ICE,
        REFLECT,
        ROBUST
    }

    /**
     * ■
     */
    public enum EnumShieldState
    {
        WOOD(      50,     "wood", false,    EnumShieldAction.NONE, Item.getItemFromBlock(Blocks.LOG), Item.getItemFromBlock(Blocks.LOG2)),
//        VANILLA(  336,  "vanilla",  true, Item.getItemFromBlock(Blocks.PLANKS)),
        DIAMOND( 1000,  "diamond",  true, EnumShieldAction.REFLECT, Items.DIAMOND),
        OBSIDIAN(1000, "obsidian",  true,  EnumShieldAction.ROBUST, Item.getItemFromBlock(Blocks.OBSIDIAN));

        public final int maxDamage;
        public final String nameMaterial;
        public final boolean canJG;
        public final EnumShieldAction action;

        public final List<Item> items;

        /**
         * ■
         */
        private EnumShieldState(int maxDamageIn, String nameIn, boolean canJGIn, EnumShieldAction actionIn, Item ...itemsIn)
        {
            maxDamage = maxDamageIn;
            nameMaterial = nameIn;
            canJG = canJGIn;
            action = actionIn;

            items = Lists.newArrayList();
            for (Item item : itemsIn) { items.add(item); }
        }

        /**
         * ■
         */
        @Nullable
        public static EnumShieldState getStateFromOrdinal(int ordinal)
        {
            EnumShieldState state[] = EnumShieldState.class.getEnumConstants();
            return state.length > ordinal ? state[ordinal] : null;
        }
    }
}
