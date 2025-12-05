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

        // --- LOGIN LOOP ---
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
