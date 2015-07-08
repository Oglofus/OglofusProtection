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
import me.nikosgram.oglofus.protection.handler.WorldGuardHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

@SuppressWarnings("unused")
public class OglofusBukkit extends JavaPlugin implements ProtectionPlugin, Listener {
    protected final Properties properties;
    @Getter
    private final Platform platform = Platform.Bukkit;
    @Getter
    private final HandlerManager handlerManager = new OglofusHandlerManager();
    protected DatabaseConnector connector;
    protected Dispatcher dispatcher;
    @Getter
    private RegionManager regionManager;
    @Getter
    private UserManager userManager;

    public OglofusBukkit() {
        InputStream stream = this.getClass().getResourceAsStream("/.properties");
        properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OglofusProtection.invoke(this);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        if (getConfig().getString("database.type").equalsIgnoreCase("sqlite")) {
            this.connector = new DatabaseConnector(
                    new SQLiteDatabaseDriver(Paths.get(getConfig().getString("database.host")))
            );
        } else {
            this.connector = new DatabaseConnector(new MySQLDatabaseDriver(
                    getConfig().getString("database.user"),
                    getConfig().getString("database.data"),
                    getConfig().getString("database.pass"),
                    getConfig().getString("database.host"),
                    getConfig().getInt("database.port")
            ));
        }

        this.connector.openConnection();

        if (connector.checkConnection()) {
            this.regionManager = new OglofusRegionManager(this);
            this.userManager = new OglofusUserManager(this);
        }
    }

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            handlerManager.registerHandlers(new WorldGuardHandler());
        }

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

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        connector.closeConnection();
    }

    @EventHandler
    public void security(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL) ||
                event.getAction().equals(Action.RIGHT_CLICK_AIR) ||
                event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            return;
        }
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getClickedBlock().getWorld().getUID(),
                event.getClickedBlock().getX(),
                event.getClickedBlock().getY(),
                event.getClickedBlock().getZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) {
            if (!region.get().getProtectionStaff().hasMemberAccess(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            } else {
                if (region.get().getProtectionVector().getBlockLocation().equals(location)) {
                    if (!region.get().getProtectionStaff().hasOwnerAccess(event.getPlayer().getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void security(BlockBurnEvent event) {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getBlock().getWorld().getUID(),
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) event.setCancelled(true);
    }

    @EventHandler
    public void security(BlockPistonEvent event) {
        //TODO: needs tests.
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getBlock().getWorld().getUID(),
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) event.setCancelled(true);
    }

    @EventHandler
    public void security(BlockMultiPlaceEvent event) {
        //TODO: needs tests.
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getBlock().getWorld().getUID(),
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) event.setCancelled(true);
    }

    @EventHandler
    public void security(BlockPlaceEvent event) {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getBlock().getWorld().getUID(),
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) {
            if (!region.get().getProtectionStaff().hasMemberAccess(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            } else {
                if (region.get().getProtectionVector().getBlockLocation().equals(location)) {
                    if (!region.get().getProtectionStaff().hasOwnerAccess(event.getPlayer().getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void security(BlockBreakEvent event) {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getBlock().getWorld().getUID(),
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ()
        );
        Optional<ProtectionRegion> region = getRegionManager().getRegion(location);
        if (region.isPresent()) {
            if (event.getPlayer() != null) {
                if (!region.get().getProtectionStaff().hasMemberAccess(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                } else {
                    if (region.get().getProtectionVector().getBlockLocation().equals(location)) {
                        if (!region.get().getProtectionStaff().hasOwnerAccess(event.getPlayer().getUniqueId())) {
                            event.setCancelled(true);
                        }
                    }
                }
            } else {
                event.setCancelled(true);
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
