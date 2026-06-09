import arc.Events;
import arc.struct.ObjectIntMap;
import arc.util.CommandHandler;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.ui.Menus;

public class TeamSelector extends Plugin {

    private int menuId;
    private final ObjectIntMap<String> saved = new ObjectIntMap<>();
    private float cx, cy;

    private static final Team[] TEAMS = {
        Team.sharded,
        Team.crux,
        Team.derelict,
    };
    private static final String[][] OPTS = {
        { "[#ffd37f]Sharded", "[#f25555]Crux", "[#dadada]View" },
    };

    @Override
    public void init() {
        menuId = Menus.registerMenu((p, i) -> {
            if (p != null && p.con != null && i >= 0 && i < 3) run(p, i, false);
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            saved.clear();
            cx = (Vars.world.width() * Vars.tilesize) / 2f;
            cy = (Vars.world.height() * Vars.tilesize) / 2f;
            Time.runTask(30f, () -> Groups.player.each(p -> run(p, 2, true)));
        });

        Events.on(EventType.PlayerJoin.class, e -> {
            Player p = e.player;
            if (p == null || p.con == null) return;
            int i = saved.get(p.uuid(), -1);
            run(p, i >= 0 ? i : 2, i < 0);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("ct", "", (a, p) -> {
            if (p != null && p.con != null) Call.menu(
                p.con,
                menuId,
                "",
                "",
                OPTS
            );
        });
    }

    private void run(Player p, int i, boolean showMenu) {
        Team t = TEAMS[i];
        if (p.team() != t) {
            saved.put(p.uuid(), i);
            p.team(t);
            var u = p.unit();
            if (u != null) u.kill();
            if (t != Team.derelict) p.checkSpawn();
            var c = t.core();
            Call.setCameraPosition(
                p.con,
                c != null ? c.x : cx,
                c != null ? c.y : cy
            );
        }
        if (showMenu) Call.menu(p.con, menuId, "", "", OPTS);
    }
}
