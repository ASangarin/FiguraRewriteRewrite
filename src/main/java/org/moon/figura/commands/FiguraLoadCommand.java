package org.moon.figura.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import org.moon.figura.avatars.AvatarManager;
import org.moon.figura.avatars.providers.LocalAvatarFetcher;
import org.moon.figura.utils.FiguraText;

import java.nio.file.Path;

public class FiguraLoadCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        LiteralArgumentBuilder<FabricClientCommandSource> load = LiteralArgumentBuilder.literal("load");

        RequiredArgumentBuilder<FabricClientCommandSource, String> path = RequiredArgumentBuilder.argument("path", StringArgumentType.greedyString());
        path.executes(FiguraLoadCommand::loadAvatar);

        return load.then(path);
    }

    private static int loadAvatar(CommandContext<FabricClientCommandSource> context) {
        String str = StringArgumentType.getString(context, "path");
        try {
            //parse path
            Path p = LocalAvatarFetcher.getLocalAvatarDirectory().resolve(Path.of(str));

            //try to load avatar
            AvatarManager.loadLocalAvatar(p);
            context.getSource().sendFeedback(new FiguraText("command.load.loading"));
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(new FiguraText("command.load.invalid", str));
        }

        return 0;
    }
}
