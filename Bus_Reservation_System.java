import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;

public class Run {

    public static void main(String[] args) throws Exception{
        //Declaration block
        PrintWriter transactionWriter;
        PrintWriter reservationWriter;
        File reservationFile = new File("reservationsDetails.txt");
        File transactionFile = new File("drawer.txt");
        Scanner getInput = new Scanner(System.in);
        int userChoice = 1;
        //Working block

        //Create files if they don't exist
        if (!transactionFile.exists())
            transactionWriter = new PrintWriter(transactionFile);
        if (!reservationFile.exists())
            reservationWriter = new PrintWriter(reservationFile);

        while (userChoice != 0){
            mainScreen();
            System.out.print("Your selection: ");
            userChoice = getInput.nextInt();
            getInput.nextLine();
            switch (userChoice){
                case 1:{ //New reservation
                    new_res(getInput);

                    break;
                }
                case 2:{
                    Scanner reader = new Scanner(reservationFile);
                    if (reader.hasNextLine())
                        cancel_res(reservationFile, transactionFile);
                    else
                        System.out.println("No reservation today");
                    reader.close();

                    System.out.println();
                    break;
                }
                case 3:{
                    Scanner reader = new Scanner(reservationFile);
                    if (reader.hasNextLine())
                        mod_res(getInput, transactionFile, reservationFile);
                    else
                        System.out.println("No reservation today");
                    reader.close();

                    System.out.println();
                    break;
                }
                case 4:{
                    Scanner reader = new Scanner(reservationFile);
                    if (reader.hasNextLine())
                        print_res();
                    else
                        System.out.println("No reservation today");
                    reader.close();

                    System.out.println();
                    break;
                }
                case 5:{
                    Scanner reader = new Scanner(reservationFile);
                    if (reader.hasNextLine())
                        search_res(getInput);
                    else
                        System.out.println("No reservation today");
                    reader.close();

                    System.out.println();
                    break;
                }
                case 6:{
                    Scanner reader = new Scanner(reservationFile);
                    if (reader.hasNextLine())
                        drawer();
                    else
                        System.out.println("No reservation today");
                    reader.close();

                    System.out.println();
                    break;
                }
                case 0:{
                    System.out.println();
                    System.out.println("Program terminated");
                    break;
                }
                default:
                    System.out.println("Error! No such command found");
            }
        }

    }

    public static void mainScreen() {
        System.out.println("=====================================================");
        System.out.println("\t\t\t  Bus Reservation System");
        System.out.println("=====================================================");
        System.out.println("1.) New reservation \n" +
                "2.) Cancel reservation \n" +
                "3.) Modify Reservation \n" +
                "4.) View/Print reservation \n" +
                "5.) Search reservation \n" +
                "6.) Drawer \n" +
                "0. Exit");
    }

    public static void new_res(Scanner getInput) throws Exception {
        System.out.println("=====================================================");
        System.out.println("\t\t\t  New Reservation");
        System.out.println("=====================================================");

        //Declaration block
        PrintWriter drawerWriter;
        Scanner drawerReader = new Scanner(new File("drawer.txt"));
        Passenger dude = new Passenger();
        int amount = 0;
        //Working block

        //Get user data
        System.out.println("Key in the passenger name");
        dude.Name = getInput.nextLine();
        System.out.print("CNIC: ");
        dude.CNIC = validateCNIC(getInput.nextLine());
        System.out.print("Destination: ");
        dude.DestinationCity = getCity(getInput);


        //Find the appropriate bus
        getBusNumber(dude);

        //Get drawer contents
        if (drawerReader.hasNextLine())
            amount = drawerReader.nextInt();
        drawerReader.close();

        //Update cash amount
        amount += getFare(dude.DestinationCity);

        //Update drawer
        drawerWriter = new PrintWriter(new File("drawer.txt"));
        drawerWriter.println(amount);
        drawerWriter.close();

        //Display reservation
        System.out.println();
        System.out.println("Ticket for " + dude.DestinationCity + " booked");
        System.out.println();
        System.out.println("Passenger Name: " + dude.Name);
        System.out.println("CNIC: " + dude.CNIC);
        System.out.println("Destination city: " + dude.DestinationCity);
        System.out.println("Bus Number: " + (dude.BusNumber+1));
        System.out.println("Departure at: " + getBusTiming(dude.DestinationCity, dude.BusNumber));
        System.out.println("Departure date: " + dude.BookingDate);
        System.out.println();

        //Increment to actual bus number
        dude.BusNumber++;

        //Store user data
        File reservationFile = new File("reservationsDetails.txt");
        Passenger [] allPassengers = fetchPassengerDatabase();
        PrintWriter reservationWriter = new PrintWriter(reservationFile);

        for (Passenger i : allPassengers){
            reservationWriter.println(i.Name);
            reservationWriter.println(i.CNIC);
            reservationWriter.println(i.DestinationCity);
            reservationWriter.println(i.BusNumber);
            reservationWriter.println(i.BookingDate);
        }

        reservationWriter.println(dude.Name);
        reservationWriter.println(dude.CNIC);
        reservationWriter.println(dude.DestinationCity);
        reservationWriter.println(dude.BusNumber);
        reservationWriter.println(dude.BookingDate);
        reservationWriter.close();

        Thread.sleep(1500);
    }

