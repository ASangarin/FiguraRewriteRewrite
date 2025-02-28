package org.moon.figura.gui.widgets.trust;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.avatars.AvatarManager;
import org.moon.figura.gui.widgets.StatusWidget;
import org.moon.figura.trust.TrustContainer;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.MathUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class PlayerStatusWidget extends StatusWidget {

	private static final List<Function<Avatar, MutableComponent>> HOVER_TEXT = List.of(
			avatar -> new FiguraText("gui.trust.size").append("\n• ").append(MathUtils.asFileSize(avatar.fileSize)),
			avatar -> new FiguraText("gui.trust.complexity").append("\n• ").append(String.valueOf(avatar.complexity)),
			avatar -> new FiguraText("gui.trust.init").append("\n• ").append(new FiguraText("gui.trust.init.root", avatar.initInstructions))
					.append("\n• ").append(new FiguraText("gui.trust.init.entity", avatar.entityInitInstructions)),
			avatar -> new FiguraText("gui.trust.tick").append("\n• ").append(new FiguraText("gui.trust.tick.world", avatar.worldTickInstructions))
					.append("\n• ").append(new FiguraText("gui.trust.tick.entity", avatar.entityTickInstructions)),
			avatar -> new FiguraText("gui.trust.render").append("\n• ")
					.append(new FiguraText("gui.trust.render.world", avatar.worldRenderInstructions)).append("\n• ")
					.append(new FiguraText("gui.trust.render.entity", avatar.entityRenderInstructions)).append("\n• ")
					.append(new FiguraText("gui.trust.render.post_entity", avatar.postEntityRenderInstructions)).append("\n• ")
					.append(new FiguraText("gui.trust.render.post_world", avatar.postWorldRenderInstructions)));

	private final UUID owner;
	private Avatar avatar;

	public PlayerStatusWidget(int x, int y, int width, UUID owner) {
		super(x, y, width, HOVER_TEXT.size());
		setBackground(false);

		this.owner = owner;
	}

	@Override
	public void tick() {
		avatar = AvatarManager.getAvatarForPlayer(owner);
		if (avatar == null || avatar.nbt == null) {
			status = 0;
			return;
		}

		//size
		status = avatar.fileSize > SIZE_LARGE ? 1 : avatar.fileSize > SIZE_WARNING ? 2 : 3;

		//complexity
		int complexity = avatar.renderer == null ? 0 : avatar.complexity >= avatar.trust.get(TrustContainer.Trust.COMPLEXITY) ? 1 : 3;
		status += complexity << 2;

		//script init
		int init = avatar.scriptError ? 1 : avatar.luaRuntime == null ? 0 : avatar.accumulatedInitInstructions >= avatar.trust.get(
				TrustContainer.Trust.INIT_INST) - 100 ? 2 : 3;
		status += init << 4;

		//script tick
		int tick = avatar.scriptError ? 1 : avatar.luaRuntime == null ? 0 : avatar.entityTickInstructions >= avatar.trust.get(
				TrustContainer.Trust.TICK_INST) - 100 || avatar.worldTickInstructions >= avatar.trust.get(
				TrustContainer.Trust.WORLD_TICK_INST) - 100 ? 2 : 3;
		status += tick << 6;

		//script render
		int render = avatar.scriptError ? 1 : avatar.luaRuntime == null ? 0 : avatar.accumulatedEntityRenderInstructions >= avatar.trust.get(
				TrustContainer.Trust.RENDER_INST) - 100 || avatar.accumulatedWorldRenderInstructions >= avatar.trust.get(
				TrustContainer.Trust.WORLD_RENDER_INST) - 100 ? 2 : 3;
		status += render << 8;
	}

	@Override
	public Component getTooltipFor(int i) {
		return avatar == null ? null : HOVER_TEXT.get(i).apply(avatar).setStyle(TEXT_COLORS.get(status >> (i * 2) & 3));
	}
}
