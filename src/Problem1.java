import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Problem1 {
    public static void main(String[] args) {
        int numGuests = Integer.parseInt(args[0]);
        Party party = new Party(numGuests);
        party.Throw();
    }
}

class Party {
    Maze maze;
    ArrayList<Guest> guests;
    ArrayList<AtomicBoolean> invites;
    Set<Integer> traversedGuests;
    AtomicBoolean leave;

    Party(int numGuests) {
        this.maze = new Maze();
        this.guests = new ArrayList<>();
        this.invites = new ArrayList<>();
        this.traversedGuests = new HashSet<Integer>();
        this.leave = new AtomicBoolean(false);

        for (int i = 0; i < numGuests; i++) {
            AtomicBoolean invite = new AtomicBoolean(false);
            Guest guest = new Guest(maze, invite, leave);
            guests.add(guest);
            invites.add(invite);
        }
    }

    void Throw() {
        System.out.println("Guests being invited...");
        ExecutorService es = Executors.newCachedThreadPool();
        for (Guest guest : this.guests) {
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        guest.AttendParty();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        System.out.println("Choosing guests...");
        while (traversedGuests.size() < this.guests.size()) {
            // Minotaur chooses a guest
            int chosenGuest = (int) (Math.random() * this.guests.size());
            System.out.printf("Guest %d has been chosen\n", chosenGuest);
            invites.get(chosenGuest).set(true);

            while (invites.get(chosenGuest).get()) {
            }
            traversedGuests.add(chosenGuest);
        }
        System.out.println("All guests have gone through the maze!");
        this.leave.set(true);
        es.shutdown();
    }
}

class Maze {
    Cupcake cupcake;

    Maze() {
        this.cupcake = new Cupcake();
    }

    Cupcake Traverse() throws InterruptedException {
        // Go through maze
        // long traverseTime = (long) Math.random() * 100;
        // Thread.sleep(traverseTime);

        return this.cupcake;
    }
}

class Guest {
    // final int maxPartyTime = 150;
    AtomicBoolean invitedToTraverse;
    AtomicBoolean leave;
    Maze maze;
    int cupcakesEaten = 0;

    Guest(Maze maze, AtomicBoolean invitedToTraverse, AtomicBoolean leave) {
        this.invitedToTraverse = invitedToTraverse;
        this.leave = leave;
        this.maze = maze;
    }

    void AttendParty() throws InterruptedException {
        while (true) {
            // enjoy the party ! :3
            while (!invitedToTraverse.get() && !leave.get()) {
                long partyTime = (long) (20.0 * Math.random());
                // if (partyTime < maxPartyTime) {
                // partyTime *= 1.5;
                // }
                Thread.sleep(partyTime);
            }

            if (leave.get()) {
                return;
            }

            // Get invited to traverse
            Cupcake cupcake = this.maze.Traverse();

            // Found exit!
            considerCupcake(cupcake);

            // Let minotaur know you left
            this.invitedToTraverse.set(false);
        }
    }

    void considerCupcake(Cupcake cupcake) {
        // Guest may request a new cupcake or leave
        if (!cupcake.Exists()) {
            if (Math.random() < .5)
                return;

            cupcake.RequestNew();
        }

        // Guest may eat the cupcake, then leave
        if (Math.random() < .5) {
            cupcake.Eat();
            this.cupcakesEaten++;
        }
    }
}

class Cupcake {
    private Boolean exists;

    Cupcake() {
        this.exists = true;
    }

    Boolean Exists() {
        return this.exists;
    }

    void RequestNew() {
        this.exists = true;
    }

    void Eat() {
        this.exists = false;
    }
}