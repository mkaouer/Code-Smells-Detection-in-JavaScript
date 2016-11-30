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
#define yylex   ltlyylex

// First part of user declarations.

#line 39 "ltlparse.cc" // lalr1.cc:399

# ifndef YY_NULLPTR
#  if defined __cplusplus && 201103L <= __cplusplus
#   define YY_NULLPTR nullptr
#  else
#   define YY_NULLPTR 0
#  endif
# endif

#include "ltlparse.hh"

// User implementation prologue.

#line 53 "ltlparse.cc" // lalr1.cc:407
// Unqualified %code blocks.
#line 54 "ltlparse.yy" // lalr1.cc:408

/* ltlparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;

#define missing_right_op_msg(op, str)		\
  error_list.push_back(parse_error(op,		\
    "missing right operand for \"" str "\""));

#define missing_right_op(res, op, str)		\
  do						\
    {						\
      missing_right_op_msg(op, str);		\
      res = constant::false_instance();		\
    }						\
  while (0);

// right is missing, so complain and use left.
#define missing_right_binop(res, left, op, str)	\
  do						\
    {						\
      missing_right_op_msg(op, str);		\
      res = left;				\
    }						\
  while (0);

// right is missing, so complain and use false.
#define missing_right_binop_hard(res, left, op, str)	\
  do							\
    {							\
      left->destroy();					\
      missing_right_op(res, op, str);			\
    }							\
  while (0);

  enum parser_type { parser_ltl, parser_bool, parser_sere };

  const formula*
  try_recursive_parse(const std::string& str,
		      const spot::location& location,
		      spot::ltl::environment& env,
		      bool debug,
		      parser_type type,
		      spot::ltl::parse_error_list& error_list)
    {
      // We want to parse a U (b U c) as two until operators applied
      // to the atomic propositions a, b, and c.  We also want to
      // parse a U (b == c) as one until operator applied to the
      // atomic propositions "a" and "b == c".  The only problem is
      // that we do not know anything about "==" or in general about
      // the syntax of atomic proposition of our users.
      //
      // To support that, the lexer will return "b U c" and "b == c"
      // as PAR_BLOCK tokens.  We then try to parse such tokens
      // recursively.  If, as in the case of "b U c", the block is
      // successfully parsed as a formula, we return this formula.
      // Otherwise, we convert the string into an atomic proposition
      // (it's up to the environment to check the syntax of this
      // proposition, and maybe reject it).

      if (str.empty())
	{
	  error_list.push_back(parse_error(location,
					   "unexpected empty block"));
	  return 0;
	}

      spot::ltl::parse_error_list suberror;
      const spot::ltl::formula* f = 0;
      switch (type)
	{
	case parser_sere:
	  f = spot::ltl::parse_sere(str, suberror, env, debug, true);
	  break;
	case parser_bool:
	  f = spot::ltl::parse_boolean(str, suberror, env, debug, true);
	  break;
	case parser_ltl:
	  f = spot::ltl::parse(str, suberror, env, debug, true);
	  break;
	}

      if (suberror.empty())
	return f;

      if (f)
	f->destroy();

      f = env.require(str);
      if (!f)
	{
	  std::string s = "atomic proposition `";
	  s += str;
	  s += "' rejected by environment `";
	  s += env.name();
	  s += "'";
	  error_list.push_back(parse_error(location, s));
	}
      return f;
    }

#line 159 "ltlparse.cc" // lalr1.cc:408


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


namespace ltlyy {
#line 245 "ltlparse.cc" // lalr1.cc:474

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
  parser::parser (spot::ltl::parse_error_list &error_list_yyarg, spot::ltl::environment &parse_environment_yyarg, const spot::ltl::formula* &result_yyarg)
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
            case 9: // "(...) block"

#line 234 "ltlparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 466 "ltlparse.cc" // lalr1.cc:599
        break;

      case 10: // "{...} block"

#line 234 "ltlparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 473 "ltlparse.cc" // lalr1.cc:599
        break;

      case 11: // "{...}! block"

#line 234 "ltlparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 480 "ltlparse.cc" // lalr1.cc:599
        break;

      case 43: // "atomic proposition"

#line 234 "ltlparse.yy" // lalr1.cc:599
        { delete (yysym.value.str); }
#line 487 "ltlparse.cc" // lalr1.cc:599
        break;

      case 79: // booleanatom

#line 235 "ltlparse.yy" // lalr1.cc:599
        { (yysym.value.ltl)->destroy(); }
#line 494 "ltlparse.cc" // lalr1.cc:599
        break;

      case 80: // sere

#line 235 "ltlparse.yy" // lalr1.cc:599
        { (yysym.value.ltl)->destroy(); }
#line 501 "ltlparse.cc" // lalr1.cc:599
        break;

      case 81: // bracedsere

#line 235 "ltlparse.yy" // lalr1.cc:599
        { (yysym.value.ltl)->destroy(); }
#line 508 "ltlparse.cc" // lalr1.cc:599
        break;

      case 82: // parenthesedsubformula

#line 235 "ltlparse.yy" // lalr1.cc:599
        { (yysym.value.ltl)->destroy(); }
#line 515 "ltlparse.cc" // lalr1.cc:599
        break;

      case 83: // boolformula

#line 235 "ltlparse.yy" // lalr1.cc:599
        { (yysym.value.ltl)->destroy(); }
#line 522 "ltlparse.cc" // lalr1.cc:599
        break;

      case 84: // subformula

#line 235 "ltlparse.yy" // lalr1.cc:599
        { (yysym.value.ltl)->destroy(); }
#line 529 "ltlparse.cc" // lalr1.cc:599
        break;

      case 85: // lbtformula

#line 235 "ltlparse.yy" // lalr1.cc:599
        { (yysym.value.ltl)->destroy(); }
#line 536 "ltlparse.cc" // lalr1.cc:599
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
            case 9: // "(...) block"

#line 237 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << *(yysym.value.str); }
#line 563 "ltlparse.cc" // lalr1.cc:617
        break;

      case 10: // "{...} block"

#line 237 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << *(yysym.value.str); }
#line 570 "ltlparse.cc" // lalr1.cc:617
        break;

      case 11: // "{...}! block"

#line 237 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << *(yysym.value.str); }
#line 577 "ltlparse.cc" // lalr1.cc:617
        break;

      case 36: // "number for square bracket operator"

#line 240 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << (yysym.value.num); }
#line 584 "ltlparse.cc" // lalr1.cc:617
        break;

      case 43: // "atomic proposition"

#line 237 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << *(yysym.value.str); }
#line 591 "ltlparse.cc" // lalr1.cc:617
        break;

      case 74: // sqbracketargs

#line 241 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << (yysym.value.minmax).min << ".." << (yysym.value.minmax).max; }
#line 598 "ltlparse.cc" // lalr1.cc:617
        break;

      case 75: // gotoargs

#line 241 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << (yysym.value.minmax).min << ".." << (yysym.value.minmax).max; }
#line 605 "ltlparse.cc" // lalr1.cc:617
        break;

      case 77: // starargs

#line 241 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << (yysym.value.minmax).min << ".." << (yysym.value.minmax).max; }
#line 612 "ltlparse.cc" // lalr1.cc:617
        break;

      case 78: // equalargs

#line 241 "ltlparse.yy" // lalr1.cc:617
        { debug_stream() << (yysym.value.minmax).min << ".." << (yysym.value.minmax).max; }
#line 619 "ltlparse.cc" // lalr1.cc:617
        break;

      case 79: // booleanatom

#line 238 "ltlparse.yy" // lalr1.cc:617
        { spot::ltl::to_string((yysym.value.ltl), debug_stream()); }
#line 626 "ltlparse.cc" // lalr1.cc:617
        break;

      case 80: // sere

#line 239 "ltlparse.yy" // lalr1.cc:617
        { spot::ltl::to_string((yysym.value.ltl), debug_stream(), false, true); }
#line 633 "ltlparse.cc" // lalr1.cc:617
        break;

      case 81: // bracedsere

#line 239 "ltlparse.yy" // lalr1.cc:617
        { spot::ltl::to_string((yysym.value.ltl), debug_stream(), false, true); }
#line 640 "ltlparse.cc" // lalr1.cc:617
        break;

      case 82: // parenthesedsubformula

#line 238 "ltlparse.yy" // lalr1.cc:617
        { spot::ltl::to_string((yysym.value.ltl), debug_stream()); }
#line 647 "ltlparse.cc" // lalr1.cc:617
        break;

      case 83: // boolformula

#line 238 "ltlparse.yy" // lalr1.cc:617
        { spot::ltl::to_string((yysym.value.ltl), debug_stream()); }
#line 654 "ltlparse.cc" // lalr1.cc:617
        break;

      case 84: // subformula

#line 238 "ltlparse.yy" // lalr1.cc:617
        { spot::ltl::to_string((yysym.value.ltl), debug_stream()); }
#line 661 "ltlparse.cc" // lalr1.cc:617
        break;

      case 85: // lbtformula

#line 238 "ltlparse.yy" // lalr1.cc:617
        { spot::ltl::to_string((yysym.value.ltl), debug_stream()); }
#line 668 "ltlparse.cc" // lalr1.cc:617
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
  case 2:
#line 245 "ltlparse.yy" // lalr1.cc:847
    { result = (yystack_[1].value.ltl);
		YYACCEPT;
	      }
#line 897 "ltlparse.cc" // lalr1.cc:847
    break;

  case 3:
#line 249 "ltlparse.yy" // lalr1.cc:847
    {
		result = 0;
		YYABORT;
	      }
#line 906 "ltlparse.cc" // lalr1.cc:847
    break;

  case 4:
#line 254 "ltlparse.yy" // lalr1.cc:847
    {
		result = (yystack_[1].value.ltl);
		YYACCEPT;
	      }
#line 915 "ltlparse.cc" // lalr1.cc:847
    break;

  case 5:
#line 259 "ltlparse.yy" // lalr1.cc:847
    { YYABORT; }
#line 921 "ltlparse.cc" // lalr1.cc:847
    break;

  case 6:
#line 261 "ltlparse.yy" // lalr1.cc:847
    { result = (yystack_[1].value.ltl);
		YYACCEPT;
	      }
#line 929 "ltlparse.cc" // lalr1.cc:847
    break;

  case 7:
#line 265 "ltlparse.yy" // lalr1.cc:847
    {
		result = 0;
		YYABORT;
	      }
#line 938 "ltlparse.cc" // lalr1.cc:847
    break;

  case 8:
#line 270 "ltlparse.yy" // lalr1.cc:847
    {
		result = (yystack_[1].value.ltl);
		YYACCEPT;
	      }
#line 947 "ltlparse.cc" // lalr1.cc:847
    break;

  case 9:
#line 275 "ltlparse.yy" // lalr1.cc:847
    { YYABORT; }
#line 953 "ltlparse.cc" // lalr1.cc:847
    break;

  case 10:
#line 277 "ltlparse.yy" // lalr1.cc:847
    { result = (yystack_[1].value.ltl);
		YYACCEPT;
	      }
#line 961 "ltlparse.cc" // lalr1.cc:847
    break;

  case 11:
#line 281 "ltlparse.yy" // lalr1.cc:847
    {
		result = 0;
		YYABORT;
	      }
#line 970 "ltlparse.cc" // lalr1.cc:847
    break;

  case 12:
#line 286 "ltlparse.yy" // lalr1.cc:847
    {
		result = (yystack_[1].value.ltl);
		YYACCEPT;
	      }
#line 979 "ltlparse.cc" // lalr1.cc:847
    break;

  case 13:
#line 291 "ltlparse.yy" // lalr1.cc:847
    { YYABORT; }
#line 985 "ltlparse.cc" // lalr1.cc:847
    break;

  case 14:
#line 293 "ltlparse.yy" // lalr1.cc:847
    { result = (yystack_[1].value.ltl);
		YYACCEPT;
	      }
#line 993 "ltlparse.cc" // lalr1.cc:847
    break;

  case 15:
#line 297 "ltlparse.yy" // lalr1.cc:847
    {
		result = 0;
		YYABORT;
	      }
#line 1002 "ltlparse.cc" // lalr1.cc:847
    break;

  case 16:
#line 302 "ltlparse.yy" // lalr1.cc:847
    {
		result = (yystack_[1].value.ltl);
		YYACCEPT;
	      }
#line 1011 "ltlparse.cc" // lalr1.cc:847
    break;

  case 17:
#line 307 "ltlparse.yy" // lalr1.cc:847
    { YYABORT; }
#line 1017 "ltlparse.cc" // lalr1.cc:847
    break;

  case 18:
#line 310 "ltlparse.yy" // lalr1.cc:847
    {
		error_list.push_back(parse_error(yylhs.location, "empty input"));
		result = 0;
	      }
#line 1026 "ltlparse.cc" // lalr1.cc:847
    break;

  case 19:
#line 316 "ltlparse.yy" // lalr1.cc:847
    {
		error_list.push_back(parse_error(yystack_[1].location,
						 "ignoring trailing garbage"));
	      }
#line 1035 "ltlparse.cc" // lalr1.cc:847
    break;

  case 26:
#line 328 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = (yystack_[3].value.num); (yylhs.value.minmax).max = (yystack_[1].value.num); }
#line 1041 "ltlparse.cc" // lalr1.cc:847
    break;

  case 27:
#line 330 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = (yystack_[2].value.num); (yylhs.value.minmax).max = bunop::unbounded; }
#line 1047 "ltlparse.cc" // lalr1.cc:847
    break;

  case 28:
#line 332 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = 0U; (yylhs.value.minmax).max = (yystack_[1].value.num); }
#line 1053 "ltlparse.cc" // lalr1.cc:847
    break;

  case 29:
#line 334 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = 0U; (yylhs.value.minmax).max = bunop::unbounded; }
#line 1059 "ltlparse.cc" // lalr1.cc:847
    break;

  case 30:
#line 336 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = (yylhs.value.minmax).max = (yystack_[1].value.num); }
#line 1065 "ltlparse.cc" // lalr1.cc:847
    break;

  case 31:
#line 340 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = (yystack_[3].value.num); (yylhs.value.minmax).max = (yystack_[1].value.num); }
#line 1071 "ltlparse.cc" // lalr1.cc:847
    break;

  case 32:
#line 342 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = (yystack_[2].value.num); (yylhs.value.minmax).max = bunop::unbounded; }
#line 1077 "ltlparse.cc" // lalr1.cc:847
    break;

  case 33:
#line 344 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = 1U; (yylhs.value.minmax).max = (yystack_[1].value.num); }
#line 1083 "ltlparse.cc" // lalr1.cc:847
    break;

  case 34:
#line 346 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = 1U; (yylhs.value.minmax).max = bunop::unbounded; }
#line 1089 "ltlparse.cc" // lalr1.cc:847
    break;

  case 35:
#line 348 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = (yylhs.value.minmax).max = 1U; }
#line 1095 "ltlparse.cc" // lalr1.cc:847
    break;

  case 36:
#line 350 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = (yylhs.value.minmax).max = (yystack_[1].value.num); }
#line 1101 "ltlparse.cc" // lalr1.cc:847
    break;

  case 37:
#line 352 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
	       "treating this goto block as [->]"));
             (yylhs.value.minmax).min = (yylhs.value.minmax).max = 1U; }
#line 1109 "ltlparse.cc" // lalr1.cc:847
    break;

  case 38:
#line 356 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
               "missing closing bracket for goto operator"));
	     (yylhs.value.minmax).min = (yylhs.value.minmax).max = 0U; }
#line 1117 "ltlparse.cc" // lalr1.cc:847
    break;

  case 41:
#line 363 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = 0U; (yylhs.value.minmax).max = bunop::unbounded; }
#line 1123 "ltlparse.cc" // lalr1.cc:847
    break;

  case 42:
#line 365 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax).min = 1U; (yylhs.value.minmax).max = bunop::unbounded; }
#line 1129 "ltlparse.cc" // lalr1.cc:847
    break;

  case 43:
#line 367 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax) = (yystack_[0].value.minmax); }
#line 1135 "ltlparse.cc" // lalr1.cc:847
    break;

  case 44:
#line 369 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
		"treating this star block as [*]"));
              (yylhs.value.minmax).min = 0U; (yylhs.value.minmax).max = bunop::unbounded; }
#line 1143 "ltlparse.cc" // lalr1.cc:847
    break;

  case 45:
#line 373 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
                "missing closing bracket for star"));
	      (yylhs.value.minmax).min = (yylhs.value.minmax).max = 0U; }
#line 1151 "ltlparse.cc" // lalr1.cc:847
    break;

  case 46:
#line 378 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.minmax) = (yystack_[0].value.minmax); }
#line 1157 "ltlparse.cc" // lalr1.cc:847
    break;

  case 47:
#line 380 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
		"treating this equal block as [*]"));
              (yylhs.value.minmax).min = 0U; (yylhs.value.minmax).max = bunop::unbounded; }
#line 1165 "ltlparse.cc" // lalr1.cc:847
    break;

  case 48:
#line 384 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
                "missing closing bracket for equal operator"));
	      (yylhs.value.minmax).min = (yylhs.value.minmax).max = 0U; }
#line 1173 "ltlparse.cc" // lalr1.cc:847
    break;

  case 49:
#line 393 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = parse_environment.require(*(yystack_[0].value.str));
		if (! (yylhs.value.ltl))
		  {
		    std::string s = "unknown atomic proposition `";
		    s += *(yystack_[0].value.str);
		    s += "' in environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error(yystack_[0].location, s));
		    delete (yystack_[0].value.str);
		    YYERROR;
		  }
		else
		  delete (yystack_[0].value.str);
	      }
#line 1194 "ltlparse.cc" // lalr1.cc:847
    break;

  case 50:
#line 410 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = parse_environment.require(*(yystack_[1].value.str));
		if (! (yylhs.value.ltl))
		  {
		    std::string s = "unknown atomic proposition `";
		    s += *(yystack_[1].value.str);
		    s += "' in environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error(yystack_[1].location, s));
		    delete (yystack_[1].value.str);
		    YYERROR;
		  }
		else
		  delete (yystack_[1].value.str);
	      }
#line 1215 "ltlparse.cc" // lalr1.cc:847
    break;

  case 51:
#line 427 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = parse_environment.require(*(yystack_[1].value.str));
		if (! (yylhs.value.ltl))
		  {
		    std::string s = "unknown atomic proposition `";
		    s += *(yystack_[1].value.str);
		    s += "' in environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error(yystack_[1].location, s));
		    delete (yystack_[1].value.str);
		    YYERROR;
		  }
		else
		  delete (yystack_[1].value.str);
		(yylhs.value.ltl) = unop::instance(unop::Not, (yylhs.value.ltl));
	      }
#line 1237 "ltlparse.cc" // lalr1.cc:847
    break;

  case 52:
#line 445 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = constant::true_instance(); }
#line 1243 "ltlparse.cc" // lalr1.cc:847
    break;

  case 53:
#line 447 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = constant::false_instance(); }
#line 1249 "ltlparse.cc" // lalr1.cc:847
    break;

  case 55:
#line 451 "ltlparse.yy" // lalr1.cc:847
    {
		if ((yystack_[0].value.ltl)->is_boolean())
		  {
		    (yylhs.value.ltl) = unop::instance(unop::Not, (yystack_[0].value.ltl));
		  }
		else
		  {
		    error_list.push_back(parse_error(yystack_[0].location,
                       "not a boolean expression: inside a SERE `!' can only "
                       "be applied to a Boolean expression"));
		    error_list.push_back(parse_error(yylhs.location,
				"treating this block as false"));
		    (yystack_[0].value.ltl)->destroy();
		    (yylhs.value.ltl) = constant::false_instance();
		  }
	      }
#line 1270 "ltlparse.cc" // lalr1.cc:847
    break;

  case 57:
#line 469 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = try_recursive_parse(*(yystack_[0].value.str), yystack_[0].location, parse_environment,
					 debug_level(), parser_sere, error_list);
		delete (yystack_[0].value.str);
		if (!(yylhs.value.ltl))
		  YYERROR;
	      }
#line 1282 "ltlparse.cc" // lalr1.cc:847
    break;

  case 58:
#line 477 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = (yystack_[1].value.ltl); }
#line 1288 "ltlparse.cc" // lalr1.cc:847
    break;

  case 59:
#line 479 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
		 "treating this parenthetical block as false"));
		(yylhs.value.ltl) = constant::false_instance();
	      }
#line 1297 "ltlparse.cc" // lalr1.cc:847
    break;

  case 60:
#line 484 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[2].location + yystack_[1].location,
				      "missing closing parenthesis"));
		(yylhs.value.ltl) = (yystack_[1].value.ltl);
	      }
#line 1306 "ltlparse.cc" // lalr1.cc:847
    break;

  case 61:
#line 489 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		(yylhs.value.ltl) = constant::false_instance();
	      }
#line 1316 "ltlparse.cc" // lalr1.cc:847
    break;

  case 62:
#line 495 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::AndRat, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1322 "ltlparse.cc" // lalr1.cc:847
    break;

  case 63:
#line 497 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location,
				    "length-matching and operator"); }
#line 1329 "ltlparse.cc" // lalr1.cc:847
    break;

  case 64:
#line 500 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::AndNLM, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1335 "ltlparse.cc" // lalr1.cc:847
    break;

  case 65:
#line 502 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location,
                                    "non-length-matching and operator"); }
#line 1342 "ltlparse.cc" // lalr1.cc:847
    break;

  case 66:
#line 505 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::OrRat, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1348 "ltlparse.cc" // lalr1.cc:847
    break;

  case 67:
#line 507 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "or operator"); }
#line 1354 "ltlparse.cc" // lalr1.cc:847
    break;

  case 68:
#line 509 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::Concat, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1360 "ltlparse.cc" // lalr1.cc:847
    break;

  case 69:
#line 511 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "concat operator"); }
#line 1366 "ltlparse.cc" // lalr1.cc:847
    break;

  case 70:
#line 513 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::Fusion, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1372 "ltlparse.cc" // lalr1.cc:847
    break;

  case 71:
#line 515 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "fusion operator"); }
#line 1378 "ltlparse.cc" // lalr1.cc:847
    break;

  case 72:
#line 517 "ltlparse.yy" // lalr1.cc:847
    {
		if ((yystack_[0].value.minmax).max < (yystack_[0].value.minmax).min)
		  {
		    error_list.push_back(parse_error(yystack_[0].location, "reversed range"));
		    std::swap((yystack_[0].value.minmax).max, (yystack_[0].value.minmax).min);
		  }
		(yylhs.value.ltl) = bunop::instance(bunop::Star, (yystack_[1].value.ltl), (yystack_[0].value.minmax).min, (yystack_[0].value.minmax).max);
	      }
#line 1391 "ltlparse.cc" // lalr1.cc:847
    break;

  case 73:
#line 526 "ltlparse.yy" // lalr1.cc:847
    {
		if ((yystack_[0].value.minmax).max < (yystack_[0].value.minmax).min)
		  {
		    error_list.push_back(parse_error(yystack_[0].location, "reversed range"));
		    std::swap((yystack_[0].value.minmax).max, (yystack_[0].value.minmax).min);
		  }
		(yylhs.value.ltl) = bunop::instance(bunop::Star, constant::true_instance(),
				     (yystack_[0].value.minmax).min, (yystack_[0].value.minmax).max);
	      }
#line 1405 "ltlparse.cc" // lalr1.cc:847
    break;

  case 74:
#line 536 "ltlparse.yy" // lalr1.cc:847
    {
		if ((yystack_[0].value.minmax).max < (yystack_[0].value.minmax).min)
		  {
		    error_list.push_back(parse_error(yystack_[0].location, "reversed range"));
		    std::swap((yystack_[0].value.minmax).max, (yystack_[0].value.minmax).min);
		  }
		if ((yystack_[1].value.ltl)->is_boolean())
		  {
		    (yylhs.value.ltl) = bunop::sugar_equal((yystack_[1].value.ltl), (yystack_[0].value.minmax).min, (yystack_[0].value.minmax).max);
		  }
		else
		  {
		    error_list.push_back(parse_error(yystack_[1].location,
				"not a boolean expression: [=...] can only "
				"be applied to a Boolean expression"));
		    error_list.push_back(parse_error(yylhs.location,
				"treating this block as false"));
		    (yystack_[1].value.ltl)->destroy();
		    (yylhs.value.ltl) = constant::false_instance();
		  }
	      }
#line 1431 "ltlparse.cc" // lalr1.cc:847
    break;

  case 75:
#line 558 "ltlparse.yy" // lalr1.cc:847
    {
		if ((yystack_[0].value.minmax).max < (yystack_[0].value.minmax).min)
		  {
		    error_list.push_back(parse_error(yystack_[0].location, "reversed range"));
		    std::swap((yystack_[0].value.minmax).max, (yystack_[0].value.minmax).min);
		  }
		if ((yystack_[1].value.ltl)->is_boolean())
		  {
		    (yylhs.value.ltl) = bunop::sugar_goto((yystack_[1].value.ltl), (yystack_[0].value.minmax).min, (yystack_[0].value.minmax).max);
		  }
		else
		  {
		    error_list.push_back(parse_error(yystack_[1].location,
				"not a boolean expression: [->...] can only "
				"be applied to a Boolean expression"));
		    error_list.push_back(parse_error(yylhs.location,
				"treating this block as false"));
		    (yystack_[1].value.ltl)->destroy();
		    (yylhs.value.ltl) = constant::false_instance();
		  }
	      }
#line 1457 "ltlparse.cc" // lalr1.cc:847
    break;

  case 76:
#line 580 "ltlparse.yy" // lalr1.cc:847
    {
		if ((yystack_[2].value.ltl)->is_boolean() && (yystack_[0].value.ltl)->is_boolean())
		  {
		    (yylhs.value.ltl) = binop::instance(binop::Xor, (yystack_[2].value.ltl), (yystack_[0].value.ltl));
		  }
		else
		  {
		    if (!(yystack_[2].value.ltl)->is_boolean())
		      {
			error_list.push_back(parse_error(yystack_[2].location,
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    if (!(yystack_[0].value.ltl)->is_boolean())
		      {
			error_list.push_back(parse_error(yystack_[0].location,
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    error_list.push_back(parse_error(yylhs.location,
				"treating this block as false"));
		    (yystack_[2].value.ltl)->destroy();
		    (yystack_[0].value.ltl)->destroy();
		    (yylhs.value.ltl) = constant::false_instance();
		  }
	      }
#line 1488 "ltlparse.cc" // lalr1.cc:847
    break;

  case 77:
#line 607 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "xor operator"); }
#line 1494 "ltlparse.cc" // lalr1.cc:847
    break;

  case 78:
#line 609 "ltlparse.yy" // lalr1.cc:847
    {
		if ((yystack_[2].value.ltl)->is_boolean())
		  {
		    (yylhs.value.ltl) = binop::instance(binop::Implies, (yystack_[2].value.ltl), (yystack_[0].value.ltl));
		  }
		else
		  {
		    if (!(yystack_[2].value.ltl)->is_boolean())
		      {
			error_list.push_back(parse_error(yystack_[2].location,
                         "not a boolean expression: inside SERE `->' can only "
                         "be applied to a Boolean expression"));
                      }
		    error_list.push_back(parse_error(yylhs.location,
				"treating this block as false"));
		    (yystack_[2].value.ltl)->destroy();
		    (yystack_[0].value.ltl)->destroy();
		    (yylhs.value.ltl) = constant::false_instance();
		  }
	      }
#line 1519 "ltlparse.cc" // lalr1.cc:847
    break;

  case 79:
#line 630 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "implication operator"); }
#line 1525 "ltlparse.cc" // lalr1.cc:847
    break;

  case 80:
#line 632 "ltlparse.yy" // lalr1.cc:847
    {
		if ((yystack_[2].value.ltl)->is_boolean() && (yystack_[0].value.ltl)->is_boolean())
		  {
		    (yylhs.value.ltl) = binop::instance(binop::Equiv, (yystack_[2].value.ltl), (yystack_[0].value.ltl));
		  }
		else
		  {
		    if (!(yystack_[2].value.ltl)->is_boolean())
		      {
			error_list.push_back(parse_error(yystack_[2].location,
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    if (!(yystack_[0].value.ltl)->is_boolean())
		      {
			error_list.push_back(parse_error(yystack_[0].location,
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    error_list.push_back(parse_error(yylhs.location,
				"treating this block as false"));
		    (yystack_[2].value.ltl)->destroy();
		    (yystack_[0].value.ltl)->destroy();
		    (yylhs.value.ltl) = constant::false_instance();
		  }
	      }
#line 1556 "ltlparse.cc" // lalr1.cc:847
    break;

  case 81:
#line 659 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "equivalent operator"); }
#line 1562 "ltlparse.cc" // lalr1.cc:847
    break;

  case 82:
#line 662 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = (yystack_[1].value.ltl); }
#line 1568 "ltlparse.cc" // lalr1.cc:847
    break;

  case 83:
#line 664 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[1].location, "ignoring this"));
		(yylhs.value.ltl) = (yystack_[2].value.ltl);
	      }
#line 1576 "ltlparse.cc" // lalr1.cc:847
    break;

  case 84:
#line 668 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
		 "treating this brace block as false"));
		(yylhs.value.ltl) = constant::false_instance();
	      }
#line 1585 "ltlparse.cc" // lalr1.cc:847
    break;

  case 85:
#line 673 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[2].location + yystack_[1].location,
				      "missing closing brace"));
		(yylhs.value.ltl) = (yystack_[1].value.ltl);
	      }
#line 1594 "ltlparse.cc" // lalr1.cc:847
    break;

  case 86:
#line 678 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[1].location,
                "ignoring trailing garbage and missing closing brace"));
		(yylhs.value.ltl) = (yystack_[2].value.ltl);
	      }
#line 1603 "ltlparse.cc" // lalr1.cc:847
    break;

  case 87:
#line 683 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
                    "missing closing brace, "
		    "treating this brace block as false"));
		(yylhs.value.ltl) = constant::false_instance();
	      }
#line 1613 "ltlparse.cc" // lalr1.cc:847
    break;

  case 88:
#line 689 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = try_recursive_parse(*(yystack_[0].value.str), yystack_[0].location, parse_environment,
					 debug_level(),
					 parser_sere, error_list);
		delete (yystack_[0].value.str);
		if (!(yylhs.value.ltl))
		  YYERROR;
	      }
#line 1626 "ltlparse.cc" // lalr1.cc:847
    break;

  case 89:
#line 699 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = try_recursive_parse(*(yystack_[0].value.str), yystack_[0].location, parse_environment,
					 debug_level(), parser_ltl, error_list);
		delete (yystack_[0].value.str);
		if (!(yylhs.value.ltl))
		  YYERROR;
	      }
#line 1638 "ltlparse.cc" // lalr1.cc:847
    break;

  case 90:
#line 707 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = (yystack_[1].value.ltl); }
#line 1644 "ltlparse.cc" // lalr1.cc:847
    break;

  case 91:
#line 709 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[1].location, "ignoring this"));
		(yylhs.value.ltl) = (yystack_[2].value.ltl);
	      }
#line 1652 "ltlparse.cc" // lalr1.cc:847
    break;

  case 92:
#line 713 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
		 "treating this parenthetical block as false"));
		(yylhs.value.ltl) = constant::false_instance();
	      }
#line 1661 "ltlparse.cc" // lalr1.cc:847
    break;

  case 93:
#line 718 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[2].location + yystack_[1].location,
				      "missing closing parenthesis"));
		(yylhs.value.ltl) = (yystack_[1].value.ltl);
	      }
#line 1670 "ltlparse.cc" // lalr1.cc:847
    break;

  case 94:
#line 723 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[1].location,
                "ignoring trailing garbage and missing closing parenthesis"));
		(yylhs.value.ltl) = (yystack_[2].value.ltl);
	      }
#line 1679 "ltlparse.cc" // lalr1.cc:847
    break;

  case 95:
#line 728 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		(yylhs.value.ltl) = constant::false_instance();
	      }
#line 1689 "ltlparse.cc" // lalr1.cc:847
    break;

  case 97:
#line 737 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = try_recursive_parse(*(yystack_[0].value.str), yystack_[0].location, parse_environment,
					 debug_level(), parser_bool, error_list);
		delete (yystack_[0].value.str);
		if (!(yylhs.value.ltl))
		  YYERROR;
	      }
#line 1701 "ltlparse.cc" // lalr1.cc:847
    break;

  case 98:
#line 745 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = (yystack_[1].value.ltl); }
#line 1707 "ltlparse.cc" // lalr1.cc:847
    break;

  case 99:
#line 747 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[1].location, "ignoring this"));
		(yylhs.value.ltl) = (yystack_[2].value.ltl);
	      }
#line 1715 "ltlparse.cc" // lalr1.cc:847
    break;

  case 100:
#line 751 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
		 "treating this parenthetical block as false"));
		(yylhs.value.ltl) = constant::false_instance();
	      }
#line 1724 "ltlparse.cc" // lalr1.cc:847
    break;

  case 101:
#line 756 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[2].location + yystack_[1].location,
				      "missing closing parenthesis"));
		(yylhs.value.ltl) = (yystack_[1].value.ltl);
	      }
#line 1733 "ltlparse.cc" // lalr1.cc:847
    break;

  case 102:
#line 761 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yystack_[1].location,
                "ignoring trailing garbage and missing closing parenthesis"));
		(yylhs.value.ltl) = (yystack_[2].value.ltl);
	      }
#line 1742 "ltlparse.cc" // lalr1.cc:847
    break;

  case 103:
#line 766 "ltlparse.yy" // lalr1.cc:847
    { error_list.push_back(parse_error(yylhs.location,
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		(yylhs.value.ltl) = constant::false_instance();
	      }
#line 1752 "ltlparse.cc" // lalr1.cc:847
    break;

  case 104:
#line 772 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::And, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1758 "ltlparse.cc" // lalr1.cc:847
    break;

  case 105:
#line 774 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "and operator"); }
#line 1764 "ltlparse.cc" // lalr1.cc:847
    break;

  case 106:
#line 776 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::And, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1770 "ltlparse.cc" // lalr1.cc:847
    break;

  case 107:
#line 778 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "and operator"); }
#line 1776 "ltlparse.cc" // lalr1.cc:847
    break;

  case 108:
#line 780 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::And, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1782 "ltlparse.cc" // lalr1.cc:847
    break;

  case 109:
#line 782 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "and operator"); }
#line 1788 "ltlparse.cc" // lalr1.cc:847
    break;

  case 110:
#line 784 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::Or, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1794 "ltlparse.cc" // lalr1.cc:847
    break;

  case 111:
#line 786 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "or operator"); }
#line 1800 "ltlparse.cc" // lalr1.cc:847
    break;

  case 112:
#line 788 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Xor, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1806 "ltlparse.cc" // lalr1.cc:847
    break;

  case 113:
#line 790 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "xor operator"); }
#line 1812 "ltlparse.cc" // lalr1.cc:847
    break;

  case 114:
#line 792 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Implies, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1818 "ltlparse.cc" // lalr1.cc:847
    break;

  case 115:
#line 794 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "implication operator"); }
#line 1824 "ltlparse.cc" // lalr1.cc:847
    break;

  case 116:
#line 796 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Equiv, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1830 "ltlparse.cc" // lalr1.cc:847
    break;

  case 117:
#line 798 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "equivalent operator"); }
#line 1836 "ltlparse.cc" // lalr1.cc:847
    break;

  case 118:
#line 800 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::Not, (yystack_[0].value.ltl)); }
#line 1842 "ltlparse.cc" // lalr1.cc:847
    break;

  case 119:
#line 802 "ltlparse.yy" // lalr1.cc:847
    { missing_right_op((yylhs.value.ltl), yystack_[1].location, "not operator"); }
#line 1848 "ltlparse.cc" // lalr1.cc:847
    break;

  case 122:
#line 807 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::And, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1854 "ltlparse.cc" // lalr1.cc:847
    break;

  case 123:
#line 809 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "and operator"); }
#line 1860 "ltlparse.cc" // lalr1.cc:847
    break;

  case 124:
#line 811 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::And, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1866 "ltlparse.cc" // lalr1.cc:847
    break;

  case 125:
#line 813 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "and operator"); }
#line 1872 "ltlparse.cc" // lalr1.cc:847
    break;

  case 126:
#line 815 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::And, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1878 "ltlparse.cc" // lalr1.cc:847
    break;

  case 127:
#line 817 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "and operator"); }
#line 1884 "ltlparse.cc" // lalr1.cc:847
    break;

  case 128:
#line 819 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::Or, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1890 "ltlparse.cc" // lalr1.cc:847
    break;

  case 129:
#line 821 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "or operator"); }
#line 1896 "ltlparse.cc" // lalr1.cc:847
    break;

  case 130:
#line 823 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Xor, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1902 "ltlparse.cc" // lalr1.cc:847
    break;

  case 131:
#line 825 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "xor operator"); }
#line 1908 "ltlparse.cc" // lalr1.cc:847
    break;

  case 132:
#line 827 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Implies, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1914 "ltlparse.cc" // lalr1.cc:847
    break;

  case 133:
#line 829 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "implication operator"); }
#line 1920 "ltlparse.cc" // lalr1.cc:847
    break;

  case 134:
#line 831 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Equiv, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1926 "ltlparse.cc" // lalr1.cc:847
    break;

  case 135:
#line 833 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "equivalent operator"); }
#line 1932 "ltlparse.cc" // lalr1.cc:847
    break;

  case 136:
#line 835 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::U, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1938 "ltlparse.cc" // lalr1.cc:847
    break;

  case 137:
#line 837 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "until operator"); }
#line 1944 "ltlparse.cc" // lalr1.cc:847
    break;

  case 138:
#line 839 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::R, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1950 "ltlparse.cc" // lalr1.cc:847
    break;

  case 139:
#line 841 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "release operator"); }
#line 1956 "ltlparse.cc" // lalr1.cc:847
    break;

  case 140:
#line 843 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::W, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1962 "ltlparse.cc" // lalr1.cc:847
    break;

  case 141:
#line 845 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "weak until operator"); }
#line 1968 "ltlparse.cc" // lalr1.cc:847
    break;

  case 142:
#line 847 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::M, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 1974 "ltlparse.cc" // lalr1.cc:847
    break;

  case 143:
#line 849 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location, "strong release operator"); }
#line 1980 "ltlparse.cc" // lalr1.cc:847
    break;

  case 144:
#line 851 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::F, (yystack_[0].value.ltl)); }
#line 1986 "ltlparse.cc" // lalr1.cc:847
    break;

  case 145:
#line 853 "ltlparse.yy" // lalr1.cc:847
    { missing_right_op((yylhs.value.ltl), yystack_[1].location, "sometimes operator"); }
#line 1992 "ltlparse.cc" // lalr1.cc:847
    break;

  case 146:
#line 855 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::G, (yystack_[0].value.ltl)); }
#line 1998 "ltlparse.cc" // lalr1.cc:847
    break;

  case 147:
#line 857 "ltlparse.yy" // lalr1.cc:847
    { missing_right_op((yylhs.value.ltl), yystack_[1].location, "always operator"); }
#line 2004 "ltlparse.cc" // lalr1.cc:847
    break;

  case 148:
#line 859 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::X, (yystack_[0].value.ltl)); }
#line 2010 "ltlparse.cc" // lalr1.cc:847
    break;

  case 149:
#line 861 "ltlparse.yy" // lalr1.cc:847
    { missing_right_op((yylhs.value.ltl), yystack_[1].location, "next operator"); }
#line 2016 "ltlparse.cc" // lalr1.cc:847
    break;

  case 150:
#line 863 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::Not, (yystack_[0].value.ltl)); }
#line 2022 "ltlparse.cc" // lalr1.cc:847
    break;

  case 151:
#line 865 "ltlparse.yy" // lalr1.cc:847
    { missing_right_op((yylhs.value.ltl), yystack_[1].location, "not operator"); }
#line 2028 "ltlparse.cc" // lalr1.cc:847
    break;

  case 152:
#line 867 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::Closure, (yystack_[0].value.ltl)); }
#line 2034 "ltlparse.cc" // lalr1.cc:847
    break;

  case 153:
#line 869 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::UConcat, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 2040 "ltlparse.cc" // lalr1.cc:847
    break;

  case 154:
#line 871 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::UConcat, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2046 "ltlparse.cc" // lalr1.cc:847
    break;

  case 155:
#line 873 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop_hard((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location,
				    "universal overlapping concat operator"); }
#line 2053 "ltlparse.cc" // lalr1.cc:847
    break;

  case 156:
#line 876 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::EConcat, (yystack_[2].value.ltl), (yystack_[0].value.ltl)); }
#line 2059 "ltlparse.cc" // lalr1.cc:847
    break;

  case 157:
#line 878 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop_hard((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location,
				    "existential overlapping concat operator");
	      }
#line 2067 "ltlparse.cc" // lalr1.cc:847
    break;

  case 158:
#line 883 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::UConcat,
		       multop::instance(multop::Concat, (yystack_[2].value.ltl),
					constant::true_instance()), (yystack_[0].value.ltl));
	      }
#line 2076 "ltlparse.cc" // lalr1.cc:847
    break;

  case 159:
#line 888 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop_hard((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location,
				  "universal non-overlapping concat operator");
	      }
#line 2084 "ltlparse.cc" // lalr1.cc:847
    break;

  case 160:
#line 893 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::EConcat,
		       multop::instance(multop::Concat, (yystack_[2].value.ltl),
					constant::true_instance()), (yystack_[0].value.ltl));
	      }
#line 2093 "ltlparse.cc" // lalr1.cc:847
    break;

  case 161:
#line 898 "ltlparse.yy" // lalr1.cc:847
    { missing_right_binop_hard((yylhs.value.ltl), (yystack_[2].value.ltl), yystack_[1].location,
				"existential non-overlapping concat operator");
	      }
#line 2101 "ltlparse.cc" // lalr1.cc:847
    break;

  case 162:
#line 903 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::EConcat, (yystack_[1].value.ltl),
				     constant::true_instance()); }
#line 2108 "ltlparse.cc" // lalr1.cc:847
    break;

  case 163:
#line 906 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = try_recursive_parse(*(yystack_[0].value.str), yystack_[0].location, parse_environment,
					 debug_level(), parser_sere, error_list);
		delete (yystack_[0].value.str);
		if (!(yylhs.value.ltl))
		  YYERROR;
		(yylhs.value.ltl) = binop::instance(binop::EConcat, (yylhs.value.ltl),
				     constant::true_instance());
	      }
#line 2122 "ltlparse.cc" // lalr1.cc:847
    break;

  case 164:
#line 917 "ltlparse.yy" // lalr1.cc:847
    {
		(yylhs.value.ltl) = parse_environment.require(*(yystack_[0].value.str));
		if (! (yylhs.value.ltl))
		  {
		    std::string s = "atomic proposition `";
		    s += *(yystack_[0].value.str);
		    s += "' rejected by environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error(yystack_[0].location, s));
		    delete (yystack_[0].value.str);
		    YYERROR;
		  }
		else
		  delete (yystack_[0].value.str);
	      }
#line 2143 "ltlparse.cc" // lalr1.cc:847
    break;

  case 165:
#line 934 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::Not, (yystack_[0].value.ltl)); }
#line 2149 "ltlparse.cc" // lalr1.cc:847
    break;

  case 166:
#line 936 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::And, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2155 "ltlparse.cc" // lalr1.cc:847
    break;

  case 167:
#line 938 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = multop::instance(multop::Or, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2161 "ltlparse.cc" // lalr1.cc:847
    break;

  case 168:
#line 940 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Xor, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2167 "ltlparse.cc" // lalr1.cc:847
    break;

  case 169:
#line 942 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Implies, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2173 "ltlparse.cc" // lalr1.cc:847
    break;

  case 170:
#line 944 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::Equiv, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2179 "ltlparse.cc" // lalr1.cc:847
    break;

  case 171:
#line 946 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::X, (yystack_[0].value.ltl)); }
#line 2185 "ltlparse.cc" // lalr1.cc:847
    break;

  case 172:
#line 948 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::F, (yystack_[0].value.ltl)); }
#line 2191 "ltlparse.cc" // lalr1.cc:847
    break;

  case 173:
#line 950 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = unop::instance(unop::G, (yystack_[0].value.ltl)); }
#line 2197 "ltlparse.cc" // lalr1.cc:847
    break;

  case 174:
#line 952 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::U, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2203 "ltlparse.cc" // lalr1.cc:847
    break;

  case 175:
#line 954 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::R, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2209 "ltlparse.cc" // lalr1.cc:847
    break;

  case 176:
#line 956 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::R, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2215 "ltlparse.cc" // lalr1.cc:847
    break;

  case 177:
#line 958 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::W, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2221 "ltlparse.cc" // lalr1.cc:847
    break;

  case 178:
#line 960 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = binop::instance(binop::M, (yystack_[1].value.ltl), (yystack_[0].value.ltl)); }
#line 2227 "ltlparse.cc" // lalr1.cc:847
    break;

  case 179:
#line 962 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = constant::true_instance(); }
#line 2233 "ltlparse.cc" // lalr1.cc:847
    break;

  case 180:
#line 964 "ltlparse.yy" // lalr1.cc:847
    { (yylhs.value.ltl) = constant::false_instance(); }
#line 2239 "ltlparse.cc" // lalr1.cc:847
    break;


#line 2243 "ltlparse.cc" // lalr1.cc:847
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


  const signed char parser::yypact_ninf_ = -122;

  const signed char parser::yytable_ninf_ = -25;

  const short int
  parser::yypact_[] =
  {
      83,   348,    16,   370,   360,     8,   -32,   452,  -122,  -122,
    -122,   459,   500,   507,   530,   558,    -9,  -122,  -122,  -122,
    -122,  -122,  -122,   193,  -122,   404,  -122,  1229,  1229,  1229,
    1229,  1229,  1229,  1229,  1229,  1229,  1229,  1229,  1229,  1229,
    1229,  -122,  -122,  -122,  -122,     9,   580,  -122,   459,  1228,
    -122,  -122,  -122,    18,  -122,  -122,  -122,  -122,  -122,   308,
    -122,  1074,  -122,  1090,  -122,  -122,  -122,   314,  -122,  -122,
      -5,   229,    -2,   266,  -122,  -122,  -122,  -122,  -122,  -122,
    -122,  -122,  -122,  -122,   606,   629,   636,   659,  -122,   687,
     710,   717,   740,   768,   791,   798,   821,   849,   872,   879,
    -122,  -122,  -122,  1229,  1229,  1229,  1229,  1229,  -122,  -122,
    -122,  1229,  1229,  1229,  1229,  1229,  -122,  -122,    -4,  1200,
     274,  -122,    48,    -3,    68,  -122,    56,   -15,  -122,   901,
     927,   953,   979,  1005,  1031,   349,   403,  1057,  1083,  -122,
    -122,  -122,  -122,  -122,     4,   428,  -122,  -122,  1100,  1131,
    1141,  1148,  1153,  1164,  1196,  -122,  -122,  -122,  -122,    10,
    -122,  -122,  -122,  -122,    36,  -122,  -122,  -122,  -122,     5,
    -122,     5,  -122,     5,  -122,     5,  -122,  1324,  -122,  1333,
    -122,    88,  -122,    88,  -122,     5,  -122,     5,  -122,   136,
    -122,   136,  -122,   136,  -122,   136,  -122,   136,  -122,  -122,
    -122,  -122,  -122,  -122,  -122,  -122,  -122,  -122,  -122,  -122,
    -122,  -122,  -122,  -122,    89,    78,    95,  -122,  -122,  -122,
    -122,  1287,  -122,  1305,  -122,   209,  -122,   209,  -122,  1281,
    -122,  1281,   103,   -11,  -122,   106,  -122,    80,   143,   126,
     117,  -122,  1234,  -122,  1281,  -122,  -122,    37,  -122,  -122,
    -122,    85,  -122,    13,  -122,   138,  -122,   138,  -122,   189,
    -122,   189,  -122,  -122,  -122,  -122,  -122,  -122,   134,  -122,
    -122,  -122,  -122,  -122,  -122,   174,   149,   166,  -122,  -122,
    -122,  -122,  -122,   168,  -122,  -122,  -122
  };

  const unsigned char
  parser::yydefact_[] =
  {
       0,     0,     0,     0,     0,     0,     0,     0,    89,    88,
     163,     0,     0,     0,     0,     0,    49,    52,    53,    18,
       5,     3,   120,   152,   121,     0,   164,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,   179,   180,    17,    15,     0,     0,    57,     0,     0,
      39,    40,    42,     0,    13,    11,    41,    73,    54,     0,
      56,     0,    97,     0,     9,     7,    96,     0,     1,    19,
       0,     0,     0,     0,   145,   144,   147,   146,   149,   148,
     151,   150,    51,    50,     0,     0,     0,     0,   154,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       2,     4,   165,     0,     0,     0,     0,     0,   171,   172,
     173,     0,     0,     0,     0,     0,    14,    16,     0,     0,
       0,    55,    25,     0,    20,    23,     0,     0,    43,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,    10,
      12,    75,    72,    74,     0,     0,   119,   118,     0,     0,
       0,     0,     0,     0,     0,     6,     8,    92,    95,     0,
      90,    93,    84,    87,     0,    82,   162,    85,   155,   153,
     157,   156,   159,   158,   161,   160,   129,   128,   131,   130,
     123,   122,   125,   124,   133,   132,   135,   134,   137,   136,
     139,   138,   141,   140,   143,   142,   127,   126,   166,   167,
     168,   169,   170,   174,   175,   176,   177,   178,    59,    61,
      58,    60,    44,    30,    20,     0,     0,    21,    29,    45,
      67,    66,    77,    76,    63,    62,    65,    64,    79,    78,
      81,    80,    25,     0,    46,    25,    35,     0,    20,     0,
       0,    69,    68,    71,    70,   100,   103,     0,    98,   101,
     111,   110,   113,   112,   105,   104,   107,   106,   115,   114,
     117,   116,   109,   108,    91,    94,    83,    86,     0,    27,
      28,    47,    48,    37,    36,    20,     0,     0,    34,    38,
      99,   102,    26,     0,    32,    33,    31
  };

  const short int
  parser::yypgoto_[] =
  {
    -122,  -122,   144,   104,  -121,  -122,    77,    96,  -122,  -122,
     -59,  -122,     2,   -10,    44,   213,   -54,   130,   159
  };

  const short int
  parser::yydefgoto_[] =
  {
      -1,     5,    20,    21,   125,   126,   127,   128,   141,    56,
      57,   143,    22,    59,    23,    24,    67,    25,    45
  };

  const short int
  parser::yytable_[] =
  {
     142,    73,   215,   157,   208,    58,    66,   145,    68,   147,
       6,   162,   245,    58,   142,   239,    69,     6,   264,   122,
      89,    90,    91,    92,    93,    94,    95,    96,    97,    98,
     150,   151,   213,   219,    99,   214,   119,   272,   120,   121,
      82,    83,   154,   158,   209,   280,   163,    60,    58,   266,
      58,    58,   246,   -22,   123,    60,   124,   116,   265,    26,
     142,   142,   142,    66,    19,    66,   -24,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    39,
      40,    41,    42,   212,   267,   281,     1,     2,     3,     4,
      60,   218,    60,    60,   251,   253,   255,   257,   259,   261,
     263,   149,   150,   151,   216,   217,    44,    55,    65,    95,
      96,    97,    98,   269,   154,   274,   276,    99,   275,   221,
     223,   225,   227,   229,   231,   268,   217,   242,   244,   101,
     270,    58,    58,    58,    58,    58,    58,    71,   271,    58,
      58,   273,    75,    77,    79,    81,    43,    54,    64,   117,
      66,    66,    66,    66,    66,    66,    66,    95,    96,    97,
      98,   278,   142,   140,   142,   279,   142,   154,   142,   282,
     142,   156,   142,    60,    60,    60,    60,    60,    60,   277,
     217,    60,    60,   142,   284,   142,   102,   103,   104,   105,
     106,   107,   108,   109,   110,   111,   112,   113,   114,   115,
       7,   285,     8,   286,   148,   149,   150,   151,   152,   153,
     283,   217,   233,   240,   169,   171,   173,   175,   154,   177,
     179,   181,   183,   185,   187,   189,   191,   193,   195,   197,
     159,   234,    84,    85,    86,    87,    88,   160,    50,    51,
      52,    53,   135,   136,    89,    90,    91,    92,    93,    94,
      95,    96,    97,    98,     0,     0,     0,     0,    99,     0,
       0,     0,   198,   199,   200,   201,   202,   164,     0,     0,
     203,   204,   205,   206,   207,   164,     0,   161,     0,   165,
     166,   129,   130,   131,   132,   133,   134,   165,     0,   129,
     130,   131,   132,   133,   134,    50,    51,    52,    53,   135,
     136,     0,     0,    50,    51,    52,    53,   135,   136,     6,
     137,   138,     0,     0,   167,     6,     0,     0,   137,   138,
       0,     0,   167,   129,   130,   131,   132,   133,   134,   148,
     149,   150,   151,   152,   153,     0,     0,    50,    51,    52,
      53,   135,   136,   154,     0,     0,     0,     0,     0,     6,
     232,     0,   137,   138,     0,     7,   139,     8,     9,    10,
      11,     6,   155,     0,     0,     0,     0,    61,     0,    62,
       0,     6,     0,    12,    13,    14,    15,    46,     0,    47,
       9,     0,    48,     0,   -22,   123,     0,   124,    63,     0,
       0,    16,     0,     0,    17,    18,    19,   -24,    49,    50,
      51,    52,    53,    16,   235,     6,    17,    18,    19,     0,
       0,     0,     0,    16,     0,     0,    17,    18,    19,    89,
      90,    91,    92,    93,    94,    95,    96,    97,    98,   247,
       0,     0,     0,    99,     0,     0,   248,     0,   236,   237,
       0,   238,     0,   148,   149,   150,   151,   152,   153,     0,
       0,   -24,   100,    70,     0,     0,     0,   154,     0,     7,
      72,     8,     9,    10,    11,     0,    46,     0,    47,     9,
       0,    48,     0,     0,     0,     0,   249,    12,    13,    14,
      15,     0,     0,     0,     0,     0,     0,    49,    50,    51,
      52,    53,     0,     0,     0,    16,     0,     0,    17,    18,
       0,    74,    16,     0,     0,    17,    18,     7,    76,     8,
       9,    10,    11,     0,     7,     0,     8,     9,    10,    11,
       0,     0,     0,     0,     0,    12,    13,    14,    15,     0,
       0,    78,    12,    13,    14,    15,     0,     7,     0,     8,
       9,    10,    11,    16,     0,     0,    17,    18,     0,     0,
      16,     0,     0,    17,    18,    12,    13,    14,    15,    80,
       0,     0,     0,     0,     0,     7,     0,     8,     9,    10,
      11,     0,     0,    16,     0,     0,    17,    18,     0,     0,
       0,   118,     0,    12,    13,    14,    15,    46,     0,    47,
       9,     0,    48,     0,     0,     0,     0,     0,     0,     0,
       0,    16,     0,     0,    17,    18,     0,   168,    49,    50,
      51,    52,    53,     7,     0,     8,     9,    10,    11,     0,
       0,     0,     0,    16,     0,     0,    17,    18,     0,     0,
     170,    12,    13,    14,    15,     0,     7,   172,     8,     9,
      10,    11,     0,     7,     0,     8,     9,    10,    11,    16,
       0,     0,    17,    18,    12,    13,    14,    15,     0,     0,
     174,    12,    13,    14,    15,     0,     7,     0,     8,     9,
      10,    11,    16,     0,     0,    17,    18,     0,     0,    16,
       0,     0,    17,    18,    12,    13,    14,    15,   176,     0,
       0,     0,     0,     0,     7,     0,     8,     9,    10,    11,
       0,     0,    16,     0,     0,    17,    18,     0,     0,     0,
       0,   178,    12,    13,    14,    15,     0,     7,   180,     8,
       9,    10,    11,     0,     7,     0,     8,     9,    10,    11,
      16,     0,     0,    17,    18,    12,    13,    14,    15,     0,
       0,   182,    12,    13,    14,    15,     0,     7,     0,     8,
       9,    10,    11,    16,     0,     0,    17,    18,     0,     0,
      16,     0,     0,    17,    18,    12,    13,    14,    15,   184,
       0,     0,     0,     0,     0,     7,     0,     8,     9,    10,
      11,     0,     0,    16,     0,     0,    17,    18,     0,     0,
       0,     0,   186,    12,    13,    14,    15,     0,     7,   188,
       8,     9,    10,    11,     0,     7,     0,     8,     9,    10,
      11,    16,     0,     0,    17,    18,    12,    13,    14,    15,
       0,     0,   190,    12,    13,    14,    15,     0,     7,     0,
       8,     9,    10,    11,    16,     0,     0,    17,    18,     0,
       0,    16,     0,     0,    17,    18,    12,    13,    14,    15,
     192,     0,     0,     0,     0,     0,     7,     0,     8,     9,
      10,    11,     0,     0,    16,     0,     0,    17,    18,     0,
       0,     0,     0,   194,    12,    13,    14,    15,     0,     7,
     196,     8,     9,    10,    11,     0,     7,     0,     8,     9,
      10,    11,    16,     0,     0,    17,    18,    12,    13,    14,
      15,     0,   220,     0,    12,    13,    14,    15,    46,     0,
      47,     9,     0,    48,     0,    16,     0,     0,    17,    18,
       0,     0,    16,     0,     0,    17,    18,     0,   222,    49,
      50,    51,    52,    53,    46,     0,    47,     9,     0,    48,
       0,     0,     0,     0,    16,     0,     0,    17,    18,     0,
       0,     0,     0,     0,   224,    49,    50,    51,    52,    53,
      46,     0,    47,     9,     0,    48,     0,     0,     0,     0,
      16,     0,     0,    17,    18,     0,     0,     0,     0,     0,
     226,    49,    50,    51,    52,    53,    46,     0,    47,     9,
       0,    48,     0,     0,     0,     0,    16,     0,     0,    17,
      18,     0,     0,     0,     0,     0,   228,    49,    50,    51,
      52,    53,    46,     0,    47,     9,     0,    48,     0,     0,
       0,     0,    16,     0,     0,    17,    18,     0,     0,     0,
       0,     0,   230,    49,    50,    51,    52,    53,    46,     0,
      47,     9,     0,    48,     0,     0,     0,     0,    16,     0,
       0,    17,    18,     0,     0,     0,     0,     0,   241,    49,
      50,    51,    52,    53,    46,     0,    47,     9,     0,    48,
       0,     0,     0,     0,    16,   144,     0,    17,    18,     0,
       0,    61,     0,    62,   243,    49,    50,    51,    52,    53,
      46,   146,    47,     9,     0,    48,     0,    61,     0,    62,
      16,   250,    63,    17,    18,     0,     0,    61,     0,    62,
       0,    49,    50,    51,    52,    53,     0,    16,    63,     0,
      17,    18,     0,     0,     0,     0,    16,     0,    63,    17,
      18,     0,   252,    16,     0,     0,    17,    18,    61,     0,
      62,     0,   254,    16,     0,     0,    17,    18,    61,   256,
      62,     0,     0,     0,   258,    61,     0,    62,     0,    63,
      61,     0,    62,     0,     0,   260,     0,     0,     0,    63,
       0,    61,     0,    62,    16,     0,    63,    17,    18,     0,
       0,    63,     0,     0,    16,     0,     0,    17,    18,     0,
       0,    16,    63,     0,    17,    18,    16,   262,     0,    17,
      18,     0,     0,    61,     0,    62,     0,    16,   210,     0,
      17,    18,     0,     0,     0,   129,   130,   131,   132,   133,
     134,     0,     0,     0,    63,     0,     0,     0,     0,    50,
      51,    52,    53,   135,   136,    46,     0,    47,     9,    16,
      48,     0,    17,    18,   137,   138,     0,     0,   211,   129,
     130,   131,   132,   133,   134,     0,    49,    50,    51,    52,
      53,     0,     0,    50,    51,    52,    53,   135,   136,     0,
       0,    16,    26,     0,    17,    18,     0,     0,     0,   138,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    39,    40,    41,    42,   129,   130,   131,   132,
     133,   134,     0,   130,   131,   132,     0,     0,     0,     0,
      50,    51,    52,    53,   135,   136,    50,    51,    52,    53,
     135,   136,   131,   132,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,    50,    51,    52,    53,   135,   136,
      90,    91,    92,     0,     0,    95,    96,    97,    98,     0,
      91,    92,     0,    99,    95,    96,    97,    98,     0,     0,
       0,     0,    99
  };

  const short int
  parser::yycheck_[] =
  {
      59,    11,   123,     8,     8,     3,     4,    61,     0,    63,
       1,    13,     8,    11,    73,   136,    48,     1,     8,     1,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      17,    18,    35,    48,    29,    38,    46,    48,    48,    49,
      49,    50,    29,    48,    48,     8,    48,     3,    46,    13,
      48,    49,    48,    35,    36,    11,    38,    48,    48,    43,
     119,   120,   121,    61,    48,    63,    48,    51,    52,    53,
      54,    55,    56,    57,    58,    59,    60,    61,    62,    63,
      64,    65,    66,    35,    48,    48,     3,     4,     5,     6,
      46,    35,    48,    49,   148,   149,   150,   151,   152,   153,
     154,    16,    17,    18,    36,    37,     2,     3,     4,    21,
      22,    23,    24,    35,    29,    35,   237,    29,    38,   129,
     130,   131,   132,   133,   134,    36,    37,   137,   138,    25,
      35,   129,   130,   131,   132,   133,   134,     7,    35,   137,
     138,    35,    12,    13,    14,    15,     2,     3,     4,    45,
     148,   149,   150,   151,   152,   153,   154,    21,    22,    23,
      24,    35,   221,    59,   223,    48,   225,    29,   227,    35,
     229,    67,   231,   129,   130,   131,   132,   133,   134,    36,
      37,   137,   138,   242,    35,   244,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    39,    40,
       7,    35,     9,    35,    15,    16,    17,    18,    19,    20,
      36,    37,   135,   136,    84,    85,    86,    87,    29,    89,
      90,    91,    92,    93,    94,    95,    96,    97,    98,    99,
       1,   135,    39,    40,    41,    42,    23,     8,    29,    30,
      31,    32,    33,    34,    15,    16,    17,    18,    19,    20,
      21,    22,    23,    24,    -1,    -1,    -1,    -1,    29,    -1,
      -1,    -1,   103,   104,   105,   106,   107,     1,    -1,    -1,
     111,   112,   113,   114,   115,     1,    -1,    48,    -1,    13,
      14,    15,    16,    17,    18,    19,    20,    13,    -1,    15,
      16,    17,    18,    19,    20,    29,    30,    31,    32,    33,
      34,    -1,    -1,    29,    30,    31,    32,    33,    34,     1,
      44,    45,    -1,    -1,    48,     1,    -1,    -1,    44,    45,
      -1,    -1,    48,    15,    16,    17,    18,    19,    20,    15,
      16,    17,    18,    19,    20,    -1,    -1,    29,    30,    31,
      32,    33,    34,    29,    -1,    -1,    -1,    -1,    -1,     1,
       1,    -1,    44,    45,    -1,     7,    48,     9,    10,    11,
      12,     1,    48,    -1,    -1,    -1,    -1,     7,    -1,     9,
      -1,     1,    -1,    25,    26,    27,    28,     7,    -1,     9,
      10,    -1,    12,    -1,    35,    36,    -1,    38,    28,    -1,
      -1,    43,    -1,    -1,    46,    47,    48,    48,    28,    29,
      30,    31,    32,    43,     1,     1,    46,    47,    48,    -1,
      -1,    -1,    -1,    43,    -1,    -1,    46,    47,    48,    15,
      16,    17,    18,    19,    20,    21,    22,    23,    24,     1,
      -1,    -1,    -1,    29,    -1,    -1,     8,    -1,    35,    36,
      -1,    38,    -1,    15,    16,    17,    18,    19,    20,    -1,
      -1,    48,    48,     1,    -1,    -1,    -1,    29,    -1,     7,
       1,     9,    10,    11,    12,    -1,     7,    -1,     9,    10,
      -1,    12,    -1,    -1,    -1,    -1,    48,    25,    26,    27,
      28,    -1,    -1,    -1,    -1,    -1,    -1,    28,    29,    30,
      31,    32,    -1,    -1,    -1,    43,    -1,    -1,    46,    47,
      -1,     1,    43,    -1,    -1,    46,    47,     7,     1,     9,
      10,    11,    12,    -1,     7,    -1,     9,    10,    11,    12,
      -1,    -1,    -1,    -1,    -1,    25,    26,    27,    28,    -1,
      -1,     1,    25,    26,    27,    28,    -1,     7,    -1,     9,
      10,    11,    12,    43,    -1,    -1,    46,    47,    -1,    -1,
      43,    -1,    -1,    46,    47,    25,    26,    27,    28,     1,
      -1,    -1,    -1,    -1,    -1,     7,    -1,     9,    10,    11,
      12,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,
      -1,     1,    -1,    25,    26,    27,    28,     7,    -1,     9,
      10,    -1,    12,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    43,    -1,    -1,    46,    47,    -1,     1,    28,    29,
      30,    31,    32,     7,    -1,     9,    10,    11,    12,    -1,
      -1,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,
       1,    25,    26,    27,    28,    -1,     7,     1,     9,    10,
      11,    12,    -1,     7,    -1,     9,    10,    11,    12,    43,
      -1,    -1,    46,    47,    25,    26,    27,    28,    -1,    -1,
       1,    25,    26,    27,    28,    -1,     7,    -1,     9,    10,
      11,    12,    43,    -1,    -1,    46,    47,    -1,    -1,    43,
      -1,    -1,    46,    47,    25,    26,    27,    28,     1,    -1,
      -1,    -1,    -1,    -1,     7,    -1,     9,    10,    11,    12,
      -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,    -1,
      -1,     1,    25,    26,    27,    28,    -1,     7,     1,     9,
      10,    11,    12,    -1,     7,    -1,     9,    10,    11,    12,
      43,    -1,    -1,    46,    47,    25,    26,    27,    28,    -1,
      -1,     1,    25,    26,    27,    28,    -1,     7,    -1,     9,
      10,    11,    12,    43,    -1,    -1,    46,    47,    -1,    -1,
      43,    -1,    -1,    46,    47,    25,    26,    27,    28,     1,
      -1,    -1,    -1,    -1,    -1,     7,    -1,     9,    10,    11,
      12,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,
      -1,    -1,     1,    25,    26,    27,    28,    -1,     7,     1,
       9,    10,    11,    12,    -1,     7,    -1,     9,    10,    11,
      12,    43,    -1,    -1,    46,    47,    25,    26,    27,    28,
      -1,    -1,     1,    25,    26,    27,    28,    -1,     7,    -1,
       9,    10,    11,    12,    43,    -1,    -1,    46,    47,    -1,
      -1,    43,    -1,    -1,    46,    47,    25,    26,    27,    28,
       1,    -1,    -1,    -1,    -1,    -1,     7,    -1,     9,    10,
      11,    12,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,
      -1,    -1,    -1,     1,    25,    26,    27,    28,    -1,     7,
       1,     9,    10,    11,    12,    -1,     7,    -1,     9,    10,
      11,    12,    43,    -1,    -1,    46,    47,    25,    26,    27,
      28,    -1,     1,    -1,    25,    26,    27,    28,     7,    -1,
       9,    10,    -1,    12,    -1,    43,    -1,    -1,    46,    47,
      -1,    -1,    43,    -1,    -1,    46,    47,    -1,     1,    28,
      29,    30,    31,    32,     7,    -1,     9,    10,    -1,    12,
      -1,    -1,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,
      -1,    -1,    -1,    -1,     1,    28,    29,    30,    31,    32,
       7,    -1,     9,    10,    -1,    12,    -1,    -1,    -1,    -1,
      43,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    -1,
       1,    28,    29,    30,    31,    32,     7,    -1,     9,    10,
      -1,    12,    -1,    -1,    -1,    -1,    43,    -1,    -1,    46,
      47,    -1,    -1,    -1,    -1,    -1,     1,    28,    29,    30,
      31,    32,     7,    -1,     9,    10,    -1,    12,    -1,    -1,
      -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,    -1,
      -1,    -1,     1,    28,    29,    30,    31,    32,     7,    -1,
       9,    10,    -1,    12,    -1,    -1,    -1,    -1,    43,    -1,
      -1,    46,    47,    -1,    -1,    -1,    -1,    -1,     1,    28,
      29,    30,    31,    32,     7,    -1,     9,    10,    -1,    12,
      -1,    -1,    -1,    -1,    43,     1,    -1,    46,    47,    -1,
      -1,     7,    -1,     9,     1,    28,    29,    30,    31,    32,
       7,     1,     9,    10,    -1,    12,    -1,     7,    -1,     9,
      43,     1,    28,    46,    47,    -1,    -1,     7,    -1,     9,
      -1,    28,    29,    30,    31,    32,    -1,    43,    28,    -1,
      46,    47,    -1,    -1,    -1,    -1,    43,    -1,    28,    46,
      47,    -1,     1,    43,    -1,    -1,    46,    47,     7,    -1,
       9,    -1,     1,    43,    -1,    -1,    46,    47,     7,     1,
       9,    -1,    -1,    -1,     1,     7,    -1,     9,    -1,    28,
       7,    -1,     9,    -1,    -1,     1,    -1,    -1,    -1,    28,
      -1,     7,    -1,     9,    43,    -1,    28,    46,    47,    -1,
      -1,    28,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,
      -1,    43,    28,    -1,    46,    47,    43,     1,    -1,    46,
      47,    -1,    -1,     7,    -1,     9,    -1,    43,     8,    -1,
      46,    47,    -1,    -1,    -1,    15,    16,    17,    18,    19,
      20,    -1,    -1,    -1,    28,    -1,    -1,    -1,    -1,    29,
      30,    31,    32,    33,    34,     7,    -1,     9,    10,    43,
      12,    -1,    46,    47,    44,    45,    -1,    -1,    48,    15,
      16,    17,    18,    19,    20,    -1,    28,    29,    30,    31,
      32,    -1,    -1,    29,    30,    31,    32,    33,    34,    -1,
      -1,    43,    43,    -1,    46,    47,    -1,    -1,    -1,    45,
      51,    52,    53,    54,    55,    56,    57,    58,    59,    60,
      61,    62,    63,    64,    65,    66,    15,    16,    17,    18,
      19,    20,    -1,    16,    17,    18,    -1,    -1,    -1,    -1,
      29,    30,    31,    32,    33,    34,    29,    30,    31,    32,
      33,    34,    17,    18,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    29,    30,    31,    32,    33,    34,
      16,    17,    18,    -1,    -1,    21,    22,    23,    24,    -1,
      17,    18,    -1,    29,    21,    22,    23,    24,    -1,    -1,
      -1,    -1,    29
  };

  const unsigned char
  parser::yystos_[] =
  {
       0,     3,     4,     5,     6,    68,     1,     7,     9,    10,
      11,    12,    25,    26,    27,    28,    43,    46,    47,    48,
      69,    70,    79,    81,    82,    84,    43,    51,    52,    53,
      54,    55,    56,    57,    58,    59,    60,    61,    62,    63,
      64,    65,    66,    69,    70,    85,     7,     9,    12,    28,
      29,    30,    31,    32,    69,    70,    76,    77,    79,    80,
      81,     7,     9,    28,    69,    70,    79,    83,     0,    48,
       1,    84,     1,    80,     1,    84,     1,    84,     1,    84,
       1,    84,    49,    50,    39,    40,    41,    42,    82,    15,
      16,    17,    18,    19,    20,    21,    22,    23,    24,    29,
      48,    70,    85,    85,    85,    85,    85,    85,    85,    85,
      85,    85,    85,    85,    85,    85,    48,    70,     1,    80,
      80,    80,     1,    36,    38,    71,    72,    73,    74,    15,
      16,    17,    18,    19,    20,    33,    34,    44,    45,    48,
      70,    75,    77,    78,     1,    83,     1,    83,    15,    16,
      17,    18,    19,    20,    29,    48,    70,     8,    48,     1,
       8,    48,    13,    48,     1,    13,    14,    48,     1,    84,
       1,    84,     1,    84,     1,    84,     1,    84,     1,    84,
       1,    84,     1,    84,     1,    84,     1,    84,     1,    84,
       1,    84,     1,    84,     1,    84,     1,    84,    85,    85,
      85,    85,    85,    85,    85,    85,    85,    85,     8,    48,
       8,    48,    35,    35,    38,    71,    36,    37,    35,    48,
       1,    80,     1,    80,     1,    80,     1,    80,     1,    80,
       1,    80,     1,    73,    74,     1,    35,    36,    38,    71,
      73,     1,    80,     1,    80,     8,    48,     1,     8,    48,
       1,    83,     1,    83,     1,    83,     1,    83,     1,    83,
       1,    83,     1,    83,     8,    48,    13,    48,    36,    35,
      35,    35,    48,    35,    35,    38,    71,    36,    35,    48,
       8,    48,    35,    36,    35,    35,    35
  };

  const unsigned char
  parser::yyr1_[] =
  {
       0,    67,    68,    68,    68,    68,    68,    68,    68,    68,
      68,    68,    68,    68,    68,    68,    68,    68,    69,    70,
      71,    71,    72,    72,    73,    73,    74,    74,    74,    74,
      74,    75,    75,    75,    75,    75,    75,    75,    75,    76,
      76,    77,    77,    77,    77,    77,    78,    78,    78,    79,
      79,    79,    79,    79,    80,    80,    80,    80,    80,    80,
      80,    80,    80,    80,    80,    80,    80,    80,    80,    80,
      80,    80,    80,    80,    80,    80,    80,    80,    80,    80,
      80,    80,    81,    81,    81,    81,    81,    81,    81,    82,
      82,    82,    82,    82,    82,    82,    83,    83,    83,    83,
      83,    83,    83,    83,    83,    83,    83,    83,    83,    83,
      83,    83,    83,    83,    83,    83,    83,    83,    83,    83,
      84,    84,    84,    84,    84,    84,    84,    84,    84,    84,
      84,    84,    84,    84,    84,    84,    84,    84,    84,    84,
      84,    84,    84,    84,    84,    84,    84,    84,    84,    84,
      84,    84,    84,    84,    84,    84,    84,    84,    84,    84,
      84,    84,    84,    84,    85,    85,    85,    85,    85,    85,
      85,    85,    85,    85,    85,    85,    85,    85,    85,    85,
      85
  };

  const unsigned char
  parser::yyr2_[] =
  {
       0,     2,     3,     2,     3,     2,     3,     2,     3,     2,
       3,     2,     3,     2,     3,     2,     3,     2,     1,     2,
       1,     2,     0,     1,     0,     1,     4,     3,     3,     2,
       2,     5,     4,     4,     3,     2,     3,     3,     3,     1,
       1,     1,     1,     2,     3,     3,     2,     3,     3,     1,
       2,     2,     1,     1,     1,     2,     1,     1,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     2,     1,     2,     2,     3,     3,     3,     3,
       3,     3,     3,     4,     3,     3,     4,     3,     1,     1,
       3,     4,     3,     3,     4,     3,     1,     1,     3,     4,
       3,     3,     4,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     2,     2,
       1,     1,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     2,     2,     2,     2,     2,     2,
       2,     2,     1,     3,     2,     3,     3,     3,     3,     3,
       3,     3,     3,     1,     1,     2,     3,     3,     3,     3,
       3,     2,     2,     2,     3,     3,     3,     3,     3,     1,
       1
  };



  // YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
  // First, the terminals, then, starting at \a yyntokens_, nonterminals.
  const char*
  const parser::yytname_[] =
  {
  "$end", "error", "$undefined", "\"LTL start marker\"",
  "\"LBT start marker\"", "\"SERE start marker\"",
  "\"BOOLEAN start marker\"", "\"opening parenthesis\"",
  "\"closing parenthesis\"", "\"(...) block\"", "\"{...} block\"",
  "\"{...}! block\"", "\"opening brace\"", "\"closing brace\"",
  "\"closing brace-bang\"", "\"or operator\"", "\"xor operator\"",
  "\"and operator\"", "\"short and operator\"", "\"implication operator\"",
  "\"equivalent operator\"", "\"until operator\"", "\"release operator\"",
  "\"weak until operator\"", "\"strong release operator\"",
  "\"sometimes operator\"", "\"always operator\"", "\"next operator\"",
  "\"not operator\"", "\"star operator\"", "\"bracket star operator\"",
  "\"plus operator\"", "\"opening bracket for star operator\"",
  "\"opening bracket for equal operator\"",
  "\"opening bracket for goto operator\"", "\"closing bracket\"",
  "\"number for square bracket operator\"", "\"unbounded mark\"",
  "\"separator for square bracket operator\"",
  "\"universal concat operator\"", "\"existential concat operator\"",
  "\"universal non-overlapping concat operator\"",
  "\"existential non-overlapping concat operator\"",
  "\"atomic proposition\"", "\"concat operator\"", "\"fusion operator\"",
  "\"constant true\"", "\"constant false\"", "\"end of formula\"",
  "\"negative suffix\"", "\"positive suffix\"", "'!'", "'&'", "'|'", "'^'",
  "'i'", "'e'", "'X'", "'F'", "'G'", "'U'", "'V'", "'R'", "'W'", "'M'",
  "'t'", "'f'", "$accept", "result", "emptyinput", "enderror",
  "OP_SQBKT_SEP_unbounded", "OP_SQBKT_SEP_opt", "error_opt",
  "sqbracketargs", "gotoargs", "kleen_star", "starargs", "equalargs",
  "booleanatom", "sere", "bracedsere", "parenthesedsubformula",
  "boolformula", "subformula", "lbtformula", YY_NULLPTR
  };

#if YYDEBUG
  const unsigned short int
  parser::yyrline_[] =
  {
       0,   244,   244,   248,   253,   258,   260,   264,   269,   274,
     276,   280,   285,   290,   292,   296,   301,   306,   309,   315,
     322,   322,   323,   323,   324,   324,   327,   329,   331,   333,
     335,   339,   341,   343,   345,   347,   349,   351,   355,   360,
     360,   362,   364,   366,   368,   372,   377,   379,   383,   392,
     409,   426,   444,   446,   449,   450,   467,   468,   476,   478,
     483,   488,   494,   496,   499,   501,   504,   506,   508,   510,
     512,   514,   516,   525,   535,   557,   579,   606,   608,   629,
     631,   658,   661,   663,   667,   672,   677,   682,   688,   698,
     706,   708,   712,   717,   722,   727,   735,   736,   744,   746,
     750,   755,   760,   765,   771,   773,   775,   777,   779,   781,
     783,   785,   787,   789,   791,   793,   795,   797,   799,   801,
     804,   805,   806,   808,   810,   812,   814,   816,   818,   820,
     822,   824,   826,   828,   830,   832,   834,   836,   838,   840,
     842,   844,   846,   848,   850,   852,   854,   856,   858,   860,
     862,   864,   866,   868,   870,   872,   875,   877,   881,   887,
     891,   897,   901,   905,   916,   933,   935,   937,   939,   941,
     943,   945,   947,   949,   951,   953,   955,   957,   959,   961,
     963
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
       2,     2,     2,    51,     2,     2,     2,     2,    52,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
      58,    59,     2,     2,     2,     2,     2,    64,     2,     2,
       2,     2,    62,     2,     2,    60,    61,    63,    57,     2,
       2,     2,     2,     2,    54,     2,     2,     2,     2,     2,
       2,    56,    66,     2,     2,    55,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,    65,     2,     2,     2,
       2,     2,     2,     2,    53,     2,     2,     2,     2,     2,
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
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    46,    47,    48,    49,    50
    };
    const unsigned int user_token_number_max_ = 305;
    const token_number_type undef_token_ = 2;

    if (static_cast<int>(t) <= yyeof_)
      return yyeof_;
    else if (static_cast<unsigned int> (t) <= user_token_number_max_)
      return translate_table[t];
    else
      return undef_token_;
  }


} // ltlyy
#line 3095 "ltlparse.cc" // lalr1.cc:1155
#line 967 "ltlparse.yy" // lalr1.cc:1156


void
ltlyy::parser::error(const location_type& location, const std::string& message)
{
  error_list.push_back(parse_error(location, message));
}

namespace spot
{
  namespace ltl
  {
    const formula*
    parse(const std::string& ltl_string,
	  parse_error_list& error_list,
	  environment& env,
	  bool debug, bool lenient)
    {
      const formula* result = 0;
      flex_set_buffer(ltl_string,
		      ltlyy::parser::token::START_LTL,
		      lenient);
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      flex_unset_buffer();
      return result;
    }

    const formula*
    parse_boolean(const std::string& ltl_string,
		  parse_error_list& error_list,
		  environment& env,
		  bool debug, bool lenient)
    {
      const formula* result = 0;
      flex_set_buffer(ltl_string,
		      ltlyy::parser::token::START_BOOL,
		      lenient);
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      flex_unset_buffer();
      return result;
    }

    const formula*
    parse_lbt(const std::string& ltl_string,
	  parse_error_list& error_list,
	  environment& env,
	  bool debug)
    {
      const formula* result = 0;
      flex_set_buffer(ltl_string,
		      ltlyy::parser::token::START_LBT,
		      false);
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      flex_unset_buffer();
      return result;
    }

    const formula*
    parse_sere(const std::string& sere_string,
	       parse_error_list& error_list,
	       environment& env,
	       bool debug,
	       bool lenient)
    {
      const formula* result = 0;
      flex_set_buffer(sere_string,
		      ltlyy::parser::token::START_SERE,
		      lenient);
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      flex_unset_buffer();
      return result;
    }

  }
}

// Local Variables:
// mode: c++
// End:
