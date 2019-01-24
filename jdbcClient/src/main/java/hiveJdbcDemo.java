import java.sql.*;

public class hiveJdbcDemo {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static  Connection conn;
    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        java.util.Properties info = new java.util.Properties();
        info.put("user", "user");
        info.put("password", "pwd");
        //info.put("auth","noSasl");
        conn = DriverManager.getConnection("jdbc:hive2://localhost:9000/default;auth=noSasl", info);
        Statement stmt = conn.createStatement();
        String sql = "SELECT * from person;";
        System.out.println("线程"+Thread.currentThread().getName()+"执行"+sql);
        ResultSet res = stmt.executeQuery(sql);
        System.out.println("线程"+Thread.currentThread().getName()+"执行完毕");
        while (res.next()) {
            System.out.println(res.getString(1));
        }

//        Thread t1 = new MyThread1();
//        Thread t2 = new MyThread2();
//        Thread t3 = new MyThread3();
//        long  start = System.currentTimeMillis();
//        t1.start();
//        t2.start();
//        t3.start();
//        t1.join();
//        t2.join();
//        t3.join();
//        long  end = System.currentTimeMillis();
//        long time =(end-start);
//        System.out.println(time);
    }


    static class MyThread1 extends Thread{
        @Override
        public void run() {
            try {
                Statement stmt = conn.createStatement();
                String sql = "SELECT * from person where name = 'xiaoli';";
                System.out.println("线程"+Thread.currentThread().getName()+"执行"+sql);
                ResultSet res = stmt.executeQuery(sql);
                System.out.println("线程"+Thread.currentThread().getName()+"执行完毕");
                while (res.next()) {
                    System.out.println(Thread.currentThread().getName()+":"+res.getString(1));
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    static class MyThread2 extends Thread{
        @Override
        public void run() {

            try {
                Statement stmt = conn.createStatement();
                String sql = "SELECT * from person where name = 'xiaodong';";
                System.out.println("线程"+Thread.currentThread().getName()+"执行"+sql);
                ResultSet res = stmt.executeQuery(sql);
                System.out.println("线程"+Thread.currentThread().getName()+"执行完毕");
                while (res.next()) {
                    System.out.println(Thread.currentThread().getName()+":"+res.getString(1));
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    static class MyThread3 extends Thread{
        @Override
        public void run() {

            try {
                Statement stmt = conn.createStatement();
                String sql = "SELECT * from person";
                System.out.println("线程"+Thread.currentThread().getName()+"执行"+sql);
                ResultSet res = stmt.executeQuery(sql);
                System.out.println("线程"+Thread.currentThread().getName()+"执行完毕");
                while (res.next()) {
                    System.out.println(Thread.currentThread().getName()+":"+res.getString(1));
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}
