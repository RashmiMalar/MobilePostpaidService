package postpaidService;
import java.sql.*;
import java.sql.Date;

class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/PostpaidApp";
    private static final String USER = "root";
    private static final String PASSWORD = "rashmi2810";

    public static Connection getConnection() throws ClassNotFoundException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    // Customer operations
    public static int insertCustomer(String name, String ph, String addr) throws ClassNotFoundException {
        int generatedId = -1;
        String query = "INSERT INTO Customers (customerName, phNo, address) VALUES (?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, ph);
            ps.setString(3, addr);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error inserting customer: " + e.getMessage());
        }
        return generatedId;
    }


    public static int deleteCustomer(int cid) throws ClassNotFoundException {
        int result = 0;
        String query = "DELETE FROM Customers WHERE customerId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, cid);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }
        return result;
    }

    public static int updateCustomer(int cid, String name, String ph, String addr) throws ClassNotFoundException {
        int result = 0;
        String query = "UPDATE Customers SET customerName = ?, phNo = ?, address = ? WHERE customerId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, ph);
            ps.setString(3, addr);
            ps.setInt(4, cid);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }
        return result;
    }

    public static void viewAllCustomers() throws ClassNotFoundException {
        String query = "SELECT * FROM Customers";
        try (Connection con = getConnection(); 
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.printf("\n%-15s %-15s %-15s %-20s %-10s\n", 
                "Customer ID", "Name", "Phone", "Address", "Plan ID");
            System.out.println("--------------------------------------------------------------------------");

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int id = rs.getInt("customerId");
                String name = rs.getString("customerName");
                String phone = rs.getString("phNo");
                String address = rs.getString("address");
                int pid = rs.getInt("planId");
                
                System.out.printf("%-15d %-15s %-15s %-20s %-10s\n",
                    id, name, phone, address, (pid > -1 ? String.valueOf(pid) : "-"));
            }
            if (!hasResults) {
                System.out.println("No customers found.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing customers: " + e.getMessage());
        }
    }

    public static void viewCustomer(int cid) throws ClassNotFoundException {
        String query = "SELECT * FROM Customers WHERE customerId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, cid);
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasResults = false;
                if (rs.next()) {
                    hasResults = true;
                    
                    System.out.printf("\n%-15s %-15s %-15s %-20s %-10s\n", 
                        "Customer ID", "Name", "Phone", "Address", "Plan ID");
                    System.out.println("--------------------------------------------------------------------------");
                    
                    int id = rs.getInt("customerId");
                    String name = rs.getString("customerName");
                    String phone = rs.getString("phNo");
                    String address = rs.getString("address");
                    int pId = rs.getInt("planId");
                    
                    System.out.printf("%-15d %-15s %-15s %-20s %-10s\n",
                        id, name, phone, address, (pId > -1 ? String.valueOf(pId) : "-"));
                }
                if (!hasResults) {
                    System.out.println("No customer found with ID: " + cid);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing customers: " + e.getMessage());
        }
    }

    public static void viewBillsByCustomerId(int customerId) throws ClassNotFoundException {
        String query = "SELECT * FROM Bill WHERE customerId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            
            boolean hasBills = false;
            if (rs.isBeforeFirst()) { // Only print header if there are bills
                System.out.printf("\n%-10s %-10s %-15s %-15s %-15s\n", 
                    "Bill ID", "Plan ID", "Amount (Rs)", "Status", "Bill Date");
                System.out.println("------------------------------------------------------------------");
            }
            
            while (rs.next()) {
                hasBills = true;
                int billId = rs.getInt("billId");
                int planId = rs.getInt("planId");
                double amount = rs.getDouble("amount");
                String status = rs.getString("status");
                Date billDate = rs.getDate("billDate");
                
                System.out.printf("%-10d %-10d %-15.2f %-15s %-15s\n",
                    billId, planId, amount, status, billDate);
            }
            
            if (!hasBills) {
                System.out.println("\nNo bills generated yet for this customer.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing customer bills: " + e.getMessage());
        }
    }



    // Plan operations
    public static int readPlan() throws ClassNotFoundException {
        int plansFetched = 0;
        String query = "SELECT * FROM Plan";
        try (Connection con = getConnection(); 
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("--- Available Plans ---");
            System.out.printf("\n%-10s %-20s %-12s %-10s %-30s\n",
            	    "Plan ID", "Plan Name", "Rent (Rs)", "Validity", "Description");
            	System.out.println("--------------------------------------------------------------------------------");

            while (rs.next()) {
                Plan pln = new Plan(
                    rs.getString("planName"),
                    rs.getDouble("rent"),
                    rs.getInt("validity"),
                    rs.getString("description")
                );
                // 🔥 EASIEST FIX:
                int pid = rs.getInt("planId");
                pln.setPlanid(pid);
                pln.displayPlan();
                plansFetched++;
            }
            if (plansFetched == 0) {
                System.out.println("No plans found.");
            }
        } catch (SQLException e) {
            System.out.println("Error reading plans: " + e.getMessage());
        }
        return plansFetched;
    }



    public static int addNewPlan(String planName, double rent, int validity, String description) throws ClassNotFoundException {
        int generatedId = -1;
        String query = "INSERT INTO Plan (planName, rent, validity, description) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, planName);
            ps.setDouble(2, rent);
            ps.setInt(3, validity);
            ps.setString(4, description);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error adding new plan: " + e.getMessage());
        }
        return generatedId;
    }


    public static int updatePlan(int planId, String planName, double rent,int validity, String description) throws ClassNotFoundException {
        int result = 0;
        String query = "UPDATE Plan SET planName = ?, rent = ?,validity = ?, description = ? WHERE planId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, planName);
            ps.setDouble(2, rent);
            ps.setInt(3,validity);
            ps.setString(4, description);
            ps.setInt(5, planId);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating plan: " + e.getMessage());
        }
        return result;
    }

    public static int deletePlan(int planId) throws ClassNotFoundException {
        int result = 0;
        String query = "DELETE FROM Plan WHERE planId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, planId);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting plan: " + e.getMessage());
        }
        return result;
    }

    public static int assignPlanToCustomer(int customerId, int planId) throws ClassNotFoundException {
        int result = 0;
        String query = "UPDATE Customers SET planId = ? WHERE customerId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, planId);
            System.out.println(planId+" "+customerId);
            ps.setInt(2, customerId);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error assigning plan to customer: " + e.getMessage());
        }
        System.out.println(result);
        return result;
    }


    // Billing operations
    public static void viewBills() throws ClassNotFoundException {
        String query = "SELECT * FROM Bill";
        try (Connection con = getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            boolean hasResults = false;
            if (rs.isBeforeFirst()) { // Check if ResultSet has any rows
                System.out.printf("\n%-10s %-15s %-10s %-15s %-15s %-15s\n", 
                    "Bill ID", "Customer ID", "Plan ID", "Amount (Rs)", "Status", "Bill Date");
                System.out.println("-------------------------------------------------------------------------------");
            }

            while (rs.next()) {
                hasResults = true;
                int billId = rs.getInt("billId");
                int customerId = rs.getInt("customerId");
                int planId = rs.getInt("planId");
                double amount = rs.getDouble("amount");
                String status = rs.getString("status");
                Date billDate = rs.getDate("billDate");

                System.out.printf("%-10d %-15d %-10d %-15.2f %-15s %-15s\n",
                    billId, customerId, planId, amount, status, billDate);
            }

            if (!hasResults) {
                System.out.println("\nNo bills found.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing bills: " + e.getMessage());
        }
    }

    public static void viewSingleBill(int billId, int customerId) throws ClassNotFoundException {
        String query = "SELECT * FROM Bill WHERE billId = ? AND customerId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, billId);
            ps.setInt(2, customerId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                System.out.printf("\n%-10s %-15s %-10s %-15s %-15s %-15s\n", 
                    "Bill ID", "Customer ID", "Plan ID", "Amount (Rs)", "Status", "Bill Date");
                System.out.println("-------------------------------------------------------------------------------");

                int bid = rs.getInt("billId");
                int cid = rs.getInt("customerId");
                int pid = rs.getInt("planId");
                double amount = rs.getDouble("amount");
                String status = rs.getString("status");
                Date billDate = rs.getDate("billDate");

                System.out.printf("%-10d %-15d %-10d %-15.2f %-15s %-15s\n",
                    bid, cid, pid, amount, status, billDate);
            } else {
                System.out.println("\nNo bill found with given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching single bill: " + e.getMessage());
        }
    }

    


    public static int enterUsage(int customerId, int planId, double amount, String status, Date billDate) throws ClassNotFoundException {
        int generatedId = -1;
        String query = "INSERT INTO Bill (customerId, planId, amount, status, billDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.setInt(2, planId);
            ps.setDouble(3, amount);
            ps.setString(4, status);
            ps.setDate(5, billDate);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error entering usage/bill: " + e.getMessage());
        }
        return generatedId;
    }


    public static int updateStatus(int billId,int customerId) throws ClassNotFoundException {
        int result = 0;
        String query = "UPDATE Bill SET status = ? WHERE customerId = ? and billId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, "Paid");
            ps.setInt(2, customerId);
            ps.setInt(3,billId);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
        }
        return result;
    }
    public static int getCustId() {
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(customerId) FROM Customers")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error generating customer ID: " + e.getMessage());
        }
        return 1; 
    }
    public static int getBillId() {
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(billId) FROM Bill")) {
            if (rs.next()) {
                return rs.getInt(1)+1;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error geting bill ID: " + e.getMessage());
        }
        return 1; 
    }
    public static boolean verifyCustomer(int cid, String phone) throws ClassNotFoundException {
        String query = "SELECT * FROM Customers WHERE customerId = ? AND phNo = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, cid);
            ps.setString(2, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error verifying customer: " + e.getMessage());
            return false;
        }
    }
    public static int getPlanId() {
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(planId) FROM Plan")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error generating plan ID: " + e.getMessage());
        }
        return 1; // Default to 1 if no plans exist or error occur
    }
    public static int getCustomerPlanId(int customerId) throws ClassNotFoundException {
        int planId = -1;
        String query = "SELECT planId FROM Customers WHERE customerId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                planId = rs.getInt("planId");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching customer planId: " + e.getMessage());
        }
        return planId;
    }
    public static boolean isBillUnpaid(int billId, int customerId) throws ClassNotFoundException {
        String query = "SELECT status FROM Bill WHERE billId = ? AND customerId = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, billId);
            ps.setInt(2, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return status.equalsIgnoreCase("unpaid");
            }
        } catch (SQLException e) {
            System.out.println("Error checking bill status: " + e.getMessage());
        }
        return false;
    }



}
