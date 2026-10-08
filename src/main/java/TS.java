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
    static final String[] TAG = {"[#dadada]", "[#ffd37f][]", "[#f25555][]"};
    static final String[] LBL = {TAG[1] + "Sharded", TAG[2] + "Crux", TAG[0] + "Наблюдать"};
    static final int menuId = Menus.registerMenuBuilder((p, r) -> {
        if(r.is("sharded") || r.is("crux")) set(p, r.is("sharded") ? Team.sharded : Team.crux, true);
    });
    @Override public void init(){
        Events.on(PlayerConnectionConfirmed.class, e -> { set(e.player, Team.derelict, true); show(e.player); });
        Events.on(AdminRequestEvent.class, e -> { if(e.action == AdminAction.switchTeam && e.other != null) set(e.other, e.other.team(), true); });
        Events.on(UnitChangeEvent.class, e -> { set(e.player, e.player.team(), false); if(e.unit != null && e.player.con != null) Call.setCameraPosition(e.player.con, e.unit.x, e.unit.y); }); // null-юнит = снятие (стектрейс)
    }
    static void set(Player p, Team t, boolean kill){
        Team team = t.id < TAG.length ? t : Team.derelict;
        p.team(team);
        if(kill && p.unit() != null) p.unit().kill();
        String n = p.name, tag = TAG[team.id];
        if(!n.startsWith(tag)){ for(String s : TAG) if(n.startsWith(s)) n = n.substring(s.length()); p.name = tag + n; }
    }
    static void show(Player p){
        Call.setCameraPosition(p.con, Vars.world.unitWidth()/2f, Vars.world.unitHeight()/2f);
        MenuBuilder.of(table().add(btn(0)).add(btn(1)).row().add(btn(2).colspan(2))).id(menuId).show(p);
    }
    static ButtonBuilder btn(int i){ return button(LBL[i]).style("cleart").height(60).width(140).pad(25).clicked(TM[i].name); }
}
