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
#define yylex   eltlyylex

// First part of user declarations.

#line 39 "eltlparse.cc" // lalr1.cc:399

# ifndef YY_NULLPTR
#  if defined __cplusplus && 201103L <= __cplusplus
#   define YY_NULLPTR nullptr
#  else
#   define YY_NULLPTR 0
#  endif
# endif

#include "eltlparse.hh"

// User implementation prologue.

#line 53 "eltlparse.cc" // lalr1.cc:407
// Unqualified %code blocks.
#line 84 "eltlparse.yy" // lalr1.cc:408

/* ltlparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::eltl;
using namespace spot::ltl;

namespace spot
{
  namespace eltl
  {
    using namespace spot::ltl::formula_tree;

    /// Alias an existing alias, as in Strong=G(F($0))->G(F($1)),
    /// where F is an alias.
    ///
    /// \param ap The original alias.
    /// \param v The arguments of the new alias.
    static node_ptr
    realias(const node_ptr ap, std::vector<node_ptr> v)
    {
      if (node_atomic* a = dynamic_cast<node_atomic*>(ap.get())) // Do it.
    	return a->i < 0 ? ap : v.at(a->i);

      // Traverse the tree.
      if (node_unop* a = dynamic_cast<node_unop*>(ap.get()))
      {
    	node_unop* res = new node_unop;
	res->op = a->op;
    	res->child = realias(a->child, v);
    	return node_ptr(res);
      }
      if (node_nfa* a = dynamic_cast<node_nfa*>(ap.get()))
      {
    	node_nfa* res = new node_nfa;
    	std::vector<node_ptr>::const_iterator i = a->children.begin();
    	while (i != a->children.end())
    	  res->children.push_back(realias(*i++, v));
    	res->nfa = a->nfa;
    	return node_ptr(res);
      }
      if (node_binop* a = dynamic_cast<node_binop*>(ap.get()))
      {
    	node_binop* res = new node_binop;
    	res->op = a->op;
    	res->lhs = realias(a->lhs, v);
    	res->rhs = realias(a->rhs, v);
    	return node_ptr(res);
      }
      if (node_multop* a = dynamic_cast<node_multop*>(ap.get()))
      {
    	node_multop* res = new node_multop;
    	res->op = a->op;
    	res->lhs = realias(a->lhs, v);
    	res->rhs = realias(a->rhs, v);
    	return node_ptr(res);
      }

      /* Unreachable code.  */
      assert(0);
      return node_ptr(static_cast<node_unop*>(0));
    }
  }
}

#define PARSE_ERROR(Loc, Msg)				\
  pe.list_.push_back					\
    (parse_error(Loc, spair(pe.file_, Msg)))

#define CHECK_EXISTING_NMAP(Loc, Ident)			\
  {							\
    nfamap::const_iterator i = nmap.find(*Ident);	\
    if (i == nmap.end())				\
    {							\
      std::string s = "unknown automaton operator `";	\
      s += *Ident;					\
      s += "'";						\
      PARSE_ERROR(Loc, s);				\
      delete Ident;					\
      YYERROR;						\
    }							\
  }

#define CHECK_ARITY(Loc, Ident, A1, A2)			\
  {							\
    if (A1 != A2)					\
    {							\
      std::ostringstream oss1;				\
      oss1 << A1;					\
      std::ostringstream oss2;				\
      oss2 << A2;					\
							\
      std::string s(*Ident);				\
      s += " is used with ";				\
      s += oss1.str();					\
      s += " arguments, but has an arity of ";		\
      s += oss2.str();					\
      PARSE_ERROR(Loc, s);				\
      delete Ident;					\
      YYERROR;						\
    }							\
  }

#define INSTANCIATE_OP(Name, TypeNode, TypeOp, L, R)	\
  {							\
    TypeNode* res = new TypeNode;			\
    res->op = TypeOp;					\
    res->lhs = formula_tree::node_ptr(L);		\
    res->rhs = formula_tree::node_ptr(R);		\
    Name = res;						\
  }


