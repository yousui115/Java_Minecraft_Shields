package yousui115.shields.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.shields.Shields;
import yousui115.shields.network.MsgKey;
import yousui115.shields.network.PacketHandler;

import com.google.common.collect.Lists;

public class ItemShields extends ItemShield
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

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (worldIn.isRemote && Shields.proxy.getThePlayer() != null)
        {
            if (stack == Shields.proxy.getThePlayer().getHeldItemOffhand())
            {
                Shields.proxy.stackInputDate();

                if (Shields.proxy.isKeyUp())
                {
                    PacketHandler.INSTANCE.sendToServer(new MsgKey('R'));
                }
            }
        }
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
            setShieldStates(stack, state);
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
            && stackIn.getItem() instanceof ItemShields
//            && stackIn.hasTagCompound()
//            && stackIn.getTagCompound().hasKey("shields.state")
           )
        {
            NBTTagCompound nbt = stackIn.getSubCompound("ShieldsTag", true);

            state = EnumShieldState.getStateFromOrdinal(nbt.getInteger("state"));
        }

        return state;
    }

    public static void setShieldState(@Nonnull ItemStack stackIn, @Nonnull EnumShieldState stateIn)
    {
        NBTTagCompound nbt = stackIn.getSubCompound("ShieldsTag", true);
        nbt.setInteger("state", stateIn.ordinal());
    }


    @Nullable
    public static int getShieldStates(ItemStack stackIn)
    {
        int states = 0;

        if (stackIn != null
            && stackIn.getItem() instanceof ItemShields
//            && stackIn.hasTagCompound()
//            && stackIn.getTagCompound().hasKey("shields.state")
           )
        {
            NBTTagCompound nbt = stackIn.getSubCompound("ShieldsTag", true);

            states = nbt.getInteger("states");
        }

        return states;
    }

    public static void setShieldStates(@Nonnull ItemStack stackIn, @Nonnull EnumShieldState stateIn)
    {
        NBTTagCompound nbt = stackIn.getSubCompound("ShieldsTag", true);
        nbt.setInteger("states", stateIn.ordinal());
    }

    public static void addShieldStates(@Nonnull ItemStack stackIn, @Nonnull EnumShieldState stateIn)
    {
        NBTTagCompound nbt = stackIn.getSubCompound("ShieldsTag", true);
        int states = nbt.getInteger("states");
        nbt.setInteger("states", states | stateIn.bit);
    }

    /**
     * ■
     */
    public enum EnumShieldAction
    {
        NONE,
        FIRE,
        FREEZE,
        REFLECT,
        ROBUST
    }

    /**
     * ■
     */
    public enum EnumShieldState
    {
        WOOD(    0x00,   50,     "wood", false,    EnumShieldAction.NONE, Item.getItemFromBlock(Blocks.PLANKS)),
        FLAME(   0x01, 1000,    "flame",  true,    EnumShieldAction.FIRE, Items.QUARTZ),
        ICE(     0x02, 1000,      "ice",  true,  EnumShieldAction.FREEZE, Item.getItemFromBlock(Blocks.ICE)),
        DIAMOND( 0x04, 1000,  "diamond",  true, EnumShieldAction.REFLECT, Items.DIAMOND),
        OBSIDIAN(0x08, 1000, "obsidian",  true,  EnumShieldAction.ROBUST, Item.getItemFromBlock(Blocks.OBSIDIAN));

        public final int bit;
        public final int maxDamage;
        public final String nameMaterial;
        public final boolean canJG;
        public final EnumShieldAction action;

        public final List<Item> items;

        /**
         * ■
         */
        private EnumShieldState(int bitIn, int maxDamageIn, String nameIn, boolean canJGIn, EnumShieldAction actionIn, Item ...itemsIn)
        {
            bit = bitIn;
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

        @Nullable
        public static EnumShieldState getStateFromBit(int bitIn)
        {
            EnumShieldState state[] = EnumShieldState.class.getEnumConstants();
            for (EnumShieldState s : state)
            {
                if (s.bit == bitIn) { return s; }
            }

            return null;
        }
    }
}
