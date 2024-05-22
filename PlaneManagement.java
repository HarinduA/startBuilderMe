import java.util.Scanner;

public class PlaneManagement {
    private static final int ROWS = 4;
    private static final int[] SEATS_PER_ROW = {14, 12, 12, 14};
    private static final char AVAILABLE_SEAT = 'O';
    private static final char SOLD_SEAT = 'X';
    private static final int[][] seats = new int[ROWS][];

    static {
        for (int i = 0; i < ROWS; i++) {
            seats[i] = new int[SEATS_PER_ROW[i]];
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;
        Ticket[] tickets = new Ticket[ROWS * SEATS_PER_ROW[0]];

        do {
            printMenu();
            System.out.print("Enter your choice: ");
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    buySeat(scanner, tickets);
                    break;
                case 2:
                    cancelSeat(scanner, tickets);
                    break;
                case 3:
                    findFirstAvailable();
                    break;
                case 4:
                    showSeatingPlan();
                    break;
                case 5:
                    print_ticket_info(tickets);
                    break;
                case 6:
                    search_ticket(scanner, tickets);
                    break;
                case 0:
                    System.out.println("Thank you for choosing us.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (option != 0);
    }

    private static void printMenu() {
        System.out.println("\n***********************************************");
        System.out.println("*                 MENU OPTION                 *");
        System.out.println("***********************************************");
        System.out.println("1. Buy a seat");
        System.out.println("2. Cancel a seat");
        System.out.println("3. Find the first available seat");
        System.out.println("4. Show seating plan");
        System.out.println("5. Print tickets information");
        System.out.println("6. Search ticket");
        System.out.println("0. Exit");
        System.out.println("***********************************************");
    }

    private static void buySeat(Scanner scanner, Ticket[] tickets) {
        char row;
        int rowNumber;
        do {
            System.out.print("Enter row letter (A-D): ");
            row = scanner.next().toUpperCase().charAt(0);
            rowNumber = row - 'A';
            if (rowNumber < 0 || rowNumber >= ROWS) {
                System.out.println("Invalid row selection. Please enter a valid row (A-D).");
            }
        } while (rowNumber < 0 || rowNumber >= ROWS);

        int maxSeatNumber = SEATS_PER_ROW[rowNumber];
        int maxAllowedSeatNumber = (row == 'A' || row == 'D') ? 14 : 12;

        // Loop until a valid seat number is entered
        int seatNumber;
        do {
            System.out.print("Enter seat number (1-" + maxSeatNumber + "): ");
            seatNumber = scanner.nextInt() - 1;

            // Check if the seat number is within the valid range
            if (!(isValidSeat(rowNumber, seatNumber) && seatNumber < maxAllowedSeatNumber)) {
                System.out.println("Invalid seat selection. Please try again.");
            }
        } while (!(isValidSeat(rowNumber, seatNumber) && seatNumber < maxAllowedSeatNumber));

        // Proceed with ticket creation after a valid seat number is entered
        Person person = createPerson(scanner);
        if (person != null) {
            double price = calculateTicketPrice(rowNumber, seatNumber);
            Ticket ticket = createTicket(String.valueOf(row), seatNumber + 1, price, person);
            ticket.save();
            tickets[rowNumber * SEATS_PER_ROW[0] + seatNumber] = ticket;
            seats[rowNumber][seatNumber] = 1;
            System.out.println("You have bought your " + row + (seatNumber + 1) + " seat successfully.");
            ticket.printTicketInfo();
        } else {
            System.out.println("Failed to buy a seat. Invalid person information.");
        }
    }

    private static void cancelSeat(Scanner scanner, Ticket[] tickets) {
        char row;
        int rowNumber;
        do {
            System.out.print("Enter row letter (A-D): ");
            row = scanner.next().toUpperCase().charAt(0);
            rowNumber = row - 'A';
            if (rowNumber < 0 || rowNumber >= ROWS) {
                System.out.println("Invalid row selection. Please enter a valid row (A-D).");
            }
        } while (rowNumber < 0 || rowNumber >= ROWS);

        int maxSeatNumber = SEATS_PER_ROW[rowNumber];
        int maxAllowedSeatNumber = (row == 'A' || row == 'D') ? 14 : 12;

        // Loop until a valid seat number is entered
        int seatNumber;
        do {
            System.out.print("Enter seat number (1-" + maxSeatNumber + "): ");
            seatNumber = scanner.nextInt() - 1;

            // Check if the seat number is within the valid range
            if (!(isValidSeat(rowNumber, seatNumber) && seatNumber < maxAllowedSeatNumber)) {
                System.out.println("Invalid seat selection. Please try again.");
            }
        } while (!(isValidSeat(rowNumber, seatNumber) && seatNumber < maxAllowedSeatNumber));

        // Proceed with seat cancellation after a valid seat number is entered
        seats[rowNumber][seatNumber] = 0;
        tickets[rowNumber * SEATS_PER_ROW[0] + seatNumber] = null;
        System.out.println("Seat canceled successfully!");
    }


    private static void findFirstAvailable() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < SEATS_PER_ROW[i]; j++) {
                if (seats[i][j] == 0) {
                    System.out.println("First available seat: Row " + (char) ('A' + i) + ", Seat " + (j + 1));
                    return;
                }
            }
        }
        System.out.println("No available seats.");
    }

