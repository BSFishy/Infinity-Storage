package infinitystorage.api;

import infinitystorage.api.autocrafting.registry.ICraftingTaskRegistry;
import infinitystorage.api.solderer.ISoldererRegistry;

public interface IAPI {

    ISoldererRegistry getSoldererRegistry();

    ICraftingTaskRegistry getCraftingTaskRegistry();

}
