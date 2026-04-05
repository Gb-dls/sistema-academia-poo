package domain;

// Imports
import java.time.LocalDate;
import java.util.ArrayList;


// Class Enrollment
public class Enrollment {

    private static int enrollmentCounter = 1;
    private int code;
    private Student student;
    private Plan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationMonths;
    private double totalPrice;
    private EnrollmentStatus status;
    private ArrayList<Payment> payments;


    // Enum Enrollment Status ---
    public enum EnrollmentStatus {
        ACTIVE, CANCELLED;
    }

    // Constructor ---
    public Enrollment(Student student, Plan plan) {
        this.code = enrollmentCounter++;
        this.student = student;
        this.plan = plan;
        this.startDate = LocalDate.now();
        this.status = EnrollmentStatus.ACTIVE;
        this.payments = new ArrayList<>();

        // Arrumar esse end dateRever onde buscar depois das outras classes plan e student pronta
        this.durationMonths = 0;
        this.totalPrice = 0;
        this.endDate = 0;
    }

    // ---------------------------------------
    // Getters
    public int getCode() {
        return code;
    }

    public Student getStudent() {
        return student;
    }

    public Plan getPlan() {
        return plan;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    // Implementar os metodos da classe

    // registerPayment(payment: Payment) :void
    // calculateTotalPaid() :double
    // calculateBalance() :double
    // cancel() :void


} // End of Class
