/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003
 *  Heikki Tauriainen <Heikki.Tauriainen@hut.fi>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

#ifndef EXCEPTION_H
#define EXCEPTION_H

#include <config.h>
#include <string>
#include <exception>
#include <iostream>

using namespace std;

/******************************************************************************
 *
 * A base class for the exceptions used in the other modules of the program.
 *
 *****************************************************************************/

class Exception : public exception
{
public:
  Exception(const string& message = "");            /* Constructor. */

  /* default copy constructor */

  virtual ~Exception() throw();                     /* Destructor. */

  /* default assignment operator */

  virtual const char* what() const throw();         /* Returns the exception's
						     * error message.
						     */

  virtual void changeMessage                        /* Changes the error */
    (const string& new_message);                    /* message.          */

private:
  string error_message;                             /* Error message. */
};



/******************************************************************************
 *
 * A class for reporting of a user break.
 *
 *****************************************************************************/

class UserBreakException : public Exception
{
public:
  UserBreakException                                /* Constructor. */
    (const string& message = "user break");

  /* default copy constructor */

  virtual ~UserBreakException() throw();            /* Destructor. */

  UserBreakException&                               /* Assignment operator. */
    operator=(const UserBreakException& e);

  /* `what' inherited from class Exception */

  /* `changeMessage' inherited from class Exception */
};



/******************************************************************************
 *
 * A base class for I/O exceptions.
 *
 *****************************************************************************/

class IOException : public Exception
{
public:
  IOException(const string& message = "");          /* Constructor. */

  /* default copy constructor */

  virtual ~IOException() throw();                   /* Destructor. */

  IOException& operator=(const IOException& e);     /* Assignment operator. */

  /* `what' inherited from class Exception */

  /* `changeMessage' inherited from class Exception */
};



/******************************************************************************
 *
 * An exception class for reporting errors when trying to open a file.
 *
 *****************************************************************************/

class FileOpenException : public IOException
{
public:
  FileOpenException();                              /* Default constructor. */

  FileOpenException(const string& filename);        /* Constructor which
						     * relates the exception's
						     * error message with a
						     * given file name.
						     */

  /* default copy constructor */

  ~FileOpenException() throw();                     /* Destructor. */

  FileOpenException& operator=                      /* Assignment operator. */
    (const FileOpenException& e);

  /* `what' inherited from class IOException */

  /* `changeMessage' inherited from class IOException */
};



/******************************************************************************
 *
 * An exception class for reporting errors when trying to create a file.
 *
 *****************************************************************************/

class FileCreationException : public IOException
{
public:
  FileCreationException();                          /* Default constructor. */

  FileCreationException(const string& filename);    /* Constructor which
						     * relates the exception's
						     * error message with a
						     * given file name.
						     */

  /* default copy constructor */

  ~FileCreationException() throw();                 /* Destructor. */

  FileCreationException& operator=                  /* Assignment operator. */
    (const FileCreationException& e);

  /* `what' inherited from class IOException */

  /* `changeMessage' inherited from class IOException */
};



/******************************************************************************
 *
 * An exception class for reporting errors when reading a file.
 *
 *****************************************************************************/

class FileReadException : public IOException
{
public:
  FileReadException();                              /* Default constructor. */

  FileReadException                                 /* Constructor which    */
    (const string& filename,                        /* relates the          */
     const string& details = "");                   /* exception's error
						     * message with a given
						     * file, including a
						     * possible explanation
						     * for the error in the
						     * message.
						     */

  /* default copy constructor */

  ~FileReadException() throw();                     /* Destructor. */

  FileReadException& operator=                      /* Assignment operator. */
    (const FileReadException& e);

  /* `what' inherited from class IOException */

  /* `changeMessage' inherited from class IOException */
};



/******************************************************************************
 *
 * An exception class for reporting errors when writing to a file.
 *
 *****************************************************************************/

