package yousui115.shields.client;

import net.minecraft.tileentity.TileEntity;
import yousui115.shields.item.ItemShields;

//TODO どうしてこうなった♪どうしてこうなった♪

public class TileEntityShield extends TileEntity
{
    public static Class getClass(ItemShields.EnumShieldState state)
    {
        switch(state)
        {
            case WOOD:
                return Wood.class;

            case FLAME:
                return Flame.class;

            case ICE:
                return Ice.class;

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

    public class Flame extends TileEntityShield {}

    public class Ice extends TileEntityShield {}

    public class Diamond extends TileEntityShield {}

    public class Obsidian extends TileEntityShield {}

}
