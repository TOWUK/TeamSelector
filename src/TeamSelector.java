import arc.Events;
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
    private static final Team[] TEAMS = {Team.sharded, Team.crux, Team.derelict};
    private static final String[] PREFIXES = {"[#ffd37f]\uF77C", "[#f25555]\uF77D", "[#dadada]"};
    private static final String[][] OPTS = {{PREFIXES[0] + "Sharded", PREFIXES[1] + "Crux"}};
    @Override
    public void init() {
        menuId = Menus.registerMenu((p, opt) -> { if (opt == 0 || opt == 1) applyTeam(p, opt); });
        Events.on(EventType.WorldLoadEvent.class, e -> Time.runTask(30f, () -> Groups.player.each(p -> applyTeam(p, 2))));
        Events.on(EventType.PlayerJoin.class, e -> applyTeam(e.player, 2));
    }
    private void applyTeam(Player p, int i) {
        p.team(TEAMS[i]);
        String base = p.name;
        for (var pre : PREFIXES) if (base.startsWith(pre)) { base = base.substring(pre.length()); break; }
        p.name = PREFIXES[i] + base;

        if (p.con != null && i == 2) {
            Call.setCameraPosition(p.con, Vars.world.unitWidth() / 2f, Vars.world.unitHeight() / 2f);
            Call.menu(p.con, menuId, null, "Нажмите Esc чтобы\nостаться наблюдателем", OPTS);
        }
    }
}