#line 170 "eltlparse.cc" // lalr1.cc:408


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


namespace eltlyy {
#line 256 "eltlparse.cc" // lalr1.cc:474

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
  parser::parser (spot::eltl::nfamap& nmap_yyarg, spot::eltl::aliasmap& amap_yyarg, spot::eltl::parse_error_list_t &pe_yyarg, spot::ltl::environment &parse_environment_yyarg, const spot::ltl::formula* &result_yyarg)
    :
#if YYDEBUG
      yydebug_ (false),
      yycdebug_ (&std::cerr),
#endif
      nmap (nmap_yyarg),
      amap (amap_yyarg),
      pe (pe_yyarg),
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
            case 3: // "atomic proposition"

#line 240 "eltlparse.yy" // lalr1.cc:599
        { delete (yysym.value.sval); }
#line 479 "eltlparse.cc" // lalr1.cc:599
        break;

      case 4: // "identifier"

#line 240 "eltlparse.yy" // lalr1.cc:599
        { delete (yysym.value.sval); }
#line 486 "eltlparse.cc" // lalr1.cc:599
        break;

      case 29: // subformula

#line 241 "eltlparse.yy" // lalr1.cc:599
        { (yysym.value.fval)->destroy(); }
#line 493 "eltlparse.cc" // lalr1.cc:599
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
            case 3: // "atomic proposition"

#line 243 "eltlparse.yy" // lalr1.cc:617
        { debug_stream() << *(yysym.value.sval); }
#line 520 "eltlparse.cc" // lalr1.cc:617
        break;

      case 4: // "identifier"

#line 243 "eltlparse.yy" // lalr1.cc:617
        { debug_stream() << *(yysym.value.sval); }
#line 527 "eltlparse.cc" // lalr1.cc:617
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
            yyla.type = yytranslate_ (yylex (&yyla.value, &yyla.location, pe));
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
#line 248 "eltlparse.yy" // lalr1.cc:847
    {
	  result = (yystack_[0].value.fval);
	  YYACCEPT;
	}
#line 757 "eltlparse.cc" // lalr1.cc:847
    break;

  case 5:
#line 261 "eltlparse.yy" // lalr1.cc:847
    {
	  (yystack_[1].value.nval)->set_name(*(yystack_[4].value.sval));
          nmap[*(yystack_[4].value.sval)] = nfa::ptr((yystack_[1].value.nval));
	  delete (yystack_[4].value.sval);
        }
#line 767 "eltlparse.cc" // lalr1.cc:847
    break;

  case 6:
#line 267 "eltlparse.yy" // lalr1.cc:847
    {
	  /// Recursivity issues of aliases are handled by a parse error.
	  aliasmap::iterator i = amap.find(*(yystack_[2].value.sval));
	  if (i != amap.end())
	  {
	    std::string s = "`";
	    s += *(yystack_[2].value.sval);
	    s += "' is already aliased";
	    PARSE_ERROR(yystack_[2].location, s);
	    delete (yystack_[2].value.sval);
	    YYERROR;
	  }
	  amap.insert(make_pair(*(yystack_[2].value.sval), formula_tree::node_ptr((yystack_[0].value.pval))));
   	  delete (yystack_[2].value.sval);
   	}
#line 787 "eltlparse.cc" // lalr1.cc:847
    break;

  case 7:
#line 285 "eltlparse.yy" // lalr1.cc:847
    {
	  (yylhs.value.nval) = new nfa;
        }
#line 795 "eltlparse.cc" // lalr1.cc:847
    break;

  case 8:
#line 289 "eltlparse.yy" // lalr1.cc:847
    {
	  (yystack_[3].value.nval)->add_transition((yystack_[2].value.ival), (yystack_[1].value.ival), formula_tree::node_ptr((yystack_[0].value.pval)));
	  (yylhs.value.nval) = (yystack_[3].value.nval);
        }
#line 804 "eltlparse.cc" // lalr1.cc:847
    break;

