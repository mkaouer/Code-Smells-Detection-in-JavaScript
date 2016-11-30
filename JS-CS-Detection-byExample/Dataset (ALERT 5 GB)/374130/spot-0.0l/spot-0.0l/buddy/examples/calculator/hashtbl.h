/*************************************************************************
  $Header: /Volumes/CVS/repository/spot/spot/buddy/examples/calculator/hashtbl.h,v 1.2 2003/05/05 13:44:54 aduret Exp $
  FILE:  hashtbl.h
  DESCR: Compiler hashtable
  AUTH:  Jorn Lind
  DATE:  (C) september 1998
*************************************************************************/

#ifndef _HASHTBL_H
#define _HASHTBL_H

#include <string.h>

class hashData
{
public:
   hashData(void) { id=NULL; type=0; def=NULL; }
   hashData(const char *s, int t, void *d) : id(s) { type=t; def=d; }
   const char *id;
   int type;
   void *def;
};


class hashElement
{
public:
   hashData data;
   int first;
   int next;
};


class hashTable
{
public:
   hashTable(void);
   ~hashTable(void);
   void add(hashData &);
   int exists(const char *);
   int lookup(const char *, hashData &) const;
   int remove(const char *);
   void clear(void);
   
private:
   void reallocate_table(void);
   unsigned int hashval(const char *) const;
   hashElement *table;
   int size, freepos;
};


#endif /* _HASHTBL_H */


/* EOF */
