package nl.saxion.parcomp.asign2;

import java.util.Scanner;

public class ArtWorld {
    // Current understanding: I need to get MULTIPLE visitors going in and out
    // Managing when or how long they stay for must either be decided by a guard or by themselves.

    private final int sizeOfTheClub = 5, nrOfRepresentatives = 4, nrOfVisitors = 12, consecutiveRepresentatives = 3;



    public static void main(String[] args) {
        new ArtWorld().startWorld();

    }
    public void startWorld() {
        ArtFair artFair = new ArtFair(sizeOfTheClub, consecutiveRepresentatives);

        for (int i = 0; i < nrOfVisitors; i++) {
            new Thread(new Visitor(artFair,"Visitor " + i),"Visitor" + i ).start();
        }
        for (int i = 0; i < nrOfRepresentatives; i++) {
            new Thread(new VipVisitor(artFair,"VipVisitor " + i),"VipVisitor" + i ).start();
        }

        System.out.println("Number of active threads from the given thread: " + Thread.activeCount());
        Scanner scan = new Scanner(System.in);
        scan.nextLine();
        System.exit(0);

    }

}
