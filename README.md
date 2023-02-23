# Assignment 2

## Problem 1: Minotaur's Birthday Party

To solve problem 1, I created a Party object that would store each guest and a
list of invites, one for each guest. This invite would be passed into each Guest
object, and used as a means for the guest to communiate with the Minotaur.
When the party is thrown, the minotaur starts choosing guests to invite, setting
their invite to true. Each guest is waiting for their invite to be set to true,
and once set true can perform the traversal through the maze and decide whether
to request the cupcake, or eat it if it's already there. Upon completion, they
sets the invite back to false, communicating to the minotaur that the next guest
can be chosen.

I used an array of communication channels (the invites) so that the minotaur
could communicate exclusively with one guest and each guest could not interact
with each other, effectively emulating an Anderson Queue lock but without
inter-thread communication. I tested exponential backoff lock times (1.5x
increase, 150ms max) for guest checking against a random constant time
(rand(20)ms) against 30 guests and found the random constant time ran much
faster. If scaled much higher or if the maze had a long traverse time, I'd
expect the exponential backoff lock to perform better since the overhead in
extra delay time would outperform constant fast index checking.

### Exponential backoff lock times

`java Problem1 30`
`11.03s user 5.46s system 203% cpu 8.100 total`
`9.52s user 7.54s system 363% cpu 4.698 total`
`12.82s user 6.47s system 226% cpu 8.530 total`

### Random constant lock runtimes

`java Problem1 30`
`0.94s user 0.20s system 138% cpu 0.826 total`
`1.05s user 0.06s system 122% cpu 0.905 total`
`0.96s user 0.08s system 123% cpu 0.844 total`

### How To Run

Run the following commands:

`cd src && javac Problem1.java`

`java Problem1 <number_of_guests>`

## Problem 2

To represent this problem we will assume that each guest is represented by a
thread, and the doors to the vase is represented by a piece of data or variable
that must be read by many threads, one at a time. As such, each option
effectively represents a different kind of Mutex contention solution as shown
in the lecture notes. They are as follows:

1. Basic Spin Lock

   Option 1 is in essence a basic spin lock because all threads (guests) are all
   actively checking the data (crowded around the doors). The lock (minotaur)
   will suffer a lot of contention, causing bottlenecking and slow performance.

2. Basic Spin Lock, a level abstracted

   Option 2 is very similar to option 1, except the data (doors) is stored
   behind another variable (sign) that points to it. This is slightly better
   than option 1, since the data itself is not getting clogged. If a VIP guest
   or the minotaur wanted to enter the room and not be blocked by all the normal
   guests, they could access the doors directly, instead of crowded at the sign.
   The downside to this is that it ultimately suffers the same bottlenecking and
   performance issues as the first option, except at a different data point
   (sign) instead. The sign does imply that maybe users would leave and check
   back again later, turning this into a backoff lock, though I'm assuming it
   doesn't

3. Anderson Queue Lock

   Option 3 can be represented by an Anderson Queue Lock, since threads could be
   queued in a priority line wherein upon the first threads completion (visiting
   the room), it could unlock the next thread's lock (telling the next guest
   upon exit) allowing it to continue the cycle. This option is the most
   performant because it causes no contention on the data, and additionally
   allows threads to more evenly access the data instead of the completely
   unmanaged nature that is having a basic spin lock. The guests should choose
   this strategy as it will prevent crowding and not upset the minotaur, as well
   as offer everyone a chance to see the vase.
