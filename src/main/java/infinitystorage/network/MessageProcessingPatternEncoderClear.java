package infinitystorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import infinitystorage.tile.TileProcessingPatternEncoder;

public class MessageProcessingPatternEncoderClear extends MessageHandlerPlayerToServer<MessageProcessingPatternEncoderClear> implements IMessage {
    private int x;
    private int y;
    private int z;

    public MessageProcessingPatternEncoderClear() {
    }

    public MessageProcessingPatternEncoderClear(TileProcessingPatternEncoder processingPatternEncoder) {
        this.x = processingPatternEncoder.getPos().getX();
        this.y = processingPatternEncoder.getPos().getY();
        this.z = processingPatternEncoder.getPos().getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void handle(MessageProcessingPatternEncoderClear message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileProcessingPatternEncoder) {
            TileProcessingPatternEncoder processingPatternEncoder = (TileProcessingPatternEncoder) tile;

            for (int i = 0; i < processingPatternEncoder.getConfiguration().getSlots(); ++i) {
                processingPatternEncoder.getConfiguration().setStackInSlot(i, null);
            }
        }
    }
}

