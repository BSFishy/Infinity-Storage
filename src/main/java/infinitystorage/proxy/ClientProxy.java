package infinitystorage.proxy;

import infinitystorage.tile.TileController;
import mcmultipart.client.multipart.ModelMultipartContainer;
import mcmultipart.raytrace.PartMOP;
import mcmultipart.raytrace.RayTraceUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.InfinityStorageItems;
import infinitystorage.block.*;
import infinitystorage.item.*;

import java.util.List;

public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        for (ModelResourceLocation model : e.getModelRegistry().getKeys()) {
            cableTypes.stream().filter(cable -> model.getResourceDomain().equals(InfinityStorage.ID) && model.getResourcePath().equals(cable.getName()) && !model.getVariant().equals("inventory")).forEach(cable -> {
                e.getModelRegistry().putObject(model, ModelMultipartContainer.fromBlock(e.getModelRegistry().getObject(model), cable));
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockDrawHighlight(DrawBlockHighlightEvent e) {
        if (e.getTarget() == null || e.getTarget().getBlockPos() == null) {
            return;
        }

        EntityPlayer player = e.getPlayer();

        BlockPos pos = e.getTarget().getBlockPos();

        IBlockState state = player.worldObj.getBlockState(pos);

        if (!(state.getBlock() instanceof BlockCable)) {
            return;
        }

        state = ((BlockCable) state.getBlock()).getActualState(state, player.worldObj, pos);

        if (((BlockCable) state.getBlock()).collisionRayTrace(state, player.worldObj, pos, RayTraceUtils.getStart(player), RayTraceUtils.getEnd(player)) instanceof PartMOP) {
            return;
        }

        List<AxisAlignedBB> unionized = ((BlockCable) state.getBlock()).getUnionizedCollisionBoxes(state);
        List<AxisAlignedBB> nonUnionized = ((BlockCable) state.getBlock()).getNonUnionizedCollisionBoxes(state);

        e.setCanceled(true);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) e.getPartialTicks();
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) e.getPartialTicks();
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) e.getPartialTicks();

        AxisAlignedBB unionizedAabb = unionized.get(0);

        for (int i = 1; i < unionized.size(); ++i) {
            unionizedAabb = unionizedAabb.union(unionized.get(i));
        }

        drawSelectionBoundingBox(unionizedAabb.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2).offset(pos.getX(), pos.getY(), pos.getZ()));

        for (AxisAlignedBB aabb : nonUnionized) {
            drawSelectionBoundingBox(aabb.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2).offset(pos.getX(), pos.getY(), pos.getZ()));
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private void drawSelectionBoundingBox(AxisAlignedBB aabb) {
        Tessellator tessellator = Tessellator.getInstance();

        VertexBuffer buffer = tessellator.getBuffer();

        buffer.begin(3, DefaultVertexFormats.POSITION);
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();

        tessellator.draw();

        buffer.begin(3, DefaultVertexFormats.POSITION);
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();

        tessellator.draw();

        buffer.begin(1, DefaultVertexFormats.POSITION);
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();

        tessellator.draw();
    }

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(this);

        // Item Variants
        ModelBakery.registerItemVariants(InfinityStorageItems.STORAGE_DISK,
            new ResourceLocation("infinitystorage:1k_storage_disk"),
            new ResourceLocation("infinitystorage:4k_storage_disk"),
            new ResourceLocation("infinitystorage:16k_storage_disk"),
            new ResourceLocation("infinitystorage:64k_storage_disk"),
            new ResourceLocation("infinitystorage:creative_storage_disk")
        );

        ModelBakery.registerItemVariants(InfinityStorageItems.STORAGE_PART,
            new ResourceLocation("infinitystorage:1k_storage_part"),
            new ResourceLocation("infinitystorage:4k_storage_part"),
            new ResourceLocation("infinitystorage:16k_storage_part"),
            new ResourceLocation("infinitystorage:64k_storage_part")
        );

        ModelBakery.registerItemVariants(InfinityStorageItems.FLUID_STORAGE_DISK,
            new ResourceLocation("infinitystorage:64k_fluid_storage_disk"),
            new ResourceLocation("infinitystorage:128k_fluid_storage_disk"),
            new ResourceLocation("infinitystorage:256k_fluid_storage_disk"),
            new ResourceLocation("infinitystorage:512k_fluid_storage_disk"),
            new ResourceLocation("infinitystorage:creative_fluid_storage_disk")
        );

        ModelBakery.registerItemVariants(InfinityStorageItems.FLUID_STORAGE_PART,
            new ResourceLocation("infinitystorage:64k_fluid_storage_part"),
            new ResourceLocation("infinitystorage:128k_fluid_storage_part"),
            new ResourceLocation("infinitystorage:256k_fluid_storage_part"),
            new ResourceLocation("infinitystorage:512k_fluid_storage_part")
        );

        ModelBakery.registerItemVariants(InfinityStorageItems.PROCESSOR,
            new ResourceLocation("infinitystorage:basic_printed_processor"),
            new ResourceLocation("infinitystorage:improved_printed_processor"),
            new ResourceLocation("infinitystorage:advanced_printed_processor"),
            new ResourceLocation("infinitystorage:basic_processor"),
            new ResourceLocation("infinitystorage:improved_processor"),
            new ResourceLocation("infinitystorage:advanced_processor"),
            new ResourceLocation("infinitystorage:printed_silicon")
        );

        ModelBakery.registerItemVariants(InfinityStorageItems.CORE,
            new ResourceLocation("infinitystorage:construction_core"),
            new ResourceLocation("infinitystorage:destruction_core")
        );

        ModelBakery.registerItemVariants(InfinityStorageItems.UPGRADE,
            new ResourceLocation("infinitystorage:upgrade"),
            new ResourceLocation("infinitystorage:range_upgrade"),
            new ResourceLocation("infinitystorage:speed_upgrade"),
            new ResourceLocation("infinitystorage:stack_upgrade"),
            new ResourceLocation("infinitystorage:interdimensional_upgrade")
        );

        // Items
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_1K, new ModelResourceLocation("infinitystorage:1k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_4K, new ModelResourceLocation("infinitystorage:4k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_16K, new ModelResourceLocation("infinitystorage:16k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_64K, new ModelResourceLocation("infinitystorage:64k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_CREATIVE, new ModelResourceLocation("infinitystorage:creative_storage_disk", "inventory"));

        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_PART, ItemStoragePart.TYPE_1K, new ModelResourceLocation("infinitystorage:1k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_PART, ItemStoragePart.TYPE_4K, new ModelResourceLocation("infinitystorage:4k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_PART, ItemStoragePart.TYPE_16K, new ModelResourceLocation("infinitystorage:16k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_PART, ItemStoragePart.TYPE_64K, new ModelResourceLocation("infinitystorage:64k_storage_part", "inventory"));

        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_64K, new ModelResourceLocation("infinitystorage:64k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_128K, new ModelResourceLocation("infinitystorage:128k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_256K, new ModelResourceLocation("infinitystorage:256k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_512K, new ModelResourceLocation("infinitystorage:512k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_CREATIVE, new ModelResourceLocation("infinitystorage:creative_fluid_storage_disk", "inventory"));

        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_64K, new ModelResourceLocation("infinitystorage:64k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_128K, new ModelResourceLocation("infinitystorage:128k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_256K, new ModelResourceLocation("infinitystorage:256k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_512K, new ModelResourceLocation("infinitystorage:512k_fluid_storage_part", "inventory"));

        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_BASIC, new ModelResourceLocation("infinitystorage:basic_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_IMPROVED, new ModelResourceLocation("infinitystorage:improved_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_ADVANCED, new ModelResourceLocation("infinitystorage:advanced_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.PROCESSOR, ItemProcessor.TYPE_BASIC, new ModelResourceLocation("infinitystorage:basic_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.PROCESSOR, ItemProcessor.TYPE_IMPROVED, new ModelResourceLocation("infinitystorage:improved_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.PROCESSOR, ItemProcessor.TYPE_ADVANCED, new ModelResourceLocation("infinitystorage:advanced_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_SILICON, new ModelResourceLocation("infinitystorage:printed_silicon", "inventory"));

        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.SILICON, 0, new ModelResourceLocation("infinitystorage:silicon", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.QUARTZ_ENRICHED_IRON, 0, new ModelResourceLocation("infinitystorage:quartz_enriched_iron", "inventory"));

        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.CORE, ItemCore.TYPE_CONSTRUCTION, new ModelResourceLocation("infinitystorage:construction_core", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.CORE, ItemCore.TYPE_DESTRUCTION, new ModelResourceLocation("infinitystorage:destruction_core", "inventory"));

        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.WIRELESS_GRID, 0, new ModelResourceLocation("infinitystorage:wireless_grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.PATTERN, 0, new ModelResourceLocation("infinitystorage:pattern", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.STORAGE_HOUSING, 0, new ModelResourceLocation("infinitystorage:storage_housing", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.GRID_FILTER, 0, new ModelResourceLocation("infinitystorage:grid_filter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.NETWORK_CARD, 0, new ModelResourceLocation("infinitystorage:network_card", "inventory"));
        if(InfinityStorage.channelsEnabled)
            ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.NETWORK_TOOL, 0, new ModelResourceLocation("infinitystorage:network_tool", "inventory"));

        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.UPGRADE, 0, new ModelResourceLocation("infinitystorage:upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.UPGRADE, ItemUpgrade.TYPE_RANGE, new ModelResourceLocation("infinitystorage:range_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED, new ModelResourceLocation("infinitystorage:speed_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.UPGRADE, ItemUpgrade.TYPE_CRAFTING, new ModelResourceLocation("infinitystorage:crafting_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.UPGRADE, ItemUpgrade.TYPE_STACK, new ModelResourceLocation("infinitystorage:stack_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(InfinityStorageItems.UPGRADE, ItemUpgrade.TYPE_INTERDIMENSIONAL, new ModelResourceLocation("infinitystorage:interdimensional_upgrade", "inventory"));

        // Blocks
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.CABLE), 0, new ModelResourceLocation("infinitystorage:cable", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.GRID), EnumGridType.NORMAL.getId(), new ModelResourceLocation("infinitystorage:grid", "connected=false,direction=north,type=normal"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.GRID), EnumGridType.CRAFTING.getId(), new ModelResourceLocation("infinitystorage:grid", "connected=false,direction=north,type=crafting"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.GRID), EnumGridType.PATTERN.getId(), new ModelResourceLocation("infinitystorage:grid", "connected=false,direction=north,type=crafting"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.GRID), EnumGridType.FLUID.getId(), new ModelResourceLocation("infinitystorage:grid", "connected=false,direction=north,type=crafting"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.MACHINE_CASING), 0, new ModelResourceLocation("infinitystorage:machine_casing", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.EXPORTER), 0, new ModelResourceLocation("infinitystorage:exporter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.IMPORTER), 0, new ModelResourceLocation("infinitystorage:importer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.EXTERNAL_STORAGE), 0, new ModelResourceLocation("infinitystorage:external_storage", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.DISK_DRIVE), 0, new ModelResourceLocation("infinitystorage:disk_drive", "inventory"));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.CONSTRUCTOR), 0, new ModelResourceLocation("infinitystorage:constructor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.DESTRUCTOR), 0, new ModelResourceLocation("infinitystorage:destructor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.SOLDERER), 0, new ModelResourceLocation("infinitystorage:solderer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.DETECTOR), 0, new ModelResourceLocation("infinitystorage:detector", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.RELAY), 0, new ModelResourceLocation("infinitystorage:relay", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.INTERFACE), 0, new ModelResourceLocation("infinitystorage:interface", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.FLUID_INTERFACE), 0, new ModelResourceLocation("infinitystorage:fluid_interface", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.WIRELESS_TRANSMITTER), 0, new ModelResourceLocation("infinitystorage:wireless_transmitter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.CRAFTING_MONITOR), 0, new ModelResourceLocation("infinitystorage:crafting_monitor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.CRAFTER), 0, new ModelResourceLocation("infinitystorage:crafter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.PROCESSING_PATTERN_ENCODER), 0, new ModelResourceLocation("infinitystorage:processing_pattern_encoder", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.NETWORK_TRANSMITTER), 0, new ModelResourceLocation("infinitystorage:network_transmitter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.NETWORK_RECEIVER), 0, new ModelResourceLocation("infinitystorage:network_receiver", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.STORAGE), EnumItemStorageType.TYPE_1K.getId(), new ModelResourceLocation("infinitystorage:storage", "type=1k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.STORAGE), EnumItemStorageType.TYPE_4K.getId(), new ModelResourceLocation("infinitystorage:storage", "type=4k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.STORAGE), EnumItemStorageType.TYPE_16K.getId(), new ModelResourceLocation("infinitystorage:storage", "type=16k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.STORAGE), EnumItemStorageType.TYPE_64K.getId(), new ModelResourceLocation("infinitystorage:storage", "type=64k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.STORAGE), EnumItemStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation("infinitystorage:storage", "type=creative"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_64K.getId(), new ModelResourceLocation("infinitystorage:fluid_storage", "type=64k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_128K.getId(), new ModelResourceLocation("infinitystorage:fluid_storage", "type=128k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_256K.getId(), new ModelResourceLocation("infinitystorage:fluid_storage", "type=256k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_512K.getId(), new ModelResourceLocation("infinitystorage:fluid_storage", "type=512k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation("infinitystorage:fluid_storage", "type=creative"));
        if(ENABLE_DISK_MANIPULATOR) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(InfinityStorageBlocks.DISK_MANIPULATOR), 0, new ModelResourceLocation("infinitystorage:disk_manipulator", "inventory"));
        }

        ModelLoader.setCustomStateMapper(InfinityStorageBlocks.CONTROLLER, new StateMap.Builder().ignore(BlockController.TYPE).build());

        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(InfinityStorageBlocks.CONTROLLER), stack -> {
            int energy = stack.getItemDamage() == EnumControllerType.CREATIVE.getId() ? 7 : TileController.getEnergyScaled(ItemBlockController.getEnergyStored(stack), ItemBlockController.getEnergyCapacity(stack), 7);

            return new ModelResourceLocation("infinitystorage:controller", "direction=north,energy=" + energy);
        });
    }
}
