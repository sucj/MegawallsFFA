package net.nuggetmc.mw;


import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.addons.AutoMapper;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.impl.MActionInsertShellCode;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorHead;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.ShellCodeReflectionMixinPluginMethodCall;
import net.minecraft.server.v1_8_R3.*;
import net.nuggetmc.mw.combat.CombatManager;
import net.nuggetmc.mw.command.*;
import net.nuggetmc.mw.economics.CoinsManager;
import net.nuggetmc.mw.economics.SellMenu;
import net.nuggetmc.mw.economics.ShopMenu;
import net.nuggetmc.mw.energy.EnergyManager;
import net.nuggetmc.mw.killeffects.KEMenu;
import net.nuggetmc.mw.killeffects.KillEffectManager;
import net.nuggetmc.mw.mixins.MixinEntity;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.MWClassManager;
import net.nuggetmc.mw.mwclass.MWClassMenu;
import net.nuggetmc.mw.mwclass.classes.*;
import net.nuggetmc.mw.special.*;
import net.nuggetmc.mw.utils.ItemUtils;
import net.nuggetmc.mw.utils.MWHealth;
import net.nuggetmc.mw.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MegaWalls extends JavaPlugin {

    private static MegaWalls INSTANCE;

    public PluginManager pluginManager;
    private MWClassManager mwClassManager;
    private MWClassMenu mwClassMenu;
    private ShopMenu shopMenu;

    public static String getMetadataValue() {
        return "MegaWalls";
    }

    public CompassManager getCompassManager() {
        return compassManager;
    }

    private CompassManager compassManager;
    public ArrayList<Player> bloodRageList = new ArrayList<>();

    private SpecialEventsManager specialEventsManager;

    public SellMenu getSellMenu() {
        return sellMenu;
    }

    private SellMenu sellMenu;
    private MWHealth mwhealth;
    private EnergyManager energyManager;
    private CoinsManager coinsManager;

    public TeamsManager getTeamsManager() {
        return teamsManager;
    }

    public KillEffectManager getKillEffectManager() {
        return killEffectManager;
    }

    public KillEffectManager killEffectManager;
    public MK2 mk2;



    private TeamsManager teamsManager;


    private SpecialItemUtils specialItemUtils;

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

    public CoinsManager getCoinsManager() {
        return this.coinsManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }
    public KEMenu getKeMenu() {
        return keMenu;
    }

    private KEMenu keMenu;

    public List<Double> redspawn;
    public List<Double> greenspawn;
    public List<Double> bluespawn;
    public List<Double> yellowspawn;
    public int breakResetTime;
    public double hbrTrueDamage= 2.5;
    public int swordLuckDrawPrice;
    public boolean balancedMegaBreaker = false;

    FileConfiguration swordNames;

    public static Random getRandom() {
        return random;
    }

    static Random random=new Random();
    File file;
    public Map<Block, Material> resetMap=new HashMap<>();
    public void tickBlockReset(){
        if (resetMap.isEmpty()) {
            return;
        }
        if (!combatManager.getInCombatPlayers().isEmpty()) {
            for (Player player : this.combatManager.getInCombatPlayers()) {
                player.sendTitle(ChatColor.RED + "THE MAP IS GOING TO BE RESET", ChatColor.BOLD + "PLEASE WAIT...");
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "[ALERT]  " + ChatColor.RESET + "THE MAP IS GOING TO BE RESET!!!");
                if (getTeamsManager().getTeamOfPlayer(player)!=null) {
                    List<Double> list = MegaWalls.getInstance().getTeamsManager().getSpawnLocOfPlayer(player);
                    Location loc = new Location(player.getWorld(), list.get(0), list.get(1), list.get(2));
                    player.teleport(loc);
                }
            }
        }
        for (Block block:resetMap.keySet()){
            block.setType(resetMap.get(block));
        }
        resetMap.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        MixinPlugin mixinPlugin = MixBukkit.registerMixinPlugin(this, AutoMapper.getMappingAsStream());
        try {
            mixinPlugin.registerMixin(
                    "Test Mixin",  // 命名空间，随便起，用于防止重复注入
                    new MActionInsertShellCode(
                            new ShellCodeReflectionMixinPluginMethodCall(
                                    MixinEntity.class.getDeclaredMethod("a",
                                            Entity.class, Item.class,int.class, CallbackInfo.class)
                                    /*false*/
                            ),
                            new HLocatorHead()  // 注入到方法顶部
                    ),
                    Entity.class,           // 目标类
                    "a",                   // 反混淆方法名
                    EntityItem.class,
                    Item.class,int.class

            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }


        System.out.println("-----------------------------------");
        System.out.println("--------MEGAWALLSFFA LOADED--------");
        System.out.println("-----------------------------------");
        //cfg
        try {
            hbrTrueDamage = (double) getConfig().get("hbr_true_damage");
        } catch (Exception e) {
            getConfig().set("hbr_true_damage", 2.5);
            hbrTrueDamage = 2.5;
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
            redspawn = getConfig().getDoubleList("spawnloc.red");
            greenspawn = getConfig().getDoubleList("spawnloc.green");
            bluespawn = getConfig().getDoubleList("spawnloc.blue");
            yellowspawn = getConfig().getDoubleList("spawnloc.yellow");
        } catch (Exception ignored) {

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
            swordLuckDrawPrice = getConfig().getInt("swordLuckDrawPrice");
        } catch (Exception e) {
            getConfig().set("swordLuckDrawPrice", 500);
            hbrTrueDamage = 500;
            saveConfig();
        }
        try {
            balancedMegaBreaker = getConfig().getBoolean("balancedMegaBreaker");
        } catch (Exception e) {
            getConfig().set("balancedMegaBreaker", true);
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
        this.keMenu=new KEMenu();
        this.killEffectManager=new KillEffectManager();
        this.mk2 = new MK2();

        // Register commands
        setExecutor("energy", new EnergyCommand());
        setExecutor("kitlock", new KitLockCommand());
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
        setExecutor("killeffects", new KillEffectCommand());
        setExecutor("pay",new PayCommand());
        setExecutor("walkspeed",new WalkSpeedCommand());
        setExecutor("mk2",new MK2Command());
        setExecutorAndTabCompleter("mwitem", new GetItemCommand());
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
                new MWArcanist(),
                new MWAsn(),
                new MWGoldenDragon(),
                new MWWereWolf(),
                new MWMole(),
                new MWMagician()
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
                this.keMenu,
                new WorldUtils(),
                this.mk2
        );

        // this.restore();
        this.initEnergy();

        ItemUtils.tickMWItems();
        file=new File(getDataFolder(),"swordNames.yml");
        swordNames= YamlConfiguration.loadConfiguration(file);
        try {
            swordNames.save(new File(getDataFolder(),"swordNames.yml"));
        } catch (IOException e) {
            System.out.println("-------------------------------------------------------------");
            System.out.println("UNABLE TO LOAD swordNames from the config.");
            System.out.println("-------------------------------------------------------------");
            e.printStackTrace();
        }
    }

    public void saveSwordNames(){
        try {
            swordNames.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public static FixedMetadataValue getFixedMetadataValue() {
        return new FixedMetadataValue(MegaWalls.getInstance(), true);
    }

    private void initEnergy() {
        energyManager.flash();

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (energyManager.get(p) == 0) {
                energyManager.clear(p);
            }
        });
    }


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


    public void breakDiamond(Player player){
        int i=random.nextInt(6);
        PotionEffectType pet=null;
        switch (i){
            case 0:
                pet=PotionEffectType.JUMP;
                break;
            case 1:
                pet=PotionEffectType.DAMAGE_RESISTANCE;
                break;
            case 2:
                pet=PotionEffectType.SPEED;
                break;
            case 3:
                pet=PotionEffectType.REGENERATION;
                break;
            case 4:
                pet=PotionEffectType.INCREASE_DAMAGE;
                break;
            case 5:
                pet=PotionEffectType.FAST_DIGGING;
                break;
        }
        player.addPotionEffect(new PotionEffect(pet,120*20,1));
        player.sendMessage("You were given "+pet.getName()+" for breaking a diamond ore!");
    }
}