  case 9:
#line 294 "eltlparse.yy" // lalr1.cc:847
    {
 	  (yystack_[2].value.nval)->set_final((yystack_[0].value.ival));
	  (yylhs.value.nval) = (yystack_[2].value.nval);
        }
#line 813 "eltlparse.cc" // lalr1.cc:847
    break;

  case 10:
#line 301 "eltlparse.yy" // lalr1.cc:847
    {
	  (yylhs.value.bval) = new formula_tree::node_nfa;
	  (yylhs.value.bval)->children.push_back(formula_tree::node_ptr((yystack_[0].value.pval)));
	}
#line 822 "eltlparse.cc" // lalr1.cc:847
    break;

  case 11:
#line 306 "eltlparse.yy" // lalr1.cc:847
    {
	  (yystack_[2].value.bval)->children.push_back(formula_tree::node_ptr((yystack_[0].value.pval)));
	  (yylhs.value.bval) = (yystack_[2].value.bval);
	}
#line 831 "eltlparse.cc" // lalr1.cc:847
    break;

  case 12:
#line 313 "eltlparse.yy" // lalr1.cc:847
    {
	  if ((yystack_[0].value.ival) == -1)
	  {
	    std::string s = "out of range integer";
	    PARSE_ERROR(yystack_[0].location, s);
	    YYERROR;
	  }
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = (yystack_[0].value.ival);
	  (yylhs.value.pval) = res;
	}
#line 847 "eltlparse.cc" // lalr1.cc:847
    break;

  case 13:
#line 325 "eltlparse.yy" // lalr1.cc:847
    {
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = formula_tree::True;
	  (yylhs.value.pval) = res;
	}
#line 857 "eltlparse.cc" // lalr1.cc:847
    break;

  case 14:
#line 331 "eltlparse.yy" // lalr1.cc:847
    {
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = formula_tree::False;
	  (yylhs.value.pval) = res;
	}
#line 867 "eltlparse.cc" // lalr1.cc:847
    break;

  case 15:
#line 337 "eltlparse.yy" // lalr1.cc:847
    {
	  formula_tree::node_unop* res = new formula_tree::node_unop;
	  res->op = unop::Not;
	  res->child = formula_tree::node_ptr((yystack_[0].value.pval));
	  (yylhs.value.pval) = res;
	}
#line 878 "eltlparse.cc" // lalr1.cc:847
    break;

  case 16:
#line 344 "eltlparse.yy" // lalr1.cc:847
    {
	  formula_tree::node_unop* res = new formula_tree::node_unop;
	  res->op = unop::Finish;
	  res->child = formula_tree::node_ptr((yystack_[1].value.pval));
	  (yylhs.value.pval) = res;
	}
#line 889 "eltlparse.cc" // lalr1.cc:847
    break;

  case 17:
#line 351 "eltlparse.yy" // lalr1.cc:847
    {
	  INSTANCIATE_OP((yylhs.value.pval), formula_tree::node_multop, multop::And, (yystack_[2].value.pval), (yystack_[0].value.pval));
	}
#line 897 "eltlparse.cc" // lalr1.cc:847
    break;

  case 18:
#line 355 "eltlparse.yy" // lalr1.cc:847
    {
	  INSTANCIATE_OP((yylhs.value.pval), formula_tree::node_multop, multop::Or, (yystack_[2].value.pval), (yystack_[0].value.pval));
	}
#line 905 "eltlparse.cc" // lalr1.cc:847
    break;

  case 19:
#line 359 "eltlparse.yy" // lalr1.cc:847
    {
	  INSTANCIATE_OP((yylhs.value.pval), formula_tree::node_binop, binop::Xor, (yystack_[2].value.pval), (yystack_[0].value.pval));
	}
#line 913 "eltlparse.cc" // lalr1.cc:847
    break;

  case 20:
#line 363 "eltlparse.yy" // lalr1.cc:847
    {
	  INSTANCIATE_OP((yylhs.value.pval), formula_tree::node_binop, binop::Implies, (yystack_[2].value.pval), (yystack_[0].value.pval));
	}
#line 921 "eltlparse.cc" // lalr1.cc:847
    break;

