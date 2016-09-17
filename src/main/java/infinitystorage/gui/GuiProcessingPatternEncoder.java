package infinitystorage.gui;

import infinitystorage.tile.data.TileDataManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.SoundEvents;
import infinitystorage.InfinityStorage;
import infinitystorage.container.ContainerProcessingPatternEncoder;
import infinitystorage.network.MessageGridPatternCreate;
import infinitystorage.network.MessageProcessingPatternEncoderClear;
import infinitystorage.tile.TileProcessingPatternEncoder;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;

public class GuiProcessingPatternEncoder extends GuiBase {
    private TileProcessingPatternEncoder processingPatternEncoder;

    private GuiCheckBox patternOredicted;

    public GuiProcessingPatternEncoder(ContainerProcessingPatternEncoder container, TileProcessingPatternEncoder processingPatternEncoder) {
        super(container, 176, 184);

        this.processingPatternEncoder = processingPatternEncoder;
    }

    @Override
    public void init(int x, int y) {
        patternOredicted = addCheckBox(x + 7, y + 76, t("misc.infinitystorage:use_oredict"), TileProcessingPatternEncoder.PATTERN_OREDICTED.getValue());
    }

    @Override
    public void update(int x, int y) {
    }

    private boolean isOverCreatePattern(int mouseX, int mouseY) {
        return inBounds(152, 38, 16, 16, mouseX, mouseY) && processingPatternEncoder.canCreatePattern();
    }

    private boolean isOverClear(int mouseX, int mouseY) {
        return inBounds(136, 75, 7, 7, mouseX, mouseY);
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/processing_pattern_encoder.png");

        drawTexture(x, y, 0, 0, width, height);

        int ty = 0;

        if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
            ty = 1;
        }

        if (!processingPatternEncoder.canCreatePattern()) {
            ty = 2;
        }

        drawTexture(x + 152, y + 38, 178, ty * 16, 16, 16);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:processing_pattern_encoder"));
        drawString(7, 91, t("container.inventory"));

        if (isOverCreatePattern(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("gui.infinitystorage:processing_pattern_encoder.pattern_create"));
        }

        if (isOverClear(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("misc.infinitystorage:clear"));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
            InfinityStorage.INSTANCE.network.sendToServer(new MessageGridPatternCreate(processingPatternEncoder.getPos().getX(), processingPatternEncoder.getPos().getY(), processingPatternEncoder.getPos().getZ()));

            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        } else if (isOverClear(mouseX - guiLeft, mouseY - guiTop)) {
            InfinityStorage.INSTANCE.network.sendToServer(new MessageProcessingPatternEncoderClear(processingPatternEncoder));

            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException{
        super.actionPerformed(button);

        if(button == patternOredicted) {
            TileDataManager.setParameter(TileProcessingPatternEncoder.PATTERN_OREDICTED, patternOredicted.isChecked());
        }
    }

    public void updatePatternOredicted(boolean oredicted){
        if(patternOredicted != null){
            patternOredicted.setIsChecked(oredicted);
        }
    }
}
