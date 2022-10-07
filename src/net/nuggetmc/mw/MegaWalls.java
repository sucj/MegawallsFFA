package net.nuggetmc.mw;

import jdk.nashorn.internal.objects.annotations.Getter;
import net.nuggetmc.mw.combat.CombatManager;
import net.nuggetmc.mw.command.*;
import net.nuggetmc.mw.economics.CoinsManager;
import net.nuggetmc.mw.economics.SellMenu;
import net.nuggetmc.mw.economics.ShopMenu;
import net.nuggetmc.mw.energy.EnergyManager;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.MWClassManager;
import net.nuggetmc.mw.mwclass.MWClassMenu;
import net.nuggetmc.mw.mwclass.classes.*;
import net.nuggetmc.mw.special.CompassManager;
import net.nuggetmc.mw.special.SpecialEventsManager;
import net.nuggetmc.mw.special.SpecialItemUtils;
import net.nuggetmc.mw.special.TeamsManager;
import net.nuggetmc.mw.utils.ItemUtils;
import net.nuggetmc.mw.utils.MWHealth;
import net.nuggetmc.mw.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MegaWalls extends JavaPlugin {

    private static MegaWalls INSTANCE;

    private PluginManager pluginManager;
    private MWClassManager mwClassManager;
    private MWClassMenu mwClassMenu;
    private ShopMenu shopMenu;

    @Getter
    public CompassManager getCompassManager() {
        return compassManager;
    }

    private CompassManager compassManager;
    public ArrayList<Player> bloodRageList = new ArrayList<>();

    @Getter
    public SpecialEventsManager getSpecialEventsManager() {
        return specialEventsManager;
    }

    private SpecialEventsManager specialEventsManager;

    @Getter
    public SellMenu getSellMenu() {
        return sellMenu;
    }

    private SellMenu sellMenu;
    private MWHealth mwhealth;
    private EnergyManager energyManager;
    private CoinsManager coinsManager;

    @Getter
    public TeamsManager getTeamsManager() {
        return teamsManager;
    }

    private TeamsManager teamsManager;
    private SpecialItemUtils specialItemUtils;

    @Getter
    public SpecialItemUtils getSpecialItemUtils() {
        return specialItemUtils;
    }

    private CombatManager combatManager;

    public static MegaWalls getInstance() {
        return INSTANCE;
    }

    public MWClassManager getClassManager() {
        return mwClassManager;
    }

    public MWClassMenu getMenu() {
        return mwClassMenu;
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    public MWHealth getMWHealth() {
        return mwhealth;
    }

    public EnergyManager getEnergyManager() {
        return energyManager;
    }

    @Getter
    public CoinsManager getCoinsManager() {
        return this.coinsManager;
    }

    @Getter
    public CombatManager getCombatManager() {
        return combatManager;
    }

    private boolean isChinese;
    public boolean antistealDiamond;
    //it is used to stop players from mining diamonds when there is only themselves.
    // 主播你不会用Vec3d或者BlockPos或者数组吗
    public List<Double> redspawn;
    public List<Double> greenspawn;
    public List<Double> bluespawn;
    public List<Double> yellowspawn;
    public int breakResetTime;
    public static boolean OPBYPASSGM = false;

    @Override
    public void onEnable() {
        INSTANCE = this;
        //cfg
        try {
            isChinese = (getConfig().get("use_chinese").equals(true));
        } catch (Exception e) {
            getConfig().set("use_chinese", false);
            isChinese = false;
            saveConfig();
        }
        try {
            breakResetTime = (int) getConfig().get("break_reset_time");
        } catch (Exception e) {
            getConfig().set("break_reset_time", 60);
            breakResetTime = 60;
            saveConfig();
        }
        try {
            antistealDiamond = (getConfig().get("antistealDiamond").equals(true));
        } catch (Exception e) {
            getConfig().set("antistealDiamond", true);
            antistealDiamond = true;
            saveConfig();
        }
        try {
            redspawn = getConfig().getDoubleList("spawnloc.red");
            greenspawn = getConfig().getDoubleList("spawnloc.green");
            bluespawn = getConfig().getDoubleList("spawnloc.blue");
            yellowspawn = getConfig().getDoubleList("spawnloc.yellow");
        } catch (Exception e) {

        }
        if (redspawn == null) {
            getConfig().set("spawnloc.red", new double[]{0, 100, 0});
            redspawn = new ArrayList<>(3);
            redspawn.add(0d);
            redspawn.add(100d);
            redspawn.add(0d);
        }
        if (greenspawn == null) {
            getConfig().set("spawnloc.green", new double[]{0, 100, 0});
            greenspawn = new ArrayList<>(3);
            greenspawn.add(0d);
            greenspawn.add(100d);
            greenspawn.add(0d);
        }
        if (bluespawn == null) {
            getConfig().set("spawnloc.blue", new double[]{0, 100, 0});
            bluespawn = new ArrayList<>(3);
            bluespawn.add(0d);
            bluespawn.add(100d);
            bluespawn.add(0d);
        }
        if (yellowspawn == null) {
            getConfig().set("spawnloc.yellow", new double[]{0, 0, 0});
            yellowspawn = new ArrayList<>(3);
            yellowspawn.add(0d);
            yellowspawn.add(100d);
            yellowspawn.add(0d);
        }

        try {
            OPBYPASSGM = (boolean) getConfig().get("opbypassgamemode");
        } catch (Exception e) {
            getConfig().set("opbypassgamemode", false);
            saveConfig();
        }
        // Create instances
        this.pluginManager = this.getServer().getPluginManager();
        this.mwClassManager = new MWClassManager(this);
        this.energyManager = new EnergyManager();
        this.coinsManager = new CoinsManager();
        this.specialEventsManager = new SpecialEventsManager();
        this.mwClassMenu = new MWClassMenu(this, "Class Selector");
        this.combatManager = new CombatManager();
        this.specialItemUtils = new SpecialItemUtils();
        this.teamsManager = new TeamsManager();
        this.mwhealth = new MWHealth();
        this.shopMenu = new ShopMenu();
        this.sellMenu = new SellMenu();
        this.compassManager = new CompassManager();

        // Register commands
        setExecutor("energy", new EnergyCommand());
        setExecutor("debug", new DebugCommand());
        setExecutor("mwinfo", new InfoCommand());
        setExecutor("mwspawn", new SetMWSpawnCommand());
        setExecutor("echest", new EchestCommand());
        setExecutor("mwcoins", new MWCoinsCommand());
        setExecutor("seeinv", new SeeinvCommand());
        setExecutor("mwshop", new ShopCommand());
        setExecutor("mwsell", new SellCommand());
        setExecutor("coinsmgr", new CoinsmgrCommand());
        setExecutor("mwride", new MWRideCommand());
        setExecutor("mwbaltop", new MWBalTopCommand());
        setExecutor("mwmakeride", new MWMakeRideCommand());
        setExecutor("mwresel", new MWReselCommand());
        setExecutorAndTabCompleter("mwitem", new getItemCommand());
        setExecutorAndTabCompleter("megawalls", new MegaWallsCommand());

        this.registerClasses(
                new MWCreeper(),
                new MWDreadlord(),
                new MWEnderman(),
                new MWGolem(),
                new MWHerobrine(),
                new MWSkeleton(),
                new MWSpider(),
                new MWSquid(),
                new MWZombie(),
                new MWCow(),
                new MWDriver(),
                new MWGuardian(),
                new MWShark(),
                new MWArcanist()
                // new MWWereWolf()
        );


        this.registerEvents(
                this.mwClassManager,
                this.mwClassMenu,
                this.mwhealth,
                this.energyManager,
                this.coinsManager,
                this.shopMenu,
                this.sellMenu,
                this.specialEventsManager,
                this.teamsManager,
                new WorldUtils()
        );

        // this.restore();
        this.initEnergy();

        ItemUtils.tickMWItems();


    }

    public int getOrDefaultFromConfig(String path, int defaulta) {
        int result;
        try {
            result = (int) getConfig().get(path);
        } catch (Exception e) {
            getConfig().set(path, defaulta);
            result = defaulta;
        }
        saveConfig();
        return result;
    }

    private void initEnergy() {
        energyManager.flash();

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (energyManager.get(p) == 0) {
                energyManager.clear(p);
            }
        });
    }