class FileWriteException : public IOException
{
public:
  FileWriteException();                             /* Default constructor. */

  FileWriteException                                /* Constructor which    */
    (const string& filename,                        /* relates the          */
     const string& details = "");                   /* exception's error
						     * message with a given
						     * file, including a
						     * possible explanation
						     * for the error in the
						     * message.
						     */

  /* default copy constructor */

  ~FileWriteException() throw();                    /* Destructor. */

  FileWriteException& operator=                     /* Assignment operator. */
    (const FileWriteException& e);

  /* `what' inherited from class IOException */

  /* `changeMessage' inherited from class IOException */
};



/******************************************************************************
 *
 * An exception class for reporting errors when trying to execute an external
 * program.
 *
 *****************************************************************************/

class ExecFailedException : public IOException
{
public:
  ExecFailedException();                            /* Default constructor. */

  ExecFailedException(const string& filename);      /* Constructor which
						     * relates the exception's
						     * error message with a
						     * given file name.
						     */

  /* default copy constructor */

  ~ExecFailedException() throw();                   /* Destructor. */

  ExecFailedException& operator=                    /* Assignment operator. */
    (const ExecFailedException& e);

  /* `what' inherited from class IOException */

  /* `changeMessage' inherited from class IOException */
};



/******************************************************************************
 *
 * A wrapper class for performing `guarded' input from a regular stream using
 * the >> operator for reading the stream. If the input operation fails, an
 * exception is thrown.
 *
 * (This class is required to make `guarded' input from regular streams
 * possible with (at least) the GNU Standard C++ Library which does not fully
 * support the ANSI C++ standard.)
 *
 *****************************************************************************/

class Exceptional_istream
{
public:
  Exceptional_istream                               /* Constructor. */
    (istream *istr,
     ios::iostate mask = ios::goodbit);

  /* default copy constructor */

  ~Exceptional_istream();                           /* Destructor. */

  /* default assignment operator */

  template<class T>                                 /* Operator for reading */
  Exceptional_istream &operator>>(T &t);            /* from the stream.     */

  operator istream&();                              /* Casts the exception-
						     * aware input stream into
						     * a regular input stream.
						     */

private:
  istream* stream;                                  /* A pointer to the
						     * `regular' input stream
						     * with which the object is
						     * associated.
						     */

  ios::iostate exception_mask;                      /* Bit mask which
						     * determines when to throw
						     * an exception.
						     */
};



/******************************************************************************
 *
 * A wrapper class for performing `guarded' output to a regular stream using
 * the << operator for writing to the stream. If the output operation fails, an
 * exception is thrown.
 *
 * (This class is required to make `guarded' output from regular streams
 * possible with (at least) the GNU Standard C++ Library which does not fully
 * support the ANSI C++ standard.)
 *
 *****************************************************************************/

class Exceptional_ostream
{
public:
  Exceptional_ostream                               /* Constructor. */
    (ostream* ostr,
     ios::iostate mask = ios::goodbit);

  /* default copy constructor */

  ~Exceptional_ostream();                           /* Destructor. */

  /* default assignment operator */

  template<class T>                                 /* Operator for writing */
  Exceptional_ostream& operator<<(const T& t);      /* to the stream.       */

  Exceptional_ostream& flush();                     /* Flushes the stream. */

  operator ostream&();                              /* Casts the exception-
						     * aware output stream into
						     * a regular output stream.
						     */

private:
  ostream* stream;                                  /* A pointer to the
						     * `regular' output stream
						     * with which the object is
						     * associated.
						     */

