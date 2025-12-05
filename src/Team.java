import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Team {

    private String teamName;
    private final List<Participant> members = new ArrayList<>();

    public Team(String teamName) {
        this.teamName = teamName;
    }
