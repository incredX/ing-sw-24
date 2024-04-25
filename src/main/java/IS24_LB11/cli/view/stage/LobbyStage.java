package IS24_LB11.cli.view.stage;

import IS24_LB11.cli.ViewHub;

import java.util.Arrays;

public class LobbyStage extends Stage {
    private static final String WALLPAPER =
            "      ...                         ..                               \n" +
                    "   xH88\"`~ .x8X                 dF                                 \n" +
                    " :8888   .f\"8888Hf        u.   '88bu.                    uL   ..   \n" +
                    ":8888>  X8L  ^\"\"`   ...ue888b  '*88888bu        .u     .@88b  @88R \n" +
                    "X8888  X888h        888R Y888r   ^\"*8888N    ud8888.  '\"Y888k/\"*P  \n" +
                    "88888  !88888.      888R I888>  beWE \"888L :888'8888.    Y888L     \n" +
                    "88888   %88888      888R I888>  888E  888E d888 '88%\"     8888     \n" +
                    "88888 '> `8888>     888R I888>  888E  888E 8888.+\"        `888N    \n" +
                    "`8888L %  ?888   ! u8888cJ888   888E  888F 8888L       .u./\"888&   \n" +
                    " `8888  `-*\"\"   /   \"*888*P\"   .888N..888  '8888c. .+ d888\" Y888*\" \n" +
                    "   \"888.      :\"      'Y\"       `\"888*\"\"    \"88888%   ` \"Y   Y\"    \n" +
                    "     `\"\"***~\"`                     \"\"         \"YP' ";

    public LobbyStage(ViewHub viewHub) {
        super(viewHub);
        resize();
    }

    @Override
    public void build() {
        drawBorders();
        drawWallpaper();
        updateViewHub();
    }

    @Override
    public void resize() {
        super.resize();
        rebuild();
    }

    private void drawWallpaper() {
        String[] lines = WALLPAPER.split("\n");
        int maxLenght = Arrays.stream(lines).max((s1, s2) -> Integer.compare(s1.length(), s2.length())).get().length();
        int offsetY = Integer.max(0, innerHeight()-lines.length)/2;
        int offsetX = Integer.max(0, innerWidth()-maxLenght)/2;
        int i = 0;
        for (String line: lines) {
            if (i > lastRow()) break;
            fillRow(firstRow()+offsetY+i, offsetX, line);
            i++;
        }
        buildRelativeArea(maxLenght, lines.length, offsetX, offsetY);
    }
}
