public class Participant extends Person {

    private String preferredGame;
    private String preferredRole;
    private int skillRating;
    private int personalityScoreRaw;
    private int personalityScoreScaled;
    private String personalityType;

    public Participant(String id, String name) {
        super(id, name);
    }

