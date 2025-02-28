package org.moon.figura.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.avatars.AvatarManager;
import org.moon.figura.backend.NetworkManager;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.List;

public class StatusWidget implements FiguraWidget, FiguraTickable, GuiEventListener {

    public static final String STATUS_INDICATORS = "-*/+";
    public static final List<String> STATUS_NAMES = List.of("size", "texture", "script", "backend");
    public static final List<Style> TEXT_COLORS = List.of(
            Style.EMPTY.withColor(ChatFormatting.WHITE),
            Style.EMPTY.withColor(ChatFormatting.RED),
            Style.EMPTY.withColor(ChatFormatting.YELLOW),
            Style.EMPTY.withColor(ChatFormatting.GREEN)
    );
    public static final int SIZE_WARNING = 75_000;
    public static final int SIZE_LARGE = 100_000;

    private final Font font;
    protected final int count;
    protected int status = 0;
    private Component disconnectedReason;

    public int x, y;
    public int width, height;
    private boolean visible = true;
    private boolean background = true;

    public StatusWidget(int x, int y, int width) {
        this(x, y, width, STATUS_NAMES.size());
    }

    protected StatusWidget(int x, int y, int width, int count) {
        this.x = x;
        this.y = y;
        this.font = Minecraft.getInstance().font;
        this.width = width;
        this.height = font.lineHeight + 5;
        this.count = count;
    }

    @Override
    public void tick() {
        if (!visible) return;

        //update status indicators
        Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        boolean empty = avatar == null || avatar.nbt == null;

        status = empty ? 0 : avatar.fileSize > SIZE_LARGE ? 1 : avatar.fileSize > SIZE_WARNING ? 2 : 3;

        int texture = empty || !avatar.hasTexture ? 0 : 3;
        status += texture << 2;

        int script = empty ? 0 : avatar.scriptError ? 1 : avatar.luaRuntime == null ? 0 : avatar.versionStatus > 0 ? 2 : 3;
        status += script << 4;

        int backend = NetworkManager.backendStatus;
        status += backend << 6;

        String dc = NetworkManager.disconnectedReason;
        disconnectedReason = backend == 1 && dc != null && !dc.isBlank() ? new TextComponent(dc) : null;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        //background
        if (background)
            UIHelper.renderSliced(stack, x, y, width, height, UIHelper.OUTLINE);

        //hover
        boolean hovered = this.isMouseOver(mouseX, mouseY);

        //text and tooltip
        float spacing = (width - (background ? 13 : 11)) / (count - 1f);
        float hSpacing = spacing / 2;
        for (int i = 0; i < count; i++) {
            int x = (int) (this.x + spacing * i + (background ? 1 : 0));

            Component text = getStatus(i);
            UIHelper.drawString(stack, font, text, x, y + (background ? 3 : 0), 0xFFFFFF);

            if (hovered && mouseX >= x - hSpacing + 6 && mouseX < x + hSpacing + 6 && mouseY >= y && mouseY < y + (background ? 14 : 11))
                UIHelper.setTooltip(getTooltipFor(i));
        }
    }

    private MutableComponent getStatus(int type) {
        return new TextComponent(String.valueOf(STATUS_INDICATORS.charAt(status >> (type * 2) & 3))).setStyle(Style.EMPTY.withFont(TextUtils.FIGURA_FONT));
    }

    public Component getTooltipFor(int i) {
        //get name and color
        int color = status >> (i * 2) & 3;
        String part = "gui.status.";
        MutableComponent text = new FiguraText(part += STATUS_NAMES.get(i)).append("\n• ").append(new FiguraText(part + "." + color)).setStyle(TEXT_COLORS.get(color));

        //get backend disconnect reason
        if (i == 3 && disconnectedReason != null)
            text.append("\n\n").append(new FiguraText(part + ".reason")).append("\n• ").append(disconnectedReason);

        return text;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return UIHelper.isMouseOver(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }
}
