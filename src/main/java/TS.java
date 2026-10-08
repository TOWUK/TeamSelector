import arc.Events;
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
import static mindustry.ui.builder.UiBuilder.*;
public class TS extends Plugin {
    static final Team[] TM = {Team.sharded, Team.crux, Team.derelict};
    static final String[] TAG = {"[#dadada]", "[#ffd37f]*[]", "[#f25555]*[]"}; // индекс = team.id: 0=derelict, 1=sharded, 2=crux
    static final String[] LBL = {TAG[1] + "Sharded", TAG[2] + "Crux", TAG[0] + "Наблюдать"};
    static final int menuId = Menus.registerMenuBuilder((p, r) -> { for(Team t : TM) if(r.is(t.name)) set(p, t, true); });
    @Override public void init(){
        Events.on(PlayerConnectionConfirmed.class, e -> { set(e.player, Team.derelict, true); show(e.player); });
        Events.on(AdminRequestEvent.class, e -> { if(e.action == AdminAction.switchTeam && e.other != null) set(e.other, e.other.team(), true); }); // NetServer:918 до ванили (948)
        Events.on(UnitChangeEvent.class, e -> { if(e.player.con != null){ set(e.player, e.player.team(), false); if(e.unit != null && !e.unit.dead) Call.setCameraPosition(e.player.con, e.unit.x, e.unit.y); } });
    }
    static void set(Player p, Team t, boolean kill){
        p.team(t); if(kill && p.unit() != null) p.unit().kill();
        String tag = TAG[t.id], n = p.name; if(n.startsWith(tag)) return; for(String s : TAG) if(n.startsWith(s)) n = n.substring(s.length()); p.name = tag + n;
    }
    static void show(Player p){
        Call.setCameraPosition(p.con, Vars.world.unitWidth()/2f, Vars.world.unitHeight()/2f);
        var t = table(); for(int i = 0; i < 3; i++){ t.add(btn(i).colspan(i == 2 ? 2 : 1)); if(i == 1) t.row(); }
        MenuBuilder.of(t).id(menuId).title(null).show(p);
    }
    static ButtonBuilder btn(int i){ return button(LBL[i]).style("cleart").height(60).width(140).pad(30).clicked(TM[i].name); }
}
