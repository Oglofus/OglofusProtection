package me.nikosgram.oglofus.protection;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.gson.reflect.TypeToken;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.nikosgram.oglofus.protection.api.entity.User;
import me.nikosgram.oglofus.protection.api.message.MessageType;
import me.nikosgram.oglofus.protection.api.region.ProtectionRank;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ToString
@EqualsAndHashCode
public class OglofusUser implements User {
    private final Map<ProtectionRegion, ProtectionRank> regions = new HashMap<ProtectionRegion, ProtectionRank>();
    private final OglofusBukkit bukkit;
    private final OfflinePlayer player;
    private final Path dataFolder;

    public OglofusUser(OglofusBukkit bukkit, OfflinePlayer player, Path dataFolder) {
        this.bukkit = bukkit;
        this.player = player;
        this.dataFolder = dataFolder;

        update();
    }

    protected final void update() {
        if (!regions.isEmpty()) regions.clear();

        Path data = Paths.get(dataFolder.toString(), player.getUniqueId().toString() + ".json");

        if (!Files.exists(data)) {
            FileWriter writer = null;
            try {
                ProtectionUtils.GSON.toJson(new HashMap<UUID, ProtectionRank>(), (writer = new FileWriter(data.toFile())));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Map<UUID, ProtectionRank> regs = null;
            FileReader reader = null;
            try {
                regs = ProtectionUtils.GSON.fromJson((reader = new FileReader(data.toFile())), new TypeToken<Map<UUID, ProtectionRank>>() {}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (regs != null) {
                for (Map.Entry<UUID, ProtectionRank> reg : regs.entrySet()) {
                    Optional<ProtectionRegion> region = bukkit.getRegionManager().getRegion(reg.getKey());
                    if (region.isPresent()) {
                        regions.put(region.get(), reg.getValue());
                    } else {
                        regs.remove(reg.getKey());
                    }
                }

                FileWriter writer = null;
                try {
                    ProtectionUtils.GSON.toJson(regs, (writer = new FileWriter(data.toFile())));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public UUID getUuid() {
        return player.getUniqueId();
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public Collection<ProtectionRegion> getOwnRegions() {
        List<ProtectionRegion> returned = new ArrayList<ProtectionRegion>();
        for (ProtectionRegion region : regions.keySet()) {
            if (regions.get(region).equals(ProtectionRank.Owner)) {
                returned.add(region);
            }
        }
        return returned;
    }

    @Override
    public Collection<ProtectionRegion> getMemberRegions() {
        List<ProtectionRegion> returned = new ArrayList<ProtectionRegion>();
        for (ProtectionRegion region : regions.keySet()) {
            if (regions.get(region).equals(ProtectionRank.Member)) {
                returned.add(region);
            }
        }
        return returned;
    }

    @Override
    public Collection<ProtectionRegion> getOfficerRegions() {
        List<ProtectionRegion> returned = new ArrayList<ProtectionRegion>();
        for (ProtectionRegion region : regions.keySet()) {
            if (regions.get(region).equals(ProtectionRank.Officer)) {
                returned.add(region);
            }
        }
        return returned;
    }

    @Override
    public Optional<ProtectionRegion> getRegion() {
        if (player.isOnline()) {
            return bukkit.getRegionManager().getRegion(new OglofusProtectionLocation(bukkit, ((Player) player).getLocation()));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public void sendMessage(MessageType type, String... messages) {
        if (player.isOnline()) {
            for (String message : messages) {
                OglofusUtils.sendMessage(((Player) player), message, type);
            }
        }
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.isOnline() && ((Player) player).hasPermission(permission);
    }

    @Override
    public void sendMessage(String... messages) {
        sendMessage(MessageType.CHAT, messages);
    }

    @Override
    public void sendMessage(Object... messages) {
        sendMessage(MessageType.CHAT, (String[]) messages);
    }

    @Override
    public boolean executeCommand(String command, String[] parameters) {
        return player.isOnline() && ((Player) player).performCommand(command + ' ' + Joiner.on(' ').join(parameters));
    }
}