    private static void showSeatingPlan() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < SEATS_PER_ROW[i]; j++) {
                System.out.print(seats[i][j] == 0 ? AVAILABLE_SEAT : SOLD_SEAT);
            }
            System.out.println();
        }
    }

    private static void  print_ticket_info(Ticket[] tickets) {
        double totalPrice = 0;
        for (Ticket ticket : tickets) {
            if (ticket != null) {
                ticket.printTicketInfo();
                totalPrice += ticket.getPrice();
            }
        }
        System.out.println("Total Price of Tickets Sold: £" + totalPrice);
    }

    private static void search_ticket(Scanner scanner, Ticket[] tickets) {
        System.out.print("Enter row letter (A-D): ");
        char row = scanner.next().toUpperCase().charAt(0);
        int rowNumber = row - 'A';
        System.out.print("Enter seat number (1-" + SEATS_PER_ROW[rowNumber] + "): ");
        int seatNumber = scanner.nextInt() - 1;

        if (isValidSeat(rowNumber, seatNumber) && tickets[rowNumber * SEATS_PER_ROW[0] + seatNumber] != null) {
            Ticket ticket = tickets[rowNumber * SEATS_PER_ROW[0] + seatNumber];
            ticket.printTicketInfo();
        } else {
            System.out.println("This seat is available.");
        }
    }

    private static boolean isValidSeat(int row, int seat) {
        return row >= 0 && row < ROWS && seat >= 0 && seat < SEATS_PER_ROW[row];
    }

    private static double calculateTicketPrice(int row, int seat) {
        return (row == 0 || row == ROWS - 1) ? 200 : 150;
    }

    private static Person createPerson(Scanner scanner) {
        String name;
        String surname;
        boolean validName;
        do {
            System.out.print("Enter your name: ");
            name = scanner.next();
            validName = isAlphabetic(name);
            if (!validName) {
                System.out.println("Invalid name. Please enter alphabetic characters only.");
            }
        } while (!validName);

        do {
            System.out.print("Enter your surname: ");
            surname = scanner.next();
            validName = isAlphabetic(surname);
            if (!validName) {
                System.out.println("Invalid surname. Please enter alphabetic characters only.");
            }
        } while (!validName);

        String email;
        boolean validEmail = false;
        do {
            System.out.print("Enter your email: ");
            email = scanner.next();
            if (isValidEmail(email)) {
                validEmail = true;
            } else {
                System.out.println("Invalid email format. Please enter a valid email.");
            }
        } while (!validEmail);
        return new Person(name, surname, email);
    }

    private static boolean isAlphabetic(String str) {
        for (char c : str.toCharArray()) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return false;
            }
        }
        return true;
    }


    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false; // Email cannot be null or empty
        }

        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');

        // Check if "@" and "." are present and in valid positions
        if (atIndex == -1 || dotIndex == -1 || atIndex >= dotIndex) {
            return false;
        }

        return true;
    }

    private static Ticket createTicket(String row, int seatNumber, double price, Person person) {
        return new Ticket(row, String.valueOf(seatNumber), price, person);
    }
}

