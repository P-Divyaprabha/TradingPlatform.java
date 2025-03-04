import java.util.*;

class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void updatePrice() {
        this.price += (Math.random() - 0.5) * 10; // Simulates price fluctuation
        if (this.price < 1) this.price = 1; // Prevents negative prices
    }

    @Override
    public String toString() {
        return symbol + " (" + name + ") - $" + String.format("%.2f", price);
    }
}

class Market {
    private Map<String, Stock> stocks = new HashMap<>();

    public Market() {
        stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 150.00));
        stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 2800.00));
        stocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 700.00));
    }

    public void updateStockPrices() {
        for (Stock stock : stocks.values()) {
            stock.updatePrice();
        }
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }

    public void displayStocks() {
        System.out.println("\n--- Available Stocks ---");
        for (Stock stock : stocks.values()) {
            System.out.println(stock);
        }
    }
}

class User {
    private String name;
    private double balance;
    private Map<String, Integer> portfolio = new HashMap<>();

    public User(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public void buyStock(Market market, Scanner scanner) {
        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.next().toUpperCase();
        Stock stock = market.getStock(symbol);

        if (stock == null) {
            System.out.println("Stock not found!");
            return;
        }

        System.out.print("Enter quantity: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid quantity! Please enter a number.");
            scanner.next();
            return;
        }

        int quantity = scanner.nextInt();
        double cost = stock.getPrice() * quantity;

        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
        } else if (balance >= cost) {
            balance -= cost;
            portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + quantity);
            System.out.println("Successfully bought " + quantity + " shares of " + symbol);
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    public void sellStock(Market market, Scanner scanner) {
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.next().toUpperCase();

        if (!portfolio.containsKey(symbol) || portfolio.get(symbol) == 0) {
            System.out.println("You do not own this stock.");
            return;
        }

        System.out.print("Enter quantity: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid quantity! Please enter a number.");
            scanner.next();
            return;
        }

        int quantity = scanner.nextInt();
        if (quantity <= 0 || portfolio.get(symbol) < quantity) {
            System.out.println("Invalid quantity! You own " + portfolio.get(symbol) + " shares.");
            return;
        }

        Stock stock = market.getStock(symbol);
        double earnings = stock.getPrice() * quantity;
        balance += earnings;
        portfolio.put(symbol, portfolio.get(symbol) - quantity);
        if (portfolio.get(symbol) == 0) portfolio.remove(symbol);

        System.out.println("Successfully sold " + quantity + " shares of " + symbol);
    }

    public void displayPortfolio(Market market) {
        System.out.println("\n--- Portfolio ---");
        System.out.println("Balance: $" + String.format("%.2f", balance));
        if (portfolio.isEmpty()) {
            System.out.println("No stocks owned.");
        } else {
            for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
                Stock stock = market.getStock(entry.getKey());
                System.out.println(stock.getSymbol() + " - " + entry.getValue() + " shares @ $" + String.format("%.2f", stock.getPrice()));
            }
        }
    }
}

public class TradingPlatform {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Market market = new Market();

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your starting balance: ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input! Enter a numeric value for balance.");
            scanner.next();
        }
        double balance = scanner.nextDouble();
        User user = new User(name, balance);

        while (true) {
            market.displayStocks();

            System.out.println("\nOptions: 1. Buy 2. Sell 3. Portfolio 4. Update Prices 5. Exit");
            System.out.print("Enter choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid choice! Enter a number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    user.buyStock(market, scanner);
                    break;
                case 2:
                    user.sellStock(market, scanner);
                    break;
                case 3:
                    user.displayPortfolio(market);
                    break;
                case 4:
                    market.updateStockPrices();
                    System.out.println("Stock prices updated!");
                    break;
                case 5:
                    System.out.println("Exiting... Thanks for trading!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
                }
            }
        }
    }