// 已弃用的方法删了得了
//    @Deprecated
//    private void restore() {
//        ConfigurationSection section = getConfig().getConfigurationSection("active_classes");
//        if (section == null) return;
//
//        ConfigurationSection sectionEnergy = getConfig().getConfigurationSection("energy");
//        boolean checkEnergy = sectionEnergy != null;
//
//        for (String key : section.getKeys(false)) {
//            String name = section.getString(key);
//
//            section.set(key, null);
//
//            Player player = Bukkit.getPlayer(key);
//            if (player == null || !player.isOnline()) continue;
//
//            MWClass mwclass = mwClassManager.fetch(name);
//            if (mwclass == null) continue;
//
//            mwClassManager.assign(player, mwclass, null);
//
//            if (checkEnergy) {
//                if (sectionEnergy.contains(key)) {
//                    int energy = sectionEnergy.getInt(key);
//
//                    energyManager.set(player, energy);
//
//                    sectionEnergy.set(key, null);
//                }
//            }
//        }
//
//        saveConfig();
//    }


    private void setExecutor(String name, CommandExecutor executor) {
        getCommand(name).setExecutor(executor);
    }

    private void setExecutorAndTabCompleter(String name, Object obj) {
        PluginCommand command = getCommand(name);

        command.setExecutor((CommandExecutor) obj);
        command.setTabCompleter((TabCompleter) obj);
    }

    private void registerEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(c -> pluginManager.registerEvents(c, this));
    }

    private void registerClasses(MWClass... mwclasses) {
        mwClassManager.register(mwclasses);
        Arrays.stream(mwclasses).forEach(this::registerEvents);
    }

    public boolean isChinese() {
        return isChinese;
    }

    public void setChinese(boolean chinese) {
        isChinese = chinese;
    }
    //  @EventHandler
    //public void onClearPot(PlayerDropItemEvent e){

    //        e.getPlayer().getInventory().remove(Material.GLASS_BOTTLE);
    //               return;
    //Why not working?
}



