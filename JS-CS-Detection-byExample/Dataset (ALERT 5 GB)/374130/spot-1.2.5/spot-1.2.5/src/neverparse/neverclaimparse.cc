// A Bison parser, made by GNU Bison 3.0.2.

// Skeleton implementation for Bison LALR(1) parsers in C++

// Copyright (C) 2002-2013 Free Software Foundation, Inc.

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

// As a special exception, you may create a larger work that contains
// part or all of the Bison parser skeleton and distribute that work
// under terms of your choice, so long as that work isn't itself a
// parser generator using the skeleton or a modified version thereof
// as a parser skeleton.  Alternatively, if you modify or redistribute
// the parser skeleton itself, you may (at your option) remove this
// special exception, which will cause the skeleton and the resulting
// Bison output files to be licensed under the GNU General Public
// License without this special exception.

// This special exception was added by the Free Software Foundation in
// version 2.2 of Bison.

// Take the name prefix into account.
#define yylex   neverclaimyylex

// First part of user declarations.

#line 39 "neverclaimparse.cc" // lalr1.cc:399

# ifndef YY_NULLPTR
#  if defined __cplusplus && 201103L <= __cplusplus
#   define YY_NULLPTR nullptr
#  else
#   define YY_NULLPTR 0
#  endif
# endif

#include "neverclaimparse.hh"

// User implementation prologue.

#line 53 "neverclaimparse.cc" // lalr1.cc:407
// Unqualified %code blocks.
#line 51 "neverclaimparse.yy" // lalr1.cc:408

#include "ltlast/constant.hh"
#include "ltlparse/public.hh"

/* neverclaimparse.hh and parsedecl.hh include each other recursively.
   We must ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;
static bool accept_all_needed = false;
static bool accept_all_seen = false;

#line 68 "neverclaimparse.cc" // lalr1.cc:408


#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> // FIXME: INFRINGES ON USER NAME SPACE.
#   define YY_(msgid) dgettext ("bison-runtime", msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(msgid) msgid
# endif
#endif

#define YYRHSLOC(Rhs, K) ((Rhs)[K].location)
/* YYLLOC_DEFAULT -- Set CURRENT to span from RHS[1] to RHS[N].
   If N is 0, then set CURRENT to the empty location which ends
   the previous symbol: RHS[0] (always defined).  */

# ifndef YYLLOC_DEFAULT
#  define YYLLOC_DEFAULT(Current, Rhs, N)                               \
    do                                                                  \
      if (N)                                                            \
        {                                                               \
          (Current).begin  = YYRHSLOC (Rhs, 1).begin;                   \
          (Current).end    = YYRHSLOC (Rhs, N).end;                     \
        }                                                               \
      else                                                              \
        {                                                               \
          (Current).begin = (Current).end = YYRHSLOC (Rhs, 0).end;      \
        }                                                               \
    while (/*CONSTCOND*/ false)
# endif


// Suppress unused-variable warnings by "using" E.
#define YYUSE(E) ((void) (E))

// Enable debugging if requested.
#if YYDEBUG

// A pseudo ostream that takes yydebug_ into account.
# define YYCDEBUG if (yydebug_) (*yycdebug_)

# define YY_SYMBOL_PRINT(Title, Symbol)         \
  do {                                          \
    if (yydebug_)                               \
    {                                           \
      *yycdebug_ << Title << ' ';               \
      yy_print_ (*yycdebug_, Symbol);           \
      *yycdebug_ << std::endl;                  \
    }                                           \
  } while (false)

# define YY_REDUCE_PRINT(Rule)          \
  do {                                  \
    if (yydebug_)                       \
      yy_reduce_print_ (Rule);          \
  } while (false)

# define YY_STACK_PRINT()               \
  do {                                  \
    if (yydebug_)                       \
      yystack_print_ ();                \
  } while (false)

#else // !YYDEBUG

# define YYCDEBUG if (false) std::cerr
# define YY_SYMBOL_PRINT(Title, Symbol)  YYUSE(Symbol)
# define YY_REDUCE_PRINT(Rule)           static_cast<void>(0)
# define YY_STACK_PRINT()                static_cast<void>(0)

#endif // !YYDEBUG

#define yyerrok         (yyerrstatus_ = 0)
#define yyclearin       (yyempty = true)

#define YYACCEPT        goto yyacceptlab
#define YYABORT         goto yyabortlab
#define YYERROR         goto yyerrorlab
#define YYRECOVERING()  (!!yyerrstatus_)


namespace neverclaimyy {
#line 154 "neverclaimparse.cc" // lalr1.cc:474

