package yousui115.shields.client;

import net.minecraft.tileentity.TileEntity;
import yousui115.shields.item.ItemSShield;

//TODO どうしてこうなった♪どうしてこうなった♪

public class TileEntityShield extends TileEntity
{
    public static Class getClass(ItemSShield.EnumShieldState state)
    {
        switch(state)
        {
            case WOOD:
                return Wood.class;

            case DIAMOND:
                return Diamond.class;

            case OBSIDIAN:
                return Obsidian.class;

            default:
                break;
        }

        return null;
    }


    public class Wood extends TileEntityShield {}

    public class Diamond extends TileEntityShield {}

    public class Obsidian extends TileEntityShield {}

}
