package infinitystorage.api;

import infinitystorage.api.autocrafting.registry.ICraftingTaskRegistry;
import infinitystorage.api.solderer.ISoldererRegistry;
import javax.annotation.Nonnull;

/**
 * Represents a Refined Storage API implementation.
 */
public interface IAPI {
    /**
     * @return The solderer registry
     */
    @Nonnull
    ISoldererRegistry getSoldererRegistry();

    /**
     * @return The crafting task registry
     */
    @Nonnull
    ICraftingTaskRegistry getCraftingTaskRegistry();
}