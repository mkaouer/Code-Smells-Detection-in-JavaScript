<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<tagfile>
  <compound kind="file">
    <name>allnodes.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>allnodes_8hh</filename>
    <includes id="binop_8hh" name="binop.hh" local="yes" imported="no">binop.hh</includes>
    <includes id="unop_8hh" name="unop.hh" local="yes" imported="no">unop.hh</includes>
    <includes id="multop_8hh" name="multop.hh" local="yes" imported="no">multop.hh</includes>
    <includes id="atomic__prop_8hh" name="atomic_prop.hh" local="yes" imported="no">atomic_prop.hh</includes>
    <includes id="constant_8hh" name="constant.hh" local="yes" imported="no">constant.hh</includes>
    <includes id="automatop_8hh" name="automatop.hh" local="yes" imported="no">automatop.hh</includes>
    <includes id="bunop_8hh" name="bunop.hh" local="yes" imported="no">bunop.hh</includes>
  </compound>
  <compound kind="file">
    <name>atomic_prop.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>atomic__prop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <class kind="class">spot::ltl::atomic_prop</class>
  </compound>
  <compound kind="file">
    <name>automatop.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>automatop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <includes id="nfa_8hh" name="nfa.hh" local="yes" imported="no">nfa.hh</includes>
    <class kind="class">spot::ltl::automatop</class>
    <class kind="struct">spot::ltl::automatop::tripletcmp</class>
  </compound>
  <compound kind="file">
    <name>binop.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>binop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <class kind="class">spot::ltl::binop</class>
  </compound>
  <compound kind="file">
    <name>bunop.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>bunop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <includes id="constant_8hh" name="constant.hh" local="yes" imported="no">constant.hh</includes>
    <class kind="class">spot::ltl::bunop</class>
  </compound>
  <compound kind="file">
    <name>constant.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>constant_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">formula.hh</includes>
    <class kind="class">spot::ltl::constant</class>
  </compound>
  <compound kind="file">
    <name>formula.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>formula_8hh</filename>
    <includes id="predecl_8hh" name="predecl.hh" local="yes" imported="no">predecl.hh</includes>
    <class kind="class">spot::ltl::formula</class>
    <class kind="struct">spot::ltl::formula::ltl_prop</class>
    <class kind="struct">spot::ltl::formula_ptr_less_than</class>
    <class kind="struct">spot::ltl::formula_ptr_less_than_bool_first</class>
    <class kind="struct">spot::ltl::formula_ptr_hash</class>
  </compound>
  <compound kind="file">
    <name>formula_tree.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>formula__tree_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">formula.hh</includes>
    <includes id="multop_8hh" name="multop.hh" local="yes" imported="no">multop.hh</includes>
    <includes id="binop_8hh" name="binop.hh" local="yes" imported="no">binop.hh</includes>
    <includes id="unop_8hh" name="unop.hh" local="yes" imported="no">unop.hh</includes>
    <includes id="nfa_8hh" name="nfa.hh" local="yes" imported="no">nfa.hh</includes>
    <class kind="struct">spot::ltl::formula_tree::node</class>
    <class kind="struct">spot::ltl::formula_tree::node_unop</class>
    <class kind="struct">spot::ltl::formula_tree::node_binop</class>
    <class kind="struct">spot::ltl::formula_tree::node_multop</class>
    <class kind="struct">spot::ltl::formula_tree::node_nfa</class>
    <class kind="struct">spot::ltl::formula_tree::node_atomic</class>
    <namespace>spot::ltl::formula_tree</namespace>
    <member kind="typedef">
      <type>boost::shared_ptr&lt; node &gt;</type>
      <name>node_ptr</name>
      <anchorfile>namespacespot_1_1ltl_1_1formula__tree.html</anchorfile>
      <anchor>aca5141e136bf68845459fe16cee595a9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>instanciate</name>
      <anchorfile>namespacespot_1_1ltl_1_1formula__tree.html</anchorfile>
      <anchor>a15921c6ac098624c6194659ce7256a80</anchor>
      <arglist>(const node_ptr np, const std::vector&lt; const formula * &gt; &amp;v)</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>arity</name>
      <anchorfile>namespacespot_1_1ltl_1_1formula__tree.html</anchorfile>
      <anchor>af67581a7e99a31f157617cd73ff5f1a2</anchor>
      <arglist>(const node_ptr np)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>multop.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>multop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <class kind="class">spot::ltl::multop</class>
    <class kind="struct">spot::ltl::multop::paircmp</class>
  </compound>
  <compound kind="file">
    <name>nfa.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>nfa_8hh</filename>
    <class kind="class">spot::ltl::nfa</class>
    <class kind="struct">spot::ltl::nfa::transition</class>
    <class kind="class">spot::ltl::succ_iterator</class>
    <namespace>spot::ltl::formula_tree</namespace>
  </compound>
  <compound kind="file">
    <name>predecl.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>predecl_8hh</filename>
  </compound>
  <compound kind="file">
    <name>refformula.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>refformula_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">formula.hh</includes>
    <class kind="class">spot::ltl::ref_formula</class>
  </compound>
  <compound kind="file">
    <name>unop.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>unop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <includes id="bunop_8hh" name="bunop.hh" local="yes" imported="no">bunop.hh</includes>
    <class kind="class">spot::ltl::unop</class>
  </compound>
  <compound kind="file">
    <name>visitor.hh</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>visitor_8hh</filename>
    <includes id="predecl_8hh" name="predecl.hh" local="yes" imported="no">predecl.hh</includes>
    <class kind="struct">spot::ltl::visitor</class>
  </compound>
  <compound kind="group">
    <name>ltl</name>
    <title>LTL formulae</title>
    <filename>group__ltl.html</filename>
    <subgroup>ltl_essential</subgroup>
    <subgroup>ltl_ast</subgroup>
    <subgroup>ltl_environment</subgroup>
    <subgroup>ltl_algorithm</subgroup>
  </compound>
  <compound kind="group">
    <name>saba</name>
    <title>SABA (State-based Alternating BÃ¼chi Automata)</title>
    <filename>group__saba.html</filename>
    <subgroup>saba_essentials</subgroup>
    <class kind="class">spot::saba_complement_tgba</class>
  </compound>
  <compound kind="group">
    <name>ta</name>
    <title>TA (Testing Automata)</title>
    <filename>group__ta.html</filename>
    <subgroup>ta_essentials</subgroup>
    <subgroup>ta_representation</subgroup>
    <subgroup>ta_algorithms</subgroup>
  </compound>
  <compound kind="group">
    <name>tgba</name>
    <title>TGBA (Transition-based Generalized Büchi Automata)</title>
    <filename>group__tgba.html</filename>
    <subgroup>kripke</subgroup>
    <subgroup>tgba_essentials</subgroup>
    <subgroup>tgba_representation</subgroup>
    <subgroup>tgba_algorithms</subgroup>
    <class kind="class">spot::future_conditions_collector</class>
    <class kind="class">spot::tgba_scc</class>
  </compound>
  <compound kind="group">
    <name>emptiness_check_ssp</name>
    <title>Emptiness-check algorithms for SSP</title>
    <filename>group__emptiness__check__ssp.html</filename>
  </compound>
  <compound kind="group">
    <name>tgba_io</name>
    <title>Input/Output of TGBA</title>
    <filename>group__tgba__io.html</filename>
    <subgroup>tgba_dotty</subgroup>
    <class kind="struct">spot::dstar_aut</class>
    <member kind="typedef">
      <type>std::pair&lt; spot::location, std::string &gt;</type>
      <name>dstar_parse_error</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga81bb81ddadf0bebe6ec470adb2edde9c</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; dstar_parse_error &gt;</type>
      <name>dstar_parse_error_list</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga283a222f073c8d804863c9cb6960c192</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; spot::location, std::string &gt;</type>
      <name>neverclaim_parse_error</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga79cac3332a7695d6cec2dd95540f5bbb</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; neverclaim_parse_error &gt;</type>
      <name>neverclaim_parse_error_list</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga15242b071d7c485804548ff2ee41b3d1</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; spot::location, std::string &gt;</type>
      <name>tgba_parse_error</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga85ffc4af8e1053fb9e1ea998442c50c4</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; tgba_parse_error &gt;</type>
      <name>tgba_parse_error_list</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga76b22bb081d5c36378098caa5bf58081</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>SPOT_API dstar_aut *</type>
      <name>dstar_parse</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga3d535656b5ce0d358ebf8574e4d4b7b5</anchor>
      <arglist>(const std::string &amp;filename, dstar_parse_error_list &amp;error_list, bdd_dict *dict, ltl::environment &amp;env=ltl::default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>format_dstar_parse_errors</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gacb42242af17b5ed4d1331c0f5f134683</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;filename, dstar_parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>nra_to_nba</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga2b1cf8e84814665802a4ea8ddea80ba4</anchor>
      <arglist>(const dstar_aut *nra)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>nra_to_nba</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gac6a6a7b21044c58bb9c31778c40307c7</anchor>
      <arglist>(const dstar_aut *nra, const state_set *ignore)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>dra_to_ba</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gac7efb588643cba8d8f01e88827ca4b67</anchor>
      <arglist>(const dstar_aut *dra, bool *dba_output=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>nsa_to_tgba</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga326d25f6ff8c6222aafd95b9add2c1fa</anchor>
      <arglist>(const dstar_aut *nra)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>dstar_to_tgba</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gaaff54adf9af6d6d018a0de93919aaa55</anchor>
      <arglist>(const dstar_aut *dstar)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>kripke_save_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gaf4a35fe25eef24ba32e103935c5d7575</anchor>
      <arglist>(std::ostream &amp;os, const kripke *k)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>kripke_save_reachable_renumbered</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga8ca394bab4e4feed811cb1ca9d6d4e3d</anchor>
      <arglist>(std::ostream &amp;os, const kripke *k)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_explicit_string *</type>
      <name>neverclaim_parse</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gae677831cadecd47d995be9e3d3e2c9a4</anchor>
      <arglist>(const std::string &amp;filename, neverclaim_parse_error_list &amp;error_list, bdd_dict *dict, ltl::environment &amp;env=ltl::default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>format_neverclaim_parse_errors</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga3589b19e6d27b6462058788072bc6595</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;filename, neverclaim_parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>dotty_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga286828d03780223723fd366e5531afa8</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g, bool assume_sba=false, dotty_decorator *dd=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>hoaf_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gad4439b97532e3b11a20d05a77fe56c86</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g, const ltl::formula *f=0, hoaf_acceptance acceptance=Hoaf_Acceptance_States, hoaf_alias alias=Hoaf_Alias_None, bool newlines=true)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>lbtt_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga3348c2ea02cbee1e95c09ed7403247c4</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g, bool sba=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const tgba *</type>
      <name>lbtt_parse</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga715a95f2850dc2e4702592143c7f0eed</anchor>
      <arglist>(std::istream &amp;is, std::string &amp;error, bdd_dict *dict, ltl::environment &amp;env=ltl::default_environment::instance(), ltl::environment &amp;envacc=ltl::default_environment::instance())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>never_claim_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga3df247eddf647ea45067ee90ee8ff7f2</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g, const ltl::formula *f=0, bool comments=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>tgba_save_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga996e8da26c7aa19ca0be74bf52bfca82</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_explicit_string *</type>
      <name>tgba_parse</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gaee37430bef0cf6d8aa938f6cfc3529f8</anchor>
      <arglist>(const std::string &amp;filename, tgba_parse_error_list &amp;error_list, bdd_dict *dict, ltl::environment &amp;env=ltl::default_environment::instance(), ltl::environment &amp;envacc=ltl::default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>format_tgba_parse_errors</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>ga04549fddc118c02da91244eaaa883756</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;filename, tgba_parse_error_list &amp;error_list)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ltl_io</name>
    <title>Input/Output of LTL formulae</title>
    <filename>group__ltl__io.html</filename>
    <class kind="class">spot::ltl::ltl_file</class>
    <class kind="class">spot::ltl::random_formula</class>
    <class kind="class">spot::ltl::random_ltl</class>
    <class kind="class">spot::ltl::random_boolean</class>
    <class kind="class">spot::ltl::random_sere</class>
    <class kind="class">spot::ltl::random_psl</class>
    <member kind="typedef">
      <type>std::pair&lt; spot::location, spair &gt;</type>
      <name>parse_error</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga22bfe02649dde06a6eba252ed16a6ff1</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; parse_error &gt;</type>
      <name>parse_error_list</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga017167786b87df9a15ec651897ed5cb0</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; location, std::string &gt;</type>
      <name>parse_error</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gad9d5ceb0eb2188cf2db664032f86d392</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; parse_error &gt;</type>
      <name>parse_error_list</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga9eb0f7867a212f92b0fd64a6ac5a12cd</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>parse_file</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gaa1b344ecd592830c46ddc0b5414f2967</anchor>
      <arglist>(const std::string &amp;filename, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>parse_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga0b21d7e34c784bc290f23fe80fcd38c4</anchor>
      <arglist>(const std::string &amp;eltl_string, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>format_parse_errors</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga9d6d5997acc20b7f078b92a7f7209675</anchor>
      <arglist>(std::ostream &amp;os, parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>parse</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gacd1c74572de8d1addfe5f45a239a01cb</anchor>
      <arglist>(const std::string &amp;ltl_string, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false, bool lenient=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>parse_boolean</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga8e3c68fad68a6d23ab1c03ff8e0dba65</anchor>
      <arglist>(const std::string &amp;ltl_string, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false, bool lenient=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>parse_lbt</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gac8e6e121a6da9c2f82c4ec6cef0fec67</anchor>
      <arglist>(const std::string &amp;ltl_string, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>parse_sere</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gad3d88b783babf37a53f62df9d29a92a2</anchor>
      <arglist>(const std::string &amp;sere_string, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false, bool lenient=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>format_parse_errors</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gadd1bb9e7c7a13b11cb337cc9410b32e9</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;input_string, const parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>fix_utf8_locations</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga798851c1bb918f63265bce356479bca4</anchor>
      <arglist>(const std::string &amp;input_string, parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>dotty</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga7687f551ce0e287e81e4152002ae008c</anchor>
      <arglist>(std::ostream &amp;os, const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>dump</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga410bc3eaed909f4c0687dc5c449907db</anchor>
      <arglist>(std::ostream &amp;os, const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>to_lbt_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga00941055abdb0a5f4461c9650b99d21d</anchor>
      <arglist>(const formula *f, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::string</type>
      <name>to_lbt_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga801538d6b1933c49f203c67a2f1050c7</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>to_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga54f0da54c8798afb01b9f2aa605f6b21</anchor>
      <arglist>(const formula *f, std::ostream &amp;os, bool full_parent=false, bool ratexp=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::string</type>
      <name>to_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga710ceff49a60300bd9ed99e40ac342cd</anchor>
      <arglist>(const formula *f, bool full_parent=false, bool ratexp=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>to_utf8_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gaf13cc1ab08f063e304f74fe99a2d5326</anchor>
      <arglist>(const formula *f, std::ostream &amp;os, bool full_parent=false, bool ratexp=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::string</type>
      <name>to_utf8_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gaa74a6ae7206b51fcc1fa109131dec858</anchor>
      <arglist>(const formula *f, bool full_parent=false, bool ratexp=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>to_spin_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gadf1e905621e01320f1e55146cb3cf83d</anchor>
      <arglist>(const formula *f, std::ostream &amp;os, bool full_parent=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::string</type>
      <name>to_spin_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gafadea056965374010e41609c32f2be8b</anchor>
      <arglist>(const formula *f, bool full_parent=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>to_wring_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga194bbfda410b89ca616f91b265c37441</anchor>
      <arglist>(const formula *f, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::string</type>
      <name>to_wring_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gab44eff34258eaf361ee69e0fe12dd162</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>to_latex_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>ga9ff219c5277168251c15af97f40cc75b</anchor>
      <arglist>(const formula *f, std::ostream &amp;os, bool full_parent=false, bool ratexp=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::string</type>
      <name>to_latex_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gabeb2be24ed5fb5c1607eb0d8c86b299d</anchor>
      <arglist>(const formula *f, bool full_parent=false, bool ratexp=false)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>kripke</name>
    <title>Kripke Structures</title>
    <filename>group__kripke.html</filename>
    <class kind="class">spot::fair_kripke_succ_iterator</class>
    <class kind="class">spot::fair_kripke</class>
    <class kind="class">spot::kripke_succ_iterator</class>
    <class kind="class">spot::kripke</class>
  </compound>
  <compound kind="group">
    <name>ltl_essential</name>
    <title>Essential LTL types</title>
    <filename>group__ltl__essential.html</filename>
    <class kind="class">spot::ltl::formula</class>
    <class kind="struct">spot::ltl::visitor</class>
    <class kind="class">spot::ltl::environment</class>
    <member kind="function">
      <type>SPOT_API SPOT_DEPRECATED const formula *</type>
      <name>clone</name>
      <anchorfile>group__ltl__essential.html</anchorfile>
      <anchor>ga512cb6df40718878c9488955ecd06148</anchor>
      <arglist>(const formula *f) __attribute__((deprecated))</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API SPOT_DEPRECATED void</type>
      <name>destroy</name>
      <anchorfile>group__ltl__essential.html</anchorfile>
      <anchor>ga6fb5b0eeba3732317475bf9d9fdeb119</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ltl_ast</name>
    <title>LTL Abstract Syntax Tree</title>
    <filename>group__ltl__ast.html</filename>
    <class kind="class">spot::ltl::atomic_prop</class>
    <class kind="class">spot::ltl::binop</class>
    <class kind="class">spot::ltl::bunop</class>
    <class kind="class">spot::ltl::constant</class>
    <class kind="class">spot::ltl::formula</class>
    <class kind="class">spot::ltl::multop</class>
    <class kind="class">spot::ltl::ref_formula</class>
    <class kind="class">spot::ltl::unop</class>
  </compound>
  <compound kind="group">
    <name>ltl_environment</name>
    <title>LTL environments</title>
    <filename>group__ltl__environment.html</filename>
    <class kind="class">spot::ltl::declarative_environment</class>
    <class kind="class">spot::ltl::default_environment</class>
  </compound>
  <compound kind="group">
    <name>ltl_algorithm</name>
    <title>Algorithms for LTL formulae</title>
    <filename>group__ltl__algorithm.html</filename>
    <subgroup>ltl_io</subgroup>
    <subgroup>ltl_visitor</subgroup>
    <subgroup>ltl_rewriting</subgroup>
    <subgroup>ltl_misc</subgroup>
  </compound>
  <compound kind="group">
    <name>ltl_visitor</name>
    <title>Derivable visitors</title>
    <filename>group__ltl__visitor.html</filename>
    <class kind="class">spot::ltl::clone_visitor</class>
    <class kind="class">spot::ltl::unabbreviate_logic_visitor</class>
    <class kind="class">spot::ltl::postfix_visitor</class>
    <class kind="class">spot::ltl::simplify_f_g_visitor</class>
    <class kind="class">spot::ltl::unabbreviate_ltl_visitor</class>
  </compound>
  <compound kind="group">
    <name>ltl_rewriting</name>
    <title>Rewriting LTL formulae</title>
    <filename>group__ltl__rewriting.html</filename>
    <class kind="class">spot::ltl::ltl_simplifier</class>
    <member kind="enumeration">
      <type></type>
      <name>reduce_options</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>gac9e66395d0e9cb870fa7b1ca208b70ca</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_None</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ggac9e66395d0e9cb870fa7b1ca208b70caabff3607cc02f12d6756d0244a8f5464a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Basics</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ggac9e66395d0e9cb870fa7b1ca208b70caab83ef042ab620af2f258a817e95f8f80</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Syntactic_Implications</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ggac9e66395d0e9cb870fa7b1ca208b70caa22d75bbadb5b030981574ae49668ad94</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Eventuality_And_Universality</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ggac9e66395d0e9cb870fa7b1ca208b70caaabb627af73b5817a542506be482f396d</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Containment_Checks</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ggac9e66395d0e9cb870fa7b1ca208b70caa22286d57705e7511f13a75c05ac0a39f</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Containment_Checks_Stronger</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ggac9e66395d0e9cb870fa7b1ca208b70caa0721d15d048b11cfe234f14850dbc9c5</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_All</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ggac9e66395d0e9cb870fa7b1ca208b70caa1629bc689540d42e2f86eea77a6cd275</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>unabbreviate_logic</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ga0233271eb9ee3ba3d1aad77fbd2c19e9</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>mark_concat_ops</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ga32279d715efca917b5ac55e1262a6a29</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>negative_normal_form</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>gac8b67c51332e56de07547b219e22a3c5</anchor>
      <arglist>(const formula *f, bool negated=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API SPOT_DEPRECATED const formula *</type>
      <name>reduce</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ga106a78cee8f2767e58bb284088ed50fc</anchor>
      <arglist>(const formula *f, int opt=Reduce_All)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>relabel</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ga21152fa93545d8c52d40705b77a7c679</anchor>
      <arglist>(const formula *f, relabeling_style style, relabeling_map *m=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>relabel_bse</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ga187688c46d7030c7618871a1cfd27884</anchor>
      <arglist>(const formula *f, relabeling_style style, relabeling_map *m=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>simplify_f_g</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ga5324f43d1d4101062fdc99c57fc14fb4</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const formula *</type>
      <name>unabbreviate_wm</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ga30990992634fa97920f8b7fa65060838</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ltl_misc</name>
    <title>Miscellaneous algorithms for LTL formulae</title>
    <filename>group__ltl__misc.html</filename>
    <member kind="typedef">
      <type>std::set&lt; const atomic_prop *, formula_ptr_less_than &gt;</type>
      <name>atomic_prop_set</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>ga8347f76c4cd9c56970ba55c8fb40ab1a</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>destroy_atomic_prop_set</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>ga11cfec8184a7f387b2035e13397e12e9</anchor>
      <arglist>(atomic_prop_set &amp;as)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API atomic_prop_set *</type>
      <name>atomic_prop_collect</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>ga61cc7bde72f03e0ab5753c031fdae68b</anchor>
      <arglist>(const formula *f, atomic_prop_set *s=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bdd</type>
      <name>atomic_prop_collect_as_bdd</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>ga12f3e6fd1b6bdb8948a18e205f6003de</anchor>
      <arglist>(const formula *f, const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API int</type>
      <name>length</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>ga19de6fd83a1042696183727a7603f596</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API int</type>
      <name>length_boolone</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>ga095a57900a953b9042cc7b9a6f251606</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API SPOT_DEPRECATED bool</type>
      <name>is_eventual</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gac9fabd75423fcbe4c43f8539c220879f</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API SPOT_DEPRECATED bool</type>
      <name>is_universal</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gaa4be023cc9d346018692ac5241dde44a</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>misc_tools</name>
    <title>Miscellaneous helper algorithms</title>
    <filename>group__misc__tools.html</filename>
    <subgroup>hash_funcs</subgroup>
    <subgroup>random</subgroup>
    <class kind="struct">spot::bdd_less_than</class>
    <class kind="struct">spot::bdd_hash</class>
    <class kind="class">spot::bitvect</class>
    <class kind="class">spot::bitvect_array</class>
    <class kind="struct">spot::char_ptr_less_than</class>
    <class kind="class">spot::minato_isop</class>
    <class kind="class">spot::option_map</class>
    <class kind="struct">spot::time_info</class>
    <class kind="class">spot::timer</class>
    <class kind="class">spot::timer_map</class>
    <class kind="class">spot::temporary_file</class>
    <class kind="class">spot::open_temporary_file</class>
    <member kind="function">
      <type>SPOT_API std::string</type>
      <name>quote_unless_bare_word</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gaf92348ef03c5bfc00ec77a82a0a683a8</anchor>
      <arglist>(const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bitvect *</type>
      <name>make_bitvect</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga121eda4743e57aabb73f440b787bf2a6</anchor>
      <arglist>(size_t bitcount)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bitvect_array *</type>
      <name>make_bitvect_array</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga28fa6cac0ce4b3868b0ca289ed01d2b2</anchor>
      <arglist>(size_t bitcount, size_t vectcount)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>escape_rfc4180</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gae7e9dcdf4a8745bdddc4398d30d4b6fa</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>escape_str</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gae057438056fef44f8fa3743f4184c39f</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::string</type>
      <name>escape_str</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gaee57c95276b1eecbc34950fe02333fb5</anchor>
      <arglist>(const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>trim</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gafc61b1552dc1ff1ad63a0fd087ff1071</anchor>
      <arglist>(std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>int_array_array_compress2</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga5e053c471ab1b17bb5298be02123b576</anchor>
      <arglist>(const int *array, size_t n, int *dest, size_t &amp;dest_size)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>int_array_array_decompress2</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gaf5553cc492bb813eb01ce3c743bf9889</anchor>
      <arglist>(const int *array, size_t array_size, int *res, size_t size)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>int_vector_vector_compress</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gafba5835a530b54e1cbfa0348a55ab1c0</anchor>
      <arglist>(const std::vector&lt; int &gt; &amp;input, std::vector&lt; unsigned int &gt; &amp;output)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>int_vector_vector_decompress</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga4c7a5e82eb892f359072be941ca88880</anchor>
      <arglist>(const std::vector&lt; unsigned int &gt; &amp;array, std::vector&lt; int &gt; &amp;output, size_t size)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const std::vector&lt; unsigned int &gt; *</type>
      <name>int_array_vector_compress</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga8f4b4ef3edcbbfd5b723dc94dee465cb</anchor>
      <arglist>(const int *array, size_t n)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>int_vector_array_decompress</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga27960c38667e73cd38df4514a2ba6e57</anchor>
      <arglist>(const std::vector&lt; unsigned int &gt; *array, int *res, size_t size)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>int_array_array_compress</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga6b9d49d4dd4b4372fb32fa13847fa3a4</anchor>
      <arglist>(const int *array, size_t n, int *dest, size_t &amp;dest_size)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>int_array_array_decompress</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gace61cf73105130f47acf8f82e37d4143</anchor>
      <arglist>(const int *array, size_t array_size, int *res, size_t size)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API temporary_file *</type>
      <name>create_tmpfile</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga586f32723a4df1241b9ab527374e8c82</anchor>
      <arglist>(const char *prefix, const char *suffix=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API open_temporary_file *</type>
      <name>create_open_tmpfile</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga042c543f23409c5723c5e1eaac40ba51</anchor>
      <arglist>(const char *prefix, const char *suffix=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>cleanup_tmpfiles</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga599e3bb80583a8b7277ffcbd857d00c3</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const char *</type>
      <name>version</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>ga1016612fd709ef2157090cc02e7c8601</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>hash_funcs</name>
    <title>Hashing functions</title>
    <filename>group__hash__funcs.html</filename>
    <class kind="struct">spot::ltl::formula_ptr_hash</class>
    <class kind="struct">spot::ptr_hash</class>
    <class kind="struct">spot::identity_hash</class>
    <class kind="struct">spot::saba_state_ptr_hash</class>
    <class kind="struct">spot::saba_state_shared_ptr_hash</class>
    <class kind="struct">spot::state_ptr_hash</class>
    <class kind="struct">spot::state_shared_ptr_hash</class>
    <member kind="function">
      <type>size_t</type>
      <name>wang32_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>ga9422ff0c16df957910dd4a0275d9f726</anchor>
      <arglist>(size_t key)</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>knuth32_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>gaea94dbea4a286b0bde253baf07e7a56e</anchor>
      <arglist>(size_t key)</arglist>
    </member>
    <member kind="typedef">
      <type>Sgi::hash&lt; std::string &gt;</type>
      <name>string_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>ga33960cd9c4aa7bb175efe1debb7534b0</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>random</name>
    <title>Random functions</title>
    <filename>group__random.html</filename>
    <class kind="class">spot::barand</class>
    <member kind="function">
      <type>SPOT_API void</type>
      <name>srand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>gafead3897e111f5f13f5b53efaf0b9a91</anchor>
      <arglist>(unsigned int seed)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API int</type>
      <name>rrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>ga3f667773fdb399fb4b6e888a3fe2ce17</anchor>
      <arglist>(int min, int max)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API int</type>
      <name>mrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>gabe1ebbeb20868bbf5bdc1b65f05ccfb9</anchor>
      <arglist>(int max)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API double</type>
      <name>drand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>ga184e343031b85873cfebdfcf07759abb</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API double</type>
      <name>nrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>gad3670c76d308325a194acd2139382b5d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API double</type>
      <name>bmrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>ga3babd7262a9503d26ad9758e695544f1</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API int</type>
      <name>prand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>gaf8d8d9e0cf65084e95b3dbed311f05a4</anchor>
      <arglist>(double p)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>saba_essentials</name>
    <title>Essential SABA types</title>
    <filename>group__saba__essentials.html</filename>
    <class kind="class">spot::explicit_state_conjunction</class>
    <class kind="class">spot::saba</class>
    <class kind="class">spot::saba_state</class>
    <class kind="struct">spot::saba_state_ptr_less_than</class>
    <class kind="struct">spot::saba_state_ptr_equal</class>
    <class kind="struct">spot::saba_state_ptr_hash</class>
    <class kind="struct">spot::saba_state_shared_ptr_less_than</class>
    <class kind="struct">spot::saba_state_shared_ptr_equal</class>
    <class kind="struct">spot::saba_state_shared_ptr_hash</class>
    <class kind="class">spot::saba_state_conjunction</class>
    <class kind="class">spot::saba_succ_iterator</class>
  </compound>
  <compound kind="group">
    <name>ta_essentials</name>
    <title>Essential TA types</title>
    <filename>group__ta__essentials.html</filename>
    <class kind="class">spot::ta</class>
    <class kind="class">spot::ta_succ_iterator</class>
    <class kind="class">spot::tgta</class>
  </compound>
  <compound kind="group">
    <name>ta_representation</name>
    <title>TA representations</title>
    <filename>group__ta__representation.html</filename>
    <class kind="class">spot::ta_explicit</class>
    <class kind="class">spot::state_ta_explicit</class>
    <class kind="class">spot::tgta_explicit</class>
  </compound>
  <compound kind="group">
    <name>ta_algorithms</name>
    <title>TA algorithms</title>
    <filename>group__ta__algorithms.html</filename>
    <subgroup>ta_io</subgroup>
    <subgroup>tgba_ta</subgroup>
    <subgroup>ta_generic</subgroup>
    <subgroup>ta_reduction</subgroup>
    <subgroup>ta_misc</subgroup>
    <subgroup>ta_emptiness_check</subgroup>
  </compound>
  <compound kind="group">
    <name>ta_io</name>
    <title>Input/Output of TA</title>
    <filename>group__ta__io.html</filename>
  </compound>
  <compound kind="group">
    <name>tgba_ta</name>
    <title>Transforming TGBA into TA</title>
    <filename>group__tgba__ta.html</filename>
    <member kind="function">
      <type>SPOT_API ta_explicit *</type>
      <name>tgba_to_ta</name>
      <anchorfile>group__tgba__ta.html</anchorfile>
      <anchor>ga5e1d4a53f0bce27ba621f3d67ebd774b</anchor>
      <arglist>(const tgba *tgba_to_convert, bdd atomic_propositions_set, bool degeneralized=true, bool artificial_initial_state_mode=true, bool single_pass_emptiness_check=false, bool artificial_livelock_state_mode=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgta_explicit *</type>
      <name>tgba_to_tgta</name>
      <anchorfile>group__tgba__ta.html</anchorfile>
      <anchor>gad242edc9f019c6406675418417f4d57a</anchor>
      <arglist>(const tgba *tgba_to_convert, bdd atomic_propositions_set)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ta_generic</name>
    <title>Algorithm patterns</title>
    <filename>group__ta__generic.html</filename>
    <class kind="class">spot::ta_reachable_iterator</class>
    <class kind="class">spot::ta_reachable_iterator_depth_first</class>
    <class kind="class">spot::ta_reachable_iterator_breadth_first</class>
  </compound>
  <compound kind="group">
    <name>ta_reduction</name>
    <title>TA simplifications</title>
    <filename>group__ta__reduction.html</filename>
    <member kind="function">
      <type>SPOT_API ta *</type>
      <name>minimize_ta</name>
      <anchorfile>group__ta__reduction.html</anchorfile>
      <anchor>ga8f6da0e7504f3d03aa1661c8f6827afc</anchor>
      <arglist>(const ta *ta_)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgta_explicit *</type>
      <name>minimize_tgta</name>
      <anchorfile>group__ta__reduction.html</anchorfile>
      <anchor>gaa6560e75eefbe1cdb46d4ec6f0cde4e4</anchor>
      <arglist>(const tgta_explicit *tgta_)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ta_misc</name>
    <title>Miscellaneous algorithms on TA</title>
    <filename>group__ta__misc.html</filename>
    <class kind="struct">spot::ta_statistics</class>
    <member kind="function">
      <type>SPOT_API ta_statistics</type>
      <name>stats_reachable</name>
      <anchorfile>group__ta__misc.html</anchorfile>
      <anchor>ga47d1b7bcf8f234807bf50979c4c0dee7</anchor>
      <arglist>(const ta *t)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ta_emptiness_check</name>
    <title>Emptiness-checks</title>
    <filename>group__ta__emptiness__check.html</filename>
    <subgroup>ta_emptiness_check_algorithms</subgroup>
    <class kind="class">spot::state_ta_product</class>
    <class kind="class">spot::ta_product</class>
  </compound>
  <compound kind="group">
    <name>ta_emptiness_check_algorithms</name>
    <title>Emptiness-check algorithms</title>
    <filename>group__ta__emptiness__check__algorithms.html</filename>
  </compound>
  <compound kind="group">
    <name>tgba_essentials</name>
    <title>Essential TGBA types</title>
    <filename>group__tgba__essentials.html</filename>
    <class kind="class">spot::bdd_dict</class>
    <class kind="class">spot::sba</class>
    <class kind="class">spot::state</class>
    <class kind="struct">spot::state_ptr_less_than</class>
    <class kind="struct">spot::state_ptr_equal</class>
    <class kind="struct">spot::state_ptr_hash</class>
    <class kind="struct">spot::state_shared_ptr_less_than</class>
    <class kind="struct">spot::state_shared_ptr_equal</class>
    <class kind="struct">spot::state_shared_ptr_hash</class>
    <class kind="class">spot::tgba_succ_iterator</class>
    <class kind="class">spot::tgba</class>
  </compound>
  <compound kind="group">
    <name>tgba_representation</name>
    <title>TGBA representations</title>
    <filename>group__tgba__representation.html</filename>
    <class kind="class">spot::state_bdd</class>
    <class kind="class">spot::tgba_succ_iterator_concrete</class>
    <class kind="class">spot::tgba_bdd_concrete</class>
    <class kind="class">spot::state_explicit</class>
    <class kind="class">spot::state_explicit_number</class>
    <class kind="class">spot::state_explicit_string</class>
    <class kind="class">spot::state_explicit_formula</class>
    <class kind="class">spot::tgba_explicit_succ_iterator</class>
    <class kind="class">spot::explicit_graph</class>
    <class kind="class">spot::explicit_conf</class>
  </compound>
  <compound kind="group">
    <name>tgba_algorithms</name>
    <title>TGBA algorithms</title>
    <filename>group__tgba__algorithms.html</filename>
    <subgroup>tgba_on_the_fly_algorithms</subgroup>
    <subgroup>tgba_io</subgroup>
    <subgroup>tgba_ltl</subgroup>
    <subgroup>tgba_generic</subgroup>
    <subgroup>tgba_reduction</subgroup>
    <subgroup>tgba_misc</subgroup>
    <subgroup>emptiness_check</subgroup>
    <member kind="function">
      <type>SPOT_API tgba_bdd_concrete *</type>
      <name>product</name>
      <anchorfile>group__tgba__algorithms.html</anchorfile>
      <anchor>ga132ce5d7495622bc9be9e2d4ae1c0be5</anchor>
      <arglist>(const tgba_bdd_concrete *left, const tgba_bdd_concrete *right)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_on_the_fly_algorithms</name>
    <title>TGBA on-the-fly algorithms</title>
    <filename>group__tgba__on__the__fly__algorithms.html</filename>
    <class kind="class">spot::tgba_kv_complement</class>
    <class kind="class">spot::tgba_mask</class>
    <class kind="class">spot::state_product</class>
    <class kind="class">spot::tgba_proxy</class>
    <class kind="class">spot::tgba_safra_complement</class>
    <class kind="class">spot::tgba_sgba_proxy</class>
    <class kind="class">spot::tgba_tba_proxy</class>
    <class kind="class">spot::tgba_sba_proxy</class>
    <class kind="class">spot::state_union</class>
    <member kind="function">
      <type>SPOT_API const tgba *</type>
      <name>build_tgba_mask_keep</name>
      <anchorfile>group__tgba__on__the__fly__algorithms.html</anchorfile>
      <anchor>ga31304d0245ab7c85ec9d3aaa7c89abd1</anchor>
      <arglist>(const tgba *to_mask, const state_set &amp;to_keep, const state *init=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API const tgba *</type>
      <name>build_tgba_mask_ignore</name>
      <anchorfile>group__tgba__on__the__fly__algorithms.html</anchorfile>
      <anchor>ga2012a22613d553c4fe8382ae4561c661</anchor>
      <arglist>(const tgba *to_mask, const state_set &amp;to_ignore, const state *init=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>wdba_complement</name>
      <anchorfile>group__tgba__on__the__fly__algorithms.html</anchorfile>
      <anchor>ga055d3aa2b15e12c904e133a48289a1ee</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_ltl</name>
    <title>Translating LTL formulae into TGBA</title>
    <filename>group__tgba__ltl.html</filename>
    <class kind="class">spot::translator</class>
    <member kind="function">
      <type>SPOT_API tgba_bdd_concrete *</type>
      <name>eltl_to_tgba_lacim</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>ga548b9853d9abe3aadf1c906a3de58eb9</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API taa_tgba *</type>
      <name>ltl_to_taa</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>ga6bc612ee59f7732019883bbaf8d33b8d</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict, bool refined_rules=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_explicit_formula *</type>
      <name>ltl_to_tgba_fm</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>gae1efe8b1cc714158bd43d36054b87b2e</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict, bool exprop=false, bool symb_merge=true, bool branching_postponement=false, bool fair_loop_approx=false, const ltl::atomic_prop_set *unobs=0, ltl::ltl_simplifier *simplifier=0)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_bdd_concrete *</type>
      <name>ltl_to_tgba_lacim</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>ga5d847d248aab2b3c4d1ec0ec679ac29e</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_generic</name>
    <title>Algorithm patterns</title>
    <filename>group__tgba__generic.html</filename>
    <class kind="class">spot::tgba_reachable_iterator</class>
    <class kind="class">spot::tgba_reachable_iterator_breadth_first</class>
    <class kind="class">spot::tgba_reachable_iterator_depth_first</class>
    <class kind="class">spot::tgba_reachable_iterator_depth_first_stack</class>
  </compound>
  <compound kind="group">
    <name>tgba_reduction</name>
    <title>TGBA simplifications</title>
    <filename>group__tgba__reduction.html</filename>
    <class kind="class">spot::postprocessor</class>
    <member kind="enumeration">
      <type></type>
      <name>reduce_tgba_options</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>ga5bd08ab74b3ab10a27beceaa04d9217a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_None</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gga5bd08ab74b3ab10a27beceaa04d9217aa28ab8e63b3f476424eec1d49fb19b1e6</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_quotient_Dir_Sim</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gga5bd08ab74b3ab10a27beceaa04d9217aa6ef94353974a1119b8c3265eb0bcbe42</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_transition_Dir_Sim</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gga5bd08ab74b3ab10a27beceaa04d9217aa7b2d15a717f241527e25e1266370033e</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_quotient_Del_Sim</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gga5bd08ab74b3ab10a27beceaa04d9217aa040be3fac90f0ddb426644a52418d7a3</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_transition_Del_Sim</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gga5bd08ab74b3ab10a27beceaa04d9217aa21c74a793193bfe95e63b43ef1b59350</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Scc</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gga5bd08ab74b3ab10a27beceaa04d9217aa685daa8530de270e1588cdc2c178a2ab</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_All</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gga5bd08ab74b3ab10a27beceaa04d9217aa81a405e18e9bbd601c4183e1c7ff49e1</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>SPOT_API sba_explicit_number *</type>
      <name>minimize_monitor</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gafdf4d275a525b2f8d0cf5d6222d22985</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API sba_explicit_number *</type>
      <name>minimize_wdba</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gacd18a79a913433abf90a5545dab9f3aa</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>minimize_obligation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>ga09393dbe7673d04bd47fdf076c3e9aad</anchor>
      <arglist>(const tgba *aut_f, const ltl::formula *f=0, const tgba *aut_neg_f=0, bool reject_bigger=false)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API SPOT_DEPRECATED const tgba *</type>
      <name>reduc_tgba_sim</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>ga8c93e8e23cae8a3eb42a3bc0043aa1da</anchor>
      <arglist>(const tgba *a, int opt=Reduce_All)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>ga6dd7a01b55bed2f0fa489897fadf673d</anchor>
      <arglist>(const tgba *automaton)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>cosimulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gacb51f407ea878e3610b1165664a61e5f</anchor>
      <arglist>(const tgba *automaton)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>iterated_simulations</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>ga764dc4981ee6737fa86bc861bb17f4b3</anchor>
      <arglist>(const tgba *automaton)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_misc</name>
    <title>Miscellaneous algorithms on TGBA</title>
    <filename>group__tgba__misc.html</filename>
    <class kind="class">spot::bfs_steps</class>
    <class kind="struct">spot::tgba_statistics</class>
    <class kind="struct">spot::tgba_sub_statistics</class>
    <class kind="class">spot::printable_formula</class>
    <class kind="class">spot::stat_printer</class>
    <member kind="function">
      <type>SPOT_API sba *</type>
      <name>degeneralize</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gac5a739396d459336fb13a45957a2090a</anchor>
      <arglist>(const tgba *a, bool use_z_lvl=true, bool use_cust_acc_orders=false, int use_lvl_cache=1, bool skip_levels=true)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_explicit_number *</type>
      <name>tgba_dupexp_bfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga61782f7db3ae1ca82378ffaae95ce2fa</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_explicit_number *</type>
      <name>tgba_dupexp_dfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga3492f1f085f2c2ca7508f4673405f22b</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_explicit_number *</type>
      <name>tgba_dupexp_bfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga7a04fc4e9913deb9c42e01e27ee436d6</anchor>
      <arglist>(const tgba *aut, std::map&lt; const state *, const state *, state_ptr_less_than &gt; &amp;relation)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_explicit_number *</type>
      <name>tgba_dupexp_dfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga0b61b3e71348077d0ec8bbabc97599d5</anchor>
      <arglist>(const tgba *aut, std::map&lt; const state *, const state *, state_ptr_less_than &gt; &amp;relation)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API unsigned</type>
      <name>count_nondet_states</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga6fc0abdbe1820c80126aab752d5b1349</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>is_deterministic</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gaa9b9a1dc7e3a28d6994d0ff4bc4ab82a</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>is_complete</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gacf73d9eea80cd34b6dc29adaf314229a</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>is_inherently_weak_scc</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gacedc6cf8955616fc8002a2d3249efb30</anchor>
      <arglist>(scc_map &amp;map, unsigned scc)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>is_weak_scc</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga2dcb54c0ecf6dad403d6e3457448fdf4</anchor>
      <arglist>(scc_map &amp;map, unsigned scc)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>is_complete_scc</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga4110dc986c069b0ad42df9ba548238b7</anchor>
      <arglist>(scc_map &amp;map, unsigned scc)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>is_syntactic_weak_scc</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga9d3b37479f9996c6e157cacb31be05e9</anchor>
      <arglist>(scc_map &amp;map, unsigned scc)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>is_syntactic_terminal_scc</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga3344a5400ec32f1aaa0f003f91634a39</anchor>
      <arglist>(scc_map &amp;map, unsigned scc)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>is_terminal_scc</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gac4b0c59e5185aa88c984fc0be55cc90e</anchor>
      <arglist>(scc_map &amp;map, unsigned scc)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>random_graph</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gaafc01af4b4688e144f4f04ef0a7c59f0</anchor>
      <arglist>(int n, float d, const ltl::atomic_prop_set *ap, bdd_dict *dict, int n_acc=0, float a=0.1, float t=0.5, ltl::environment *env=&amp;ltl::default_environment::instance())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_statistics</type>
      <name>stats_reachable</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gafc41419bd3f2ec839eca737554115928</anchor>
      <arglist>(const tgba *g)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_sub_statistics</type>
      <name>sub_stats_reachable</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gaeb1db3d41f0de65c2d6b93cb46225096</anchor>
      <arglist>(const tgba *g)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API sba_explicit_number *</type>
      <name>strip_acceptance</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga5bf67a06f0f4abcb9ac3f8313b6aa1f2</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>stutterize</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga4d72068a20bcf5b2c8b4bd53bb6d993d</anchor>
      <arglist>(const tgba *a, bdd atomic_propositions)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_explicit_number *</type>
      <name>tgba_powerset</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>ga20c27a27db7186d6fb164ee21ffc0471</anchor>
      <arglist>(const tgba *aut, power_map &amp;pm, bool merge=true)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_dotty</name>
    <title>Decorating the dot output</title>
    <filename>group__tgba__dotty.html</filename>
    <class kind="class">spot::dotty_decorator</class>
    <class kind="class">spot::tgba_run_dotty_decorator</class>
  </compound>
  <compound kind="group">
    <name>emptiness_check</name>
    <title>Emptiness-checks</title>
    <filename>group__emptiness__check.html</filename>
    <subgroup>emptiness_check_ssp</subgroup>
    <subgroup>emptiness_check_algorithms</subgroup>
    <subgroup>tgba_run</subgroup>
    <subgroup>emptiness_check_stats</subgroup>
    <class kind="class">spot::emptiness_check_result</class>
    <class kind="class">spot::emptiness_check</class>
    <class kind="class">spot::emptiness_check_instantiator</class>
  </compound>
  <compound kind="group">
    <name>emptiness_check_algorithms</name>
    <title>Emptiness-check algorithms</title>
    <filename>group__emptiness__check__algorithms.html</filename>
    <class kind="class">spot::couvreur99_check</class>
    <class kind="class">spot::couvreur99_check_shy</class>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>couvreur99</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ga4dfc3881988710d697245055407c14da</anchor>
      <arglist>(const tgba *a, option_map options=option_map(), const numbered_state_heap_factory *nshf=numbered_state_heap_hash_map_factory::instance())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>explicit_gv04_check</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ga03ee089d344d508e71f91d5eb845b798</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>explicit_magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>gab986a45098e3d57d97c8a8d3172c1f12</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>bit_state_hashing_magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>gae2d7c16261d9b057e9ae981b6a036218</anchor>
      <arglist>(const tgba *a, size_t size, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ga62ae5da8c13b07d2123295c9bc203580</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>explicit_se05_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>gadc300b4d5924172eb418aee4b2d5c5a6</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>bit_state_hashing_se05_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ga90fb6bff7456fae04c6afe8ccad28e6e</anchor>
      <arglist>(const tgba *a, size_t size, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>se05</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ga268e8d25071258132088b0962a9c16ea</anchor>
      <arglist>(const tgba *a, option_map o)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>explicit_tau03_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>gaea13787a2be1af9a5b7cec845e822d3b</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API emptiness_check *</type>
      <name>explicit_tau03_opt_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>gadeaeb31458d4987caeb5409bb0e15520</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_run</name>
    <title>TGBA runs and supporting functions</title>
    <filename>group__tgba__run.html</filename>
    <class kind="struct">spot::tgba_run</class>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>print_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>gadd27120d85211db83e9aee1695d2151b</anchor>
      <arglist>(std::ostream &amp;os, const tgba *a, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba *</type>
      <name>tgba_run_to_tgba</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>ga212e09efaf21f81148aa238a956b6b3c</anchor>
      <arglist>(const tgba *a, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_run *</type>
      <name>project_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>ga921ad5b462754c9b17de95f68a95132a</anchor>
      <arglist>(const tgba *a_run, const tgba *a_proj, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API tgba_run *</type>
      <name>reduce_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>gab176735c6663f2896656b77cf2f6f111</anchor>
      <arglist>(const tgba *a, const tgba_run *org)</arglist>
    </member>
    <member kind="function">
      <type>SPOT_API bool</type>
      <name>replay_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>gac791bf842d2902db8af2f796b81a698f</anchor>
      <arglist>(std::ostream &amp;os, const tgba *a, const tgba_run *run, bool debug=false)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>emptiness_check_stats</name>
    <title>Emptiness-check statistics</title>
    <filename>group__emptiness__check__stats.html</filename>
    <class kind="struct">spot::unsigned_statistics</class>
    <class kind="class">spot::unsigned_statistics_copy</class>
    <class kind="class">spot::ec_statistics</class>
    <class kind="class">spot::ars_statistics</class>
    <class kind="class">spot::acss_statistics</class>
  </compound>
  <compound kind="class">
    <name>aut_stat_printer</name>
    <filename>classaut__stat__printer.html</filename>
    <base protection="protected">spot::stat_printer</base>
    <member kind="function">
      <type>bool</type>
      <name>has</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>a3ee523bf70b7d9ba9e350534334e2633</anchor>
      <arglist>(char c) const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>a1fd3752a72f250ba8d73cbbede1d9868</anchor>
      <arglist>(char c, const printable *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set_output</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>a2d321a5ed339b3bf33869dc3c8933e2e</anchor>
      <arglist>(std::ostream &amp;output)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>format</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>a64b02cc1c3804b490463888757be35bb</anchor>
      <arglist>(const char *fmt)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>format</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>a2aec18e8b23e0fff4e6641846e16d67d</anchor>
      <arglist>(std::ostream &amp;output, const char *fmt)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>format</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>a5bae69390fc6e76ceea86eca57cbf888</anchor>
      <arglist>(const std::string &amp;fmt)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>format</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>af6cabca4ec183a6fdf0827575516b43b</anchor>
      <arglist>(std::ostream &amp;output, const std::string &amp;fmt)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>scan</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>a663f4c6946ced8761ca87af81c526bbb</anchor>
      <arglist>(const char *fmt, std::vector&lt; bool &gt; &amp;has) const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>prime</name>
      <anchorfile>classspot_1_1formater.html</anchorfile>
      <anchor>af760bacb505f4262d8ba966993b16253</anchor>
      <arglist>(const char *fmt)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>job</name>
    <filename>structjob.html</filename>
  </compound>
  <compound kind="class">
    <name>job_processor</name>
    <filename>classjob__processor.html</filename>
  </compound>
  <compound kind="class">
    <name>printable_formula</name>
    <filename>classprintable__formula.html</filename>
    <base>printable_value&lt; const spot::ltl::formula * &gt;</base>
  </compound>
  <compound kind="struct">
    <name>range</name>
    <filename>structrange.html</filename>
  </compound>
  <compound kind="class">
    <name>kripkeyy::location</name>
    <filename>classkripkeyy_1_1location.html</filename>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>a480b3dfd7d55bb47c19e13c920d63bfb</anchor>
      <arglist>(const position &amp;b, const position &amp;e)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>aa765e15dcd70ea43c93be5403a1e4c4b</anchor>
      <arglist>(const position &amp;p=position())</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>ae0bd70264c4fee7ac4679fe3e5891653</anchor>
      <arglist>(std::string *f, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>afd8a8f9f2f714acaaa2b467eee446a6e</anchor>
      <arglist>(std::string *f=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>step</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>a618bec446e6aa337765ce6d34af26601</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>a996c7caa5465aae609ccc17892fa1cc3</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>aac8a695321aeb1fa8ca201fede8ff3ae</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>begin</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>a752f2350fe2e8d05af03f81089aeef62</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>end</name>
      <anchorfile>classkripkeyy_1_1location.html</anchorfile>
      <anchor>a1e5336b39c2d801de7bf23b42c717c31</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>kripkeyy::position</name>
    <filename>classkripkeyy_1_1position.html</filename>
    <member kind="function">
      <type></type>
      <name>position</name>
      <anchorfile>classkripkeyy_1_1position.html</anchorfile>
      <anchor>ac86663edb494a4a8871adcbd3ee9dd9d</anchor>
      <arglist>(std::string *f=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classkripkeyy_1_1position.html</anchorfile>
      <anchor>a586c67fd067f687403db3f0fa7791cd0</anchor>
      <arglist>(std::string *fn=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classkripkeyy_1_1position.html</anchorfile>
      <anchor>a5a1476821f0e6c52f787b4195a879c55</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classkripkeyy_1_1position.html</anchorfile>
      <anchor>aa2acf7853b4ae10784fd12e10248db45</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>std::string *</type>
      <name>filename</name>
      <anchorfile>classkripkeyy_1_1position.html</anchorfile>
      <anchor>ad768b7da99747f6bdc2eaec7882ab38d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>line</name>
      <anchorfile>classkripkeyy_1_1position.html</anchorfile>
      <anchor>acc0b7ca8ac697ffc7c72198a946f0086</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>column</name>
      <anchorfile>classkripkeyy_1_1position.html</anchorfile>
      <anchor>ab0a2181ebac3b2d484ed08cef3e4df68</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>ltlyy::location</name>
    <filename>classltlyy_1_1location.html</filename>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>a2c766d7554644521fe3100d4d8d46306</anchor>
      <arglist>(const position &amp;b, const position &amp;e)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>a0eb76f7fee7d386ca2e9c36b87cf516a</anchor>
      <arglist>(const position &amp;p=position())</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>aea073bf3232101ad1ceeaf6db7e5677d</anchor>
      <arglist>(std::string *f, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>aec5a3d7fe6e1a7007db451b5fbd1e190</anchor>
      <arglist>(std::string *f=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>step</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>a0cb10e032ede2952ebff291bf11b3654</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>adf146e08e5fffd4dab9862c0f6a310bc</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>a8fcb821f469792c4e0de5fd5049e7f6d</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>begin</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>aadd64beae4835b78c359f97683155317</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>end</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>a6750a25f8b82b02801327952ca942bc5</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>ltlyy::position</name>
    <filename>classltlyy_1_1position.html</filename>
    <member kind="function">
      <type></type>
      <name>position</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>a7e058bf9995782c0442981d9fe57de49</anchor>
      <arglist>(std::string *f=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>acf1dd13a36bf7792e5da06f2edfb1c3f</anchor>
      <arglist>(std::string *fn=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>aad9d63b47c96c61f74fa22c1866fa28f</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>aa97041344fc45ab8f01c0dd625a36e80</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>std::string *</type>
      <name>filename</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>aaaafaf5e29c0851b6e040c800dc8a906</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>line</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>a91e1bd88aad032f4a1e1dafcab06f4c5</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>column</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>a4e34ca78e4f8d15cef154cb98f1d28dd</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>neverclaimyy::location</name>
    <filename>classneverclaimyy_1_1location.html</filename>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>aaaf48eea942dc5133c6d7a06ad7168af</anchor>
      <arglist>(const position &amp;b, const position &amp;e)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>aed41467a627b3676fc8d5bf23524a8d9</anchor>
      <arglist>(const position &amp;p=position())</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>aa8c38f7c622cbc9ebd8b465830899d46</anchor>
      <arglist>(std::string *f, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>a91b3c974feec126d0e7c3aa8dff4c081</anchor>
      <arglist>(std::string *f=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>step</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>ab585263b0b719f5570b9a60587a0db91</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>ab6e0a3dcd06155419f5d9c9345a0cfe2</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>aa7b93d746f2671dba02a4bd0ed2c5df9</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>begin</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>acdbf2ae108217568cf1dc873dc4a1551</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>end</name>
      <anchorfile>classneverclaimyy_1_1location.html</anchorfile>
      <anchor>ace225416967f03dcd616603d0b342a7d</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>neverclaimyy::position</name>
    <filename>classneverclaimyy_1_1position.html</filename>
    <member kind="function">
      <type></type>
      <name>position</name>
      <anchorfile>classneverclaimyy_1_1position.html</anchorfile>
      <anchor>a816a8601d65946614aadc98c4bbb41e8</anchor>
      <arglist>(std::string *f=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classneverclaimyy_1_1position.html</anchorfile>
      <anchor>a5eb8cde0ef64641635da3e499f13a364</anchor>
      <arglist>(std::string *fn=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classneverclaimyy_1_1position.html</anchorfile>
      <anchor>af5713d2bbe6f9f4948b3c182d62465f1</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classneverclaimyy_1_1position.html</anchorfile>
      <anchor>a74986bfcbfeb0b46e912a256e4984590</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>std::string *</type>
      <name>filename</name>
      <anchorfile>classneverclaimyy_1_1position.html</anchorfile>
      <anchor>ad7914a618adac4dc40da3c13ed7b3095</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>line</name>
      <anchorfile>classneverclaimyy_1_1position.html</anchorfile>
      <anchor>a77e81f211864bcbaa80f2a5b829e6f7e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>column</name>
      <anchorfile>classneverclaimyy_1_1position.html</anchorfile>
      <anchor>aad933b0a66ce9e68c548be8e66a4471c</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::dstar_aut</name>
    <filename>structspot_1_1dstar__aut.html</filename>
    <member kind="variable">
      <type>dstar_type</type>
      <name>type</name>
      <anchorfile>structspot_1_1dstar__aut.html</anchorfile>
      <anchor>a65f6ed7298e6a8d925bb964c1088d942</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>size_t</type>
      <name>accpair_count</name>
      <anchorfile>structspot_1_1dstar__aut.html</anchorfile>
      <anchor>abae9153b53d00a3f29f708d0a3e4374e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bitvect_array *</type>
      <name>accsets</name>
      <anchorfile>structspot_1_1dstar__aut.html</anchorfile>
      <anchor>a0f2ac864fd11e9dfd43d745894a996dd</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::fair_kripke_succ_iterator</name>
    <filename>classspot_1_1fair__kripke__succ__iterator.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function">
      <type></type>
      <name>fair_kripke_succ_iterator</name>
      <anchorfile>classspot_1_1fair__kripke__succ__iterator.html</anchorfile>
      <anchor>af5f13094ee31ec1532f1828eda633884</anchor>
      <arglist>(const bdd &amp;cond, const bdd &amp;acc_cond)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1fair__kripke__succ__iterator.html</anchorfile>
      <anchor>a8014eccf2ef9156d8d5816554d3a0398</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1fair__kripke__succ__iterator.html</anchorfile>
      <anchor>ac6ac67ac1f85ba3fc76f506b18864b46</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>a09901d8bb1addc2512f99ea2d47dc70a</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>aad7914dae3d29f19e3d48c628a4e2da1</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>a8e5b92f684fad0d93c49a85342cad192</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>ac2e83755c24ac845e3dc88a371bbd50c</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::fair_kripke</name>
    <filename>classspot_1_1fair__kripke.html</filename>
    <base>spot::tgba</base>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>state_condition</name>
      <anchorfile>classspot_1_1fair__kripke.html</anchorfile>
      <anchor>ab82a2fe269593fecab7c63d0c4c9ac03</anchor>
      <arglist>(const state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>state_acceptance_conditions</name>
      <anchorfile>classspot_1_1fair__kripke.html</anchorfile>
      <anchor>a53abb58e05f201b9b2ce74d8e9798e3a</anchor>
      <arglist>(const state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a3455011d3860bbd101a7f75ae2aad009</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a372b5a1582f8cb9fccd91c413350c8b8</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const =0</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a48c5b7c324749e8228815142b25cc201</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>afff8afc89af772ae288106cda2f5d951</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a5a52dc9090be87bf46c59d487bf84f42</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a2700b9f829bf2a884e29df8394ee2521</anchor>
      <arglist>(const state *state) const =0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a00272efd8025be669d827658dc121c82</anchor>
      <arglist>(const tgba_succ_iterator *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>ab411601915aacf07dd0a9035fd5eebe0</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a752a730728601d572f5147dd8811316d</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a6645d4206b82ef4773781924b30dc502</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a4d531428cb11fe9be3ddbf1928eb3657</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1fair__kripke.html</anchorfile>
      <anchor>a3b16acfa5d90c43b6086a3ed07be3959</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1fair__kripke.html</anchorfile>
      <anchor>ad7f0b0379bc951430835a079983c4587</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::kripke_succ_iterator</name>
    <filename>classspot_1_1kripke__succ__iterator.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function">
      <type></type>
      <name>kripke_succ_iterator</name>
      <anchorfile>classspot_1_1kripke__succ__iterator.html</anchorfile>
      <anchor>ad9e251d935e182910c21e05cb85e75d4</anchor>
      <arglist>(const bdd &amp;cond)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1kripke__succ__iterator.html</anchorfile>
      <anchor>a0e4579806f511b2f3dfadcb00bb39149</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1kripke__succ__iterator.html</anchorfile>
      <anchor>abf13d02bf3d7ca433a98cecbfbbdf7a4</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::kripke</name>
    <filename>classspot_1_1kripke.html</filename>
    <base>spot::fair_kripke</base>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>state_acceptance_conditions</name>
      <anchorfile>classspot_1_1kripke.html</anchorfile>
      <anchor>a86e7dadbe7fc48e2dbaa95f15dda57f3</anchor>
      <arglist>(const state *) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1kripke.html</anchorfile>
      <anchor>a6696401b19377b3ffbf9339e93e09c9e</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1kripke.html</anchorfile>
      <anchor>a6fa3b564adacefa55542641b0d994f4a</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_kripke</name>
    <filename>classspot_1_1state__kripke.html</filename>
    <base>spot::state</base>
  </compound>
  <compound kind="class">
    <name>spot::kripke_explicit_succ_iterator</name>
    <filename>classspot_1_1kripke__explicit__succ__iterator.html</filename>
    <base>spot::kripke_succ_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1kripke__explicit__succ__iterator.html</anchorfile>
      <anchor>a1ab2d99a137978a613cb3e2ace890282</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1kripke__explicit__succ__iterator.html</anchorfile>
      <anchor>a8c9541840c401605b6d0b2611dc05b06</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1kripke__explicit__succ__iterator.html</anchorfile>
      <anchor>af412537fd24d2fa522deff91e56ae86c</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_kripke *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1kripke__explicit__succ__iterator.html</anchorfile>
      <anchor>a3ca6e69059ad49e72a3413ab38ebf647</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::kripke_explicit</name>
    <filename>classspot_1_1kripke__explicit.html</filename>
    <base>spot::kripke</base>
    <member kind="function">
      <type>bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>aa533c7b99b5accf9c51a038fdfe38a82</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>state_kripke *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a2f32e0d705e24ddf85697c740eeea2b9</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>kripke_explicit_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a9272837dfae76a266b2ede2339fa3142</anchor>
      <arglist>(const spot::state *local_state, const spot::state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>state_condition</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a01f1f2b3c6b2746cdacb23e46a700ecf</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>state_condition</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a92045e8ac63a2e3afc4b420b9955e1de</anchor>
      <arglist>(const std::string &amp;) const </arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a643d1878bcc92522fd7bf0355e3baeae</anchor>
      <arglist>(const state *) const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>acf94abbb44466ef441e4f6d4345e5c10</anchor>
      <arglist>(std::string)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_transition</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a0b2e7ecb1907191d14b91a8c40e19053</anchor>
      <arglist>(std::string source, std::string dest)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_conditions</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a0d6c1a8223752d522750ec37613d80a8</anchor>
      <arglist>(bdd add, std::string on_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_condition</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a0b674bd369693621924df050458dedf7</anchor>
      <arglist>(const ltl::formula *f, std::string on_me)</arglist>
    </member>
    <member kind="function">
      <type>const std::map&lt; const state_kripke *, std::string &gt; &amp;</type>
      <name>sn_get</name>
      <anchorfile>classspot_1_1kripke__explicit.html</anchorfile>
      <anchor>a6f75caf56ec57a94fbbbb71f399751da</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::bdd_less_than</name>
    <filename>structspot_1_1bdd__less__than.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::bdd_hash</name>
    <filename>structspot_1_1bdd__hash.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::bitvect</name>
    <filename>classspot_1_1bitvect.html</filename>
    <member kind="function">
      <type>void</type>
      <name>reserve_blocks</name>
      <anchorfile>classspot_1_1bitvect.html</anchorfile>
      <anchor>a1683bdf11b2ac71217e5614975c8e038</anchor>
      <arglist>(size_t new_block_count)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>push_back</name>
      <anchorfile>classspot_1_1bitvect.html</anchorfile>
      <anchor>abdb21a168eb3e8da6c7446e7d4c7a87a</anchor>
      <arglist>(bool val)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>push_back</name>
      <anchorfile>classspot_1_1bitvect.html</anchorfile>
      <anchor>a37494e4c54d0ef32b4666f42b0d896e5</anchor>
      <arglist>(block_t data, unsigned count)</arglist>
    </member>
    <member kind="friend">
      <type>friend SPOT_API std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>classspot_1_1bitvect.html</anchorfile>
      <anchor>a3dae1f785b04a659469baae01c3097f5</anchor>
      <arglist>(std::ostream &amp;, const bitvect &amp;)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::bitvect_array</name>
    <filename>classspot_1_1bitvect__array.html</filename>
    <member kind="function">
      <type>size_t</type>
      <name>size</name>
      <anchorfile>classspot_1_1bitvect__array.html</anchorfile>
      <anchor>a2853984ba77df3d952dc968fbdad5b05</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bitvect &amp;</type>
      <name>at</name>
      <anchorfile>classspot_1_1bitvect__array.html</anchorfile>
      <anchor>a3145640ee8a128d8fa3c192ce21a3e6f</anchor>
      <arglist>(const size_t index)</arglist>
    </member>
    <member kind="function">
      <type>const bitvect &amp;</type>
      <name>at</name>
      <anchorfile>classspot_1_1bitvect__array.html</anchorfile>
      <anchor>ab18b7087548c49947f0498ca03ec7152</anchor>
      <arglist>(const size_t index) const </arglist>
    </member>
    <member kind="friend">
      <type>friend SPOT_API std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>classspot_1_1bitvect__array.html</anchorfile>
      <anchor>a8da1bd094a72c5f7c37e96a8895814fc</anchor>
      <arglist>(std::ostream &amp;, const bitvect_array &amp;)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::fixed_size_pool</name>
    <filename>classspot_1_1fixed__size__pool.html</filename>
    <member kind="function">
      <type></type>
      <name>fixed_size_pool</name>
      <anchorfile>classspot_1_1fixed__size__pool.html</anchorfile>
      <anchor>a6a4e2ff7241946bb41b10ad29016d415</anchor>
      <arglist>(size_t size)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~fixed_size_pool</name>
      <anchorfile>classspot_1_1fixed__size__pool.html</anchorfile>
      <anchor>a186aa1b99e7caa7a8924dc5afae6c188</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void *</type>
      <name>allocate</name>
      <anchorfile>classspot_1_1fixed__size__pool.html</anchorfile>
      <anchor>a12378ce82675d629e94180fe4a8d955d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>deallocate</name>
      <anchorfile>classspot_1_1fixed__size__pool.html</anchorfile>
      <anchor>a3d2cbc9689e5bb4a9c56573711787261</anchor>
      <arglist>(const void *ptr)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::printable</name>
    <filename>classspot_1_1printable.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::printable_value</name>
    <filename>classspot_1_1printable__value.html</filename>
    <templarg>T</templarg>
    <base>spot::printable</base>
  </compound>
  <compound kind="class">
    <name>spot::printable_id</name>
    <filename>classspot_1_1printable__id.html</filename>
    <base>spot::printable</base>
  </compound>
  <compound kind="class">
    <name>spot::printable_percent</name>
    <filename>classspot_1_1printable__percent.html</filename>
    <base>spot::printable</base>
  </compound>
  <compound kind="class">
    <name>spot::formater</name>
    <filename>classspot_1_1formater.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::ptr_hash</name>
    <filename>structspot_1_1ptr__hash.html</filename>
    <templarg></templarg>
  </compound>
  <compound kind="struct">
    <name>spot::identity_hash</name>
    <filename>structspot_1_1identity__hash.html</filename>
    <templarg></templarg>
  </compound>
  <compound kind="struct">
    <name>spot::char_ptr_less_than</name>
    <filename>structspot_1_1char__ptr__less__than.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::minato_isop</name>
    <filename>classspot_1_1minato__isop.html</filename>
    <member kind="function">
      <type></type>
      <name>minato_isop</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>a219911ee13dbbe288f7ec765bf4eab93</anchor>
      <arglist>(bdd input)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>minato_isop</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>a2962f8b4798aad61c41aa2e1124581f2</anchor>
      <arglist>(bdd input, bdd vars)</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>next</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>af3b6eb9c04010f353116a38ed670b7e7</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::multiple_size_pool</name>
    <filename>classspot_1_1multiple__size__pool.html</filename>
    <member kind="function">
      <type></type>
      <name>multiple_size_pool</name>
      <anchorfile>classspot_1_1multiple__size__pool.html</anchorfile>
      <anchor>ae56659d9df67d2439688a685d4871588</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~multiple_size_pool</name>
      <anchorfile>classspot_1_1multiple__size__pool.html</anchorfile>
      <anchor>a438325e77081406270ccfd860cec5ddf</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void *</type>
      <name>allocate</name>
      <anchorfile>classspot_1_1multiple__size__pool.html</anchorfile>
      <anchor>a179d051d39ac6ac65b8d9688b421152c</anchor>
      <arglist>(size_t size)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>deallocate</name>
      <anchorfile>classspot_1_1multiple__size__pool.html</anchorfile>
      <anchor>ab4090c05e6a847fa02d6364e653b3d49</anchor>
      <arglist>(const void *ptr, size_t size)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::option_map</name>
    <filename>classspot_1_1option__map.html</filename>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>acb6b25bf19608a1927e5d8daaba72a05</anchor>
      <arglist>(const char *options)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>a9b3db963d1cca0285d320bf5ea616d8c</anchor>
      <arglist>(const char *option, int def=0) const </arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>operator[]</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>adbe2aa8f513c8f4251b6f78f722c3cdf</anchor>
      <arglist>(const char *option) const </arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>set</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>ad9fb6b43a106db55f9b84b59ed766646</anchor>
      <arglist>(const char *option, int val, int def=0)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>a54ca434ab0eec0f87e1b5bcf71f50785</anchor>
      <arglist>(const option_map &amp;o)</arglist>
    </member>
    <member kind="function">
      <type>int &amp;</type>
      <name>operator[]</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>a6855a963741c87f0dcb89fe12f98e849</anchor>
      <arglist>(const char *option)</arglist>
    </member>
    <member kind="friend">
      <type>friend SPOT_API std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>a0473e537f29ad9a0b14d63a14e3152a7</anchor>
      <arglist>(std::ostream &amp;os, const option_map &amp;m)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::barand</name>
    <filename>classspot_1_1barand.html</filename>
    <templarg>gen</templarg>
  </compound>
  <compound kind="class">
    <name>spot::clause_counter</name>
    <filename>classspot_1_1clause__counter.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::satsolver</name>
    <filename>classspot_1_1satsolver.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::time_info</name>
    <filename>structspot_1_1time__info.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::timer</name>
    <filename>classspot_1_1timer.html</filename>
    <member kind="function">
      <type>void</type>
      <name>start</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>a55c6674711dab0e67008dc378011e46d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>stop</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>a34330e7a7a879a13b2dfb60df84867cd</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>clock_t</type>
      <name>utime</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>aaab40549d862ebfbb7d07ccf0ba4a129</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>clock_t</type>
      <name>stime</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>a87c8d0c9bed27d39846f0ec421daffbb</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_running</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>a868bfb05189f521d08dfb274b93b459a</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::timer_map</name>
    <filename>classspot_1_1timer__map.html</filename>
    <member kind="function">
      <type>void</type>
      <name>start</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>a85d4bf24749d93be6dcbb5e66aacd249</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>stop</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>a709679565ddbc19293c6f63138367911</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>cancel</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>ab35d7a8e21cc87716039cecd5040856e</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>const spot::timer &amp;</type>
      <name>timer</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>a0b2786417c063a93c851174df0ef2071</anchor>
      <arglist>(const std::string &amp;name) const </arglist>
    </member>
    <member kind="function">
      <type>spot::timer &amp;</type>
      <name>timer</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>a02d67150c7c115d2f06ae49ef1dadc19</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>empty</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>a60fd945cfdc64df8e7ca1bc3a975c346</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>SPOT_API std::ostream &amp;</type>
      <name>print</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>ae96a39691fa0b52bbe6e040dafa78e91</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>reset_all</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>af2293403679d234d686f650ba564cf35</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::temporary_file</name>
    <filename>classspot_1_1temporary__file.html</filename>
    <base>spot::printable</base>
  </compound>
  <compound kind="class">
    <name>spot::open_temporary_file</name>
    <filename>classspot_1_1open__temporary__file.html</filename>
    <base>spot::temporary_file</base>
  </compound>
  <compound kind="class">
    <name>spot::unique_ptr</name>
    <filename>classspot_1_1unique__ptr.html</filename>
    <templarg>T</templarg>
  </compound>
  <compound kind="class">
    <name>spot::explicit_state_conjunction</name>
    <filename>classspot_1_1explicit__state__conjunction.html</filename>
    <base>spot::saba_state_conjunction</base>
    <member kind="function">
      <type>void</type>
      <name>add</name>
      <anchorfile>classspot_1_1explicit__state__conjunction.html</anchorfile>
      <anchor>ae40ff6fa0a676c7360d6fd9abbd9be5c</anchor>
      <arglist>(saba_state *state)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1explicit__state__conjunction.html</anchorfile>
      <anchor>a68bcdde7f9916cd9e4dc48c4aa9d0bb7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1explicit__state__conjunction.html</anchorfile>
      <anchor>a9570e7ccb81a33698c5f218f2b798405</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1explicit__state__conjunction.html</anchorfile>
      <anchor>acd528a15232bdb1723c1486ea30b1d35</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>explicit_state_conjunction *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1explicit__state__conjunction.html</anchorfile>
      <anchor>a7bc58079a529be56845df71ab10a90b2</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual saba_state *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1explicit__state__conjunction.html</anchorfile>
      <anchor>a3e7336394301c23d637b3fc6edc3a661</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::saba</name>
    <filename>classspot_1_1saba.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual saba_state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1saba.html</anchorfile>
      <anchor>a7a79f6031065065e9b3cb043d127ed04</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual saba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1saba.html</anchorfile>
      <anchor>a0909c9f4122c977e0be1511cfc4139f0</anchor>
      <arglist>(const saba_state *local_state) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1saba.html</anchorfile>
      <anchor>a37e27e9273d98fc5efadda40bea65c0f</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1saba.html</anchorfile>
      <anchor>a2ab04c3225cd11f91c8117370ac659de</anchor>
      <arglist>(const saba_state *state) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1saba.html</anchorfile>
      <anchor>a69cdd40dc78534ba73cd6b32e668e51c</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1saba.html</anchorfile>
      <anchor>aa6896ae07574d91d22594620ac2cd9f0</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::saba_complement_tgba</name>
    <filename>classspot_1_1saba__complement__tgba.html</filename>
    <base>spot::saba</base>
    <member kind="function" virtualness="virtual">
      <type>virtual saba_state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1saba__complement__tgba.html</anchorfile>
      <anchor>af3b28bc0db80637feb358a4d1e37c1ca</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual saba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1saba__complement__tgba.html</anchorfile>
      <anchor>a6cf49fb61448deb14622876973435d95</anchor>
      <arglist>(const saba_state *local_state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1saba__complement__tgba.html</anchorfile>
      <anchor>aa5b9bae059ccb7f1bb48f54f8ceb6f61</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1saba__complement__tgba.html</anchorfile>
      <anchor>adfcb58a95bb7252329fac02a717a5b0c</anchor>
      <arglist>(const saba_state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1saba__complement__tgba.html</anchorfile>
      <anchor>a40289b127286faa04f894aa0fdd2b5af</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::saba_state</name>
    <filename>classspot_1_1saba__state.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1saba__state.html</anchorfile>
      <anchor>a53a2cc4c8220a68d07c27280013cf25d</anchor>
      <arglist>(const saba_state *other) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1saba__state.html</anchorfile>
      <anchor>ae0de4ee7704984f2e38f0a06353c01a1</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual saba_state *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1saba__state.html</anchorfile>
      <anchor>a72b71aba5cabdf27396e17df479986be</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>acceptance_conditions</name>
      <anchorfile>classspot_1_1saba__state.html</anchorfile>
      <anchor>a0e454880406b23c2199d5d44b4236f80</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::saba_state_ptr_less_than</name>
    <filename>structspot_1_1saba__state__ptr__less__than.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::saba_state_ptr_equal</name>
    <filename>structspot_1_1saba__state__ptr__equal.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::saba_state_ptr_hash</name>
    <filename>structspot_1_1saba__state__ptr__hash.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::saba_state_shared_ptr_less_than</name>
    <filename>structspot_1_1saba__state__shared__ptr__less__than.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::saba_state_shared_ptr_equal</name>
    <filename>structspot_1_1saba__state__shared__ptr__equal.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::saba_state_shared_ptr_hash</name>
    <filename>structspot_1_1saba__state__shared__ptr__hash.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::saba_state_conjunction</name>
    <filename>classspot_1_1saba__state__conjunction.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual saba_state_conjunction *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1saba__state__conjunction.html</anchorfile>
      <anchor>ab2c0c3a6016144fce75f5b3b223d05ef</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1saba__state__conjunction.html</anchorfile>
      <anchor>a30c8a6a7d907d6821e45338c039d5051</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1saba__state__conjunction.html</anchorfile>
      <anchor>afa5dbbd47bc162049f3bd812b2bd1da3</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1saba__state__conjunction.html</anchorfile>
      <anchor>a3bb0025ea04da3734859bd5faa8e7924</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual saba_state *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1saba__state__conjunction.html</anchorfile>
      <anchor>a5dd1612bb002d1e30485eb51b5014aea</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::saba_succ_iterator</name>
    <filename>classspot_1_1saba__succ__iterator.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1saba__succ__iterator.html</anchorfile>
      <anchor>a5232a8a98c3f8d7979d33dd19224991c</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1saba__succ__iterator.html</anchorfile>
      <anchor>a145523b3177344034ccdc43ec2edfaa3</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1saba__succ__iterator.html</anchorfile>
      <anchor>a60cff9fe2e50305156ebb6555bebae44</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual saba_state_conjunction *</type>
      <name>current_conjunction</name>
      <anchorfile>classspot_1_1saba__succ__iterator.html</anchorfile>
      <anchor>a7b209ea564fc88cea087cce05fa062ef</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1saba__succ__iterator.html</anchorfile>
      <anchor>a30ab665fc84e48ba0f6e2e25cd9249a2</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::saba_reachable_iterator</name>
    <filename>classspot_1_1saba__reachable__iterator.html</filename>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>a83bb08834e1df8c0cf51becb0677778f</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>want_state</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>ad695645c0219f28c8f94ae40902489a8</anchor>
      <arglist>(const saba_state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>a5d1fdcb00ea6ba79619c141f0364535a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>a7b731e4b4e556bb2f1f81c784c0a1172</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>a4982ba8d7555c5cf37680acb4f730268</anchor>
      <arglist>(const saba_state *s, int n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state_conjunction</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>ab7cf7239a3894091161263b42f87aa54</anchor>
      <arglist>(const saba_state *in_s, int in, const saba_state_conjunction *sc, int sc_id, const saba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>a78ae18637c807677f775199751667d36</anchor>
      <arglist>(const saba_state *in_s, int in, const saba_state *out_s, int out, const saba_state_conjunction *sc, int sc_id, const saba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const saba_state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>a9f02dc856075bb007231bfd79fcce73c</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const saba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>a164226a027f744ec5a8e7153f5536ba7</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1saba__reachable__iterator.html</anchorfile>
      <anchor>ad12a2f1017b6e2fd3bee4018e973b0e9</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::saba_reachable_iterator_depth_first</name>
    <filename>classspot_1_1saba__reachable__iterator__depth__first.html</filename>
    <base>spot::saba_reachable_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual const saba_state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1saba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>aaba1262b77f4fbdf46b7252148872a99</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::stack&lt; const saba_state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1saba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>acff021580162fa03cf12e733ac076a2f</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::saba_reachable_iterator_breadth_first</name>
    <filename>classspot_1_1saba__reachable__iterator__breadth__first.html</filename>
    <base>spot::saba_reachable_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual const saba_state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1saba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>aa86f4576b5151845471cd7320b5af0eb</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const saba_state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1saba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>a0822727f5fab702c3bd172a714fc0407</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta</name>
    <filename>classspot_1_1ta.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual const states_set_t</type>
      <name>get_initial_states_set</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>ae847e1d2f986db3bbc7a0d7535155677</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual spot::state *</type>
      <name>get_artificial_initial_state</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a5cbd41f6c0e19af83d55816a5fbb2321</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual ta_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>af0b2f71c85c0dd17b850197537a383bc</anchor>
      <arglist>(const spot::state *state) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual ta_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>af9ebc32d6cc33044f538c25b07eb09bd</anchor>
      <arglist>(const spot::state *state, bdd changeset) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a5b1cf7537d0702b2acc136578895d926</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a075f7fdb44428659a16484170477e77e</anchor>
      <arglist>(const spot::state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>is_accepting_state</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a05398307c3e98a177f7f182b2becb6ee</anchor>
      <arglist>(const spot::state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>is_livelock_accepting_state</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a5e2b5e5f1ad31adbc157571d88007b01</anchor>
      <arglist>(const spot::state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>is_initial_state</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a9291b601b2c32aa38c04feeb5f48a703</anchor>
      <arglist>(const spot::state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>get_state_condition</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a807b62b44071746902e7d5e76b7fefad</anchor>
      <arglist>(const spot::state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>free_state</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a83fe8227b08e5d5ae575fddec620d85c</anchor>
      <arglist>(const spot::state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1ta.html</anchorfile>
      <anchor>a06639adf20622dcafaa7a9a3e7f6417f</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_succ_iterator</name>
    <filename>classspot_1_1ta__succ__iterator.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1ta__succ__iterator.html</anchorfile>
      <anchor>a4fdbe3595410319007bd02c7587ef15e</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1ta__succ__iterator.html</anchorfile>
      <anchor>aa7802616a4978be0fdd1a75e6df6c02d</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1ta__succ__iterator.html</anchorfile>
      <anchor>a5b18bd5ca7d46de3fffad641641f2d9f</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1ta__succ__iterator.html</anchorfile>
      <anchor>a6fadb6179fd5d88cb5d55a167a7257c9</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1ta__succ__iterator.html</anchorfile>
      <anchor>a9b31683f32aecc86ecaaee404d201af7</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1ta__succ__iterator.html</anchorfile>
      <anchor>aaebcce590d0375ac43d221bb1b5dbc34</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::scc_stack_ta</name>
    <filename>classspot_1_1scc__stack__ta.html</filename>
    <class kind="struct">spot::scc_stack_ta::connected_component</class>
    <member kind="function">
      <type>void</type>
      <name>push</name>
      <anchorfile>classspot_1_1scc__stack__ta.html</anchorfile>
      <anchor>a728b9f4a4c7e88f62f6b7ca873cdabb7</anchor>
      <arglist>(int index)</arglist>
    </member>
    <member kind="function">
      <type>connected_component &amp;</type>
      <name>top</name>
      <anchorfile>classspot_1_1scc__stack__ta.html</anchorfile>
      <anchor>a7890d4b313efcdb53a6581d2b0f453dc</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const connected_component &amp;</type>
      <name>top</name>
      <anchorfile>classspot_1_1scc__stack__ta.html</anchorfile>
      <anchor>a200119230a7af3a3c5b5bb7ed1eba567</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>pop</name>
      <anchorfile>classspot_1_1scc__stack__ta.html</anchorfile>
      <anchor>a60865fb164be8b7fe75f8bf772992ed0</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>size</name>
      <anchorfile>classspot_1_1scc__stack__ta.html</anchorfile>
      <anchor>a5c80fb3fafd706a720fcd00b2b838493</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>std::list&lt; state * &gt; &amp;</type>
      <name>rem</name>
      <anchorfile>classspot_1_1scc__stack__ta.html</anchorfile>
      <anchor>afde37c79a58a4a9ea7e1487c94e42642</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>empty</name>
      <anchorfile>classspot_1_1scc__stack__ta.html</anchorfile>
      <anchor>a595d6e7699cd03fcf8e2127f2b9c6ee4</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::scc_stack_ta::connected_component</name>
    <filename>structspot_1_1scc__stack__ta_1_1connected__component.html</filename>
    <member kind="variable">
      <type>int</type>
      <name>index</name>
      <anchorfile>structspot_1_1scc__stack__ta_1_1connected__component.html</anchorfile>
      <anchor>a8b831f37dc942f8f33d4a48376161e43</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>condition</name>
      <anchorfile>structspot_1_1scc__stack__ta_1_1connected__component.html</anchorfile>
      <anchor>a7d5d3e5f5562d146745d036dcc795072</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_explicit</name>
    <filename>classspot_1_1ta__explicit.html</filename>
    <base>spot::ta</base>
    <member kind="function" virtualness="virtual">
      <type>virtual const states_set_t</type>
      <name>get_initial_states_set</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a829c195d324bf9fd80cabdb883f1beb3</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual ta_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a56ff22eacfb314076c3795e29c51bd45</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual ta_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>ae63d26c7687ae408133aeb95c793a4d5</anchor>
      <arglist>(const spot::state *s, bdd condition) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a2d9f1b65d258892c253750376a758213</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a75d9f86c25fd4a7cd6ababa7467846d2</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>is_accepting_state</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a908627a9e60708a3c1f5a63f641ff5da</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>is_livelock_accepting_state</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a6de8fc9f373578cbc436af5f1d58fa97</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>is_initial_state</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a1d914fc0d7f5aed14ab92befcda2d668</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>get_state_condition</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a696a3fcfbe853754a3d6402a8ec16f56</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>free_state</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>aa7879d15aae1adf6d25386d381f894f3</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function">
      <type>spot::state *</type>
      <name>get_artificial_initial_state</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a7f13f19527ada113bb7748d6f79597f0</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1ta__explicit.html</anchorfile>
      <anchor>a0a4f238c1876d503352f15e80940721c</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_ta_explicit</name>
    <filename>classspot_1_1state__ta__explicit.html</filename>
    <base>spot::state</base>
    <class kind="struct">spot::state_ta_explicit::transition</class>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__ta__explicit.html</anchorfile>
      <anchor>a24f7ac446cc6c4a93ebc6f0760116743</anchor>
      <arglist>(const spot::state *other) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__ta__explicit.html</anchorfile>
      <anchor>a5abbce9b917e118c458652b04c933b4a</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_ta_explicit *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__ta__explicit.html</anchorfile>
      <anchor>a9f640f51840272f41f0b1c0a0a1cb463</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>destroy</name>
      <anchorfile>classspot_1_1state__ta__explicit.html</anchorfile>
      <anchor>a288d7b70dce99263bc0b640cd7939cb3</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_hole_state</name>
      <anchorfile>classspot_1_1state__ta__explicit.html</anchorfile>
      <anchor>a3437056558cd4120fe240ff794cf959c</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>delete_stuttering_and_hole_successors</name>
      <anchorfile>classspot_1_1state__ta__explicit.html</anchorfile>
      <anchor>a9f61e901935f09e3c2118a0e3df7a364</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::state_ta_explicit::transition</name>
    <filename>structspot_1_1state__ta__explicit_1_1transition.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ta_explicit_succ_iterator</name>
    <filename>classspot_1_1ta__explicit__succ__iterator.html</filename>
    <base>spot::ta_succ_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1ta__explicit__succ__iterator.html</anchorfile>
      <anchor>ac0d5ceb2a3d1a22e934945b9689f8905</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1ta__explicit__succ__iterator.html</anchorfile>
      <anchor>a3e1fd56a1755a304d546b5e7ef2e637d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1ta__explicit__succ__iterator.html</anchorfile>
      <anchor>a3f5c76f93540ee8719f0687eb8763b34</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1ta__explicit__succ__iterator.html</anchorfile>
      <anchor>a0d43ac04a314abf7780333b5a816f440</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1ta__explicit__succ__iterator.html</anchorfile>
      <anchor>a12179921eeda385c5330948048960eef</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1ta__explicit__succ__iterator.html</anchorfile>
      <anchor>a1eae6200980284f383233c3530cab698</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_ta_product</name>
    <filename>classspot_1_1state__ta__product.html</filename>
    <base>spot::state</base>
    <member kind="function">
      <type></type>
      <name>state_ta_product</name>
      <anchorfile>classspot_1_1state__ta__product.html</anchorfile>
      <anchor>add2973f1d94df019f45e300f3ce95aa4</anchor>
      <arglist>(state *ta_state, state *kripke_state)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>state_ta_product</name>
      <anchorfile>classspot_1_1state__ta__product.html</anchorfile>
      <anchor>a9556804b0a24a9122fa193234aaf4909</anchor>
      <arglist>(const state_ta_product &amp;o)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__ta__product.html</anchorfile>
      <anchor>a763222974c10ce97f8680fa34ac6aeda</anchor>
      <arglist>(const state *other) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__ta__product.html</anchorfile>
      <anchor>ad9373db991061709fa603d42dadbef5e</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_ta_product *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__ta__product.html</anchorfile>
      <anchor>aba4ae4b71c6227e5c660c12c181875be</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>destroy</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>afa00fb3e8019389e2b6fbec0b5e40ded</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_succ_iterator_product</name>
    <filename>classspot_1_1ta__succ__iterator__product.html</filename>
    <base>spot::ta_succ_iterator</base>
    <member kind="function">
      <type>void</type>
      <name>first</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>aec89aff7b04f79714e930ab797b1d62d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>next</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>aff3d99ed74053abf76a86a7cac96275b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>addc916cde3929080edb12ddb73abb5bc</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>state_ta_product *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>addfb653dad3aa7ee382d4cbe90e41709</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>aad65165e4b41c130fda1d2941f07e2e8</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>a07f5f22f2deccee429e2f727990163b2</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_stuttering_transition</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>a64ce6c7b3838bca19775335083ed224b</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>step_</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>ab6ab78f0e28cd4ed299b823e87b894ed</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>next_kripke_dest</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product.html</anchorfile>
      <anchor>aa9a5b882202a3dbc21fafe551d4d5b21</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_product</name>
    <filename>classspot_1_1ta__product.html</filename>
    <base>spot::ta</base>
    <member kind="function">
      <type></type>
      <name>ta_product</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>a6903e7ee92887d20047fcca6d6c5d80a</anchor>
      <arglist>(const ta *testing_automaton, const kripke *kripke_structure)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const std::set&lt; state *, state_ptr_less_than &gt;</type>
      <name>get_initial_states_set</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>abb252ecb9029cfddfcad6bc4e9e98bb9</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual ta_succ_iterator_product *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>aba95be080867af93d7c3cd4f3e8ffedb</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual ta_succ_iterator_product *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>a700b77deec59337ac426796f2f94c453</anchor>
      <arglist>(const spot::state *s, bdd changeset) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>acea792a98272b138671d47dfbef04805</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>a4e33014ad5446206c2d213daaf86ef9a</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>is_accepting_state</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>ab9c5f0fbc7c6ce5f721c280537386c75</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>is_livelock_accepting_state</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>af6a40ce841dbb319f5ee9d6467344943</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>is_initial_state</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>abefe2a449db42357241e2aefd8b52934</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>is_hole_state_in_ta_component</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>affbe07cbfda5c7994319b19a8d579252</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>get_state_condition</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>a60defd29b185e0042c197f732c4e25c6</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>a92e05f856dea6407fb13a385bc41e202</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>free_state</name>
      <anchorfile>classspot_1_1ta__product.html</anchorfile>
      <anchor>ab5d98a9265409d846c8ac85fde9f2ff2</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_succ_iterator_product_by_changeset</name>
    <filename>classspot_1_1ta__succ__iterator__product__by__changeset.html</filename>
    <base>spot::ta_succ_iterator_product</base>
    <member kind="function">
      <type>void</type>
      <name>next_kripke_dest</name>
      <anchorfile>classspot_1_1ta__succ__iterator__product__by__changeset.html</anchorfile>
      <anchor>a449b12f79b48528fc82f29d390daac32</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgta</name>
    <filename>classspot_1_1tgta.html</filename>
    <base>spot::tgba</base>
    <member kind="function" virtualness="pure">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter_by_changeset</name>
      <anchorfile>classspot_1_1tgta.html</anchorfile>
      <anchor>a41348ca3d04225703fb1e3b898292da9</anchor>
      <arglist>(const spot::state *s, bdd change_set) const =0</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a917f0b1310e4838626cd3de923642356</anchor>
      <arglist>(const state *state) const =0</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>ae9128398fd3001b3e0b76f43d0c7b4aa</anchor>
      <arglist>(const state *state) const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgta_explicit</name>
    <filename>classspot_1_1tgta__explicit.html</filename>
    <base>spot::tgta</base>
    <member kind="function" virtualness="virtual">
      <type>virtual spot::state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>a6b4f5267b01307e5b802659da3b26c68</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>a7fd7a9d77067730695eb89c4f2c73c9e</anchor>
      <arglist>(const spot::state *local_state, const spot::state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>a849588ba6f1b486e6407a1869e31b928</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>a903e916243d044ffd6b07f094dba17aa</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>a003dc363fcb87cd1a23a0f0ababbb40b</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>afb05742c1a7bdd4fbcccd4eaeb5e348c</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter_by_changeset</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>a392a5ff63bc1aa863de865d0229b54eb</anchor>
      <arglist>(const spot::state *s, bdd change_set) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>a28d52eaae738d103fb6082d0383a7602</anchor>
      <arglist>(const spot::state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgta__explicit.html</anchorfile>
      <anchor>ae67407ee85848efdd35b04dbf0139123</anchor>
      <arglist>(const spot::state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgta_product</name>
    <filename>classspot_1_1tgta__product.html</filename>
    <base>spot::tgba_product</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgta__product.html</anchorfile>
      <anchor>a4c48930222a773d7908bb2423e6dcc19</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgta__product.html</anchorfile>
      <anchor>afbe58448d24d4aab81209ba0a19e8420</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a98be76c9a1c99c98d3d98b922e6754c4</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a253991f4680be4b9b6cc4ea2b58f0f1e</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a5bd9816b75446546f7ab22f1f08a4b7f</anchor>
      <arglist>(const tgba_succ_iterator *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a63415db59de0198957b8db61e68b2d25</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a588b3078a18b0d9073c1d06a8ec5429c</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>acc05aeb22baf7ca5a3c0ca415767bafd</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a4a46399685445cc7b0ce81e507f1d14c</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a900940457e4d9ecfa27cbac423730471</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgta_succ_iterator_product</name>
    <filename>classspot_1_1tgta__succ__iterator__product.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function">
      <type>void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgta__succ__iterator__product.html</anchorfile>
      <anchor>ad09eaa0f0ea0e73d7998a63be77fdb9f</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgta__succ__iterator__product.html</anchorfile>
      <anchor>af9620384d8083e951bf82c16cd523b35</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgta__succ__iterator__product.html</anchorfile>
      <anchor>ace194cdbd1a206aca561fb633f9d0338</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>state_product *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgta__succ__iterator__product.html</anchorfile>
      <anchor>a5f5272e3748e9608a1e6cd14052287a8</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgta__succ__iterator__product.html</anchorfile>
      <anchor>a51f34affd9ec9bda6ba970cdc5315d65</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgta__succ__iterator__product.html</anchorfile>
      <anchor>ae40c88b932beb7901c4a6bbc8f6af23d</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_check</name>
    <filename>classspot_1_1ta__check.html</filename>
    <base>spot::ec_statistics</base>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>check</name>
      <anchorfile>classspot_1_1ta__check.html</anchorfile>
      <anchor>af27b9b5667dc9fe644be52b0481212e3</anchor>
      <arglist>(bool disable_second_pass=false, bool disable_heuristic_for_livelock_detection=false)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>livelock_detection</name>
      <anchorfile>classspot_1_1ta__check.html</anchorfile>
      <anchor>ac2dfba4a283f022d68d03f44d1b5345e</anchor>
      <arglist>(const ta_product *t)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::ostream &amp;</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1ta__check.html</anchorfile>
      <anchor>a0ef38b3394854ceea97d4332839626b7</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>heuristic_livelock_detection</name>
      <anchorfile>classspot_1_1ta__check.html</anchorfile>
      <anchor>a1f47e6f536ad188608d1360e49a43d9f</anchor>
      <arglist>(const state *stuttering_succ, numbered_state_heap *h, int h_livelock_root, std::set&lt; const state *, state_ptr_less_than &gt; liveset_curr)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const ta_product *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1ta__check.html</anchorfile>
      <anchor>a3439fece5dd2da06b6a95f35be5424a9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1ta__check.html</anchorfile>
      <anchor>ac07d98b416c4f77f9ef9a33688d09e9c</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_reachable_iterator</name>
    <filename>classspot_1_1ta__reachable__iterator.html</filename>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>a454e2ac517cf0d7e95512e227672b2a4</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>want_state</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>aa680ef6988f48589b2e702895698a144</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>af891fcf3bba10f78aecf301ad2235cb5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>afd74b5f21e08ed690fc4a765494652c9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>ab9c235e497cef942c159a98ebb0da95b</anchor>
      <arglist>(const state *s, int n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>af7cad569c3866860816852f186959c0f</anchor>
      <arglist>(int in, int out, const ta_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>aeb2902e9e8c6e5770de07543fbeb4a19</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const ta *</type>
      <name>t_automata_</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>a8c47d2b850a6ccdcc378225ee7612e51</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1ta__reachable__iterator.html</anchorfile>
      <anchor>af98a37b38855a26480567ea9074ee9eb</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_reachable_iterator_depth_first</name>
    <filename>classspot_1_1ta__reachable__iterator__depth__first.html</filename>
    <base>spot::ta_reachable_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1ta__reachable__iterator__depth__first.html</anchorfile>
      <anchor>a7ac0ebe14d4362ba582ffbdf672b6880</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::stack&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1ta__reachable__iterator__depth__first.html</anchorfile>
      <anchor>a5cdfa0bc2aafdc0a09dd9098df181581</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ta_reachable_iterator_breadth_first</name>
    <filename>classspot_1_1ta__reachable__iterator__breadth__first.html</filename>
    <base>spot::ta_reachable_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1ta__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>ac37ea09bde036fd6e97356aecf8e8f39</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1ta__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>ad5d8d1df09d1b416f000d7501db45621</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ta_statistics</name>
    <filename>structspot_1_1ta__statistics.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::acc_marks</name>
    <filename>classspot_1_1acc__marks.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::acc_condition</name>
    <filename>classspot_1_1acc__condition.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::bdd_dict</name>
    <filename>classspot_1_1bdd__dict.html</filename>
    <class kind="struct">spot::bdd_dict::bdd_info</class>
    <member kind="typedef">
      <type>std::map&lt; const ltl::formula *, int &gt;</type>
      <name>fv_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a5bd5f592056f364fdd862a3e0de9fd22</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; int, const ltl::formula * &gt;</type>
      <name>vf_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>ad0b9e89c7a60caad9e5786c82a15e0ee</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::set&lt; const void * &gt;</type>
      <name>ref_set</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a015d330da101dd646875b8ed1613a2d6</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~bdd_dict</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>af6f03983c2f4647cf0a192d5868c1b14</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_proposition</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a6095afcbbb4fbe67066d51169e80d333</anchor>
      <arglist>(const ltl::formula *f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>register_propositions</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>abb99eed0b49ffba5229af45ef7c1e05f</anchor>
      <arglist>(bdd f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_state</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>ac5b0d292aafacbfbbd233080fe4a4ad0</anchor>
      <arglist>(const ltl::formula *f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_acceptance_variable</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>abb27e26e93d99e046dbe9e9d0cb21485</anchor>
      <arglist>(const ltl::formula *f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_clone_acc</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>abebfcb644db70ac9368ff316317059a3</anchor>
      <arglist>(int var, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>register_acceptance_variables</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>ac4e496809f4027831aed7457fc8b7f3a</anchor>
      <arglist>(bdd f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>const ltl::formula *</type>
      <name>oneacc_to_formula</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>ae9584f1914c0f5dad897e60f5d560bb9</anchor>
      <arglist>(bdd oneacc) const </arglist>
    </member>
    <member kind="function">
      <type>const ltl::formula *</type>
      <name>oneacc_to_formula</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>ad1f021480a019e739c54c8e107b27939</anchor>
      <arglist>(int var) const </arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_anonymous_variables</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a7075f190e5d3a4face68a7a6e129eb95</anchor>
      <arglist>(int n, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>register_all_variables_of</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>af7e1e37179f390322da4d4cbf134b282</anchor>
      <arglist>(const void *from_other, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>unregister_all_my_variables</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a64b9d9dcc789312a700519388faf40e8</anchor>
      <arglist>(const void *me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>unregister_all_typed_variables</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>abb388a1a6f76ca01af640f26958cb37b</anchor>
      <arglist>(var_type type, const void *me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>unregister_variable</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>aa502521a61d81107e8c7ce5fab09ed7c</anchor>
      <arglist>(int var, const void *me)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>ad5ce6572f9b85f7996f391276ea024f0</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>assert_emptiness</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a24d18da7cfd50865e23c32195e13f4d4</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_registered_proposition</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a0676f051e8333759c46e01e8a602eeaf</anchor>
      <arglist>(const ltl::formula *f, const void *by_me)</arglist>
    </member>
    <member kind="variable">
      <type>fv_map</type>
      <name>now_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>af654f827c195d9a47fb733a7c6341aae</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>fv_map</type>
      <name>var_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a9f47535b3c6ca438bb58975a240d783f</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>fv_map</type>
      <name>acc_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>adea537e22c5889d908170b17ab8e8fd0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bddPair *</type>
      <name>next_to_now</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a1ecc1ea5dd6b7cf1d89d7add2cd056de</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bddPair *</type>
      <name>now_to_next</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a27dd24fcedeb14ccc72df82be8588a41</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::bdd_dict::bdd_info</name>
    <filename>structspot_1_1bdd__dict_1_1bdd__info.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::future_conditions_collector</name>
    <filename>classspot_1_1future__conditions__collector.html</filename>
    <base>spot::tgba_scc</base>
    <member kind="function">
      <type></type>
      <name>future_conditions_collector</name>
      <anchorfile>classspot_1_1future__conditions__collector.html</anchorfile>
      <anchor>affad02b39992b437787963d8058bc8d0</anchor>
      <arglist>(const tgba *aut, bool show=false)</arglist>
    </member>
    <member kind="function">
      <type>const cond_set &amp;</type>
      <name>future_conditions</name>
      <anchorfile>classspot_1_1future__conditions__collector.html</anchorfile>
      <anchor>a9417271a5ecac3517205108fbcf1e638</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1future__conditions__collector.html</anchorfile>
      <anchor>abacda9a16684c85c38d4facae91b7fe4</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>scc_of_state</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a0710f2fdfd1d418eef39b96f03b21d68</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a6600532f73f9304621e549c376f37df4</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>aeaf6d30f608df4268ea24c53b90c3bc8</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a869636920f00191acf639fe8915f29dc</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>af7c36cfb1aae3bfc5b8d8aa19ab406b6</anchor>
      <arglist>(const tgba_succ_iterator *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a8ba8488fdfb38d40e195ff5d401efbf0</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>ae498ad52cc2dfca08b9ecc71093be05e</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a3556e6082858d044414259716d91d494</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a2ce1ad3317b1e2bb28e62eef910956f2</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a4300be39b5499a988e993ed07024ff86</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::sba</name>
    <filename>classspot_1_1sba.html</filename>
    <base>spot::tgba</base>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>state_is_accepting</name>
      <anchorfile>classspot_1_1sba.html</anchorfile>
      <anchor>a9c406600cd4e363d5d6be8812f28495a</anchor>
      <arglist>(const spot::state *s) const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state</name>
    <filename>classspot_1_1state.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>af3a06cae8daa3aa622f83873e983b75c</anchor>
      <arglist>(const state *other) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>a453665382e0f590fab7d6608e690729f</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>a761dcaab0d082dd18db5f6ebf7c38ce0</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~state</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>a93b28d1aa2200cccdb4159bcf3e7b761</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::state_ptr_less_than</name>
    <filename>structspot_1_1state__ptr__less__than.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::state_ptr_equal</name>
    <filename>structspot_1_1state__ptr__equal.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::state_ptr_hash</name>
    <filename>structspot_1_1state__ptr__hash.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::state_shared_ptr_less_than</name>
    <filename>structspot_1_1state__shared__ptr__less__than.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::state_shared_ptr_equal</name>
    <filename>structspot_1_1state__shared__ptr__equal.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::state_shared_ptr_hash</name>
    <filename>structspot_1_1state__shared__ptr__hash.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::state_bdd</name>
    <filename>classspot_1_1state__bdd.html</filename>
    <base>spot::state</base>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>as_bdd</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>a5bfc19408e3ddf2e8322466efc17f04d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>a1cef7f8917a422071bfc919e3a16fbb8</anchor>
      <arglist>(const state *other) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>af8b5f391533201596c6aab7693dd274c</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_bdd *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>adfa6f9e17caed53e128b2b83b368ae47</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>state_</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>a90b4a51248aa8218ed633e54258bb1da</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_succ_iterator</name>
    <filename>classspot_1_1tgba__succ__iterator.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>a6498ab0e8ad7af781876f5c09a23b6a6</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>a00f20efb3ef4741c65069adb4cfb5ae6</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_succ_iterator_concrete</name>
    <filename>classspot_1_1tgba__succ__iterator__concrete.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function">
      <type></type>
      <name>tgba_succ_iterator_concrete</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>aa364e35af0138d35f7f705f551c740e2</anchor>
      <arglist>(const tgba_bdd_core_data &amp;d, bdd successors)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>a5896f9fb5b8f9cbd8f6605978363bda3</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>afade7f08aab50c91d14497714c03ddb5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>a0005c2c8adec1858a9c6ab2505c4f5fa</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>state_bdd *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>aeb9162fccaa85bdc5a71282908d0cc04</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>a5384ea3288ddfc065e46987c2a2c4813</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>aec308caa9f3dbca3d5d14e13d809f274</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::taa_tgba</name>
    <filename>classspot_1_1taa__tgba.html</filename>
    <base>spot::tgba</base>
    <class kind="struct">spot::taa_tgba::transition</class>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~taa_tgba</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>ab6956a578c9071cc0bd0b052529b800a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual spot::state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>a13f3d623845bf96854e276a85ad1d18f</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>aa77fd6a112f5992c9a4407f8475254d2</anchor>
      <arglist>(const spot::state *local_state, const spot::state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>a500cee2eac8f394345fcca6c50ee9645</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>ae34b7b1535fc4b5a7f00ce4c381df8c7</anchor>
      <arglist>(const spot::state *state) const =0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>a58443bde21da3ab6b5ad97185b849cf6</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>a6519608142b9e2f8735859bed8db7d95</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>a7c7f646be7454e09edb7e28bb4a30123</anchor>
      <arglist>(const spot::state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1taa__tgba.html</anchorfile>
      <anchor>a54eedc3021c0cf124288f81674a94ec1</anchor>
      <arglist>(const spot::state *state) const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::taa_tgba::transition</name>
    <filename>structspot_1_1taa__tgba_1_1transition.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::set_state</name>
    <filename>classspot_1_1set__state.html</filename>
    <base>spot::state</base>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1set__state.html</anchorfile>
      <anchor>a76c99440a7e9c16fc1707378825e8d4b</anchor>
      <arglist>(const spot::state *) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1set__state.html</anchorfile>
      <anchor>a934af3355e8f57f0cb81d3d667b382e9</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual set_state *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1set__state.html</anchorfile>
      <anchor>a5aac015e5c36a0ffcb2de4a1865120f7</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::taa_succ_iterator</name>
    <filename>classspot_1_1taa__succ__iterator.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1taa__succ__iterator.html</anchorfile>
      <anchor>a504c0ec0282beb6a7c2d2f1183d20f1c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1taa__succ__iterator.html</anchorfile>
      <anchor>ac59717005fd43ce818d7d272d1118ac7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1taa__succ__iterator.html</anchorfile>
      <anchor>a0d9ebba037a889c465a135f0021a4bf8</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual set_state *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1taa__succ__iterator.html</anchorfile>
      <anchor>a3653efa99b4e53513bdc78efcf1786ad</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1taa__succ__iterator.html</anchorfile>
      <anchor>a61631dcc09e9c3fed33b4239d8cd81cb</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1taa__succ__iterator.html</anchorfile>
      <anchor>a23c600f1b51389fc2beefae2a780a775</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::taa_tgba_labelled</name>
    <filename>classspot_1_1taa__tgba__labelled.html</filename>
    <templarg>label</templarg>
    <templarg>label_hash</templarg>
    <base>spot::taa_tgba</base>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1taa__tgba__labelled.html</anchorfile>
      <anchor>a3fb77cd8594b8c8d896210c753a01135</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>output</name>
      <anchorfile>classspot_1_1taa__tgba__labelled.html</anchorfile>
      <anchor>ad80d9a727fccd44fa76abdb05fc2b063</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual std::string</type>
      <name>label_to_string</name>
      <anchorfile>classspot_1_1taa__tgba__labelled.html</anchorfile>
      <anchor>ad5a245025d4623607818a2369908e900</anchor>
      <arglist>(const label_t &amp;lbl) const =0</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual label_t</type>
      <name>clone_if</name>
      <anchorfile>classspot_1_1taa__tgba__labelled.html</anchorfile>
      <anchor>a75b77437ad8a684f575125c16e914985</anchor>
      <arglist>(const label_t &amp;lbl) const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::taa_tgba_string</name>
    <filename>classspot_1_1taa__tgba__string.html</filename>
    <base>taa_tgba_labelled&lt; std::string, string_hash &gt;</base>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1taa__tgba__labelled.html</anchorfile>
      <anchor>a3fb77cd8594b8c8d896210c753a01135</anchor>
      <arglist>(const spot::state *s) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>output</name>
      <anchorfile>classspot_1_1taa__tgba__labelled.html</anchorfile>
      <anchor>ad80d9a727fccd44fa76abdb05fc2b063</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual std::string</type>
      <name>label_to_string</name>
      <anchorfile>classspot_1_1taa__tgba__string.html</anchorfile>
      <anchor>a8899ed75f067bbaa4d959e2d5b8f982a</anchor>
      <arglist>(const std::string &amp;label) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual std::string</type>
      <name>clone_if</name>
      <anchorfile>classspot_1_1taa__tgba__string.html</anchorfile>
      <anchor>a12895103adb6c7f520f21ca9769be50a</anchor>
      <arglist>(const std::string &amp;label) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::taa_tgba_formula</name>
    <filename>classspot_1_1taa__tgba__formula.html</filename>
    <base>taa_tgba_labelled&lt; const ltl::formula *, ltl::formula_ptr_hash &gt;</base>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1taa__tgba__labelled.html</anchorfile>
      <anchor>a3fb77cd8594b8c8d896210c753a01135</anchor>
      <arglist>(const spot::state *s) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>output</name>
      <anchorfile>classspot_1_1taa__tgba__labelled.html</anchorfile>
      <anchor>ad80d9a727fccd44fa76abdb05fc2b063</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual std::string</type>
      <name>label_to_string</name>
      <anchorfile>classspot_1_1taa__tgba__formula.html</anchorfile>
      <anchor>a9bdf42bc781816427afa434e19cd9ef8</anchor>
      <arglist>(const label_t &amp;label) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual const ltl::formula *</type>
      <name>clone_if</name>
      <anchorfile>classspot_1_1taa__tgba__formula.html</anchorfile>
      <anchor>a9a852208441c31e096c7b0ede61917a7</anchor>
      <arglist>(const label_t &amp;label) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba</name>
    <filename>classspot_1_1tgba.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::tgba_bdd_concrete</name>
    <filename>classspot_1_1tgba__bdd__concrete.html</filename>
    <base>spot::tgba</base>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_concrete</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a38abffd64eeecace04ed61471370f759</anchor>
      <arglist>(const tgba_bdd_factory &amp;fact)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_concrete</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a5daeb83641ac0a3edf39d16dd27d2a0f</anchor>
      <arglist>(const tgba_bdd_factory &amp;fact, bdd init)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>set_init_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>afa8e0f6c33006b3b3afb997a1a570ed2</anchor>
      <arglist>(bdd s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_bdd *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a90ba591caf7752240e11cfc7c30d8e65</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>get_init_bdd</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a76d420c40bc8cd4ee2f0e0269134d7a3</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator_concrete *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>ae1a905bd60233fd94c8a693519239308</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>aabe8293cf460853659bae84283f07895</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>ae6efdd09761a0c8f62f17ce88f237527</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const tgba_bdd_core_data &amp;</type>
      <name>get_core_data</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>af6a5daf37a38e1496e04af6766a2524e</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>afada031707788ff70a99c7c1b5c23476</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a64575d12a09c13735d161d7c1bd0313d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>delete_unaccepting_scc</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>ae812c7452c83cc608bbfb5485c52d599</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a882cd2a26d0fa9d5da1273fb205d6b37</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a654b132604cc4001bca8050f7e47bbf7</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>tgba_bdd_core_data</type>
      <name>data_</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>ae9f128a7964713decc50a18934691e5c</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>init_</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a7e99671d794c73c99d29b2c744e4d5f4</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_bdd_concrete_factory</name>
    <filename>classspot_1_1tgba__bdd__concrete__factory.html</filename>
    <base>spot::tgba_bdd_factory</base>
    <member kind="function">
      <type>int</type>
      <name>create_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>ac6cf312ceb992728e78e37bdc4ed0336</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>create_anonymous_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>a27b93af55fd747d17e8c85b74b195975</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>create_atomic_prop</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>aeec16411072546e89dc612ef139b19a9</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>aa83197dccba164a208cf22b1c643d5a6</anchor>
      <arglist>(bdd b, const ltl::formula *a)</arglist>
    </member>
    <member kind="function">
      <type>const tgba_bdd_core_data &amp;</type>
      <name>get_core_data</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>a6a7f60a1e8e45d43cac26516795a98fb</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>constrain_relation</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>aedb7c77666bfad9ee507254b8b382571</anchor>
      <arglist>(bdd new_rel)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>finish</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>a8c575ec7e41eafc29c7fb4ca439b67c9</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_bdd_core_data</name>
    <filename>structspot_1_1tgba__bdd__core__data.html</filename>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_core_data</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>ac4d1e835b96ab8264aac2a2ed50f73df</anchor>
      <arglist>(bdd_dict *dict)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_core_data</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>ac236f34dee1ce44a31ca7199cad9face</anchor>
      <arglist>(const tgba_bdd_core_data &amp;copy)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_core_data</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a0a49ac42ea3111e3e14df59a0350ee33</anchor>
      <arglist>(const tgba_bdd_core_data &amp;left, const tgba_bdd_core_data &amp;right)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_now_next</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>ae9804355c508876e8bd04d6b119c0e4a</anchor>
      <arglist>(bdd now, bdd next)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_atomic_prop</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>ade918f045d34a51e000158f73c698072</anchor>
      <arglist>(bdd var)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_acceptance_condition</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a668e6bbc7dfc65d231db6694fe256b4a</anchor>
      <arglist>(bdd prom)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>delete_unaccepting_scc</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a5d207815a5512733ae0cfaf2febe86e3</anchor>
      <arglist>(bdd init)</arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>relation</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>aa304f53084eadf2a73b2ed16fd63eeb4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acceptance_conditions</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a8a693bbcaa794d1503c667bb56905a1e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acceptance_conditions_support</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a86c46e456ca25f64684550acc5262ada</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>aabee4b6acc70a6a9dd7c4193cf70b648</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>now_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a57f5083712b3f2ff3bbc05236df0ba9d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>next_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a3ced731d0d9eeaf380b681fae44c3dec</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>nownext_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>aeb96c7c0d8901082c3ee7af12cd1b0cc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>notnow_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a3f647bc2217a35cd5b41ea442b5f0fa1</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>notnext_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a95d4747f62d7ef1348d89b370318b02d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>var_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>aa1bc747a58e8d64bbb94c735a66548c4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>notvar_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a2e83403899aa907ac4c3e3b90749a252</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>varandnext_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a40e40af7150cc431d3f17c71d157f452</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acc_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a997858217ebc0cbc304258fe53d5c990</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>notacc_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a12e79ba7db251dd24bac7849bcc840fc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>negacc_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a27ea4bcb390c7b2c54b547444d67ae56</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd_dict *</type>
      <name>dict</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a06a48a4843cbec690ad5ef99d12eb1ea</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_bdd_factory</name>
    <filename>classspot_1_1tgba__bdd__factory.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual const tgba_bdd_core_data &amp;</type>
      <name>get_core_data</name>
      <anchorfile>classspot_1_1tgba__bdd__factory.html</anchorfile>
      <anchor>ae219a33c2fb16b4c4bb7080581d083dc</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::destroy_key</name>
    <filename>structspot_1_1destroy__key.html</filename>
    <templarg></templarg>
  </compound>
  <compound kind="struct">
    <name>spot::destroy_key&lt; const ltl::formula * &gt;</name>
    <filename>structspot_1_1destroy__key_3_01const_01ltl_1_1formula_01_5_01_4.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::state_explicit</name>
    <filename>classspot_1_1state__explicit.html</filename>
    <templarg>Label</templarg>
    <templarg>label_hash</templarg>
    <base>spot::state</base>
    <class kind="struct">spot::state_explicit::transition</class>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>destroy</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>acfaec036581d609b822c3098641b2340</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>a1fc57e536139259c0cb12a2ec360c6a4</anchor>
      <arglist>(const state *other) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>a011b10e996795c993c02666803ab2a06</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_explicit&lt; Label, label_hash &gt; *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>afecf9b6317b0729e40c7d4a03437bafe</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::state_explicit::transition</name>
    <filename>structspot_1_1state__explicit_1_1transition.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::state_explicit_number</name>
    <filename>classspot_1_1state__explicit__number.html</filename>
    <base>state_explicit&lt; int, identity_hash&lt; int &gt; &gt;</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>destroy</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>acfaec036581d609b822c3098641b2340</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>a1fc57e536139259c0cb12a2ec360c6a4</anchor>
      <arglist>(const state *other) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>a011b10e996795c993c02666803ab2a06</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_explicit&lt; int, identity_hash&lt; int &gt; &gt; *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>afecf9b6317b0729e40c7d4a03437bafe</anchor>
      <arglist>() const</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_explicit_string</name>
    <filename>classspot_1_1state__explicit__string.html</filename>
    <base>state_explicit&lt; std::string, string_hash &gt;</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>destroy</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>acfaec036581d609b822c3098641b2340</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>a1fc57e536139259c0cb12a2ec360c6a4</anchor>
      <arglist>(const state *other) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>a011b10e996795c993c02666803ab2a06</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_explicit&lt; std::string, string_hash &gt; *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>afecf9b6317b0729e40c7d4a03437bafe</anchor>
      <arglist>() const</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_explicit_formula</name>
    <filename>classspot_1_1state__explicit__formula.html</filename>
    <base>state_explicit&lt; const ltl::formula *, ltl::formula_ptr_hash &gt;</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>destroy</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>acfaec036581d609b822c3098641b2340</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>a1fc57e536139259c0cb12a2ec360c6a4</anchor>
      <arglist>(const state *other) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>a011b10e996795c993c02666803ab2a06</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_explicit&lt; const ltl::formula *, ltl::formula_ptr_hash &gt; *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>afecf9b6317b0729e40c7d4a03437bafe</anchor>
      <arglist>() const</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_explicit_succ_iterator</name>
    <filename>classspot_1_1tgba__explicit__succ__iterator.html</filename>
    <templarg>State</templarg>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>aba7320445c30a4c73847d8ca9eecf86b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>a3fde92223e7fed5149ca9369acd4765b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>a9154acf28bc1fdbfa703ad640276d1ea</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual State *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>a5aecac9f99eb3c8c485100f9c30e22b3</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>a690faff50371bd40a9f34c40ea14f5c4</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>a65f16bf982246123ea7c51e2ec914cd6</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::explicit_graph</name>
    <filename>classspot_1_1explicit__graph.html</filename>
    <templarg>State</templarg>
    <templarg>Type</templarg>
    <member kind="function">
      <type>void</type>
      <name>add_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>abc0817f65d9694dea39dde4f87a05808</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
    <member kind="function">
      <type>const State *</type>
      <name>get_state</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a0117a5421c10e7be17303261bd8e91f7</anchor>
      <arglist>(const label_t &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>State *</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a812a6dca9bde6bd5283dfe0ac05386f2</anchor>
      <arglist>(const label_t &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_state_alias</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a56b9a9e1c99b3ddf0117c1092a5d8641</anchor>
      <arglist>(const label_t &amp;alias, const label_t &amp;real)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>copy_acceptance_conditions_of</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a6ed023d767a97f41836787123cd82dce</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set_acceptance_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a8237bac44dabf3ced482b99a65f72530</anchor>
      <arglist>(bdd acc)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_acceptance_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>ad36ec6dd37dbbb1b1f64ad6b9d22e35f</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_explicit</name>
    <filename>classspot_1_1tgba__explicit.html</filename>
    <templarg>State</templarg>
    <base>explicit_graph&lt; State, tgba &gt;</base>
    <member kind="function">
      <type>void</type>
      <name>add_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>abc0817f65d9694dea39dde4f87a05808</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
    <member kind="function">
      <type>const State *</type>
      <name>get_state</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a0117a5421c10e7be17303261bd8e91f7</anchor>
      <arglist>(const label_t &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>State *</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a812a6dca9bde6bd5283dfe0ac05386f2</anchor>
      <arglist>(const label_t &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_state_alias</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a56b9a9e1c99b3ddf0117c1092a5d8641</anchor>
      <arglist>(const label_t &amp;alias, const label_t &amp;real)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>copy_acceptance_conditions_of</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a6ed023d767a97f41836787123cd82dce</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set_acceptance_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a8237bac44dabf3ced482b99a65f72530</anchor>
      <arglist>(bdd acc)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_acceptance_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>ad36ec6dd37dbbb1b1f64ad6b9d22e35f</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::sba_explicit</name>
    <filename>classspot_1_1sba__explicit.html</filename>
    <templarg>State</templarg>
    <base>explicit_graph&lt; State, sba &gt;</base>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>state_is_accepting</name>
      <anchorfile>classspot_1_1sba__explicit.html</anchorfile>
      <anchor>a7e36b17d6cfb4925b834e5aa25303be0</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>abc0817f65d9694dea39dde4f87a05808</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
    <member kind="function">
      <type>const State *</type>
      <name>get_state</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a0117a5421c10e7be17303261bd8e91f7</anchor>
      <arglist>(const label_t &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>State *</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a812a6dca9bde6bd5283dfe0ac05386f2</anchor>
      <arglist>(const label_t &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_state_alias</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a56b9a9e1c99b3ddf0117c1092a5d8641</anchor>
      <arglist>(const label_t &amp;alias, const label_t &amp;real)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>copy_acceptance_conditions_of</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a6ed023d767a97f41836787123cd82dce</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set_acceptance_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>a8237bac44dabf3ced482b99a65f72530</anchor>
      <arglist>(bdd acc)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_acceptance_conditions</name>
      <anchorfile>classspot_1_1explicit__graph.html</anchorfile>
      <anchor>ad36ec6dd37dbbb1b1f64ad6b9d22e35f</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::explicit_conf</name>
    <filename>classspot_1_1explicit__conf.html</filename>
    <templarg></templarg>
    <templarg></templarg>
  </compound>
  <compound kind="class">
    <name>spot::explicit_conf&lt; graph, state_explicit_string &gt;</name>
    <filename>classspot_1_1explicit__conf_3_01graph_00_01state__explicit__string_01_4.html</filename>
    <templarg></templarg>
  </compound>
  <compound kind="class">
    <name>spot::explicit_conf&lt; graph, state_explicit_formula &gt;</name>
    <filename>classspot_1_1explicit__conf_3_01graph_00_01state__explicit__formula_01_4.html</filename>
    <templarg></templarg>
  </compound>
  <compound kind="class">
    <name>spot::bdd_ordered</name>
    <filename>classspot_1_1bdd__ordered.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::tgba_kv_complement</name>
    <filename>classspot_1_1tgba__kv__complement.html</filename>
    <base>spot::tgba</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__kv__complement.html</anchorfile>
      <anchor>a1cfed0a3f4f91aa9d2bc1303f3518658</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__kv__complement.html</anchorfile>
      <anchor>a972b4c688c8546ff84aefdbe9fdfadb6</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__kv__complement.html</anchorfile>
      <anchor>a37d82b9f490cbca4c97e9d15dc84ead9</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__kv__complement.html</anchorfile>
      <anchor>a60fc666c9815caac2baf6d694007dc9d</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__kv__complement.html</anchorfile>
      <anchor>a788bd1b846a0ade82a804e32da243670</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__kv__complement.html</anchorfile>
      <anchor>af56a4f915209d8d2c7e55da9645cb9df</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__kv__complement.html</anchorfile>
      <anchor>a4914a7a0bf431001a095eaf53390a0da</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__kv__complement.html</anchorfile>
      <anchor>a2cfbecf00af6c5b698d0f140c6b248d1</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_mask</name>
    <filename>classspot_1_1tgba__mask.html</filename>
    <base>spot::tgba_proxy</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__mask.html</anchorfile>
      <anchor>a1f089b02266696196b2b3072894c377d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__mask.html</anchorfile>
      <anchor>aa7e9e058b6c5932811aa94c8aedbff6e</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>a85dc6a8caecee1becd63add0a1654166</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>ad5fd0a71e4aecd223ca0ec8feecd921b</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>a50a2360208178f078bb6aaf9f257b3dd</anchor>
      <arglist>(const tgba_succ_iterator *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>ac49527796c5c4aee290030a6f4396ce3</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>ab1bbb98b4a429af405e715ce35257b08</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>a3b3644dae795b22e9da024a8ad8b5d32</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>tgba_mask</name>
      <anchorfile>classspot_1_1tgba__mask.html</anchorfile>
      <anchor>a96ff36aa7d0fc3e5087af0593c188c23</anchor>
      <arglist>(const tgba *masked, const state *init=0)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>a6e030e9b77c5cd8a21c87e770766165d</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>a97ac5052137d46054e24179a6a78c6db</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_product</name>
    <filename>classspot_1_1state__product.html</filename>
    <base>spot::state</base>
    <member kind="function">
      <type></type>
      <name>state_product</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>ae87b3a01f724267993ab53d9a3c11907</anchor>
      <arglist>(state *left, state *right, fixed_size_pool *pool)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>destroy</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>a15b1df31022f8ac8a14f8db13c8fb0bb</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>a1ff87a76c6f956b027f6191cac8c462e</anchor>
      <arglist>(const state *other) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>a4a3ad4053c86d85c02faad1f3ee229eb</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_product *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>aa42c2234459b1b60b14782b1a209f9b9</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_product</name>
    <filename>classspot_1_1tgba__product.html</filename>
    <base>spot::tgba</base>
    <member kind="function">
      <type></type>
      <name>tgba_product</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>ac87cc8973de290dfb03e4af05b6f18aa</anchor>
      <arglist>(const tgba *left, const tgba *right)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a7c82db48b43c65e11637cc4e7608298b</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>a3619b6770ca99f736db1d22e424f93e4</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_product_init</name>
    <filename>classspot_1_1tgba__product__init.html</filename>
    <base>spot::tgba_product</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__product__init.html</anchorfile>
      <anchor>a2ff20add5355ec8b4334236652c22d2c</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_proxy</name>
    <filename>classspot_1_1tgba__proxy.html</filename>
    <base>spot::tgba</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>a11c81a246a19c6ce50e8360ac75846a1</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__proxy.html</anchorfile>
      <anchor>ae3d48b773c166146bae455d504faad37</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_safra_complement</name>
    <filename>classspot_1_1tgba__safra__complement.html</filename>
    <base>spot::tgba</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__safra__complement.html</anchorfile>
      <anchor>a4416dc1a96843c9748f40c8f4e740e6c</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__safra__complement.html</anchorfile>
      <anchor>ab7d78122fa470aeef3f84e98f0598429</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__safra__complement.html</anchorfile>
      <anchor>a1806070d03e6ffd340f3015ba5843ca4</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__safra__complement.html</anchorfile>
      <anchor>a59271880c56bcd5b8b9eedb78687f4d0</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__safra__complement.html</anchorfile>
      <anchor>a77029bbc877ac88dbbee8876f986a914</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__safra__complement.html</anchorfile>
      <anchor>a0e217a50bab2a0f988ec88ce774637f9</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__safra__complement.html</anchorfile>
      <anchor>ab31d07db53c668fda1f62033046eadaf</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__safra__complement.html</anchorfile>
      <anchor>abdc51afd4833e415ade77e38e95a5bd3</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_scc</name>
    <filename>classspot_1_1tgba__scc.html</filename>
    <base>spot::tgba</base>
    <member kind="function">
      <type></type>
      <name>tgba_scc</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a86791ac78a572450d0715536f3cb2d2b</anchor>
      <arglist>(const tgba *aut, bool show=false)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__scc.html</anchorfile>
      <anchor>a9f3d7b929852fc1aec87e37d728b8ac1</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_sgba_proxy</name>
    <filename>classspot_1_1tgba__sgba__proxy.html</filename>
    <base>spot::tgba</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>a18bc8545601817b1fdf071f8748972f2</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>a285e748dd6b46066b18529cb244e2167</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>aa4813fb6f7f34a829c6a53d11c01557e</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>ab429ebb43292fdb39f624ca63bc2e357</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>a56288756cbbf248be79c18a0b0d9c78a</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>ac5fa8b86dbf32d2d63949c40a749b608</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>state_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>a3b140a4fa421103c915f4f23ca7d7e6e</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>a96254fd6f5a1265b4af49d6e907a1025</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__sgba__proxy.html</anchorfile>
      <anchor>a43ba1721edcd8d1082b6a3b4d0aaedf4</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_tba_proxy</name>
    <filename>classspot_1_1tgba__tba__proxy.html</filename>
    <base>spot::tgba</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a68713d24694ec70ed477dc145a550e78</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>ad2a2082d9686c77b5aae8b236a6d8479</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a2582a3298c12e384640bed93a7dd90ac</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a61d88f823e3d1eabd35f45ae57975d1c</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>ad4bba2f97a28446d59e83a37ddfde1be</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a21a2975a60ede4b62748e685dcc8d1af</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>ad3275433431b1995354a801d2a66feac</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>common_acceptance_conditions_of_original_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a89b554fc92de63ac6dd93af84e38e06d</anchor>
      <arglist>(const state *ostate) const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>union_acceptance_conditions_of_original_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a5ebc7c65d11e01d0c951f3fef70a9c1b</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function">
      <type>state *</type>
      <name>create_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a8734bd9164142721c8eb5fd9037d4dc8</anchor>
      <arglist>(state *s, cycle_list::const_iterator acc) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a273341ec958163d8548709673ebe6077</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>a52e9928cbc1230d566abfc55c1eb7619</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_sba_proxy</name>
    <filename>classspot_1_1tgba__sba__proxy.html</filename>
    <base>spot::tgba_tba_proxy</base>
    <member kind="function">
      <type>bool</type>
      <name>state_is_accepting</name>
      <anchorfile>classspot_1_1tgba__sba__proxy.html</anchorfile>
      <anchor>a722f8b8480c44ede6318ccb8642c75df</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__sba__proxy.html</anchorfile>
      <anchor>a9c3b5e8f7b60839c13e7698b19759c94</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_union</name>
    <filename>classspot_1_1state__union.html</filename>
    <base>spot::state</base>
    <member kind="function">
      <type></type>
      <name>state_union</name>
      <anchorfile>classspot_1_1state__union.html</anchorfile>
      <anchor>a78c94aaadc1e1b18f3e91ae8eb8ef2c4</anchor>
      <arglist>(state *left, state *right)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>state_union</name>
      <anchorfile>classspot_1_1state__union.html</anchorfile>
      <anchor>a897ecfa75be60bea2c05ea3cdc70cb03</anchor>
      <arglist>(const state_union &amp;o)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__union.html</anchorfile>
      <anchor>a5d0e3a98f2d32cfce0c42c666cb941f3</anchor>
      <arglist>(const state *other) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__union.html</anchorfile>
      <anchor>ae0daf8ce6d65d4df80f47587fee73739</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_union *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__union.html</anchorfile>
      <anchor>aaaba05ebd3c387a259e0c448e61e53d6</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_succ_iterator_union</name>
    <filename>classspot_1_1tgba__succ__iterator__union.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function">
      <type>void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__union.html</anchorfile>
      <anchor>ad732eabed9ca5a8e3b12756b33284dc9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__union.html</anchorfile>
      <anchor>aeefd5a6bec455088541bac5bcc1852f4</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__union.html</anchorfile>
      <anchor>abd82caae4abf6d1e3b9aa72ad59e3970</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>state_union *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__union.html</anchorfile>
      <anchor>a91f4afdb55eebfad06514098f93322f0</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__union.html</anchorfile>
      <anchor>a9f712d0a04142c0466d30ddd42f0cca3</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__union.html</anchorfile>
      <anchor>abb4481a52e5384ee45956086c29703f9</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_union</name>
    <filename>classspot_1_1tgba__union.html</filename>
    <base>spot::tgba</base>
    <member kind="function">
      <type></type>
      <name>tgba_union</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>a6d45afa2fa0f93463aa7a6f0134fcd92</anchor>
      <arglist>(const tgba *left, const tgba *right)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>a8bfdf237574f4f35f91babef0135499d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator_union *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>aab204c7193332dd43f4f193d081f127e</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>a57b3a6adfae3d13c2a86a119f13dd434</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>a1a3d0e84a4c7d7c60f9fd3616951551d</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>ac0c4daabbb8989ff5930937b7db513fa</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>a524a1240c56f28f6e8e660c1f412970a</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>ad143fa255e6913368a7f41d961d4eb0d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>a1763a39ed0119ecf7d6b1490eb57b257</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__union.html</anchorfile>
      <anchor>af37d0c292e9c3f97ddcacdc321dd5d86</anchor>
      <arglist>(const state *state) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::bfs_steps</name>
    <filename>classspot_1_1bfs__steps.html</filename>
    <member kind="function">
      <type>const state *</type>
      <name>search</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>a6efc199a86fc9ad53c2220964facd53a</anchor>
      <arglist>(const state *start, tgba_run::steps &amp;l)</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>filter</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>a12319da13fb0fde22376522fc416e833</anchor>
      <arglist>(const state *s)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>match</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>ad94ee7be9b944e52d4def759eb559868</anchor>
      <arglist>(tgba_run::step &amp;step, const state *dest)=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>finalize</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>aaef31a86c98ad2ead6e4cf4aac5ed8b2</anchor>
      <arglist>(const std::map&lt; const state *, tgba_run::step, state_ptr_less_than &gt; &amp;father, const tgba_run::step &amp;s, const state *start, tgba_run::steps &amp;l)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>a2340b458a0f9af2da0e5b74120e7f3b7</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::sccs_set</name>
    <filename>structspot_1_1sccs__set.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::enumerate_cycles</name>
    <filename>classspot_1_1enumerate__cycles.html</filename>
    <class kind="struct">spot::enumerate_cycles::dfs_entry</class>
    <class kind="struct">spot::enumerate_cycles::state_info</class>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1enumerate__cycles.html</anchorfile>
      <anchor>a8183feda2fb77c100c3b5ec23701ab44</anchor>
      <arglist>(unsigned scc)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>cycle_found</name>
      <anchorfile>classspot_1_1enumerate__cycles.html</anchorfile>
      <anchor>adac22e51f24e114a87f87e00df7679d0</anchor>
      <arglist>(const state *start)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::enumerate_cycles::dfs_entry</name>
    <filename>structspot_1_1enumerate__cycles_1_1dfs__entry.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::enumerate_cycles::state_info</name>
    <filename>structspot_1_1enumerate__cycles_1_1state__info.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::dotty_decorator</name>
    <filename>classspot_1_1dotty__decorator.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>state_decl</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>ab39acdd2d4dc5c5e5fa04e1154cf3209</anchor>
      <arglist>(const tgba *a, const state *s, int n, tgba_succ_iterator *si, const std::string &amp;label, bool accepting)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>link_decl</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>aab4c4e8f63648c09469be5145098af91</anchor>
      <arglist>(const tgba *a, const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si, const std::string &amp;label)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static dotty_decorator *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>a3f65ca9c0c1a37d7aba531c243f52c6e</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::emptiness_check_result</name>
    <filename>classspot_1_1emptiness__check__result.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_run *</type>
      <name>accepting_run</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a909ae24b5e8454cb966d320e997570ab</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a36de02afca3bba529baaf091af8d2575</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a5480637d90d73e3e67e2891cb7468d20</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a66f0c4f9074073e8d26ac764d6103d9f</anchor>
      <arglist>(char *options)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const unsigned_statistics *</type>
      <name>statistics</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a0d0bebd88677d1a0221f8263692f1b17</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>options_updated</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a3ed3330145ca9d988eeca2e5bf773dad</anchor>
      <arglist>(const option_map &amp;old)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a87ce44800ef5cb67e181f186f3d3dcbc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a42a0ad82518ab92ce6ea2166c119e4cc</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::emptiness_check</name>
    <filename>classspot_1_1emptiness__check.html</filename>
    <member kind="function">
      <type>const tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>a54c8f2e6ba519944e19f75e74d6d93da</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>a892ab07ce172c1d6deda9f0802b9eaa6</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>ad0e61dab3b4593d493da86ee625a25c3</anchor>
      <arglist>(char *options)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>safe</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>a217e667676d60e9f6f4fa7ba923925c0</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual emptiness_check_result *</type>
      <name>check</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>ad573f9d7402dfdc44959cd183ae81774</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const unsigned_statistics *</type>
      <name>statistics</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>a35f1119514acb36b217a7701bbf4b6f0</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::ostream &amp;</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>a67a72e44b2fa859aef3c23c052512924</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>options_updated</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>aac525dce9e038b424a1f680b797e2639</anchor>
      <arglist>(const option_map &amp;old)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>a315fb003ae56a13d26dc8ffb03a34ed6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>a2bf27940474ed0e6ea39a6f8b6c7fcc0</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::emptiness_check_instantiator</name>
    <filename>classspot_1_1emptiness__check__instantiator.html</filename>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>instantiate</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>a4af6350795c509ae155159fa08200fd3</anchor>
      <arglist>(const tgba *a) const </arglist>
    </member>
    <member kind="function">
      <type>unsigned int</type>
      <name>min_acceptance_conditions</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>a356319c9ddc0116d0fb528abcd1d751f</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>unsigned int</type>
      <name>max_acceptance_conditions</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>ae7172afd92e323421cf34e80ee767b58</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>a795a1701f756da2019925528248697e7</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static emptiness_check_instantiator *</type>
      <name>construct</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>ad454d1d69ca258b2fc68222436eb62a3</anchor>
      <arglist>(const char *name, const char **err)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_run</name>
    <filename>structspot_1_1tgba__run.html</filename>
    <class kind="struct">spot::tgba_run::step</class>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_run::step</name>
    <filename>structspot_1_1tgba__run_1_1step.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::unsigned_statistics</name>
    <filename>structspot_1_1unsigned__statistics.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::unsigned_statistics_copy</name>
    <filename>classspot_1_1unsigned__statistics__copy.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ec_statistics</name>
    <filename>classspot_1_1ec__statistics.html</filename>
    <base>spot::unsigned_statistics</base>
  </compound>
  <compound kind="class">
    <name>spot::ars_statistics</name>
    <filename>classspot_1_1ars__statistics.html</filename>
    <base>spot::unsigned_statistics</base>
  </compound>
  <compound kind="class">
    <name>spot::acss_statistics</name>
    <filename>classspot_1_1acss__statistics.html</filename>
    <base>spot::ars_statistics</base>
    <member kind="function" virtualness="pure">
      <type>virtual unsigned</type>
      <name>acss_states</name>
      <anchorfile>classspot_1_1acss__statistics.html</anchorfile>
      <anchor>a013cda6ae3aece6bbb7ffbc61a33fffb</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::couvreur99_check_result</name>
    <filename>classspot_1_1couvreur99__check__result.html</filename>
    <base>spot::emptiness_check_result</base>
    <base>spot::acss_statistics</base>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_run *</type>
      <name>accepting_run</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>a2ad4b0f54841a33797c336505109ee16</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned</type>
      <name>acss_states</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>a2b97b342b5e84b3d6cd0fc95b42be43b</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>accepting_cycle</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>aacf9472e522e742957e29d29453f64a7</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::explicit_connected_component</name>
    <filename>classspot_1_1explicit__connected__component.html</filename>
    <base>spot::scc_stack::connected_component</base>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>has_state</name>
      <anchorfile>classspot_1_1explicit__connected__component.html</anchorfile>
      <anchor>a32befd8aef0876c44d45cb78d5955272</anchor>
      <arglist>(const state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1explicit__connected__component.html</anchorfile>
      <anchor>a65ca837e0460d52c9346244fb7c98fe2</anchor>
      <arglist>(const state *s)=0</arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>index</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>a58d4077c0bb19d470764be5e79a9adf0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>condition</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>aa753864e5e55807ac67794a9999873b9</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::connected_component_hash_set</name>
    <filename>classspot_1_1connected__component__hash__set.html</filename>
    <base>spot::explicit_connected_component</base>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>has_state</name>
      <anchorfile>classspot_1_1connected__component__hash__set.html</anchorfile>
      <anchor>a44422952ba14017f0933f7434739fdb7</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1connected__component__hash__set.html</anchorfile>
      <anchor>af8544e2de3bddac0dfa923a3788a5fd0</anchor>
      <arglist>(const state *s)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::explicit_connected_component_factory</name>
    <filename>classspot_1_1explicit__connected__component__factory.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual explicit_connected_component *</type>
      <name>build</name>
      <anchorfile>classspot_1_1explicit__connected__component__factory.html</anchorfile>
      <anchor>a94bff811308cd87633dc9b781e8d5f3c</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::connected_component_hash_set_factory</name>
    <filename>classspot_1_1connected__component__hash__set__factory.html</filename>
    <base>spot::explicit_connected_component_factory</base>
    <member kind="function" virtualness="virtual">
      <type>virtual connected_component_hash_set *</type>
      <name>build</name>
      <anchorfile>classspot_1_1connected__component__hash__set__factory.html</anchorfile>
      <anchor>a68cf04582b84a6d851f19b2697bc8c1b</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const connected_component_hash_set_factory *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1connected__component__hash__set__factory.html</anchorfile>
      <anchor>a202e65c89fe1b43c762f5d65bce82ac7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>connected_component_hash_set_factory</name>
      <anchorfile>classspot_1_1connected__component__hash__set__factory.html</anchorfile>
      <anchor>aa29f42e74183d72b27d2652975bc93f4</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::couvreur99_check</name>
    <filename>classspot_1_1couvreur99__check.html</filename>
    <base>spot::emptiness_check</base>
    <base>spot::ec_statistics</base>
    <member kind="function" virtualness="virtual">
      <type>virtual emptiness_check_result *</type>
      <name>check</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>acd2571ab75300c53ea1b9f5945c6bbcc</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::ostream &amp;</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>a659be4b0219537e8089f7196cf5bbf4d</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
    <member kind="function">
      <type>const couvreur99_check_status *</type>
      <name>result</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>a1586281a720baeaa2cf538fc5a12cd6a</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove_component</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>a3b48d6451f133c57c7d62b8946d6339b</anchor>
      <arglist>(const state *start_delete)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>poprem_</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>a13a1e31c34889d7a97cea7c7a0097416</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>unsigned</type>
      <name>removed_components</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>afef1267ab1a6dbb03961e88cc718da42</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::couvreur99_check_shy</name>
    <filename>classspot_1_1couvreur99__check__shy.html</filename>
    <base>spot::couvreur99_check</base>
    <class kind="struct">spot::couvreur99_check_shy::successor</class>
    <class kind="struct">spot::couvreur99_check_shy::todo_item</class>
    <member kind="function" virtualness="virtual">
      <type>virtual emptiness_check_result *</type>
      <name>check</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>a46acfebce7513b47f8d6d98f42dcefb9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>dump_queue</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>a4df98a24aa2099c639d6f8dfdc4224a4</anchor>
      <arglist>(std::ostream &amp;os=std::cerr)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual numbered_state_heap::state_index_p</type>
      <name>find_state</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>a1e9cf243327a7f1c4f1034bf61735638</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>group_</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>a112c0863e8f602e5dddcc222d4250b7a</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::couvreur99_check_shy::successor</name>
    <filename>structspot_1_1couvreur99__check__shy_1_1successor.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::couvreur99_check_shy::todo_item</name>
    <filename>structspot_1_1couvreur99__check__shy_1_1todo__item.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap_const_iterator</name>
    <filename>classspot_1_1numbered__state__heap__const__iterator.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1numbered__state__heap__const__iterator.html</anchorfile>
      <anchor>a10ddb31c77032b460509153e2fc8c1fe</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>get_state</name>
      <anchorfile>classspot_1_1numbered__state__heap__const__iterator.html</anchorfile>
      <anchor>ae6c21c4128258f565aca57c74e301be1</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap</name>
    <filename>classspot_1_1numbered__state__heap.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>ae1190a20a23bf805c00a9a030c1fa44c</anchor>
      <arglist>(const state *s, int index)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual int</type>
      <name>size</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>a6967c26452451766938f8a16af188b0c</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual numbered_state_heap_const_iterator *</type>
      <name>iterator</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>a71766568e653529969ae47733758db46</anchor>
      <arglist>() const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state_index</type>
      <name>find</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>ab6616967723c7233740d4dcef2e3f145</anchor>
      <arglist>(const state *s) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state_index</type>
      <name>index</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>a30ac36612181bf3b0c1f7a0ae108e692</anchor>
      <arglist>(const state *s) const =0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap_factory</name>
    <filename>classspot_1_1numbered__state__heap__factory.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap_hash_map</name>
    <filename>classspot_1_1numbered__state__heap__hash__map.html</filename>
    <base>spot::numbered_state_heap</base>
    <member kind="function" virtualness="virtual">
      <type>virtual state_index</type>
      <name>find</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>a33c53af9be271e188f2a96f3766eaa1a</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_index</type>
      <name>index</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>a7b2a1636b4e5eed2319d379f2f06e969</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>acf08fbf1f29d09fdc8883768feb625f9</anchor>
      <arglist>(const state *s, int index)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>size</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>a80b69aa80d6ec021f7be03f705653f5b</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual numbered_state_heap_const_iterator *</type>
      <name>iterator</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>adf07e9a27deb8d28a5c7554cb045bcf6</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>hash_type</type>
      <name>h</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>afb219195433667abe491130fa3acd6a3</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap_hash_map_factory</name>
    <filename>classspot_1_1numbered__state__heap__hash__map__factory.html</filename>
    <base>spot::numbered_state_heap_factory</base>
    <member kind="function" static="yes">
      <type>static const numbered_state_heap_hash_map_factory *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map__factory.html</anchorfile>
      <anchor>ac9ff2d24e71f763cbd999e09be6aa518</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::scc_stack</name>
    <filename>classspot_1_1scc__stack.html</filename>
    <class kind="struct">spot::scc_stack::connected_component</class>
    <member kind="function">
      <type>void</type>
      <name>push</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>ab41e8e1aad7d4ca3940f1eaf8064a0c2</anchor>
      <arglist>(int index)</arglist>
    </member>
    <member kind="function">
      <type>connected_component &amp;</type>
      <name>top</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>a9fb956e08441d13f89442ac99974b6af</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const connected_component &amp;</type>
      <name>top</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>a1fcb202de7bce708b0616ce9ad9a1bdb</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>pop</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>a2da83367ca277053968ff856f3c6295b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>size</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>a627055834783bbe23e93aa061bc5f7d3</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>std::list&lt; const state * &gt; &amp;</type>
      <name>rem</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>a74f1cf725431e87096f4823bc0d19bcf</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>clear_rem</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>a939ef574064e26b22df1a48d3e52cf27</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>empty</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>af60619c5be8a175029f309481f56588f</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::scc_stack::connected_component</name>
    <filename>structspot_1_1scc__stack_1_1connected__component.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::couvreur99_check_status</name>
    <filename>classspot_1_1couvreur99__check__status.html</filename>
    <member kind="function">
      <type>void</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>ac85eb5d7ae5fe26defdf157f12a57e2f</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>states</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>ac0dad048c592f621dd9cbc8e76c72d3d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="variable">
      <type>numbered_state_heap *</type>
      <name>h</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>a015a6b109e569b398bd4c3f18dec4060</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::postprocessor</name>
    <filename>classspot_1_1postprocessor.html</filename>
    <member kind="function">
      <type></type>
      <name>postprocessor</name>
      <anchorfile>classspot_1_1postprocessor.html</anchorfile>
      <anchor>a5835fe01b51e2658e89a4eac757198ba</anchor>
      <arglist>(const option_map *opt=0)</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>run</name>
      <anchorfile>classspot_1_1postprocessor.html</anchorfile>
      <anchor>a8f479f91221be0e1c24fac555cf1708c</anchor>
      <arglist>(const tgba *input_disown, const ltl::formula *f)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::power_map</name>
    <filename>structspot_1_1power__map.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::tgba_reachable_iterator</name>
    <filename>classspot_1_1tgba__reachable__iterator.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aecad73f17f4ae8a067614a6cf78ac3cd</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>want_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a0679e3fa4cae75e5d540ab5c960695dc</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a958d443339cdc7da2b6da976186cf299</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aa6bef3072977a131257e206221b63d89</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a6b38d281157866c5bae8806c4b7cd8fd</anchor>
      <arglist>(const state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a6214cd7eb3295c93fae10a800b4635f7</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a045c244be5739955e1e851beee4f5dca</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>aut_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a2fd15caa56fd3b9c39d0360c6134ed27</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a33fbcfee3eb7694451f2b0760ee79de1</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_reachable_iterator_breadth_first</name>
    <filename>classspot_1_1tgba__reachable__iterator__breadth__first.html</filename>
    <base>spot::tgba_reachable_iterator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>ac368fb5a611b2509c33f9deb03a7ccc6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>ab591b269fe4c8cbc61769c5309255b46</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_reachable_iterator_depth_first</name>
    <filename>classspot_1_1tgba__reachable__iterator__depth__first.html</filename>
    <class kind="struct">spot::tgba_reachable_iterator_depth_first::stack_item</class>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>a33b798eaa8ca87f584a7cf4eeefa2cc3</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>want_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>ac04fd9e5b5eade3ae1298e0dfd85fb8a</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>a2a8be35db097a507599712e3ddbe589b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>ac29ede1119b67de21f6a698300f6df85</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>ac6c41b51e7be8ba8f7166ec14ca49979</anchor>
      <arglist>(const state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>acc530c9e06e4a4eec5a19dd93f4f1c1a</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>push</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>a078ec75f427eedba02e632baad06d55b</anchor>
      <arglist>(const state *s, int sn)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>pop</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>ad29da21f9160fbf99aac310c10629b81</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>aut_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>a50e54061a176337f9ae518f1cae78c64</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>a981e88e48fc601694fd01ed85497e43b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; stack_item &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>abcdece329a38c368a4d0045fa3c14e3a</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_reachable_iterator_depth_first::stack_item</name>
    <filename>structspot_1_1tgba__reachable__iterator__depth__first_1_1stack__item.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::tgba_reachable_iterator_depth_first_stack</name>
    <filename>classspot_1_1tgba__reachable__iterator__depth__first__stack.html</filename>
    <base>spot::tgba_reachable_iterator_depth_first</base>
    <member kind="function">
      <type>bool</type>
      <name>on_stack</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first__stack.html</anchorfile>
      <anchor>ac30d42b606e2a9b88fc02b071137d11d</anchor>
      <arglist>(int sn) const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>push</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first__stack.html</anchorfile>
      <anchor>af353d93a85ef4f391b0f2f378dd696fb</anchor>
      <arglist>(const state *s, int sn)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>pop</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first__stack.html</anchorfile>
      <anchor>aafeb3084786069392f2de4069bdc90d9</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_run_dotty_decorator</name>
    <filename>classspot_1_1tgba__run__dotty__decorator.html</filename>
    <base>spot::dotty_decorator</base>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>state_decl</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>ad61dc9deb22f8d312ff4ac10c0e5739a</anchor>
      <arglist>(const tgba *a, const state *s, int n, tgba_succ_iterator *si, const std::string &amp;label, bool accepting)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>link_decl</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>ad350e1e551b6cc9ecab570b6d3d59bf8</anchor>
      <arglist>(const tgba *a, const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si, const std::string &amp;label)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::scc_stats</name>
    <filename>structspot_1_1scc__stats.html</filename>
    <member kind="variable">
      <type>unsigned</type>
      <name>scc_total</name>
      <anchorfile>structspot_1_1scc__stats.html</anchorfile>
      <anchor>a39a24beafce284dbcdde8d46cd4aae43</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned</type>
      <name>acc_scc</name>
      <anchorfile>structspot_1_1scc__stats.html</anchorfile>
      <anchor>a5463022830795ba17165e148d4a02774</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned</type>
      <name>dead_scc</name>
      <anchorfile>structspot_1_1scc__stats.html</anchorfile>
      <anchor>a8232f0d2651d52c529ef57e8df63db8e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned</type>
      <name>acc_paths</name>
      <anchorfile>structspot_1_1scc__stats.html</anchorfile>
      <anchor>a61218de47af06297c734661ddd8495cd</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned</type>
      <name>dead_paths</name>
      <anchorfile>structspot_1_1scc__stats.html</anchorfile>
      <anchor>a57f5bbe1dfccccb2cba728cc7e238b99</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>std::vector&lt; bool &gt;</type>
      <name>useless_scc_map</name>
      <anchorfile>structspot_1_1scc__stats.html</anchorfile>
      <anchor>ad868c4ea8ecc3365d0d89646c702c30e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>useful_acc</name>
      <anchorfile>structspot_1_1scc__stats.html</anchorfile>
      <anchor>a3a9c99145ee8a0b729372d44b784fe5d</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::scc_map</name>
    <filename>classspot_1_1scc__map.html</filename>
    <class kind="struct">spot::scc_map::scc</class>
    <member kind="function">
      <type></type>
      <name>scc_map</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a6b698d7c3a99a29bf516e912977dbf57</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>build_map</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>aa33121e63cff2f3bb172010358e493aa</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>get_aut</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a8c9563333308a4e924035be25e2265fe</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>scc_count</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a4eda9c8d5cf2f5ecfe67990d34c8c586</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>initial</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a501ebdcc5f219e29cdc249f699720aa6</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const succ_type &amp;</type>
      <name>succ</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a9224f6ed62e803a07ff2bcf78c2ba67b</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>trivial</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a4a1b0c6346151aadeb4baeed4e5c3423</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>accepting</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a797209aa8f668c819a9abdb1cbc1e67a</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>const cond_set &amp;</type>
      <name>cond_set_of</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a881c60367dce7b10b575bea66e131624</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>ap_set_of</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a7e7dd9ee5500322c785798a216dff42c</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>aprec_set_of</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>ac16a077904059733982206cb3d664923</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>acc_set_of</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>ad7683fdee7cefca72f8a9b270b944dc7</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>useful_acc_of</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>acc422ce1dae072d752c6b82d61ab98a3</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>const std::list&lt; const state * &gt; &amp;</type>
      <name>states_of</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a3b8792230732cf9d26eaf4cad048948f</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>one_state_of</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a93c95445b7fb64d9d1a0ef7cf8aad27b</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>scc_of_state</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>a2a04c0d560f42b77e979f93cc04117ce</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>self_loops</name>
      <anchorfile>classspot_1_1scc__map.html</anchorfile>
      <anchor>ade10989447f3deac378c2e9e922cb651</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::scc_map::scc</name>
    <filename>structspot_1_1scc__map_1_1scc.html</filename>
    <member kind="variable">
      <type>int</type>
      <name>index</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>a0ac4b2cb3be8f75a58b601e4eeb73447</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acc</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>a49cd1138b3aa98fff2d9d557eaf9d2a3</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>std::list&lt; const state * &gt;</type>
      <name>states</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>ab2804b213a89e61a77b4b0cccdb189a2</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>cond_set</type>
      <name>conds</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>a81f43b7400f58d689f02374c9a91b9d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>supp</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>abbe8efeb92c6ddcec45e628006fb40ed</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>supp_rec</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>ac9f425e221eed43fab64b19b372da659</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>succ_type</type>
      <name>succ</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>aab43767a2260a4e70f277732646d6214</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bool</type>
      <name>trivial</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>a659a7d473b579a0f00405c54b1542133</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>useful_acc</name>
      <anchorfile>structspot_1_1scc__map_1_1scc.html</anchorfile>
      <anchor>afd38ac4ffc5d5066444a3ee5b397cf81</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_statistics</name>
    <filename>structspot_1_1tgba__statistics.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_sub_statistics</name>
    <filename>structspot_1_1tgba__sub__statistics.html</filename>
    <base>spot::tgba_statistics</base>
  </compound>
  <compound kind="class">
    <name>spot::printable_formula</name>
    <filename>classspot_1_1printable__formula.html</filename>
    <base>printable_value&lt; const ltl::formula * &gt;</base>
  </compound>
  <compound kind="class">
    <name>spot::stat_printer</name>
    <filename>classspot_1_1stat__printer.html</filename>
    <base protection="protected">spot::formater</base>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>print</name>
      <anchorfile>classspot_1_1stat__printer.html</anchorfile>
      <anchor>a732f60ded8d6ffa900c55affea483cfb</anchor>
      <arglist>(const tgba *aut, const ltl::formula *f=0, double run_time=-1.)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::translator</name>
    <filename>classspot_1_1translator.html</filename>
    <base protection="protected">spot::postprocessor</base>
    <member kind="function">
      <type>const tgba *</type>
      <name>run</name>
      <anchorfile>classspot_1_1translator.html</anchorfile>
      <anchor>afbfbd1ec479efd7ad80f2d4b772da369</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>run</name>
      <anchorfile>classspot_1_1translator.html</anchorfile>
      <anchor>aa101bf301dad2069ee212ca95a7823a2</anchor>
      <arglist>(const ltl::formula **f)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::weight</name>
    <filename>classspot_1_1weight.html</filename>
    <member kind="function">
      <type></type>
      <name>weight</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>a8ec48103bbf49e3362dba6c3a43ec9ca</anchor>
      <arglist>(const bdd &amp;neg_all_cond)</arglist>
    </member>
    <member kind="function">
      <type>weight &amp;</type>
      <name>operator+=</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>a06d51ff55f29297828cccb306700d7f0</anchor>
      <arglist>(const bdd &amp;acc)</arglist>
    </member>
    <member kind="function">
      <type>weight &amp;</type>
      <name>operator-=</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>a7eca972118d15fc6c951bcf422676b75</anchor>
      <arglist>(const bdd &amp;acc)</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>operator-</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>a705d79b4f7c9bd4b220df93696f608f6</anchor>
      <arglist>(const weight &amp;w) const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_word</name>
    <filename>structspot_1_1tgba__word.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::gspn_exception</name>
    <filename>classspot_1_1gspn__exception.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::gspn_interface</name>
    <filename>classspot_1_1gspn__interface.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::gspn_ssp_interface</name>
    <filename>classspot_1_1gspn__ssp__interface.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::atomic_prop</name>
    <filename>classspot_1_1ltl_1_1atomic__prop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <member kind="enumeration">
      <type></type>
      <name>opkind</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac82808aaf6b4c6b35989fec75f13654b</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Constant</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac82808aaf6b4c6b35989fec75f13654ba923c681db289ce26c9d30c8e2cd4bc04</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>AtomicProp</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac82808aaf6b4c6b35989fec75f13654ba64397ddd71f78b2e7508f79cae0b5622</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>UnOp</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac82808aaf6b4c6b35989fec75f13654baf8c23a94e780d218b21c8493fd1e2ccf</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>BinOp</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac82808aaf6b4c6b35989fec75f13654bae811e3074fd4bde9b2f8732d5a7a381c</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>MultOp</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac82808aaf6b4c6b35989fec75f13654ba418b6a238689281b64e9a62c9a1511c7</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>BUnOp</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac82808aaf6b4c6b35989fec75f13654ba414031ca5ab4eef5eac25eb37990c89b</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>AutomatOp</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac82808aaf6b4c6b35989fec75f13654bae055693a330c8f38e80b113ffca33824</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>a72e2e15db86934db70a3bae58e2f6114</anchor>
      <arglist>(visitor &amp;visitor) const </arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>ad6a64b71a4ea8df318337821305cf3ba</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>environment &amp;</type>
      <name>env</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>ae61a369d3df8fb5304d07ee25e0be25d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>a57d23aab781a7230bd25e14f8da15e88</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a22895a0683e2947dd56bd661cb372581</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>destroy</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a5b0bb11f739944460bf63f13f6c9e901</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>opkind</type>
      <name>kind</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a23536bf52c04989ad8b31f7089f5a292</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_boolean</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>af5a043e09efe6c128a6daf6d169e9f58</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_sugar_free_boolean</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a5eeadae30e61d87e3a8620dc8b09a2ab</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_in_nenoform</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a71033d11fd3b9d4b1677f2e35387fbff</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_X_free</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ad5f1ede16540107aff77f58dbd32d0c8</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_sugar_free_ltl</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a7d03721ccde1d1f87fbed61736d508a2</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_ltl_formula</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a1d48d7345e873238c653b443db45a7a7</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_eltl_formula</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a96c648af8a7202016392669e65c3a656</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_psl_formula</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a0c534935d2450607cbe10aa38f8560c4</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_sere_formula</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a6e79749c3aeb12f5023383e43c5b708b</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_finite</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ab18ed0fd814e81309789213ca7aad409</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_eventual</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a32775c79e0a08e928c69669e0e92ce3a</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_universal</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ada451ff5a87ab82e20035ebcb9c24230</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_syntactic_safety</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ab5853cb67aba76b070aa09eaf1d5bcdd</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_syntactic_guarantee</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a564a934850f732d56b4e86195a156e58</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_syntactic_obligation</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a1ad15456ed578eb8f4858b138031e403</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_syntactic_recurrence</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ac31906441452c7e649bcd24fd62e480f</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_syntactic_persistence</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a81c520d3680793abed0f42a7cb29b511</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_marked</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a448c24edd9e84237e25a73b9d32a9aff</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>accepts_eword</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ae5d1e12ffa45478996b01d1ed2832a91</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>get_props</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a727f20cb5b4c05e23884294006dbbcc3</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a6473df6e9b0a479d3359401f0d9b177a</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const atomic_prop *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>a31ecd65b281d5f471df8c9a2cef00f03</anchor>
      <arglist>(const std::string &amp;name, environment &amp;env)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>a7f519f7edb2766b90e28edc1e28d6917</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static std::ostream &amp;</type>
      <name>dump_instances</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>a7aa747d99e75f74ad2bc0d886647e61c</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>a6b5f267272f8b6c362ca02e09d75a5a2</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>a01c66aaa7378aa5b7d8b3d914a7cf520</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>ref_count_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>ae8ae6d071c1846b5e21e8135dd1b2e13</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>size_t</type>
      <name>count_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a79d5a955150b1aa40ab396a5333eed7a</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::automatop</name>
    <filename>classspot_1_1ltl_1_1automatop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <class kind="struct">spot::ltl::automatop::tripletcmp</class>
    <member kind="typedef">
      <type>std::vector&lt; const formula * &gt;</type>
      <name>vec</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>ab66a93cf0d38d1cc8278cb48b0b8d661</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>af127a74a970803c9eab1ee8b30428e8d</anchor>
      <arglist>(visitor &amp;v) const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>size</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>a98e97615f70c9ea4f998d6975d263934</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>nth</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>a6730cc61a70ff0ee295f91a130788400</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>const spot::ltl::nfa::ptr</type>
      <name>get_nfa</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>a3be1a04aa5bb8649021644bb53d72122</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_negated</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>aac4399fe7b8af8547056d82b036af2c3</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>a0e7df583f8ed23dd0938b82412cf75ae</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const automatop *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>a8e6bc33e8a9ac8a2e6b60e6c27b549c5</anchor>
      <arglist>(const nfa::ptr nfa, vec *v, bool negated)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>a37f14507dc0aa468848340cc377728d1</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static std::ostream &amp;</type>
      <name>dump_instances</name>
      <anchorfile>classspot_1_1ltl_1_1automatop.html</anchorfile>
      <anchor>a46c8ce1e2af2efc18fb49a7c3b8a6ee3</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::automatop::tripletcmp</name>
    <filename>structspot_1_1ltl_1_1automatop_1_1tripletcmp.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::binop</name>
    <filename>classspot_1_1ltl_1_1binop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <member kind="enumeration">
      <type></type>
      <name>type</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Xor</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2a71177fc6c4bfbc11a0fd7acceeed1ed5</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Implies</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2ac5d2a034f0dd62b98fe785d0372c0c9a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Equiv</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2a0177c3febeabb4808b46226565b8df22</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>U</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2ab01994b5e43401a5cf70fc7ef1599119</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>R</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2a2912f480a149c4899b9f61f1a5975c38</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>W</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2a1838d447207f27c06df827716ae393b6</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>M</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2aeca825f59e0628f120869c38c1332432</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>EConcat</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2a556e405ec5227857780ce4668a140b5c</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>EConcatMarked</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2a285ca2f52a1cf305996911b759b30c70</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>UConcat</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a7c5967c6908151a90ff72f210bfb59a2aa5c22ec5f0538b7593a31404717ce409</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>add418be2b0cbd59fc568c7efe65d7afd</anchor>
      <arglist>(visitor &amp;v) const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>first</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a930b3e135d57e56705a0628998e182c2</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>second</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a3a338b2bd7de07b52549f13b2c5752eb</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>op</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a1e32c8b55b2d5512880eab572a2a787d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>op_name</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a761edd641d03b63371b267223091c783</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>aa2442c7e50e4429bc9d2feb9dbc9b9f8</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const formula *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>afef4f363766ec6fd4d14bd92e8f487be</anchor>
      <arglist>(type op, const formula *first, const formula *second)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>ae2374ced030c8da8cadf295f6fcac010</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static std::ostream &amp;</type>
      <name>dump_instances</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a5e0305d43188330be6c2a8f88a21b669</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::bunop</name>
    <filename>classspot_1_1ltl_1_1bunop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>a9a3b2cb8c1ecadb5865e7cb1af50a908</anchor>
      <arglist>(visitor &amp;v) const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>child</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>afbe203d5455e1bed86a81e1433daf4ca</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>min</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>acff397dd465f1fd7de282ebff4565b99</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>max</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>aa73c82483e6f212ccc2dccb175f236db</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>format</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>aed0cc1e2df0592f672d5a61b37da0a59</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>op</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>a516c969b221b56f91ac4b607b1b344bf</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>op_name</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>a8b6d82af0178d3f37ea7ad852a61cace</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>a3f901b52fd1a59e59fbd2facd4b81e8e</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const formula *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>acd9e5bd3c7d5ca39b7ab8ebfd55bb90f</anchor>
      <arglist>(type op, const formula *child, unsigned min=0, unsigned max=unbounded)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const formula *</type>
      <name>sugar_goto</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>a4dae737061028e77385b6e141a06d4f1</anchor>
      <arglist>(const formula *child, unsigned min=1, unsigned max=unbounded)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const formula *</type>
      <name>sugar_equal</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>ac886f38b2317237e35962a088ee497cb</anchor>
      <arglist>(const formula *child, unsigned min=0, unsigned max=unbounded)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>a45f054a0023cf07a534753a6f593404d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static std::ostream &amp;</type>
      <name>dump_instances</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>a7b6dd801a2bfc09125670b1def4fa2a3</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const formula *</type>
      <name>one_star</name>
      <anchorfile>classspot_1_1ltl_1_1bunop.html</anchorfile>
      <anchor>a15f6541c02f453355b66d519f433878f</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::constant</name>
    <filename>classspot_1_1ltl_1_1constant.html</filename>
    <base>spot::ltl::formula</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>afc566286f1ab1ac07068c5c06ef8dce5</anchor>
      <arglist>(visitor &amp;v) const </arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>val</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>a7866b696a1af367f2611e5967aae0d5d</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>val_name</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>a1cd1b6154e4c6e9eaa5dd7048fca9815</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>a390b246ef39e3a26edf4a05c337d1fee</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static constant *</type>
      <name>true_instance</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>a343dd7a20e2df06851836a6bd7fc1b90</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static constant *</type>
      <name>false_instance</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>a0ddc280986f98b088800542d8012b6f2</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static constant *</type>
      <name>empty_word_instance</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>a7b756eb41e76457d074cf5966df31686</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a1f2fc388e38e1a9f1f868a0068a2ed4e</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a501a18656f827e78a6f338c83414f5c7</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::formula</name>
    <filename>classspot_1_1ltl_1_1formula.html</filename>
    <class kind="struct">spot::ltl::formula::ltl_prop</class>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>a1424f7c2d139793094f571d9b8992fb1</anchor>
      <arglist>(visitor &amp;v) const =0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual std::string</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ad2be1ef6bdd40768e21a1b5fc7d0c96c</anchor>
      <arglist>() const =0</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula::ltl_prop</name>
    <filename>structspot_1_1ltl_1_1formula_1_1ltl__prop.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_ptr_less_than</name>
    <filename>structspot_1_1ltl_1_1formula__ptr__less__than.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_ptr_less_than_bool_first</name>
    <filename>structspot_1_1ltl_1_1formula__ptr__less__than__bool__first.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_ptr_hash</name>
    <filename>structspot_1_1ltl_1_1formula__ptr__hash.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::multop</name>
    <filename>classspot_1_1ltl_1_1multop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <class kind="struct">spot::ltl::multop::paircmp</class>
    <member kind="typedef">
      <type>std::vector&lt; const formula * &gt;</type>
      <name>vec</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>ad0313204fe3fe2eb5938a03e4ff72fb9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>ae5f423f57f51ac5b6e4186735f992863</anchor>
      <arglist>(visitor &amp;v) const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>size</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a0a9f29f20419757484fee9fd5d672374</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>nth</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a825d1e2f5f99488766ea9d897c1774ba</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>all_but</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a3d4132f775e83d7775d8822ca4954393</anchor>
      <arglist>(unsigned n) const </arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>boolean_count</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>af6c91df6be8cdc6d523ee67711979d79</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>boolean_operands</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a396347233bb9fff03ed04a4ebb6b0b8d</anchor>
      <arglist>(unsigned *width=0) const </arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>op</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a9adb7f5b4fe4616df30c7da5a39ae839</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>op_name</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>aa3d7635bc72f66c1af14d9d56ddcca4e</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a14009f0d040d4eb0c8849f1082273924</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const formula *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a3cddcd6e72fa35eaec4bc1003ee571ed</anchor>
      <arglist>(type op, const formula *first, const formula *second)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const formula *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>ac47c6b77fc3f46320dadf40417f9a42d</anchor>
      <arglist>(type op, vec *v)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a07737a4f338b029504fe22d3dc44e342</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static std::ostream &amp;</type>
      <name>dump_instances</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a0a7cffb297896b680980f0179268d774</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::multop::paircmp</name>
    <filename>structspot_1_1ltl_1_1multop_1_1paircmp.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::nfa</name>
    <filename>classspot_1_1ltl_1_1nfa.html</filename>
    <class kind="struct">spot::ltl::nfa::transition</class>
    <member kind="typedef">
      <type>succ_iterator</type>
      <name>iterator</name>
      <anchorfile>classspot_1_1ltl_1_1nfa.html</anchorfile>
      <anchor>a1279d02cf940a04df8507a094fa42485</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1ltl_1_1nfa.html</anchorfile>
      <anchor>ad2304b7ffcaf21633a9562e66c73b521</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_final</name>
      <anchorfile>classspot_1_1ltl_1_1nfa.html</anchorfile>
      <anchor>ae8bb8777121775d86bebb1453c033be9</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_loop</name>
      <anchorfile>classspot_1_1ltl_1_1nfa.html</anchorfile>
      <anchor>a1430217666a2b8bc73713fc80c3dc617</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>arity</name>
      <anchorfile>classspot_1_1ltl_1_1nfa.html</anchorfile>
      <anchor>a164536f6643805a75aacab958cfa24ff</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>iterator</type>
      <name>begin</name>
      <anchorfile>classspot_1_1ltl_1_1nfa.html</anchorfile>
      <anchor>ad2bff1be23bd062cf2bb7cae1803540a</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
    <member kind="function">
      <type>iterator</type>
      <name>end</name>
      <anchorfile>classspot_1_1ltl_1_1nfa.html</anchorfile>
      <anchor>a12197fc34b000bdd72691de0f9b5acde</anchor>
      <arglist>(const state *s) const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::nfa::transition</name>
    <filename>structspot_1_1ltl_1_1nfa_1_1transition.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::succ_iterator</name>
    <filename>classspot_1_1ltl_1_1succ__iterator.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::ref_formula</name>
    <filename>classspot_1_1ltl_1_1ref__formula.html</filename>
    <base>spot::ltl::formula</base>
  </compound>
  <compound kind="class">
    <name>spot::ltl::unop</name>
    <filename>classspot_1_1ltl_1_1unop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>a23b7e20d030a033e9628791f4aa3db5c</anchor>
      <arglist>(visitor &amp;v) const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>child</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>a3855d5c4933c3a0b57a41383e11a275f</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>op</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>ac8580e6c98e25a768444d4a419ebf3aa</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>op_name</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>ac8efed3eae938137236dbcd35de22fb7</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>a22d62d8d993add8db16dbe05ecabef56</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const formula *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>a0360ef6d4a4e5de32df3115e2439fde2</anchor>
      <arglist>(type op, const formula *child)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>a0c08b2cfc5767255a9096910b287fe42</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static std::ostream &amp;</type>
      <name>dump_instances</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>ac06f942b2ffc655579deaee8a63f064f</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::visitor</name>
    <filename>structspot_1_1ltl_1_1visitor.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::declarative_environment</name>
    <filename>classspot_1_1ltl_1_1declarative__environment.html</filename>
    <base>spot::ltl::environment</base>
    <member kind="function">
      <type>bool</type>
      <name>declare</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>a0ee488854ba5d0eb54accf9d08aebcaf</anchor>
      <arglist>(const std::string &amp;prop_str)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const formula *</type>
      <name>require</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>aa48c9c1742b513a548c558b8dde8fd6f</anchor>
      <arglist>(const std::string &amp;prop_str)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>a304df69a8a1201bc1aea4dd36b4d7b5c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const prop_map &amp;</type>
      <name>get_prop_map</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>ac984cdf6392cca00c3472cf47f46b7a4</anchor>
      <arglist>() const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::default_environment</name>
    <filename>classspot_1_1ltl_1_1default__environment.html</filename>
    <base>spot::ltl::environment</base>
    <member kind="function" virtualness="virtual">
      <type>virtual const formula *</type>
      <name>require</name>
      <anchorfile>classspot_1_1ltl_1_1default__environment.html</anchorfile>
      <anchor>a80a4011ea8cd823204614e5cf54eec3f</anchor>
      <arglist>(const std::string &amp;prop_str)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1default__environment.html</anchorfile>
      <anchor>a598bf78f41d23916e1d2c02fba636740</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static default_environment &amp;</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1default__environment.html</anchorfile>
      <anchor>a6f82853f22c3bf57128a484a28a4d550</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::environment</name>
    <filename>classspot_1_1ltl_1_1environment.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual const formula *</type>
      <name>require</name>
      <anchorfile>classspot_1_1ltl_1_1environment.html</anchorfile>
      <anchor>abfcd56f7f245e1a2084507e7f6e1f635</anchor>
      <arglist>(const std::string &amp;prop_str)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1environment.html</anchorfile>
      <anchor>a52845adbef2d65748b4bba6287827270</anchor>
      <arglist>()=0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::ltl_file</name>
    <filename>classspot_1_1ltl_1_1ltl__file.html</filename>
    <member kind="function">
      <type>const formula *</type>
      <name>next</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__file.html</anchorfile>
      <anchor>a389aad5fd8874885ed2590d9b9bbf610</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::clone_visitor</name>
    <filename>classspot_1_1ltl_1_1clone__visitor.html</filename>
    <base>spot::ltl::visitor</base>
  </compound>
  <compound kind="class">
    <name>spot::ltl::language_containment_checker</name>
    <filename>classspot_1_1ltl_1_1language__containment__checker.html</filename>
    <member kind="function">
      <type></type>
      <name>language_containment_checker</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>af1d93e29c75d3c0b5f18aadbae7a1f08</anchor>
      <arglist>(bdd_dict *dict, bool exprop, bool symb_merge, bool branching_postponement, bool fair_loop_approx)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>clear</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>a28c365e1a1ad2de0ce665edff069e5ff</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>contained</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>a41da10076c5996501f6c50a6bb8232df</anchor>
      <arglist>(const formula *l, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>neg_contained</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>abd13da68e9214781e803149d0a24540b</anchor>
      <arglist>(const formula *l, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>contained_neg</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>a6c33553adb7b06fdb776b4545b2bdecd</anchor>
      <arglist>(const formula *l, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>equal</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>ac90866276d5666262fb7cf385825ba7e</anchor>
      <arglist>(const formula *l, const formula *g)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::unabbreviate_logic_visitor</name>
    <filename>classspot_1_1ltl_1_1unabbreviate__logic__visitor.html</filename>
    <base>spot::ltl::clone_visitor</base>
  </compound>
  <compound kind="class">
    <name>spot::ltl::mark_tools</name>
    <filename>classspot_1_1ltl_1_1mark__tools.html</filename>
    <member kind="function">
      <type>const formula *</type>
      <name>mark_concat_ops</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ga32279d715efca917b5ac55e1262a6a29</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::postfix_visitor</name>
    <filename>classspot_1_1ltl_1_1postfix__visitor.html</filename>
    <base>spot::ltl::visitor</base>
  </compound>
  <compound kind="class">
    <name>spot::ltl::random_formula</name>
    <filename>classspot_1_1ltl_1_1random__formula.html</filename>
    <class kind="struct">spot::ltl::random_formula::op_proba</class>
    <member kind="function">
      <type>const atomic_prop_set *</type>
      <name>ap</name>
      <anchorfile>classspot_1_1ltl_1_1random__formula.html</anchorfile>
      <anchor>a8ba7f45cadaf07f475607d5471a74c54</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>generate</name>
      <anchorfile>classspot_1_1ltl_1_1random__formula.html</anchorfile>
      <anchor>a2fc4dc931143f3fc14e2154fb344c71e</anchor>
      <arglist>(int n) const </arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump_priorities</name>
      <anchorfile>classspot_1_1ltl_1_1random__formula.html</anchorfile>
      <anchor>a4810ccf354d4c5f11bcb2affb98693e8</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1ltl_1_1random__formula.html</anchorfile>
      <anchor>ac6ceded8e630a8e5f800843c43b0cd23</anchor>
      <arglist>(char *options)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::random_formula::op_proba</name>
    <filename>structspot_1_1ltl_1_1random__formula_1_1op__proba.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::random_ltl</name>
    <filename>classspot_1_1ltl_1_1random__ltl.html</filename>
    <base>spot::ltl::random_formula</base>
    <member kind="function">
      <type></type>
      <name>random_ltl</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>a3a6eaaabb6c56d573ecb114a3d1959d9</anchor>
      <arglist>(const atomic_prop_set *ap)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::random_boolean</name>
    <filename>classspot_1_1ltl_1_1random__boolean.html</filename>
    <base>spot::ltl::random_formula</base>
    <member kind="function">
      <type></type>
      <name>random_boolean</name>
      <anchorfile>classspot_1_1ltl_1_1random__boolean.html</anchorfile>
      <anchor>a6b1f33d388861a5c493a06fb5c90f26d</anchor>
      <arglist>(const atomic_prop_set *ap)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::random_sere</name>
    <filename>classspot_1_1ltl_1_1random__sere.html</filename>
    <base>spot::ltl::random_formula</base>
    <member kind="function">
      <type></type>
      <name>random_sere</name>
      <anchorfile>classspot_1_1ltl_1_1random__sere.html</anchorfile>
      <anchor>a6bb0d4a67a63a3c276de0c75950a2bff</anchor>
      <arglist>(const atomic_prop_set *ap)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::random_psl</name>
    <filename>classspot_1_1ltl_1_1random__psl.html</filename>
    <base>spot::ltl::random_ltl</base>
    <member kind="function">
      <type></type>
      <name>random_psl</name>
      <anchorfile>classspot_1_1ltl_1_1random__psl.html</anchorfile>
      <anchor>aef17b0ddba29c97cc4e32ed695952c02</anchor>
      <arglist>(const atomic_prop_set *ap)</arglist>
    </member>
    <member kind="variable">
      <type>random_sere</type>
      <name>rs</name>
      <anchorfile>classspot_1_1ltl_1_1random__psl.html</anchorfile>
      <anchor>a538cd2f430cb3a9b7d250ca214e6614e</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::relabeling_map</name>
    <filename>structspot_1_1ltl_1_1relabeling__map.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::simplify_f_g_visitor</name>
    <filename>classspot_1_1ltl_1_1simplify__f__g__visitor.html</filename>
    <base>spot::ltl::clone_visitor</base>
  </compound>
  <compound kind="class">
    <name>spot::ltl::ltl_simplifier_options</name>
    <filename>classspot_1_1ltl_1_1ltl__simplifier__options.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::ltl::ltl_simplifier</name>
    <filename>classspot_1_1ltl_1_1ltl__simplifier.html</filename>
    <member kind="function">
      <type>const formula *</type>
      <name>simplify</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a09958aebdec4239bb25c185c7fcb1ba7</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>negative_normal_form</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a1438e6f44913b4d9c1a9668dd6610da5</anchor>
      <arglist>(const formula *f, bool negated=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>syntactic_implication</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a160d282dbd4a278eeabf1db1ad10e334</anchor>
      <arglist>(const formula *f, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>syntactic_implication_neg</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a29c8c00a48c0a277fa73b378cfb472b7</anchor>
      <arglist>(const formula *f, const formula *g, bool right)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>are_equivalent</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a1a35008d9c232a06f835088c8987a6b2</anchor>
      <arglist>(const formula *f, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>implication</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a5a80b97f921bd57e78ce1b91358aa98c</anchor>
      <arglist>(const formula *f, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>as_bdd</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a814023d189d870ffeeba120b91b989bf</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>clear_as_bdd_cache</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a5e209a9baeea02e08a032355c971f196</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>addd6710daea5c439c0bb88128dc9d27f</anchor>
      <arglist>() const </arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>star_normal_form</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a99e3b0cff9b87ec6fa844e83cb822524</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>boolean_to_isop</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a7295cdcd629ea8e2a96f50d6f5d048a1</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1ltl_1_1ltl__simplifier.html</anchorfile>
      <anchor>a5017ab065fd0dc81b4adb616f4fc2bf3</anchor>
      <arglist>(std::ostream &amp;os) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::unabbreviate_ltl_visitor</name>
    <filename>classspot_1_1ltl_1_1unabbreviate__ltl__visitor.html</filename>
    <base>spot::ltl::unabbreviate_logic_visitor</base>
  </compound>
  <compound kind="namespace">
    <name>spot::ltl::formula_tree</name>
    <filename>namespacespot_1_1ltl_1_1formula__tree.html</filename>
    <class kind="struct">spot::ltl::formula_tree::node</class>
    <class kind="struct">spot::ltl::formula_tree::node_unop</class>
    <class kind="struct">spot::ltl::formula_tree::node_binop</class>
    <class kind="struct">spot::ltl::formula_tree::node_multop</class>
    <class kind="struct">spot::ltl::formula_tree::node_nfa</class>
    <class kind="struct">spot::ltl::formula_tree::node_atomic</class>
    <member kind="typedef">
      <type>boost::shared_ptr&lt; node &gt;</type>
      <name>node_ptr</name>
      <anchorfile>namespacespot_1_1ltl_1_1formula__tree.html</anchorfile>
      <anchor>aca5141e136bf68845459fe16cee595a9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>instanciate</name>
      <anchorfile>namespacespot_1_1ltl_1_1formula__tree.html</anchorfile>
      <anchor>a15921c6ac098624c6194659ce7256a80</anchor>
      <arglist>(const node_ptr np, const std::vector&lt; const formula * &gt; &amp;v)</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>arity</name>
      <anchorfile>namespacespot_1_1ltl_1_1formula__tree.html</anchorfile>
      <anchor>af67581a7e99a31f157617cd73ff5f1a2</anchor>
      <arglist>(const node_ptr np)</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_tree::node</name>
    <filename>structspot_1_1ltl_1_1formula__tree_1_1node.html</filename>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_tree::node_unop</name>
    <filename>structspot_1_1ltl_1_1formula__tree_1_1node__unop.html</filename>
    <base>spot::ltl::formula_tree::node</base>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_tree::node_binop</name>
    <filename>structspot_1_1ltl_1_1formula__tree_1_1node__binop.html</filename>
    <base>spot::ltl::formula_tree::node</base>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_tree::node_multop</name>
    <filename>structspot_1_1ltl_1_1formula__tree_1_1node__multop.html</filename>
    <base>spot::ltl::formula_tree::node</base>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_tree::node_nfa</name>
    <filename>structspot_1_1ltl_1_1formula__tree_1_1node__nfa.html</filename>
    <base>spot::ltl::formula_tree::node</base>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_tree::node_atomic</name>
    <filename>structspot_1_1ltl_1_1formula__tree_1_1node__atomic.html</filename>
    <base>spot::ltl::formula_tree::node</base>
  </compound>
  <compound kind="class">
    <name>tgbayy::location</name>
    <filename>classtgbayy_1_1location.html</filename>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>a175148204ce90c2f88832188e2ae615a</anchor>
      <arglist>(const position &amp;b, const position &amp;e)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>a98a9611bfe5f3bc1c55e560ecea0e105</anchor>
      <arglist>(const position &amp;p=position())</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>a6ae3744bd9d759e0a97232563a57d719</anchor>
      <arglist>(std::string *f, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>aca1684122f878134d05af3c195cbcc54</anchor>
      <arglist>(std::string *f=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>step</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>a5189d01223312a55949c09e5d084c2e1</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>ad39b16d2365d3b89f156e68cf4bdae35</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>a39c5ccd994275c73d0f17ffa608f80ec</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>begin</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>a7c2de2e5b7c72ea837bc33b4175cde15</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>end</name>
      <anchorfile>classtgbayy_1_1location.html</anchorfile>
      <anchor>a6c075bb0f0f7dcbd2284663d662c3648</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>tgbayy::position</name>
    <filename>classtgbayy_1_1position.html</filename>
    <member kind="function">
      <type></type>
      <name>position</name>
      <anchorfile>classtgbayy_1_1position.html</anchorfile>
      <anchor>a4e91c5383e0b8d487b92e9a68427964b</anchor>
      <arglist>(std::string *f=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classtgbayy_1_1position.html</anchorfile>
      <anchor>aaf3a9baaf8dd62621accd9e08d4c9542</anchor>
      <arglist>(std::string *fn=YY_NULL, unsigned int l=1u, unsigned int c=1u)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classtgbayy_1_1position.html</anchorfile>
      <anchor>aa7a7733f5ec12fd81fdf9206a09a5990</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classtgbayy_1_1position.html</anchorfile>
      <anchor>a668252911f65b7ed94d6e0f44946ff55</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>std::string *</type>
      <name>filename</name>
      <anchorfile>classtgbayy_1_1position.html</anchorfile>
      <anchor>ab68710fc002483a4b1f84937a04a8198</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>line</name>
      <anchorfile>classtgbayy_1_1position.html</anchorfile>
      <anchor>a8227251bf0e895f1df9ec37a549ec76e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>column</name>
      <anchorfile>classtgbayy_1_1position.html</anchorfile>
      <anchor>a85d4b89078725a4c3073f88defbc98d6</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="dir">
    <name>bin</name>
    <path>/home/adl/git/spot/src/bin/</path>
    <filename>dir_031c4fc26031de218e3c67acfca35073.html</filename>
    <file>common_cout.hh</file>
    <file>common_finput.hh</file>
    <file>common_output.hh</file>
    <file>common_post.hh</file>
    <file>common_r.hh</file>
    <file>common_range.hh</file>
    <file>common_setup.hh</file>
    <file>common_sys.hh</file>
  </compound>
  <compound kind="dir">
    <name>dstarparse</name>
    <path>/home/adl/git/spot/src/dstarparse/</path>
    <filename>dir_2fc0502e4474988e3fd9d340d86f7f7e.html</filename>
    <file>public.hh</file>
  </compound>
  <compound kind="dir">
    <name>dve2</name>
    <path>/home/adl/git/spot/iface/dve2/</path>
    <filename>dir_2b8fb8017d661cbc871fe6446a08e858.html</filename>
    <file>dve2.hh</file>
  </compound>
  <compound kind="dir">
    <name>eltlparse</name>
    <path>/home/adl/git/spot/src/eltlparse/</path>
    <filename>dir_c86f7d44d4e61a20d53fa1c6951625fa.html</filename>
    <file>public.hh</file>
  </compound>
  <compound kind="dir">
    <name>gspn</name>
    <path>/home/adl/git/spot/iface/gspn/</path>
    <filename>dir_1b8f9ba14f4bd6cd59c2da0d223f617d.html</filename>
    <file>common.hh</file>
    <file>gspn.hh</file>
    <file>ssp.hh</file>
  </compound>
  <compound kind="dir">
    <name>tgbaalgos/gtec</name>
    <path>/home/adl/git/spot/src/tgbaalgos/gtec/</path>
    <filename>dir_769a5096e6d87c9d57fd03d0959c078f.html</filename>
    <file>ce.hh</file>
    <file>explscc.hh</file>
    <file>gtec.hh</file>
    <file>nsheap.hh</file>
    <file>sccstack.hh</file>
    <file>status.hh</file>
  </compound>
  <compound kind="dir">
    <name>kripke</name>
    <path>/home/adl/git/spot/src/kripke/</path>
    <filename>dir_0105be830fc9dd4d086d7709652f0f1c.html</filename>
    <file>fairkripke.hh</file>
    <file>kripke.hh</file>
    <file>kripkeexplicit.hh</file>
    <file>kripkeprint.hh</file>
    <file>univmodel.hh</file>
  </compound>
  <compound kind="dir">
    <name>kripkeparse</name>
    <path>/home/adl/git/spot/src/kripkeparse/</path>
    <filename>dir_b259c25245aa52cc5d4341c072d12fbb.html</filename>
    <file>location.hh</file>
    <file>position.hh</file>
    <file>public.hh</file>
  </compound>
  <compound kind="dir">
    <name>ltlast</name>
    <path>/home/adl/git/spot/src/ltlast/</path>
    <filename>dir_733ec110de5903fabfd3dfeab6deb7b0.html</filename>
    <file>allnodes.hh</file>
    <file>atomic_prop.hh</file>
    <file>automatop.hh</file>
    <file>binop.hh</file>
    <file>bunop.hh</file>
    <file>constant.hh</file>
    <file>formula.hh</file>
    <file>formula_tree.hh</file>
    <file>multop.hh</file>
    <file>nfa.hh</file>
    <file>predecl.hh</file>
    <file>refformula.hh</file>
    <file>unop.hh</file>
    <file>visitor.hh</file>
  </compound>
  <compound kind="dir">
    <name>ltlenv</name>
    <path>/home/adl/git/spot/src/ltlenv/</path>
    <filename>dir_46a3af01e87d9ed4a6459748af7d191a.html</filename>
    <file>declenv.hh</file>
    <file>defaultenv.hh</file>
    <file>environment.hh</file>
  </compound>
  <compound kind="dir">
    <name>ltlparse</name>
    <path>/home/adl/git/spot/src/ltlparse/</path>
    <filename>dir_bb7035e55cdb146a9bcd2fa1c2fae653.html</filename>
    <file>location.hh</file>
    <file>ltlfile.hh</file>
    <file>position.hh</file>
    <file>public.hh</file>
  </compound>
  <compound kind="dir">
    <name>ltlvisit</name>
    <path>/home/adl/git/spot/src/ltlvisit/</path>
    <filename>dir_dad17ef05958cb6ec778792e72343bb5.html</filename>
    <file>apcollect.hh</file>
    <file>clone.hh</file>
    <file>contain.hh</file>
    <file>destroy.hh</file>
    <file>dotty.hh</file>
    <file>dump.hh</file>
    <file>lbt.hh</file>
    <file>length.hh</file>
    <file>lunabbrev.hh</file>
    <file>mark.hh</file>
    <file>nenoform.hh</file>
    <file>postfix.hh</file>
    <file>randomltl.hh</file>
    <file>reduce.hh</file>
    <file>relabel.hh</file>
    <file>remove_x.hh</file>
    <file>simpfg.hh</file>
    <file>simplify.hh</file>
    <file>snf.hh</file>
    <file>tostring.hh</file>
    <file>tunabbrev.hh</file>
    <file>wmunabbrev.hh</file>
  </compound>
  <compound kind="dir">
    <name>misc</name>
    <path>/home/adl/git/spot/src/misc/</path>
    <filename>dir_88826aa2a10e1882b573f502bc7f4b55.html</filename>
    <file>bareword.hh</file>
    <file>bddlt.hh</file>
    <file>bddop.hh</file>
    <file>bitvect.hh</file>
    <file>casts.hh</file>
    <file>common.hh</file>
    <file>escape.hh</file>
    <file>fixpool.hh</file>
    <file>formater.hh</file>
    <file>hash.hh</file>
    <file>hashfunc.hh</file>
    <file>intvcmp2.hh</file>
    <file>intvcomp.hh</file>
    <file>ltstr.hh</file>
    <file>memusage.hh</file>
    <file>minato.hh</file>
    <file>mspool.hh</file>
    <file>optionmap.hh</file>
    <file>random.hh</file>
    <file>satsolver.hh</file>
    <file>timer.hh</file>
    <file>tmpfile.hh</file>
    <file>unique_ptr.hh</file>
    <file>version.hh</file>
  </compound>
  <compound kind="dir">
    <name>neverparse</name>
    <path>/home/adl/git/spot/src/neverparse/</path>
    <filename>dir_55b30e711846349ae7f192b08c6807ac.html</filename>
    <file>location.hh</file>
    <file>position.hh</file>
    <file>public.hh</file>
  </compound>
  <compound kind="dir">
    <name>saba</name>
    <path>/home/adl/git/spot/src/saba/</path>
    <filename>dir_fa0f1f8546989799fbf643cde19c9e38.html</filename>
    <file>explicitstateconjunction.hh</file>
    <file>saba.hh</file>
    <file>sabacomplementtgba.hh</file>
    <file>sabastate.hh</file>
    <file>sabasucciter.hh</file>
  </compound>
  <compound kind="dir">
    <name>sabaalgos</name>
    <path>/home/adl/git/spot/src/sabaalgos/</path>
    <filename>dir_2c2115823ac848f022e42e11f72dcd6c.html</filename>
    <file>sabadotty.hh</file>
    <file>sabareachiter.hh</file>
  </compound>
  <compound kind="dir">
    <name>ta</name>
    <path>/home/adl/git/spot/src/ta/</path>
    <filename>dir_f6f09ab9c08a3fdc06da35f318405aad.html</filename>
    <file>ta.hh</file>
    <file>taexplicit.hh</file>
    <file>taproduct.hh</file>
    <file>tgta.hh</file>
    <file>tgtaexplicit.hh</file>
    <file>tgtaproduct.hh</file>
  </compound>
  <compound kind="dir">
    <name>taalgos</name>
    <path>/home/adl/git/spot/src/taalgos/</path>
    <filename>dir_4768d2f19b7afbe048757f7169e80214.html</filename>
    <file>dotty.hh</file>
    <file>emptinessta.hh</file>
    <file>minimize.hh</file>
    <file>reachiter.hh</file>
    <file>statessetbuilder.hh</file>
    <file>stats.hh</file>
    <file>tgba2ta.hh</file>
  </compound>
  <compound kind="dir">
    <name>tgba</name>
    <path>/home/adl/git/spot/src/tgba/</path>
    <filename>dir_cbab49d3f64287c85191cb4142dfb6a4.html</filename>
    <file>acc.hh</file>
    <file>bdddict.hh</file>
    <file>bddprint.hh</file>
    <file>formula2bdd.hh</file>
    <file>futurecondcol.hh</file>
    <file>public.hh</file>
    <file>sba.hh</file>
    <file>state.hh</file>
    <file>statebdd.hh</file>
    <file>succiter.hh</file>
    <file>succiterconcrete.hh</file>
    <file>taatgba.hh</file>
    <file>tgba.hh</file>
    <file>tgbabddconcrete.hh</file>
    <file>tgbabddconcretefactory.hh</file>
    <file>tgbabddconcreteproduct.hh</file>
    <file>tgbabddcoredata.hh</file>
    <file>tgbabddfactory.hh</file>
    <file>tgbaexplicit.hh</file>
    <file>tgbakvcomplement.hh</file>
    <file>tgbamask.hh</file>
    <file>tgbaproduct.hh</file>
    <file>tgbaproxy.hh</file>
    <file>tgbasafracomplement.hh</file>
    <file>tgbascc.hh</file>
    <file>tgbasgba.hh</file>
    <file>tgbatba.hh</file>
    <file>tgbaunion.hh</file>
    <file>wdbacomp.hh</file>
  </compound>
  <compound kind="dir">
    <name>tgbaalgos</name>
    <path>/home/adl/git/spot/src/tgbaalgos/</path>
    <filename>dir_c94130e9bcfe8e0a5c5ab888502605db.html</filename>
    <dir>tgbaalgos/gtec</dir>
    <file>bfssteps.hh</file>
    <file>complete.hh</file>
    <file>compsusp.hh</file>
    <file>cutscc.hh</file>
    <file>cycles.hh</file>
    <file>degen.hh</file>
    <file>dotty.hh</file>
    <file>dottydec.hh</file>
    <file>dtbasat.hh</file>
    <file>dtgbacomp.hh</file>
    <file>dtgbasat.hh</file>
    <file>dupexp.hh</file>
    <file>eltl2tgba_lacim.hh</file>
    <file>emptiness.hh</file>
    <file>emptiness_stats.hh</file>
    <file>gv04.hh</file>
    <file>hoaf.hh</file>
    <file>isdet.hh</file>
    <file>isweakscc.hh</file>
    <file>lbtt.hh</file>
    <file>ltl2taa.hh</file>
    <file>ltl2tgba_fm.hh</file>
    <file>ltl2tgba_lacim.hh</file>
    <file>magic.hh</file>
    <file>minimize.hh</file>
    <file>neverclaim.hh</file>
    <file>postproc.hh</file>
    <file>powerset.hh</file>
    <file>projrun.hh</file>
    <file>randomgraph.hh</file>
    <file>reachiter.hh</file>
    <file>reducerun.hh</file>
    <file>reductgba_sim.hh</file>
    <file>replayrun.hh</file>
    <file>rundotdec.hh</file>
    <file>safety.hh</file>
    <file>save.hh</file>
    <file>scc.hh</file>
    <file>sccfilter.hh</file>
    <file>se05.hh</file>
    <file>simulation.hh</file>
    <file>stats.hh</file>
    <file>stripacc.hh</file>
    <file>stutterize.hh</file>
    <file>tau03.hh</file>
    <file>tau03opt.hh</file>
    <file>translate.hh</file>
    <file>weight.hh</file>
    <file>word.hh</file>
  </compound>
  <compound kind="dir">
    <name>tgbaparse</name>
    <path>/home/adl/git/spot/src/tgbaparse/</path>
    <filename>dir_164d0467f4e04588848845f703945861.html</filename>
    <file>location.hh</file>
    <file>position.hh</file>
    <file>public.hh</file>
  </compound>
  <compound kind="page">
    <name>index</name>
    <title>Spot Library Documentation</title>
    <filename>index</filename>
    <docanchor file="index" title="The Spot Library">overview</docanchor>
    <docanchor file="index" title="This Document">thisdoc</docanchor>
    <docanchor file="index" title="Handy starting points">pointers</docanchor>
  </compound>
</tagfile>
