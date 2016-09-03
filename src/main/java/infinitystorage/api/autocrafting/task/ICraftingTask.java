package infinitystorage.api.autocrafting.task;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import infinitystorage.api.autocrafting.ICraftingPattern;
import infinitystorage.api.network.INetworkMaster;

import javax.annotation.Nullable;

/**
 * Represents a crafting task.
 */
public interface ICraftingTask {
    /**
     * @return The pattern
     */
    ICraftingPattern getPattern();

    /**
     * @return The child task
     */
    @Nullable
    ICraftingTask getChild();

    /**
     * @param child The child task
     */
    void setChild(@Nullable ICraftingTask child);

    /**
     * @param world   The world
     * @param network The network
     * @return If the crafting task is done
     */
    boolean update(World world, INetworkMaster network);

    /**
     * Gets called when this crafting task is cancelled.
     *
     * @param network The network
     */
    void onCancelled(INetworkMaster network);

    /**
     * Writes this crafting task to NBT.
     *
     * @param tag The NBT tag to write to
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * Returns status info used in the tooltip of the crafting monitor.
     *
     * @return The status
     */
    String getStatus();

    /**
     * @return The progress for display in the crafting monitor, -1 for no progress
     */
    int getProgress();
}
