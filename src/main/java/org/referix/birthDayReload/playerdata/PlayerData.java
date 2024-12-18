package org.referix.birthDayReload.playerdata;

import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    private Player player;
    private LocalDate birthday;
    private boolean isWished;
    private List<String> Wished;

    private String prefix;

    public PlayerData(Player player, LocalDate birthday, boolean isWished, List<String> wished , String prefix) {
        this.player = player;
        this.birthday = birthday;
        this.isWished = isWished;
        Wished = wished;
        this.prefix = prefix;
    }

    public PlayerData(Player player) {
        this.player = player;
        this.birthday = null;
        this.isWished = false;
        this.Wished = new ArrayList<>();
        this.prefix = null;
    }


    public String getPrefix() {
        return prefix;
    }


    public List<String> getWished() {
        return Wished;
    }

    public boolean isWished() {
        return isWished;
    }

    public Player getPlayer() {
        return player;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setWished(boolean wished) {
        isWished = wished;
    }

    public void setWished(List<String> wished) {
        Wished = wished;
    }
}
