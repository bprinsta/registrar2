Osita Ighodaro and Ben Musoke-Lubega Assignment 2

I (Osita) worked on the server side of the assignment. I used our
solutions to assignment 1 as bases for the functions that would
return all the course information. I also tried to fix the small
bugs that we had in that first submission so they would not affect
this assignment. I used a mix of echothreadmult and coursesever5 as
the basis for regserver.java. In the file, there are actually three
classes. In the class CourseStuff, I designed an object to hold the
classid of a certain row of course information, and also hold the
basic class data in a string. In RegServerThread, I implement the
functions and handle reading and writing the objects. In RegServer
I handle spawning multiple threads to handle client requests.

Ben Paragraph

We received some help from Jerry when we were brainstorming the best
way to send and receive objects from the client to the server.

We spent roughly 20 hours on this project

This assignment helped greatly with GUI and network programming,
and also increased our proficiency using Github as well! I (Osita)
think that maybe a way to improve may to give guidelines on
modularising the assignment we had already submitted. For example,
I wasn't really sure what to modularise. I think it would help to
have some idea about what would have been good.

Our known bugs are that when you press on a certain class, the 
window that pops up does not update the classid sent to the server,
so the page only displays detailed info for one class.