  ios::iostate exception_mask;                      /* Bit mask which
						     * determines when to throw
						     * an exception.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class Exception.
 *
 *****************************************************************************/

/* ========================================================================= */
inline Exception::Exception(const string& message) :
  error_message(message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Exception. Creates a new exception
 *                object, initializing it with a given error message.
 *
 * Argument:      message  --  An error message. The message defaults to the
 *                             empty string if this argument is not given.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Exception::~Exception() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Exception.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline const char* Exception::what() const throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the Exception's error message.
 *
 * Arguments:     None.
 *
 * Returns:       The error message as a C-style string.
 *
 * ------------------------------------------------------------------------- */
{
  return error_message.c_str();
}

/* ========================================================================= */
inline void Exception::changeMessage(const string& new_message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Changes the Exception's error message.
 *
 * Argument:      new_message  --  A replacement error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  error_message = new_message;
}



/******************************************************************************
 *
 * Inline function definitions for class UserBreakException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline UserBreakException::UserBreakException(const string& message) :
  Exception(message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class UserBreakException. Creates a new
 *                UserBreakException object and initializes it with an error
 *                message.
 *
 * Argument:      message  --  An error message. The message defaults to the
 *                             string `User break.' if no alternative message
 *                             is given.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline UserBreakException::~UserBreakException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class UserBreakException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline UserBreakException& UserBreakException::operator=
  (const UserBreakException& e)
/* ----------------------------------------------------------------------------
 *
 * Descrption:   Assignment operator for class UserBreakException. Assigns the
 *               value of another UserBreakException to `this' one.
 *
 * Argument:     e  --  A reference to a constant UserBreakException.
 *
 * Returns:      A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class IOException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline IOException::IOException(const string& message) :
  Exception(message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class IOException. Creates a new IOException
 *                object and initializes it with an error message.
 *
 * Argument:      message  --  An error message. The message defaults to the
 *                             empty string if the argument is not given.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline IOException::~IOException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class IOException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline IOException& IOException::operator=(const IOException& e)
/* ----------------------------------------------------------------------------
 *
 * Descrption:   Assignment operator for class IOException. Assigns the value
 *               of another IOException to `this' one.
 *
 * Argument:     e  --  A reference to a constant IOException.
 *
 * Returns:      A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class FileOpenException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline FileOpenException::FileOpenException() :
  IOException("file open error")
/* ----------------------------------------------------------------------------
 *
 * Description:   Default constructor for class FileOpenException. Creates a
 *                new FileOpenException object and initializes it with a
 *                generic error message `File open error.'
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileOpenException::FileOpenException(const string& filename) :
  IOException("error opening " + filename)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class FileOpenException. This constructor
 *                relates the exception's error message with a given file name.
 *
 * Argument:      filename  --  A reference to a constant string (the file
 *                              name).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileOpenException::~FileOpenException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class FileOpenException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileOpenException& FileOpenException::operator=
  (const FileOpenException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class FileOpenException. Assigns the
 *                `value' of another FileOpenException to `this' one.
 *
 * Argument:      e  --  A reference to a constant FileOpenException object.
 *
 * Returns:       A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  IOException::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class FileCreationException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline FileCreationException::FileCreationException() :
  IOException("file creation error")
/* ----------------------------------------------------------------------------
 *
 * Description:   Default constructor for class FileCreationException. Creates
 *                a new FileCreationException, initializing the error message
 *                to `File creation error.'
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileCreationException::FileCreationException(const string& filename) :
  IOException("unable to create " + filename)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class FileCreationException. This constructor
 *                relates the exception's error message with a given file name.
 *
 * Argument:      filename  --  A reference to a constant string (the file
 *                              name).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileCreationException::~FileCreationException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class FileCreationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileCreationException& FileCreationException::operator=
  (const FileCreationException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class FileCreationException. Assigns
 *                the `value' of another FileCreationException to `this' one.
 *
 * Argument:      e  --  A reference to a constant FileCreationException.
 *
 * Returns:       A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  IOException::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class FileReadException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline FileReadException::FileReadException() :
  IOException("error reading file")
/* ----------------------------------------------------------------------------
 *
 * Description:   Default constructor for class FileReadException. Creates a
 *                new FileReadException object and initializes it with the
 *                error message `Error reading file.'
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileReadException::FileReadException
  (const string& filename, const string& details) :
  IOException("error reading " + filename
	      + string(details.empty() ? "" : " " + details))
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class FileReadException. This constructor
 *                relates the exception's error message to a given file name.
 *                The error message can also include an explanation for the
 *                error.
 *
 * Arguments:     filename  --  A reference to a constant string (the file
 *                              name).
 *                details   --  Explanation for the error (defaults to the
 *                              empty string if not specified).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileReadException::~FileReadException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class FileReadException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileReadException& FileReadException::operator=
  (const FileReadException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class FileReadException. Assigns the
 *                `value' of another FileReadException to `this' one.
 *
 * Argument:      e  --  A reference to a constant FileReadException.
 *
 * Returns:       A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  IOException::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class FileWriteException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline FileWriteException::FileWriteException() :
  IOException("error writing to file")
/* ----------------------------------------------------------------------------
 *
 * Description:   Default constructor for class FileWriteException. Creates a
 *                new FileWriteException object and initializes it with the
 *                error message `Error writing to file.'
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileWriteException::FileWriteException
  (const string& filename, const string& details) :
  IOException("error writing to " + filename
	      + string(details.empty() ? "" : " " + details))
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class FileWriteException. This constructor
 *                relates the exception's error message to a given file name.
 *                The error message can also include an explanation for the
 *                error.
 *
 * Arguments:     filename  --  A reference to a constant string (the file
 *                              name).
 *                details   --  Explanation for the error (defaults to the
 *                              empty string if not specified).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileWriteException::~FileWriteException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class FileWriteException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FileWriteException& FileWriteException::operator=
  (const FileWriteException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class FileWriteException. Assigns the
 *                `value' of another FileWriteException to `this' one.
 *
 * Argument:      e  --  A reference to a constant FileWriteException.
 *
 * Returns:       A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  IOException::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class ExecFailedException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline ExecFailedException::ExecFailedException() :
  IOException("program execution failed")
/* ----------------------------------------------------------------------------
 *
 * Description:   Default constructor for class ExecFailedException. Creates a
 *                new ExecFailedException object and initializes it with the
 *                error message `Program execution failed.'
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ExecFailedException::ExecFailedException(const string& filename) :
  IOException("execution of `" + filename + "' failed")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ExecFailedException. This constructor
 *                relates the exception's error message to a given file name.
 *
 * Argument:      filename  --  A reference to a constant string (the file
 *                              name).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ExecFailedException::~ExecFailedException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ExecFailedException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ExecFailedException& ExecFailedException::operator=
  (const ExecFailedException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class ExecFailedException. Assigns
 *                the `value' of another ExecFailedException object to `this'
 *                one.
 *
 * Argument:      e  --  A reference to a constant ExecFailedException.
 *
 * Returns:       A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  IOException::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class Exceptional_istream.
 *
 *****************************************************************************/

/* ========================================================================= */
inline Exceptional_istream::Exceptional_istream
  (istream* istr, ios::iostate mask) :
  stream(istr), exception_mask(mask)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Exceptional_istream. Creates a new
 *                object providing `guarded' input from a regular stream
 *                using the >> operator for the input operations.
 *
 * Arguments:     istr  --  A pointer to an object of type istream.
 *                mask  --  A bit mask determining when the Exceptional_istream
 *                          should throw exceptions. The most useful constants
 *                          for the bit mask are
 *                            ios::badbit   Throw an exception if the input
 *                                          stream (after performing an input
 *                                          operation) enters a state in which
 *                                          the call to bad() would return
 *                                          true.
 *                            ios::failbit  Throw an exception if the input
 *                                          stream (after performing an input
 *                                          operation) enters a state in which
 *                                          the call to fail() would return
 *                                          true.
 *                            ios::eofbit   Throw an exception if the input
 *                                          stream (after performing an input
 *                                          operation) enters a state in which
 *                                          the call to eof() would return
 *                                          true.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Exceptional_istream::~Exceptional_istream()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Exceptional_istream.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Exceptional_istream::operator istream&()
/* ----------------------------------------------------------------------------
 *
 * Description:   Casts the exception-aware input stream into a regular input
 *                stream.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the input stream associated with the object.
 *
 * ------------------------------------------------------------------------- */
{
  return *stream;
}



/******************************************************************************
 *
 * Template function definitions for class Exceptional_istream.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class T>
Exceptional_istream& Exceptional_istream::operator>>(T& t)
/* ----------------------------------------------------------------------------
 *
 * Description:   Input operator for reading from a stream. If the read
 *                operation puts the stream into an undesirable state, an
 *                exception will be thrown.
 *
 * Arguments:     t  --  A reference to an object which will store the value
 *                       read.
 *
 * Returns:       A reference to the Exceptional_istream object (to support
 *                chaining of the >> operators).
 *
 * ------------------------------------------------------------------------- */
{
  *stream >> t;
  if (stream->rdstate() & exception_mask)
    throw IOException("error reading from stream");

  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class Exceptional_ostream.
 *
 *****************************************************************************/

/* ========================================================================= */
inline Exceptional_ostream::Exceptional_ostream
  (ostream* ostr, ios::iostate mask) :
  stream(ostr), exception_mask(mask)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Exceptional_ostream. Creates a new
 *                object providing `guarded' output into a regular stream
 *                using the << operator for the output operations.
 *
 * Arguments:     ostr  --  A pointer to an object of type ostream.
 *                mask  --  A bit mask determining when the Exceptional_ostream
 *                          should throw exceptions. The most useful constants
 *                          for the bit mask are
 *                            ios::badbit   Throw an exception if the output
 *                                          stream (after performing an output
 *                                          operation) enters a state in which
 *                                          the call to bad() would return
 *                                          true.
 *                            ios::failbit  Throw an exception if the output
 *                                          stream (after performing an output
 *                                          operation) enters a state in which
 *                                          the call to fail() would return
 *                                          true.
 *                            ios::eofbit   Throw an exception if the output
 *                                          stream (after performing an output
 *                                          operation) enters a state in which
 *                                          the call to eof() would return
 *                                          true.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Exceptional_ostream::~Exceptional_ostream()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Exceptional_ostream.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Exceptional_ostream::operator ostream&()
/* ----------------------------------------------------------------------------
 *
 * Description:   Casts the exception-aware output stream into a regular output
 *                stream.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the input output associated with the object.
 *
 * ------------------------------------------------------------------------- */
{
  return *stream;
}

/* ========================================================================= */
inline Exceptional_ostream& Exceptional_ostream::flush()
/* ----------------------------------------------------------------------------
 *
 * Description:   Flushes an Exceptional_ostream.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the Exceptional_ostream.
 *
 * ------------------------------------------------------------------------- */
{
  stream->flush();
  if (stream->rdstate() & exception_mask)
    throw IOException("error writing to stream");

  return *this;
}



/******************************************************************************
 *
 * Template function definitions for class Exceptional_ostream.
 *
 *****************************************************************************/

/* ========================================================================= */
template<typename T>
Exceptional_ostream& Exceptional_ostream::operator<<(const T& t)
/* ----------------------------------------------------------------------------
 *
 * Description:   Output operator for writing into a stream. If the write
 *                operation puts the stream into an undesirable state, an
 *                exception will be thrown.
 *
 * Arguments:     t  --  A reference to a constant object which is to be
 *                       written into the stream.
 *
 * Returns:       A reference to the Exceptional_ostream object (to support
 *                chaining of the << operators).
 *
 * ------------------------------------------------------------------------- */
{
  *stream << t;
  if (stream->rdstate() & exception_mask)
    throw IOException("error writing to stream");

  return *this;
}



#endif /* !EXCEPTION_H */
