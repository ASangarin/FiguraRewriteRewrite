package org.moon.figura.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.avatars.AvatarManager;
import org.moon.figura.utils.FiguraText;

public class FiguraRunCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        LiteralArgumentBuilder<FabricClientCommandSource> run = LiteralArgumentBuilder.literal("run");
        RequiredArgumentBuilder<FabricClientCommandSource, String> arg = RequiredArgumentBuilder.argument("code", StringArgumentType.greedyString());
        arg.executes(FiguraRunCommand::executeCode);
        run.then(arg);
        return run;
    }

    private static int executeCode(CommandContext<FabricClientCommandSource> context) {
        String lua = StringArgumentType.getString(context, "code");
        Avatar localAvatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        if (localAvatar == null) {
            context.getSource().sendError(new FiguraText("command.run.not_local_error"));
            return 0;
        }
        if (localAvatar.luaRuntime == null || localAvatar.scriptError) {
            context.getSource().sendError(new FiguraText("command.run.no_script_error"));
            return 0;
        }

        return localAvatar.luaRuntime.runCommand(lua);
    }
}