  case 21:
#line 367 "eltlparse.yy" // lalr1.cc:847
    {
	  INSTANCIATE_OP((yylhs.value.pval), formula_tree::node_binop, binop::Equiv, (yystack_[2].value.pval), (yystack_[0].value.pval));
	}
#line 929 "eltlparse.cc" // lalr1.cc:847
    break;

  case 22:
#line 371 "eltlparse.yy" // lalr1.cc:847
    {
	  aliasmap::const_iterator i = amap.find(*(yystack_[3].value.sval));
	  if (i != amap.end())
	  {
            unsigned arity = formula_tree::arity(i->second);
	    CHECK_ARITY(yystack_[3].location, (yystack_[3].value.sval), (yystack_[1].value.bval)->children.size(), arity);

	    // Hack to return the right type without screwing with the
	    // boost::shared_ptr memory handling by using get for
	    // example. FIXME: Wait for the next version of boost and
	    // modify the %union to handle formula_tree::node_ptr.
	    formula_tree::node_unop* tmp1 = new formula_tree::node_unop;
	    tmp1->op = unop::Not;
	    tmp1->child = realias(i->second, (yystack_[1].value.bval)->children);
	    formula_tree::node_unop* tmp2 = new formula_tree::node_unop;
	    tmp2->op = unop::Not;
	    tmp2->child = formula_tree::node_ptr(tmp1);
	    (yylhs.value.pval) = tmp2;
	    delete (yystack_[1].value.bval);
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP(yystack_[3].location, (yystack_[3].value.sval));
	    nfa::ptr np = nmap[*(yystack_[3].value.sval)];

	    CHECK_ARITY(yystack_[3].location, (yystack_[3].value.sval), (yystack_[1].value.bval)->children.size(), np->arity());
	    (yystack_[1].value.bval)->nfa = np;
	    (yylhs.value.pval) = (yystack_[1].value.bval);
	  }
	  delete (yystack_[3].value.sval);
	}
#line 965 "eltlparse.cc" // lalr1.cc:847
    break;

  case 23:
#line 406 "eltlparse.yy" // lalr1.cc:847
    {
	  (yylhs.value.fval) = parse_environment.require(*(yystack_[0].value.sval));
	  if (!(yylhs.value.fval))
	  {
	    std::string s = "unknown atomic proposition `";
	    s += *(yystack_[0].value.sval);
	    s += "' in environment `";
	    s += parse_environment.name();
	    s += "'";
	    PARSE_ERROR(yystack_[0].location, s);
	    delete (yystack_[0].value.sval);
	    YYERROR;
	  }
	  else
	    delete (yystack_[0].value.sval);
	}
#line 986 "eltlparse.cc" // lalr1.cc:847
    break;

  case 24:
#line 423 "eltlparse.yy" // lalr1.cc:847
    {
	  aliasmap::iterator i = amap.find(*(yystack_[1].value.sval));
	  if (i != amap.end())
	  {
	    CHECK_ARITY(yystack_[2].location, (yystack_[1].value.sval), 2, formula_tree::arity(i->second));
	    automatop::vec v;
	    v.push_back((yystack_[2].value.fval));
	    v.push_back((yystack_[0].value.fval));
	    (yylhs.value.fval) = instanciate(i->second, v);
	    (yystack_[2].value.fval)->destroy();
	    (yystack_[0].value.fval)->destroy();
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP(yystack_[2].location, (yystack_[1].value.sval));
	    nfa::ptr np = nmap[*(yystack_[1].value.sval)];
	    CHECK_ARITY(yystack_[2].location, (yystack_[1].value.sval), 2, np->arity());
	    automatop::vec* v = new automatop::vec;
	    v->push_back((yystack_[2].value.fval));
	    v->push_back((yystack_[0].value.fval));
	    (yylhs.value.fval) = automatop::instance(np, v, false);
	  }
	  delete (yystack_[1].value.sval);
	}
#line 1015 "eltlparse.cc" // lalr1.cc:847
    break;

