package dev.skyphi.Models;

import java.util.UUID;

import dev.skyphi.IridiumCTF;

public class Statistics {

    public static void add(String statistic, UUID playerUUID, long value) {
        if (IridiumCTF.API == null) return;
        IridiumCTF.API.addStatistic("CTF", statistic, playerUUID, value);
    }

    public static void increment(String statistic, UUID playerUUID) {
        if (IridiumCTF.API == null) return;
        IridiumCTF.API.addStatistic("CTF", statistic, playerUUID, 1);
    }

}
