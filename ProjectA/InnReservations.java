/*
   J. Randomgeek
   CSC 365 Project A UI
*/

import java.sql.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.math.*;

// main function. Contains main program loop
public class InnReservations {

    public static Connection conn = null;
    // enter main program loop
    public static void main(String args[]){
            String url ="", user="", pw="";

            try {
                Scanner sc = new Scanner(new File("ServerSettings.txt"));
                url = sc.nextLine();
                user = sc.nextLine();
                pw = sc.nextLine();
            }
            catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(-1);
            }

        sqlConnect(url, user, pw);
        createTables1("rooms");
        createTables1("reservations");


        // eeb: you may want to put various set-up functionality here

        boolean exit = false;
        Scanner input = new Scanner(System.in);

        // clear the screen to freshen up the display
        clearScreen();
        while (!exit) {
            displayMain();

            char option = input.nextLine().toLowerCase().charAt(0);

            switch(option) {
                case 'a':   adminLoop();
                    break;
                case 'o':   ownerLoop();
                    break;
                case 'g':   guestLoop();
                    break;
                case 'q':   exit = true;
                    break;
            }
        }

        input.close();

    }

    public static void sqlConnect(String url, String userName, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("Driver class found and loaded.");
        }
        catch (Exception e) {
            System.out.println("Driver not found... " + e);
        };

        try {
            conn = DriverManager.getConnection(url, userName, password);
        }
        catch(Exception e) {
            System.out.println("Could not open connection");
            System.exit(-1);
        }
        System.out.println("\nConnected!\n");
    }

    public static void createTables(String tableN) {
        try {
            String table = "create table if not exists "+tableN+" like INN."+tableN;
            System.out.println("create statement "+tableN);

            Statement s1 = conn.createStatement();
            System.out.println("createStatement ok");

            s1.executeUpdate(table);
        }
        catch (Exception ee) {
            System.out.println("yolo :"+ee);
        }
    }

    public static void createTables1(String tableN) {
        try {
            String table = "create table "+tableN+" as select * from INN."+tableN;
            System.out.println("create statement1 "+tableN);

            Statement s1 = conn.createStatement();
            System.out.println("createStatement1 ok");

            s1.executeUpdate(table);
        }
        catch (Exception ee) {
            System.out.println("yolo :"+ee);
        }
    }


    // Main UI display
    public static void displayMain() {
        // Clear the screen
        // clearScreen();

        // Display UI
        System.out.println("Welcome. Please choose your role:\n\n"
                + "- (A)dmin\n"
                + "- (O)wner\n"
                + "- (G)uest\n"
                + "- (Q)uit\n");
    }

    // Program loop for admin subsystem
    public static void adminLoop() {
        boolean exit = false;
        Scanner input = new Scanner(System.in);

        while (!exit) {
            displayAdmin();

            String tokens = input.next();
            char option = tokens.charAt(0);
            System.out.println("option chosen: " + option);


            switch(option) {
                case 'v':   System.out.println("displayTable\n");
               				admin.tableDisplay(conn, input);
                            // admin.tableDisplayRoom(conn);
                            // admin.tableDisplayRes(conn);
                            break;
                case 'c':   System.out.println("clearDB\n");
                            admin.clearDB(conn);
                            break;
                case 'l':   System.out.println("loadDB\n");
                            if(admin.rows(conn,"rooms") == 0 || admin.rows(conn,"reservations") == 0){
                                if(admin.rows(conn,"rooms") == 0) {
                                    System.out.println("rooms empty inserting!");
                                    admin.insertInto(conn, "rooms");
                                }
                                if(admin.rows(conn,"reservations") == 0) {
                                    System.out.println("reservations empty inserting!");
                                    admin.insertInto(conn, "reservations");
                                }
                            }   
                            else if(admin.rows(conn,"rooms") == 10 && admin.rows(conn,"reservations") == 600)
                            {
                                System.out.println("database is populated..");
                            }
                            else
                            {
                                System.out.println("Creating tables");
                                createTables1("rooms");
                                createTables1("reservations");
                            }
                            break;
                case 'r':   System.out.println("removeDB\n");
                			admin.deleteDB(conn);
                            break;
                case 'b':   exit = true;
                            break;
            }
        }
    }

   // Program loop for owner subsystem
   public static void ownerLoop() {
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      while (!exit) {
         displayOwner();

         String[] tokens = input.nextLine().toLowerCase().split("\\s");
         char option = tokens[0].charAt(0);
         char dataOpt = 0;

         if (tokens.length == 2)
            dataOpt = tokens[1].charAt(0);

         switch(option) {
            case 'o':   
               clearScreen();
               Owner.occupancy(conn);
               break;
            case 'd':   
               Owner.revenue(conn);
               break;
            case 's':   
               System.out.print("Enter start date: ");
               String d1 = InnReservations.getDate();
               System.out.print("Enter end date: ");
               String d2 = getDate();
               Owner.browseRes(d1, d2, conn);
               break;
            case 'r':   
               Owner.showRooms(conn);
               String in1 = viewRooms();
               Owner.rooms(in1, conn);
                        break;
            case 'b':   exit = true;
                        break;
         }
      }
   }

    // Program loop for guest subsystem
    public static void guestLoop() {
        boolean exit = false;
        Scanner input = new Scanner(System.in);

        while (!exit) {
            displayGuest();

            char option = input.next().toLowerCase().charAt(0);

            switch(option) {
                case 'r':   
                    //guest.tableDisplayRoom(conn);
                    clearScreen();
                    Owner.showRooms(conn);
                    System.out.println("guestloop");
                    guest.RoomsNRates(conn);
                    break;
                case 's':   
                     //checkAvailability(conn, 'TAA')
	             guest.reservationList(conn);
                    break;
                case 't' : 
                    //guest.pricecheck(conn, "AOB", "2018-12-28", "2019-01-03");
                    guest.reservationList(conn);
                    break;
                case 'b':   exit = true;

            }
        }
    }

    // Guest UI display
    public static void displayGuest() {
        // Clear the screen
        // clearScreen();

        // Display UI
        System.out.println("Welcome, Guest.\n\n"
                + "Choose an option:\n"
                + "- (R)ooms - View rooms and rates\n"
                + "- (S)tays - View availability for your stay\n"
                + "- (B)ack - Goes back to main menu\n");
    }

    // Clears the console screen when running interactive
    public static void clearScreen() {
        Console c = System.console();
        if (c != null) {

            // Clear screen for the first time
            System.out.print("\033[H\033[2J");
            System.out.flush();
            //c.writer().print(ESC + "[2J");
            //c.flush();

            // Clear the screen again and place the cursor in the top left
            System.out.print("\033[H\033[1;1H");
            System.out.flush();
            //c.writer().print(ESC + "[1;1H");
            //c.flush();
        }
    }

    // Admin UI display
    public static void displayAdmin() {

        // Clear the screen -- only if it makes sense to do it
        // clearScreen();

        // Display UI
        // add your own information for the state of the database
        System.out.println("Welcome, Admin.\n\n"
                + admin.currentStatusDis(conn)
                + "Choose an option:\n"
                + "- (V)iew [table name] - Displays table contents\n"
                + "- (C)lear - Deletes all table contents\n"
                + "- (L)oad - Loads all table contents\n"
                + "- (R)emove - Removes tables\n"
                + "- (B)ack - Goes back to main menu\n");

    }


    // during the display of a database table you may offer the option
    // to stop the display (since there are many reservations):
    //    System.out.print("Type (q)uit to exit: ");
    //    etc.

    // Owner UI display
    public static void displayOwner() {
        // Clear the screen
        // clearScreen();

        // Display UI
        System.out.println("Welcome, Owner.\n\n"
                + "Choose an option:\n"
                + "- (O)ccupancy - View occupancy of rooms\n"
                + "- (D)ata [(c)ounts|(d)ays|(r)evenue] - View data on "
                + "counts, days, or revenue of each room\n"
                + "- (S)tays - Browse list of reservations\n"
                + "- (R)ooms - View list of rooms\n"
                + "- (B)ack - Goes back to main menu\n");
    }


    // Get a date from input
    public static String getDate() {
        Scanner input = new Scanner(System.in);

        String monthName = input.next();
        int month = monthNum(monthName);
        int day = input.nextInt();
        String date = "'2010-" + month + "-" + day + "'";
        return date;
    }

    // Convert month name to month number
    public static int monthNum(String month) {
        switch (month) {
            case "january": return 1;
            case "february": return 2;
            case "march": return 3;
            case "april": return 4;
            case "may": return 5;
            case "june": return 6;
            case "july": return 7;
            case "august": return 8;
            case "september": return 9;
            case "october": return 10;
            case "november": return 11;
            case "december": return 12;
        }

        return 0;
    }

    // ask how many dates will be entered
    public static int getNumDates() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter number of dates (1 or 2): ");

        int numDates = input.nextInt();
        while (numDates != 1 && numDates != 2) {
            System.out.print("Enter number of dates (1 or 2): ");
            numDates = input.nextInt();
        }
        return numDates;
    }


    // get the room code or a 'q' response to back up the menu
    public static String getRoomCodeOrQ() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter room code for more details "
                + "(or (q)uit to exit): ");
        String roomCode = input.next();
        return roomCode;
    }


    // get the reservation code or a 'q' response to back up the menu
    public static String getReservCodeOrQ() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter reservation code for more details "
                + "(or (q)uit to exit): ");
        String rvCode = input.next();
        return rvCode;
    }


    // Revenue and volume data subsystem -- option to continue or quit
    public static char revenueData() {
        Scanner input = new Scanner(System.in);
        char opt;
        System.out.print("Type (c)ount, (d)ays, or (r)evenue to view "
                + "different table data (or (q)uit to exit): ");
        opt = input.next().toLowerCase().charAt(0);

        return opt;
    }



    // potentially useful for Rooms Viewing Subsystem -- gets option to
    // view room code or reservations room code or exit
    public static String viewRooms() {
        Scanner input = new Scanner(System.in);
        System.out.print("Type (v)iew [room code] or "
                + "(r)eservations [room code], or (q)uit to exit: ");

        char option = input.next().toLowerCase().charAt(0);
        String roomCode = String.valueOf(option);
        if (option != 'q')
            roomCode = roomCode + " '" + input.next() + "'";
        return roomCode;
    }

    // ask user if they wish to quit
    public static char askIfQuit() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter (q)uit to quit: ");
        char go = input.next().toLowerCase().charAt(0);

        return go;
    }


    // ask user if they wish to go back
    public static char askIfGoBack() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter (b)ack to go back: ");
        char go = input.next().toLowerCase().charAt(0);

        return go;
    }


    // potentially useful for check availability subsystem
    public static char availabilityOrGoBack() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter (a)vailability, or "
                + "(b)ack to go back: ");
        char option = input.next().toLowerCase().charAt(0);

        return option;
    }

    // Check availability subsystem:
    // ask if they want to place reservation or renege
    public static char reserveOrGoBack() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter (r)eserve to place a reservation, "
                + "or (b)ack to go back: ");
        char option = input.next().toLowerCase().charAt(0);

        return option;
    }

    // Get the user's first name (for making a reservation)
    public static String getFirstName() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter your first name: ");
        String firstName = "'" + input.next() + "'";
        return firstName;
    }

    // Get the user's last name (for making a reservation)
    public static String getLastName() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter your last name: ");
        String lastName = "'" + input.next() + "'";
        return lastName;
    }

    // Get the number of adults for a reservation
    public static int getNumAdults() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter number of adults: ");
        int numAdults = input.nextInt();
        return numAdults;
    }

    // Get the number of children for a reservation
    public static int getNumChildren() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter number of children: ");
        int numChildren = input.nextInt();
        return numChildren;
    }

    // get discount for a room reservation
    public static String getDiscount() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter discount (AAA or AARP, if applicable): ");
        String dsName = input.nextLine().toUpperCase();

        return dsName;
    }

}
