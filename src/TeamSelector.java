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
import java.util.regex.Pattern;

public class TeamSelector extends Plugin {
    private int menuId;
    private static final Team[] TEAMS = {Team.sharded, Team.crux, Team.derelict};
    private static final String[] PREFIXES = {"[#ffd37f]\uF77C", "[#f25555]\uF77D", "[#dadada]"};
    private static final String[][] OPTS = {{PREFIXES[0] + "Sharded", PREFIXES[1] + "Crux"}, {PREFIXES[2] + "НАБЛЮДАТЬ"}};
    private static final Pattern INVISIBLES = Pattern.compile("[\\p{C}\\u00A0\\u2000-\\u200D\\u202F\\u205F\\u3000\\uFEFF\\u3164\\u2800]");
    @Override
    public void init() {
        menuId = Menus.registerMenu((p, opt) -> { if(opt >= 0 && opt < 3) setTeam(p, TEAMS[opt], false); });
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
        String n = INVISIBLES.matcher(p.plainName()).replaceAll("");
        n = n.trim().isEmpty()
            ? "MindCup_" + Integer.toString((p.uuid().hashCode() & 0x7FFFFFFF) % 45360 + 1296, 36)
            : n.substring(n.charAt(0) == '\uF77C' || n.charAt(0) == '\uF77D' ? 1 : 0);
        p.name = PREFIXES[i] + n;
        if(p.unit() != null) p.unit().kill();
        if(i == 2 && showMenu && p.con != null) {
            Call.setCameraPosition(p.con, Vars.world.unitWidth() * 0.5f, Vars.world.unitHeight() * 0.5f);
            Call.menu(p.con, menuId, null, "\nВЫБЕРИ КОМАНДУ\nили нажми ESC/НАБЛЮДАТЬ\nчтобы смотреть игру!\n\n", OPTS);
        }
    }
    private int idx(Team t) { return t == Team.sharded ? 0 : t == Team.crux ? 1 : 2; }
}
