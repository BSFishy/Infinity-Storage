package infinitystorage.tile.config;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import infinitystorage.tile.data.ITileDataConsumer;
import infinitystorage.tile.data.ITileDataProducer;
import infinitystorage.tile.data.TileDataParameter;

public interface IComparable {
    static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IComparable) tile).getCompare();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                ((IComparable) tile).setCompare(value);
            }
        });
    }

    int getCompare();

    void setCompare(int compare);
}
