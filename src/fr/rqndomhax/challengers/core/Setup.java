/*
 * Copyright (c) 2020.
 * Discord : _Paul#6918
 * Author : RqndomHax
 * Github: https://github.com/RqndomHax
 */

package fr.rqndomhax.challengers.core;

import fr.rqndomhax.challengers.activites.firstactivity.*;
import fr.rqndomhax.challengers.commands.ActivityCommands;
import fr.rqndomhax.challengers.inventoryapi.RInventoryHandler;
import fr.rqndomhax.challengers.inventoryapi.RInventoryManager;
import fr.rqndomhax.challengers.inventoryapi.RInventoryTask;
import fr.rqndomhax.challengers.listeners.LocationManager;
import fr.rqndomhax.challengers.listeners.PlayerListener;
import fr.rqndomhax.challengers.listeners.TeamListener;
import fr.rqndomhax.challengers.managers.Activites;
import fr.rqndomhax.challengers.managers.MessageManagers;
import fr.rqndomhax.challengers.managers.PlayerData;
import fr.rqndomhax.challengers.managers.game.GameManager;
import fr.rqndomhax.challengers.managers.game.GameState;
import fr.rqndomhax.challengers.managers.tasks.TaskManager;
import fr.rqndomhax.challengers.managers.team.TeamData;
import fr.rqndomhax.challengers.managers.team.TeamList;
import fr.rqndomhax.challengers.managers.team.TeamManager;
import fr.rqndomhax.challengers.scoreboard.TeamScoreboard;
import fr.rqndomhax.challengers.utils.Tablist;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.UUID;

public class Setup {
    private final Core core;
    private final MessageManagers mm;
    private RInventoryManager rInventoryManager;
    private Bodyguard bG;
    private VIP vip;
    private FirstM fm;
    private TeamManager tm;
    private GameManager gm;
    private TaskManager taskM;
    private TeamScoreboard teamScoreboard;

    public Setup(Core core) {
        this.core = core;
        mm = new MessageManagers(core);
        new Tablist().registerTab(core);
    }

    private void registerVars() {
        rInventoryManager = new RInventoryManager();
        tm = new TeamManager();
        fm = new FirstM(this);
        gm = new GameManager(this);
        taskM = new TaskManager(this);
        bG = new Bodyguard(this);
        vip = new VIP(this);
    }

    // Register all plugin events
    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new TeamListener(this), this.core);
        pm.registerEvents(new PlayerListener(this), this.core);
        pm.registerEvents(new RInventoryHandler(this.core, rInventoryManager), this.core);
        new RInventoryTask(new RInventoryManager()).runTaskTimerAsynchronously(this.core, 0, 1);
        pm.registerEvents(new LocationManager(this), this.core);
    }

    // Register all plugin commands
    private void registerCommands() {

        this.core.getCommand("ac").setExecutor(new ActivityCommands(this));
        this.core.getCommand("vip").setExecutor(new VipSelect(this));
        this.core.getCommand("bg").setExecutor(new BodyGuardSelect(this));

    }

    // Create teams
    private void createTeams() {

        HashMap<Activites, Location> locations = new HashMap<>();

        for(TeamList teamList : TeamList.values()) {

            for(Activites activites : Activites.values()) {

                if(activites == Activites.ARENAMOB || activites == Activites.MAZE) continue;

                String[] coords = core.getConfig().getString("Locations." + activites.getName().substring(2) + "." + teamList.getPath() + ".coords").replaceAll(" ", "").split(",");

                locations.put(activites, new Location(Bukkit.getWorld(core.getConfig().getString("Locations." + activites.getName().substring(2) + "." + teamList.getPath() + ".WorldName"))
                                                     , Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[1])));

            }

            tm.getTeam().getTeams().add(new TeamData(teamList, core.getConfig().getInt("Teams." + teamList.getPath() + "maxSlots"), locations));

            locations.clear();
        }

    }

    // Register scoreboard
    private void registerScoreboard() {
        teamScoreboard = new TeamScoreboard(this);
    }

    public void setup() {

        // Save plugin's config
        this.core.saveDefaultConfig();

        registerVars();

        // Set game state to waiting
        gm.getGame().setGameState(GameState.WAITING);

        createTeams();

        // Register plugin's events and plugin's commands
        registerEvents();
        registerCommands();

        PlayerData rushWay = new PlayerData(UUID.fromString("da749607-d3ea-4407-a229-54d6233da1d4"));
        PlayerData friendOne = new PlayerData(UUID.fromString("3e9762ec-681a-4cda-80df-30d3cdd7000b"));
        PlayerData friendTwo = new PlayerData(UUID.fromString("0beaed02-2216-4a4c-9444-533e449404aa"));

        gm.getGame().getPlayers().add(rushWay);
        gm.getGame().getPlayers().add(friendOne);
        gm.getGame().getPlayers().add(friendTwo);

        tm.addToTeam(rushWay, TeamList.RED);
        tm.addToTeam(friendOne, TeamList.GREEN);
        tm.addToTeam(friendTwo, TeamList.CYAN);

        registerScoreboard();
        teamScoreboard.runBoard();
    }

    public Core getCore() {
        return core;
    }

    public FirstM getFm() {
        return fm;
    }

    public GameManager getGm() {
        return gm;
    }

    public MessageManagers getMm() {
        return mm;
    }

    public TaskManager getTaskM() {
        return taskM;
    }

    public TeamManager getTm() {
        return tm;
    }

    public Bodyguard getbG() {
        return bG;
    }

    public VIP getVip() {
        return vip;
    }

    public TeamScoreboard getTeamScoreboard() {
        return teamScoreboard;
    }

}
