import arc.Core;
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
    private static final String[][] TEAMS = {
        { "[#ffd37f]", "SHARDED" },
        { "[#f25555]", "CRUX" },
        { "[#dadada]", "НАБЛЮДАТЬ" },
    };
    private static final String[][] MENU_OPTIONS = {
        { TEAMS[0][0] + TEAMS[0][1] },
        { TEAMS[1][0] + TEAMS[1][1] },
        { TEAMS[2][0] + TEAMS[2][1] },
    };

    private static String clean(String s) {
        return s.replaceAll("\\[.*?\\]", "");
    }

    private void resetPlayer(Player p) {
        if (p == null || p.con == null) return;
        p.name = clean(p.name);
        p.team(Team.derelict);
        if (p.unit() != null) p.unit().kill();
        float half = Vars.tilesize / 2f;
        float cx = Vars.world.width() * half;
        float cy = Vars.world.height() * half;
        Call.setPosition(p.con, cx, cy);
        Call.setCameraPosition(p.con, cx, cy);
        Call.menu(
            p.con,
            menuId,
            "Выбор команды",
            "Выбери свою команду:",
            MENU_OPTIONS
        );
    }

    @Override
    public void init() {
        menuId = Menus.registerMenu((p, opt) -> {
            if (p == null || p.con == null) return;
            Team t =
                opt == 0 ? Team.sharded : opt == 1 ? Team.crux : Team.derelict;
            p.name = TEAMS[opt][0] + clean(p.name) + "[]";
            p.team(t);
            //Call.sendMessage("+" + t.emoji + " " + p.name); // если кому надо в чатик настрочить
            if (t != Team.derelict) p.checkSpawn();
        });

        // Событие при подключении игрока
        Events.on(EventType.PlayerConnect.class, e -> resetPlayer(e.player));

        // Событие при загрузке новой карты (небольшая задержка для синхронизации)
        Events.on(EventType.WorldLoadEvent.class, e ->
            Time.runTask(15, () -> {
                for (Player p : Groups.player) resetPlayer(p);
            })
        );
    }
}