  /* Return YYSTR after stripping away unnecessary quotes and
     backslashes, so that it's suitable for yyerror.  The heuristic is
     that double-quoting is unnecessary unless the string contains an
     apostrophe, a comma, or backslash (other than backslash-backslash).
     YYSTR is taken from yytname.  */
  std::string
  parser::yytnamerr_ (const char *yystr)
  {
    if (*yystr == '"')
      {
        std::string yyr = "";
        char const *yyp = yystr;

        for (;;)
          switch (*++yyp)
            {
            case '\'':
            case ',':
              goto do_not_strip_quotes;

            case '\\':
              if (*++yyp != '\\')
                goto do_not_strip_quotes;
              // Fall through.
            default:
              yyr += *yyp;
              break;

            case '"':
              return yyr;
            }
      do_not_strip_quotes: ;
      }

    return yystr;
  }


  /// Build a parser object.
  parser::parser (spot::neverclaim_parse_error_list& error_list_yyarg, spot::ltl::environment& parse_environment_yyarg, spot::tgba_explicit_string*& result_yyarg)
    :
#if YYDEBUG
      yydebug_ (false),
      yycdebug_ (&std::cerr),
#endif
      error_list (error_list_yyarg),
      parse_environment (parse_environment_yyarg),
      result (result_yyarg)
  {}

  parser::~parser ()
  {}


  /*---------------.
  | Symbol types.  |
  `---------------*/

  inline
  parser::syntax_error::syntax_error (const location_type& l, const std::string& m)
    : std::runtime_error (m)
    , location (l)
  {}

  // basic_symbol.
  template <typename Base>
  inline
  parser::basic_symbol<Base>::basic_symbol ()
    : value ()
  {}

  template <typename Base>
  inline
  parser::basic_symbol<Base>::basic_symbol (const basic_symbol& other)
    : Base (other)
    , value ()
    , location (other.location)
  {
    value = other.value;
  }


  template <typename Base>
  inline
  parser::basic_symbol<Base>::basic_symbol (typename Base::kind_type t, const semantic_type& v, const location_type& l)
    : Base (t)
    , value (v)
    , location (l)
  {}


  /// Constructor for valueless symbols.
  template <typename Base>
  inline
  parser::basic_symbol<Base>::basic_symbol (typename Base::kind_type t, const location_type& l)
    : Base (t)
    , value ()
    , location (l)
  {}

  template <typename Base>
  inline
  parser::basic_symbol<Base>::~basic_symbol ()
  {
  }

  template <typename Base>
  inline
  void
  parser::basic_symbol<Base>::move (basic_symbol& s)
  {
    super_type::move(s);
    value = s.value;
    location = s.location;
  }

  // by_type.
  inline
  parser::by_type::by_type ()
     : type (empty)
  {}

  inline
  parser::by_type::by_type (const by_type& other)
    : type (other.type)
  {}

  inline
  parser::by_type::by_type (token_type t)
    : type (yytranslate_ (t))
  {}

  inline
  void
  parser::by_type::move (by_type& that)
  {
    type = that.type;
    that.type = empty;
  }

  inline
  int
  parser::by_type::type_get () const
  {
    return type;
  }


  // by_state.
  inline
  parser::by_state::by_state ()
    : state (empty)
  {}

  inline
  parser::by_state::by_state (const by_state& other)
    : state (other.state)
  {}

  inline
  void
  parser::by_state::move (by_state& that)
  {
    state = that.state;
    that.state = empty;
  }

  inline
  parser::by_state::by_state (state_type s)
    : state (s)
  {}

  inline
  parser::symbol_number_type
  parser::by_state::type_get () const
  {
    return state == empty ? 0 : yystos_[state];
  }

  inline
  parser::stack_symbol_type::stack_symbol_type ()
  {}


  inline
  parser::stack_symbol_type::stack_symbol_type (state_type s, symbol_type& that)
    : super_type (s, that.location)
  {
    value = that.value;
    // that is emptied.
    that.type = empty;
  }

  inline
  parser::stack_symbol_type&
  parser::stack_symbol_type::operator= (const stack_symbol_type& that)
  {
    state = that.state;
    value = that.value;
    location = that.location;
    return *this;
  }


