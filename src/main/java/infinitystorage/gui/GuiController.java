package infinitystorage.gui;

import infinitystorage.tile.TileNode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import infinitystorage.container.ContainerController;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.tile.ClientNode;
import infinitystorage.tile.TileController;

import java.util.List;

public class GuiController extends GuiBase {
    private static final int VISIBLE_ROWS = 2;

    private TileController controller;

    private int barX = 8;
    private int barY = 20;
    private int barWidth = 16;
    private int barHeight = 59;

    public GuiController(ContainerController container, TileController controller) {
        super(container, 176, 181);

        this.scrollbar = new Scrollbar(157, 20, 12, 59);

        this.controller = controller;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileController.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
        scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/controller.png");

        drawTexture(x, y, 0, 0, width, height);

        int barHeightNew = TileController.getEnergyScaled(TileController.ENERGY_STORED.getValue(), TileController.ENERGY_CAPACITY.getValue(), barHeight);

        drawTexture(x + barX, y + barY + barHeight - barHeightNew, 178, barHeight - barHeightNew, barWidth, barHeightNew);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:controller." + controller.getType().getId()));
        drawString(7, 87, t("container.inventory"));
        controller.channelDataInit();
        drawString(67, 87, t("gui.infinitystorage:controller.channels", controller.channelData.channelsUsed));

        int x = 33;
        int y = 26;

        int slot = scrollbar.getOffset() * 2;
        int currentSlot = slot;

        RenderHelper.enableGUIStandardItemLighting();

        List<ClientNode> nodes = TileController.NODES.getValue();

        ClientNode nodeHovering = null;

        for (int i = 0; i < 4; ++i) {
            if (slot < nodes.size()) {
                ClientNode node = nodes.get(slot);
                boolean draw = false;

                for (TileNode tn : controller.CONNECTION) {
                    if (tn.clientNode.equals(node) || tn.clientNode == node) {
                        draw = true;
                    }
                }

                if (draw) {
                    drawItem(x, y + 5, node.getStack());

                    float scale = 0.5f;

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale, scale, 1);

                    drawString(calculateOffsetOnScale(x + 1, scale), calculateOffsetOnScale(y - 2, scale), node.getStack().getDisplayName());
                    drawString(calculateOffsetOnScale(x + 21, scale), calculateOffsetOnScale(y + 10, scale), node.getAmount() + "x");

                    GlStateManager.popMatrix();

                    if (inBounds(x, y, 16, 16, mouseX, mouseY)) {
                        nodeHovering = node;
                    }

                    if (i == 1) {
                        x = 33;
                        y += 30;
                    } else {
                        x += 60;
                    }
                }

                slot++;
            }

            if (nodeHovering != null) {
                drawTooltip(mouseX, mouseY, t("misc.infinitystorage:energy_usage_minimal", nodeHovering.getEnergyUsage()));
            }

            if (inBounds(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
                drawTooltip(mouseX, mouseY, t("misc.infinitystorage:energy_usage", TileController.ENERGY_USAGE.getValue()) + "\n" + t("misc.infinitystorage:energy_stored", TileController.ENERGY_STORED.getValue(), TileController.ENERGY_CAPACITY.getValue()));
            }
        }
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) TileController.NODES.getValue().size() / 2F));
    }
}
