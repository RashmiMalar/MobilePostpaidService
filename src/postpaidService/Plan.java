package postpaidService;

class Plan {
    private int planId;
    private String planName;
    private double rent;
    private int validity;
    private String description;

    public Plan(String planName, double rent, int validity, String description) {
        this.planName = planName;
        this.rent = rent >= 0 ? rent : 0;
        this.validity = validity;
        this.description = description;
    }
    public void setPlanid(int pid) {
    	this.planId = pid;
    }


    public void displayPlan() {
        
        System.out.printf("%-10d %-20s %-12.2f %-10d %-30s\n",
            this.planId,
            this.planName,
            this.rent,
            this.validity,
            this.description
        );
    }


    public int modifyPlan(int planId, String planName, double rent, int validity, String description) throws ClassNotFoundException {
        int result = Database.updatePlan(planId, planName, rent, validity, description);
        if (result > 0) {
            System.out.println("Plan updated successfully.");
        } else {
            System.out.println("Failed to update plan.");
        }
        return result;
    }

    public int addNewPlan() throws ClassNotFoundException {
        int result = Database.addNewPlan(this.planName, this.rent, this.validity, this.description);
        if (result > 0) {
            System.out.println("Plan added successfully!");
        } else {
            System.out.println("Failed to add plan.");
        }
        return result;
    }


    public int removePlan(int pid) throws ClassNotFoundException {
        int result = Database.deletePlan(pid);
        if (result > 0) {
            System.out.println("Plan removed successfully.");
        } else {
            System.out.println("Failed to remove plan.");
        }
        return result;
    }

    public int getValidity() {
        return validity;
    }
}