    public static void cancel_res(File reservationFile, File transactionFile)throws Exception {
        System.out.println("=====================================================");
        System.out.println("\t\t\t  Cancel Reservation");
        System.out.println("=====================================================");

        int amount = 0;
        int recordFound = 0;

        Scanner readDrawer = new Scanner(transactionFile);
        if (readDrawer.hasNextInt())
            amount = readDrawer.nextInt();

        // *** close the scanner object ***
        readDrawer.close();

        //*** You could take the scanner from the main method ***
        Scanner input = new Scanner(System.in);
        System.out.print("Enter CNIC:  ");
        String CNIC = input.nextLine();
        Passenger[] people = fetchPassengerDatabase();

        int total_passengers = NumberOfRecords();
        int index =0;

        for (int i = 0; i<total_passengers; i++ ){
            if (CNIC.equals(people[i].CNIC)){
                recordFound = 1;
                System.out.println("Name: " +people[i].Name);
                System.out.println("CNIC: " +people[i].CNIC);
                System.out.println("Destination City: " +people[i].DestinationCity);
                System.out.println("Bus Number" +people[i].BusNumber);
                System.out.println("Booking Date: " +people[i].BookingDate);

                index = i;}
        }

        if (recordFound == 0){
            System.out.println("Reservation not found");

        }

        else{

            Passenger[] newpeople = new Passenger[people.length-1];

            System.out.print("Are you sure you want to cancel your reservation?\n"
                    +"Enter Y for Yes \n"
                    + "enter N for No:  ");
            String choice = input.next();
            if (choice.equalsIgnoreCase("y")){
                //*** You could parseInt the "timecheck" directly instead of making a new variable ***
                String timecheck = getBusTiming(people[index].DestinationCity, people[index].BusNumber-1);
                int now = timeNow("hm");
                int timecheckval = Integer.parseInt(timecheck);

                if (timecheckval - now >= 15)
                    amount -= getFare(people[index].DestinationCity);


                int k=0;

                for (int j = 0; j<newpeople.length; j++, k++){
                    newpeople[j] = new Passenger();

                    if (j == index){
                        k++;
                    }
                    newpeople[j].Name = people[k].Name;
                    newpeople[j].CNIC = people[k].CNIC;
                    newpeople[j].DestinationCity = people[k].DestinationCity;
                    newpeople[j].BusNumber = people[k].BusNumber;
                    newpeople[j].BookingDate = people[k].BookingDate;
                    //*** This one is well just a orthodox programing practice, take k++ up in the loop condition
                    //k++;
                }

                PrintWriter cancelWriter = new PrintWriter(reservationFile);

                for(int j = 0; j<newpeople.length; j++){
                    cancelWriter.println(newpeople[j].Name);
                    cancelWriter.println(newpeople[j].CNIC);
                    cancelWriter.println(newpeople[j].DestinationCity);
                    cancelWriter.println(newpeople[j].BusNumber);
                    cancelWriter.println(newpeople[j].BookingDate);
                }
                cancelWriter.close();

                PrintWriter canceldrawerwriter = new PrintWriter(transactionFile);
                canceldrawerwriter.println(amount);
                canceldrawerwriter.close();
                Thread.sleep(500);
                System.out.println("Reservation Cancelled");

            }

        }

        Thread.sleep(1500);
    }

