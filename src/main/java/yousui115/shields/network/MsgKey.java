package yousui115.shields.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MsgKey implements IMessage
{
    public char key;

    public MsgKey() {}

    public MsgKey(char keyIn)
    {
        key = keyIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.key = buf.readChar();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeChar(key);
    }


}
