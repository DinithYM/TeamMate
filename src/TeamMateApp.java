import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TeamMateApp {
    private static boolean csvLoaded = false;

    private static Login.Role currentRole = Login.Role.NONE;






    private static final Scanner scanner = new Scanner(System.in);

    private static final CsvManager csvManager = new CsvManager();
    private static final PersonalityClassifier classifier = new PersonalityClassifier();

    private static List<Participant> participants = new ArrayList<>();
    private static List<Team> teams = new ArrayList<>();

    public static void main(String[] args) {


        while (currentRole == Login.Role.NONE) {
            currentRole = Login.login(scanner);
            if (currentRole == Login.Role.NONE) {
                System.out.println("Please try again.\n");
            }
        }

        boolean exit = false;

        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();

            if (currentRole == Login.Role.ADMIN) {
                switch (choice) {
                    case "1":
                        loadFromCsv();
                        break;
                    case "2":
                        registerNewParticipant();
                        break;
                    case "3":
                        viewAllParticipants();
                        break;
                    case "4":
                        formTeams();
                        break;
                    case "5":
                        viewAllTeams();
                        break;
                    case "6":
                        saveTeamsToFile();
                        break;
                    case "9":
                        relogin();
                        break;
                    case "0":
                        exit = true;
                        System.out.println("Exiting");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } else {
                switch (choice) {
                    case "1":
                        registerNewParticipant();
                        break;
                    case "9":
                        relogin();
                        break;
                    case "0":
                        exit = true;
                        System.out.println("Exiting");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("          Team Building System           ");
        System.out.println("        Logged in as: " + currentRole);
        System.out.println();

        if (currentRole == Login.Role.ADMIN) {
            System.out.println("1. Load data from CSV");
            System.out.println("2. Register new participant");
            System.out.println("3. View all participants");
            System.out.println("4. Form teams");
            System.out.println("5. View all teams");
            System.out.println("6. Save teams to CSV");
            System.out.println("9. Re-login");
            System.out.println("0. Exit");
        } else { // USER
            System.out.println("1. Register new participant");
            System.out.println("9. Re-login");
            System.out.println("0. Exit");
        }

        System.out.print("Enter your choice: ");
    }



    private static void loadFromCsv() {
        System.out.print("Enter CSV file path (default: participants_sample.csv): ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            path = "participants_sample.csv";
        }


        if (!csvManager.verifyParticipantsCsv(path)) {
            System.out.println("CSV verification failed. Please try again.");
            return;
        }

        try {
            List<Participant> loaded = csvManager.loadParticipants(path);


            participants.addAll(loaded);

            csvLoaded = true;

            System.out.println("Loaded " + loaded.size() + " participants from " + path);
            System.out.println("Total participants in system now: " + participants.size());

        } catch (IOException e) {
            System.out.println("Error loading participants: " + e.getMessage());
        }
    }



    private static void registerNewParticipant() {



        System.out.println("    Register New Participant   ");


        String idNumber;

        while (true) {
            System.out.print("Enter participant ID number: ");
            idNumber = scanner.nextLine().trim();

            if (idNumber.isEmpty()) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            if (!idNumber.matches("\\d+")) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            String fullId = "P" + idNumber;


            boolean exists = false;
            for (Participant existing : participants) {
                if (existing.getId().equalsIgnoreCase(fullId)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                System.out.println("ID already exists. Please try again.");
                continue;
            }


            idNumber = fullId;
            break;
        }

        String id = idNumber;



        String name;
        while (true) {
            System.out.print("Enter name: ");
            name = scanner.nextLine().trim();

            if (!name.isEmpty()) {
                break;
            }

            System.out.println("Please enter a valid name.");
        }



        System.out.println("Select preferred game:");
        System.out.println("  1. Chess");
        System.out.println("  2. FIFA");
        System.out.println("  3. Basketball");
        System.out.println("  4. CS:GO");
        System.out.println("  5. DOTA 2");
        System.out.println("  6. Valorant");

        int gameChoice = readIntWithPrompt("Enter game number (1-6): ", 1, 6);
        String game = mapGameChoice(gameChoice);



        System.out.println("Select preferred role:");
        System.out.println("  1. Attacker");
        System.out.println("  2. Defender");
        System.out.println("  3. Strategist");
        System.out.println("  4. Supporter");
        System.out.println("  5. Coordinator");

        int roleChoice = readIntWithPrompt("Enter role number (1-5): ", 1, 5);
        String role = mapRoleChoice(roleChoice);


        int skill = readIntWithPrompt("Enter skill rating (1-10): ", 1, 10);


        System.out.println("Rate the following from 1 (low) to 5 (high):");
        int total = 0;
        for (int i = 1; i <= 5; i++) {
            int score = readIntWithPrompt("Question " + i + " score (1-5): ", 1, 5);
            total += score;
        }

        Participant p = new Participant(id, name);
        p.setPreferredGame(game);
        p.setPreferredRole(role);
        p.setSkillRating(skill);
        p.setPersonalityScore(total);


        Thread surveyThread = new Thread(() -> {
            classifier.classify(p);
        });
        surveyThread.start();

        try {
            surveyThread.join();
        } catch (InterruptedException e) {
            System.out.println("Survey processing thread interrupted: " + e.getMessage());

        }

        participants.add(p);

        System.out.println("Participant registered successfully: " + p);

    }

    private static void viewAllParticipants() {
        if (participants.isEmpty()) {
            System.out.println("No participants available.");
            return;
        }

        System.out.println("    All Participants    ");
        for (Participant p : participants) {
            System.out.println(p);
        }
    }


    private static void formTeams() {
        if (participants.isEmpty()) {
            System.out.println("Please add data first.");
            return;
        }

        int size = readIntWithPrompt("Enter desired team size : ", 2, 100);

        System.out.println("Forming teams...");

        Thread teamThread = new Thread(() -> {
            TeamBuilder builder = new TeamBuilder(size);
            teams = builder.buildTeams(participants);
        });

        teamThread.start();

        try {
            teamThread.join();
        } catch (InterruptedException e) {
            System.out.println("Team formation thread interrupted: " + e.getMessage());
        }


        System.out.println("Teams formed: " + teams.size());
        for (Team t : teams) {

            int memberCount = t.getSize();
            int totalSkill = t.getTotalSkill();
            double avgSkill = (memberCount > 0)
                    ? (double) totalSkill / memberCount
                    : 0.0;

            System.out.println(
                    t.getTeamName()
                            + " (members=" + memberCount
                            + ", avg skill rating=" + String.format("%.2f", avgSkill)
                            + ")"
            );
        }

    }


    private static void viewAllTeams() {
        if (teams == null || teams.isEmpty()) {
            System.out.println("No teams have been formed yet.");
            return;
        }

        System.out.println("   All Teams   ");
        for (Team team : teams) {
            System.out.println(team.getTeamName() + " (members=" + team.getSize() + ")");
            for (Participant p : team.getMembers()) {
                System.out.println("  - " + p.getId() + " | " + p.getName()
                        + " | Role=" + p.getPreferredRole()
                        + " | Type=" + p.getPersonalityType());
            }
        }
    }


    private static void saveTeamsToFile() {
        if (teams == null || teams.isEmpty()) {
            System.out.println("No teams to save. Please form teams first.");
            return;
        }

        System.out.print("Enter output CSV file name (default: formed_teams.csv): ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            path = "formed_teams.csv";
        }

        try {
            csvManager.saveTeams(path, teams);
            System.out.println("Teams saved to: " + path);
        } catch (IOException e) {
            System.out.println("Error saving teams: " + e.getMessage());
        }
    }



    private static int readIntWithPrompt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Please enter a value between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static String mapRoleChoice(int choice) {
        switch (choice) {
            case 1: return "Attacker";
            case 2: return "Defender";
            case 3: return "Strategist";
            case 4: return "Supporter";
            case 5: return "Coordinator";
            default: return "Unknown";
        }
    }

    private static String mapGameChoice(int choice) {
        switch (choice) {
            case 1: return "Chess";
            case 2: return "FIFA";
            case 3: return "Basketball";
            case 4: return "CS:GO";
            case 5: return "DOTA 2";
            case 6: return "Valorant";
            default: return "Unknown";
        }
    }

    private static void relogin() {
        System.out.println("\n--- Re-login ---");
        currentRole = Login.Role.NONE;

        while (currentRole == Login.Role.NONE) {
            currentRole = Login.login(scanner);
            if (currentRole == Login.Role.NONE) {
                System.out.println("Please try again.\n");
            }
        }
    }



    private static int findHighestId(List<Participant> list) {
        int max = 0;
        for (Participant p : list) {
            try {

                String num = p.getId().replaceAll("[^0-9]", "");
                int value = Integer.parseInt(num);
                if (value > max) {
                    max = value;
                }
            } catch (Exception ignored) {}
        }
        return max;
    }


}
