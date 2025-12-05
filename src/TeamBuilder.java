import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TeamBuilder {

    private final int teamSize;
    private final int maxSameGamePerTeam = 2;
    private final int maxThinkersPerTeam = 2;
    private final int desiredDistinctRoles = 3;

    public TeamBuilder(int teamSize) {
        if (teamSize < 2) {
            throw new IllegalArgumentException("Team size must be at least 2.");
        }
        this.teamSize = teamSize;
    }
