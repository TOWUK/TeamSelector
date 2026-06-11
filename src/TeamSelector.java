import arc.Events;
import arc.struct.ObjectMap;
import arc.util.Strings;
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
    private float cx, cy;
    private static final String IS = "" + (char)63356, IC = "" + (char)63357;
    private static final String[] P = {"[#dadada]", "[#ffd37f]" + IS, "[#f25555]" + IC};
    private static final String[][] OPTS = {{P[1] + "Sharded", P[2] + "Crux"}, {P[0] + "Watch the game"}};
    private static final Team[] TEAMS = {Team.sharded, Team.crux, Team.derelict};
    private final ObjectMap<String, String> baseNames = new ObjectMap<>();
    @Override
    public void init() {
        menuId = Menus.registerMenu((p, opt) -> { if (opt >= 0 && opt < 3 && opt != 2) { p.team(TEAMS[opt]); nick(p); } });
        Events.on(EventType.WorldLoadEvent.class, e -> { cx = Vars.world.unitWidth() / 2; cy = Vars.world.unitHeight() / 2; Time.runTask(28, () -> Groups.player.each(this::reset)); });
        Events.on(EventType.PlayerJoin.class, e -> { baseNames.put(e.player.uuid(), Strings.stripColors(e.player.name).replace(IS, "").replace(IC, "").trim()); reset(e.player); });
        Events.on(EventType.PlayerLeave.class, e -> { baseNames.remove(e.player.uuid()); });
    }
    private void reset(Player p) { p.team(Team.derelict); nick(p); Call.setCameraPosition(p.con, cx, cy); Call.menu(p.con, menuId, null, null, OPTS); }
    private void nick(Player p) { p.name = P[p.team().id % P.length] + baseNames.get(p.uuid(), p.name); }
}