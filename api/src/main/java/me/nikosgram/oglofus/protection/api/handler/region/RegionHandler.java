package me.nikosgram.oglofus.protection.api.handler.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.nikosgram.oglofus.protection.api.handler.Handler;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

@ToString
@RequiredArgsConstructor
@SuppressWarnings("unused")
public abstract class RegionHandler extends Handler {
    @Getter
    private final ProtectionRegion region;
}