    public static void mod_res(Scanner input, File transactionFile, File reservationFile) throws Exception {    // Method for modification for the reservations.

        System.out.println("=====================================================");
        System.out.println("\t\t\t  Reservation Modification");
        System.out.println("\t\t\t The time is " +timeNow("hm") +" hrs.");
        System.out.println("=====================================================");

        // Make a scanner type object for reading the "Transaction File"
        Scanner readDrawer = new Scanner(transactionFile);


        // Initialized the amount to zero.
        int rupees = 0;

        // Checks if entry has been found for the given CNIC.
        boolean entry_found = false;

        // Index for the matched CNIC number in the Passenger[] array.
        int foundAt = -1;

        // Checks if the drawer already has some amount and adds it to the variable "rupees"
        if ( readDrawer.hasNextInt() ) {
            rupees = readDrawer.nextInt();
        }

        System.out.println();
        System.out.print("Please Enter CNIC: ");
        String cnic = input.nextLine();

        // Array "people_array" from the Reservations File
        Passenger[] people_array = fetchPassengerDatabase();

        // Loop for searching whether the person who want to modify his reservation booked or not.
        for ( int i = 0 ; i < people_array.length ; i++ ) {

            // Prints the particulars of booking if found
            if ( cnic.equals( people_array[i].CNIC ) ) {

                System.out.println("Name: " +people_array[i].Name);
                System.out.println("CNIC: " +people_array[i].CNIC);
                System.out.println("Destination: " +people_array[i].DestinationCity);
                System.out.println("Bus no. " +people_array[i].BusNumber);
                entry_found = true;
                foundAt = i;
                break;
            }
        }

        if ( entry_found ) {

            String time_of_bus_str = getBusTiming(people_array[foundAt].DestinationCity, people_array[foundAt].BusNumber - 1);
            int time_of_bus = Integer.parseInt(time_of_bus_str);
            int time_rn = timeNow("hm");

            // Checks if the person is cancelling 15 minutes before so the amount can be refunded back to him
            if ( time_of_bus - time_rn >= 15 ) {
                rupees -= getFare(people_array[foundAt].DestinationCity);
            }

            else { // If the person id cancelling, less than 15 minutes prior to deparutre, this else condition telling that the amount cannot be refunded

                System.out.println("We're sorry! There are less than 15 minutes prior to departure. ");

                System.out.println("We can't refund your amount.");

                System.out.println("Please select your destination now.");
                System.out.println();
            }

            // The modification process.
            System.out.println();
            System.out.print("Please enter your destination: ");

            // Saves the "city'" and the "bus number" into that particular index of array which was reserved for the old booking.
            people_array[foundAt].DestinationCity = getCity(input);
            getBusNumber(people_array[foundAt]);
            // Adds the fareof the new trip to the drawer
            rupees += getFare(people_array[foundAt].DestinationCity);

            // Print the booking details
            System.out.println();
            System.out.println("Ticket for " + people_array[foundAt].DestinationCity + " booked");
            System.out.println();
            System.out.println("Passenger Name: " + people_array[foundAt].Name);
            System.out.println("CNIC: " + people_array[foundAt].CNIC);
            System.out.println("Destination city: " + people_array[foundAt].DestinationCity);
            System.out.println("Bus Number: " + (people_array[foundAt].BusNumber + 1));
            System.out.println("Departure at: " + getBusTiming( people_array[foundAt].DestinationCity, people_array[foundAt].BusNumber));
            System.out.println("Departure date: " + people_array[foundAt].BookingDate);

            // Incrementing the bus number
            people_array[foundAt].BusNumber++;

            // Updating the drawer by creating a new PrintWriter object and saves the "rupees" into the file.
            PrintWriter transactionWriter = new PrintWriter(transactionFile);
            transactionWriter.println(rupees);
            transactionWriter.close();
            // Writing process completed. ( Transaction File )




            //updating reservations database
            PrintWriter reservationWriter = new PrintWriter(reservationFile);

            // This loops updates the Reservation database according to the array.
            for ( Passenger m : people_array ) {
                reservationWriter.println(m.Name);
                reservationWriter.println(m.CNIC);
                reservationWriter.println(m.DestinationCity);
                reservationWriter.println(m.BusNumber);
                reservationWriter.println(m.BookingDate);
            }

            reservationWriter.close();
            // Writing process completed. ( Reservation File )

        }

        else { // If the old boooking isn't found then it asks if the person wants a new reservation to be made.

            System.out.println();
            System.out.println("Sorry, no previous reservation found.");

            System.out.println("Would you like to make a new reservation? ");

            System.out.print("Enter \'Y\' for making new reservation or \'0\' to quit: ");
            String choice = input.nextLine();

            if ( choice.equalsIgnoreCase("y") ) {
                new_res(input); // Invoking the new_res() method for the new reservation.
            }

            else {
                System.out.println();

                System.out.println("Thank you!");
            }

        }

        Thread.sleep(1500);

    } // new_res() method ends here.