  template <typename Base>
  inline
  void
  parser::yy_destroy_ (const char* yymsg, basic_symbol<Base>& yysym) const
  {
    if (yymsg)
      YY_SYMBOL_PRINT (yymsg, yysym);

    // User destructor.
    switch (yysym.type_get ())
    {
            case 14: // "boolean formula"

#line 83 "neverclaimparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 375 "neverclaimparse.cc" // lalr1.cc:599
        break;

      case 15: // "identifier"

#line 83 "neverclaimparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 382 "neverclaimparse.cc" // lalr1.cc:599
        break;

      case 23: // ident_list

#line 83 "neverclaimparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 389 "neverclaimparse.cc" // lalr1.cc:599
        break;

      case 24: // transition_block

#line 85 "neverclaimparse.yy" // lalr1.cc:599
        {
  for (std::list<pair>::iterator i = (yysym.value.list)->begin();
       i != (yysym.value.list)->end(); ++i)
  {
    i->first->destroy();
    delete i->second;
  }
  delete (yysym.value.list);
  }
#line 404 "neverclaimparse.cc" // lalr1.cc:599
        break;

      case 26: // transitions

#line 85 "neverclaimparse.yy" // lalr1.cc:599
        {
  for (std::list<pair>::iterator i = (yysym.value.list)->begin();
       i != (yysym.value.list)->end(); ++i)
  {
    i->first->destroy();
    delete i->second;
  }
  delete (yysym.value.list);
  }
#line 419 "neverclaimparse.cc" // lalr1.cc:599
        break;

      case 27: // formula

#line 83 "neverclaimparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 426 "neverclaimparse.cc" // lalr1.cc:599
        break;

      case 28: // opt_dest

#line 83 "neverclaimparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 433 "neverclaimparse.cc" // lalr1.cc:599
        break;

      case 29: // src_dest

#line 84 "neverclaimparse.yy" // lalr1.cc:599
        { (yysym.value.p)->first->destroy(); delete (yysym.value.p)->second; delete (yysym.value.p); }
#line 440 "neverclaimparse.cc" // lalr1.cc:599
        break;

      case 30: // transition

#line 84 "neverclaimparse.yy" // lalr1.cc:599
        { (yysym.value.p)->first->destroy(); delete (yysym.value.p)->second; delete (yysym.value.p); }
#line 447 "neverclaimparse.cc" // lalr1.cc:599
        break;


      default:
        break;
    }
  }

#if YYDEBUG
  template <typename Base>
  void
  parser::yy_print_ (std::ostream& yyo,
                                     const basic_symbol<Base>& yysym) const
  {
    std::ostream& yyoutput = yyo;
    YYUSE (yyoutput);
    symbol_number_type yytype = yysym.type_get ();
    yyo << (yytype < yyntokens_ ? "token" : "nterm")
        << ' ' << yytname_[yytype] << " ("
        << yysym.location << ": ";
    switch (yytype)
    {
            case 14: // "boolean formula"

#line 94 "neverclaimparse.yy" // lalr1.cc:617
        {
    if ((yysym.value.str))
      debug_stream() << *(yysym.value.str);
    else
      debug_stream() << "\"\""; }
#line 478 "neverclaimparse.cc" // lalr1.cc:617
        break;

      case 15: // "identifier"

#line 94 "neverclaimparse.yy" // lalr1.cc:617
        {
    if ((yysym.value.str))
      debug_stream() << *(yysym.value.str);
    else
      debug_stream() << "\"\""; }
#line 489 "neverclaimparse.cc" // lalr1.cc:617
        break;

      case 23: // ident_list

#line 94 "neverclaimparse.yy" // lalr1.cc:617
        {
    if ((yysym.value.str))
      debug_stream() << *(yysym.value.str);
    else
      debug_stream() << "\"\""; }
#line 500 "neverclaimparse.cc" // lalr1.cc:617
        break;

      case 27: // formula

#line 94 "neverclaimparse.yy" // lalr1.cc:617
        {
    if ((yysym.value.str))
      debug_stream() << *(yysym.value.str);
    else
      debug_stream() << "\"\""; }
#line 511 "neverclaimparse.cc" // lalr1.cc:617
        break;

      case 28: // opt_dest

#line 94 "neverclaimparse.yy" // lalr1.cc:617
        {
    if ((yysym.value.str))
      debug_stream() << *(yysym.value.str);
    else
      debug_stream() << "\"\""; }
#line 522 "neverclaimparse.cc" // lalr1.cc:617
        break;


      default:
        break;
    }
    yyo << ')';
  }
#endif

  inline
  void
  parser::yypush_ (const char* m, state_type s, symbol_type& sym)
  {
    stack_symbol_type t (s, sym);
    yypush_ (m, t);
  }

  inline
  void
  parser::yypush_ (const char* m, stack_symbol_type& s)
  {
    if (m)
      YY_SYMBOL_PRINT (m, s);
    yystack_.push (s);
  }

  inline
  void
  parser::yypop_ (unsigned int n)
  {
    yystack_.pop (n);
  }

#if YYDEBUG
  std::ostream&
  parser::debug_stream () const
  {
    return *yycdebug_;
  }

  void
  parser::set_debug_stream (std::ostream& o)
  {
    yycdebug_ = &o;
  }


  parser::debug_level_type
  parser::debug_level () const
  {
    return yydebug_;
  }

  void
  parser::set_debug_level (debug_level_type l)
  {
    yydebug_ = l;
  }
#endif // YYDEBUG

  inline parser::state_type
  parser::yy_lr_goto_state_ (state_type yystate, int yysym)
  {
    int yyr = yypgoto_[yysym - yyntokens_] + yystate;
    if (0 <= yyr && yyr <= yylast_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - yyntokens_];
  }

  inline bool
  parser::yy_pact_value_is_default_ (int yyvalue)
  {
    return yyvalue == yypact_ninf_;
  }

  inline bool
  parser::yy_table_value_is_error_ (int yyvalue)
  {
    return yyvalue == yytable_ninf_;
  }

