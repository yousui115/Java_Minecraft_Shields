package yousui115.shields.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.shields.Shields;
import yousui115.shields.network.MsgKey;
import yousui115.shields.network.PacketHandler;
import yousui115.shields.util.SItems;
import yousui115.shields.util.SUtils;

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
//    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        int states = ItemShields.getShieldStates(stack);
        if (states == 0) { return; }

        String tip = "";

        for (int next = 1; next != ~Integer.MAX_VALUE; next <<= 1)
        {
            if ((states & next) != next) { continue; }

            EnumShieldState state = EnumShieldState.getStateFromBit(next);

            tip += state.getTooltip();
        }

        tooltip.add(tip);
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
//    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (!this.isInCreativeTab(tab)) { return; }

        for (EnumShieldState state : EnumShieldState.class.getEnumConstants())
        {
            ItemStack stack = new ItemStack(SItems.SHIELD);
            setShieldState(stack, state);
            addShieldStates(stack, state);
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

    public static EnumShieldState getShieldState(ItemStack stackIn)
    {
        EnumShieldState state = null;

        if (!SUtils.isEmptyStack(stackIn) &&
            stackIn.getItem() instanceof ItemShields)
        {
            NBTTagCompound nbt = stackIn.getOrCreateSubCompound("ShieldsTag");

            state = EnumShieldState.getStateFromOrdinal(nbt.getInteger("state"));
        }

        return state;
    }

    public static void setShieldState(ItemStack stackIn, EnumShieldState stateIn)
    {
        NBTTagCompound nbt = stackIn.getOrCreateSubCompound("ShieldsTag");
        nbt.setInteger("state", stateIn.ordinal());
    }

    public static int getShieldStates(ItemStack stackIn)
    {
        int states = 0;

        if (!SUtils.isEmptyStack(stackIn)
            && stackIn.getItem() instanceof ItemShields )
        {
            NBTTagCompound nbt = stackIn.getOrCreateSubCompound("ShieldsTag");

            states = nbt.getInteger("states");
        }

        return states;
    }

    public static void setShieldStates(ItemStack stackIn, EnumShieldState stateIn)
    {
        NBTTagCompound nbt = stackIn.getOrCreateSubCompound("ShieldsTag");
        nbt.setInteger("states", stateIn.bit);
    }

    public static void addShieldStates(ItemStack stackIn, EnumShieldState stateIn)
    {
        NBTTagCompound nbt = stackIn.getOrCreateSubCompound("ShieldsTag");
        int states = nbt.getInteger("states");
        nbt.setInteger("states", states | stateIn.bit);
    }

    public static void setShieldEOHand(ItemStack stackIn, boolean equipIn)
    {
        NBTTagCompound nbt = stackIn.getOrCreateSubCompound("ShieldsTag");
        nbt.setBoolean("offhand", equipIn);
    }

    public static boolean getShieldEOHand(ItemStack stackIn)
    {
        boolean equip = false;

        if (!SUtils.isEmptyStack(stackIn)
            && (stackIn.getItem() instanceof ItemShields || stackIn.getItem() == Items.SHIELD))
        {
            NBTTagCompound nbt = stackIn.getOrCreateSubCompound("ShieldsTag");

            equip = nbt.getBoolean("offhand");
        }

        return equip;
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
        WOOD(    0x00,   50,     "wood",    EnumShieldAction.NONE, Item.getItemFromBlock(Blocks.PLANKS)),
        FLAME(   0x01, 1000,    "flame",    EnumShieldAction.FIRE, Items.QUARTZ),
        ICE(     0x02, 1000,      "ice",  EnumShieldAction.FREEZE, Item.getItemFromBlock(Blocks.ICE)),
        DIAMOND( 0x04, 1000,  "diamond", EnumShieldAction.REFLECT, Items.DIAMOND),
        OBSIDIAN(0x08, 1000, "obsidian",  EnumShieldAction.ROBUST, Item.getItemFromBlock(Blocks.OBSIDIAN));

        public final int bit;
        public final int maxDamage;
        public final String nameMaterial;
        public final EnumShieldAction action;

        public final List<Item> items;

        /**
         * ■
         */
        private EnumShieldState(int bitIn, int maxDamageIn, String nameIn, EnumShieldAction actionIn, Item ...itemsIn)
        {
            bit = bitIn;
            maxDamage = maxDamageIn;
            nameMaterial = nameIn;
            action = actionIn;

            items = Lists.newArrayList();
            for (Item item : itemsIn) { items.add(item); }
        }

        /**
         * ■
         */
        public boolean canJG() { return this == WOOD ? false : true; }

        /**
         * ■
         */
        @Nullable
        public static EnumShieldState getStateFromOrdinal(int ordinal)
        {
            EnumShieldState state[] = EnumShieldState.class.getEnumConstants();
            return state.length > ordinal ? state[ordinal] : null;
        }

        /**
         * ■
         */
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

        /**
         * ■
         */
        public Object[] getObjectMaterial()
        {
            Object[] obj = new Object[2];

            switch (this)
            {
            case FLAME:
                obj[0] = Blocks.MAGMA;
                obj[1] = Items.QUARTZ;
                break;

            case ICE:
                obj[0] = Blocks.ICE;
                obj[1] = Blocks.PACKED_ICE;
                break;

            case DIAMOND:
                obj[0] = Items.DIAMOND;
                obj[1] = Items.ENDER_PEARL;
                break;

            case OBSIDIAN:
                obj[0] = Blocks.OBSIDIAN;
                obj[1] = Items.ENDER_PEARL;
                break;

            case WOOD:
            default:
                obj[0] = null;
                obj[1] = null;
                break;
            }

            return obj;
        }

        @Nullable
        public String getTooltip()
        {
            String tooltip;

            switch(this)
            {
            case FLAME:
                tooltip = TextFormatting.RED + " F";
                break;

            case ICE:
                tooltip = TextFormatting.BLUE + " I";
                break;

            case DIAMOND:
                tooltip = TextFormatting.AQUA + " D";
                break;

            case OBSIDIAN:
                tooltip = TextFormatting.DARK_PURPLE + " O";
                break;

            case WOOD:
            default:
                tooltip = null;
                break;
            }
            return tooltip;
        }

        public int getRepairCost()
        {
            int cost = 1;
            switch(this)
            {
                default:
                    cost = 2;
                    break;
            }
            return cost;
        }
    }
}
