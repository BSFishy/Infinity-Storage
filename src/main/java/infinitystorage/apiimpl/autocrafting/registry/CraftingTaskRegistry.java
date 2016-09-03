package infinitystorage.apiimpl.autocrafting.registry;

import infinitystorage.api.autocrafting.registry.ICraftingTaskFactory;
import infinitystorage.api.autocrafting.registry.ICraftingTaskRegistry;

import java.util.HashMap;
import java.util.Map;

public class CraftingTaskRegistry implements ICraftingTaskRegistry {
    private Map<String, ICraftingTaskFactory> registry = new HashMap<String, ICraftingTaskFactory>();

    @Override
    public void addFactory(String id, ICraftingTaskFactory factory) {
        registry.put(id, factory);
    }

    @Override
    public ICraftingTaskFactory getFactory(String id) {
        return registry.get(id);
    }
}
