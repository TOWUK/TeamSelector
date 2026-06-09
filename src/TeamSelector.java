import arc.Events;
import arc.struct.ObjectIntMap;
import arc.util.CommandHandler;
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
    private final ObjectIntMap<String> savedTeams = new ObjectIntMap<>();
    private float mapCenterX, mapCenterY;

    private static final String[] ICONS = {
        String.valueOf((char) 63356),
        String.valueOf((char) 63357),
        String.valueOf((char) 63358),
    };
    private static final String[] COLORS = {
        "[#ffd37f]",
        "[#f25555]",
        "[#dadada]",
    };
    private static final String[] NAMES = {
        "Расколотые",
        "Агрессоры",
        "Наблюдать",
    };
    private static final Team[] TEAMS = {
        Team.sharded,
        Team.crux,
        Team.derelict,
    };

    private static final String[][] MENU_OPTIONS = {
        { COLORS[0] + ICONS[0] + " " + NAMES[0] },
        { COLORS[1] + ICONS[1] + " " + NAMES[1] },
        { COLORS[2] + ICONS[2] + " " + NAMES[2] },
    };

    private static String cleanName(String name) {
        String clean = Strings.stripColors(name);
        for (String icon : ICONS) clean = clean.replace(icon, "");
        return clean.trim();
    }

    @Override
    public void init() {
        menuId = Menus.registerMenu((p, opt) -> {
            if (
                p != null && p.con != null && opt >= 0 && opt < TEAMS.length
            ) applyTeam(p, opt, true);
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            savedTeams.clear();
            mapCenterX = (Vars.world.width() * Vars.tilesize) / 2f;
            mapCenterY = (Vars.world.height() * Vars.tilesize) / 2f;
            Time.runTask(30f, () ->
                Groups.player.each(
                    p -> p.team() != Team.derelict,
                    p -> applyTeam(p, 2, true)
                )
            );
        });

        Events.on(EventType.PlayerJoin.class, e -> {
            Player p = e.player;
            if (p == null || p.con == null) return;
            int saved = savedTeams.get(p.uuid(), -1);
            applyTeam(p, saved != -1 ? saved : 2, saved == -1);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("ct", "Сменить команду", (args, p) -> {
            if (p != null && p.con != null) showMenu(p);
        });
    }

    private void showMenu(Player p) {
        Call.menu(
            p.con,
            menuId,
            "",
            "[white]    Выбор команды\n[#dadada]Выберите сторону или нажмите Esc",
            MENU_OPTIONS
        );
    }

    private void applyTeam(Player p, int index, boolean showMenu) {
        if (p.team() == TEAMS[index]) return;

        savedTeams.put(p.uuid(), index);
        p.name = COLORS[index] + ICONS[index] + " " + cleanName(p.name);
        p.team(TEAMS[index]);

        if (p.unit() != null) p.unit().kill();

        Call.setCameraPosition(
            p.con,
            TEAMS[index].core() != null ? TEAMS[index].core().x : mapCenterX,
            TEAMS[index].core() != null ? TEAMS[index].core().y : mapCenterY
        );

        if (index != 2) p.checkSpawn();
        else if (showMenu) showMenu(p);
    }
}
