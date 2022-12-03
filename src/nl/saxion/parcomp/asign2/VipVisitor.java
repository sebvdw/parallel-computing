package nl.saxion.parcomp.asign2;

public class VipVisitor extends BaseVisitor {
    public VipVisitor(ArtFair club, String name) {
        super(club, name);
    }

    @Override
    public void run() {
        try {
            artFair.visitFair(name, true);
            Thread.sleep(5000);
            artFair.privateExit(name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
