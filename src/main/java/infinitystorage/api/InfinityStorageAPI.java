package infinitystorage.api;

import infinitystorage.api.autocrafting.registry.ICraftingTaskRegistry;
import infinitystorage.api.solderer.ISoldererRegistry;

public final class InfinityStorageAPI {
    /**
     * The solderer registry, set in pre-initialization
     */
    public static ISoldererRegistry SOLDERER_REGISTRY;

    /**
     * The crafting task registry, set in pre-initialization
     */
    public static ICraftingTaskRegistry CRAFTING_TASK_REGISTRY;
}
