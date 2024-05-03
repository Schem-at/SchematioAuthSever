package io.schemat.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.schemat.utils.ApiClient;
import io.schemat.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class PasswordCommand extends Command {
    private final ApiClient apiClient;

    public PasswordCommand(ConfigManager configManager) {
        super("password");
        setDefaultExecutor(this::usage);

        var passwordArg = ArgumentType.String("password");

        addSyntax(this::executePassword, passwordArg);

        apiClient = new ApiClient(configManager);
    }

    private void usage(CommandSender sender, CommandContext context) {
        sender.sendMessage(Component.text("Usage: ", NamedTextColor.YELLOW)
                .append(Component.text("/password <password>", NamedTextColor.AQUA, TextDecoration.BOLD)));
    }

    private void executePassword(CommandSender sender, CommandContext context) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return;
        }

        String password = context.get("password");

        JsonObject payload = new JsonObject();
        payload.addProperty("password", password);
        payload.addProperty("player_uuid", player.getUuid().toString());
        payload.addProperty("player_name", player.getUsername());

        try {
            JsonElement response = apiClient.makePostRequest("/password-set", payload);

            if (!response.isJsonObject()) {
                player.sendMessage(Component.text("An error occurred while setting the password.", NamedTextColor.RED));
                return;
            }

            JsonObject responseObj = response.getAsJsonObject();

            if (responseObj.has("errors")) {
                AtomicReference<Component> errorMessage = new AtomicReference<>(Component.text("Validation errors:", NamedTextColor.RED, TextDecoration.BOLD)
                        .append(Component.newline()));

                responseObj.getAsJsonObject("errors").entrySet().forEach(entry -> {
                    errorMessage.set(errorMessage.get().append(Component.text(entry.getKey() + ": ", NamedTextColor.RED))
                            .append(Component.text(entry.getValue().getAsString(), NamedTextColor.RED))
                            .append(Component.newline()));
                });

                player.sendMessage(errorMessage.get());
                return;
            }

            player.sendMessage(Component.text("Password set successfully!", NamedTextColor.GREEN));
        } catch (IOException | InterruptedException e) {
            player.sendMessage(Component.text("An error occurred while setting the password.", NamedTextColor.RED));
            e.printStackTrace();
        }
    }
}