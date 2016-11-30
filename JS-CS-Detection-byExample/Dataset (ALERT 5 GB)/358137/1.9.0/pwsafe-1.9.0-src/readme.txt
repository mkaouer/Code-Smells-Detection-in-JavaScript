This is the first binary release for the 2.0 beta cycle of 
Password Safe.  It is marked as version 1.9.0.  Subsequent 
betas will be 1.9.1, 1.9.2, etc., leading eventually to 
a stable 2.0.

Bug numbers refer to the table at

     http://passwordsafe.sourceforge.net/todo.html
     
     
Fixes for 1.9.0:

*  Fixed bug #2, where a password was not saved if it 
   ended in a nathan (otherwise known as an asterisk).
   
*  Fixed bug #3, allowing selection of an existing 
   database that was not the most recently used one 
   on startup.
   
*  Fixed bug #4, allowing use of databases that end in 
   extensions other than ".DAT".

*  Help file converted to Microsoft HTML help format

*  Fixed bug #8, making PgUp and PgDn work sanely

I've also marked bug #13 ("doesn't work under XP") as 
resolved, since I've heard from quite a few XP users 
that say Password Safe works just dandy on XP.  Also, 
from personal experience and user reports, Password 
Safe works very well on Linux using recent versions of 
Wine.

In addition, a lot of code cleanup has been done, and some 
restructuring to allow for eventual porting to other platforms.

More changes a-coming, but please try out 1.9.0, and report 
on successes and failures via the mailing lists.

What?  Not subscribed to the mailing lists?  You can join 
them at the following URL's:

     http://lists.sourceforge.net/lists/listinfo/passwordsafe-users
     http://lists.sourceforge.net/lists/listinfo/passwordsafe-announce
     http://lists.sourceforge.net/lists/listinfo/passwordsafe-dev

The lists are the right place to report bugs, suggest new features, 
and talk about Password Safe in general.  I can't guarantee answering 
email directed to me personally, though.  (I do my best.)

Jim Russell
<jrussell@users.sourceforge.net>
