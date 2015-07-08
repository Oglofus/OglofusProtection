/*
 * Copyright 2014-2015 Nikos Grammatikos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://raw.githubusercontent.com/nikosgram13/OglofusProtection/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.nikosgram.oglofus.protection;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sk89q.intake.Intake;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.Authorizer;
import lombok.Getter;
import me.nikosgram.oglofus.protection.api.CommandExecutor;
import me.nikosgram.oglofus.protection.api.Platform;
import me.nikosgram.oglofus.protection.api.ProtectionPlugin;
import me.nikosgram.oglofus.protection.api.manager.HandlerManager;
import me.nikosgram.oglofus.protection.api.manager.RegionManager;
import me.nikosgram.oglofus.protection.api.manager.UserManager;
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import me.nikosgram.oglofus.protection.database.DatabaseConnector;
import me.nikosgram.oglofus.protection.database.MySQLDatabaseDriver;
import me.nikosgram.oglofus.protection.database.SQLiteDatabaseDriver;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerBreakBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerInteractBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerPlaceBlockEvent;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@SuppressWarnings("unused")
@Plugin(id = "OglofusProtection", name = "OglofusProtection", version = "2.0.1-R0.1-SNAPSHOT")
public class OglofusSponge implements ProtectionPlugin {
    protected final Properties properties;
    @Getter
    private final Platform platform = Platform.Sponge;
    @Getter
    private final HandlerManager handlerManager = new OglofusHandlerManager();
    @Inject
    protected Game game;
    protected Server server;
    @Inject
    protected Logger logger;
    @Inject
    @DefaultConfig(sharedRoot = true)
    protected File configFile;
    @Inject
    @DefaultConfig(sharedRoot = true)
    protected ConfigurationLoader<CommentedConfigurationNode> configManager;
    protected ConfigurationNode config;
    protected DatabaseConnector connector;
    @Getter
    protected RegionManager regionManager;
    @Getter
    protected UserManager userManager;
    protected Dispatcher dispatcher;

    public OglofusSponge() {
        InputStream stream = this.getClass().getResourceAsStream("/.properties");
        properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OglofusProtection.invoke(this);
    }

    @Subscribe
    public void onPreInitialization(PreInitializationEvent event) {
        server = game.getServer();
        try {
            if (!configFile.exists()) {
                Files.createFile(configFile.toPath());
                config = configManager.load();

                config.getNode("ConfigVersion").setValue(1);

                config.getNode("database", "type").setValue("mysql");
                config.getNode("database", "host").setValue("localhost");
                config.getNode("database", "port").setValue(3306);
                config.getNode("database", "user").setValue("root");
                config.getNode("database", "pass").setValue("password");
                config.getNode("database", "data").setValue("database");

                config.getNode("protection", "material").setValue("SPONGE");
                config.getNode("protection", "metadata").setValue("protector");

                configManager.save(config);
                logger.info(
                        "Created default configuration, " +
                                "OglofusProtection will not run until you have edited this file!"
                );
            }
        } catch (IOException exception) {
            logger.error("Couldn't create default configuration file!");
        }

        if (config.getNode("database", "type").getString().equalsIgnoreCase("sqlite")) {
            connector = new DatabaseConnector(
                    new SQLiteDatabaseDriver(Paths.get(config.getNode("database", "host").getString()))
            );
        } else {
            connector = new DatabaseConnector(new MySQLDatabaseDriver(
                    config.getNode("database", "user").getString(),
                    config.getNode("database", "data").getString(),
                    config.getNode("database", "pass").getString(),
                    config.getNode("database", "host").getString(),
                    config.getNode("database", "port").getInt()
            ));
        }

        connector.openConnection();

        if (connector.checkConnection()) {
            regionManager = new OglofusRegionManager(this);
            userManager = new OglofusUserManager(this);
        }
    }

    @Subscribe
    public void onInitialization(InitializationEvent event) {
        Injector injector = Intake.createInjector();
        injector.install(new PrimitivesModule());
        injector.install(new OglofusModule());

        ParametricBuilder builder = new ParametricBuilder(injector);
        builder.setAuthorizer(new Authorizer() {
            @Override
            public boolean testPermission(Namespace namespace, String permission) {
                CommandExecutor sender = namespace.get(CommandExecutor.class);
                if (sender == null) {
                    throw new RuntimeException("Uh oh! A user didn't use this command.");
                } else {
                    return sender.hasPermission(permission);
                }
            }
        });

        dispatcher = new CommandGraph()
                .builder(builder)
                .commands()
                .group("protection", "protector", "protect", "p")
                .registerMethods(new OglofusCommands())
                .parent()
                .graph()
                .getDispatcher();
    }

    @Subscribe
    public void onServerStopped(ServerStoppedEvent event) {
        connector.closeConnection();
    }

    @Subscribe
    public void security(PlayerInteractBlockEvent event) {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                ((World) event.getBlock().getExtent()).getUniqueId(),
                event.getBlock().getBlockX(),
                event.getBlock().getBlockY(),
                event.getBlock().getBlockZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) {
            if (!region.get().getProtectionStaff().hasMemberAccess(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            } else {
                if (region.get().getProtectionVector().getBlockLocation().equals(location)) {
                    if (!region.get().getProtectionStaff().hasOwnerAccess(event.getEntity().getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Subscribe
    public void security(PlayerPlaceBlockEvent event) {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                ((World) event.getBlock().getExtent()).getUniqueId(),
                event.getBlock().getBlockX(),
                event.getBlock().getBlockY(),
                event.getBlock().getBlockZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) {
            if (!region.get().getProtectionStaff().hasMemberAccess(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            } else {
                if (region.get().getProtectionVector().getBlockLocation().equals(location)) {
                    if (!region.get().getProtectionStaff().hasOwnerAccess(event.getEntity().getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Subscribe
    public void security(PlayerBreakBlockEvent event) {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                ((World) event.getBlock().getExtent()).getUniqueId(),
                event.getBlock().getBlockX(),
                event.getBlock().getBlockY(),
                event.getBlock().getBlockZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) {
            if (!region.get().getProtectionStaff().hasMemberAccess(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            } else {
                if (region.get().getProtectionVector().getBlockLocation().equals(location)) {
                    if (!region.get().getProtectionStaff().hasOwnerAccess(event.getEntity().getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public String getVersion() {
        return properties.getProperty("version");
    }

    @Override
    public String getVersionName() {
        return properties.getProperty("version-name");
    }

    @Override
    public int getProtocol() {
        return (int) properties.get("protocol");
    }
}
