// Main.java
package io.schemat;

import io.schemat.utils.ConfigManager;
import io.schemat.commands.PasswordCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.utils.identity.NamedAndIdentified;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        MojangAuth.init();

        ConfigManager configManager = new ConfigManager();

        MinecraftServer.getBenchmarkManager().enable(Duration.of(10, TimeUnit.SECOND));
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> System.out.println("Good night"));
        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> {
            ResponseData responseData = event.getResponseData();
            responseData.addEntry(NamedAndIdentified.named("The first line is separated from the others"));
            responseData.addEntry(NamedAndIdentified.named("Could be a name, or a message"));
            if (event.getConnection() != null) {
                responseData.addEntry(NamedAndIdentified.named("IP test: " + event.getConnection().getRemoteAddress().toString()));
                responseData.addEntry(NamedAndIdentified.named("Connection Info:"));
                String ip = event.getConnection().getServerAddress();
                responseData.addEntry(NamedAndIdentified.named(Component.text('-', NamedTextColor.DARK_GRAY)
                        .append(Component.text(" IP: ", NamedTextColor.GRAY))
                        .append(Component.text(ip != null ? ip : "???", NamedTextColor.YELLOW))));
                responseData.addEntry(NamedAndIdentified.named(Component.text('-', NamedTextColor.DARK_GRAY)
                        .append(Component.text(" PORT: ", NamedTextColor.GRAY))
                        .append(Component.text(event.getConnection().getServerPort()))));
                responseData.addEntry(NamedAndIdentified.named(Component.text('-', NamedTextColor.DARK_GRAY)
                        .append(Component.text(" VERSION: ", NamedTextColor.GRAY))
                        .append(Component.text(event.getConnection().getProtocolVersion()))));
            }
            responseData.addEntry(NamedAndIdentified.named(Component.text("Time", NamedTextColor.YELLOW)
                    .append(Component.text(": ", NamedTextColor.GRAY))
                    .append(Component.text(System.currentTimeMillis(), Style.style(TextDecoration.ITALIC)))));
            responseData.addEntry(NamedAndIdentified.named(Component.text("You can use ").append(Component.text("styling too!", NamedTextColor.RED, TextDecoration.BOLD))));
            responseData.setDescription(Component.text("This is a Minestom Server", TextColor.color(0x66b3ff)));
        });
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setGenerator(unit ->
                unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            System.out.println(player.getUsername());
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new PasswordCommand(configManager));
        minecraftServer.start("0.0.0.0", 25565);
    }
}