  case 25:
#line 448 "eltlparse.yy" // lalr1.cc:847
    {
	  aliasmap::iterator i = amap.find(*(yystack_[3].value.sval));
	  if (i != amap.end())
	  {
	    CHECK_ARITY(yystack_[3].location, (yystack_[3].value.sval), (yystack_[1].value.aval)->size(), formula_tree::arity(i->second));
	    (yylhs.value.fval) = instanciate(i->second, *(yystack_[1].value.aval));
	    automatop::vec::iterator it = (yystack_[1].value.aval)->begin();
	    while (it != (yystack_[1].value.aval)->end())
	      (*it++)->destroy();
	    delete (yystack_[1].value.aval);
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP(yystack_[3].location, (yystack_[3].value.sval));
	    nfa::ptr np = nmap[*(yystack_[3].value.sval)];

	    /// Easily handle deletion of $3 when CHECK_ARITY fails.
	    unsigned i = (yystack_[1].value.aval)->size();
	    if ((yystack_[1].value.aval)->size() != np->arity())
	    {
	      automatop::vec::iterator it = (yystack_[1].value.aval)->begin();
	      while (it != (yystack_[1].value.aval)->end())
		(*it++)->destroy();
	      delete (yystack_[1].value.aval);
	    }

	    CHECK_ARITY(yystack_[3].location, (yystack_[3].value.sval), i, np->arity());
	    (yylhs.value.fval) = automatop::instance(np, (yystack_[1].value.aval), false);
	  }
	  delete (yystack_[3].value.sval);
	}
#line 1051 "eltlparse.cc" // lalr1.cc:847
    break;

  case 26:
#line 480 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = constant::true_instance(); }
#line 1057 "eltlparse.cc" // lalr1.cc:847
    break;

  case 27:
#line 482 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = constant::false_instance(); }
#line 1063 "eltlparse.cc" // lalr1.cc:847
    break;

  case 28:
#line 484 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = (yystack_[1].value.fval); }
#line 1069 "eltlparse.cc" // lalr1.cc:847
    break;

  case 29:
#line 486 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = multop::instance(multop::And, (yystack_[2].value.fval), (yystack_[0].value.fval)); }
#line 1075 "eltlparse.cc" // lalr1.cc:847
    break;

  case 30:
#line 488 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = multop::instance(multop::Or, (yystack_[2].value.fval), (yystack_[0].value.fval)); }
#line 1081 "eltlparse.cc" // lalr1.cc:847
    break;

  case 31:
#line 490 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = binop::instance(binop::Xor, (yystack_[2].value.fval), (yystack_[0].value.fval)); }
#line 1087 "eltlparse.cc" // lalr1.cc:847
    break;

  case 32:
#line 492 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = binop::instance(binop::Implies, (yystack_[2].value.fval), (yystack_[0].value.fval)); }
#line 1093 "eltlparse.cc" // lalr1.cc:847
    break;

  case 33:
#line 494 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = binop::instance(binop::Equiv, (yystack_[2].value.fval), (yystack_[0].value.fval)); }
#line 1099 "eltlparse.cc" // lalr1.cc:847
    break;

  case 34:
#line 496 "eltlparse.yy" // lalr1.cc:847
    { (yylhs.value.fval) = unop::instance(unop::Not, (yystack_[0].value.fval)); }
#line 1105 "eltlparse.cc" // lalr1.cc:847
    break;

  case 35:
#line 500 "eltlparse.yy" // lalr1.cc:847
    {
	  (yylhs.value.aval) = new automatop::vec;
	  (yylhs.value.aval)->push_back((yystack_[0].value.fval));
	}
#line 1114 "eltlparse.cc" // lalr1.cc:847
    break;

  case 36:
#line 505 "eltlparse.yy" // lalr1.cc:847
    {
	  (yystack_[2].value.aval)->push_back((yystack_[0].value.fval));
	  (yylhs.value.aval) = (yystack_[2].value.aval);
	}
#line 1123 "eltlparse.cc" // lalr1.cc:847
    break;


#line 1127 "eltlparse.cc" // lalr1.cc:847
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


