package infinitystorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import infinitystorage.block.EnumGridType;
import infinitystorage.tile.TileProcessingPatternEncoder;
import infinitystorage.tile.grid.TileGrid;

public class MessageGridPatternCreate extends MessageHandlerPlayerToServer<MessageGridPatternCreate> implements IMessage {
    private int x;
    private int y;
    private int z;

    public MessageGridPatternCreate() {
    }

    public MessageGridPatternCreate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
    public void handle(MessageGridPatternCreate message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileGrid && ((TileGrid) tile).getType() == EnumGridType.PATTERN) {
            ((TileGrid) tile).onCreatePattern();
        } else if (tile instanceof TileProcessingPatternEncoder) {
            ((TileProcessingPatternEncoder) tile).onCreatePattern();
        }
    }
}
