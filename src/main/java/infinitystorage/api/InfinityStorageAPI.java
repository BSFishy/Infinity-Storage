package infinitystorage.api;

import infinitystorage.api.autocrafting.registry.ICraftingTaskRegistry;
import infinitystorage.api.solderer.ISoldererRegistry;

import java.lang.reflect.Field;

public final class InfinityStorageAPI {
    private static final String API_IMPL_CLASS = "infinitystorage.apiimpl.API";
    private static final String API_IMPL_FIELD = "INSTANCE";

    private static final IAPI API;

    static {
        try {
            Class<?> apiClass = Class.forName(API_IMPL_CLASS);
            Field apiField = apiClass.getField(API_IMPL_FIELD);

            API = (IAPI) apiField.get(apiClass);
        } catch (Exception e) {
            throw new Error("The Infinity Storage IAPI implementation is unavailable, make sure Infinity Storage is installed");
        }
    }

    public static IAPI instance() {
        return API;
    }
}
