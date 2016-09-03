package infinitystorage.api.storage.item;

import java.util.List;

/**
 * Represents a tile that provides item storage to the network. Implement this on a tile that is a {@link infinitystorage.api.network.INetworkNode}.
 */
public interface IItemStorageProvider {
    /**
     * Adds the item storages that this storage provider provides.
     *
     * @param storages The previously added item storages
     */
    void addItemStorages(List<IItemStorage> storages);
}
