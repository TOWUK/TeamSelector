import arc.Events;
import arc.util.Time;
import arc.util.Timer;
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
        menuId = Menus.registerMenu((p, opt) -> { if(opt >= 0 && opt < 2) setTeam(p, TEAMS[opt], false); });
        Events.on(EventType.PlayEvent.class, e -> Time.runTask(44f, () -> Groups.player.each(p -> setTeam(p, Team.derelict, true))));
        Events.on(EventType.PlayerJoin.class, e -> setTeam(e.player, Team.derelict, true));
        Timer.schedule(this::checkTeams, 1f, 1f);
    }
    private void checkTeams() {
        if(!Vars.state.isGame()) return;
        Groups.player.each(p -> { if(!p.name.startsWith(PREFIXES[idx(p.team())])) setTeam(p, p.team(), false); });
    }
    private void setTeam(Player p, Team team, boolean showMenu) {
        int i = idx(team);
        p.team(TEAMS[i]);
        for(var pre : PREFIXES) if(p.name.startsWith(pre)) { p.name = p.name.substring(pre.length()); break; }
        p.name = PREFIXES[i] + p.name;
        if(p.unit() != null) p.unit().kill();
        if(i == 2 && showMenu && p.con != null) {
            Call.setCameraPosition(p.con, Vars.world.unitWidth() / 2f, Vars.world.unitHeight() / 2f);
            Call.menu(p.con, menuId, null, "Нажмите Esc чтобы\nостаться наблюдателем", OPTS);
        }
    }
    private int idx(Team t) { return t == Team.sharded ? 0 : t == Team.crux ? 1 : 2; }
}
