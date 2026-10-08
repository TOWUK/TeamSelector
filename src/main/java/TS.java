import arc.Events;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.game.EventType.AdminRequestEvent;
import mindustry.game.EventType.PlayerConnectionConfirmed;
import mindustry.game.EventType.UnitChangeEvent;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.net.Packets.AdminAction;
import mindustry.ui.Menus;
import mindustry.ui.builder.MenuBuilder;
import java.util.regex.Pattern;
import static mindustry.ui.builder.UiBuilder.*;
public class TS extends Plugin {
    static final String[] ICON = {"", "[#ffd37f][]", "[#f25555][]"}, LBL = {"Наблюдать", "Sharded", "Crux"};
    static final Pattern BLANK = Pattern.compile("[\\p{C}\\p{Z}\\u115F\\u1160\\u3164\\u2800\\uFFA0]");
    static final int menuId = Menus.registerMenuBuilder((p, r) -> { for(int i = 0; i < 3; i++) if(r.is(Team.get(i).name)){ set(p, Team.get(i)); if(Team.get(i).core() != null) p.checkSpawn(); } });
    @Override public void init(){
        Vars.netServer.assigner = (player, players) -> Team.derelict;
        Events.on(PlayerConnectionConfirmed.class, e -> { set(e.player, Team.derelict); Call.setCameraPosition(e.player.con, Vars.world.unitWidth()/2f, Vars.world.unitHeight()/2f); MenuBuilder.of(table().add(btn(0).colspan(2)).row().add(btn(1)).add(btn(2))).id(menuId).show(e.player); });
        Events.on(AdminRequestEvent.class, e -> { if(e.action == AdminAction.switchTeam && e.other != null && e.other.unit() != null) e.other.unit().kill(); });
        Events.on(UnitChangeEvent.class, e -> { set(e.player, e.player.team()); if(e.unit != null && e.player.con != null) Call.setCameraPosition(e.player.con, e.unit.x, e.unit.y); });
    }
    static void set(Player p, Team t){
        if(t.id >= ICON.length) t = Team.derelict;
        p.team(t);
        String n = p.name; for(String s : ICON) if(s.length() > 0 && n.startsWith(s)) n = n.substring(s.length());
        if(BLANK.matcher(Strings.stripColors(n)).replaceAll("").isEmpty()) n = "MindCup_" + Integer.toString((p.uuid().hashCode() & 0x7FFFFFFF) % 1632960 + 46656, 36);
        p.name = ICON[t.id] + n;
    }
    static ButtonBuilder btn(int i){ return button(ICON[i] + LBL[i]).style("cleart").height(60).width(140).pad(25).clicked(Team.get(i).name); }
}
