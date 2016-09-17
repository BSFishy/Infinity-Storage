package infinitystorage.api.network;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Represents the data handler for channels
 */
public interface IChannelData {

    /**
     * Adds or removes however many channels from the network
     * @param channels The amount of channels to add or remove
     * @param aod Add or remove channels; add=true, remove=false
     */
    void changeChannelNumber(int channels, boolean aod);

    /**
     * Gets the amount of channels that have already been used
     * @return The amount of channels that have already been used
     */
    int channelsUsed();

    /**
     * Reloads all of the data concerning the cables connected to the controller
     */
    void reloadCables(EntityPlayer player);
}
