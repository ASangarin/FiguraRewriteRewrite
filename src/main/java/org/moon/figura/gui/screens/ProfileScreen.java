package org.moon.figura.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.moon.figura.gui.FiguraToast;
import org.moon.figura.gui.widgets.TexturedButton;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;

public class ProfileScreen extends AbstractPanelScreen {

    public static final Component TITLE = new FiguraText("gui.panels.title.profile").withStyle(ChatFormatting.RED);

    public ProfileScreen(Screen parentScreen) {
        super(parentScreen, TITLE, 0);
    }

    @Override
    public void init() {
        super.init();

        FiguraToast.sendToast("not yet!", "<3");

        this.addRenderableWidget(new TexturedButton(width / 2 - 30, height / 2 - 30, 60, 20, new TextComponent("meow"),
                new TextComponent("test").append("\n").append("one line").append("\n\n").append("two lines").append("\n").append("\n").append("two lines").append("\n\n\n").append("three lines").append("\n").append("\n").append("\n").append("three lines").append("\n"), button -> {
            FiguraToast.sendToast(new TextComponent("Backend restarting").setStyle(Style.EMPTY.withColor(0x99BBEE)), "in 10 minutes!", FiguraToast.ToastType.DEFAULT);
        }));

        this.addRenderableWidget(new TexturedButton(width / 2 - 30, height / 2 + 10, 60, 20, new TextComponent("meow"), TextUtils.tryParseJson(
                "{\"text\": \"△🟥🟧🟨🟩\n🟦🟪🟫⬜⬛\n\n❗❌🧀🍔🦐\n\n\n🌙🌀❤☆★\n\",\"font\": \"figura:default\"}"), button -> {
            FiguraToast.sendToast(new TextComponent("Backend restarting").setStyle(Style.EMPTY.withColor(0x99BBEE)), "in 10 minutes!", FiguraToast.ToastType.DEFAULT);
        }));
    }
}