  const signed char parser::yypact_ninf_ = -27;

  const signed char parser::yytable_ninf_ = -1;

  const signed char
  parser::yypact_[] =
  {
     -27,     5,    45,   -27,    -9,    10,    13,    13,   -27,   -27,
     -27,    80,    13,    47,   -27,     1,    13,    13,    13,    13,
      13,    13,    80,     9,    12,   -27,    65,    16,   -27,   -27,
     -27,    97,   -27,   -27,    89,    92,     3,    32,    32,   -27,
      13,    65,   -27,    65,    41,    65,    65,    65,    65,    65,
      80,    38,    97,    64,    40,    44,   -27,   101,    -8,    68,
     -27,   -27,   -27,    65,   -27,    65,   -27,    97,    97
  };

  const unsigned char
  parser::yydefact_[] =
  {
       3,     0,     0,     1,    23,     0,     0,     0,    26,    27,
       4,     2,     0,     0,    34,     0,     0,     0,     0,     0,
       0,     0,    35,     0,     0,    12,     0,     0,     7,    13,
      14,     6,    28,    24,    30,    31,    29,    32,    33,    25,
       0,     0,    15,     0,     0,     0,     0,     0,     0,     0,
      36,     0,    10,     0,     0,     0,     5,    18,    19,    17,
      20,    21,    22,     0,    16,     0,     9,    11,     8
  };

  const signed char
  parser::yypgoto_[] =
  {
     -27,   -27,   -27,   -27,   -27,   -27,   -26,    24,   -27
  };

  const signed char
  parser::yydefgoto_[] =
  {
      -1,     1,     2,    10,    44,    51,    31,    11,    23
  };

  const unsigned char
  parser::yytable_[] =
  {
      42,    47,    48,    49,    16,     3,    16,    12,    17,    18,
      19,    20,    21,    20,    21,    52,     4,    53,    32,    57,
      58,    59,    60,    61,    13,     6,    39,    40,    41,     7,
      14,    15,    43,     8,     9,    16,    22,    67,     0,    68,
      33,    34,    35,    36,    37,    38,    65,    54,     4,     5,
      66,    24,    25,     0,    55,    62,    63,     6,    56,    26,
       0,     7,    27,    28,    50,     8,     9,    29,    30,    24,
      25,    45,    46,    47,    48,    49,     0,    26,    48,    49,
      27,    64,     0,    16,     0,    29,    30,    17,    18,    19,
      20,    21,    16,     0,     0,    16,     0,    18,    19,    20,
      21,    19,    20,    21,    45,    46,    47,    48,    49,    46,
      47,    48,    49
  };

  const signed char
  parser::yycheck_[] =
  {
      26,     9,    10,    11,     3,     0,     3,    16,     7,     8,
       9,    10,    11,    10,    11,    41,     3,    43,    17,    45,
      46,    47,    48,    49,    14,    12,    17,    18,    16,    16,
       6,     7,    16,    20,    21,     3,    12,    63,    -1,    65,
      16,    17,    18,    19,    20,    21,     6,     6,     3,     4,
       6,     4,     5,    -1,    13,    17,    18,    12,    17,    12,
      -1,    16,    15,    16,    40,    20,    21,    20,    21,     4,
       5,     7,     8,     9,    10,    11,    -1,    12,    10,    11,
      15,    17,    -1,     3,    -1,    20,    21,     7,     8,     9,
      10,    11,     3,    -1,    -1,     3,    -1,     8,     9,    10,
      11,     9,    10,    11,     7,     8,     9,    10,    11,     8,
       9,    10,    11
  };

