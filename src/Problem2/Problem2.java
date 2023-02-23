package Problem2;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Problem2 {
    public static void main(String[] args) {
        int numGuests = Integer.parseInt(args[0]);
        System.out.println(numGuests);
        Party party = new Party(numGuests);
        party.Throw();
    }
}

class Party {
    ArrayList<Guest> guests;
    AndersonQueue queue;
    Room vaseRoom;

    public Party(int numGuests) {
        this.guests = new ArrayList<>();
        this.queue = new AndersonQueue(numGuests);
        this.vaseRoom = new Room();

        // to end the program
        int numVisitsPerGuest = Math.max(5, numGuests / 10);
        for (int i = 0; i < numGuests; i++) {
            this.guests.add(new Guest(queue, vaseRoom, numVisitsPerGuest, i));
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
        System.out.println("All guests arrived!");
        es.shutdown();
    }
}

class Guest {
    AndersonQueue queue;
    Room room;
    int id;
    int maxVisits; // just to have program end

    public Guest(AndersonQueue queue, Room room, int maxVisits, int id) {
        this.queue = queue;
        this.room = room;
        this.maxVisits = maxVisits;
        this.id = id;
    }

    void AttendParty() throws InterruptedException {
        // add varience to guest attendence order
        Thread.sleep((long) (Math.random() * 1000));

        for (int curNumVisits = 0; curNumVisits < maxVisits; curNumVisits++) {
            // wait a random amount of time before entering
            // (adds varience, makes concurrent correctness more clear to see)
            Thread.sleep((long) (Math.random() * 500));

            // wait in line
            int slot = queue.getNextSlot();
            System.out.printf("Guest[%d] in position %d in line...\n", id, slot);
            queue.lock(slot);

            // your turn, look at vase
            System.out.printf("Guest[%d] in position %d entered...\n", id, slot);
            room.lookAtVase();

            // leave room, tell next person to go
            System.out.printf("Guest[%d] in position %d left...\n", id, slot);
            queue.unlock(slot);
        }
    }
}

class AndersonQueue {
    int size;
    AtomicInteger next;
    AtomicBoolean[] queue;

    public AndersonQueue(int size) {
        AtomicBoolean[] queue = new AtomicBoolean[size];
        for (int i = 0; i < queue.length; i++) {
            queue[i] = new AtomicBoolean(false);
        }
        queue[0].set(true);

        this.size = size;
        this.queue = queue;
        this.next = new AtomicInteger(0);
    }

    int getNextSlot() {
        return next.getAndIncrement();
    }

    void lock(int slot) {
        while (!queue[slot % this.size].get()) {
            // wait for your turn
        }

        queue[slot % this.size].set(false);
    }

    void unlock(int slot) {
        queue[(slot + 1) % this.size].set(true);
    }
}

class Room {
    void lookAtVase() throws InterruptedException {
        // wow, look at that vase
        Thread.sleep((long) (Math.random() * 500));
    }
}
