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
    public String getTeamName() {
        return teamName;
    }

    public List<Participant> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public int getSize() {
        return members.size();
    }