  const unsigned char
  parser::yystos_[] =
  {
       0,    23,    24,     0,     3,     4,    12,    16,    20,    21,
      25,    29,    16,    14,    29,    29,     3,     7,     8,     9,
      10,    11,    29,    30,     4,     5,    12,    15,    16,    20,
      21,    28,    17,    29,    29,    29,    29,    29,    29,    17,
      18,    16,    28,    16,    26,     7,     8,     9,    10,    11,
      29,    27,    28,    28,     6,    13,    17,    28,    28,    28,
      28,    28,    17,    18,    17,     6,     6,    28,    28
  };

  const unsigned char
  parser::yyr1_[] =
  {
       0,    22,    23,    24,    24,    25,    25,    26,    26,    26,
      27,    27,    28,    28,    28,    28,    28,    28,    28,    28,
      28,    28,    28,    29,    29,    29,    29,    29,    29,    29,
      29,    29,    29,    29,    29,    30,    30
  };

  const unsigned char
  parser::yyr2_[] =
  {
       0,     2,     2,     0,     2,     5,     3,     0,     4,     3,
       1,     3,     1,     1,     1,     2,     4,     3,     3,     3,
       3,     3,     4,     1,     3,     4,     1,     1,     3,     3,
       3,     3,     3,     3,     2,     1,     3
  };



  // YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
  // First, the terminals, then, starting at \a yyntokens_, nonterminals.
  const char*
  const parser::yytname_[] =
  {
  "$end", "error", "$undefined", "\"atomic proposition\"",
  "\"identifier\"", "\"argument\"", "\"state\"", "\"or operator\"",
  "\"xor operator\"", "\"and operator\"", "\"implication operator\"",
  "\"equivalent operator\"", "\"not operator\"", "\"accept\"", "\"=\"",
  "\"finish\"", "\"(\"", "\")\"", "\",\"", "\"end of file\"",
  "\"constant true\"", "\"constant false\"", "$accept", "result",
  "nfa_list", "nfa", "nfa_def", "nfa_arg_list", "nfa_arg", "subformula",
  "arg_list", YY_NULLPTR
  };

#if YYDEBUG
  const unsigned short int
  parser::yyrline_[] =
  {
       0,   247,   247,   256,   257,   260,   266,   285,   288,   293,
     300,   305,   312,   324,   330,   336,   343,   350,   354,   358,
     362,   366,   370,   405,   422,   447,   479,   481,   483,   485,
     487,   489,   491,   493,   495,   499,   504
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
      15,    16,    17,    18,    19,    20,    21
    };
    const unsigned int user_token_number_max_ = 276;
    const token_number_type undef_token_ = 2;

    if (static_cast<int>(t) <= yyeof_)
      return yyeof_;
    else if (static_cast<unsigned int> (t) <= user_token_number_max_)
      return translate_table[t];
    else
      return undef_token_;
  }


} // eltlyy
#line 1593 "eltlparse.cc" // lalr1.cc:1155
#line 511 "eltlparse.yy" // lalr1.cc:1156


void
eltlyy::parser::error(const location_type& loc, const std::string& s)
{
  PARSE_ERROR(loc, s);
}

namespace spot
{
  namespace eltl
  {
    const formula*
    parse_file(const std::string& name,
	       parse_error_list& error_list,
	       environment& env,
	       bool debug)
    {
      if (flex_open(name))
      {
	error_list.push_back
	  (parse_error(spot::location(),
		       spair("-", std::string("Cannot open file ") + name)));
	return 0;
      }
      const formula* result = 0;
      nfamap nmap;
      aliasmap amap;
      parse_error_list_t pe;
      pe.file_ = name;
      eltlyy::parser parser(nmap, amap, pe, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      error_list = pe.list_;
      flex_close();
      return result;
    }

    const formula*
    parse_string(const std::string& eltl_string,
		 parse_error_list& error_list,
		 environment& env,
		 bool debug)
    {
      flex_set_buffer(eltl_string);
      const formula* result = 0;
      nfamap nmap;
      aliasmap amap;
      parse_error_list_t pe;
      eltlyy::parser parser(nmap, amap, pe, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      error_list = pe.list_;
      flex_unset_buffer();
      return result;
    }
  }
}

// Local Variables:
// mode: c++
// End:
