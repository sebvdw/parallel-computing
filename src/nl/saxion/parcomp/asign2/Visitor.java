package nl.saxion.parcomp.asign2;

public class Visitor extends BaseVisitor {
    public Visitor(ArtFair club, String name) { super(club, name); }

    @Override
    public void run() {
        try {
            artFair.visitFair(name, false);
            Thread.sleep(5000);
            artFair.publicExit(name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