    public static void print_res() throws Exception {
        System.out.println("=====================================================");
        System.out.println("\t\t\t  Reservations");
        System.out.println("=====================================================");
        System.out.println();

        int res = NumberOfRecords();
        System.out.println("we have a total of " +res +" reservations so far!");
        System.out.println();

        Passenger[] people = fetchPassengerDatabase();

        int muzf_res = 0; // Muzaffarabad
        int pswr_res = 0; // Peshawar
        int slkt_res = 0; // Sialkot
        int fsld_res = 0; // Faisalabad
        int lhr_res = 0; // Lahore

        for ( int i = 0 ; i < people.length ; i++ ) {

            if ( people[i].DestinationCity.equalsIgnoreCase("Muzaffarabad") )
                muzf_res++;
            else if ( people[i].DestinationCity.equalsIgnoreCase("Peshawar") )
                pswr_res++;
            else if ( people[i].DestinationCity.equalsIgnoreCase("Sialkot") )
                slkt_res++;
            else if ( people[i].DestinationCity.equalsIgnoreCase("Faisalabad") )
                fsld_res++;
            else if ( people[i].DestinationCity.equalsIgnoreCase("Lahore") )
                lhr_res++;
        }

        System.out.println("Reservations to Muzaffarabad: " +muzf_res);
        System.out.println("Reservations to Peshawar: " +pswr_res);
        System.out.println("Reservations to Sialkot: " +slkt_res);
        System.out.println("Reservations to Faisalabad: " +fsld_res);
        System.out.println("Reservations to Lahore: " +lhr_res);
        System.out.println();

        Thread.sleep(1500);

    }

    public static void search_res(Scanner input) throws Exception {

        System.out.println("=====================================================");
        System.out.println("\t\t\t  Search for Reservations");
        System.out.println("=====================================================");
        System.out.println();

        boolean found = false;

        Passenger[] list = fetchPassengerDatabase();
        int foundAt = -1;

        System.out.print("Please enter your CNIC: ");
        String cnic = input.nextLine();
        System.out.println();

        for ( int i = 0 ; i < list.length ; i++ ) {

            if ( cnic.equals(list[i].CNIC ) ) {
                foundAt = i;
                found = true;
                break;
            }
        }

        if ( found ) {

            System.out.println("Your reservation is found.");
            System.out.println();

            System.out.println("Passenger Name: " + list[foundAt].Name);
            System.out.println("CNIC: " + list[foundAt].CNIC);
            System.out.println("Destination city: " + list[foundAt].DestinationCity);
            System.out.println("Bus Number: " + ( list[foundAt].BusNumber));
            System.out.println("Departure at: " + getBusTiming( list[foundAt].DestinationCity, list[foundAt].BusNumber-1));
            System.out.println("Departure date: " + list[foundAt].BookingDate); // *** Added this print statement ***

        }

        else {
            System.out.println("Sorry! Reservation not found.");
            System.out.println();
        }

        Thread.sleep(1500);


    }

    public static void drawer() throws Exception {
        Scanner drawerReader = new Scanner(new File("drawer.txt"));
        System.out.println();
        System.out.println("=====================================================");
        System.out.println("\t\t\tDrawer");
        System.out.println("=====================================================");


        int amount;

        //Show total cash earned
        amount = drawerReader.nextInt();
        System.out.println();
        System.out.println("Total earning for all the cities is: Rs " + amount);
        System.out.println();

        drawerReader.close();

        Thread.sleep(1500);
    }

