import java.io.*;
import java.util.*;

class Expense {
    private String date;
    private String category;
    private double amount;

    public Expense(String date, String category, double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Category: " + category + ", Amount: " + amount;
    }
}

class User {
    private String username;
    private String password;
    private List<Expense> expenses;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.expenses = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void saveExpensesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(username + "_expenses.txt"))) {
            for (Expense expense : expenses) {
                writer.println(expense.getDate() + "," + expense.getCategory() + "," + expense.getAmount());
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    public void loadExpensesFromFile() {
        expenses.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(username + "_expenses.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    expenses.add(new Expense(parts[0], parts[1], Double.parseDouble(parts[2])));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    public double getTotalExpensesByCategory(String category) {
        double total = 0;
        for (Expense expense : expenses) {
            if (expense.getCategory().equalsIgnoreCase(category)) {
                total += expense.getAmount();
            }
        }
        return total;
    }
}

public class ExpenseTracker {
    private static Map<String, User> users = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (users.containsKey(username)) {
            System.out.println("Username already taken. Please try again.");
        } else {
            users.put(username, new User(username, password));
            System.out.println("User registered successfully.");
        }
    }

    private static void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            user.loadExpensesFromFile();
            System.out.println("Login successful.");
            userMenu(user);
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private static void userMenu(User user) {
        while (true) {
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. View Total by Category");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    addExpense(user);
                    break;
                case 2:
                    viewExpenses(user);
                    break;
                case 3:
                    viewTotalByCategory(user);
                    break;
                case 4:
                    user.saveExpensesToFile();
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void addExpense(User user) {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        user.addExpense(new Expense(date, category, amount));
        System.out.println("Expense added successfully.");
    }

    private static void viewExpenses(User user) {
        List<Expense> expenses = user.getExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
        } else {
            for (Expense expense : expenses) {
                System.out.println(expense);
            }
        }
    }

    private static void viewTotalByCategory(User user) {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        double total = user.getTotalExpensesByCategory(category);
        System.out.println("Total expenses for category '" + category + "': " + total);
    }
}
