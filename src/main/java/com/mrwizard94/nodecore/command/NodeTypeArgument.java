package com.mrwizard94.nodecore.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mrwizard94.nodecore.node.NodeType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class NodeTypeArgument implements ArgumentType<NodeType> {
    private static final DynamicCommandExceptionType INVALID =
            new DynamicCommandExceptionType(value ->
                    Component.translatable("argument.nodecore.node_type.invalid", value));

    public static NodeTypeArgument nodeType() {
        return new NodeTypeArgument();
    }

    public static NodeType getNodeType(CommandContext<CommandSourceStack> ctx, String name) {
        return ctx.getArgument(name, NodeType.class);
    }

    @Override
    public NodeType parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();
        try {
            return NodeType.byId(input);
        } catch (IllegalArgumentException e) {
            throw INVALID.createWithContext(reader, input);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
                Arrays.stream(NodeType.values()).map(NodeType::getId),
                builder);
    }
}