  int
  parser::parse ()
  {
    /// Whether yyla contains a lookahead.
    bool yyempty = true;

    // State.
    int yyn;
    /// Length of the RHS of the rule being reduced.
    int yylen = 0;

    // Error handling.
    int yynerrs_ = 0;
    int yyerrstatus_ = 0;

    /// The lookahead symbol.
    symbol_type yyla;

    /// The locations where the error started and ended.
    stack_symbol_type yyerror_range[3];

    /// The return value of parse ().
    int yyresult;

    // FIXME: This shoud be completely indented.  It is not yet to
    // avoid gratuitous conflicts when merging into the master branch.
    try
      {
    YYCDEBUG << "Starting parse" << std::endl;


    /* Initialize the stack.  The initial state will be set in
       yynewstate, since the latter expects the semantical and the
       location values to have been already stored, initialize these
       stacks with a primary value.  */
    yystack_.clear ();
    yypush_ (YY_NULLPTR, 0, yyla);

    // A new symbol was pushed on the stack.
  yynewstate:
    YYCDEBUG << "Entering state " << yystack_[0].state << std::endl;

    // Accept?
    if (yystack_[0].state == yyfinal_)
      goto yyacceptlab;

    goto yybackup;

    // Backup.
  yybackup:

    // Try to take a decision without lookahead.
    yyn = yypact_[yystack_[0].state];
    if (yy_pact_value_is_default_ (yyn))
      goto yydefault;

    // Read a lookahead token.
    if (yyempty)
      {
        YYCDEBUG << "Reading a token: ";
        try
          {
            yyla.type = yytranslate_ (yylex (&yyla.value, &yyla.location, error_list));
          }
        catch (const syntax_error& yyexc)
          {
            error (yyexc);
            goto yyerrlab1;
          }
        yyempty = false;
      }
    YY_SYMBOL_PRINT ("Next token is", yyla);

    /* If the proper action on seeing token YYLA.TYPE is to reduce or
       to detect an error, take that action.  */
    yyn += yyla.type_get ();
    if (yyn < 0 || yylast_ < yyn || yycheck_[yyn] != yyla.type_get ())
      goto yydefault;

    // Reduce or error.
    yyn = yytable_[yyn];
    if (yyn <= 0)
      {
        if (yy_table_value_is_error_ (yyn))
          goto yyerrlab;
        yyn = -yyn;
        goto yyreduce;
      }

    // Discard the token being shifted.
    yyempty = true;

    // Count tokens shifted since error; after three, turn off error status.
    if (yyerrstatus_)
      --yyerrstatus_;

    // Shift the lookahead token.
    yypush_ ("Shifting", yyn, yyla);
    goto yynewstate;

  /*-----------------------------------------------------------.
  | yydefault -- do the default action for the current state.  |
  `-----------------------------------------------------------*/
  yydefault:
    yyn = yydefact_[yystack_[0].state];
    if (yyn == 0)
      goto yyerrlab;
    goto yyreduce;

  /*-----------------------------.
  | yyreduce -- Do a reduction.  |
  `-----------------------------*/
  yyreduce:
    yylen = yyr2_[yyn];
    {
      stack_symbol_type yylhs;
      yylhs.state = yy_lr_goto_state_(yystack_[yylen].state, yyr1_[yyn]);
      /* If YYLEN is nonzero, implement the default value of the
         action: '$$ = $1'.  Otherwise, use the top of the stack.

         Otherwise, the following line sets YYLHS.VALUE to garbage.
         This behavior is undocumented and Bison users should not rely
         upon it.  */
      if (yylen)
        yylhs.value = yystack_[yylen - 1].value;
      else
        yylhs.value = yystack_[0].value;

      // Compute the default @$.
      {
        slice<stack_symbol_type, stack_type> slice (yystack_, yylen);
        YYLLOC_DEFAULT (yylhs.location, slice, yylen);
      }

      // Perform the reduction.
      YY_REDUCE_PRINT (yyn);
      try
        {
          switch (yyn)
            {
  case 7:
#line 113 "neverclaimparse.yy" // lalr1.cc:847
    {
      (yylhs.value.str) = (yystack_[1].value.str);
    }
#line 751 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 8:
#line 117 "neverclaimparse.yy" // lalr1.cc:847
    {
      result->add_state_alias(*(yystack_[1].value.str), *(yystack_[2].value.str));
      // Keep any identifier that starts with accept.
      if (strncmp("accept", (yystack_[2].value.str)->c_str(), 6))
        {
          delete (yystack_[2].value.str);
          (yylhs.value.str) = (yystack_[1].value.str);
        }
      else
        {
	  delete (yystack_[1].value.str);
	  (yylhs.value.str) = (yystack_[2].value.str);
        }
    }
#line 770 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 9:
#line 135 "neverclaimparse.yy" // lalr1.cc:847
    {
      (yylhs.value.list) = (yystack_[1].value.list);
    }
#line 778 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 10:
#line 139 "neverclaimparse.yy" // lalr1.cc:847
    {
      (yylhs.value.list) = (yystack_[1].value.list);
    }
#line 786 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 11:
#line 145 "neverclaimparse.yy" // lalr1.cc:847
    {
      if (*(yystack_[1].value.str) == "accept_all")
	accept_all_seen = true;

      spot::state_explicit_string::transition* t = result->create_transition(*(yystack_[1].value.str), *(yystack_[1].value.str));
      bool acc = !strncmp("accept", (yystack_[1].value.str)->c_str(), 6);
      if (acc)
	result->add_acceptance_condition(t,
					 spot::ltl::constant::true_instance());
      delete (yystack_[1].value.str);
    }
#line 802 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 12:
#line 156 "neverclaimparse.yy" // lalr1.cc:847
    { delete (yystack_[0].value.str); }
#line 808 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 13:
#line 157 "neverclaimparse.yy" // lalr1.cc:847
    { delete (yystack_[1].value.str); }
#line 814 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 14:
#line 159 "neverclaimparse.yy" // lalr1.cc:847
    {
      std::list<pair>::iterator it;
      bool acc = !strncmp("accept", (yystack_[1].value.str)->c_str(), 6);
      for (it = (yystack_[0].value.list)->begin(); it != (yystack_[0].value.list)->end(); ++it)
      {
	spot::state_explicit_string::transition* t =
	  result->create_transition(*(yystack_[1].value.str), *it->second);

	result->add_condition(t, it->first);
	if (acc)
	  result
	    ->add_acceptance_condition(t, spot::ltl::constant::true_instance());
      }
      // Free the list
      delete (yystack_[1].value.str);
      for (std::list<pair>::iterator it = (yystack_[0].value.list)->begin();
	   it != (yystack_[0].value.list)->end(); ++it)
	delete it->second;
      delete (yystack_[0].value.list);
    }
#line 839 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 15:
#line 182 "neverclaimparse.yy" // lalr1.cc:847
    { (yylhs.value.list) = new std::list<pair>; }
#line 845 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 16:
#line 184 "neverclaimparse.yy" // lalr1.cc:847
    {
      if ((yystack_[0].value.p))
	{
	  (yystack_[1].value.list)->push_back(*(yystack_[0].value.p));
	  delete (yystack_[0].value.p);
	}
      (yylhs.value.list) = (yystack_[1].value.list);
    }
#line 858 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 19:
#line 194 "neverclaimparse.yy" // lalr1.cc:847
    { (yylhs.value.str) = new std::string("0"); }
#line 864 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 20:
#line 198 "neverclaimparse.yy" // lalr1.cc:847
    {
      (yylhs.value.str) = 0;
    }
#line 872 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 21:
#line 202 "neverclaimparse.yy" // lalr1.cc:847
    {
      (yylhs.value.str) = (yystack_[0].value.str);
    }
#line 880 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 22:
#line 206 "neverclaimparse.yy" // lalr1.cc:847
    {
      delete (yystack_[0].value.str);
      (yylhs.value.str) = new std::string("accept_all");
      accept_all_needed = true;
    }
#line 890 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 23:
#line 213 "neverclaimparse.yy" // lalr1.cc:847
    {
      // If there is no destination, do ignore the transition.
      // This happens for instance with
      //   if
      //   :: false
      //   fi
      if (!(yystack_[0].value.str))
	{
	  delete (yystack_[1].value.str);
	  (yylhs.value.p) = 0;
	}
      else
	{
	  spot::ltl::parse_error_list pel;
	  const spot::ltl::formula* f =
	    spot::ltl::parse_boolean(*(yystack_[1].value.str), pel, parse_environment,
				     debug_level(), true);
	  delete (yystack_[1].value.str);
	  for(spot::ltl::parse_error_list::const_iterator i = pel.begin();
	  i != pel.end(); ++i)
	    {
	      // Adjust the diagnostic to the current position.
	      spot::location here = yystack_[1].location;
	      here.end.line = here.begin.line + i->first.end.line - 1;
	      here.end.column = here.begin.column + i->first.end.column -1;
	      here.begin.line += i->first.begin.line - 1;
	      here.begin.column += i->first.begin.column - 1;
	      error(here, i->second);
	    }
	  (yylhs.value.p) = new pair(f, (yystack_[0].value.str));
	}
    }
#line 927 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 24:
#line 249 "neverclaimparse.yy" // lalr1.cc:847
    {
      (yylhs.value.p) = (yystack_[1].value.p);
    }
#line 935 "neverclaimparse.cc" // lalr1.cc:847
    break;

  case 25:
#line 253 "neverclaimparse.yy" // lalr1.cc:847
    {
      (yylhs.value.p) = (yystack_[0].value.p);
    }
#line 943 "neverclaimparse.cc" // lalr1.cc:847
    break;


#line 947 "neverclaimparse.cc" // lalr1.cc:847
            default:
              break;
            }
        }
      catch (const syntax_error& yyexc)
        {
          error (yyexc);
          YYERROR;
        }
      YY_SYMBOL_PRINT ("-> $$ =", yylhs);
      yypop_ (yylen);
      yylen = 0;
      YY_STACK_PRINT ();

      // Shift the result of the reduction.
      yypush_ (YY_NULLPTR, yylhs);
    }
    goto yynewstate;

  /*--------------------------------------.
  | yyerrlab -- here on detecting error.  |
  `--------------------------------------*/
  yyerrlab:
    // If not already recovering from an error, report this error.
    if (!yyerrstatus_)
      {
        ++yynerrs_;
        error (yyla.location, yysyntax_error_ (yystack_[0].state,
                                           yyempty ? yyempty_ : yyla.type_get ()));
      }


    yyerror_range[1].location = yyla.location;
    if (yyerrstatus_ == 3)
      {
        /* If just tried and failed to reuse lookahead token after an
           error, discard it.  */

        // Return failure if at end of input.
        if (yyla.type_get () == yyeof_)
          YYABORT;
        else if (!yyempty)
          {
            yy_destroy_ ("Error: discarding", yyla);
            yyempty = true;
          }
      }

    // Else will try to reuse lookahead token after shifting the error token.
    goto yyerrlab1;


  /*---------------------------------------------------.
  | yyerrorlab -- error raised explicitly by YYERROR.  |
  `---------------------------------------------------*/
  yyerrorlab:

    /* Pacify compilers like GCC when the user code never invokes
       YYERROR and the label yyerrorlab therefore never appears in user
       code.  */
    if (false)
      goto yyerrorlab;
    yyerror_range[1].location = yystack_[yylen - 1].location;
    /* Do not reclaim the symbols of the rule whose action triggered
       this YYERROR.  */
    yypop_ (yylen);
    yylen = 0;
    goto yyerrlab1;

  /*-------------------------------------------------------------.
  | yyerrlab1 -- common code for both syntax error and YYERROR.  |
  `-------------------------------------------------------------*/
  yyerrlab1:
    yyerrstatus_ = 3;   // Each real token shifted decrements this.
    {
      stack_symbol_type error_token;
      for (;;)
        {
          yyn = yypact_[yystack_[0].state];
          if (!yy_pact_value_is_default_ (yyn))
            {
              yyn += yyterror_;
              if (0 <= yyn && yyn <= yylast_ && yycheck_[yyn] == yyterror_)
                {
                  yyn = yytable_[yyn];
                  if (0 < yyn)
                    break;
                }
            }

          // Pop the current state because it cannot handle the error token.
          if (yystack_.size () == 1)
            YYABORT;

          yyerror_range[1].location = yystack_[0].location;
          yy_destroy_ ("Error: popping", yystack_[0]);
          yypop_ ();
          YY_STACK_PRINT ();
        }

      yyerror_range[2].location = yyla.location;
      YYLLOC_DEFAULT (error_token.location, yyerror_range, 2);

      // Shift the error token.
      error_token.state = yyn;
      yypush_ ("Shifting", error_token);
    }
    goto yynewstate;

    // Accept.
  yyacceptlab:
    yyresult = 0;
    goto yyreturn;

    // Abort.
  yyabortlab:
    yyresult = 1;
    goto yyreturn;

  yyreturn:
    if (!yyempty)
      yy_destroy_ ("Cleanup: discarding lookahead", yyla);

    /* Do not reclaim the symbols of the rule whose action triggered
       this YYABORT or YYACCEPT.  */
    yypop_ (yylen);
    while (1 < yystack_.size ())
      {
        yy_destroy_ ("Cleanup: popping", yystack_[0]);
        yypop_ ();
      }

    return yyresult;
  }
    catch (...)
      {
        YYCDEBUG << "Exception caught: cleaning lookahead and stack"
                 << std::endl;
        // Do not try to display the values of the reclaimed symbols,
        // as their printer might throw an exception.
        if (!yyempty)
          yy_destroy_ (YY_NULLPTR, yyla);

        while (1 < yystack_.size ())
          {
            yy_destroy_ (YY_NULLPTR, yystack_[0]);
            yypop_ ();
          }
        throw;
      }
  }

