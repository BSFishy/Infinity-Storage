package infinitystorage.apiimpl;

import infinitystorage.api.IAPI;
import infinitystorage.api.autocrafting.registry.ICraftingTaskRegistry;
import infinitystorage.api.solderer.ISoldererRegistry;
import infinitystorage.apiimpl.autocrafting.registry.CraftingTaskRegistry;
import infinitystorage.apiimpl.solderer.SoldererRegistry;

import javax.annotation.Nonnull;

public class API implements IAPI {
    public static final IAPI INSTANCE = new API();

    private ISoldererRegistry soldererRegistry = new SoldererRegistry();
    private ICraftingTaskRegistry craftingTaskRegistry = new CraftingTaskRegistry();

    @Override
    @Nonnull
    public ISoldererRegistry getSoldererRegistry() {
        return soldererRegistry;
    }

    @Override
    @Nonnull
    public ICraftingTaskRegistry getCraftingTaskRegistry() {
        return craftingTaskRegistry;
    }
}
