package com.example.demo;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.example.entity.Department;
import com.example.entity.Employee;
import com.example.util.HibernateUtil;

public class MainApp {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        int ch;
        do {
            System.out.println("\n=== HQL MENU ===");
            System.out.println("1. Insert Sample Data");
            System.out.println("2. Show All Employees");
            System.out.println("3. Update Salary");
            System.out.println("4. Delete Employee");
            System.out.println("5. Sort Employees by Salary");
            System.out.println("6. Aggregate Functions");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            ch = sc.nextInt();

            switch (ch) {
                case 1 : insertSampleData();
                break;
                case 2 : showEmployees();
                break;
                case 3 :updateSalary();
                case 4 : deleteEmployee();
                case 5 : sortEmployees();
                case 6 : aggregate();
                case 0 : System.out.println("Exiting...");
                default : System.out.println("Invalid choice!");
            }
        } while (ch != 0);

        sc.close();
        HibernateUtil.shutdown();
    }

    // 1️⃣ INSERT SAMPLE DATA
    static void insertSampleData() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Department hr = new Department("HR");
        Department it = new Department("IT");

        session.persist(hr);
        session.persist(it);

        session.persist(new Employee("Mahesh", 30000, hr));
        session.persist(new Employee("Sita", 45000, it));
        session.persist(new Employee("Ravi", 50000, it));

        tx.commit();
        session.close();

        System.out.println("Sample data inserted successfully!");
    }

    // 2️⃣ SELECT QUERY
    static void showEmployees() {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<Employee> query =
                session.createQuery("from Employee", Employee.class);

        List<Employee> list = query.list();

        System.out.println("\nID | NAME | SALARY | DEPARTMENT");

        for (Employee e : list) {
            System.out.println(
                    e.getEmpId() + " | " +
                    e.getName() + " | " +
                    e.getSalary() + " | " +
                    e.getDept().getDeptName()
            );
        }

        session.close();
    }

    // 3️⃣ UPDATE – POSITIONAL PARAMETER
    static void updateSalary() {

        System.out.print("Enter Employee ID: ");
        int id = sc.nextInt();

        System.out.print("Enter new salary: ");
        double salary = sc.nextDouble();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Query<?> query = session.createQuery(
                "update Employee e set e.salary = ?1 where e.empId = ?2");
        query.setParameter(1, salary);
        query.setParameter(2, id);

        int count = query.executeUpdate();

        tx.commit();
        session.close();

        System.out.println(count + " record(s) updated!");
    }

    // 4️⃣ DELETE – NAMED PARAMETER
    static void deleteEmployee() {

        System.out.print("Enter Employee ID to delete: ");
        int id = sc.nextInt();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Query<?> query = session.createQuery(
                "delete from Employee e where e.empId = :id");
        query.setParameter("id", id);

        int count = query.executeUpdate();

        tx.commit();
        session.close();

        System.out.println(count + " record(s) deleted!");
    }

    // 5️⃣ SORT – ORDER BY
    static void sortEmployees() {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<Employee> query =
                session.createQuery(
                        "from Employee e order by e.salary desc",
                        Employee.class);

        System.out.println("\n-- Employees Sorted by Salary (DESC) --");

        for (Employee e : query.list()) {
            System.out.println(e.getName() + " → Rs. " + e.getSalary());
        }

        session.close();
    }

    // 6️⃣ AGGREGATE FUNCTIONS
    static void aggregate() {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Long count = session.createQuery(
                "select count(e) from Employee e", Long.class)
                .getSingleResult();

        Double avg = session.createQuery(
                "select avg(e.salary) from Employee e", Double.class)
                .getSingleResult();

        Double sum = session.createQuery(
                "select sum(e.salary) from Employee e", Double.class)
                .getSingleResult();

        System.out.println("\nTotal Employees : " + count);
        System.out.println("Average Salary  : " + avg);
        System.out.println("Total Salary   : " + sum);

        session.close();
    }
}