  void
  parser::error (const syntax_error& yyexc)
  {
    error (yyexc.location, yyexc.what());
  }

  // Generate an error message.
  std::string
  parser::yysyntax_error_ (state_type yystate, symbol_number_type yytoken) const
  {
    std::string yyres;
    // Number of reported tokens (one for the "unexpected", one per
    // "expected").
    size_t yycount = 0;
    // Its maximum.
    enum { YYERROR_VERBOSE_ARGS_MAXIMUM = 5 };
    // Arguments of yyformat.
    char const *yyarg[YYERROR_VERBOSE_ARGS_MAXIMUM];

    /* There are many possibilities here to consider:
       - If this state is a consistent state with a default action, then
         the only way this function was invoked is if the default action
         is an error action.  In that case, don't check for expected
         tokens because there are none.
       - The only way there can be no lookahead present (in yytoken) is
         if this state is a consistent state with a default action.
         Thus, detecting the absence of a lookahead is sufficient to
         determine that there is no unexpected or expected token to
         report.  In that case, just report a simple "syntax error".
       - Don't assume there isn't a lookahead just because this state is
         a consistent state with a default action.  There might have
         been a previous inconsistent state, consistent state with a
         non-default action, or user semantic action that manipulated
         yyla.  (However, yyla is currently not documented for users.)
       - Of course, the expected token list depends on states to have
         correct lookahead information, and it depends on the parser not
         to perform extra reductions after fetching a lookahead from the
         scanner and before detecting a syntax error.  Thus, state
         merging (from LALR or IELR) and default reductions corrupt the
         expected token list.  However, the list is correct for
         canonical LR with one exception: it will still contain any
         token that will not be accepted due to an error action in a
         later state.
    */
    if (yytoken != yyempty_)
      {
        yyarg[yycount++] = yytname_[yytoken];
        int yyn = yypact_[yystate];
        if (!yy_pact_value_is_default_ (yyn))
          {
            /* Start YYX at -YYN if negative to avoid negative indexes in
               YYCHECK.  In other words, skip the first -YYN actions for
               this state because they are default actions.  */
            int yyxbegin = yyn < 0 ? -yyn : 0;
            // Stay within bounds of both yycheck and yytname.
            int yychecklim = yylast_ - yyn + 1;
            int yyxend = yychecklim < yyntokens_ ? yychecklim : yyntokens_;
            for (int yyx = yyxbegin; yyx < yyxend; ++yyx)
              if (yycheck_[yyx + yyn] == yyx && yyx != yyterror_
                  && !yy_table_value_is_error_ (yytable_[yyx + yyn]))
                {
                  if (yycount == YYERROR_VERBOSE_ARGS_MAXIMUM)
                    {
                      yycount = 1;
                      break;
                    }
                  else
                    yyarg[yycount++] = yytname_[yyx];
                }
          }
      }

    char const* yyformat = YY_NULLPTR;
    switch (yycount)
      {
#define YYCASE_(N, S)                         \
        case N:                               \
          yyformat = S;                       \
        break
        YYCASE_(0, YY_("syntax error"));
        YYCASE_(1, YY_("syntax error, unexpected %s"));
        YYCASE_(2, YY_("syntax error, unexpected %s, expecting %s"));
        YYCASE_(3, YY_("syntax error, unexpected %s, expecting %s or %s"));
        YYCASE_(4, YY_("syntax error, unexpected %s, expecting %s or %s or %s"));
        YYCASE_(5, YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s"));
#undef YYCASE_
      }

    // Argument number.
    size_t yyi = 0;
    for (char const* yyp = yyformat; *yyp; ++yyp)
      if (yyp[0] == '%' && yyp[1] == 's' && yyi < yycount)
        {
          yyres += yytnamerr_ (yyarg[yyi++]);
          ++yyp;
        }
      else
        yyres += *yyp;
    return yyres;
  }


  const signed char parser::yypact_ninf_ = -13;

  const signed char parser::yytable_ninf_ = -1;

  const signed char
  parser::yypact_[] =
  {
       6,     5,    22,     8,   -13,     9,   -12,    -3,   -13,   -13,
     -13,     8,   -13,   -13,   -13,   -13,    10,   -13,   -13,    -6,
      -5,   -13,   -13,    11,   -13,   -13,     4,   -13,    15,   -13,
     -13,    16,   -13,    -4,     7,   -13,    17,    12,    18,   -13,
     -13,   -13
  };

  const unsigned char
  parser::yydefact_[] =
  {
       0,     0,     0,     3,     1,     0,     0,    12,     4,     7,
       2,     6,    11,    15,    15,    13,     0,    14,     5,     0,
       0,     8,     9,     0,    16,    10,     0,    19,     0,    17,
      18,    20,    25,     0,     0,    23,     0,     0,     0,    24,
      21,    22
  };

  const signed char
  parser::yypgoto_[] =
  {
     -13,   -13,   -13,   -13,   -13,    13,    19,   -13,   -13,    -7,
     -13
  };

  const signed char
  parser::yydefgoto_[] =
  {
      -1,     2,     6,     7,    17,     8,    19,    31,    35,    32,
      24
  };

  const unsigned char
  parser::yytable_[] =
  {
      22,    12,    13,    25,    14,    10,    11,    27,    15,     1,
      29,    30,    16,    23,    23,    27,    28,    37,    29,    30,
      38,     3,     4,     5,    18,    34,    36,    40,     9,    21,
      26,    33,    41,    20,    39
  };

  const unsigned char
  parser::yycheck_[] =
  {
       6,     4,     5,     8,     7,    17,    18,    11,    11,     3,
      14,    15,    15,    19,    19,    11,    12,    10,    14,    15,
      13,    16,     0,    15,    11,     9,    33,    15,    19,    19,
      19,    16,    14,    14,    17
  };

  const unsigned char
  parser::yystos_[] =
  {
       0,     3,    21,    16,     0,    15,    22,    23,    25,    19,
      17,    18,     4,     5,     7,    11,    15,    24,    25,    26,
      26,    19,     6,    19,    30,     8,    19,    11,    12,    14,
      15,    27,    29,    16,     9,    28,    29,    10,    13,    17,
      15,    14
  };

  const unsigned char
  parser::yyr1_[] =
  {
       0,    20,    21,    22,    22,    22,    22,    23,    23,    24,
      24,    25,    25,    25,    25,    26,    26,    27,    27,    27,
      28,    28,    28,    29,    30,    30
  };

  const unsigned char
  parser::yyr2_[] =
  {
       0,     2,     4,     0,     1,     3,     2,     2,     3,     3,
       3,     2,     1,     2,     2,     0,     2,     1,     1,     1,
       0,     3,     3,     2,     6,     3
  };



  // YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
  // First, the terminals, then, starting at \a yyntokens_, nonterminals.
  const char*
  const parser::yytname_[] =
  {
  "$end", "error", "$undefined", "\"never\"", "\"skip\"", "\"if\"",
  "\"fi\"", "\"do\"", "\"od\"", "\"->\"", "\"goto\"", "\"false\"",
  "\"atomic\"", "\"assert\"", "\"boolean formula\"", "\"identifier\"",
  "'{'", "'}'", "';'", "':'", "$accept", "neverclaim", "states",
  "ident_list", "transition_block", "state", "transitions", "formula",
  "opt_dest", "src_dest", "transition", YY_NULLPTR
  };

#if YYDEBUG
  const unsigned char
  parser::yyrline_[] =
  {
       0,   102,   102,   105,   107,   108,   109,   112,   116,   134,
     138,   144,   156,   157,   158,   182,   183,   194,   194,   194,
     198,   201,   205,   212,   248,   252
  };

  // Print the state stack on the debug stream.
  void
  parser::yystack_print_ ()
  {
    *yycdebug_ << "Stack now";
    for (stack_type::const_iterator
           i = yystack_.begin (),
           i_end = yystack_.end ();
         i != i_end; ++i)
      *yycdebug_ << ' ' << i->state;
    *yycdebug_ << std::endl;
  }

  // Report on the debug stream that the rule \a yyrule is going to be reduced.
  void
  parser::yy_reduce_print_ (int yyrule)
  {
    unsigned int yylno = yyrline_[yyrule];
    int yynrhs = yyr2_[yyrule];
    // Print the symbols being reduced, and their result.
    *yycdebug_ << "Reducing stack by rule " << yyrule - 1
               << " (line " << yylno << "):" << std::endl;
    // The symbols being reduced.
    for (int yyi = 0; yyi < yynrhs; yyi++)
      YY_SYMBOL_PRINT ("   $" << yyi + 1 << " =",
                       yystack_[(yynrhs) - (yyi + 1)]);
  }
#endif // YYDEBUG

  // Symbol number corresponding to token number t.
  inline
  parser::token_number_type
  parser::yytranslate_ (int t)
  {
    static
    const token_number_type
    translate_table[] =
    {
     0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    19,    18,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    16,     2,    17,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15
    };
    const unsigned int user_token_number_max_ = 270;
    const token_number_type undef_token_ = 2;

    if (static_cast<int>(t) <= yyeof_)
      return yyeof_;
    else if (static_cast<unsigned int> (t) <= user_token_number_max_)
      return translate_table[t];
    else
      return undef_token_;
  }


} // neverclaimyy
#line 1388 "neverclaimparse.cc" // lalr1.cc:1155
#line 256 "neverclaimparse.yy" // lalr1.cc:1156


void
neverclaimyy::parser::error(const location_type& location,
			    const std::string& message)
{
  error_list.push_back(spot::neverclaim_parse_error(location, message));
}

namespace spot
{
  tgba_explicit_string*
  neverclaim_parse(const std::string& name,
		   neverclaim_parse_error_list& error_list,
		   bdd_dict* dict,
		   environment& env,
		   bool debug)
  {
    if (neverclaimyyopen(name))
      {
	error_list.push_back
	  (neverclaim_parse_error(spot::location(),
				  std::string("Cannot open file ") + name));
	return 0;
      }
    tgba_explicit_string* result = new tgba_explicit_string(dict);
    result->declare_acceptance_condition(spot::ltl::constant::true_instance());
    neverclaimyy::parser parser(error_list, env, result);
    parser.set_debug_level(debug);
    parser.parse();
    neverclaimyyclose();

    if (accept_all_needed && !accept_all_seen)
      {
	spot::state_explicit_string::transition* t =
	  result->create_transition("accept_all", "accept_all");
	result->add_acceptance_condition
	  (t, spot::ltl::constant::true_instance());
      }
    accept_all_needed = false;
    accept_all_seen = false;

    return result;
  }
}

// Local Variables:
// mode: c++
// End:
