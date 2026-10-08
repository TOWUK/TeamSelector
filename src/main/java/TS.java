import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType.PlayerConnectionConfirmed;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.ui.Menus;
import mindustry.ui.builder.MenuBuilder;

import static mindustry.ui.builder.UiBuilder.*;

public class TS extends Plugin {
    static final Team[] TM = {Team.sharded, Team.crux, Team.derelict};
    static final String[] TAG = {"[#dadada]", "[#ffd37f][]", "[#f25555][]"};
    static final String[] LBL = {TAG[1] + "Sharded", TAG[2] + "Crux", TAG[0] + "Наблюдать"};
    static final int menuId = Menus.registerMenuBuilder((p, r) -> { for(Team t : TM) if(r.is(t.name)) set(p, t); });

    @Override public void init(){ Events.on(PlayerConnectionConfirmed.class, e -> { set(e.player, Team.derelict); menu(e.player); }); }

    static void set(Player p, Team t){
        p.team(t);
        if(p.unit() != null) p.unit().kill();
        String n = p.name;
        for(String s : TAG) if(n.startsWith(s)) n = n.substring(s.length());
        p.name = TAG[t.id] + n;
    }

    static void menu(Player p){
        Call.setCameraPosition(p.con, Vars.world.unitWidth()/2f, Vars.world.unitHeight()/2f);
        var t = table().add(defaults().pad(8));
        for(int i = 0; i < TM.length; i++) t.add(button(LBL[i]).style("cleart").height(60).width(140).pad(30).clicked(TM[i].name)).row();
        MenuBuilder.of(t).id(menuId).title(null).show(p);
    }
}
