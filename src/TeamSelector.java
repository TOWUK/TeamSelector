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
    private static final Team[] TEAMS = { Team.sharded, Team.crux, Team.derelict };
    private static final String[] PREFIXES = { "[#ffd37f]" + (char)63356, "[#f25555]" + (char)63357, "[#dadada]" };
    private static final String[][] OPTS = { { PREFIXES[0] + "Sharded", PREFIXES[1] + "Crux" } };
    @Override
    public void init() {
        menuId = Menus.registerMenu((p, opt) -> { if (opt == 0 || opt == 1) applyTeam(p, opt); });
        Events.on(EventType.WorldLoadEvent.class, e -> Time.runTask(28f, () -> Groups.player.each(p -> applyTeam(p, 2))));
        Events.on(EventType.PlayerJoin.class, e -> applyTeam(e.player, 2));
    }
    private void applyTeam(Player p, int index) {
        p.team(TEAMS[index]);
        p.name = PREFIXES[index] + p.name.replace(PREFIXES[0], "").replace(PREFIXES[1], "").replace(PREFIXES[2], "");
        if (index == 2) {
            Call.setCameraPosition(p.con, Vars.world.unitWidth() / 2f, Vars.world.unitHeight() / 2f);
            Call.menu(p.con, menuId, null, PREFIXES[2] + "Нажмите Esc чтобы\nостаться наблюдателем", OPTS);
        }
    }
}