    public static String validateCNIC(String dude_CNIC) throws Exception{
        //Declaration block
        Scanner getInput = new Scanner(System.in);
        Passenger [] database = fetchPassengerDatabase();
        boolean unique;

        //Working block
        do{
            unique = true;
            for (Passenger i : database){
                if (dude_CNIC.equals(i.CNIC)){
                    unique = false;
                    System.out.print("The booking with this CNIC already exists \nEnter another CNIC: ");
                    dude_CNIC = getInput.nextLine();
                    break;
                }
            }
        }while(!unique);

        return  dude_CNIC;
    }

    public static String getCity(Scanner getInput){
        String city;
        boolean correctCity;
        do{
            correctCity = true;
            city = getInput.nextLine();
            if (!(city.equalsIgnoreCase("muzaffarabad") || city.equalsIgnoreCase("peshawar") ||
                    city.equalsIgnoreCase("sialkot")  || city.equalsIgnoreCase("faisalabad") ||
                    city.equalsIgnoreCase("lahore"))) {
                correctCity = false;
                System.out.println();
                System.out.println("Sorry! We do not travel to this city");
                System.out.println();
                System.out.println("You can travel to the following cities\n" +
                        "\tMuzaffarabad\n\tPesahwar\n\tSialkot\n\tFaisalabad\n\tLahore");
                System.out.println("Key in a correct destination city");
            }
        }while(!correctCity);
        return city;
    }

