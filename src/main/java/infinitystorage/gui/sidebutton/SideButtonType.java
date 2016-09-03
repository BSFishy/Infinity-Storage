package infinitystorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import infinitystorage.gui.GuiBase;
import infinitystorage.tile.config.IType;
import infinitystorage.tile.data.TileDataManager;
import infinitystorage.tile.data.TileDataParameter;

public class SideButtonType extends SideButton {
    private TileDataParameter<Integer> type;

    public SideButtonType(TileDataParameter<Integer> type) {
        this.type = type;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.infinitystorage:type") + TextFormatting.RESET + "\n" + gui.t("sidebutton.infinitystorage:type." + type.getValue());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");

        gui.drawTexture(x, y + 1, 16 * type.getValue(), 128, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(type, type.getValue() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
    }
}
