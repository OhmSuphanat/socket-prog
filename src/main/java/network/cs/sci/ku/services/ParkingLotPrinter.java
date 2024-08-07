package network.cs.sci.ku.services;


public class ParkingLotPrinter{

    public static void print(String section, String parkedCars) {

        System.out.println("    1   2   3   4   5");
        for (char row = 'A'; row <= 'G'; row++) {
            System.out.print(row + "   ");
            for (int col = 1; col <= 5; col++) {
                String element = row + String.valueOf(col);
                if (section.contains(element)) {
                    System.out.print("V   ");
                } else if (parkedCars.contains(element)) {
                    System.out.print("X   ");
                } else {
                    System.out.print("O   ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