    public static void getBusNumber(Passenger dude) throws Exception {
        //Declaration block
        final long DATE_TOMORROW = (long)8.64e+7;
        int time = timeNow("h");
        long date = System.currentTimeMillis();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        String dateNow = dateFormat.format(date);
        int i;

        //Working block
        //Decide bus number
        if (dude.DestinationCity.equalsIgnoreCase("muzaffarabad") ) {
            do {
                i = 0;
                if ((time < 5 || time >= 20) && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 10 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 15 && seatAvailability(dude.DestinationCity, dateNow, i)){
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else
                    i++;
                if (time < 20 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else{
                    date += DATE_TOMORROW;
                    dateNow = dateFormat.format(date);
                }

            }while(true);
        }

        else if (dude.DestinationCity.equalsIgnoreCase("peshawar")){
            do {
                i = 0;
                if ((time < 6 || time >= 21) && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 11 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 16 && seatAvailability(dude.DestinationCity, dateNow, i)){
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else
                    i++;
                if (time < 21 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else{
                    date += DATE_TOMORROW;
                    dateNow = dateFormat.format(date);
                }

            }while(true);
        }
        else if (dude.DestinationCity.equalsIgnoreCase("sialkot")){
            do {
                i = 0;
                if ((time < 7 || time >= 22) && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 12 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 17 && seatAvailability(dude.DestinationCity, dateNow, i)){
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else
                    i++;
                if (time < 22 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else{
                    date += DATE_TOMORROW;
                    dateNow = dateFormat.format(date);
                }

            }while(true);
        }
        else if (dude.DestinationCity.equalsIgnoreCase("faisalabad")){
            do {
                i = 0;
                if ((time < 8 || time >= 23) && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 13 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 18 && seatAvailability(dude.DestinationCity, dateNow, i)){
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else
                    i++;
                if (time < 23 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else{
                    date += DATE_TOMORROW;
                    dateNow = dateFormat.format(date);
                }

            }while(true);
        }

        else {
            do {
                i = 0;
                if ((time < 9 || time >= 24) && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 14 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                } else
                    i++;
                if (time < 19 && seatAvailability(dude.DestinationCity, dateNow, i)){
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else
                    i++;
                if (time < 24 && seatAvailability(dude.DestinationCity, dateNow, i)) {
                    dude.BusNumber = i;
                    dude.BookingDate = dateNow;
                    break;
                }
                else{
                    date += DATE_TOMORROW; //Goto next day
                    dateNow = dateFormat.format(date);
                }

            }while(true);
        }
    }

    public static boolean seatAvailability(String city, String date, int bus) throws Exception {
        //Declaration block
        Passenger [] passengers = fetchPassengerDatabase();
        int seats = 0;
        boolean seatAvailable = true;

        //Working block
        for (Passenger i : passengers){
            if (city.equalsIgnoreCase(i.DestinationCity) &&
                    date.equalsIgnoreCase(i.BookingDate) &&
                    bus == i.BusNumber-1){
                seats++;
            }

        }
        if (seats >= 2)
            /*
            seats are "2" for checking otherwise it should be
            if (seats >= 55)
             */
            seatAvailable = false;
        return seatAvailable;
    }

    public static Passenger [] fetchPassengerDatabase() throws  Exception {
        //Working block
        Scanner reservationReader = new Scanner(new File("reservationsDetails.txt"));
        Passenger [] dudes = new Passenger[NumberOfRecords()];
        int i;

        //Working block
        i = 0;
        while (reservationReader.hasNextLine()){
            dudes[i] = new Passenger();
            dudes[i].Name = reservationReader.nextLine();
            dudes[i].CNIC = reservationReader.nextLine();
            dudes[i].DestinationCity = reservationReader.nextLine();
            dudes[i].BusNumber = reservationReader.nextInt();
            reservationReader.nextLine();
            dudes[i].BookingDate = reservationReader.nextLine();
            i++;
        }
        return dudes;

    }

    public static int NumberOfRecords() throws Exception{
        //Declaration block

        //Create Scanner object to read the data from file
        Scanner FileRead = new Scanner(new File("reservationsDetails.txt"));
        int lineCount = 0;

        //Working block

        while(FileRead.hasNext()){ //while not the end of file
            FileRead.nextLine(); //Go to next line
            lineCount++; //Count the line
        }
            /*
              Each passenger has 5 attributes, so number of passengers is obtained
              by dividing the number of lines by 5
            */
        return (lineCount / 5);
    }

    public static String getBusTiming(String city, int busNumber){
        //Declaration block
        BusTimings timings = new BusTimings();

        //Working block
        if (city.equals("Muzaffarabad"))
            return timings.Muzaffarabad[busNumber];
        else if (city.equals("Peshawar"))
            return timings.Peshawar[busNumber];
        else if (city.equals("Sialkot"))
            return timings.Sialkot[busNumber];
        else if (city.equals("Faisalabad"))
            return timings.Faisalabad[busNumber];
        else
            return timings.Lahore[busNumber];
    }

    public static int getFare(String city){
        Fares fare = new Fares();
        if (city.equals("Muzaffarabad"))
            return fare.Muzaffarabad;
        else if (city.equals("Peshawar"))
            return fare.Peshawar;
        else if (city.equals("Sialkot"))
            return  fare.Sialkot;
        else if (city.equals("Faisalabad"))
            return fare.Faisalabad;
        else
            return fare.Lahore;

    }

    public static int timeNow(String timeType){
        int hours = 0;
        long totalMilliseconds = System.currentTimeMillis();
        long totalSeconds = totalMilliseconds / 1000;
        long totalMinutes = totalSeconds / 60;
        long currentMinute = totalMinutes % 60;
        long totalHours = totalMinutes / 60;
        long currentHour = totalHours % 24;

        for (int i = 0; i < 5; i++){
            currentHour++;
            if (currentHour >= 24)
                currentHour = 0;
        }

        if (timeType.equals("h"))
            hours = (int)(currentHour);
        else if (timeType.equals("hm"))
            hours = (int)(((currentHour) * 100) + currentMinute);

        return hours;
    }

}

class Passenger{
    String Name;
    String CNIC;
    String DestinationCity;
    int BusNumber;
    String BookingDate;
}

class BusTimings{
    String [] Muzaffarabad = {"0500", "1000", "1500", "2000"};
    String [] Peshawar = {"0600", "1100", "1600", "2100"};
    String [] Sialkot = {"0700", "1200", "1700", "2200"};
    String [] Faisalabad = {"0800", "1300", "1800", "2300"};
    String [] Lahore = {"0900", "1400", "1900", "2400"};
}

class Fares{
    int Muzaffarabad = 2000;
    int Peshawar = 1350;
    int Sialkot = 1100;
    int Faisalabad = 1350;
    int Lahore = 1500;
}
