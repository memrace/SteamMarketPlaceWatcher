package com.sozonovalexander.steammarketplacewatcher.models;

import lombok.Getter;

public enum SteamAppId {
    CS_GO(730, "CS:GO"),
    DOTA_2(570, "Dota 2");

    @Getter
    private final int id;
    @Getter
    private final String name;

    SteamAppId(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
