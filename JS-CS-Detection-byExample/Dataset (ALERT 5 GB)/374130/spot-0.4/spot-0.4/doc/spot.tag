<?xml version='1.0' encoding='ISO-8859-1' standalone='yes' ?>
<tagfile>
  <compound kind="page">
    <name>index</name>
    <title></title>
    <filename>index</filename>
    <docanchor file="index">pointers</docanchor>
    <docanchor file="index">overview</docanchor>
    <docanchor file="index">thisdoc</docanchor>
  </compound>
  <compound kind="file">
    <name>mainpage.dox</name>
    <path>/home/adl/proj/spot/doc/</path>
    <filename>mainpage_8dox</filename>
  </compound>
  <compound kind="file">
    <name>common.hh</name>
    <path>/home/adl/proj/spot/iface/gspn/</path>
    <filename>common_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::gspn_exception</class>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>83bcadbb0c347d956021471f8c7d2135</anchor>
      <arglist>(std::ostream &amp;os, const gspn_exception &amp;e)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>gspn.hh</name>
    <path>/home/adl/proj/spot/iface/gspn/</path>
    <filename>gspn_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba/tgba.hh</includes>
    <includes id="common_8hh" name="common.hh" local="yes" imported="no">common.hh</includes>
    <includes id="declenv_8hh" name="declenv.hh" local="yes" imported="no">ltlenv/declenv.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::gspn_interface</class>
  </compound>
  <compound kind="file">
    <name>ssp.hh</name>
    <path>/home/adl/proj/spot/iface/gspn/</path>
    <filename>ssp_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba/tgba.hh</includes>
    <includes id="common_8hh" name="common.hh" local="yes" imported="no">common.hh</includes>
    <includes id="gtec_8hh" name="gtec.hh" local="yes" imported="no">tgbaalgos/gtec/gtec.hh</includes>
    <includes id="ce_8hh" name="ce.hh" local="yes" imported="no">tgbaalgos/gtec/ce.hh</includes>
    <includes id="declenv_8hh" name="declenv.hh" local="yes" imported="no">ltlenv/declenv.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::gspn_ssp_interface</class>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_semi</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g41573dafc02e8da2283670fa7aefebcf</anchor>
      <arglist>(const tgba *ssp_automata)</arglist>
    </member>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_shy_semi</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g9e84d9fcba32903d98fc0a0ebdc73ac1</anchor>
      <arglist>(const tgba *ssp_automata)</arglist>
    </member>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_shy</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g9dba9cdb805e4187dc072c0ff7ea3d67</anchor>
      <arglist>(const tgba *ssp_automata, bool stack_inclusion=true)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>evtgba.hh</name>
    <path>/home/adl/proj/spot/src/evtgba/</path>
    <filename>evtgba_8hh</filename>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">tgba/state.hh</includes>
    <includes id="evtgbaiter_8hh" name="evtgbaiter.hh" local="yes" imported="no">evtgbaiter.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::evtgba</class>
  </compound>
  <compound kind="file">
    <name>evtgbaiter.hh</name>
    <path>/home/adl/proj/spot/src/evtgba/</path>
    <filename>evtgbaiter_8hh</filename>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">tgba/state.hh</includes>
    <includes id="symbol_8hh" name="symbol.hh" local="yes" imported="no">symbol.hh</includes>
    <includes id="evtgbaiter_8hh" name="evtgbaiter.hh" local="yes" imported="no">evtgbaiter.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::evtgba_iterator</class>
  </compound>
  <compound kind="file">
    <name>explicit.hh</name>
    <path>/home/adl/proj/spot/src/evtgba/</path>
    <filename>explicit_8hh</filename>
    <includes id="evtgba_8hh" name="evtgba.hh" local="yes" imported="no">evtgba.hh</includes>
    <includes id="hash_8hh" name="hash.hh" local="yes" imported="no">misc/hash.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::evtgba_explicit</class>
    <class kind="struct">spot::evtgba_explicit::state</class>
    <class kind="struct">spot::evtgba_explicit::transition</class>
    <class kind="class">spot::state_evtgba_explicit</class>
  </compound>
  <compound kind="file">
    <name>product.hh</name>
    <path>/home/adl/proj/spot/src/evtgba/</path>
    <filename>product_8hh</filename>
    <includes id="evtgba_8hh" name="evtgba.hh" local="yes" imported="no">evtgba/evtgba.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::evtgba_product</class>
  </compound>
  <compound kind="file">
    <name>symbol.hh</name>
    <path>/home/adl/proj/spot/src/evtgba/</path>
    <filename>symbol_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::symbol</class>
    <class kind="class">spot::rsymbol</class>
    <member kind="typedef">
      <type>std::set&lt; const symbol * &gt;</type>
      <name>symbol_set</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>b1fe964166144356a185c98ce0bcbde6</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::set&lt; rsymbol &gt;</type>
      <name>rsymbol_set</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>5f139f442de158fbe3bf9fab3ddee7f3</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>dotty.hh</name>
    <path>/home/adl/proj/spot/src/evtgbaalgos/</path>
    <filename>evtgbaalgos_2dotty_8hh</filename>
    <includes id="evtgba_8hh" name="evtgba.hh" local="yes" imported="no">evtgba/evtgba.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dotty_reachable</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>b3ff98345b771b22e0a9b67063676e84</anchor>
      <arglist>(std::ostream &amp;os, const evtgba *g)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>dotty.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>ltlvisit_2dotty_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="no" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dotty</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g7e27d31b2209954c1d57d3b8b5224473</anchor>
      <arglist>(std::ostream &amp;os, const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>dotty.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>tgbaalgos_2dotty_8hh</filename>
    <includes id="dottydec_8hh" name="dottydec.hh" local="yes" imported="no">dottydec.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dotty_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g07d47453e3bab574bf4b09589a18dcf9</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g, dotty_decorator *dd=dotty_decorator::instance())</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>reachiter.hh</name>
    <path>/home/adl/proj/spot/src/evtgbaalgos/</path>
    <filename>evtgbaalgos_2reachiter_8hh</filename>
    <includes id="hash_8hh" name="hash.hh" local="yes" imported="no">misc/hash.hh</includes>
    <includes id="evtgba_8hh" name="evtgba.hh" local="yes" imported="no">evtgba/evtgba.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::evtgba_reachable_iterator</class>
    <class kind="class">spot::evtgba_reachable_iterator_depth_first</class>
    <class kind="class">spot::evtgba_reachable_iterator_breadth_first</class>
  </compound>
  <compound kind="file">
    <name>reachiter.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>tgbaalgos_2reachiter_8hh</filename>
    <includes id="hash_8hh" name="hash.hh" local="yes" imported="no">misc/hash.hh</includes>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba/tgba.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_reachable_iterator</class>
    <class kind="class">spot::tgba_reachable_iterator_depth_first</class>
    <class kind="class">spot::tgba_reachable_iterator_breadth_first</class>
  </compound>
  <compound kind="file">
    <name>save.hh</name>
    <path>/home/adl/proj/spot/src/evtgbaalgos/</path>
    <filename>evtgbaalgos_2save_8hh</filename>
    <includes id="evtgba_8hh" name="evtgba.hh" local="yes" imported="no">evtgba/evtgba.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>evtgba_save_reachable</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>ade1f7e5b6c685b5393d8a6d3ab7e09d</anchor>
      <arglist>(std::ostream &amp;os, const evtgba *g)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>save.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>tgbaalgos_2save_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba/tgba.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>tgba_save_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g5c7f56a5941eb2380676c4ff6706e1c2</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>tgba2evtgba.hh</name>
    <path>/home/adl/proj/spot/src/evtgbaalgos/</path>
    <filename>tgba2evtgba_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>evtgba_explicit *</type>
      <name>tgba_to_evtgba</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>3b22a05fb3e16d693b2d80425dfa4500</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>public.hh</name>
    <path>/home/adl/proj/spot/src/evtgbaparse/</path>
    <filename>evtgbaparse_2public_8hh</filename>
    <includes id="explicit_8hh" name="explicit.hh" local="yes" imported="no">evtgba/explicit.hh</includes>
    <namespace>spot</namespace>
    <member kind="typedef">
      <type>std::pair&lt; evtgbayy::location, std::string &gt;</type>
      <name>evtgba_parse_error</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>d049f28e03c2ebe740e5597034fd5c93</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; evtgba_parse_error &gt;</type>
      <name>evtgba_parse_error_list</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>90dc0e2318bb80c45893922f42ec44fe</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>evtgba_explicit *</type>
      <name>evtgba_parse</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>6805befaee0df3f7cb4427d30782f230</anchor>
      <arglist>(const std::string &amp;filename, evtgba_parse_error_list &amp;error_list, bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>format_evtgba_parse_errors</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>805ae4b750b93c1d2e19d81faa609065</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;filename, evtgba_parse_error_list &amp;error_list)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>public.hh</name>
    <path>/home/adl/proj/spot/src/ltlparse/</path>
    <filename>ltlparse_2public_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="ltlparse_2location_8hh" name="location.hh" local="yes" imported="no">ltlparse/location.hh</includes>
    <includes id="defaultenv_8hh" name="defaultenv.hh" local="yes" imported="no">ltlenv/defaultenv.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="typedef">
      <type>std::pair&lt; ltlyy::location, std::string &gt;</type>
      <name>parse_error</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g9bf19c38b4ae7d74e6a9633ed360c147</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; parse_error &gt;</type>
      <name>parse_error_list</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g9eb0f7867a212f92b0fd64a6ac5a12cd</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>parse</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g64770999ec846fe07292163e33509da5</anchor>
      <arglist>(const std::string &amp;ltl_string, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>format_parse_errors</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gc69f09c520acfe742653158946413faf</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;ltl_string, parse_error_list &amp;error_list)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>public.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgba_2public_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba.hh</includes>
    <includes id="tgbabddconcrete_8hh" name="tgbabddconcrete.hh" local="yes" imported="no">tgbabddconcrete.hh</includes>
    <includes id="tgbabddconcreteproduct_8hh" name="tgbabddconcreteproduct.hh" local="yes" imported="no">tgbabddconcreteproduct.hh</includes>
    <includes id="bddprint_8hh" name="bddprint.hh" local="yes" imported="no">bddprint.hh</includes>
  </compound>
  <compound kind="file">
    <name>public.hh</name>
    <path>/home/adl/proj/spot/src/tgbaparse/</path>
    <filename>tgbaparse_2public_8hh</filename>
    <includes id="tgbaexplicit_8hh" name="tgbaexplicit.hh" local="yes" imported="no">tgba/tgbaexplicit.hh</includes>
    <includes id="defaultenv_8hh" name="defaultenv.hh" local="yes" imported="no">ltlenv/defaultenv.hh</includes>
    <namespace>spot</namespace>
    <member kind="typedef">
      <type>std::pair&lt; tgbayy::location, std::string &gt;</type>
      <name>tgba_parse_error</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gdcc2cd9b328a5c3f63918c577f86f18c</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; tgba_parse_error &gt;</type>
      <name>tgba_parse_error_list</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g76b22bb081d5c36378098caa5bf58081</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_parse</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g4548696ddb1001650da2d5fd9b0f77bd</anchor>
      <arglist>(const std::string &amp;filename, tgba_parse_error_list &amp;error_list, bdd_dict *dict, ltl::environment &amp;env=ltl::default_environment::instance(), ltl::environment &amp;envacc=ltl::default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>format_tgba_parse_errors</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g95b48c3bd896478c7e22b0d49c39d89c</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;filename, tgba_parse_error_list &amp;error_list)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>allnodes.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>allnodes_8hh</filename>
    <includes id="binop_8hh" name="binop.hh" local="yes" imported="no">binop.hh</includes>
    <includes id="unop_8hh" name="unop.hh" local="yes" imported="no">unop.hh</includes>
    <includes id="multop_8hh" name="multop.hh" local="yes" imported="no">multop.hh</includes>
    <includes id="atomic__prop_8hh" name="atomic_prop.hh" local="yes" imported="no">atomic_prop.hh</includes>
    <includes id="constant_8hh" name="constant.hh" local="yes" imported="no">constant.hh</includes>
  </compound>
  <compound kind="file">
    <name>atomic_prop.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>atomic__prop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <includes id="environment_8hh" name="environment.hh" local="yes" imported="no">ltlenv/environment.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::atomic_prop</class>
  </compound>
  <compound kind="file">
    <name>binop.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>binop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::binop</class>
  </compound>
  <compound kind="file">
    <name>constant.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>constant_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::constant</class>
  </compound>
  <compound kind="file">
    <name>formula.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>formula_8hh</filename>
    <includes id="predecl_8hh" name="predecl.hh" local="yes" imported="no">predecl.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::formula</class>
    <class kind="struct">spot::ltl::formula_ptr_less_than</class>
    <class kind="struct">spot::ltl::formula_ptr_hash</class>
  </compound>
  <compound kind="file">
    <name>multop.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>multop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::multop</class>
    <class kind="struct">spot::ltl::multop::paircmp</class>
  </compound>
  <compound kind="file">
    <name>predecl.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>predecl_8hh</filename>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
  </compound>
  <compound kind="file">
    <name>refformula.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>refformula_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::ref_formula</class>
  </compound>
  <compound kind="file">
    <name>unop.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>unop_8hh</filename>
    <includes id="refformula_8hh" name="refformula.hh" local="yes" imported="no">refformula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::unop</class>
  </compound>
  <compound kind="file">
    <name>visitor.hh</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>visitor_8hh</filename>
    <includes id="predecl_8hh" name="predecl.hh" local="yes" imported="no">predecl.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="struct">spot::ltl::visitor</class>
    <class kind="struct">spot::ltl::const_visitor</class>
  </compound>
  <compound kind="file">
    <name>declenv.hh</name>
    <path>/home/adl/proj/spot/src/ltlenv/</path>
    <filename>declenv_8hh</filename>
    <includes id="environment_8hh" name="environment.hh" local="yes" imported="no">environment.hh</includes>
    <includes id="destroy_8hh" name="destroy.hh" local="yes" imported="no">ltlvisit/destroy.hh</includes>
    <includes id="atomic__prop_8hh" name="atomic_prop.hh" local="yes" imported="no">ltlast/atomic_prop.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::declarative_environment</class>
  </compound>
  <compound kind="file">
    <name>defaultenv.hh</name>
    <path>/home/adl/proj/spot/src/ltlenv/</path>
    <filename>defaultenv_8hh</filename>
    <includes id="environment_8hh" name="environment.hh" local="yes" imported="no">environment.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::default_environment</class>
  </compound>
  <compound kind="file">
    <name>environment.hh</name>
    <path>/home/adl/proj/spot/src/ltlenv/</path>
    <filename>environment_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::environment</class>
  </compound>
  <compound kind="file">
    <name>rodeco.hh</name>
    <path>/home/adl/proj/spot/src/ltlenv/</path>
    <filename>rodeco_8hh</filename>
    <includes id="environment_8hh" name="environment.hh" local="yes" imported="no">environment.hh</includes>
    <includes id="destroy_8hh" name="destroy.hh" local="yes" imported="no">ltlvisit/destroy.hh</includes>
    <includes id="atomic__prop_8hh" name="atomic_prop.hh" local="yes" imported="no">ltlast/atomic_prop.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::read_only_environment</class>
  </compound>
  <compound kind="file">
    <name>location.hh</name>
    <path>/home/adl/proj/spot/src/ltlparse/</path>
    <filename>ltlparse_2location_8hh</filename>
    <includes id="ltlparse_2position_8hh" name="position.hh" local="yes" imported="no">position.hh</includes>
    <namespace>ltlyy</namespace>
    <class kind="class">ltlyy::location</class>
    <member kind="function">
      <type>const location</type>
      <name>operator+</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>70e347efcc4af1489a902f013c075795</anchor>
      <arglist>(const location &amp;begin, const location &amp;end)</arglist>
    </member>
    <member kind="function">
      <type>const location</type>
      <name>operator+</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>5ad9aab98f3f4a4d03e28d26b885f0a9</anchor>
      <arglist>(const location &amp;begin, unsigned int width)</arglist>
    </member>
    <member kind="function">
      <type>location &amp;</type>
      <name>operator+=</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>e2f8bb8edf11642db0e6459bc4f2f2a5</anchor>
      <arglist>(location &amp;res, unsigned int width)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>7ca655af533c8a7eb435a85f9a1cd234</anchor>
      <arglist>(std::ostream &amp;ostr, const location &amp;loc)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>location.hh</name>
    <path>/home/adl/proj/spot/src/sautparse/</path>
    <filename>sautparse_2location_8hh</filename>
    <includes id="sautparse_2position_8hh" name="position.hh" local="yes" imported="no">position.hh</includes>
    <namespace>sautyy</namespace>
    <class kind="class">sautyy::location</class>
    <member kind="function">
      <type>const location</type>
      <name>operator+</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>ede8694104898a79760c3a3e7feef742</anchor>
      <arglist>(const location &amp;begin, const location &amp;end)</arglist>
    </member>
    <member kind="function">
      <type>const location</type>
      <name>operator+</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>bd47fd459e7ff01bbcf78ddf011a4270</anchor>
      <arglist>(const location &amp;begin, unsigned int width)</arglist>
    </member>
    <member kind="function">
      <type>location &amp;</type>
      <name>operator+=</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>797709a44e4953c4491527df2466760f</anchor>
      <arglist>(location &amp;res, unsigned int width)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>3a441520483d3e701cc2a578cca742c1</anchor>
      <arglist>(std::ostream &amp;ostr, const location &amp;loc)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>position.hh</name>
    <path>/home/adl/proj/spot/src/ltlparse/</path>
    <filename>ltlparse_2position_8hh</filename>
    <namespace>ltlyy</namespace>
    <class kind="class">ltlyy::position</class>
    <member kind="function">
      <type>const position &amp;</type>
      <name>operator+=</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>d4b9d0fc18d630a816d0d5879569a400</anchor>
      <arglist>(position &amp;res, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position</type>
      <name>operator+</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>cbba141d458e781c3e884545fff8726f</anchor>
      <arglist>(const position &amp;begin, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position &amp;</type>
      <name>operator-=</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>f3f149ab4d359d9539af7f886620d4ac</anchor>
      <arglist>(position &amp;res, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position</type>
      <name>operator-</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>e322d7f17d8337de215434430d4b5039</anchor>
      <arglist>(const position &amp;begin, const int width)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>0dd64d7f5203ae05d3a671f4dc66511b</anchor>
      <arglist>(std::ostream &amp;ostr, const position &amp;pos)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>position.hh</name>
    <path>/home/adl/proj/spot/src/sautparse/</path>
    <filename>sautparse_2position_8hh</filename>
    <namespace>sautyy</namespace>
    <class kind="class">sautyy::position</class>
    <member kind="function">
      <type>const position &amp;</type>
      <name>operator+=</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>9582da2fc987ee661e87cb56b8a0f7d9</anchor>
      <arglist>(position &amp;res, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position</type>
      <name>operator+</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>e6bc5e4aeccb2a840854b726928b448f</anchor>
      <arglist>(const position &amp;begin, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position &amp;</type>
      <name>operator-=</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>87545a05c5946f819073c186d2ea05c7</anchor>
      <arglist>(position &amp;res, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position</type>
      <name>operator-</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>e0cbc2c21416807379de671135c7bd5a</anchor>
      <arglist>(const position &amp;begin, const int width)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>b4e5b909a69338a5fc382290bc4152bf</anchor>
      <arglist>(std::ostream &amp;ostr, const position &amp;pos)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>stack.hh</name>
    <path>/home/adl/proj/spot/src/ltlparse/</path>
    <filename>ltlparse_2stack_8hh</filename>
    <namespace>ltlyy</namespace>
    <class kind="class">ltlyy::stack</class>
    <class kind="class">ltlyy::slice</class>
  </compound>
  <compound kind="file">
    <name>stack.hh</name>
    <path>/home/adl/proj/spot/src/sautparse/</path>
    <filename>sautparse_2stack_8hh</filename>
    <namespace>sautyy</namespace>
    <class kind="class">sautyy::stack</class>
    <class kind="class">sautyy::slice</class>
  </compound>
  <compound kind="file">
    <name>apcollect.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>apcollect_8hh</filename>
    <includes id="atomic__prop_8hh" name="atomic_prop.hh" local="yes" imported="no">ltlast/atomic_prop.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="typedef">
      <type>std::set&lt; atomic_prop *, formula_ptr_less_than &gt;</type>
      <name>atomic_prop_set</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g305ebfb1906e717fc70cfba0fa14b4b9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>atomic_prop_set *</type>
      <name>atomic_prop_collect</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g335fb11ef18d07a729b03ee76719ead4</anchor>
      <arglist>(const formula *f, atomic_prop_set *s=0)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>basicreduce.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>basicreduce_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="function">
      <type>formula *</type>
      <name>basic_reduce</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>g0f4e7d16383675691de1722ee00388a2</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_GF</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g937576e148ca0cea8678e2e35f95d80c</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_FG</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gbd383d0bbdfa61428df282309cadddc6</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>clone.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>clone_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="visitor_8hh" name="visitor.hh" local="yes" imported="no">ltlast/visitor.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::clone_visitor</class>
    <member kind="function">
      <type>formula *</type>
      <name>clone</name>
      <anchorfile>group__ltl__essential.html</anchorfile>
      <anchor>g8e017c7cfd3dcd47b3cc1074371da6bc</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>contain.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>contain_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="ltl2tgba__fm_8hh" name="ltl2tgba_fm.hh" local="yes" imported="no">tgbaalgos/ltl2tgba_fm.hh</includes>
    <includes id="hash_8hh" name="hash.hh" local="yes" imported="no">misc/hash.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::language_containment_checker</class>
    <class kind="struct">spot::ltl::language_containment_checker::record_</class>
    <member kind="function">
      <type>formula *</type>
      <name>reduce_tau03</name>
      <anchorfile>namespacespot_1_1ltl.html</anchorfile>
      <anchor>16bd0bb215f85d6aed821ecbca7e36de</anchor>
      <arglist>(const formula *f, bool stronger=true)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>destroy.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>destroy_8hh</filename>
    <includes id="postfix_8hh" name="postfix.hh" local="yes" imported="no">ltlvisit/postfix.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="function">
      <type>void</type>
      <name>destroy</name>
      <anchorfile>group__ltl__essential.html</anchorfile>
      <anchor>g7dfba082e4a6aca346befcc46f87e358</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>dump.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>dump_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gaf316635f1320fa38885fa89e23e2098</anchor>
      <arglist>(std::ostream &amp;os, const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>length.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>length_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="function">
      <type>int</type>
      <name>length</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gbf324b4e946522d1b4caf3ce003ec903</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>lunabbrev.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>lunabbrev_8hh</filename>
    <includes id="clone_8hh" name="clone.hh" local="yes" imported="no">clone.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::unabbreviate_logic_visitor</class>
    <member kind="function">
      <type>formula *</type>
      <name>unabbreviate_logic</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ge5f253667eed8184ea82a34db7ae2f71</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>nenoform.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>nenoform_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="visitor_8hh" name="visitor.hh" local="yes" imported="no">ltlast/visitor.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="function">
      <type>formula *</type>
      <name>negative_normal_form</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>gfe4e6a149b451249b6c8bf03dedc5eeb</anchor>
      <arglist>(const formula *f, bool negated=false)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>postfix.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>postfix_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="visitor_8hh" name="visitor.hh" local="yes" imported="no">ltlast/visitor.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::postfix_visitor</class>
  </compound>
  <compound kind="file">
    <name>randomltl.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>randomltl_8hh</filename>
    <includes id="apcollect_8hh" name="apcollect.hh" local="yes" imported="no">apcollect.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::random_ltl</class>
    <class kind="struct">spot::ltl::random_ltl::op_proba</class>
  </compound>
  <compound kind="file">
    <name>reduce.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>reduce_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="visitor_8hh" name="visitor.hh" local="yes" imported="no">ltlast/visitor.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="enumeration">
      <name>reduce_options</name>
      <anchor>gc9e66395d0e9cb870fa7b1ca208b70ca</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_None</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70cabff3607cc02f12d6756d0244a8f5464a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Basics</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70cab83ef042ab620af2f258a817e95f8f80</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Syntactic_Implications</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca22d75bbadb5b030981574ae49668ad94</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Eventuality_And_Universality</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70caabb627af73b5817a542506be482f396d</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Containment_Checks</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca22286d57705e7511f13a75c05ac0a39f</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Containment_Checks_Stronger</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca0721d15d048b11cfe234f14850dbc9c5</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_All</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca1629bc689540d42e2f86eea77a6cd275</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>reduce</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>g31729856de4be685ad913e4e8da59344</anchor>
      <arglist>(const formula *f, int opt=Reduce_All)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_eventual</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g3210a9b433640efe1ef74e0da6c678e5</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_universal</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g10c79e8a8c5b58fa77cbb6dab5857083</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>simpfg.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>simpfg_8hh</filename>
    <includes id="clone_8hh" name="clone.hh" local="yes" imported="no">clone.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::simplify_f_g_visitor</class>
    <member kind="function">
      <type>formula *</type>
      <name>simplify_f_g</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>gee8b3f409c756decaa12345b2bac6091</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>syntimpl.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>syntimpl_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="function">
      <type>bool</type>
      <name>syntactic_implication</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g0117add821f085e941eada4aa2ce4bf9</anchor>
      <arglist>(const formula *f1, const formula *f2)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>syntactic_implication_neg</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gd190a68d649650c2b608829dcf258ea1</anchor>
      <arglist>(const formula *f1, const formula *f2, bool right)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>tostring.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>tostring_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="no" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>to_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gda7ab7261e386edb04ba2949b1a83210</anchor>
      <arglist>(const formula *f, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>to_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g49f14e7d6937a9ed58173e6af1080592</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>to_spin_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gc80486e726928b415b2da6c41eabf02b</anchor>
      <arglist>(const formula *f, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>to_spin_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gcc70e63a877976973682279353031407</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>tunabbrev.hh</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>tunabbrev_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="lunabbrev_8hh" name="lunabbrev.hh" local="yes" imported="no">ltlvisit/lunabbrev.hh</includes>
    <namespace>spot</namespace>
    <namespace>spot::ltl</namespace>
    <class kind="class">spot::ltl::unabbreviate_ltl_visitor</class>
    <member kind="function">
      <type>formula *</type>
      <name>unabbreviate_ltl</name>
      <anchorfile>namespacespot_1_1ltl.html</anchorfile>
      <anchor>dcb3082ccb3a689482acbe76c25c3840</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>bareword.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>bareword_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>bool</type>
      <name>is_bare_word</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gae6fadac6a2f91d7b8e27b3eb6ad647e</anchor>
      <arglist>(const char *str)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>quote_unless_bare_word</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g4731f21b7b43332b5c7b5bc63c6d67e6</anchor>
      <arglist>(const std::string &amp;str)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>bddalloc.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>bddalloc_8hh</filename>
    <includes id="freelist_8hh" name="freelist.hh" local="yes" imported="no">freelist.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::bdd_allocator</class>
  </compound>
  <compound kind="file">
    <name>bddlt.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>bddlt_8hh</filename>
    <namespace>spot</namespace>
    <class kind="struct">spot::bdd_less_than</class>
  </compound>
  <compound kind="file">
    <name>escape.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>escape_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>escape_str</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g84a8b196e2ec651f0fd039c70c7ff6cb</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>escape_str</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gd4b0b4fae7b93db7d704a5fef6e021c6</anchor>
      <arglist>(const std::string &amp;str)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>freelist.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>freelist_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::free_list</class>
  </compound>
  <compound kind="file">
    <name>hash.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>hash_8hh</filename>
    <includes id="hashfunc_8hh" name="hashfunc.hh" local="yes" imported="no">hashfunc.hh</includes>
    <namespace>spot</namespace>
    <class kind="struct">spot::ptr_hash</class>
    <class kind="struct">spot::string_hash</class>
  </compound>
  <compound kind="file">
    <name>hashfunc.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>hashfunc_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>size_t</type>
      <name>wang32_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>g9422ff0c16df957910dd4a0275d9f726</anchor>
      <arglist>(size_t key)</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>knuth32_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>gea94dbea4a286b0bde253baf07e7a56e</anchor>
      <arglist>(size_t key)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>ltstr.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>ltstr_8hh</filename>
    <namespace>spot</namespace>
    <class kind="struct">spot::char_ptr_less_than</class>
  </compound>
  <compound kind="file">
    <name>memusage.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>memusage_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>int</type>
      <name>memusage</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>1a8d0610b61c0a30aad16791b0b73d15</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>minato.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>minato_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::minato_isop</class>
    <class kind="struct">spot::minato_isop::local_vars</class>
  </compound>
  <compound kind="file">
    <name>modgray.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>modgray_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::loopless_modular_mixed_radix_gray_code</class>
  </compound>
  <compound kind="file">
    <name>optionmap.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>optionmap_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::option_map</class>
  </compound>
  <compound kind="file">
    <name>random.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>random_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::barand</class>
    <member kind="function">
      <type>void</type>
      <name>srand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g539812ab355a561fee1ecbfe60b276e4</anchor>
      <arglist>(unsigned int seed)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>rrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g86ce713fe60de9af440a7331de74aa1a</anchor>
      <arglist>(int min, int max)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>mrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>gfe8da996f40caa7d188f6b408ae62904</anchor>
      <arglist>(int max)</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>drand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g42801cd81368df5c1c61aa1626e299ff</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>nrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g978e1c6fb8f447274a05ddd3f87ce58b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>bmrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g0130217a19e5156f796ab774dca4b08c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>prand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g816b558a7c64daca3a2a018704377dac</anchor>
      <arglist>(double p)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>timer.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>timer_8hh</filename>
    <namespace>spot</namespace>
    <class kind="struct">spot::time_info</class>
    <class kind="class">spot::timer</class>
    <class kind="class">spot::timer_map</class>
  </compound>
  <compound kind="file">
    <name>version.hh</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>version_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>const char *</type>
      <name>version</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g85c83eb1d18703782d129dbe4a518fca</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>bdddict.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>bdddict_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="bddalloc_8hh" name="bddalloc.hh" local="yes" imported="no">misc/bddalloc.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::bdd_dict</class>
    <class kind="class">spot::bdd_dict::anon_free_list</class>
  </compound>
  <compound kind="file">
    <name>bddprint.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>bddprint_8hh</filename>
    <includes id="bdddict_8hh" name="bdddict.hh" local="yes" imported="no">bdddict.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_sat</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>59e91579989d390a3979a5badf302255</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>bdd_format_sat</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>ba342c9d66d191b7ed930e2b02043e7f</anchor>
      <arglist>(const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_acc</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>3b659fe265e2014282d3b25db928794b</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_accset</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>a75a117405f2292f6d80abc2ae930946</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>bdd_format_accset</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>a04818924fa41d2230160b499d95173d</anchor>
      <arglist>(const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_set</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>ca49e9ec778c8a6b38455a68db8d6ece</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>bdd_format_set</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>73d8fa997efd95d08e0d1b9dfa7b7073</anchor>
      <arglist>(const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_formula</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>174c8f965f37e9a26b33c9f76183720f</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>bdd_format_formula</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>b9eea881fdb5d8e629db7102fde452ea</anchor>
      <arglist>(const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_dot</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>3ec151057e33aadb60d2b47ffaa64c24</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_table</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>c8434843d2dcb0d99a027e398b01bafe</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>formula2bdd.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>formula2bdd_8hh</filename>
    <includes id="bdddict_8hh" name="bdddict.hh" local="yes" imported="no">bdddict.hh</includes>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>bdd</type>
      <name>formula_to_bdd</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>b6641d7d9896e016376b1c618da92469</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *d, void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>const ltl::formula *</type>
      <name>bdd_to_formula</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>ebfbc06108c79ba74f84a838af4f1772</anchor>
      <arglist>(bdd f, const bdd_dict *d)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>state.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>state_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::state</class>
    <class kind="struct">spot::state_ptr_less_than</class>
    <class kind="struct">spot::state_ptr_equal</class>
    <class kind="struct">spot::state_ptr_hash</class>
  </compound>
  <compound kind="file">
    <name>statebdd.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>statebdd_8hh</filename>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">state.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::state_bdd</class>
  </compound>
  <compound kind="file">
    <name>succiter.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>succiter_8hh</filename>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">state.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_succ_iterator</class>
  </compound>
  <compound kind="file">
    <name>succiterconcrete.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>succiterconcrete_8hh</filename>
    <includes id="statebdd_8hh" name="statebdd.hh" local="yes" imported="no">statebdd.hh</includes>
    <includes id="succiter_8hh" name="succiter.hh" local="yes" imported="no">succiter.hh</includes>
    <includes id="tgbabddcoredata_8hh" name="tgbabddcoredata.hh" local="yes" imported="no">tgbabddcoredata.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_succ_iterator_concrete</class>
  </compound>
  <compound kind="file">
    <name>tgba.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgba_8hh</filename>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">state.hh</includes>
    <includes id="succiter_8hh" name="succiter.hh" local="yes" imported="no">succiter.hh</includes>
    <includes id="bdddict_8hh" name="bdddict.hh" local="yes" imported="no">bdddict.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba</class>
  </compound>
  <compound kind="file">
    <name>tgbabddconcrete.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbabddconcrete_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba.hh</includes>
    <includes id="statebdd_8hh" name="statebdd.hh" local="yes" imported="no">statebdd.hh</includes>
    <includes id="tgbabddfactory_8hh" name="tgbabddfactory.hh" local="yes" imported="no">tgbabddfactory.hh</includes>
    <includes id="succiterconcrete_8hh" name="succiterconcrete.hh" local="yes" imported="no">succiterconcrete.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_bdd_concrete</class>
  </compound>
  <compound kind="file">
    <name>tgbabddconcretefactory.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbabddconcretefactory_8hh</filename>
    <includes id="hash_8hh" name="hash.hh" local="yes" imported="no">misc/hash.hh</includes>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="tgbabddfactory_8hh" name="tgbabddfactory.hh" local="yes" imported="no">tgbabddfactory.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_bdd_concrete_factory</class>
  </compound>
  <compound kind="file">
    <name>tgbabddconcreteproduct.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbabddconcreteproduct_8hh</filename>
    <includes id="tgbabddconcrete_8hh" name="tgbabddconcrete.hh" local="yes" imported="no">tgbabddconcrete.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>tgba_bdd_concrete *</type>
      <name>product</name>
      <anchorfile>group__tgba__algorithms.html</anchorfile>
      <anchor>g9a8f2973e4358cf18aa162634b61ab51</anchor>
      <arglist>(const tgba_bdd_concrete *left, const tgba_bdd_concrete *right)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>tgbabddcoredata.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbabddcoredata_8hh</filename>
    <includes id="bdddict_8hh" name="bdddict.hh" local="yes" imported="no">bdddict.hh</includes>
    <namespace>spot</namespace>
    <class kind="struct">spot::tgba_bdd_core_data</class>
  </compound>
  <compound kind="file">
    <name>tgbabddfactory.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbabddfactory_8hh</filename>
    <includes id="tgbabddcoredata_8hh" name="tgbabddcoredata.hh" local="yes" imported="no">tgbabddcoredata.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_bdd_factory</class>
  </compound>
  <compound kind="file">
    <name>tgbaexplicit.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbaexplicit_8hh</filename>
    <includes id="hash_8hh" name="hash.hh" local="yes" imported="no">misc/hash.hh</includes>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba.hh</includes>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_explicit</class>
    <class kind="struct">spot::tgba_explicit::transition</class>
    <class kind="class">spot::state_explicit</class>
    <class kind="class">spot::tgba_explicit_succ_iterator</class>
  </compound>
  <compound kind="file">
    <name>tgbaproduct.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbaproduct_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba.hh</includes>
    <includes id="statebdd_8hh" name="statebdd.hh" local="yes" imported="no">statebdd.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::state_product</class>
    <class kind="class">spot::tgba_succ_iterator_product</class>
    <class kind="class">spot::tgba_product</class>
  </compound>
  <compound kind="file">
    <name>tgbareduc.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbareduc_8hh</filename>
    <includes id="tgbaexplicit_8hh" name="tgbaexplicit.hh" local="yes" imported="no">tgbaexplicit.hh</includes>
    <includes id="tgbaalgos_2reachiter_8hh" name="reachiter.hh" local="yes" imported="no">tgbaalgos/reachiter.hh</includes>
    <includes id="explscc_8hh" name="explscc.hh" local="yes" imported="no">tgbaalgos/gtec/explscc.hh</includes>
    <includes id="nsheap_8hh" name="nsheap.hh" local="yes" imported="no">tgbaalgos/gtec/nsheap.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::direct_simulation_relation</class>
    <class kind="class">spot::delayed_simulation_relation</class>
    <class kind="class">spot::tgba_reduc</class>
    <member kind="typedef">
      <type>std::pair&lt; const spot::state *, const spot::state * &gt;</type>
      <name>state_couple</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>335079e354907be71c014b01c2fb1573</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; state_couple * &gt;</type>
      <name>simulation_relation</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>9ec17bbfe207de0e9c4e699ee3f02572</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>tgbatba.hh</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>tgbatba_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba.hh</includes>
    <includes id="bddlt_8hh" name="bddlt.hh" local="yes" imported="no">misc/bddlt.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_tba_proxy</class>
    <class kind="class">spot::tgba_sba_proxy</class>
  </compound>
  <compound kind="file">
    <name>bfssteps.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>bfssteps_8hh</filename>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">tgba/state.hh</includes>
    <includes id="emptiness_8hh" name="emptiness.hh" local="yes" imported="no">emptiness.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::bfs_steps</class>
  </compound>
  <compound kind="file">
    <name>dottydec.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>dottydec_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::dotty_decorator</class>
  </compound>
  <compound kind="file">
    <name>dupexp.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>dupexp_8hh</filename>
    <includes id="tgbaexplicit_8hh" name="tgbaexplicit.hh" local="yes" imported="no">tgba/tgbaexplicit.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_dupexp_bfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gee64e1fa586af205fa41aaf2c0dbb4bc</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_dupexp_dfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>g2d17e428249fb0e43ce9ae1ee0e4ddf8</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>emptiness.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>emptiness_8hh</filename>
    <includes id="optionmap_8hh" name="optionmap.hh" local="yes" imported="no">misc/optionmap.hh</includes>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">tgba/state.hh</includes>
    <includes id="emptiness__stats_8hh" name="emptiness_stats.hh" local="yes" imported="no">emptiness_stats.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::emptiness_check_result</class>
    <class kind="class">spot::emptiness_check</class>
    <class kind="class">spot::emptiness_check_instantiator</class>
    <class kind="struct">spot::tgba_run</class>
    <class kind="struct">spot::tgba_run::step</class>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>print_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g3aa3b6dd9854c94b6aaabafd4a612de2</anchor>
      <arglist>(std::ostream &amp;os, const tgba *a, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>tgba_run_to_tgba</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g60bd6401d35428aa22bda3388765099b</anchor>
      <arglist>(const tgba *a, const tgba_run *run)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>emptiness_stats.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>emptiness__stats_8hh</filename>
    <includes id="ltstr_8hh" name="ltstr.hh" local="yes" imported="no">misc/ltstr.hh</includes>
    <namespace>spot</namespace>
    <class kind="struct">spot::unsigned_statistics</class>
    <class kind="class">spot::unsigned_statistics_copy</class>
    <class kind="class">spot::ec_statistics</class>
    <class kind="class">spot::ars_statistics</class>
    <class kind="class">spot::acss_statistics</class>
  </compound>
  <compound kind="file">
    <name>ce.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/gtec/</path>
    <filename>ce_8hh</filename>
    <includes id="status_8hh" name="status.hh" local="yes" imported="no">status.hh</includes>
    <includes id="emptiness_8hh" name="emptiness.hh" local="yes" imported="no">tgbaalgos/emptiness.hh</includes>
    <includes id="emptiness__stats_8hh" name="emptiness_stats.hh" local="yes" imported="no">tgbaalgos/emptiness_stats.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::couvreur99_check_result</class>
  </compound>
  <compound kind="file">
    <name>explscc.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/gtec/</path>
    <filename>explscc_8hh</filename>
    <includes id="hash_8hh" name="hash.hh" local="yes" imported="no">misc/hash.hh</includes>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">tgba/state.hh</includes>
    <includes id="sccstack_8hh" name="sccstack.hh" local="yes" imported="no">sccstack.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::explicit_connected_component</class>
    <class kind="class">spot::connected_component_hash_set</class>
    <class kind="class">spot::explicit_connected_component_factory</class>
    <class kind="class">spot::connected_component_hash_set_factory</class>
  </compound>
  <compound kind="file">
    <name>gtec.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/gtec/</path>
    <filename>gtec_8hh</filename>
    <includes id="status_8hh" name="status.hh" local="yes" imported="no">status.hh</includes>
    <includes id="emptiness_8hh" name="emptiness.hh" local="yes" imported="no">tgbaalgos/emptiness.hh</includes>
    <includes id="emptiness__stats_8hh" name="emptiness_stats.hh" local="yes" imported="no">tgbaalgos/emptiness_stats.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::couvreur99_check</class>
    <class kind="class">spot::couvreur99_check_shy</class>
    <class kind="struct">spot::couvreur99_check_shy::successor</class>
    <class kind="struct">spot::couvreur99_check_shy::todo_item</class>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>couvreur99</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g9bb3670ecff03df6d792c8a315c3e75c</anchor>
      <arglist>(const tgba *a, option_map options=option_map(), const numbered_state_heap_factory *nshf=numbered_state_heap_hash_map_factory::instance())</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>nsheap.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/gtec/</path>
    <filename>nsheap_8hh</filename>
    <includes id="state_8hh" name="state.hh" local="yes" imported="no">tgba/state.hh</includes>
    <includes id="hash_8hh" name="hash.hh" local="yes" imported="no">misc/hash.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::numbered_state_heap_const_iterator</class>
    <class kind="class">spot::numbered_state_heap</class>
    <class kind="class">spot::numbered_state_heap_factory</class>
    <class kind="class">spot::numbered_state_heap_hash_map</class>
    <class kind="class">spot::numbered_state_heap_hash_map_factory</class>
  </compound>
  <compound kind="file">
    <name>sccstack.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/gtec/</path>
    <filename>sccstack_8hh</filename>
    <includes id="state_8hh" name="state.hh" local="no" imported="no">tgba/state.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::scc_stack</class>
    <class kind="struct">spot::scc_stack::connected_component</class>
  </compound>
  <compound kind="file">
    <name>status.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/gtec/</path>
    <filename>status_8hh</filename>
    <includes id="sccstack_8hh" name="sccstack.hh" local="yes" imported="no">sccstack.hh</includes>
    <includes id="nsheap_8hh" name="nsheap.hh" local="yes" imported="no">nsheap.hh</includes>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba/tgba.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::couvreur99_check_status</class>
  </compound>
  <compound kind="file">
    <name>gv04.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>gv04_8hh</filename>
    <includes id="optionmap_8hh" name="optionmap.hh" local="yes" imported="no">misc/optionmap.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_gv04_check</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ge45e3a4c2ba4b8c0609a2afca67eabe8</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>lbtt.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>lbtt_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba/tgba.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>lbtt_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g955eb1141519477fda8d09fae2a9cb4a</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>ltl2tgba_fm.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>ltl2tgba__fm_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="tgbaexplicit_8hh" name="tgbaexplicit.hh" local="yes" imported="no">tgba/tgbaexplicit.hh</includes>
    <includes id="apcollect_8hh" name="apcollect.hh" local="yes" imported="no">ltlvisit/apcollect.hh</includes>
    <includes id="reduce_8hh" name="reduce.hh" local="yes" imported="no">ltlvisit/reduce.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>ltl_to_tgba_fm</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>g749653f0f15ebc9d56e6978314f50421</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict, bool exprop=false, bool symb_merge=true, bool branching_postponement=false, bool fair_loop_approx=false, const ltl::atomic_prop_set *unobs=0, int reduce_ltl=ltl::Reduce_None, bool containment_checks=false)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>ltl2tgba_lacim.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>ltl2tgba__lacim_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="tgbabddconcrete_8hh" name="tgbabddconcrete.hh" local="yes" imported="no">tgba/tgbabddconcrete.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>tgba_bdd_concrete *</type>
      <name>ltl_to_tgba_lacim</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>g911db84b8e05185bb50b5eda55efe6b6</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>magic.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>magic_8hh</filename>
    <includes id="optionmap_8hh" name="optionmap.hh" local="yes" imported="no">misc/optionmap.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g392d772bf851002cdda0ca34615aa54b</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>bit_state_hashing_magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>gdae7938a96420813bcdabb1b31295294</anchor>
      <arglist>(const tgba *a, size_t size, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g054c1948b6c5076350e44a85ad580403</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>neverclaim.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>neverclaim_8hh</filename>
    <includes id="formula_8hh" name="formula.hh" local="yes" imported="no">ltlast/formula.hh</includes>
    <includes id="tgbatba_8hh" name="tgbatba.hh" local="yes" imported="no">tgba/tgbatba.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>never_claim_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gc20ccafb63e86ac18a638cbfda5c0eab</anchor>
      <arglist>(std::ostream &amp;os, const tgba_sba_proxy *g, const ltl::formula *f=0)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>powerset.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>powerset_8hh</filename>
    <includes id="tgbaexplicit_8hh" name="tgbaexplicit.hh" local="yes" imported="no">tgba/tgbaexplicit.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_powerset</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>g261dcd2ce23378bebecc939b72889e08</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>projrun.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>projrun_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>tgba_run *</type>
      <name>project_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g1e0b8aeb36e622ac26a6c540ac17fddc</anchor>
      <arglist>(const tgba *a_run, const tgba *a_proj, const tgba_run *run)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>randomgraph.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>randomgraph_8hh</filename>
    <includes id="apcollect_8hh" name="apcollect.hh" local="yes" imported="no">ltlvisit/apcollect.hh</includes>
    <includes id="defaultenv_8hh" name="defaultenv.hh" local="yes" imported="no">ltlenv/defaultenv.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>tgba *</type>
      <name>random_graph</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gdcf8ba83bd92b5cbf8f5b8f1083a793b</anchor>
      <arglist>(int n, float d, const ltl::atomic_prop_set *ap, bdd_dict *dict, int n_acc=0, float a=0.1, float t=0.5, ltl::environment *env=&amp;ltl::default_environment::instance())</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>reducerun.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>reducerun_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>tgba_run *</type>
      <name>reduce_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>gbc4ad10830cd80a3237415d28adeee1b</anchor>
      <arglist>(const tgba *a, const tgba_run *org)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>reductgba_sim.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>reductgba__sim_8hh</filename>
    <includes id="tgbareduc_8hh" name="tgbareduc.hh" local="yes" imported="no">tgba/tgbareduc.hh</includes>
    <includes id="tgbaalgos_2reachiter_8hh" name="reachiter.hh" local="yes" imported="no">tgbaalgos/reachiter.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::parity_game_graph</class>
    <class kind="class">spot::spoiler_node</class>
    <class kind="class">spot::duplicator_node</class>
    <class kind="class">spot::parity_game_graph_direct</class>
    <class kind="class">spot::spoiler_node_delayed</class>
    <class kind="class">spot::duplicator_node_delayed</class>
    <class kind="class">spot::parity_game_graph_delayed</class>
    <member kind="typedef">
      <type>std::vector&lt; spoiler_node * &gt;</type>
      <name>sn_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>ga807b9e3dabba60063c3dff70244c79a</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; duplicator_node * &gt;</type>
      <name>dn_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g57d3005aa0c7d42803556c39d62d2995</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; const state * &gt;</type>
      <name>s_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g1aecc57153bbe4d48c62d7dcedde5afb</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumeration">
      <name>reduce_tgba_options</name>
      <anchor>g5bd08ab74b3ab10a27beceaa04d9217a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_None</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a28ab8e63b3f476424eec1d49fb19b1e6</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_quotient_Dir_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a6ef94353974a1119b8c3265eb0bcbe42</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_transition_Dir_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a7b2d15a717f241527e25e1266370033e</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_quotient_Del_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a040be3fac90f0ddb426644a52418d7a3</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_transition_Del_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a21c74a793193bfe95e63b43ef1b59350</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Scc</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a685daa8530de270e1588cdc2c178a2ab</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_All</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a81a405e18e9bbd601c4183e1c7ff49e1</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>reduc_tgba_sim</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g3e05060db57bf0e735f08d057db3fae2</anchor>
      <arglist>(const tgba *a, int opt=Reduce_All)</arglist>
    </member>
    <member kind="function">
      <type>direct_simulation_relation *</type>
      <name>get_direct_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g031fe9f92e0d1dbe9af8ca695b2f1161</anchor>
      <arglist>(const tgba *a, std::ostream &amp;os, int opt=-1)</arglist>
    </member>
    <member kind="function">
      <type>delayed_simulation_relation *</type>
      <name>get_delayed_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gb80a323c2a5aa8bbf1541afa8a3036a9</anchor>
      <arglist>(const tgba *a, std::ostream &amp;os, int opt=-1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>free_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gb2aceabca87cc15e9aa79abbcc23358e</anchor>
      <arglist>(direct_simulation_relation *rel)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>free_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g9ef87dd9bf31b79cfaa19431a019b98a</anchor>
      <arglist>(delayed_simulation_relation *rel)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>replayrun.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>replayrun_8hh</filename>
    <namespace>spot</namespace>
    <member kind="function">
      <type>bool</type>
      <name>replay_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>ge2bf747c24d2fb1a06f1a033f67fe6dc</anchor>
      <arglist>(std::ostream &amp;os, const tgba *a, const tgba_run *run, bool debug=false)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>rundotdec.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>rundotdec_8hh</filename>
    <includes id="dottydec_8hh" name="dottydec.hh" local="yes" imported="no">dottydec.hh</includes>
    <includes id="emptiness_8hh" name="emptiness.hh" local="yes" imported="no">emptiness.hh</includes>
    <namespace>spot</namespace>
    <class kind="class">spot::tgba_run_dotty_decorator</class>
  </compound>
  <compound kind="file">
    <name>se05.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>se05_8hh</filename>
    <includes id="optionmap_8hh" name="optionmap.hh" local="yes" imported="no">misc/optionmap.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_se05_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g8c176368673a0c009dd3e934d57fb492</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>bit_state_hashing_se05_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ge822266082cdb9772ce31388a6538cb9</anchor>
      <arglist>(const tgba *a, size_t size, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>se05</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g23a00d19bf7613222e6e41833e515c00</anchor>
      <arglist>(const tgba *a, option_map o)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>stats.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>stats_8hh</filename>
    <includes id="tgba_8hh" name="tgba.hh" local="yes" imported="no">tgba/tgba.hh</includes>
    <namespace>spot</namespace>
    <class kind="struct">spot::tgba_statistics</class>
    <member kind="function">
      <type>tgba_statistics</type>
      <name>stats_reachable</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>geeb949ca5fa52c1e292e0a7732ef46c4</anchor>
      <arglist>(const tgba *g)</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>tau03.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>tau03_8hh</filename>
    <includes id="optionmap_8hh" name="optionmap.hh" local="yes" imported="no">misc/optionmap.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_tau03_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g159e81f9b91f99b2a749185f47924d99</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>tau03opt.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>tau03opt_8hh</filename>
    <includes id="optionmap_8hh" name="optionmap.hh" local="yes" imported="no">misc/optionmap.hh</includes>
    <namespace>spot</namespace>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_tau03_opt_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g68f7245733f5fe5a86e6dd416d7746f1</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
  </compound>
  <compound kind="file">
    <name>weight.hh</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>weight_8hh</filename>
    <namespace>spot</namespace>
    <class kind="class">spot::weight</class>
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
    <name>tgba</name>
    <title>TGBA (Transition-based Generalized Büchi Automata)</title>
    <filename>group__tgba.html</filename>
    <subgroup>tgba_essentials</subgroup>
    <subgroup>tgba_representation</subgroup>
    <subgroup>tgba_algorithms</subgroup>
  </compound>
  <compound kind="group">
    <name>emptiness_check_ssp</name>
    <title>Emptiness-check algorithms for SSP</title>
    <filename>group__emptiness__check__ssp.html</filename>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_semi</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g41573dafc02e8da2283670fa7aefebcf</anchor>
      <arglist>(const tgba *ssp_automata)</arglist>
    </member>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_shy_semi</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g9e84d9fcba32903d98fc0a0ebdc73ac1</anchor>
      <arglist>(const tgba *ssp_automata)</arglist>
    </member>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_shy</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g9dba9cdb805e4187dc072c0ff7ea3d67</anchor>
      <arglist>(const tgba *ssp_automata, bool stack_inclusion=true)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ltl_essential</name>
    <title>Essential LTL types</title>
    <filename>group__ltl__essential.html</filename>
    <class kind="class">spot::ltl::formula</class>
    <class kind="struct">spot::ltl::visitor</class>
    <class kind="class">spot::ltl::environment</class>
    <member kind="function">
      <type>formula *</type>
      <name>clone</name>
      <anchorfile>group__ltl__essential.html</anchorfile>
      <anchor>g8e017c7cfd3dcd47b3cc1074371da6bc</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>destroy</name>
      <anchorfile>group__ltl__essential.html</anchorfile>
      <anchor>g7dfba082e4a6aca346befcc46f87e358</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ltl_ast</name>
    <title>LTL Abstract Syntax Tree</title>
    <filename>group__ltl__ast.html</filename>
    <class kind="class">spot::ltl::atomic_prop</class>
    <class kind="class">spot::ltl::binop</class>
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
    <class kind="class">spot::ltl::read_only_environment</class>
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
    <name>ltl_io</name>
    <title>Input/Output of LTL formulae</title>
    <filename>group__ltl__io.html</filename>
    <class kind="class">spot::ltl::random_ltl</class>
    <member kind="typedef">
      <type>std::pair&lt; ltlyy::location, std::string &gt;</type>
      <name>parse_error</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g9bf19c38b4ae7d74e6a9633ed360c147</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; parse_error &gt;</type>
      <name>parse_error_list</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g9eb0f7867a212f92b0fd64a6ac5a12cd</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>parse</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g64770999ec846fe07292163e33509da5</anchor>
      <arglist>(const std::string &amp;ltl_string, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>format_parse_errors</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gc69f09c520acfe742653158946413faf</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;ltl_string, parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dotty</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g7e27d31b2209954c1d57d3b8b5224473</anchor>
      <arglist>(std::ostream &amp;os, const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gaf316635f1320fa38885fa89e23e2098</anchor>
      <arglist>(std::ostream &amp;os, const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>to_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gda7ab7261e386edb04ba2949b1a83210</anchor>
      <arglist>(const formula *f, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>to_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g49f14e7d6937a9ed58173e6af1080592</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>to_spin_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gc80486e726928b415b2da6c41eabf02b</anchor>
      <arglist>(const formula *f, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>to_spin_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gcc70e63a877976973682279353031407</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
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
    <member kind="enumeration">
      <name>reduce_options</name>
      <anchor>gc9e66395d0e9cb870fa7b1ca208b70ca</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_None</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70cabff3607cc02f12d6756d0244a8f5464a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Basics</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70cab83ef042ab620af2f258a817e95f8f80</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Syntactic_Implications</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca22d75bbadb5b030981574ae49668ad94</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Eventuality_And_Universality</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70caabb627af73b5817a542506be482f396d</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Containment_Checks</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca22286d57705e7511f13a75c05ac0a39f</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Containment_Checks_Stronger</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca0721d15d048b11cfe234f14850dbc9c5</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_All</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca1629bc689540d42e2f86eea77a6cd275</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>basic_reduce</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>g0f4e7d16383675691de1722ee00388a2</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>unabbreviate_logic</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ge5f253667eed8184ea82a34db7ae2f71</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>negative_normal_form</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>gfe4e6a149b451249b6c8bf03dedc5eeb</anchor>
      <arglist>(const formula *f, bool negated=false)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>reduce</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>g31729856de4be685ad913e4e8da59344</anchor>
      <arglist>(const formula *f, int opt=Reduce_All)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>simplify_f_g</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>gee8b3f409c756decaa12345b2bac6091</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>ltl_misc</name>
    <title>Miscellaneous algorithms for LTL formulae</title>
    <filename>group__ltl__misc.html</filename>
    <member kind="typedef">
      <type>std::set&lt; atomic_prop *, formula_ptr_less_than &gt;</type>
      <name>atomic_prop_set</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g305ebfb1906e717fc70cfba0fa14b4b9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>atomic_prop_set *</type>
      <name>atomic_prop_collect</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g335fb11ef18d07a729b03ee76719ead4</anchor>
      <arglist>(const formula *f, atomic_prop_set *s=0)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_GF</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g937576e148ca0cea8678e2e35f95d80c</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_FG</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gbd383d0bbdfa61428df282309cadddc6</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>length</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gbf324b4e946522d1b4caf3ce003ec903</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_eventual</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g3210a9b433640efe1ef74e0da6c678e5</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_universal</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g10c79e8a8c5b58fa77cbb6dab5857083</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>syntactic_implication</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g0117add821f085e941eada4aa2ce4bf9</anchor>
      <arglist>(const formula *f1, const formula *f2)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>syntactic_implication_neg</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gd190a68d649650c2b608829dcf258ea1</anchor>
      <arglist>(const formula *f1, const formula *f2, bool right)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>misc_tools</name>
    <title>Miscellaneous helper algorithms</title>
    <filename>group__misc__tools.html</filename>
    <subgroup>hash_funcs</subgroup>
    <subgroup>random</subgroup>
    <class kind="class">spot::bdd_allocator</class>
    <class kind="struct">spot::bdd_less_than</class>
    <class kind="class">spot::free_list</class>
    <class kind="struct">spot::char_ptr_less_than</class>
    <class kind="class">spot::minato_isop</class>
    <class kind="class">spot::loopless_modular_mixed_radix_gray_code</class>
    <class kind="class">spot::option_map</class>
    <class kind="struct">spot::time_info</class>
    <class kind="class">spot::timer</class>
    <class kind="class">spot::timer_map</class>
    <member kind="function">
      <type>bool</type>
      <name>is_bare_word</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gae6fadac6a2f91d7b8e27b3eb6ad647e</anchor>
      <arglist>(const char *str)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>quote_unless_bare_word</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g4731f21b7b43332b5c7b5bc63c6d67e6</anchor>
      <arglist>(const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>escape_str</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g84a8b196e2ec651f0fd039c70c7ff6cb</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>escape_str</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gd4b0b4fae7b93db7d704a5fef6e021c6</anchor>
      <arglist>(const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>version</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g85c83eb1d18703782d129dbe4a518fca</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>hash_funcs</name>
    <title>Hashing functions</title>
    <filename>group__hash__funcs.html</filename>
    <class kind="struct">spot::ltl::formula_ptr_hash</class>
    <class kind="struct">spot::ptr_hash</class>
    <class kind="struct">spot::string_hash</class>
    <class kind="struct">spot::state_ptr_hash</class>
    <member kind="function">
      <type>size_t</type>
      <name>wang32_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>g9422ff0c16df957910dd4a0275d9f726</anchor>
      <arglist>(size_t key)</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>knuth32_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>gea94dbea4a286b0bde253baf07e7a56e</anchor>
      <arglist>(size_t key)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>random</name>
    <title>Random functions</title>
    <filename>group__random.html</filename>
    <class kind="class">spot::barand</class>
    <member kind="function">
      <type>void</type>
      <name>srand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g539812ab355a561fee1ecbfe60b276e4</anchor>
      <arglist>(unsigned int seed)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>rrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g86ce713fe60de9af440a7331de74aa1a</anchor>
      <arglist>(int min, int max)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>mrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>gfe8da996f40caa7d188f6b408ae62904</anchor>
      <arglist>(int max)</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>drand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g42801cd81368df5c1c61aa1626e299ff</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>nrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g978e1c6fb8f447274a05ddd3f87ce58b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>bmrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g0130217a19e5156f796ab774dca4b08c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>prand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g816b558a7c64daca3a2a018704377dac</anchor>
      <arglist>(double p)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_essentials</name>
    <title>Essential TGBA types</title>
    <filename>group__tgba__essentials.html</filename>
    <class kind="class">spot::bdd_dict</class>
    <class kind="class">spot::state</class>
    <class kind="struct">spot::state_ptr_less_than</class>
    <class kind="struct">spot::state_ptr_equal</class>
    <class kind="struct">spot::state_ptr_hash</class>
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
    <class kind="class">spot::tgba_explicit</class>
    <class kind="class">spot::state_explicit</class>
    <class kind="class">spot::tgba_explicit_succ_iterator</class>
    <class kind="class">spot::tgba_reduc</class>
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
      <type>tgba_bdd_concrete *</type>
      <name>product</name>
      <anchorfile>group__tgba__algorithms.html</anchorfile>
      <anchor>g9a8f2973e4358cf18aa162634b61ab51</anchor>
      <arglist>(const tgba_bdd_concrete *left, const tgba_bdd_concrete *right)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_on_the_fly_algorithms</name>
    <title>TGBA on-the-fly algorithms</title>
    <filename>group__tgba__on__the__fly__algorithms.html</filename>
    <class kind="class">spot::state_product</class>
    <class kind="class">spot::tgba_tba_proxy</class>
    <class kind="class">spot::tgba_sba_proxy</class>
  </compound>
  <compound kind="group">
    <name>tgba_io</name>
    <title>Input/Output of TGBA</title>
    <filename>group__tgba__io.html</filename>
    <subgroup>tgba_dotty</subgroup>
    <member kind="typedef">
      <type>std::pair&lt; tgbayy::location, std::string &gt;</type>
      <name>tgba_parse_error</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gdcc2cd9b328a5c3f63918c577f86f18c</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; tgba_parse_error &gt;</type>
      <name>tgba_parse_error_list</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g76b22bb081d5c36378098caa5bf58081</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dotty_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g07d47453e3bab574bf4b09589a18dcf9</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g, dotty_decorator *dd=dotty_decorator::instance())</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>lbtt_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g955eb1141519477fda8d09fae2a9cb4a</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>never_claim_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gc20ccafb63e86ac18a638cbfda5c0eab</anchor>
      <arglist>(std::ostream &amp;os, const tgba_sba_proxy *g, const ltl::formula *f=0)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>tgba_save_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g5c7f56a5941eb2380676c4ff6706e1c2</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g)</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_parse</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g4548696ddb1001650da2d5fd9b0f77bd</anchor>
      <arglist>(const std::string &amp;filename, tgba_parse_error_list &amp;error_list, bdd_dict *dict, ltl::environment &amp;env=ltl::default_environment::instance(), ltl::environment &amp;envacc=ltl::default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>format_tgba_parse_errors</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g95b48c3bd896478c7e22b0d49c39d89c</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;filename, tgba_parse_error_list &amp;error_list)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_ltl</name>
    <title>Translating LTL formulae into TGBA</title>
    <filename>group__tgba__ltl.html</filename>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>ltl_to_tgba_fm</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>g749653f0f15ebc9d56e6978314f50421</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict, bool exprop=false, bool symb_merge=true, bool branching_postponement=false, bool fair_loop_approx=false, const ltl::atomic_prop_set *unobs=0, int reduce_ltl=ltl::Reduce_None, bool containment_checks=false)</arglist>
    </member>
    <member kind="function">
      <type>tgba_bdd_concrete *</type>
      <name>ltl_to_tgba_lacim</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>g911db84b8e05185bb50b5eda55efe6b6</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_generic</name>
    <title>Algorithm patterns</title>
    <filename>group__tgba__generic.html</filename>
    <class kind="class">spot::tgba_reachable_iterator</class>
    <class kind="class">spot::tgba_reachable_iterator_depth_first</class>
    <class kind="class">spot::tgba_reachable_iterator_breadth_first</class>
  </compound>
  <compound kind="group">
    <name>tgba_reduction</name>
    <title>TGBA simplifications</title>
    <filename>group__tgba__reduction.html</filename>
    <class kind="class">spot::parity_game_graph</class>
    <class kind="class">spot::spoiler_node</class>
    <class kind="class">spot::duplicator_node</class>
    <class kind="class">spot::parity_game_graph_direct</class>
    <class kind="class">spot::spoiler_node_delayed</class>
    <class kind="class">spot::duplicator_node_delayed</class>
    <class kind="class">spot::parity_game_graph_delayed</class>
    <member kind="typedef">
      <type>std::vector&lt; spoiler_node * &gt;</type>
      <name>sn_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>ga807b9e3dabba60063c3dff70244c79a</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; duplicator_node * &gt;</type>
      <name>dn_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g57d3005aa0c7d42803556c39d62d2995</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; const state * &gt;</type>
      <name>s_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g1aecc57153bbe4d48c62d7dcedde5afb</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumeration">
      <name>reduce_tgba_options</name>
      <anchor>g5bd08ab74b3ab10a27beceaa04d9217a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_None</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a28ab8e63b3f476424eec1d49fb19b1e6</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_quotient_Dir_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a6ef94353974a1119b8c3265eb0bcbe42</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_transition_Dir_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a7b2d15a717f241527e25e1266370033e</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_quotient_Del_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a040be3fac90f0ddb426644a52418d7a3</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_transition_Del_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a21c74a793193bfe95e63b43ef1b59350</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Scc</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a685daa8530de270e1588cdc2c178a2ab</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_All</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a81a405e18e9bbd601c4183e1c7ff49e1</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>reduc_tgba_sim</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g3e05060db57bf0e735f08d057db3fae2</anchor>
      <arglist>(const tgba *a, int opt=Reduce_All)</arglist>
    </member>
    <member kind="function">
      <type>direct_simulation_relation *</type>
      <name>get_direct_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g031fe9f92e0d1dbe9af8ca695b2f1161</anchor>
      <arglist>(const tgba *a, std::ostream &amp;os, int opt=-1)</arglist>
    </member>
    <member kind="function">
      <type>delayed_simulation_relation *</type>
      <name>get_delayed_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gb80a323c2a5aa8bbf1541afa8a3036a9</anchor>
      <arglist>(const tgba *a, std::ostream &amp;os, int opt=-1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>free_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gb2aceabca87cc15e9aa79abbcc23358e</anchor>
      <arglist>(direct_simulation_relation *rel)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>free_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g9ef87dd9bf31b79cfaa19431a019b98a</anchor>
      <arglist>(delayed_simulation_relation *rel)</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_misc</name>
    <title>Miscellaneous algorithms on TGBA</title>
    <filename>group__tgba__misc.html</filename>
    <class kind="class">spot::bfs_steps</class>
    <class kind="struct">spot::tgba_statistics</class>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_dupexp_bfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gee64e1fa586af205fa41aaf2c0dbb4bc</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_dupexp_dfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>g2d17e428249fb0e43ce9ae1ee0e4ddf8</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_powerset</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>g261dcd2ce23378bebecc939b72889e08</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>random_graph</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gdcf8ba83bd92b5cbf8f5b8f1083a793b</anchor>
      <arglist>(int n, float d, const ltl::atomic_prop_set *ap, bdd_dict *dict, int n_acc=0, float a=0.1, float t=0.5, ltl::environment *env=&amp;ltl::default_environment::instance())</arglist>
    </member>
    <member kind="function">
      <type>tgba_statistics</type>
      <name>stats_reachable</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>geeb949ca5fa52c1e292e0a7732ef46c4</anchor>
      <arglist>(const tgba *g)</arglist>
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
      <type>emptiness_check *</type>
      <name>couvreur99</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g9bb3670ecff03df6d792c8a315c3e75c</anchor>
      <arglist>(const tgba *a, option_map options=option_map(), const numbered_state_heap_factory *nshf=numbered_state_heap_hash_map_factory::instance())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_gv04_check</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ge45e3a4c2ba4b8c0609a2afca67eabe8</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g392d772bf851002cdda0ca34615aa54b</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>bit_state_hashing_magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>gdae7938a96420813bcdabb1b31295294</anchor>
      <arglist>(const tgba *a, size_t size, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g054c1948b6c5076350e44a85ad580403</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_se05_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g8c176368673a0c009dd3e934d57fb492</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>bit_state_hashing_se05_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ge822266082cdb9772ce31388a6538cb9</anchor>
      <arglist>(const tgba *a, size_t size, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>se05</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g23a00d19bf7613222e6e41833e515c00</anchor>
      <arglist>(const tgba *a, option_map o)</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_tau03_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g159e81f9b91f99b2a749185f47924d99</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_tau03_opt_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g68f7245733f5fe5a86e6dd416d7746f1</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
  </compound>
  <compound kind="group">
    <name>tgba_run</name>
    <title>TGBA runs and supporting functions</title>
    <filename>group__tgba__run.html</filename>
    <class kind="struct">spot::tgba_run</class>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>print_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g3aa3b6dd9854c94b6aaabafd4a612de2</anchor>
      <arglist>(std::ostream &amp;os, const tgba *a, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>tgba_run_to_tgba</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g60bd6401d35428aa22bda3388765099b</anchor>
      <arglist>(const tgba *a, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>tgba_run *</type>
      <name>project_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g1e0b8aeb36e622ac26a6c540ac17fddc</anchor>
      <arglist>(const tgba *a_run, const tgba *a_proj, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>tgba_run *</type>
      <name>reduce_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>gbc4ad10830cd80a3237415d28adeee1b</anchor>
      <arglist>(const tgba *a, const tgba_run *org)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>replay_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>ge2bf747c24d2fb1a06f1a033f67fe6dc</anchor>
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
  <compound kind="namespace">
    <name>ltlyy</name>
    <filename>namespaceltlyy.html</filename>
    <class kind="class">ltlyy::location</class>
    <class kind="class">ltlyy::position</class>
    <class kind="class">ltlyy::stack</class>
    <class kind="class">ltlyy::slice</class>
    <member kind="function">
      <type>const location</type>
      <name>operator+</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>70e347efcc4af1489a902f013c075795</anchor>
      <arglist>(const location &amp;begin, const location &amp;end)</arglist>
    </member>
    <member kind="function">
      <type>const location</type>
      <name>operator+</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>5ad9aab98f3f4a4d03e28d26b885f0a9</anchor>
      <arglist>(const location &amp;begin, unsigned int width)</arglist>
    </member>
    <member kind="function">
      <type>location &amp;</type>
      <name>operator+=</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>e2f8bb8edf11642db0e6459bc4f2f2a5</anchor>
      <arglist>(location &amp;res, unsigned int width)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>7ca655af533c8a7eb435a85f9a1cd234</anchor>
      <arglist>(std::ostream &amp;ostr, const location &amp;loc)</arglist>
    </member>
    <member kind="function">
      <type>const position &amp;</type>
      <name>operator+=</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>d4b9d0fc18d630a816d0d5879569a400</anchor>
      <arglist>(position &amp;res, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position</type>
      <name>operator+</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>cbba141d458e781c3e884545fff8726f</anchor>
      <arglist>(const position &amp;begin, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position &amp;</type>
      <name>operator-=</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>f3f149ab4d359d9539af7f886620d4ac</anchor>
      <arglist>(position &amp;res, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position</type>
      <name>operator-</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>e322d7f17d8337de215434430d4b5039</anchor>
      <arglist>(const position &amp;begin, const int width)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespaceltlyy.html</anchorfile>
      <anchor>0dd64d7f5203ae05d3a671f4dc66511b</anchor>
      <arglist>(std::ostream &amp;ostr, const position &amp;pos)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>ltlyy::location</name>
    <filename>classltlyy_1_1location.html</filename>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>e2ff12ab024484a2d1245bf657290142</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>8f1b859b38fe5cb33d306beb98ec04bb</anchor>
      <arglist>(std::string *fn)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>step</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>0cb10e032ede2952ebff291bf11b3654</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>df146e08e5fffd4dab9862c0f6a310bc</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>8fcb821f469792c4e0de5fd5049e7f6d</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>begin</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>add64beae4835b78c359f97683155317</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>end</name>
      <anchorfile>classltlyy_1_1location.html</anchorfile>
      <anchor>6750a25f8b82b02801327952ca942bc5</anchor>
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
      <anchor>846eecdf8d903355f4ae3e544ca88998</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>3cfe2972548145ce7b025da0a6a01220</anchor>
      <arglist>(std::string *fn)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>ad9d63b47c96c61f74fa22c1866fa28f</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>a97041344fc45ab8f01c0dd625a36e80</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>std::string *</type>
      <name>filename</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>aaafaf5e29c0851b6e040c800dc8a906</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>line</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>91e1bd88aad032f4a1e1dafcab06f4c5</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>column</name>
      <anchorfile>classltlyy_1_1position.html</anchorfile>
      <anchor>4e34ca78e4f8d15cef154cb98f1d28dd</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>ltlyy::stack</name>
    <filename>classltlyy_1_1stack.html</filename>
    <templarg>T</templarg>
    <templarg>S</templarg>
    <member kind="typedef">
      <type>S::reverse_iterator</type>
      <name>iterator</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>94a04e80e72696ab3f2047aa28386a5f</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>S::const_reverse_iterator</type>
      <name>const_iterator</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>239eb8ed9ae3418507948ea22bf4b17d</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>stack</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>4ba9a1280192f8fa71d596359af81bb5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>stack</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>4828adafbe0411fe1c364ef3fe6b6cc2</anchor>
      <arglist>(unsigned int n)</arglist>
    </member>
    <member kind="function">
      <type>T &amp;</type>
      <name>operator[]</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>5043f71bbda8168cf6c25132539f1cdd</anchor>
      <arglist>(unsigned int i)</arglist>
    </member>
    <member kind="function">
      <type>const T &amp;</type>
      <name>operator[]</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>b9decfb914e0eb683f2732ecc3305e4b</anchor>
      <arglist>(unsigned int i) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>push</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>a74a3ec1b90a6d5508eb3800aa9994d4</anchor>
      <arglist>(const T &amp;t)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>pop</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>11bfaba9e90dc3b2c65bf9347c9f735f</anchor>
      <arglist>(unsigned int n=1)</arglist>
    </member>
    <member kind="function">
      <type>unsigned int</type>
      <name>height</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>5b6e56adeea6c177476e93b73d9cbe13</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const_iterator</type>
      <name>begin</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>ae35a8e914c56314edefc7ac852a6567</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const_iterator</type>
      <name>end</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>bf3b3c4ea0cd85adaeafed6e13bca833</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>S</type>
      <name>seq_</name>
      <anchorfile>classltlyy_1_1stack.html</anchorfile>
      <anchor>15579b222f8b5378fd138e4e2cd6fc5f</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>ltlyy::slice</name>
    <filename>classltlyy_1_1slice.html</filename>
    <templarg>T</templarg>
    <templarg>S</templarg>
    <member kind="function">
      <type></type>
      <name>slice</name>
      <anchorfile>classltlyy_1_1slice.html</anchorfile>
      <anchor>9ef6761922013aafd4e3495cf769fe3f</anchor>
      <arglist>(const S &amp;stack, unsigned int range)</arglist>
    </member>
    <member kind="function">
      <type>const T &amp;</type>
      <name>operator[]</name>
      <anchorfile>classltlyy_1_1slice.html</anchorfile>
      <anchor>31cfde158f493874ac779850b146ba50</anchor>
      <arglist>(unsigned int i) const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const S &amp;</type>
      <name>stack_</name>
      <anchorfile>classltlyy_1_1slice.html</anchorfile>
      <anchor>ef72daa49c051fed98133d5a25fa5cb7</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned int</type>
      <name>range_</name>
      <anchorfile>classltlyy_1_1slice.html</anchorfile>
      <anchor>3dfbb1d178a9e2b022395f16c6279a11</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="namespace">
    <name>sautyy</name>
    <filename>namespacesautyy.html</filename>
    <class kind="class">sautyy::location</class>
    <class kind="class">sautyy::position</class>
    <class kind="class">sautyy::stack</class>
    <class kind="class">sautyy::slice</class>
    <member kind="function">
      <type>const location</type>
      <name>operator+</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>ede8694104898a79760c3a3e7feef742</anchor>
      <arglist>(const location &amp;begin, const location &amp;end)</arglist>
    </member>
    <member kind="function">
      <type>const location</type>
      <name>operator+</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>bd47fd459e7ff01bbcf78ddf011a4270</anchor>
      <arglist>(const location &amp;begin, unsigned int width)</arglist>
    </member>
    <member kind="function">
      <type>location &amp;</type>
      <name>operator+=</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>797709a44e4953c4491527df2466760f</anchor>
      <arglist>(location &amp;res, unsigned int width)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>3a441520483d3e701cc2a578cca742c1</anchor>
      <arglist>(std::ostream &amp;ostr, const location &amp;loc)</arglist>
    </member>
    <member kind="function">
      <type>const position &amp;</type>
      <name>operator+=</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>9582da2fc987ee661e87cb56b8a0f7d9</anchor>
      <arglist>(position &amp;res, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position</type>
      <name>operator+</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>e6bc5e4aeccb2a840854b726928b448f</anchor>
      <arglist>(const position &amp;begin, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position &amp;</type>
      <name>operator-=</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>87545a05c5946f819073c186d2ea05c7</anchor>
      <arglist>(position &amp;res, const int width)</arglist>
    </member>
    <member kind="function">
      <type>const position</type>
      <name>operator-</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>e0cbc2c21416807379de671135c7bd5a</anchor>
      <arglist>(const position &amp;begin, const int width)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespacesautyy.html</anchorfile>
      <anchor>b4e5b909a69338a5fc382290bc4152bf</anchor>
      <arglist>(std::ostream &amp;ostr, const position &amp;pos)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>sautyy::location</name>
    <filename>classsautyy_1_1location.html</filename>
    <member kind="function">
      <type></type>
      <name>location</name>
      <anchorfile>classsautyy_1_1location.html</anchorfile>
      <anchor>2a44f82cdfb7cad2407a902200dcab96</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classsautyy_1_1location.html</anchorfile>
      <anchor>b132ed4fb81caa54031cf4ee3275ab0d</anchor>
      <arglist>(std::string *fn)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>step</name>
      <anchorfile>classsautyy_1_1location.html</anchorfile>
      <anchor>f214609c27cf365020206b18eb1455ef</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classsautyy_1_1location.html</anchorfile>
      <anchor>f377952a676fdfbc30108a0b66e6c5d4</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classsautyy_1_1location.html</anchorfile>
      <anchor>7a37f5203c6546b3ca374722477726a2</anchor>
      <arglist>(unsigned int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>begin</name>
      <anchorfile>classsautyy_1_1location.html</anchorfile>
      <anchor>fc6ebf5d1f67b1800ba906352bb0a3b2</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>position</type>
      <name>end</name>
      <anchorfile>classsautyy_1_1location.html</anchorfile>
      <anchor>222a2efb0a28809c0501554d5f90c847</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>sautyy::position</name>
    <filename>classsautyy_1_1position.html</filename>
    <member kind="function">
      <type></type>
      <name>position</name>
      <anchorfile>classsautyy_1_1position.html</anchorfile>
      <anchor>50efea82849c306cc884be550f48c713</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>initialize</name>
      <anchorfile>classsautyy_1_1position.html</anchorfile>
      <anchor>9ad9fff58613c45559edd866966123b5</anchor>
      <arglist>(std::string *fn)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>lines</name>
      <anchorfile>classsautyy_1_1position.html</anchorfile>
      <anchor>548783e0200c9e1a81d3f8e9173ed443</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>columns</name>
      <anchorfile>classsautyy_1_1position.html</anchorfile>
      <anchor>104b7d1b5cae78b3049afd36069fb90d</anchor>
      <arglist>(int count=1)</arglist>
    </member>
    <member kind="variable">
      <type>std::string *</type>
      <name>filename</name>
      <anchorfile>classsautyy_1_1position.html</anchorfile>
      <anchor>e82a428e02e108c95ef150ab71eb04dd</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>line</name>
      <anchorfile>classsautyy_1_1position.html</anchorfile>
      <anchor>5bdefa035075f5eba3363483786f0b64</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned int</type>
      <name>column</name>
      <anchorfile>classsautyy_1_1position.html</anchorfile>
      <anchor>a020e378138f0355778778eb9fcc2e84</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>sautyy::stack</name>
    <filename>classsautyy_1_1stack.html</filename>
    <templarg>T</templarg>
    <templarg>S</templarg>
    <member kind="typedef">
      <type>S::reverse_iterator</type>
      <name>iterator</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>66e9c92f709234e31e797a3c4a73b926</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>S::const_reverse_iterator</type>
      <name>const_iterator</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>813f2b6e285398d4dc45141a21131029</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>stack</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>1d691dc11bead8e20a529913dcb74ece</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>stack</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>b3cafc6a69eb7f0deded26dda698b98c</anchor>
      <arglist>(unsigned int n)</arglist>
    </member>
    <member kind="function">
      <type>T &amp;</type>
      <name>operator[]</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>3cbc6d7c672033686079b26219022b0f</anchor>
      <arglist>(unsigned int i)</arglist>
    </member>
    <member kind="function">
      <type>const T &amp;</type>
      <name>operator[]</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>2db82a1d91d81c16bcc82173e77ede15</anchor>
      <arglist>(unsigned int i) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>push</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>c6a233b3e236e03f2c004c1793061a51</anchor>
      <arglist>(const T &amp;t)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>pop</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>44a9c0efc0cebacd5dfeb6462776bb13</anchor>
      <arglist>(unsigned int n=1)</arglist>
    </member>
    <member kind="function">
      <type>unsigned int</type>
      <name>height</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>8335d4194e34e562be5366c5fd85cb5c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const_iterator</type>
      <name>begin</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>5325de2449d23cce25464ef70248bb74</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const_iterator</type>
      <name>end</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>8a6400675ea97da0c65241663fb4c83e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>S</type>
      <name>seq_</name>
      <anchorfile>classsautyy_1_1stack.html</anchorfile>
      <anchor>e9d6defd99f5f3c1a95b191ef2e12685</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>sautyy::slice</name>
    <filename>classsautyy_1_1slice.html</filename>
    <templarg>T</templarg>
    <templarg>S</templarg>
    <member kind="function">
      <type></type>
      <name>slice</name>
      <anchorfile>classsautyy_1_1slice.html</anchorfile>
      <anchor>e588fb834d8626d31a171003f32a5876</anchor>
      <arglist>(const S &amp;stack, unsigned int range)</arglist>
    </member>
    <member kind="function">
      <type>const T &amp;</type>
      <name>operator[]</name>
      <anchorfile>classsautyy_1_1slice.html</anchorfile>
      <anchor>c9fe2bd20f2348647cf19c5413c133e3</anchor>
      <arglist>(unsigned int i) const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const S &amp;</type>
      <name>stack_</name>
      <anchorfile>classsautyy_1_1slice.html</anchorfile>
      <anchor>8bcacc7c932d9cbd086454735b854d33</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned int</type>
      <name>range_</name>
      <anchorfile>classsautyy_1_1slice.html</anchorfile>
      <anchor>ba81fe7d0d67673bd1d15ed46c4344f2</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="namespace">
    <name>spot</name>
    <filename>namespacespot.html</filename>
    <class kind="class">spot::evtgba</class>
    <class kind="class">spot::evtgba_iterator</class>
    <class kind="class">spot::evtgba_explicit</class>
    <class kind="class">spot::state_evtgba_explicit</class>
    <class kind="class">spot::evtgba_product</class>
    <class kind="class">spot::symbol</class>
    <class kind="class">spot::rsymbol</class>
    <class kind="class">spot::evtgba_reachable_iterator</class>
    <class kind="class">spot::evtgba_reachable_iterator_depth_first</class>
    <class kind="class">spot::evtgba_reachable_iterator_breadth_first</class>
    <class kind="class">spot::bdd_allocator</class>
    <class kind="struct">spot::bdd_less_than</class>
    <class kind="class">spot::free_list</class>
    <class kind="struct">spot::ptr_hash</class>
    <class kind="struct">spot::string_hash</class>
    <class kind="struct">spot::char_ptr_less_than</class>
    <class kind="class">spot::minato_isop</class>
    <class kind="class">spot::loopless_modular_mixed_radix_gray_code</class>
    <class kind="class">spot::option_map</class>
    <class kind="class">spot::barand</class>
    <class kind="struct">spot::time_info</class>
    <class kind="class">spot::timer</class>
    <class kind="class">spot::timer_map</class>
    <class kind="class">spot::bdd_dict</class>
    <class kind="class">spot::state</class>
    <class kind="struct">spot::state_ptr_less_than</class>
    <class kind="struct">spot::state_ptr_equal</class>
    <class kind="struct">spot::state_ptr_hash</class>
    <class kind="class">spot::state_bdd</class>
    <class kind="class">spot::tgba_succ_iterator</class>
    <class kind="class">spot::tgba_succ_iterator_concrete</class>
    <class kind="class">spot::tgba</class>
    <class kind="class">spot::tgba_bdd_concrete</class>
    <class kind="class">spot::tgba_bdd_concrete_factory</class>
    <class kind="struct">spot::tgba_bdd_core_data</class>
    <class kind="class">spot::tgba_bdd_factory</class>
    <class kind="class">spot::tgba_explicit</class>
    <class kind="class">spot::state_explicit</class>
    <class kind="class">spot::tgba_explicit_succ_iterator</class>
    <class kind="class">spot::state_product</class>
    <class kind="class">spot::tgba_succ_iterator_product</class>
    <class kind="class">spot::tgba_product</class>
    <class kind="class">spot::direct_simulation_relation</class>
    <class kind="class">spot::delayed_simulation_relation</class>
    <class kind="class">spot::tgba_reduc</class>
    <class kind="class">spot::tgba_tba_proxy</class>
    <class kind="class">spot::tgba_sba_proxy</class>
    <class kind="class">spot::bfs_steps</class>
    <class kind="class">spot::dotty_decorator</class>
    <class kind="class">spot::emptiness_check_result</class>
    <class kind="class">spot::emptiness_check</class>
    <class kind="class">spot::emptiness_check_instantiator</class>
    <class kind="struct">spot::tgba_run</class>
    <class kind="struct">spot::unsigned_statistics</class>
    <class kind="class">spot::unsigned_statistics_copy</class>
    <class kind="class">spot::ec_statistics</class>
    <class kind="class">spot::ars_statistics</class>
    <class kind="class">spot::acss_statistics</class>
    <class kind="class">spot::couvreur99_check_result</class>
    <class kind="class">spot::explicit_connected_component</class>
    <class kind="class">spot::connected_component_hash_set</class>
    <class kind="class">spot::explicit_connected_component_factory</class>
    <class kind="class">spot::connected_component_hash_set_factory</class>
    <class kind="class">spot::couvreur99_check</class>
    <class kind="class">spot::couvreur99_check_shy</class>
    <class kind="class">spot::numbered_state_heap_const_iterator</class>
    <class kind="class">spot::numbered_state_heap</class>
    <class kind="class">spot::numbered_state_heap_factory</class>
    <class kind="class">spot::numbered_state_heap_hash_map</class>
    <class kind="class">spot::numbered_state_heap_hash_map_factory</class>
    <class kind="class">spot::scc_stack</class>
    <class kind="class">spot::couvreur99_check_status</class>
    <class kind="class">spot::tgba_reachable_iterator</class>
    <class kind="class">spot::tgba_reachable_iterator_depth_first</class>
    <class kind="class">spot::tgba_reachable_iterator_breadth_first</class>
    <class kind="class">spot::parity_game_graph</class>
    <class kind="class">spot::spoiler_node</class>
    <class kind="class">spot::duplicator_node</class>
    <class kind="class">spot::parity_game_graph_direct</class>
    <class kind="class">spot::spoiler_node_delayed</class>
    <class kind="class">spot::duplicator_node_delayed</class>
    <class kind="class">spot::parity_game_graph_delayed</class>
    <class kind="class">spot::tgba_run_dotty_decorator</class>
    <class kind="struct">spot::tgba_statistics</class>
    <class kind="class">spot::weight</class>
    <class kind="class">spot::gspn_exception</class>
    <class kind="class">spot::gspn_interface</class>
    <class kind="class">spot::gspn_ssp_interface</class>
    <namespace>spot::ltl</namespace>
    <member kind="typedef">
      <type>std::set&lt; const symbol * &gt;</type>
      <name>symbol_set</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>b1fe964166144356a185c98ce0bcbde6</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::set&lt; rsymbol &gt;</type>
      <name>rsymbol_set</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>5f139f442de158fbe3bf9fab3ddee7f3</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; evtgbayy::location, std::string &gt;</type>
      <name>evtgba_parse_error</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>d049f28e03c2ebe740e5597034fd5c93</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; evtgba_parse_error &gt;</type>
      <name>evtgba_parse_error_list</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>90dc0e2318bb80c45893922f42ec44fe</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; const spot::state *, const spot::state * &gt;</type>
      <name>state_couple</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>335079e354907be71c014b01c2fb1573</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; state_couple * &gt;</type>
      <name>simulation_relation</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>9ec17bbfe207de0e9c4e699ee3f02572</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; spoiler_node * &gt;</type>
      <name>sn_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>ga807b9e3dabba60063c3dff70244c79a</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; duplicator_node * &gt;</type>
      <name>dn_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g57d3005aa0c7d42803556c39d62d2995</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; const state * &gt;</type>
      <name>s_v</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g1aecc57153bbe4d48c62d7dcedde5afb</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; tgbayy::location, std::string &gt;</type>
      <name>tgba_parse_error</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gdcc2cd9b328a5c3f63918c577f86f18c</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; tgba_parse_error &gt;</type>
      <name>tgba_parse_error_list</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g76b22bb081d5c36378098caa5bf58081</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumeration">
      <name>reduce_tgba_options</name>
      <anchor>g5bd08ab74b3ab10a27beceaa04d9217a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_None</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a28ab8e63b3f476424eec1d49fb19b1e6</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_quotient_Dir_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a6ef94353974a1119b8c3265eb0bcbe42</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_transition_Dir_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a7b2d15a717f241527e25e1266370033e</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_quotient_Del_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a040be3fac90f0ddb426644a52418d7a3</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_transition_Del_Sim</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a21c74a793193bfe95e63b43ef1b59350</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Scc</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a685daa8530de270e1588cdc2c178a2ab</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_All</name>
      <anchor>gg5bd08ab74b3ab10a27beceaa04d9217a81a405e18e9bbd601c4183e1c7ff49e1</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dotty_reachable</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>b3ff98345b771b22e0a9b67063676e84</anchor>
      <arglist>(std::ostream &amp;os, const evtgba *g)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>evtgba_save_reachable</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>ade1f7e5b6c685b5393d8a6d3ab7e09d</anchor>
      <arglist>(std::ostream &amp;os, const evtgba *g)</arglist>
    </member>
    <member kind="function">
      <type>evtgba_explicit *</type>
      <name>tgba_to_evtgba</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>3b22a05fb3e16d693b2d80425dfa4500</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>evtgba_explicit *</type>
      <name>evtgba_parse</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>6805befaee0df3f7cb4427d30782f230</anchor>
      <arglist>(const std::string &amp;filename, evtgba_parse_error_list &amp;error_list, bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>format_evtgba_parse_errors</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>805ae4b750b93c1d2e19d81faa609065</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;filename, evtgba_parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_bare_word</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gae6fadac6a2f91d7b8e27b3eb6ad647e</anchor>
      <arglist>(const char *str)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>quote_unless_bare_word</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g4731f21b7b43332b5c7b5bc63c6d67e6</anchor>
      <arglist>(const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>escape_str</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g84a8b196e2ec651f0fd039c70c7ff6cb</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>escape_str</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>gd4b0b4fae7b93db7d704a5fef6e021c6</anchor>
      <arglist>(const std::string &amp;str)</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>wang32_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>g9422ff0c16df957910dd4a0275d9f726</anchor>
      <arglist>(size_t key)</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>knuth32_hash</name>
      <anchorfile>group__hash__funcs.html</anchorfile>
      <anchor>gea94dbea4a286b0bde253baf07e7a56e</anchor>
      <arglist>(size_t key)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>memusage</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>1a8d0610b61c0a30aad16791b0b73d15</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>srand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g539812ab355a561fee1ecbfe60b276e4</anchor>
      <arglist>(unsigned int seed)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>rrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g86ce713fe60de9af440a7331de74aa1a</anchor>
      <arglist>(int min, int max)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>mrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>gfe8da996f40caa7d188f6b408ae62904</anchor>
      <arglist>(int max)</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>drand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g42801cd81368df5c1c61aa1626e299ff</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>nrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g978e1c6fb8f447274a05ddd3f87ce58b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>double</type>
      <name>bmrand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g0130217a19e5156f796ab774dca4b08c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>prand</name>
      <anchorfile>group__random.html</anchorfile>
      <anchor>g816b558a7c64daca3a2a018704377dac</anchor>
      <arglist>(double p)</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>version</name>
      <anchorfile>group__misc__tools.html</anchorfile>
      <anchor>g85c83eb1d18703782d129dbe4a518fca</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_sat</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>59e91579989d390a3979a5badf302255</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>bdd_format_sat</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>ba342c9d66d191b7ed930e2b02043e7f</anchor>
      <arglist>(const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_acc</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>3b659fe265e2014282d3b25db928794b</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_accset</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>a75a117405f2292f6d80abc2ae930946</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>bdd_format_accset</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>a04818924fa41d2230160b499d95173d</anchor>
      <arglist>(const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_set</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>ca49e9ec778c8a6b38455a68db8d6ece</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>bdd_format_set</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>73d8fa997efd95d08e0d1b9dfa7b7073</anchor>
      <arglist>(const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_formula</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>174c8f965f37e9a26b33c9f76183720f</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>bdd_format_formula</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>b9eea881fdb5d8e629db7102fde452ea</anchor>
      <arglist>(const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_dot</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>3ec151057e33aadb60d2b47ffaa64c24</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>bdd_print_table</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>c8434843d2dcb0d99a027e398b01bafe</anchor>
      <arglist>(std::ostream &amp;os, const bdd_dict *dict, bdd b)</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>formula_to_bdd</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>b6641d7d9896e016376b1c618da92469</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *d, void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>const ltl::formula *</type>
      <name>bdd_to_formula</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>ebfbc06108c79ba74f84a838af4f1772</anchor>
      <arglist>(bdd f, const bdd_dict *d)</arglist>
    </member>
    <member kind="function">
      <type>tgba_bdd_concrete *</type>
      <name>product</name>
      <anchorfile>group__tgba__algorithms.html</anchorfile>
      <anchor>g9a8f2973e4358cf18aa162634b61ab51</anchor>
      <arglist>(const tgba_bdd_concrete *left, const tgba_bdd_concrete *right)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dotty_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g07d47453e3bab574bf4b09589a18dcf9</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g, dotty_decorator *dd=dotty_decorator::instance())</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_dupexp_bfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gee64e1fa586af205fa41aaf2c0dbb4bc</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_dupexp_dfs</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>g2d17e428249fb0e43ce9ae1ee0e4ddf8</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>print_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g3aa3b6dd9854c94b6aaabafd4a612de2</anchor>
      <arglist>(std::ostream &amp;os, const tgba *a, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>tgba_run_to_tgba</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g60bd6401d35428aa22bda3388765099b</anchor>
      <arglist>(const tgba *a, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>couvreur99</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g9bb3670ecff03df6d792c8a315c3e75c</anchor>
      <arglist>(const tgba *a, option_map options=option_map(), const numbered_state_heap_factory *nshf=numbered_state_heap_hash_map_factory::instance())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_gv04_check</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ge45e3a4c2ba4b8c0609a2afca67eabe8</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>lbtt_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g955eb1141519477fda8d09fae2a9cb4a</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g)</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>ltl_to_tgba_fm</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>g749653f0f15ebc9d56e6978314f50421</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict, bool exprop=false, bool symb_merge=true, bool branching_postponement=false, bool fair_loop_approx=false, const ltl::atomic_prop_set *unobs=0, int reduce_ltl=ltl::Reduce_None, bool containment_checks=false)</arglist>
    </member>
    <member kind="function">
      <type>tgba_bdd_concrete *</type>
      <name>ltl_to_tgba_lacim</name>
      <anchorfile>group__tgba__ltl.html</anchorfile>
      <anchor>g911db84b8e05185bb50b5eda55efe6b6</anchor>
      <arglist>(const ltl::formula *f, bdd_dict *dict)</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g392d772bf851002cdda0ca34615aa54b</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>bit_state_hashing_magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>gdae7938a96420813bcdabb1b31295294</anchor>
      <arglist>(const tgba *a, size_t size, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>magic_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g054c1948b6c5076350e44a85ad580403</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>never_claim_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>gc20ccafb63e86ac18a638cbfda5c0eab</anchor>
      <arglist>(std::ostream &amp;os, const tgba_sba_proxy *g, const ltl::formula *f=0)</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_powerset</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>g261dcd2ce23378bebecc939b72889e08</anchor>
      <arglist>(const tgba *aut)</arglist>
    </member>
    <member kind="function">
      <type>tgba_run *</type>
      <name>project_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>g1e0b8aeb36e622ac26a6c540ac17fddc</anchor>
      <arglist>(const tgba *a_run, const tgba *a_proj, const tgba_run *run)</arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>random_graph</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>gdcf8ba83bd92b5cbf8f5b8f1083a793b</anchor>
      <arglist>(int n, float d, const ltl::atomic_prop_set *ap, bdd_dict *dict, int n_acc=0, float a=0.1, float t=0.5, ltl::environment *env=&amp;ltl::default_environment::instance())</arglist>
    </member>
    <member kind="function">
      <type>tgba_run *</type>
      <name>reduce_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>gbc4ad10830cd80a3237415d28adeee1b</anchor>
      <arglist>(const tgba *a, const tgba_run *org)</arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>reduc_tgba_sim</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g3e05060db57bf0e735f08d057db3fae2</anchor>
      <arglist>(const tgba *a, int opt=Reduce_All)</arglist>
    </member>
    <member kind="function">
      <type>direct_simulation_relation *</type>
      <name>get_direct_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g031fe9f92e0d1dbe9af8ca695b2f1161</anchor>
      <arglist>(const tgba *a, std::ostream &amp;os, int opt=-1)</arglist>
    </member>
    <member kind="function">
      <type>delayed_simulation_relation *</type>
      <name>get_delayed_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gb80a323c2a5aa8bbf1541afa8a3036a9</anchor>
      <arglist>(const tgba *a, std::ostream &amp;os, int opt=-1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>free_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>gb2aceabca87cc15e9aa79abbcc23358e</anchor>
      <arglist>(direct_simulation_relation *rel)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>free_relation_simulation</name>
      <anchorfile>group__tgba__reduction.html</anchorfile>
      <anchor>g9ef87dd9bf31b79cfaa19431a019b98a</anchor>
      <arglist>(delayed_simulation_relation *rel)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>replay_tgba_run</name>
      <anchorfile>group__tgba__run.html</anchorfile>
      <anchor>ge2bf747c24d2fb1a06f1a033f67fe6dc</anchor>
      <arglist>(std::ostream &amp;os, const tgba *a, const tgba_run *run, bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>tgba_save_reachable</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g5c7f56a5941eb2380676c4ff6706e1c2</anchor>
      <arglist>(std::ostream &amp;os, const tgba *g)</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_se05_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g8c176368673a0c009dd3e934d57fb492</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>bit_state_hashing_se05_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>ge822266082cdb9772ce31388a6538cb9</anchor>
      <arglist>(const tgba *a, size_t size, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>se05</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g23a00d19bf7613222e6e41833e515c00</anchor>
      <arglist>(const tgba *a, option_map o)</arglist>
    </member>
    <member kind="function">
      <type>tgba_statistics</type>
      <name>stats_reachable</name>
      <anchorfile>group__tgba__misc.html</anchorfile>
      <anchor>geeb949ca5fa52c1e292e0a7732ef46c4</anchor>
      <arglist>(const tgba *g)</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_tau03_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g159e81f9b91f99b2a749185f47924d99</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>emptiness_check *</type>
      <name>explicit_tau03_opt_search</name>
      <anchorfile>group__emptiness__check__algorithms.html</anchorfile>
      <anchor>g68f7245733f5fe5a86e6dd416d7746f1</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function">
      <type>tgba_explicit *</type>
      <name>tgba_parse</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g4548696ddb1001650da2d5fd9b0f77bd</anchor>
      <arglist>(const std::string &amp;filename, tgba_parse_error_list &amp;error_list, bdd_dict *dict, ltl::environment &amp;env=ltl::default_environment::instance(), ltl::environment &amp;envacc=ltl::default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>format_tgba_parse_errors</name>
      <anchorfile>group__tgba__io.html</anchorfile>
      <anchor>g95b48c3bd896478c7e22b0d49c39d89c</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;filename, tgba_parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>namespacespot.html</anchorfile>
      <anchor>83bcadbb0c347d956021471f8c7d2135</anchor>
      <arglist>(std::ostream &amp;os, const gspn_exception &amp;e)</arglist>
    </member>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_semi</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g41573dafc02e8da2283670fa7aefebcf</anchor>
      <arglist>(const tgba *ssp_automata)</arglist>
    </member>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_shy_semi</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g9e84d9fcba32903d98fc0a0ebdc73ac1</anchor>
      <arglist>(const tgba *ssp_automata)</arglist>
    </member>
    <member kind="function">
      <type>couvreur99_check *</type>
      <name>couvreur99_check_ssp_shy</name>
      <anchorfile>group__emptiness__check__ssp.html</anchorfile>
      <anchor>g9dba9cdb805e4187dc072c0ff7ea3d67</anchor>
      <arglist>(const tgba *ssp_automata, bool stack_inclusion=true)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::evtgba</name>
    <filename>classspot_1_1evtgba.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~evtgba</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>abc9dfaaa11ae2817b815e04e6f0ac97</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual evtgba_iterator *</type>
      <name>init_iter</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>ebdabb38d9abd039fb8e382dcd76ec58</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual evtgba_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>14a4572f9e3aa40d66f1d0810a8b47b6</anchor>
      <arglist>(const state *s) const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual evtgba_iterator *</type>
      <name>pred_iter</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>9889d471b780409cf59dc9bb15f220a2</anchor>
      <arglist>(const state *s) const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>390a8ef343218ba9ea7bd35bd0c70675</anchor>
      <arglist>(const state *state) const=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_label</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>1d0748a93153bb953567f4400c97b1f6</anchor>
      <arglist>(const symbol *symbol) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_acceptance_condition</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>6cb3397d6eebf41ab57a52ee62f24f81</anchor>
      <arglist>(const symbol *symbol) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_acceptance_conditions</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>6b122202a13ddc539fe7124a9ec57f4d</anchor>
      <arglist>(const symbol_set &amp;symset) const</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const symbol_set &amp;</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>1d3516fa7847ae3ffeb2b58ce929108e</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const symbol_set &amp;</type>
      <name>alphabet</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>726c38279d9bf85f97c27eb9e0b53a81</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>evtgba</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>4e0cd70bfc949737c24cd5feaa79a639</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::evtgba_iterator</name>
    <filename>classspot_1_1evtgba__iterator.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~evtgba_iterator</name>
      <anchorfile>classspot_1_1evtgba__iterator.html</anchorfile>
      <anchor>9d8c61953e4e360e67656eb5a7f1e855</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1evtgba__iterator.html</anchorfile>
      <anchor>71d31cdc1a9fb301f127381a40eb0fb5</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1evtgba__iterator.html</anchorfile>
      <anchor>7bf70fa00c95a082ab1005a54b660bd9</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1evtgba__iterator.html</anchorfile>
      <anchor>3c68e3a12a951f885d1b80ee07327602</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1evtgba__iterator.html</anchorfile>
      <anchor>223a0ad03f0d1dc226bde14ecb408165</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const symbol *</type>
      <name>current_label</name>
      <anchorfile>classspot_1_1evtgba__iterator.html</anchorfile>
      <anchor>652975cb2abce1bcbe41291100ab9c3c</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual symbol_set</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1evtgba__iterator.html</anchorfile>
      <anchor>9d4249b5e24037204c6bde29c3ce1048</anchor>
      <arglist>() const=0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::evtgba_explicit</name>
    <filename>classspot_1_1evtgba__explicit.html</filename>
    <base>spot::evtgba</base>
    <member kind="typedef">
      <type>std::list&lt; transition * &gt;</type>
      <name>transition_list</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>4677acf05a8457e05eeb2e6cf4da892d</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>evtgba_explicit</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>8ab45da7a566e4bc1a36e7a6929a2b0f</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~evtgba_explicit</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>9e55ed8ffcd48b6a6988e3122fc9b3e7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual evtgba_iterator *</type>
      <name>init_iter</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>bdc3e745df2c54ff67787bb5e7dd3205</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual evtgba_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>2f587aef77ca4ad6c20289ba83ec5a5b</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual evtgba_iterator *</type>
      <name>pred_iter</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>4ed8eeaedc7f99b871abdb99269efa55</anchor>
      <arglist>(const spot::state *s) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>6979a798463f43989e7ee6c1b0552218</anchor>
      <arglist>(const spot::state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const symbol_set &amp;</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>c9ca8756af5a8f6234314111cfc5af84</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const symbol_set &amp;</type>
      <name>alphabet</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>f0cd4babfc97127e94619a0187caa0ee</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>transition *</type>
      <name>add_transition</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>746910840b7172bc47c736699873447e</anchor>
      <arglist>(const std::string &amp;source, const rsymbol &amp;label, rsymbol_set acc, const std::string &amp;dest)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set_init_state</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>5729d795c5a0133f14b0a0a197b8e188</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_acceptance_condition</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>b5c309fa836cebcb0d18cabdbb1ae34f</anchor>
      <arglist>(const rsymbol &amp;acc)</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual evtgba_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>14a4572f9e3aa40d66f1d0810a8b47b6</anchor>
      <arglist>(const state *s) const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual evtgba_iterator *</type>
      <name>pred_iter</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>9889d471b780409cf59dc9bb15f220a2</anchor>
      <arglist>(const state *s) const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>390a8ef343218ba9ea7bd35bd0c70675</anchor>
      <arglist>(const state *state) const=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_label</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>1d0748a93153bb953567f4400c97b1f6</anchor>
      <arglist>(const symbol *symbol) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_acceptance_condition</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>6cb3397d6eebf41ab57a52ee62f24f81</anchor>
      <arglist>(const symbol *symbol) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_acceptance_conditions</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>6b122202a13ddc539fe7124a9ec57f4d</anchor>
      <arglist>(const symbol_set &amp;symset) const</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const std::string, evtgba_explicit::state *, string_hash &gt;</type>
      <name>ns_map</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>a16e2b4b65c45c9475af7a713f538fea</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const evtgba_explicit::state *, std::string, ptr_hash&lt; evtgba_explicit::state &gt; &gt;</type>
      <name>sn_map</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>b97038f0504cdf602c705837ad6f40aa</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>state *</type>
      <name>declare_state</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>2ee2537d11d6a1f6de223779328ac46c</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>ns_map</type>
      <name>name_state_map_</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>372eb937ce1d9b428c936a0753eb9771</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_map</type>
      <name>state_name_map_</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>c71fdf79c51b3bae07c5ba09aa7a97b9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>symbol_set</type>
      <name>acc_set_</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>978b4d26fdfda151c88f9599c15b43c4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>symbol_set</type>
      <name>alphabet_</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>609277b4be1ff425b595630e7cd83c37</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>transition_list</type>
      <name>init_states_</name>
      <anchorfile>classspot_1_1evtgba__explicit.html</anchorfile>
      <anchor>8b0c5f622be35e8bcd8bc2410d834726</anchor>
      <arglist></arglist>
    </member>
    <class kind="struct">spot::evtgba_explicit::state</class>
    <class kind="struct">spot::evtgba_explicit::transition</class>
  </compound>
  <compound kind="struct">
    <name>spot::evtgba_explicit::state</name>
    <filename>structspot_1_1evtgba__explicit_1_1state.html</filename>
    <member kind="variable">
      <type>transition_list</type>
      <name>in</name>
      <anchorfile>structspot_1_1evtgba__explicit_1_1state.html</anchorfile>
      <anchor>bdd128cb3cf9cfa528715693c3eb6e9b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>transition_list</type>
      <name>out</name>
      <anchorfile>structspot_1_1evtgba__explicit_1_1state.html</anchorfile>
      <anchor>f3302ed17eccd35a966d5d6228da6a9d</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::evtgba_explicit::transition</name>
    <filename>structspot_1_1evtgba__explicit_1_1transition.html</filename>
    <member kind="variable">
      <type>const symbol *</type>
      <name>label</name>
      <anchorfile>structspot_1_1evtgba__explicit_1_1transition.html</anchorfile>
      <anchor>fa33a4f90c3ee7ce1e1dea480b7e8ccf</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>symbol_set</type>
      <name>acceptance_conditions</name>
      <anchorfile>structspot_1_1evtgba__explicit_1_1transition.html</anchorfile>
      <anchor>087ed92748df1e44ee858e6ff8fa1617</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>state *</type>
      <name>in</name>
      <anchorfile>structspot_1_1evtgba__explicit_1_1transition.html</anchorfile>
      <anchor>d625abb8593ae2da6f87e79f762c49c4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>state *</type>
      <name>out</name>
      <anchorfile>structspot_1_1evtgba__explicit_1_1transition.html</anchorfile>
      <anchor>ed079ec9410046f9cd280a4bb38fb8c0</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_evtgba_explicit</name>
    <filename>classspot_1_1state__evtgba__explicit.html</filename>
    <base>spot::state</base>
    <member kind="function">
      <type></type>
      <name>state_evtgba_explicit</name>
      <anchorfile>classspot_1_1state__evtgba__explicit.html</anchorfile>
      <anchor>fe4f49c8898b875826c6a152a2f48fd6</anchor>
      <arglist>(const evtgba_explicit::state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__evtgba__explicit.html</anchorfile>
      <anchor>963a44d6eb522e239263d0aa2300202d</anchor>
      <arglist>(const spot::state *other) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__evtgba__explicit.html</anchorfile>
      <anchor>7e74cc51355a85a40c22f351eb511ed8</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_evtgba_explicit *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__evtgba__explicit.html</anchorfile>
      <anchor>49475d32942310fb2714b61a790ee878</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~state_evtgba_explicit</name>
      <anchorfile>classspot_1_1state__evtgba__explicit.html</anchorfile>
      <anchor>226a8f9c05117bd084d8a2678ce25919</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const evtgba_explicit::state *</type>
      <name>get_state</name>
      <anchorfile>classspot_1_1state__evtgba__explicit.html</anchorfile>
      <anchor>6f3bf392b2f330723afbcb9fb5c7a44d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const evtgba_explicit::state *</type>
      <name>state_</name>
      <anchorfile>classspot_1_1state__evtgba__explicit.html</anchorfile>
      <anchor>07a131cd86eba7ca7115585922df3543</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::evtgba_product</name>
    <filename>classspot_1_1evtgba__product.html</filename>
    <base>spot::evtgba</base>
    <member kind="typedef">
      <type>std::vector&lt; const evtgba * &gt;</type>
      <name>evtgba_product_operands</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>2d016d95b60a34136fe3a2d2d54b5d28</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; const symbol *, std::set&lt; int &gt; &gt;</type>
      <name>common_symbol_table</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>f8bcb5296a2a25d167eb04496c70f74d</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>evtgba_product</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>82fceba91a1c73c35734fb74cde201c2</anchor>
      <arglist>(const evtgba_product_operands &amp;op)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~evtgba_product</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>5636576ff86131e7d0a728db150be3f0</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual evtgba_iterator *</type>
      <name>init_iter</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>04e12acf7d8bb337efc2c72b9d28afae</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual evtgba_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>30d7a8fd0096d9756f1344de6e8a3276</anchor>
      <arglist>(const state *s) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual evtgba_iterator *</type>
      <name>pred_iter</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>609b82112f25cb18de0d1b3064920421</anchor>
      <arglist>(const state *s) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>ace6e3ff9e5705a818f9538b07d3ad4b</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const symbol_set &amp;</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>e1b9f8bea0e1a50406e51253716e316a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const symbol_set &amp;</type>
      <name>alphabet</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>b073aeeede97428cacaf6900fce1e2ff</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_label</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>1d0748a93153bb953567f4400c97b1f6</anchor>
      <arglist>(const symbol *symbol) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_acceptance_condition</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>6cb3397d6eebf41ab57a52ee62f24f81</anchor>
      <arglist>(const symbol *symbol) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_acceptance_conditions</name>
      <anchorfile>classspot_1_1evtgba.html</anchorfile>
      <anchor>6b122202a13ddc539fe7124a9ec57f4d</anchor>
      <arglist>(const symbol_set &amp;symset) const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const evtgba_product_operands</type>
      <name>op_</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>52adb5842c2f63b8a8515a73d7ab1fe3</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>symbol_set</type>
      <name>alphabet_</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>a3e031418cc1d0ac65227bb33cc14c05</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>symbol_set</type>
      <name>all_acc_</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>4cf307b3f6a8bec8d2a5790285b1c945</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>common_symbol_table</type>
      <name>common_symbols_</name>
      <anchorfile>classspot_1_1evtgba__product.html</anchorfile>
      <anchor>d4ccc5dc54c974f7b325ed37314acfa1</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::symbol</name>
    <filename>classspot_1_1symbol.html</filename>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>3fbc3870117da7fa167db56149f65feb</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>ref</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>6c8ec7a099479bc2c59d065db598604a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>unref</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>1c794ad8c1e3289c6ea46dc308b69260</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const symbol *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>7cb3a96073a869a871a0adbf6f5c0f9b</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>d14ac6b52b2674d8b1626cedb2869774</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static std::ostream &amp;</type>
      <name>dump_instances</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>05da366065e59d84362d9a895c0a9e0c</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::map&lt; const std::string, const symbol * &gt;</type>
      <name>map</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>c735ed8997cb6ab4e510f8af5af3cf54</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>int</type>
      <name>ref_count_</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>a0c91bffefe50511a2e50acbea354f00</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>symbol</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>85153fd31287ee08b23c52210fb999e0</anchor>
      <arglist>(const std::string *name)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>~symbol</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>68fce5a9b221466d42b68d9e8f3a57d6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected" static="yes">
      <type>static map</type>
      <name>instances_</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>3699255941c0138ee008f00c4c5f5b05</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private">
      <type></type>
      <name>symbol</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>4623a4e645c68b49a8ef8ebc83d6b5c7</anchor>
      <arglist>(const symbol &amp;)</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const std::string *</type>
      <name>name_</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>9b21039ac115179e9662909193a6cc86</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>int</type>
      <name>refs_</name>
      <anchorfile>classspot_1_1symbol.html</anchorfile>
      <anchor>457748ae8181a11bc924c93b7b4a5851</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::rsymbol</name>
    <filename>classspot_1_1rsymbol.html</filename>
    <member kind="function">
      <type></type>
      <name>rsymbol</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>8430c48c75ae91c15960d0861d79d94f</anchor>
      <arglist>(const symbol *s)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>rsymbol</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>4cb4d6289efb89f514c7fcd44c16ca34</anchor>
      <arglist>(const std::string &amp;s)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>rsymbol</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>788f2c486d3ced5c0895fa255efe1bb7</anchor>
      <arglist>(const char *s)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>rsymbol</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>0ee48b6af2c8c0f08945e71928346d59</anchor>
      <arglist>(const rsymbol &amp;rs)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~rsymbol</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>ee84daf797689825550a0b41ae7815e8</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>operator const symbol *</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>cb642e422055f8ee9bdad2eb442f62fb</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const rsymbol &amp;</type>
      <name>operator=</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>4b1d92f941d7a7bbcf50025a1fd80689</anchor>
      <arglist>(const rsymbol &amp;rs)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>operator==</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>4f58fc94badafa304d3dcbd2be2191e5</anchor>
      <arglist>(const rsymbol &amp;rs) const</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>operator!=</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>ac98b8980ef1d27cd030b3d294a6b69a</anchor>
      <arglist>(const rsymbol &amp;rs) const</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>operator&lt;</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>68b0b74cbe578f31b8b45684323eb0c4</anchor>
      <arglist>(const rsymbol &amp;rs) const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const symbol *</type>
      <name>s_</name>
      <anchorfile>classspot_1_1rsymbol.html</anchorfile>
      <anchor>cfce70452a20e9e475eb5611928ac0a3</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::evtgba_reachable_iterator</name>
    <filename>classspot_1_1evtgba__reachable__iterator.html</filename>
    <member kind="function">
      <type></type>
      <name>evtgba_reachable_iterator</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>ad215c9dd64e2ada976034186c84f0b0</anchor>
      <arglist>(const evtgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~evtgba_reachable_iterator</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>b9bed460740f30800c3f48cda4f4d824</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>d0af722c94538a07affe2c965feb342a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>46605998bf6a55f260044447766523d3</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>d9c252df734681b986bbc0f4ea524cd7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>e61c37c9e29f0a79e1e5face6bbcb133</anchor>
      <arglist>(const state *s, int n, evtgba_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>59bce0b0fcaf447647c4f7753a0dba6c</anchor>
      <arglist>(int in, int out, const evtgba_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>fce8d2337bf9df243dee8446de8849eb</anchor>
      <arglist>(const state *s)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>6f180c98c268292a4ea96841e749fa5b</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>f58355525031fe6f97f68944458841c8</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const evtgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>fb33cd58f4836ccdcd1ea208e04b3079</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>f8910bbbb51a3652d8d5cd18116e9412</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::evtgba_reachable_iterator_depth_first</name>
    <filename>classspot_1_1evtgba__reachable__iterator__depth__first.html</filename>
    <base>spot::evtgba_reachable_iterator</base>
    <member kind="function">
      <type></type>
      <name>evtgba_reachable_iterator_depth_first</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>c78a17176417eaa0462b3101cfbbaa20</anchor>
      <arglist>(const evtgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>c9d67ceda8fb9bb84235f5e4c4802872</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>fdfbb44f854c7167015d052255d28951</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>d0af722c94538a07affe2c965feb342a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>46605998bf6a55f260044447766523d3</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>d9c252df734681b986bbc0f4ea524cd7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>e61c37c9e29f0a79e1e5face6bbcb133</anchor>
      <arglist>(const state *s, int n, evtgba_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>59bce0b0fcaf447647c4f7753a0dba6c</anchor>
      <arglist>(int in, int out, const evtgba_iterator *si)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>f58355525031fe6f97f68944458841c8</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::stack&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>4eb7720780d6b5a89869eae06b7d844b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const evtgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>fb33cd58f4836ccdcd1ea208e04b3079</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>f8910bbbb51a3652d8d5cd18116e9412</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::evtgba_reachable_iterator_breadth_first</name>
    <filename>classspot_1_1evtgba__reachable__iterator__breadth__first.html</filename>
    <base>spot::evtgba_reachable_iterator</base>
    <member kind="function">
      <type></type>
      <name>evtgba_reachable_iterator_breadth_first</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>fb9e32466120ed5493a0a82da74d227e</anchor>
      <arglist>(const evtgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>cebc86b514e728ac74fa8bad7385e679</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>fd98c3d35faaaf8c2989119c75bd7bd8</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>d0af722c94538a07affe2c965feb342a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>46605998bf6a55f260044447766523d3</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>d9c252df734681b986bbc0f4ea524cd7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>e61c37c9e29f0a79e1e5face6bbcb133</anchor>
      <arglist>(const state *s, int n, evtgba_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>59bce0b0fcaf447647c4f7753a0dba6c</anchor>
      <arglist>(int in, int out, const evtgba_iterator *si)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>f58355525031fe6f97f68944458841c8</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>70ab1daa5e78f0209719cc1542ef9aed</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const evtgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>fb33cd58f4836ccdcd1ea208e04b3079</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1evtgba__reachable__iterator.html</anchorfile>
      <anchor>f8910bbbb51a3652d8d5cd18116e9412</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::bdd_allocator</name>
    <filename>classspot_1_1bdd__allocator.html</filename>
    <base protection="private">spot::free_list</base>
    <member kind="function">
      <type></type>
      <name>bdd_allocator</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>4b1a522cb8d4223fcdca2c6900741494</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>allocate_variables</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>7b566c8e4046fe012d20e60ed4c058e9</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>release_variables</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>12efeee5e8c5786d814996cab0ec3097</anchor>
      <arglist>(int base, int n)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>initialize</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>1ca5ce94f2ce0de339fc7f59f1d0c2ff</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>lvarnum</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>3736ca5090949c9fb93745544b95daab</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected" static="yes">
      <type>static bool</type>
      <name>initialized</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>3916225a141645c15fa4d53848ef4f5e</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; int, int &gt;</type>
      <name>pos_lenght_pair</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>2772e28510a1ad89342eeeb93a8fb9a4</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::list&lt; pos_lenght_pair &gt;</type>
      <name>free_list_type</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>f7e87c1c112d0e26b44396de9cc78843</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private">
      <type>void</type>
      <name>extvarnum</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>396d46a37f327079e7217da8178579ba</anchor>
      <arglist>(int more)</arglist>
    </member>
    <member kind="function" protection="private" virtualness="virtual">
      <type>virtual int</type>
      <name>extend</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>837f56b5f3d2347d915767b6105101dc</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_n</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>5e2485e960b7a0924c8801c19c7d9b43</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>release_n</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>970f34e2944cb3c6573d605c4d01859a</anchor>
      <arglist>(int base, int n)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump_free_list</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>6545d8275be7fb2efa4291814b4bcc6d</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>0e9d104134e6db9feeb9a5ead1a4fe57</anchor>
      <arglist>(int base, int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>remove</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>b2ee6a3adeb1c8ae1a9bc062f3914349</anchor>
      <arglist>(int base, int n=0)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>eac5a308e26bb6e6058e720da145bf5b</anchor>
      <arglist>(free_list_type::iterator i, int base, int n)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>free_count</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>e193fe59e9484b35a0b00ad7a9cfc40d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>free_list_type</type>
      <name>fl</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>fdaed42e797fc05cfab78c5450838b00</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::bdd_less_than</name>
    <filename>structspot_1_1bdd__less__than.html</filename>
    <member kind="function">
      <type>bool</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1bdd__less__than.html</anchorfile>
      <anchor>e9724ede69d8b3233b30f88969c83370</anchor>
      <arglist>(const bdd &amp;left, const bdd &amp;right) const</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::free_list</name>
    <filename>classspot_1_1free__list.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~free_list</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>ad22b51d38fb4e5ef3553fddcac227fb</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_n</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>5e2485e960b7a0924c8801c19c7d9b43</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>release_n</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>970f34e2944cb3c6573d605c4d01859a</anchor>
      <arglist>(int base, int n)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump_free_list</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>6545d8275be7fb2efa4291814b4bcc6d</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>0e9d104134e6db9feeb9a5ead1a4fe57</anchor>
      <arglist>(int base, int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>remove</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>b2ee6a3adeb1c8ae1a9bc062f3914349</anchor>
      <arglist>(int base, int n=0)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>free_count</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>e193fe59e9484b35a0b00ad7a9cfc40d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; int, int &gt;</type>
      <name>pos_lenght_pair</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>2772e28510a1ad89342eeeb93a8fb9a4</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::list&lt; pos_lenght_pair &gt;</type>
      <name>free_list_type</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>f7e87c1c112d0e26b44396de9cc78843</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual int</type>
      <name>extend</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>f07cab38c8bde1e572c1c0418c9df068</anchor>
      <arglist>(int n)=0</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>eac5a308e26bb6e6058e720da145bf5b</anchor>
      <arglist>(free_list_type::iterator i, int base, int n)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>free_list_type</type>
      <name>fl</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>fdaed42e797fc05cfab78c5450838b00</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ptr_hash</name>
    <filename>structspot_1_1ptr__hash.html</filename>
    <templarg>T</templarg>
    <member kind="function">
      <type>size_t</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1ptr__hash.html</anchorfile>
      <anchor>60e3915ff3d5892a8c7457e93886a2b5</anchor>
      <arglist>(const T *p) const</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::string_hash</name>
    <filename>structspot_1_1string__hash.html</filename>
    <member kind="function">
      <type>size_t</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1string__hash.html</anchorfile>
      <anchor>18e7cd7eeb68050ab4df4c50a8068102</anchor>
      <arglist>(const std::string &amp;s) const </arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::char_ptr_less_than</name>
    <filename>structspot_1_1char__ptr__less__than.html</filename>
    <member kind="function">
      <type>bool</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1char__ptr__less__than.html</anchorfile>
      <anchor>c076d6b1d038d77a0c59b2c3e3162387</anchor>
      <arglist>(const char *left, const char *right) const</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::minato_isop</name>
    <filename>classspot_1_1minato__isop.html</filename>
    <member kind="function">
      <type></type>
      <name>minato_isop</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>219911ee13dbbe288f7ec765bf4eab93</anchor>
      <arglist>(bdd input)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>minato_isop</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>2962f8b4798aad61c41aa2e1124581f2</anchor>
      <arglist>(bdd input, bdd vars)</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>next</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>f3b6eb9c04010f353116a38ed670b7e7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>std::stack&lt; local_vars &gt;</type>
      <name>todo_</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>2c912d2bea55998da6e9752a6257f8ed</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>std::stack&lt; bdd &gt;</type>
      <name>cube_</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>34b35880b289790bc5d09f20c83cb68a</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>ret_</name>
      <anchorfile>classspot_1_1minato__isop.html</anchorfile>
      <anchor>8d5254583eb16ceb3f317eeba5f76202</anchor>
      <arglist></arglist>
    </member>
    <class kind="struct">spot::minato_isop::local_vars</class>
  </compound>
  <compound kind="struct">
    <name>spot::minato_isop::local_vars</name>
    <filename>structspot_1_1minato__isop_1_1local__vars.html</filename>
    <member kind="enumvalue">
      <name>FirstStep</name>
      <anchor>841b3f98a4bb35710f566cce96a148db95e18361d18985760bc7919e01548b2a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>SecondStep</name>
      <anchor>841b3f98a4bb35710f566cce96a148dbb0b480fe983530645b2a451ba2aceb07</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>ThirdStep</name>
      <anchor>841b3f98a4bb35710f566cce96a148dbd5775ec96ccb562a51b7a35f334d2ee9</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>FourthStep</name>
      <anchor>841b3f98a4bb35710f566cce96a148db89a96f6c0cfe2dd4c3f73659f63623be</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>local_vars</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>5b0c1b615df9ff440727989d96107cb6</anchor>
      <arglist>(bdd f_min, bdd f_max, bdd vars)</arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>f_min</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>05bb2eb9cffa94dd0dbfa83b4e3de2f8</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>f_max</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>aa34af86121a8ad985a78f77739be007</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>enum spot::minato_isop::local_vars::@0</type>
      <name>step</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>418ff07b80eb14afeeb24ca4f0841ec0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>vars</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>fd6f727c4e82bc00ce87b6e0cd8fb4c0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>v1</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>1a2e1004ad0ca070b95d9a2add2a89f7</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>f0_min</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>207f3bbf4d05ab88288eb7a1f0576999</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>f0_max</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>50bd324b1bf592da76cd3c777e4659ac</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>f1_min</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>2194c547aad267e834052da779bd21a9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>f1_max</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>441321a81124470f302fe508f979521a</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>g0</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>2cfa20ce2b1e344ae7676c1878ffb0cc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>g1</name>
      <anchorfile>structspot_1_1minato__isop_1_1local__vars.html</anchorfile>
      <anchor>7123d84af84fa88a237c60e4214bff7c</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::loopless_modular_mixed_radix_gray_code</name>
    <filename>classspot_1_1loopless__modular__mixed__radix__gray__code.html</filename>
    <member kind="function">
      <type></type>
      <name>loopless_modular_mixed_radix_gray_code</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>565b6c52e7b37b83bb829095e11c69c9</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~loopless_modular_mixed_radix_gray_code</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>53934ac1621f8bd1c55ef9ccbf9e4a11</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>a_first</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>f1aab585098500e6acb78675967ef530</anchor>
      <arglist>(int j)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>a_next</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>d2299afa2a00ff9cc5f202b9854bdf4d</anchor>
      <arglist>(int j)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>a_last</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>8b6a865cb55084f8a71acb8082213477</anchor>
      <arglist>(int j) const=0</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>first</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>9c918e8524bdeb3e3a80054becbd30df</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>last</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>21b854c63955b060f35b6f25863fe98a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>e1ac398218fc6a462c8ecccb31d6ec96</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>next</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>5ef37d3a269687be631a65769ad209b9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>n_</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>2551f357ca1be741bc5300d06a4c3b63</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>done_</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>01d93ad137b3a2368270ec3779e32eee</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>68e9eea9d2632d66f2ef1042f2d27b68</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int *</type>
      <name>f_</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>6f86e8454e331df5b4aacf5eef3f41ef</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int *</type>
      <name>m_</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>a8342a062c7d6845b4473c0617bb4552</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int *</type>
      <name>s_</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>3dfc0ecaeaf71747a972ce8339d63e50</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int *</type>
      <name>non_one_radixes_</name>
      <anchorfile>classspot_1_1loopless__modular__mixed__radix__gray__code.html</anchorfile>
      <anchor>7c8c0adbecfdb2060d4696cf2bbf799e</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::option_map</name>
    <filename>classspot_1_1option__map.html</filename>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>cb6b25bf19608a1927e5d8daaba72a05</anchor>
      <arglist>(const char *options)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>f1c7bada850db687c712c6cacf5e3829</anchor>
      <arglist>(const char *option, int def=0) const</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>operator[]</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>f9339d9f43b84ef557a1a069cf45f4e1</anchor>
      <arglist>(const char *option) const</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>set</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>d9fb6b43a106db55f9b84b59ed766646</anchor>
      <arglist>(const char *option, int val, int def=0)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>54ca434ab0eec0f87e1b5bcf71f50785</anchor>
      <arglist>(const option_map &amp;o)</arglist>
    </member>
    <member kind="function">
      <type>int &amp;</type>
      <name>operator[]</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>6855a963741c87f0dcb89fe12f98e849</anchor>
      <arglist>(const char *option)</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>std::map&lt; std::string, int &gt;</type>
      <name>options_</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>cffda8c6d21e00ac91edee8b146bd5cb</anchor>
      <arglist></arglist>
    </member>
    <member kind="friend">
      <type>friend std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>classspot_1_1option__map.html</anchorfile>
      <anchor>ff406edae234bc7fe98f6eea26adfbca</anchor>
      <arglist>(std::ostream &amp;os, const option_map &amp;m)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::barand</name>
    <filename>classspot_1_1barand.html</filename>
    <templarg>gen</templarg>
    <member kind="function">
      <type></type>
      <name>barand</name>
      <anchorfile>classspot_1_1barand.html</anchorfile>
      <anchor>df8fb378d8caedf22d3d352c012f46a2</anchor>
      <arglist>(int n, double p)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>rand</name>
      <anchorfile>classspot_1_1barand.html</anchorfile>
      <anchor>f06534dd9405986a06697212ae1fd2fd</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const int</type>
      <name>n_</name>
      <anchorfile>classspot_1_1barand.html</anchorfile>
      <anchor>08495399bb40a9a9d0d3aabb1d6b1eab</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const double</type>
      <name>m_</name>
      <anchorfile>classspot_1_1barand.html</anchorfile>
      <anchor>c42907f5ffd782975fc988339a00a1e6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const double</type>
      <name>s_</name>
      <anchorfile>classspot_1_1barand.html</anchorfile>
      <anchor>7893988ee67877fbc356a4727c1596fd</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::time_info</name>
    <filename>structspot_1_1time__info.html</filename>
    <member kind="function">
      <type></type>
      <name>time_info</name>
      <anchorfile>structspot_1_1time__info.html</anchorfile>
      <anchor>ee80540e8f2bd804b5f34b799cd0531e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable">
      <type>clock_t</type>
      <name>utime</name>
      <anchorfile>structspot_1_1time__info.html</anchorfile>
      <anchor>f709012d05bb4594079496920490f4c8</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>clock_t</type>
      <name>stime</name>
      <anchorfile>structspot_1_1time__info.html</anchorfile>
      <anchor>ed18079ee02fa17955f86ebda19146fb</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::timer</name>
    <filename>classspot_1_1timer.html</filename>
    <member kind="function">
      <type>void</type>
      <name>start</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>55c6674711dab0e67008dc378011e46d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>stop</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>34330e7a7a879a13b2dfb60df84867cd</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>clock_t</type>
      <name>utime</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>b430ec9ff884438199794f4c8daefc78</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>clock_t</type>
      <name>stime</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>14c07958cb4dc783b8e9fa54062aa448</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>time_info</type>
      <name>start_</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>8914fc2c0e6fa6efa8dd35337f9bd444</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>time_info</type>
      <name>total_</name>
      <anchorfile>classspot_1_1timer.html</anchorfile>
      <anchor>47049a64de0ec56cda21b852bf0a2436</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::timer_map</name>
    <filename>classspot_1_1timer__map.html</filename>
    <member kind="function">
      <type>void</type>
      <name>start</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>85d4bf24749d93be6dcbb5e66aacd249</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>stop</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>709679565ddbc19293c6f63138367911</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>cancel</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>b35d7a8e21cc87716039cecd5040856e</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>const spot::timer &amp;</type>
      <name>timer</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>fa1524738c89da19f182551da00e15f1</anchor>
      <arglist>(const std::string &amp;name) const</arglist>
    </member>
    <member kind="function">
      <type>spot::timer &amp;</type>
      <name>timer</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>02d67150c7c115d2f06ae49ef1dadc19</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>empty</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>61d1bb0c98ba02fba2be0f9163353400</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>print</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>5f43809de31b434b087b2b1d8b3deb79</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; spot::timer, int &gt;</type>
      <name>item_type</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>5764ba323cbec13c5d05e3b937712eff</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::map&lt; std::string, item_type &gt;</type>
      <name>tm_type</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>f265166b19b710115f871afb8216d611</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>tm_type</type>
      <name>tm</name>
      <anchorfile>classspot_1_1timer__map.html</anchorfile>
      <anchor>99b0e1440918586eb75c0528bfc63169</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::bdd_dict</name>
    <filename>classspot_1_1bdd__dict.html</filename>
    <base>spot::bdd_allocator</base>
    <member kind="typedef">
      <type>std::map&lt; const ltl::formula *, int &gt;</type>
      <name>fv_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>5bd5f592056f364fdd862a3e0de9fd22</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; int, const ltl::formula * &gt;</type>
      <name>vf_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>d0b9e89c7a60caad9e5786c82a15e0ee</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; int, int &gt;</type>
      <name>cc_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>138b0c0db8a6a7a6a1feda2476eceb75</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>bdd_dict</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>d2076425544919476f16812e2c9cc242</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~bdd_dict</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>f6f03983c2f4647cf0a192d5868c1b14</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_proposition</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>6095afcbbb4fbe67066d51169e80d333</anchor>
      <arglist>(const ltl::formula *f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>register_propositions</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>bb99eed0b49ffba5229af45ef7c1e05f</anchor>
      <arglist>(bdd f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_state</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>c5b0d292aafacbfbbd233080fe4a4ad0</anchor>
      <arglist>(const ltl::formula *f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_acceptance_variable</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>bb27e26e93d99e046dbe9e9d0cb21485</anchor>
      <arglist>(const ltl::formula *f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_clone_acc</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>bebfcb644db70ac9368ff316317059a3</anchor>
      <arglist>(int var, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>register_acceptance_variables</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>c4e496809f4027831aed7457fc8b7f3a</anchor>
      <arglist>(bdd f, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_anonymous_variables</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>7075f190e5d3a4face68a7a6e129eb95</anchor>
      <arglist>(int n, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>register_all_variables_of</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>f7e1e37179f390322da4d4cbf134b282</anchor>
      <arglist>(const void *from_other, const void *for_me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>unregister_all_my_variables</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>64b9d9dcc789312a700519388faf40e8</anchor>
      <arglist>(const void *me)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>unregister_variable</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>a502521a61d81107e8c7ce5fab09ed7c</anchor>
      <arglist>(int var, const void *me)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>355c13a47b13dbeb7a169859f4082081</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>assert_emptiness</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>2ceba5c1a54d38a4a25193bc4257d7ae</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>allocate_variables</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>7b566c8e4046fe012d20e60ed4c058e9</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>release_variables</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>12efeee5e8c5786d814996cab0ec3097</anchor>
      <arglist>(int base, int n)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_registered_proposition</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>0676f051e8333759c46e01e8a602eeaf</anchor>
      <arglist>(const ltl::formula *f, const void *by_me)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_registered_state</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>91c13eeb02e0bddcbe0360a05f3dec05</anchor>
      <arglist>(const ltl::formula *f, const void *by_me)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_registered_acceptance_variable</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>42a9f018f9b52e89b383b9dd722f80d1</anchor>
      <arglist>(const ltl::formula *f, const void *by_me)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>initialize</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>1ca5ce94f2ce0de339fc7f59f1d0c2ff</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable">
      <type>fv_map</type>
      <name>now_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>f654f827c195d9a47fb733a7c6341aae</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>vf_map</type>
      <name>now_formula_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>cf50ce922f71b01a12831c4b6d8acf7d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>fv_map</type>
      <name>var_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>9f47535b3c6ca438bb58975a240d783f</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>vf_map</type>
      <name>var_formula_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>90bcd076f72e4bad8872e2f2a55d57e3</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>fv_map</type>
      <name>acc_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>dea537e22c5889d908170b17ab8e8fd0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>vf_map</type>
      <name>acc_formula_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>fc740945a1bf5e2e7755369cb5c70f60</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>cc_map</type>
      <name>clone_counts</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>b9dff75e8898f9ab0df5161b326a797e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bddPair *</type>
      <name>next_to_now</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>1ecc1ea5dd6b7cf1d89d7add2cd056de</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bddPair *</type>
      <name>now_to_next</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>27dd24fcedeb14ccc72df82be8588a41</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::set&lt; const void * &gt;</type>
      <name>ref_set</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>015d330da101dd646875b8ed1613a2d6</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::map&lt; int, ref_set &gt;</type>
      <name>vr_map</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>27164f9864dc315eab9403ee353d9cbb</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::map&lt; const void *, anon_free_list &gt;</type>
      <name>free_anonymous_list_of_type</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>c2fa5711429e22d62e36d2298b8662c4</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>unregister_variable</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>852fb84759f5b8e2cbe964030731c9a8</anchor>
      <arglist>(vr_map::iterator &amp;cur, const void *me)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>vr_map</type>
      <name>var_refs</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>b97f0fbd3a64d4e44a01ccfd2f724471</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>free_anonymous_list_of_type</type>
      <name>free_anonymous_list_of</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>fc4bf1881835873c99827d4d42d63dd7</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>lvarnum</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>3736ca5090949c9fb93745544b95daab</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected" static="yes">
      <type>static bool</type>
      <name>initialized</name>
      <anchorfile>classspot_1_1bdd__allocator.html</anchorfile>
      <anchor>3916225a141645c15fa4d53848ef4f5e</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private">
      <type></type>
      <name>bdd_dict</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>0b647287ae4a25e1d72273a289eedbe6</anchor>
      <arglist>(const bdd_dict &amp;other)</arglist>
    </member>
    <member kind="function" protection="private">
      <type>bdd_dict &amp;</type>
      <name>operator=</name>
      <anchorfile>classspot_1_1bdd__dict.html</anchorfile>
      <anchor>dc2ce6e1df9d89f44c51a2430d61270a</anchor>
      <arglist>(const bdd_dict &amp;other)</arglist>
    </member>
    <class kind="class">spot::bdd_dict::anon_free_list</class>
  </compound>
  <compound kind="class">
    <name>spot::bdd_dict::anon_free_list</name>
    <filename>classspot_1_1bdd__dict_1_1anon__free__list.html</filename>
    <base>spot::free_list</base>
    <member kind="function">
      <type></type>
      <name>anon_free_list</name>
      <anchorfile>classspot_1_1bdd__dict_1_1anon__free__list.html</anchorfile>
      <anchor>765044d895b9c611fa2e239c2f42fdba</anchor>
      <arglist>(bdd_dict *d=0)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>extend</name>
      <anchorfile>classspot_1_1bdd__dict_1_1anon__free__list.html</anchorfile>
      <anchor>e2ae1a14e2aad12f7c82d8415fb4f8b6</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>register_n</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>5e2485e960b7a0924c8801c19c7d9b43</anchor>
      <arglist>(int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>release_n</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>970f34e2944cb3c6573d605c4d01859a</anchor>
      <arglist>(int base, int n)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump_free_list</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>6545d8275be7fb2efa4291814b4bcc6d</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>0e9d104134e6db9feeb9a5ead1a4fe57</anchor>
      <arglist>(int base, int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>remove</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>b2ee6a3adeb1c8ae1a9bc062f3914349</anchor>
      <arglist>(int base, int n=0)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>free_count</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>e193fe59e9484b35a0b00ad7a9cfc40d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; int, int &gt;</type>
      <name>pos_lenght_pair</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>2772e28510a1ad89342eeeb93a8fb9a4</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::list&lt; pos_lenght_pair &gt;</type>
      <name>free_list_type</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>f7e87c1c112d0e26b44396de9cc78843</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>eac5a308e26bb6e6058e720da145bf5b</anchor>
      <arglist>(free_list_type::iterator i, int base, int n)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>free_list_type</type>
      <name>fl</name>
      <anchorfile>classspot_1_1free__list.html</anchorfile>
      <anchor>fdaed42e797fc05cfab78c5450838b00</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd_dict *</type>
      <name>dict_</name>
      <anchorfile>classspot_1_1bdd__dict_1_1anon__free__list.html</anchorfile>
      <anchor>977e889d44c17faeb12e4b65feff314a</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state</name>
    <filename>classspot_1_1state.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>744089f4237554dea2e538f25690b32a</anchor>
      <arglist>(const state *other) const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>e95a1a0188e4ba456e1fd8a9bdce0728</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>ea8854c0610c11c0b36d96fce90c3612</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~state</name>
      <anchorfile>classspot_1_1state.html</anchorfile>
      <anchor>93b28d1aa2200cccdb4159bcf3e7b761</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::state_ptr_less_than</name>
    <filename>structspot_1_1state__ptr__less__than.html</filename>
    <member kind="function">
      <type>bool</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1state__ptr__less__than.html</anchorfile>
      <anchor>1012444cdcf92602b7c462a172f3015d</anchor>
      <arglist>(const state *left, const state *right) const</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::state_ptr_equal</name>
    <filename>structspot_1_1state__ptr__equal.html</filename>
    <member kind="function">
      <type>bool</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1state__ptr__equal.html</anchorfile>
      <anchor>b51a01ef41f24268b7d35a2c5d44fa2e</anchor>
      <arglist>(const state *left, const state *right) const</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::state_ptr_hash</name>
    <filename>structspot_1_1state__ptr__hash.html</filename>
    <member kind="function">
      <type>size_t</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1state__ptr__hash.html</anchorfile>
      <anchor>190cb6c69597d19241df02699f5e6ce6</anchor>
      <arglist>(const state *that) const</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_bdd</name>
    <filename>classspot_1_1state__bdd.html</filename>
    <base>spot::state</base>
    <member kind="function">
      <type></type>
      <name>state_bdd</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>d08aef515653b85eded1634f1ed59978</anchor>
      <arglist>(bdd s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>as_bdd</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>ea7556d81ec7fc7ef822a3fdd3886f03</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>8337b035577d9a74af8f4a78fa815c68</anchor>
      <arglist>(const state *other) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>5d7d345ad639b3fbcdaf51c5bac10fd9</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_bdd *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>ced2283b7055e61210a70d3e251c882c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>state_</name>
      <anchorfile>classspot_1_1state__bdd.html</anchorfile>
      <anchor>90b4a51248aa8218ed633e54258bb1da</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_succ_iterator</name>
    <filename>classspot_1_1tgba__succ__iterator.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_succ_iterator</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>e21dcf415efaa9b59e6cadc2490c3263</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>09901d8bb1addc2512f99ea2d47dc70a</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>ad7914dae3d29f19e3d48c628a4e2da1</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>87b7a78493cea240f4cb4353a7d5fa70</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>c64098592949c83529276da48a63fed4</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>5e765c6e311cc1afe19e7553161f6613</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__succ__iterator.html</anchorfile>
      <anchor>77c96c3c12c442384c649e7138c40ef2</anchor>
      <arglist>() const=0</arglist>
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
      <anchor>a364e35af0138d35f7f705f551c740e2</anchor>
      <arglist>(const tgba_bdd_core_data &amp;d, bdd successors)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_succ_iterator_concrete</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>4886d7e62d648a8f549c2b8202c1d6c5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>5896f9fb5b8f9cbd8f6605978363bda3</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>fade7f08aab50c91d14497714c03ddb5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>a1abe2aefdc05b39b3e1220ae7a3fcc5</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>state_bdd *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>f036641a20b37380770d3f179a43b75a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>267acf7f9fba3e048cfbb22b190a3067</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>ce7cf7fe4f1a9e9101524bcfeb049163</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const tgba_bdd_core_data &amp;</type>
      <name>data_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>ccdb6af8456d8257479936ed2b4614ae</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>succ_set_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>f0b6b667b577a7e596a17f07d6ff6c61</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>succ_set_left_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>eaabadd39f6633b4dabf956fa08e6bdf</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>current_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>7fabd0bcbfa9a1dddeef032a3bec0974</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>current_state_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>664ef009f693b541ab8b8e7101a1ea73</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>current_acc_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__concrete.html</anchorfile>
      <anchor>51eef158ce8bb526a7ac32b7188d9381</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba</name>
    <filename>classspot_1_1tgba.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>e87e13c65699a7c1a801bc0eb1285718</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f9a21e78139852142ce44240f74953a0</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>63de4ff3e10d4d07b5c2566ba550943c</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const=0</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f70097ee761b43a84d1987aed04c7f61</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>8bd361fdc01acf1d7ddb6b66a34d502f</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>5fb814c296eac2474fbea8b4d93e8741</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>e891397bffeb68aca2de745bd861155d</anchor>
      <arglist>(const state *state) const=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>445e3ca79b7291ee28e15b1ba79147ae</anchor>
      <arglist>(const tgba_succ_iterator *t) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>b411601915aacf07dd0a9035fd5eebe0</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>c9af48e9633173a146e0856e4ce3d0cb</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>518a117e743e4972d88a321c7c0e6c71</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>42ef729d38eb2f45dcb02da6a3ceb486</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>tgba</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>a606dc20990f4fb0c779d6e7c1b6a1fc</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>68046d7846d2ef5b463e2dfa47432a7f</anchor>
      <arglist>(const state *state) const=0</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>32ef494a0477b1d462cd8474682515b1</anchor>
      <arglist>(const state *state) const=0</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const state *</type>
      <name>last_support_conditions_input_</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>1f02d752dfc5206c7d23352d762f8150</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>last_support_conditions_output_</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>938c95ad89acd77b4a4ffa06aac1b42d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const state *</type>
      <name>last_support_variables_input_</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>876022eeefbdf3d3558227fbb9e5f5bc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>last_support_variables_output_</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f05d20866a1a1a84fde7773410da6a75</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>int</type>
      <name>num_acc_</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>d23432e675c5b406da4b7395e0729a8e</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_bdd_concrete</name>
    <filename>classspot_1_1tgba__bdd__concrete.html</filename>
    <base>spot::tgba</base>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_concrete</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>38abffd64eeecace04ed61471370f759</anchor>
      <arglist>(const tgba_bdd_factory &amp;fact)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_concrete</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>5daeb83641ac0a3edf39d16dd27d2a0f</anchor>
      <arglist>(const tgba_bdd_factory &amp;fact, bdd init)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_bdd_concrete</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>3c0bc2e64d4e78890079d7e7be401078</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>set_init_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>fa8e0f6c33006b3b3afb997a1a570ed2</anchor>
      <arglist>(bdd s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_bdd *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>3c2bdd79f40bc2daecdb6c54da3954df</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>get_init_bdd</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>cf615659898ed204cc84546b70af8414</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator_concrete *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>583b87124be78c80be11dc64ddecbf23</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>b1dda55a04c513af9f5b8858bfdf88ca</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>a10a4b0dbe83bcaaa51de0da71343920</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const tgba_bdd_core_data &amp;</type>
      <name>get_core_data</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>05e07fa29cf2c45067b02f4dd7b7bb0d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>e0d2e1bcd0ee148dfa57a0bc64999fd3</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>cedc716ee88d9c345fb47147e6dab2e3</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f70097ee761b43a84d1987aed04c7f61</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>8bd361fdc01acf1d7ddb6b66a34d502f</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>445e3ca79b7291ee28e15b1ba79147ae</anchor>
      <arglist>(const tgba_succ_iterator *t) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>b411601915aacf07dd0a9035fd5eebe0</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>518a117e743e4972d88a321c7c0e6c71</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>b0c3413be3488cce119b22ca7e9a5d3b</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>0e3f8218c473a6db007ebad06e14dea6</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>tgba_bdd_core_data</type>
      <name>data_</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>e9f128a7964713decc50a18934691e5c</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>init_</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>7e99671d794c73c99d29b2c744e4d5f4</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private">
      <type></type>
      <name>tgba_bdd_concrete</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>e9ba9cc354015f6ee349129bf0e52168</anchor>
      <arglist>(const tgba_bdd_concrete &amp;)</arglist>
    </member>
    <member kind="function" protection="private">
      <type>tgba_bdd_concrete &amp;</type>
      <name>operator=</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete.html</anchorfile>
      <anchor>4530aade4f23f653c574f22be9408df5</anchor>
      <arglist>(const tgba_bdd_concrete &amp;)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_bdd_concrete_factory</name>
    <filename>classspot_1_1tgba__bdd__concrete__factory.html</filename>
    <base>spot::tgba_bdd_factory</base>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_concrete_factory</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>830df7027d719cc3574e89d8a383e722</anchor>
      <arglist>(bdd_dict *dict)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_bdd_concrete_factory</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>91d49d7d7b98ae8483c9437cd3a12d1c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>create_state</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>c6cf312ceb992728e78e37bdc4ed0336</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>create_atomic_prop</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>eec16411072546e89dc612ef139b19a9</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>a83197dccba164a208cf22b1c643d5a6</anchor>
      <arglist>(bdd b, const ltl::formula *a)</arglist>
    </member>
    <member kind="function">
      <type>const tgba_bdd_core_data &amp;</type>
      <name>get_core_data</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>4941279d73e1810faad4d06f1a2a19b4</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>1a3fe8740621cd1a77d5527e10507668</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>constrain_relation</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>edb7c77666bfad9ee507254b8b382571</anchor>
      <arglist>(bdd new_rel)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>finish</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>8c575ec7e41eafc29c7fb4ca439b67c9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>Sgi::hash_map&lt; const ltl::formula *, bdd, ltl::formula_ptr_hash &gt;</type>
      <name>acc_map_</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>ead802a201c4b50aae323ae176be07d6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>tgba_bdd_core_data</type>
      <name>data_</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>07b3e3b8e7b06e2f38165daa0609585f</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>acc_map_</type>
      <name>acc_</name>
      <anchorfile>classspot_1_1tgba__bdd__concrete__factory.html</anchorfile>
      <anchor>9f8c68bc5956364f77c4f610f467fa45</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_bdd_core_data</name>
    <filename>structspot_1_1tgba__bdd__core__data.html</filename>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_core_data</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>c4d1e835b96ab8264aac2a2ed50f73df</anchor>
      <arglist>(bdd_dict *dict)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_core_data</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>c236f34dee1ce44a31ca7199cad9face</anchor>
      <arglist>(const tgba_bdd_core_data &amp;copy)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_bdd_core_data</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>0a49ac42ea3111e3e14df59a0350ee33</anchor>
      <arglist>(const tgba_bdd_core_data &amp;left, const tgba_bdd_core_data &amp;right)</arglist>
    </member>
    <member kind="function">
      <type>const tgba_bdd_core_data &amp;</type>
      <name>operator=</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>48cb178a36be852106a3f5a2c500e2aa</anchor>
      <arglist>(const tgba_bdd_core_data &amp;copy)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_now_next</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>e9804355c508876e8bd04d6b119c0e4a</anchor>
      <arglist>(bdd now, bdd next)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_atomic_prop</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>de918f045d34a51e000158f73c698072</anchor>
      <arglist>(bdd var)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_acceptance_condition</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>668e6bbc7dfc65d231db6694fe256b4a</anchor>
      <arglist>(bdd prom)</arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>relation</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a304f53084eadf2a73b2ed16fd63eeb4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acceptance_conditions</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>8a693bbcaa794d1503c667bb56905a1e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>abee4b6acc70a6a9dd7c4193cf70b648</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>now_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>57f5083712b3f2ff3bbc05236df0ba9d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>next_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>3ced731d0d9eeaf380b681fae44c3dec</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>nownext_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>eb96c7c0d8901082c3ee7af12cd1b0cc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>notnow_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>3f647bc2217a35cd5b41ea442b5f0fa1</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>notnext_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>95d4747f62d7ef1348d89b370318b02d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>var_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>a1bc747a58e8d64bbb94c735a66548c4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>notvar_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>2e83403899aa907ac4c3e3b90749a252</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>varandnext_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>40e40af7150cc431d3f17c71d157f452</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acc_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>997858217ebc0cbc304258fe53d5c990</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>notacc_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>12e79ba7db251dd24bac7849bcc840fc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>negacc_set</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>27ea4bcb390c7b2c54b547444d67ae56</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd_dict *</type>
      <name>dict</name>
      <anchorfile>structspot_1_1tgba__bdd__core__data.html</anchorfile>
      <anchor>06a48a4843cbec690ad5ef99d12eb1ea</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_bdd_factory</name>
    <filename>classspot_1_1tgba__bdd__factory.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_bdd_factory</name>
      <anchorfile>classspot_1_1tgba__bdd__factory.html</anchorfile>
      <anchor>07d4fe635eb0c52f15953a51ff5d2ea1</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const tgba_bdd_core_data &amp;</type>
      <name>get_core_data</name>
      <anchorfile>classspot_1_1tgba__bdd__factory.html</anchorfile>
      <anchor>9553d9ffe1c6f62c726e4691735243cd</anchor>
      <arglist>() const=0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_explicit</name>
    <filename>classspot_1_1tgba__explicit.html</filename>
    <base>spot::tgba</base>
    <member kind="typedef">
      <type>std::list&lt; transition * &gt;</type>
      <name>state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>63e4dcb561f53751d2ab80f895eb56c3</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_explicit</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>0d73e30c31631cf41713bbe0d001266d</anchor>
      <arglist>(bdd_dict *dict)</arglist>
    </member>
    <member kind="function">
      <type>state *</type>
      <name>set_init_state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>2a1f069bcef5d32a8d0b8490436a71dd</anchor>
      <arglist>(const std::string &amp;state)</arglist>
    </member>
    <member kind="function">
      <type>transition *</type>
      <name>create_transition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>eb295e68e22bde2d2c2db92b6b8987a5</anchor>
      <arglist>(const std::string &amp;source, const std::string &amp;dest)</arglist>
    </member>
    <member kind="function">
      <type>transition *</type>
      <name>create_transition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>9fe966c1f6551504a9142ad34ff161ab</anchor>
      <arglist>(state *source, const state *dest)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>dbde96d82990b620d2738b3a35450252</anchor>
      <arglist>(transition *t, const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>44093c89c2c0fb8673cb3e9b4e28e41c</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>fb73a96c82f4112b4e131c785e5a6af2</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>copy_acceptance_conditions_of</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>68762ba59c31ddb255c953e5f87d6a12</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>has_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>35d745759208f3f436ed2050e5a4b7e4</anchor>
      <arglist>(const ltl::formula *f) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>54f981cf0c5f50960ea188587ad9d982</anchor>
      <arglist>(transition *t, const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>ce6b8d5dbe773bd2507b30a887bbf69a</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>complement_all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>2850e269a5e05e52d31486c97b141926</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>merge_transitions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d3592ced8a48f38e43181a632e6ff871</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>state *</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>9688d438833cf5785de617effad64185</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_explicit</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>5902a1e0e40e14eff973ea2eff178418</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual spot::state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d34f526e9ca762527fabd599c532b957</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>e5490165a5b00ba905a18683b9c1d918</anchor>
      <arglist>(const spot::state *local_state, const spot::state *global_state=0, const tgba *global_automaton=0) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>76850ad04e7d53d4b60537506f5bb9ab</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>de53ccac85c69a43552b76cb818c02fc</anchor>
      <arglist>(const spot::state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>98e3335a84ce287ef4582afe92d20229</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>c45a01e44bb69da460be22c8cbb949e2</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f70097ee761b43a84d1987aed04c7f61</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>8bd361fdc01acf1d7ddb6b66a34d502f</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>445e3ca79b7291ee28e15b1ba79147ae</anchor>
      <arglist>(const tgba_succ_iterator *t) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>b411601915aacf07dd0a9035fd5eebe0</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>518a117e743e4972d88a321c7c0e6c71</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const std::string, tgba_explicit::state *, string_hash &gt;</type>
      <name>ns_map</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d3e79e2867360e40e55232cfe88da87f</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const tgba_explicit::state *, std::string, ptr_hash&lt; tgba_explicit::state &gt; &gt;</type>
      <name>sn_map</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>6bde0c48da71735ea39f0eb9fd37e1cf</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d4ace9907e61c0c03e415fc12009bb00</anchor>
      <arglist>(const spot::state *state) const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>72b0153dea94092633739422cb84f651</anchor>
      <arglist>(const spot::state *state) const</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bdd</type>
      <name>get_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>dfd9263c12089f000b39b022af63b7c7</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>ns_map</type>
      <name>name_state_map_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>a652b1938e2b07fb436778e43e8010b6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_map</type>
      <name>state_name_map_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>4d0e88040478a8f287f6cb8aa3fab772</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd_dict *</type>
      <name>dict_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>2d38155de9dded363355db0f4bff0f2c</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>tgba_explicit::state *</type>
      <name>init_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>5c2d7992104e18806372a73d11b31e0b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>all_acceptance_conditions_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d018379ffe693cc24907555fb09541ed</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>neg_acceptance_conditions_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>ace207e13a0b909227913a803d564d0b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>all_acceptance_conditions_computed_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>8a16b95873c589c9d8c53bf9ea92dd7d</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private">
      <type></type>
      <name>tgba_explicit</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>c3931a6c07b957a118c3f3498b5f0414</anchor>
      <arglist>(const tgba_explicit &amp;other)</arglist>
    </member>
    <member kind="function" protection="private">
      <type>tgba_explicit &amp;</type>
      <name>operator=</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>1a1ba2530e150eabd508153a8bacc3dc</anchor>
      <arglist>(const tgba_explicit &amp;other)</arglist>
    </member>
    <class kind="struct">spot::tgba_explicit::transition</class>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_explicit::transition</name>
    <filename>structspot_1_1tgba__explicit_1_1transition.html</filename>
    <member kind="variable">
      <type>bdd</type>
      <name>condition</name>
      <anchorfile>structspot_1_1tgba__explicit_1_1transition.html</anchorfile>
      <anchor>8fc097e79fd53beb58188e0f20af6ee9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acceptance_conditions</name>
      <anchorfile>structspot_1_1tgba__explicit_1_1transition.html</anchorfile>
      <anchor>42f03d36a91c070cb81dcd86af89e2a6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>const state *</type>
      <name>dest</name>
      <anchorfile>structspot_1_1tgba__explicit_1_1transition.html</anchorfile>
      <anchor>281b4e8d9a55f165f3c57d9e8775d20b</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::state_explicit</name>
    <filename>classspot_1_1state__explicit.html</filename>
    <base>spot::state</base>
    <member kind="function">
      <type></type>
      <name>state_explicit</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>16f7620d65b38f263e573ee12669b812</anchor>
      <arglist>(const tgba_explicit::state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>b5ee4e3c2b991a18163bbd41df75d689</anchor>
      <arglist>(const spot::state *other) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>f77bd25caf535a95d5667ec05bc1b09a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_explicit *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>f2dbf8d3711131b5e0191820e6739579</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~state_explicit</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>26c74ae83e087e37a25d53985abcd3b1</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const tgba_explicit::state *</type>
      <name>get_state</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>ee8aa57cd2fb02fd8342e2aace5569df</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const tgba_explicit::state *</type>
      <name>state_</name>
      <anchorfile>classspot_1_1state__explicit.html</anchorfile>
      <anchor>5ba28bcaf0e861f8bb1a8bed40a6769c</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_explicit_succ_iterator</name>
    <filename>classspot_1_1tgba__explicit__succ__iterator.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function">
      <type></type>
      <name>tgba_explicit_succ_iterator</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>0ab7eb8e1a7a3c852d76d0b4b059f4f6</anchor>
      <arglist>(const tgba_explicit::state *s, bdd all_acc)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_explicit_succ_iterator</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>f0d925b804de83ffdfc1af68a40f5aed</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>d3e5560a5c51b4c6e8d844a91f94f9b9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>2edf632c3e3e29437121ae9784f347e7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>d2be7be08213360bcdd25a83cc13e2e2</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_explicit *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>d839bea1baaee9bd6f4b2d07e3e02201</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>f701984191eb618661de5bd789613166</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>06828a96b2479fc2b9aff39440eae0a2</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const tgba_explicit::state *</type>
      <name>s_</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>02317f3cf6dfd3b331cc806e550a5678</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>tgba_explicit::state::const_iterator</type>
      <name>i_</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>7ba269ff82562a63fb4359ef4ea6ccc2</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>all_acceptance_conditions_</name>
      <anchorfile>classspot_1_1tgba__explicit__succ__iterator.html</anchorfile>
      <anchor>8c1a098f6270cd8d18dd99a827565132</anchor>
      <arglist></arglist>
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
      <anchor>cbefb312091137ab48ce46337fe79b65</anchor>
      <arglist>(state *left, state *right)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>state_product</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>482aa4b3471b307c1bafa016d313249a</anchor>
      <arglist>(const state_product &amp;o)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~state_product</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>6a3821df5f1ff42234d3d31f394f36a0</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>state *</type>
      <name>left</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>dceb83990acd49781dbb57983c96ea9d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>state *</type>
      <name>right</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>91c4081145ecb3031d1d616602eb764b</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>compare</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>3ef72e2f42c4e1b5480b1032a2a105f5</anchor>
      <arglist>(const state *other) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>731b1b69652eef0d9dac4da18187869a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_product *</type>
      <name>clone</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>21a7a4fee32bfeb6f5a4e44b0d30ac25</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>state *</type>
      <name>left_</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>ccfa4c6250d58869f7b41a88182ac112</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>state *</type>
      <name>right_</name>
      <anchorfile>classspot_1_1state__product.html</anchorfile>
      <anchor>1a73a9b421d4cccf7d9429e435b6b298</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_succ_iterator_product</name>
    <filename>classspot_1_1tgba__succ__iterator__product.html</filename>
    <base>spot::tgba_succ_iterator</base>
    <member kind="function">
      <type></type>
      <name>tgba_succ_iterator_product</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>82e2dd818b3c0de21b6b3738da616742</anchor>
      <arglist>(tgba_succ_iterator *left, tgba_succ_iterator *right, bdd left_neg, bdd right_neg, bddPair *right_common_acc)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_succ_iterator_product</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>884503c8a03102a5a48cd88c885937b0</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>first</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>c6b1e103a4d469f41051c9cee362a849</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>next</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>6b2a44718df756e53f08e5c4974be9ba</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>7535262ef7e96cb18b3cd608716f73f8</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>state_product *</type>
      <name>current_state</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>ba2be703d0f48916f8a1fcc84e3f014e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_condition</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>5d54efe6b478ac2d3be4bd24f19a3253</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>current_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>51c676f6e520d7f4db2163affb315254</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>tgba_succ_iterator *</type>
      <name>left_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>da9206950b4bcc47cd9514183d534e42</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>tgba_succ_iterator *</type>
      <name>right_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>07579093db34b1de96dd4d52e738e586</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>current_cond_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>2057f31373dbca92dd2f1d866a49a9b5</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>left_neg_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>3bdff5bf1129d1d17ac8f4e22fa3c73e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>right_neg_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>bcd9841b196664e8151c727adf9e2fff</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bddPair *</type>
      <name>right_common_acc_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>4b69e50804747fd58750ebf7d6383d97</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private">
      <type>void</type>
      <name>step_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>decf714af9532b61622d68b6cc9ae6bb</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="private">
      <type>void</type>
      <name>next_non_false_</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>02fdc0f31bc182afb32c711bc5492254</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="friend" protection="protected">
      <type>friend class</type>
      <name>tgba_product</name>
      <anchorfile>classspot_1_1tgba__succ__iterator__product.html</anchorfile>
      <anchor>bbfbbac15a2b21d3ecaff5458d24b49d</anchor>
      <arglist></arglist>
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
      <anchor>c87cc8973de290dfb03e4af05b6f18aa</anchor>
      <arglist>(const tgba *left, const tgba *right)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_product</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>8de0dee13a1255a76c1900a40c0a0f51</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>9dc083ac9992e70594e56c395cd4e485</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator_product *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>c4eb8258ef4201244b4d9ce6ee7ca1e9</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>3d1d281240109fb2e5ae6b8c8e0ea9e5</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>42f8efcc2885c1cf3f4ac3fe98e6c8e3</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>23b2cf2245596c820b79d1be55a4b959</anchor>
      <arglist>(const tgba_succ_iterator *t) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>63415db59de0198957b8db61e68b2d25</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>7383c0799ddbc5d233efdfda9acf748f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>3d287951476975248221260e964c96f7</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f70097ee761b43a84d1987aed04c7f61</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>8bd361fdc01acf1d7ddb6b66a34d502f</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>518a117e743e4972d88a321c7c0e6c71</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>ec0926045064d369b671ae7f3df632c9</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>c30c851400c4c9ea33510141d8fd2cb7</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" protection="private">
      <type></type>
      <name>tgba_product</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>f311dd3e7e44c3af92bb5f7fe4047f71</anchor>
      <arglist>(const tgba_product &amp;)</arglist>
    </member>
    <member kind="function" protection="private">
      <type>tgba_product &amp;</type>
      <name>operator=</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>ee07ebfc8aef8111c3923dd72f0ed80f</anchor>
      <arglist>(const tgba_product &amp;)</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd_dict *</type>
      <name>dict_</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>8048054ed2d511fb8d42634b5695979e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const tgba *</type>
      <name>left_</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>ed415f850f93de54fbd9b4d03a5b3ef7</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const tgba *</type>
      <name>right_</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>8e5c0b58b51f8dce0c89678e8f08be60</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>left_acc_complement_</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>0606c9cb0323788eb93a06feff3b5591</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>right_acc_complement_</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>4f7ea0afd907310b04127e2ad424c66e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>all_acceptance_conditions_</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>d203a496c27b0ef4ab8d553987768be6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>neg_acceptance_conditions_</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>c50b0170453065edc2e8986062180f32</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bddPair *</type>
      <name>right_common_acc_</name>
      <anchorfile>classspot_1_1tgba__product.html</anchorfile>
      <anchor>0a0e9c103aa6b363b200bed070787000</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::direct_simulation_relation</name>
    <filename>classspot_1_1direct__simulation__relation.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::delayed_simulation_relation</name>
    <filename>classspot_1_1delayed__simulation__relation.html</filename>
  </compound>
  <compound kind="class">
    <name>spot::tgba_reduc</name>
    <filename>classspot_1_1tgba__reduc.html</filename>
    <base>spot::tgba_explicit</base>
    <base>spot::tgba_reachable_iterator_breadth_first</base>
    <member kind="typedef">
      <type>std::list&lt; transition * &gt;</type>
      <name>state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>63e4dcb561f53751d2ab80f895eb56c3</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_reduc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>6fd2405e672f957ce3ee5a866ab2de59</anchor>
      <arglist>(const tgba *a, const numbered_state_heap_factory *nshf=numbered_state_heap_hash_map_factory::instance())</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~tgba_reduc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>ec778b35ca9efef02a714c35eca041df</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>quotient_state</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>47b21da960f6b34bf91d9631ffffbd5a</anchor>
      <arglist>(direct_simulation_relation *rel)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>quotient_state</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>9c25652a9154bb3098f679dee4b92778</anchor>
      <arglist>(delayed_simulation_relation *rel)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>delete_transitions</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>7b6c47cbf8ba8ee502e5d4f6c8d3a84f</anchor>
      <arglist>(simulation_relation *rel)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>prune_scc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>90942403ba8da62dad862ebc5006064d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>prune_acc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>6b09c0831226b90a23a57addabc1f482</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>compute_scc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>b6b22ac8626a7370714e7d308806cc40</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>ddb387067e4cc5e5e8e5a870001e3b57</anchor>
      <arglist>(const spot::state *state) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>display_rel_sim</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>231293dd0a62fb5ab2ca2c330359fdc0</anchor>
      <arglist>(simulation_relation *rel, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>display_scc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>d493c5fc4ae646b0427d58c86b0db718</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>state *</type>
      <name>set_init_state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>2a1f069bcef5d32a8d0b8490436a71dd</anchor>
      <arglist>(const std::string &amp;state)</arglist>
    </member>
    <member kind="function">
      <type>transition *</type>
      <name>create_transition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>eb295e68e22bde2d2c2db92b6b8987a5</anchor>
      <arglist>(const std::string &amp;source, const std::string &amp;dest)</arglist>
    </member>
    <member kind="function">
      <type>transition *</type>
      <name>create_transition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>9fe966c1f6551504a9142ad34ff161ab</anchor>
      <arglist>(state *source, const state *dest)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>dbde96d82990b620d2738b3a35450252</anchor>
      <arglist>(transition *t, const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>44093c89c2c0fb8673cb3e9b4e28e41c</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>declare_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>fb73a96c82f4112b4e131c785e5a6af2</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>copy_acceptance_conditions_of</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>68762ba59c31ddb255c953e5f87d6a12</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>has_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>35d745759208f3f436ed2050e5a4b7e4</anchor>
      <arglist>(const ltl::formula *f) const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>54f981cf0c5f50960ea188587ad9d982</anchor>
      <arglist>(transition *t, const ltl::formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>add_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>ce6b8d5dbe773bd2507b30a887bbf69a</anchor>
      <arglist>(transition *t, bdd f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>complement_all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>2850e269a5e05e52d31486c97b141926</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>merge_transitions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d3592ced8a48f38e43181a632e6ff871</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>state *</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>9688d438833cf5785de617effad64185</anchor>
      <arglist>(const std::string &amp;name)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual spot::state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d34f526e9ca762527fabd599c532b957</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>e5490165a5b00ba905a18683b9c1d918</anchor>
      <arglist>(const spot::state *local_state, const spot::state *global_state=0, const tgba *global_automaton=0) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>76850ad04e7d53d4b60537506f5bb9ab</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>98e3335a84ce287ef4582afe92d20229</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>c45a01e44bb69da460be22c8cbb949e2</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f70097ee761b43a84d1987aed04c7f61</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>8bd361fdc01acf1d7ddb6b66a34d502f</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>445e3ca79b7291ee28e15b1ba79147ae</anchor>
      <arglist>(const tgba_succ_iterator *t) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>b411601915aacf07dd0a9035fd5eebe0</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>518a117e743e4972d88a321c7c0e6c71</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>962348317141a53df5bc92086071fd8c</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>c368fb5a611b2509c33f9deb03a7ccc6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aefeca520f9e39c86018d284ff1aa4ce</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6214cd7eb3295c93fae10a800b4635f7</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const tgba_explicit::state *, std::list&lt; state * &gt; *, ptr_hash&lt; tgba_explicit::state &gt; &gt;</type>
      <name>sp_map</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>4978a68337601b6590bc46529b1f72ce</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const spot::state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>f0cb07ad7098ea06c97276f327388d8f</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const std::string, tgba_explicit::state *, string_hash &gt;</type>
      <name>ns_map</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d3e79e2867360e40e55232cfe88da87f</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const tgba_explicit::state *, std::string, ptr_hash&lt; tgba_explicit::state &gt; &gt;</type>
      <name>sn_map</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>6bde0c48da71735ea39f0eb9fd37e1cf</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>start</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>c43e55568b7fec4c4dccdf3793995ea9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>end</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>7ab0d918a2c06008a08babb5f0469652</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>76199dcb58b00e5a49e7a9ef83555f6f</anchor>
      <arglist>(const spot::state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>ffa829625c2c9d6b497ab75e1674039f</anchor>
      <arglist>(int in, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>transition *</type>
      <name>create_transition</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>6e91c9cdf7ca742fe05f66252cf936ce</anchor>
      <arglist>(const spot::state *source, const spot::state *dest)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>redirect_transition</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>dcb12d3470e913cc3bf86b0bdf826031</anchor>
      <arglist>(const spot::state *s, const spot::state *simul)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove_predecessor_state</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>bb30023f38099864d8051a453e530286</anchor>
      <arglist>(const state *s, const state *p)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove_state</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>802a5d31de0c0912164bc3cfddceb400</anchor>
      <arglist>(const spot::state *s)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>merge_state</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>0e2a5bda03c42d579027f28a772f5293</anchor>
      <arglist>(const spot::state *s1, const spot::state *s2)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>merge_state_delayed</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>deb58c64e57ef085024e4e6e519208fb</anchor>
      <arglist>(const spot::state *s1, const spot::state *s2)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>delete_scc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>5a1fab3c3b46bbc16aa7ce1e520f057a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>is_terminal</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>cf2c8254b17f385b6eaad537406ae135</anchor>
      <arglist>(const spot::state *s, int n=-1)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>is_not_accepting</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>7ab0a6e45fa87c24d4086ad40808ea62</anchor>
      <arglist>(const spot::state *s, int n=-1)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove_acc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>39c68765c7d218c46447a3beac65fae0</anchor>
      <arglist>(const spot::state *s)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove_scc</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>f1ffbe2559be80acaa71624df1fe02ef</anchor>
      <arglist>(spot::state *s)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove_component</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>de7d36b84878ce96111c24f6ae5b66bd</anchor>
      <arglist>(const spot::state *from)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>int</type>
      <name>nb_set_acc_cond</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>20055b997a4aacf4e86b9b8ca45a268b</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d4ace9907e61c0c03e415fc12009bb00</anchor>
      <arglist>(const spot::state *state) const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>72b0153dea94092633739422cb84f651</anchor>
      <arglist>(const spot::state *state) const</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bdd</type>
      <name>get_acceptance_condition</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>dfd9263c12089f000b39b022af63b7c7</anchor>
      <arglist>(const ltl::formula *f)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>scc_computed_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>b8963ecb9595893996a394bd9eb17723</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>scc_stack</type>
      <name>root_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>458c4ab524153cd0c4021dc4f45b7b5d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>numbered_state_heap *</type>
      <name>h_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>dba9690ae24925e78d4298bab166b5c3</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::stack&lt; const spot::state * &gt;</type>
      <name>state_scc_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>3fd7b0b093ad102ea6976685475bb169</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>Sgi::hash_map&lt; int, const spot::state * &gt;</type>
      <name>state_scc_v_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>492782d369d05da9c3cf6cda33630779</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sp_map</type>
      <name>state_predecessor_map_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>ace4630c052a489d2026e6e18a5c23b0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>si_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>2d468bcc9141eec9a76b113450d7a87b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map *</type>
      <name>seen_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>410cf7fac05184edb94cb9413f4a7879</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>acc_</name>
      <anchorfile>classspot_1_1tgba__reduc.html</anchorfile>
      <anchor>f0afde6906793e857f9e089ab31eda12</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>ns_map</type>
      <name>name_state_map_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>a652b1938e2b07fb436778e43e8010b6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_map</type>
      <name>state_name_map_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>4d0e88040478a8f287f6cb8aa3fab772</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd_dict *</type>
      <name>dict_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>2d38155de9dded363355db0f4bff0f2c</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>tgba_explicit::state *</type>
      <name>init_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>5c2d7992104e18806372a73d11b31e0b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>all_acceptance_conditions_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>d018379ffe693cc24907555fb09541ed</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>neg_acceptance_conditions_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>ace207e13a0b909227913a803d564d0b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>all_acceptance_conditions_computed_</name>
      <anchorfile>classspot_1_1tgba__explicit.html</anchorfile>
      <anchor>8a16b95873c589c9d8c53bf9ea92dd7d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>b591b269fe4c8cbc61769c5309255b46</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>7fccc175964d8c8d481552443313a319</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>33fbcfee3eb7694451f2b0760ee79de1</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_tba_proxy</name>
    <filename>classspot_1_1tgba__tba__proxy.html</filename>
    <base>spot::tgba</base>
    <member kind="typedef">
      <type>std::list&lt; bdd &gt;</type>
      <name>cycle_list</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>f96a338414230a500e1552de4e163090</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_tba_proxy</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>46c5d3860618e80fd220e2b1989f08b6</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_tba_proxy</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>f350cdd1c403cddb11470b2bbaeeec77</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>aeab255a68e6b2d05c014e5bcd79086a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>acc3b16addf2e12892821a3d796e0066</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>58fe2207ae48b7179757b369bcbe0477</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>991359f6b7ae2d0c165a76e370977bdc</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>d4bba2f97a28446d59e83a37ddfde1be</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>fa0c1f6b0477261742edacafa210bbde</anchor>
      <arglist>(const tgba_succ_iterator *t) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>156dd03fe127999004f05948cf418915</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>ea840b04276269ca0caedee5d363225a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f70097ee761b43a84d1987aed04c7f61</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>8bd361fdc01acf1d7ddb6b66a34d502f</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>518a117e743e4972d88a321c7c0e6c71</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>db3230a4b939198b4385111d826d686b</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>1415980a202262db31683a1f5f6e0e8d</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>cycle_list</type>
      <name>acc_cycle_</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>29df378cea35f367a95d2343e3bc8416</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private">
      <type></type>
      <name>tgba_tba_proxy</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>44590e974cf794ca89349f154ca9950a</anchor>
      <arglist>(const tgba_tba_proxy &amp;)</arglist>
    </member>
    <member kind="function" protection="private">
      <type>tgba_tba_proxy &amp;</type>
      <name>operator=</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>97aaa47e4f112fa6269704953e08cc13</anchor>
      <arglist>(const tgba_tba_proxy &amp;)</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>db2b18182613b1df0be98af1119d10b6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>the_acceptance_cond_</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>1623fc95cc5b30af9ef4baec68555e0e</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_sba_proxy</name>
    <filename>classspot_1_1tgba__sba__proxy.html</filename>
    <base>spot::tgba_tba_proxy</base>
    <member kind="typedef">
      <type>std::list&lt; bdd &gt;</type>
      <name>cycle_list</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>f96a338414230a500e1552de4e163090</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_sba_proxy</name>
      <anchorfile>classspot_1_1tgba__sba__proxy.html</anchorfile>
      <anchor>7711ebb0fc29220e2473db20148d32ba</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>state_is_accepting</name>
      <anchorfile>classspot_1_1tgba__sba__proxy.html</anchorfile>
      <anchor>2146ecff2f41f846d6d10f83c826c405</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>get_init_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>aeab255a68e6b2d05c014e5bcd79086a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_succ_iterator *</type>
      <name>succ_iter</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>acc3b16addf2e12892821a3d796e0066</anchor>
      <arglist>(const state *local_state, const state *global_state=0, const tgba *global_automaton=0) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd_dict *</type>
      <name>get_dict</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>58fe2207ae48b7179757b369bcbe0477</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>format_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>991359f6b7ae2d0c165a76e370977bdc</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state *</type>
      <name>project_state</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>d4bba2f97a28446d59e83a37ddfde1be</anchor>
      <arglist>(const state *s, const tgba *t) const </arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>transition_annotation</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>fa0c1f6b0477261742edacafa210bbde</anchor>
      <arglist>(const tgba_succ_iterator *t) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>all_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>156dd03fe127999004f05948cf418915</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bdd</type>
      <name>neg_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>ea840b04276269ca0caedee5d363225a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>f70097ee761b43a84d1987aed04c7f61</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>support_variables</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>8bd361fdc01acf1d7ddb6b66a34d502f</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned int</type>
      <name>number_of_acceptance_conditions</name>
      <anchorfile>classspot_1_1tgba.html</anchorfile>
      <anchor>518a117e743e4972d88a321c7c0e6c71</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_conditions</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>db3230a4b939198b4385111d826d686b</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bdd</type>
      <name>compute_support_variables</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>1415980a202262db31683a1f5f6e0e8d</anchor>
      <arglist>(const state *state) const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>cycle_list</type>
      <name>acc_cycle_</name>
      <anchorfile>classspot_1_1tgba__tba__proxy.html</anchorfile>
      <anchor>29df378cea35f367a95d2343e3bc8416</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::bfs_steps</name>
    <filename>classspot_1_1bfs__steps.html</filename>
    <member kind="function">
      <type></type>
      <name>bfs_steps</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>cf02b9b32a4061bfcd9816d69eefdc5c</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~bfs_steps</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>d04e5b2c808b0106da37b5b75bf36937</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>search</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>6efc199a86fc9ad53c2220964facd53a</anchor>
      <arglist>(const state *start, tgba_run::steps &amp;l)</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>filter</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>12319da13fb0fde22376522fc416e833</anchor>
      <arglist>(const state *s)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>match</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>d94ee7be9b944e52d4def759eb559868</anchor>
      <arglist>(tgba_run::step &amp;step, const state *dest)=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>finalize</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>aef31a86c98ad2ead6e4cf4aac5ed8b2</anchor>
      <arglist>(const std::map&lt; const state *, tgba_run::step, state_ptr_less_than &gt; &amp;father, const tgba_run::step &amp;s, const state *start, tgba_run::steps &amp;l)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1bfs__steps.html</anchorfile>
      <anchor>2340b458a0f9af2da0e5b74120e7f3b7</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::dotty_decorator</name>
    <filename>classspot_1_1dotty__decorator.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~dotty_decorator</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>c6843387d3f254b503db260e88a721b0</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>state_decl</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>b5228e4a0a320c844efc78f159f3e52c</anchor>
      <arglist>(const tgba *a, const state *s, int n, tgba_succ_iterator *si, const std::string &amp;label)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>link_decl</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>ab4c4e8f63648c09469be5145098af91</anchor>
      <arglist>(const tgba *a, const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si, const std::string &amp;label)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static dotty_decorator *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>3f65ca9c0c1a37d7aba531c243f52c6e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>dotty_decorator</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>1e68819d430b782d8693aa07d34a0aaa</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::emptiness_check_result</name>
    <filename>classspot_1_1emptiness__check__result.html</filename>
    <member kind="function">
      <type></type>
      <name>emptiness_check_result</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>3dcdb828a0a7d6b184ed25b49f9b9bb8</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~emptiness_check_result</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>8953757641fa48a979ec6b14a5647248</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_run *</type>
      <name>accepting_run</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>909ae24b5e8454cb966d320e997570ab</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a9485bb2190b755afa81111fb9ac5303</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>4623c4ba6c4129f750d0c487a7c12e99</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>66f0c4f9074073e8d26ac764d6103d9f</anchor>
      <arglist>(char *options)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const unsigned_statistics *</type>
      <name>statistics</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>7251e795b0457abfd828d4f141038fc8</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>options_updated</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>3ed3330145ca9d988eeca2e5bf773dad</anchor>
      <arglist>(const option_map &amp;old)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>87ce44800ef5cb67e181f186f3d3dcbc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>42a0ad82518ab92ce6ea2166c119e4cc</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::emptiness_check</name>
    <filename>classspot_1_1emptiness__check.html</filename>
    <member kind="function">
      <type></type>
      <name>emptiness_check</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>bd84a2695a4a7613b6b328fb956500bb</anchor>
      <arglist>(const tgba *a, option_map o=option_map())</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~emptiness_check</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>1defb939b69b2ab87280937631005d4f</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>d4d716c54bda588b39ac6f3b643047cd</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>c7b3a1c1e2adc0923b2eea39fbb766ec</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>d0e61dab3b4593d493da86ee625a25c3</anchor>
      <arglist>(char *options)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>safe</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>7a3c73585e7fcea20a643db2209f8020</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual emptiness_check_result *</type>
      <name>check</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>d573f9d7402dfdc44959cd183ae81774</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const unsigned_statistics *</type>
      <name>statistics</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>df35e35484e15ff56cbb1ffbcbd2cc7c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::ostream &amp;</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>48d9479e85bf763d099ed9914944b702</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>options_updated</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>ac525dce9e038b424a1f680b797e2639</anchor>
      <arglist>(const option_map &amp;old)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>315fb003ae56a13d26dc8ffb03a34ed6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>2bf27940474ed0e6ea39a6f8b6c7fcc0</anchor>
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
      <anchor>4af6350795c509ae155159fa08200fd3</anchor>
      <arglist>(const tgba *a) const </arglist>
    </member>
    <member kind="function">
      <type>unsigned int</type>
      <name>min_acceptance_conditions</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>9413f7f91a5bf49bcf17e75554d8ea1f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned int</type>
      <name>max_acceptance_conditions</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>b7958dccc64e50073579b83eeab8b298</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>5b7b7835f0ddcb85d3966c4191e01f50</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>94c541281a8ddc037242ebc1c1621710</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static emptiness_check_instantiator *</type>
      <name>construct</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>d454d1d69ca258b2fc68222436eb62a3</anchor>
      <arglist>(const char *name, const char **err)</arglist>
    </member>
    <member kind="function" protection="private">
      <type></type>
      <name>emptiness_check_instantiator</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>86275bcf54360bac8329abaad9179bdc</anchor>
      <arglist>(option_map o, void *i)</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>9505cb9fbf29a97dccadf419b59587e2</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>void *</type>
      <name>info_</name>
      <anchorfile>classspot_1_1emptiness__check__instantiator.html</anchorfile>
      <anchor>8d79069be90cdfe8feab162df09ea0b9</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_run</name>
    <filename>structspot_1_1tgba__run.html</filename>
    <member kind="typedef">
      <type>std::list&lt; step &gt;</type>
      <name>steps</name>
      <anchorfile>structspot_1_1tgba__run.html</anchorfile>
      <anchor>992869805fc5a8c3146e77309be61312</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~tgba_run</name>
      <anchorfile>structspot_1_1tgba__run.html</anchorfile>
      <anchor>62f1b2824bbc74b7163321da7541978e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_run</name>
      <anchorfile>structspot_1_1tgba__run.html</anchorfile>
      <anchor>6aaba1570a621ee9b0c8978363a26735</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>tgba_run</name>
      <anchorfile>structspot_1_1tgba__run.html</anchorfile>
      <anchor>46d2f9e56cb61e77c10be5cbe8a37209</anchor>
      <arglist>(const tgba_run &amp;run)</arglist>
    </member>
    <member kind="function">
      <type>tgba_run &amp;</type>
      <name>operator=</name>
      <anchorfile>structspot_1_1tgba__run.html</anchorfile>
      <anchor>1943b571944e9027aaf49e5e1535a17d</anchor>
      <arglist>(const tgba_run &amp;run)</arglist>
    </member>
    <member kind="variable">
      <type>steps</type>
      <name>prefix</name>
      <anchorfile>structspot_1_1tgba__run.html</anchorfile>
      <anchor>18d04f72b2050764fb8d7394c8f03548</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>steps</type>
      <name>cycle</name>
      <anchorfile>structspot_1_1tgba__run.html</anchorfile>
      <anchor>7ac206474ee5a1ecb9be7239a63d178c</anchor>
      <arglist></arglist>
    </member>
    <class kind="struct">spot::tgba_run::step</class>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_run::step</name>
    <filename>structspot_1_1tgba__run_1_1step.html</filename>
    <member kind="variable">
      <type>const state *</type>
      <name>s</name>
      <anchorfile>structspot_1_1tgba__run_1_1step.html</anchorfile>
      <anchor>e61b3c22a558f14d65de9a1a0e97f229</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>label</name>
      <anchorfile>structspot_1_1tgba__run_1_1step.html</anchorfile>
      <anchor>db117550f1dd40c893c4f1e1a4cfb66d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acc</name>
      <anchorfile>structspot_1_1tgba__run_1_1step.html</anchorfile>
      <anchor>e5d5c9c4955433c0cd151c68155e2eff</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::unsigned_statistics</name>
    <filename>structspot_1_1unsigned__statistics.html</filename>
    <member kind="typedef">
      <type>unsigned(unsigned_statistics::*)</type>
      <name>unsigned_fun</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>001095ee22b6f1af39b6f3b21878f225</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; const char *, unsigned_fun, char_ptr_less_than &gt;</type>
      <name>stats_map</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>3e8a1630d3c2ca9ca25d5a50438387d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~unsigned_statistics</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>95aa4c42b92cd3ad870863faf1a56dec</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>get</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>1d8bcb95613cbc5da8ccc91bcced1dff</anchor>
      <arglist>(const char *str) const</arglist>
    </member>
    <member kind="variable">
      <type>stats_map</type>
      <name>stats</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>9696e48357820edfaa12abdc1961eadb</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::unsigned_statistics_copy</name>
    <filename>classspot_1_1unsigned__statistics__copy.html</filename>
    <member kind="typedef">
      <type>std::map&lt; const char *, unsigned, char_ptr_less_than &gt;</type>
      <name>stats_map</name>
      <anchorfile>classspot_1_1unsigned__statistics__copy.html</anchorfile>
      <anchor>f822f09fe592215c426a3c8700e27454</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>unsigned_statistics_copy</name>
      <anchorfile>classspot_1_1unsigned__statistics__copy.html</anchorfile>
      <anchor>5ea4d2e95260fdf38498065a85c33ca6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>unsigned_statistics_copy</name>
      <anchorfile>classspot_1_1unsigned__statistics__copy.html</anchorfile>
      <anchor>c86ebc8dff41dba3e86f9156ad376b8a</anchor>
      <arglist>(const unsigned_statistics &amp;o)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>seteq</name>
      <anchorfile>classspot_1_1unsigned__statistics__copy.html</anchorfile>
      <anchor>5e27e164d6506e7232285c04f4c9fb2f</anchor>
      <arglist>(const unsigned_statistics &amp;o)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>operator==</name>
      <anchorfile>classspot_1_1unsigned__statistics__copy.html</anchorfile>
      <anchor>2b7bd0628352b1fdd94ed1af94cb2f91</anchor>
      <arglist>(const unsigned_statistics_copy &amp;o) const</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>operator!=</name>
      <anchorfile>classspot_1_1unsigned__statistics__copy.html</anchorfile>
      <anchor>7567435ad6822db5325ca069b1444092</anchor>
      <arglist>(const unsigned_statistics_copy &amp;o) const</arglist>
    </member>
    <member kind="variable">
      <type>stats_map</type>
      <name>stats</name>
      <anchorfile>classspot_1_1unsigned__statistics__copy.html</anchorfile>
      <anchor>da02e8d891e755de82cdb5b207418dcd</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bool</type>
      <name>set</name>
      <anchorfile>classspot_1_1unsigned__statistics__copy.html</anchorfile>
      <anchor>d07714972847e22c9a3af4308fdb6b6a</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ec_statistics</name>
    <filename>classspot_1_1ec__statistics.html</filename>
    <base>spot::unsigned_statistics</base>
    <member kind="typedef">
      <type>unsigned(unsigned_statistics::*)</type>
      <name>unsigned_fun</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>001095ee22b6f1af39b6f3b21878f225</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; const char *, unsigned_fun, char_ptr_less_than &gt;</type>
      <name>stats_map</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>3e8a1630d3c2ca9ca25d5a50438387d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>ec_statistics</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>8cded4229f7b8471a9fe18d7c58f1aad</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set_states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>81d1156d35135a920f17d22765e07724</anchor>
      <arglist>(unsigned n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>779baacabc1e56f583dd07ab9cc693af</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_transitions</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>36673e3fb88feefbb1097b5cadd01327</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>de5ca49fc87a2bc9ce9414fc3a96c56b</anchor>
      <arglist>(unsigned n=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>dec_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>cb78038e2e0be1a3d5761cf5b5bdcae9</anchor>
      <arglist>(unsigned n=1)</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>969cdceb81f3886a8687b9b52d9b2304</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>transitions</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>0be440094298131338a7f9a0d9e5e12e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>max_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>9225baed03ca66e56573917d12f8dd1e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>5556318b7a1b194365c4a92049b3d08d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>get</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>1d8bcb95613cbc5da8ccc91bcced1dff</anchor>
      <arglist>(const char *str) const</arglist>
    </member>
    <member kind="variable">
      <type>stats_map</type>
      <name>stats</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>9696e48357820edfaa12abdc1961eadb</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned</type>
      <name>states_</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>351989d328fe32a2c841ca4c759f2fe5</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned</type>
      <name>transitions_</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>09a8f1cd2fa661f9301c9b9fb35ed724</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned</type>
      <name>depth_</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>af92679f9a66c811adcfdbc08587678a</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned</type>
      <name>max_depth_</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>0c9bc3c5d707fce78db849b85c570466</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ars_statistics</name>
    <filename>classspot_1_1ars__statistics.html</filename>
    <base>spot::unsigned_statistics</base>
    <member kind="typedef">
      <type>unsigned(unsigned_statistics::*)</type>
      <name>unsigned_fun</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>001095ee22b6f1af39b6f3b21878f225</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; const char *, unsigned_fun, char_ptr_less_than &gt;</type>
      <name>stats_map</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>3e8a1630d3c2ca9ca25d5a50438387d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>ars_statistics</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>2e3da45cfc7310ee3108f6a219b66578</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_ars_prefix_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>61014af68a3e62e3fee1a473515b27bf</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>ars_prefix_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>4f68f055271af416ec665a90173a3e6c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_ars_cycle_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>f8d364ef84d0299f7b2e6294b500b274</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>ars_cycle_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>cbf2b59ea92ca6951ad3ea62c3c6f3d5</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>get</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>1d8bcb95613cbc5da8ccc91bcced1dff</anchor>
      <arglist>(const char *str) const</arglist>
    </member>
    <member kind="variable">
      <type>stats_map</type>
      <name>stats</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>9696e48357820edfaa12abdc1961eadb</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned</type>
      <name>prefix_states_</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>4493449d446199bb3174671090382095</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned</type>
      <name>cycle_states_</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>f57e0d79e7066fa6fe32dacc371cf179</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::acss_statistics</name>
    <filename>classspot_1_1acss__statistics.html</filename>
    <base>spot::ars_statistics</base>
    <member kind="typedef">
      <type>unsigned(unsigned_statistics::*)</type>
      <name>unsigned_fun</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>001095ee22b6f1af39b6f3b21878f225</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; const char *, unsigned_fun, char_ptr_less_than &gt;</type>
      <name>stats_map</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>3e8a1630d3c2ca9ca25d5a50438387d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>acss_statistics</name>
      <anchorfile>classspot_1_1acss__statistics.html</anchorfile>
      <anchor>f028719871809df77ff73495c889e888</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~acss_statistics</name>
      <anchorfile>classspot_1_1acss__statistics.html</anchorfile>
      <anchor>8be4486b6a439c88573896ab54d27a68</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual unsigned</type>
      <name>acss_states</name>
      <anchorfile>classspot_1_1acss__statistics.html</anchorfile>
      <anchor>f5a3e32642882875df6e120d47318488</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_ars_prefix_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>61014af68a3e62e3fee1a473515b27bf</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>ars_prefix_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>4f68f055271af416ec665a90173a3e6c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_ars_cycle_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>f8d364ef84d0299f7b2e6294b500b274</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>ars_cycle_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>cbf2b59ea92ca6951ad3ea62c3c6f3d5</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>get</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>1d8bcb95613cbc5da8ccc91bcced1dff</anchor>
      <arglist>(const char *str) const</arglist>
    </member>
    <member kind="variable">
      <type>stats_map</type>
      <name>stats</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>9696e48357820edfaa12abdc1961eadb</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::couvreur99_check_result</name>
    <filename>classspot_1_1couvreur99__check__result.html</filename>
    <base>spot::emptiness_check_result</base>
    <base>spot::acss_statistics</base>
    <member kind="typedef">
      <type>unsigned(unsigned_statistics::*)</type>
      <name>unsigned_fun</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>001095ee22b6f1af39b6f3b21878f225</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; const char *, unsigned_fun, char_ptr_less_than &gt;</type>
      <name>stats_map</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>3e8a1630d3c2ca9ca25d5a50438387d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>couvreur99_check_result</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>ee5e806f626f857fa3ecd914220e8827</anchor>
      <arglist>(const couvreur99_check_status *ecs, option_map o=option_map())</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual tgba_run *</type>
      <name>accepting_run</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>2ad4b0f54841a33797c336505109ee16</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>59e8464d5d79de60c5554a2095c0d6cd</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual unsigned</type>
      <name>acss_states</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>0e357a4e14975008043d25007ec626d7</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>a9485bb2190b755afa81111fb9ac5303</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>4623c4ba6c4129f750d0c487a7c12e99</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>66f0c4f9074073e8d26ac764d6103d9f</anchor>
      <arglist>(char *options)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const unsigned_statistics *</type>
      <name>statistics</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>7251e795b0457abfd828d4f141038fc8</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_ars_prefix_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>61014af68a3e62e3fee1a473515b27bf</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>ars_prefix_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>4f68f055271af416ec665a90173a3e6c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_ars_cycle_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>f8d364ef84d0299f7b2e6294b500b274</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>ars_cycle_states</name>
      <anchorfile>classspot_1_1ars__statistics.html</anchorfile>
      <anchor>cbf2b59ea92ca6951ad3ea62c3c6f3d5</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>get</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>1d8bcb95613cbc5da8ccc91bcced1dff</anchor>
      <arglist>(const char *str) const</arglist>
    </member>
    <member kind="variable">
      <type>stats_map</type>
      <name>stats</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>9696e48357820edfaa12abdc1961eadb</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>accepting_cycle</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>acf9472e522e742957e29d29453f64a7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>options_updated</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>3ed3330145ca9d988eeca2e5bf773dad</anchor>
      <arglist>(const option_map &amp;old)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>87ce44800ef5cb67e181f186f3d3dcbc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1emptiness__check__result.html</anchorfile>
      <anchor>42a0ad82518ab92ce6ea2166c119e4cc</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const couvreur99_check_status *</type>
      <name>ecs_</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>0c5ed8e0b96439e4d94ffea6a84b52c2</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>tgba_run *</type>
      <name>run_</name>
      <anchorfile>classspot_1_1couvreur99__check__result.html</anchorfile>
      <anchor>98d5f7f8d9138fb76803ae4c9d814add</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::explicit_connected_component</name>
    <filename>classspot_1_1explicit__connected__component.html</filename>
    <base>spot::scc_stack::connected_component</base>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~explicit_connected_component</name>
      <anchorfile>classspot_1_1explicit__connected__component.html</anchorfile>
      <anchor>ec2aaed565e8a4eaba49e6752e03851c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>has_state</name>
      <anchorfile>classspot_1_1explicit__connected__component.html</anchorfile>
      <anchor>9d956a044f4c5e8014d869345dd00c8f</anchor>
      <arglist>(const state *s) const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1explicit__connected__component.html</anchorfile>
      <anchor>65ca837e0460d52c9346244fb7c98fe2</anchor>
      <arglist>(const state *s)=0</arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>index</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>58d4077c0bb19d470764be5e79a9adf0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>condition</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>a753864e5e55807ac67794a9999873b9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>std::list&lt; const state * &gt;</type>
      <name>rem</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>205e9ecf4993038b597838eafd5102cb</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::connected_component_hash_set</name>
    <filename>classspot_1_1connected__component__hash__set.html</filename>
    <base>spot::explicit_connected_component</base>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~connected_component_hash_set</name>
      <anchorfile>classspot_1_1connected__component__hash__set.html</anchorfile>
      <anchor>fc8bcedf6b7f5e5cd66c97d66b5b8e07</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>has_state</name>
      <anchorfile>classspot_1_1connected__component__hash__set.html</anchorfile>
      <anchor>23e1d00a8c16031162a70d504aa93958</anchor>
      <arglist>(const state *s) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1connected__component__hash__set.html</anchorfile>
      <anchor>f8544e2de3bddac0dfa923a3788a5fd0</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>index</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>58d4077c0bb19d470764be5e79a9adf0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>condition</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>a753864e5e55807ac67794a9999873b9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>std::list&lt; const state * &gt;</type>
      <name>rem</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>205e9ecf4993038b597838eafd5102cb</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_set&lt; const state *, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>set_type</name>
      <anchorfile>classspot_1_1connected__component__hash__set.html</anchorfile>
      <anchor>397565e28b3561653570fd15224abc2d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>set_type</type>
      <name>states</name>
      <anchorfile>classspot_1_1connected__component__hash__set.html</anchorfile>
      <anchor>75fb2a089b75ce89f7b4277952e14ab7</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::explicit_connected_component_factory</name>
    <filename>classspot_1_1explicit__connected__component__factory.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~explicit_connected_component_factory</name>
      <anchorfile>classspot_1_1explicit__connected__component__factory.html</anchorfile>
      <anchor>dc372f3d1bf138af5d621d136bb1084e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual explicit_connected_component *</type>
      <name>build</name>
      <anchorfile>classspot_1_1explicit__connected__component__factory.html</anchorfile>
      <anchor>7be87eaea65135f4f3bc0f64c9c9dc35</anchor>
      <arglist>() const=0</arglist>
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
      <anchor>597cfd7a3e3853f972f969c5edc0b4a2</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const connected_component_hash_set_factory *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1connected__component__hash__set__factory.html</anchorfile>
      <anchor>202e65c89fe1b43c762f5d65bce82ac7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~connected_component_hash_set_factory</name>
      <anchorfile>classspot_1_1connected__component__hash__set__factory.html</anchorfile>
      <anchor>809313e8ec726cd35e401a9d904ec47e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>connected_component_hash_set_factory</name>
      <anchorfile>classspot_1_1connected__component__hash__set__factory.html</anchorfile>
      <anchor>a29f42e74183d72b27d2652975bc93f4</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::couvreur99_check</name>
    <filename>classspot_1_1couvreur99__check.html</filename>
    <base>spot::emptiness_check</base>
    <base>spot::ec_statistics</base>
    <member kind="typedef">
      <type>unsigned(unsigned_statistics::*)</type>
      <name>unsigned_fun</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>001095ee22b6f1af39b6f3b21878f225</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; const char *, unsigned_fun, char_ptr_less_than &gt;</type>
      <name>stats_map</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>3e8a1630d3c2ca9ca25d5a50438387d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>couvreur99_check</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>5567070e76d7e35363f49589d515c736</anchor>
      <arglist>(const tgba *a, option_map o=option_map(), const numbered_state_heap_factory *nshf=numbered_state_heap_hash_map_factory::instance())</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~couvreur99_check</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>f159127474608bb53042ff1344241c4e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual emptiness_check_result *</type>
      <name>check</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>cd2571ab75300c53ea1b9f5945c6bbcc</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::ostream &amp;</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>316074b40cd2520d4c132f8227610d9c</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function">
      <type>const couvreur99_check_status *</type>
      <name>result</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>a9c5f91ca88aedd3a9c96e8281ac727f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>d4d716c54bda588b39ac6f3b643047cd</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>c7b3a1c1e2adc0923b2eea39fbb766ec</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>d0e61dab3b4593d493da86ee625a25c3</anchor>
      <arglist>(char *options)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>safe</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>7a3c73585e7fcea20a643db2209f8020</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const unsigned_statistics *</type>
      <name>statistics</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>df35e35484e15ff56cbb1ffbcbd2cc7c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>options_updated</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>ac525dce9e038b424a1f680b797e2639</anchor>
      <arglist>(const option_map &amp;old)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set_states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>81d1156d35135a920f17d22765e07724</anchor>
      <arglist>(unsigned n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>779baacabc1e56f583dd07ab9cc693af</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_transitions</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>36673e3fb88feefbb1097b5cadd01327</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>de5ca49fc87a2bc9ce9414fc3a96c56b</anchor>
      <arglist>(unsigned n=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>dec_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>cb78038e2e0be1a3d5761cf5b5bdcae9</anchor>
      <arglist>(unsigned n=1)</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>969cdceb81f3886a8687b9b52d9b2304</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>transitions</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>0be440094298131338a7f9a0d9e5e12e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>max_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>9225baed03ca66e56573917d12f8dd1e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>5556318b7a1b194365c4a92049b3d08d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>get</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>1d8bcb95613cbc5da8ccc91bcced1dff</anchor>
      <arglist>(const char *str) const</arglist>
    </member>
    <member kind="variable">
      <type>stats_map</type>
      <name>stats</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>9696e48357820edfaa12abdc1961eadb</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove_component</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>3b48d6451f133c57c7d62b8946d6339b</anchor>
      <arglist>(const state *start_delete)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>get_removed_components</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>db0c536b43f620a2392f5709665d8429</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>get_vmsize</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>5d27e95c69fbc8c1a773db02cd668bd8</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>couvreur99_check_status *</type>
      <name>ecs_</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>3e87c0bdcab6b6958d725eb67ee6c436</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>poprem_</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>13a1e31c34889d7a97cea7c7a0097416</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>unsigned</type>
      <name>removed_components</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>fef1267ab1a6dbb03961e88cc718da42</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>315fb003ae56a13d26dc8ffb03a34ed6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>2bf27940474ed0e6ea39a6f8b6c7fcc0</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::couvreur99_check_shy</name>
    <filename>classspot_1_1couvreur99__check__shy.html</filename>
    <base>spot::couvreur99_check</base>
    <member kind="typedef">
      <type>unsigned(unsigned_statistics::*)</type>
      <name>unsigned_fun</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>001095ee22b6f1af39b6f3b21878f225</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="typedef">
      <type>std::map&lt; const char *, unsigned_fun, char_ptr_less_than &gt;</type>
      <name>stats_map</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>3e8a1630d3c2ca9ca25d5a50438387d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>couvreur99_check_shy</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>73a4502883a3db87e7835d2b42acfb81</anchor>
      <arglist>(const tgba *a, option_map o=option_map(), const numbered_state_heap_factory *nshf=numbered_state_heap_hash_map_factory::instance())</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~couvreur99_check_shy</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>5f821ff9fcc7c3058eb2de9fe10d3eea</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual emptiness_check_result *</type>
      <name>check</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>46acfebce7513b47f8d6d98f42dcefb9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::ostream &amp;</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>316074b40cd2520d4c132f8227610d9c</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function">
      <type>const couvreur99_check_status *</type>
      <name>result</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>a9c5f91ca88aedd3a9c96e8281ac727f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>d4d716c54bda588b39ac6f3b643047cd</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const option_map &amp;</type>
      <name>options</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>c7b3a1c1e2adc0923b2eea39fbb766ec</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>d0e61dab3b4593d493da86ee625a25c3</anchor>
      <arglist>(char *options)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>safe</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>7a3c73585e7fcea20a643db2209f8020</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const unsigned_statistics *</type>
      <name>statistics</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>df35e35484e15ff56cbb1ffbcbd2cc7c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>options_updated</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>ac525dce9e038b424a1f680b797e2639</anchor>
      <arglist>(const option_map &amp;old)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>set_states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>81d1156d35135a920f17d22765e07724</anchor>
      <arglist>(unsigned n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>779baacabc1e56f583dd07ab9cc693af</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_transitions</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>36673e3fb88feefbb1097b5cadd01327</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>inc_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>de5ca49fc87a2bc9ce9414fc3a96c56b</anchor>
      <arglist>(unsigned n=1)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>dec_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>cb78038e2e0be1a3d5761cf5b5bdcae9</anchor>
      <arglist>(unsigned n=1)</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>states</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>969cdceb81f3886a8687b9b52d9b2304</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>transitions</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>0be440094298131338a7f9a0d9e5e12e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>max_depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>9225baed03ca66e56573917d12f8dd1e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>depth</name>
      <anchorfile>classspot_1_1ec__statistics.html</anchorfile>
      <anchor>5556318b7a1b194365c4a92049b3d08d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>get</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>1d8bcb95613cbc5da8ccc91bcced1dff</anchor>
      <arglist>(const char *str) const</arglist>
    </member>
    <member kind="variable">
      <type>stats_map</type>
      <name>stats</name>
      <anchorfile>structspot_1_1unsigned__statistics.html</anchorfile>
      <anchor>9696e48357820edfaa12abdc1961eadb</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::list&lt; successor &gt;</type>
      <name>succ_queue</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>17e8361583eeceb540d7b2d8dd5ba098</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::list&lt; todo_item &gt;</type>
      <name>todo_list</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>575a5df7a1e6ff2d0f92f455d41c84a6</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>clear_todo</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>aa12e9662fbe6a5766b0748a7916cb2b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual numbered_state_heap::state_index_p</type>
      <name>find_state</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>1e9cf243327a7f1c4f1034bf61735638</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>remove_component</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>3b48d6451f133c57c7d62b8946d6339b</anchor>
      <arglist>(const state *start_delete)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>get_removed_components</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>db0c536b43f620a2392f5709665d8429</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>get_vmsize</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>5d27e95c69fbc8c1a773db02cd668bd8</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::stack&lt; bdd &gt;</type>
      <name>arc</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>ad1a65d3d9fff5804f173edcb04c1dd0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>num</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>f10ac30976ec671be89d559f0c7f362b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>succ_queue::iterator</type>
      <name>pos</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>6fa9998c57b217e4f90719c7b235693d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>todo_list</type>
      <name>todo</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>ae1d99f0235e147d78a9ca009683b7f4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>group_</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>112c0863e8f602e5dddcc222d4250b7a</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>group2_</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>fe55f697d80c24009d2630a56afc49aa</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>onepass_</name>
      <anchorfile>classspot_1_1couvreur99__check__shy.html</anchorfile>
      <anchor>65fda5f5b5850a47bda92941e948899a</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>couvreur99_check_status *</type>
      <name>ecs_</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>3e87c0bdcab6b6958d725eb67ee6c436</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>poprem_</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>13a1e31c34889d7a97cea7c7a0097416</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>unsigned</type>
      <name>removed_components</name>
      <anchorfile>classspot_1_1couvreur99__check.html</anchorfile>
      <anchor>fef1267ab1a6dbb03961e88cc718da42</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>a_</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>315fb003ae56a13d26dc8ffb03a34ed6</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>option_map</type>
      <name>o_</name>
      <anchorfile>classspot_1_1emptiness__check.html</anchorfile>
      <anchor>2bf27940474ed0e6ea39a6f8b6c7fcc0</anchor>
      <arglist></arglist>
    </member>
    <class kind="struct">spot::couvreur99_check_shy::successor</class>
    <class kind="struct">spot::couvreur99_check_shy::todo_item</class>
  </compound>
  <compound kind="struct">
    <name>spot::couvreur99_check_shy::successor</name>
    <filename>structspot_1_1couvreur99__check__shy_1_1successor.html</filename>
    <member kind="function">
      <type></type>
      <name>successor</name>
      <anchorfile>structspot_1_1couvreur99__check__shy_1_1successor.html</anchorfile>
      <anchor>980b5139f8a00ab0e20e45a4ed57b7c8</anchor>
      <arglist>(bdd acc, const spot::state *s)</arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>acc</name>
      <anchorfile>structspot_1_1couvreur99__check__shy_1_1successor.html</anchorfile>
      <anchor>1bdb54bd31ef8bd124207ad116ca09a0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>const spot::state *</type>
      <name>s</name>
      <anchorfile>structspot_1_1couvreur99__check__shy_1_1successor.html</anchorfile>
      <anchor>ae3d2574a4d7411d94b7ec0dec6ecd9b</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::couvreur99_check_shy::todo_item</name>
    <filename>structspot_1_1couvreur99__check__shy_1_1todo__item.html</filename>
    <member kind="function">
      <type></type>
      <name>todo_item</name>
      <anchorfile>structspot_1_1couvreur99__check__shy_1_1todo__item.html</anchorfile>
      <anchor>37fd3fd565dd2c653d365bac62ff44ab</anchor>
      <arglist>(const state *s, int n, couvreur99_check_shy *shy)</arglist>
    </member>
    <member kind="variable">
      <type>const state *</type>
      <name>s</name>
      <anchorfile>structspot_1_1couvreur99__check__shy_1_1todo__item.html</anchorfile>
      <anchor>c23ec08e4d15168ea8034453c3d6831a</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>n</name>
      <anchorfile>structspot_1_1couvreur99__check__shy_1_1todo__item.html</anchorfile>
      <anchor>a6e0e40f38e4b153cd89313b80dd480c</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>succ_queue</type>
      <name>q</name>
      <anchorfile>structspot_1_1couvreur99__check__shy_1_1todo__item.html</anchorfile>
      <anchor>19f7249eac349d73ddc9bf50a3e0ab31</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap_const_iterator</name>
    <filename>classspot_1_1numbered__state__heap__const__iterator.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~numbered_state_heap_const_iterator</name>
      <anchorfile>classspot_1_1numbered__state__heap__const__iterator.html</anchorfile>
      <anchor>84bf9e6b4d2897d26d720d66a48300a3</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>first</name>
      <anchorfile>classspot_1_1numbered__state__heap__const__iterator.html</anchorfile>
      <anchor>10ddb31c77032b460509153e2fc8c1fe</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>next</name>
      <anchorfile>classspot_1_1numbered__state__heap__const__iterator.html</anchorfile>
      <anchor>290efd2641628a740c2d7e52b0e98bb3</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual bool</type>
      <name>done</name>
      <anchorfile>classspot_1_1numbered__state__heap__const__iterator.html</anchorfile>
      <anchor>787855baa3e5d318b6f61efb617f4743</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>get_state</name>
      <anchorfile>classspot_1_1numbered__state__heap__const__iterator.html</anchorfile>
      <anchor>d7a9a84f3651e5a01cd682c873affab7</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual int</type>
      <name>get_index</name>
      <anchorfile>classspot_1_1numbered__state__heap__const__iterator.html</anchorfile>
      <anchor>c830034cc3d3024876ab2e6f6259136c</anchor>
      <arglist>() const=0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap</name>
    <filename>classspot_1_1numbered__state__heap.html</filename>
    <member kind="typedef">
      <type>std::pair&lt; const state *, int * &gt;</type>
      <name>state_index_p</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>cc0b4cf8fc60a89e267d08a4d45b1c4a</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; const state *, int &gt;</type>
      <name>state_index</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>c39af7af085a760d16a34d0e2f9e18d5</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~numbered_state_heap</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>988d52f967e803673e1766e135b90354</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>e1190a20a23bf805c00a9a030c1fa44c</anchor>
      <arglist>(const state *s, int index)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual int</type>
      <name>size</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>3381b1d8e8c7310065c65dabe3c20207</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual numbered_state_heap_const_iterator *</type>
      <name>iterator</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>e311f648b031155454b698111ca4d944</anchor>
      <arglist>() const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state_index</type>
      <name>find</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>ddc6004d68c99785b4cf9bc7f8524bed</anchor>
      <arglist>(const state *s) const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state_index_p</type>
      <name>find</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>7eb4c58a703a6db54bfbe83e50bf802f</anchor>
      <arglist>(const state *s)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state_index</type>
      <name>index</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>77ef6501e67bd9567776caabba2b1369</anchor>
      <arglist>(const state *s) const=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual state_index_p</type>
      <name>index</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>33d6e875baca0507397a813646006219</anchor>
      <arglist>(const state *s)=0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap_factory</name>
    <filename>classspot_1_1numbered__state__heap__factory.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~numbered_state_heap_factory</name>
      <anchorfile>classspot_1_1numbered__state__heap__factory.html</anchorfile>
      <anchor>580c1ad5560eab27671f0feefb5259ba</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual numbered_state_heap *</type>
      <name>build</name>
      <anchorfile>classspot_1_1numbered__state__heap__factory.html</anchorfile>
      <anchor>c11943d790f24a0b363aa369011b0db1</anchor>
      <arglist>() const=0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap_hash_map</name>
    <filename>classspot_1_1numbered__state__heap__hash__map.html</filename>
    <base>spot::numbered_state_heap</base>
    <member kind="typedef">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>hash_type</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>30660d5f9c5d6154ba47a1548ecbf141</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; const state *, int * &gt;</type>
      <name>state_index_p</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>cc0b4cf8fc60a89e267d08a4d45b1c4a</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::pair&lt; const state *, int &gt;</type>
      <name>state_index</name>
      <anchorfile>classspot_1_1numbered__state__heap.html</anchorfile>
      <anchor>c39af7af085a760d16a34d0e2f9e18d5</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~numbered_state_heap_hash_map</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>4b37a85f45f12c38389a405ed2f33de3</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_index</type>
      <name>find</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>b71ae41a54dcb4f86d06e6a3d773008c</anchor>
      <arglist>(const state *s) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_index_p</type>
      <name>find</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>2b4a5b5e344fe76a1422def88c8130ff</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_index</type>
      <name>index</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>a0d0f384fe26da224c93090e2edc3e9d</anchor>
      <arglist>(const state *s) const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual state_index_p</type>
      <name>index</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>eff788dc2b943bd0d4ec1144fc5bd2c8</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>insert</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>cf08fbf1f29d09fdc8883768feb625f9</anchor>
      <arglist>(const state *s, int index)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual int</type>
      <name>size</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>90fa3b9e550e02538f7a6b1dedf9cb8b</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual numbered_state_heap_const_iterator *</type>
      <name>iterator</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>6ae0fffca77b09658a1bb042f11c3083</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>hash_type</type>
      <name>h</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map.html</anchorfile>
      <anchor>fb219195433667abe491130fa3acd6a3</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::numbered_state_heap_hash_map_factory</name>
    <filename>classspot_1_1numbered__state__heap__hash__map__factory.html</filename>
    <base>spot::numbered_state_heap_factory</base>
    <member kind="function" virtualness="virtual">
      <type>virtual numbered_state_heap_hash_map *</type>
      <name>build</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map__factory.html</anchorfile>
      <anchor>cc6320995fc2ce8135dc5209870dbfe4</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static const numbered_state_heap_hash_map_factory *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map__factory.html</anchorfile>
      <anchor>c9ff2d24e71f763cbd999e09be6aa518</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~numbered_state_heap_hash_map_factory</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map__factory.html</anchorfile>
      <anchor>eca03181988d66d7301dd25b98ee3f23</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>numbered_state_heap_hash_map_factory</name>
      <anchorfile>classspot_1_1numbered__state__heap__hash__map__factory.html</anchorfile>
      <anchor>830f29fa2401b4cbf4168e7e9db0ed0d</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::scc_stack</name>
    <filename>classspot_1_1scc__stack.html</filename>
    <member kind="typedef">
      <type>std::list&lt; connected_component &gt;</type>
      <name>stack_type</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>7919ffabb7b1b072c94d2db0a5157382</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>push</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>b41e8e1aad7d4ca3940f1eaf8064a0c2</anchor>
      <arglist>(int index)</arglist>
    </member>
    <member kind="function">
      <type>connected_component &amp;</type>
      <name>top</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>9fb956e08441d13f89442ac99974b6af</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const connected_component &amp;</type>
      <name>top</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>e292047175933e23bf070a99dcacc87d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>pop</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>2da83367ca277053968ff856f3c6295b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>size_t</type>
      <name>size</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>e5d667566109542f89fdc33df1ecabf1</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>std::list&lt; const state * &gt; &amp;</type>
      <name>rem</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>74f1cf725431e87096f4823bc0d19bcf</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>clear_rem</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>939ef574064e26b22df1a48d3e52cf27</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>empty</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>a40608448b82e4479478d3e1318cd22a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable">
      <type>stack_type</type>
      <name>s</name>
      <anchorfile>classspot_1_1scc__stack.html</anchorfile>
      <anchor>c384c36b7776955a5f3f77e7abd0ee0b</anchor>
      <arglist></arglist>
    </member>
    <class kind="struct">spot::scc_stack::connected_component</class>
  </compound>
  <compound kind="struct">
    <name>spot::scc_stack::connected_component</name>
    <filename>structspot_1_1scc__stack_1_1connected__component.html</filename>
    <member kind="function">
      <type></type>
      <name>connected_component</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>b7e02d26855cba8b9b912848750cccbe</anchor>
      <arglist>(int index=-1)</arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>index</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>58d4077c0bb19d470764be5e79a9adf0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bdd</type>
      <name>condition</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>a753864e5e55807ac67794a9999873b9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>std::list&lt; const state * &gt;</type>
      <name>rem</name>
      <anchorfile>structspot_1_1scc__stack_1_1connected__component.html</anchorfile>
      <anchor>205e9ecf4993038b597838eafd5102cb</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::couvreur99_check_status</name>
    <filename>classspot_1_1couvreur99__check__status.html</filename>
    <member kind="function">
      <type></type>
      <name>couvreur99_check_status</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>002d7a25b4cbe893d02f93cdde9b3217</anchor>
      <arglist>(const tgba *aut, const numbered_state_heap_factory *nshf)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~couvreur99_check_status</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>cc8253ea69979bbc16d9e3b7cec9f1e2</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>print_stats</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>29764d152123348aa00b9ed5d4c9b6c8</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>states</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>1ee25c07baee0ba59f9c0feada0a461b</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable">
      <type>const tgba *</type>
      <name>aut</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>c4db05ebb3edd2176e40406a0d0ef306</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>scc_stack</type>
      <name>root</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>6bf17d3301e2c753c6259600f83963c3</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>numbered_state_heap *</type>
      <name>h</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>015a6b109e569b398bd4c3f18dec4060</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>const state *</type>
      <name>cycle_seed</name>
      <anchorfile>classspot_1_1couvreur99__check__status.html</anchorfile>
      <anchor>7e476db66078f99415a0ba62956cec3c</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_reachable_iterator</name>
    <filename>classspot_1_1tgba__reachable__iterator.html</filename>
    <member kind="function">
      <type></type>
      <name>tgba_reachable_iterator</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>71307833df5755d4af5528d64fe3fdb3</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_reachable_iterator</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>082dc58180e304e6eb9531b45738dc68</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aefeca520f9e39c86018d284ff1aa4ce</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>958d443339cdc7da2b6da976186cf299</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a6bef3072977a131257e206221b63d89</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6b38d281157866c5bae8806c4b7cd8fd</anchor>
      <arglist>(const state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6214cd7eb3295c93fae10a800b4635f7</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>21afe78fce17e6620f911a3ba7effbdb</anchor>
      <arglist>(const state *s)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>045c244be5739955e1e851beee4f5dca</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>914620c536c096c6bde20b92811315d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>7fccc175964d8c8d481552443313a319</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>33fbcfee3eb7694451f2b0760ee79de1</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_reachable_iterator_depth_first</name>
    <filename>classspot_1_1tgba__reachable__iterator__depth__first.html</filename>
    <base>spot::tgba_reachable_iterator</base>
    <member kind="function">
      <type></type>
      <name>tgba_reachable_iterator_depth_first</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>a10418a272b92adb47e4e87497f4eb7c</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>eb354c7f1a448f49278afdb51e314d82</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>b2d7298c30ef62ca9c42a619cae20d08</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aefeca520f9e39c86018d284ff1aa4ce</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>958d443339cdc7da2b6da976186cf299</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a6bef3072977a131257e206221b63d89</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6b38d281157866c5bae8806c4b7cd8fd</anchor>
      <arglist>(const state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6214cd7eb3295c93fae10a800b4635f7</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>914620c536c096c6bde20b92811315d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::stack&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__depth__first.html</anchorfile>
      <anchor>c7a1289d2adf2367e0b3d1de14188f7e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>7fccc175964d8c8d481552443313a319</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>33fbcfee3eb7694451f2b0760ee79de1</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_reachable_iterator_breadth_first</name>
    <filename>classspot_1_1tgba__reachable__iterator__breadth__first.html</filename>
    <base>spot::tgba_reachable_iterator</base>
    <member kind="function">
      <type></type>
      <name>tgba_reachable_iterator_breadth_first</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>4c87d902aae981f84e77b9336a132d29</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>962348317141a53df5bc92086071fd8c</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>c368fb5a611b2509c33f9deb03a7ccc6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aefeca520f9e39c86018d284ff1aa4ce</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>start</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>958d443339cdc7da2b6da976186cf299</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>end</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>a6bef3072977a131257e206221b63d89</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6b38d281157866c5bae8806c4b7cd8fd</anchor>
      <arglist>(const state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6214cd7eb3295c93fae10a800b4635f7</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>914620c536c096c6bde20b92811315d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>b591b269fe4c8cbc61769c5309255b46</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>7fccc175964d8c8d481552443313a319</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>33fbcfee3eb7694451f2b0760ee79de1</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::parity_game_graph</name>
    <filename>classspot_1_1parity__game__graph.html</filename>
    <base>spot::tgba_reachable_iterator_breadth_first</base>
    <member kind="function">
      <type></type>
      <name>parity_game_graph</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>e9f217fbccff73208fe29a7287bdaa17</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~parity_game_graph</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>b22f876d210cf2d34319389ded2fa0f0</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual simulation_relation *</type>
      <name>get_relation</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>7352a9293a62b6388d43ef801a783dd2</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>print</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>e33b6645711104a6e2aa1698495626ac</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>962348317141a53df5bc92086071fd8c</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>c368fb5a611b2509c33f9deb03a7ccc6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aefeca520f9e39c86018d284ff1aa4ce</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6214cd7eb3295c93fae10a800b4635f7</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>914620c536c096c6bde20b92811315d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>start</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>630a69d823710152ae5a544e7f240226</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>end</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>492ef0dd5bc389a26af36901de72f583</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>b975699bc3acb3888f926b77c198e5e0</anchor>
      <arglist>(const state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>3256db15e516c6c1939545b580d3d6fa</anchor>
      <arglist>(int in, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual void</type>
      <name>build_graph</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>08331d34915c4f4601c5dcb96d28c9bf</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="pure">
      <type>virtual void</type>
      <name>lift</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>692ba9041369c018d011bed6a875458a</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v</type>
      <name>spoiler_vertice_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>2f481e4132cd8b01e6d9f47e2a1be976</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>dn_v</type>
      <name>duplicator_vertice_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>ec5ee9782f98f7e7fcf1030f8b1b3df9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>s_v</type>
      <name>tgba_state_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>7d2f76f1b15f9c3c40846bbec8b4bb95</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>nb_node_parity_game</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>7509b4ec83fe1e8c670242f0a8328eb9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>b591b269fe4c8cbc61769c5309255b46</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>7fccc175964d8c8d481552443313a319</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>33fbcfee3eb7694451f2b0760ee79de1</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::spoiler_node</name>
    <filename>classspot_1_1spoiler__node.html</filename>
    <member kind="function">
      <type></type>
      <name>spoiler_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7b4a2ff04712dec65aa1d8f98e190120</anchor>
      <arglist>(const state *d_node, const state *s_node, int num)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~spoiler_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>130c5da8f4d4c1f8b157fb27f4c0a05a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>add_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7d1fa13e8baaf5744358473ff2b5ac36</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>del_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>99c6382be3b5c4959e78d0a37819c502</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>a74ce0d4f143efa1ea0b06fc76cf6988</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>del_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>35239b34eed091ab9013e09f9fe5916d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get_nb_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>752b4f4440bee20059d838e3a2eb3dba</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>prune</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>acca6f6a2176f2bb473aa81252b8b237</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>set_win</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>9ac1e099d9cd2000bf716536972e2116</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>to_string</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>9bdf2ec657d2fe9a4faab392e226db01</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>succ_to_string</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>674041f3b256741dfac59133684cf8a6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>compare</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>161936933eb56d82e352fb4215c9a015</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_spoiler_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>62ca6b4fed6f9976c5f62deb5fce1d55</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_duplicator_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>24ece9ce82801fa1e51f07ef4ec98a89</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>state_couple *</type>
      <name>get_pair</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7935a6d8bd096f8e3836857e865caad5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable">
      <type>bool</type>
      <name>not_win</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>6245f82f7b535be70b3f2ddd4bf62d31</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>num_</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>0b80414f19130b0f187fd07ad8e02344</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v *</type>
      <name>lnode_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>ff47c073465fb5e8765581ca8122651e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v *</type>
      <name>lnode_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>cfc2402b055252c1f83bfb444814f216</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>state_couple *</type>
      <name>sc_</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>e5c57a32b47e53e4c4f6fc81628dcb2f</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::duplicator_node</name>
    <filename>classspot_1_1duplicator__node.html</filename>
    <base>spot::spoiler_node</base>
    <member kind="function">
      <type></type>
      <name>duplicator_node</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>16ce97d2b3bc864feca8be1e21161cb9</anchor>
      <arglist>(const state *d_node, const state *s_node, bdd l, bdd a, int num)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~duplicator_node</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>6700b2e10b36e549c9583bdc8e807546</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>set_win</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>58d3b3fa55e9fb5fbe4ffd660c30cdd9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>to_string</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>3a4417993f2a7b816041a8ac3f4d1da0</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>compare</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>fa03eec072da0505f9597c33bc9e7950</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>match</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>4b5a82cdd3f4a2a621ffb14eca927b1c</anchor>
      <arglist>(bdd l, bdd a)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>implies</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>dd4545a0881086e4d5570be2127a2e01</anchor>
      <arglist>(bdd l, bdd a)</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>get_label</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>78e85fdb84b20870ad7fa2f7920afcf7</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>get_acc</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>437d5e489e60ea324ffffcb12efae0e3</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>add_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7d1fa13e8baaf5744358473ff2b5ac36</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>del_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>99c6382be3b5c4959e78d0a37819c502</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>a74ce0d4f143efa1ea0b06fc76cf6988</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>del_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>35239b34eed091ab9013e09f9fe5916d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get_nb_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>752b4f4440bee20059d838e3a2eb3dba</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>prune</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>acca6f6a2176f2bb473aa81252b8b237</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>succ_to_string</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>674041f3b256741dfac59133684cf8a6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_spoiler_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>62ca6b4fed6f9976c5f62deb5fce1d55</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_duplicator_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>24ece9ce82801fa1e51f07ef4ec98a89</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>state_couple *</type>
      <name>get_pair</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7935a6d8bd096f8e3836857e865caad5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable">
      <type>bool</type>
      <name>not_win</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>6245f82f7b535be70b3f2ddd4bf62d31</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>num_</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>0b80414f19130b0f187fd07ad8e02344</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>label_</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>9394559f86428a7631e6cce91e46840b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>acc_</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>cc076976d448b933ef60137c63053202</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v *</type>
      <name>lnode_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>ff47c073465fb5e8765581ca8122651e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v *</type>
      <name>lnode_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>cfc2402b055252c1f83bfb444814f216</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>state_couple *</type>
      <name>sc_</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>e5c57a32b47e53e4c4f6fc81628dcb2f</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::parity_game_graph_direct</name>
    <filename>classspot_1_1parity__game__graph__direct.html</filename>
    <base>spot::parity_game_graph</base>
    <member kind="function">
      <type></type>
      <name>parity_game_graph_direct</name>
      <anchorfile>classspot_1_1parity__game__graph__direct.html</anchorfile>
      <anchor>ebd1cedf161a9b3736a92859d84476f4</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~parity_game_graph_direct</name>
      <anchorfile>classspot_1_1parity__game__graph__direct.html</anchorfile>
      <anchor>cb6335723cacae6fc100a126c4c6f181</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual direct_simulation_relation *</type>
      <name>get_relation</name>
      <anchorfile>classspot_1_1parity__game__graph__direct.html</anchorfile>
      <anchor>b72dc02fae3f74494b012b0f65981599</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>print</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>e33b6645711104a6e2aa1698495626ac</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6214cd7eb3295c93fae10a800b4635f7</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>962348317141a53df5bc92086071fd8c</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>c368fb5a611b2509c33f9deb03a7ccc6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aefeca520f9e39c86018d284ff1aa4ce</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>914620c536c096c6bde20b92811315d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>build_graph</name>
      <anchorfile>classspot_1_1parity__game__graph__direct.html</anchorfile>
      <anchor>557fb50902f96d985ba39e9e8ed98b3e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>lift</name>
      <anchorfile>classspot_1_1parity__game__graph__direct.html</anchorfile>
      <anchor>ea0c4335cbd9f7500055cfaf725d0b1d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>build_link</name>
      <anchorfile>classspot_1_1parity__game__graph__direct.html</anchorfile>
      <anchor>8a331d21b9fdd2b4bc606ae41e8c5f9b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>start</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>630a69d823710152ae5a544e7f240226</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>end</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>492ef0dd5bc389a26af36901de72f583</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>b975699bc3acb3888f926b77c198e5e0</anchor>
      <arglist>(const state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>3256db15e516c6c1939545b580d3d6fa</anchor>
      <arglist>(int in, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v</type>
      <name>spoiler_vertice_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>2f481e4132cd8b01e6d9f47e2a1be976</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>dn_v</type>
      <name>duplicator_vertice_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>ec5ee9782f98f7e7fcf1030f8b1b3df9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>s_v</type>
      <name>tgba_state_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>7d2f76f1b15f9c3c40846bbec8b4bb95</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>nb_node_parity_game</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>7509b4ec83fe1e8c670242f0a8328eb9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>b591b269fe4c8cbc61769c5309255b46</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>7fccc175964d8c8d481552443313a319</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>33fbcfee3eb7694451f2b0760ee79de1</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::spoiler_node_delayed</name>
    <filename>classspot_1_1spoiler__node__delayed.html</filename>
    <base>spot::spoiler_node</base>
    <member kind="function">
      <type></type>
      <name>spoiler_node_delayed</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>d1a68f3a61720ae4d9aeef6891870ecc</anchor>
      <arglist>(const state *d_node, const state *s_node, bdd a, int num)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~spoiler_node_delayed</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>031f83870da33ef0a81895e075b8edfb</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>set_win</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>fe2fca836b5dfa52b57dbe066f5f8aaf</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>get_acceptance_condition_visited</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>24d999af7ab5f7448ec20ec36ab718b6</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>compare</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>3af269255b311f9646a944693f74b4f5</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>to_string</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>2f9f4dc950b068edbbc9860c782105d5</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get_progress_measure</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>936f3f6da0e516aadee828c024cd13c8</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>get_lead_2_acc_all</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>afc4bf2865bb1f2182244b6b084c1433</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>set_lead_2_acc_all</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>c2d26696bfadf86c57584f49bb38fc8b</anchor>
      <arglist>(bdd acc=bddfalse)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>add_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7d1fa13e8baaf5744358473ff2b5ac36</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>del_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>99c6382be3b5c4959e78d0a37819c502</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>a74ce0d4f143efa1ea0b06fc76cf6988</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>del_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>35239b34eed091ab9013e09f9fe5916d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get_nb_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>752b4f4440bee20059d838e3a2eb3dba</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>prune</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>acca6f6a2176f2bb473aa81252b8b237</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>succ_to_string</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>674041f3b256741dfac59133684cf8a6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_spoiler_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>62ca6b4fed6f9976c5f62deb5fce1d55</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_duplicator_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>24ece9ce82801fa1e51f07ef4ec98a89</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>state_couple *</type>
      <name>get_pair</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7935a6d8bd096f8e3836857e865caad5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable">
      <type>bool</type>
      <name>seen_</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>d1907a33e11bbc330de7d81966a529a8</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bool</type>
      <name>not_win</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>6245f82f7b535be70b3f2ddd4bf62d31</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>num_</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>0b80414f19130b0f187fd07ad8e02344</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>acceptance_condition_visited_</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>fc8a54dd866a0bdd2814180142540e14</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>progress_measure_</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>398b260513d2052a0d110793f86f988b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>lead_2_acc_all_</name>
      <anchorfile>classspot_1_1spoiler__node__delayed.html</anchorfile>
      <anchor>6019e1adf9649d9e88aa9df902f10eda</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v *</type>
      <name>lnode_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>ff47c073465fb5e8765581ca8122651e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v *</type>
      <name>lnode_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>cfc2402b055252c1f83bfb444814f216</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>state_couple *</type>
      <name>sc_</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>e5c57a32b47e53e4c4f6fc81628dcb2f</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::duplicator_node_delayed</name>
    <filename>classspot_1_1duplicator__node__delayed.html</filename>
    <base>spot::duplicator_node</base>
    <member kind="function">
      <type></type>
      <name>duplicator_node_delayed</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>4ca7c1dba37d8e271c54533c82bbf46c</anchor>
      <arglist>(const state *d_node, const state *s_node, bdd l, bdd a, int num)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~duplicator_node_delayed</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>6bc51cd5bb6d442932c4709cc643a315</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>set_win</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>62a39e7a20359b35a744852a84a5103d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>to_string</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>da599414ef7fff70275625eadf0883ff</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>implies_label</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>291cea525fb2621c8002377a7471c185</anchor>
      <arglist>(bdd l)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>implies_acc</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>d4ddeddfcde8d67ced68af3ce0bc04f8</anchor>
      <arglist>(bdd a)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get_progress_measure</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>f887c893e8511cea8311cbdf274ef4f1</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>get_lead_2_acc_all</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>4d0d1f6570511424026211b1d9829c11</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>set_lead_2_acc_all</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>898cdaab3e849ab31c9327704fd6af39</anchor>
      <arglist>(bdd acc=bddfalse)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual bool</type>
      <name>compare</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>fa03eec072da0505f9597c33bc9e7950</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>match</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>4b5a82cdd3f4a2a621ffb14eca927b1c</anchor>
      <arglist>(bdd l, bdd a)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>implies</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>dd4545a0881086e4d5570be2127a2e01</anchor>
      <arglist>(bdd l, bdd a)</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>get_label</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>78e85fdb84b20870ad7fa2f7920afcf7</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>get_acc</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>437d5e489e60ea324ffffcb12efae0e3</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>add_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7d1fa13e8baaf5744358473ff2b5ac36</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>del_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>99c6382be3b5c4959e78d0a37819c502</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>a74ce0d4f143efa1ea0b06fc76cf6988</anchor>
      <arglist>(spoiler_node *n)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>del_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>35239b34eed091ab9013e09f9fe5916d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get_nb_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>752b4f4440bee20059d838e3a2eb3dba</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>prune</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>acca6f6a2176f2bb473aa81252b8b237</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>succ_to_string</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>674041f3b256741dfac59133684cf8a6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_spoiler_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>62ca6b4fed6f9976c5f62deb5fce1d55</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const state *</type>
      <name>get_duplicator_node</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>24ece9ce82801fa1e51f07ef4ec98a89</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>state_couple *</type>
      <name>get_pair</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>7935a6d8bd096f8e3836857e865caad5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable">
      <type>bool</type>
      <name>seen_</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>39344f694ad3dc230a9db2af4ce87343</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>bool</type>
      <name>not_win</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>6245f82f7b535be70b3f2ddd4bf62d31</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>num_</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>0b80414f19130b0f187fd07ad8e02344</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>progress_measure_</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>e698057c6d12e5f4529b67ee9a4f433d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>lead_2_acc_all_</name>
      <anchorfile>classspot_1_1duplicator__node__delayed.html</anchorfile>
      <anchor>971965c948c5bbad607a2fc198c67d13</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>label_</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>9394559f86428a7631e6cce91e46840b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd</type>
      <name>acc_</name>
      <anchorfile>classspot_1_1duplicator__node.html</anchorfile>
      <anchor>cc076976d448b933ef60137c63053202</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v *</type>
      <name>lnode_succ</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>ff47c073465fb5e8765581ca8122651e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v *</type>
      <name>lnode_pred</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>cfc2402b055252c1f83bfb444814f216</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>state_couple *</type>
      <name>sc_</name>
      <anchorfile>classspot_1_1spoiler__node.html</anchorfile>
      <anchor>e5c57a32b47e53e4c4f6fc81628dcb2f</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::parity_game_graph_delayed</name>
    <filename>classspot_1_1parity__game__graph__delayed.html</filename>
    <base>spot::parity_game_graph</base>
    <member kind="function">
      <type></type>
      <name>parity_game_graph_delayed</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>e62948ec7e73c837ac2f0bd1415a9d6a</anchor>
      <arglist>(const tgba *a)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~parity_game_graph_delayed</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>62e513a46584841473f08baa715c921e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual delayed_simulation_relation *</type>
      <name>get_relation</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>837beca49e960298d0b2ed6d17eb29da</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>print</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>e33b6645711104a6e2aa1698495626ac</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>6214cd7eb3295c93fae10a800b4635f7</anchor>
      <arglist>(const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>add_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>962348317141a53df5bc92086071fd8c</anchor>
      <arglist>(const state *s)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const state *</type>
      <name>next_state</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>c368fb5a611b2509c33f9deb03a7ccc6</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>run</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>aefeca520f9e39c86018d284ff1aa4ce</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>Sgi::hash_map&lt; const state *, int, state_ptr_hash, state_ptr_equal &gt;</type>
      <name>seen_map</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>914620c536c096c6bde20b92811315d9</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>start</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>630a69d823710152ae5a544e7f240226</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>end</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>492ef0dd5bc389a26af36901de72f583</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>process_state</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>b975699bc3acb3888f926b77c198e5e0</anchor>
      <arglist>(const state *s, int n, tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>process_link</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>3256db15e516c6c1939545b580d3d6fa</anchor>
      <arglist>(int in, int out, const tgba_succ_iterator *si)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>sn_v</type>
      <name>spoiler_vertice_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>2f481e4132cd8b01e6d9f47e2a1be976</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>dn_v</type>
      <name>duplicator_vertice_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>ec5ee9782f98f7e7fcf1030f8b1b3df9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>s_v</type>
      <name>tgba_state_</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>7d2f76f1b15f9c3c40846bbec8b4bb95</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>int</type>
      <name>nb_node_parity_game</name>
      <anchorfile>classspot_1_1parity__game__graph.html</anchorfile>
      <anchor>7509b4ec83fe1e8c670242f0a8328eb9</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::deque&lt; const state * &gt;</type>
      <name>todo</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator__breadth__first.html</anchorfile>
      <anchor>b591b269fe4c8cbc61769c5309255b46</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>const tgba *</type>
      <name>automata_</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>7fccc175964d8c8d481552443313a319</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>seen_map</type>
      <name>seen</name>
      <anchorfile>classspot_1_1tgba__reachable__iterator.html</anchorfile>
      <anchor>33fbcfee3eb7694451f2b0760ee79de1</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>std::vector&lt; bdd &gt;</type>
      <name>bdd_v</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>21fb8e084c8495d3d19189cb5ca41dc6</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private">
      <type>int</type>
      <name>nb_set_acc_cond</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>a659bf9a22b860eb4add5cfa642a13fe</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="private">
      <type>duplicator_node_delayed *</type>
      <name>add_duplicator_node_delayed</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>ceb3cc90d72f96908a67bf7f578d4d48</anchor>
      <arglist>(const spot::state *sn, const spot::state *dn, bdd acc, bdd label, int nb)</arglist>
    </member>
    <member kind="function" protection="private">
      <type>spoiler_node_delayed *</type>
      <name>add_spoiler_node_delayed</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>103e42994a79fb9949bdbfb5c23f87aa</anchor>
      <arglist>(const spot::state *sn, const spot::state *dn, bdd acc, int nb)</arglist>
    </member>
    <member kind="function" protection="private">
      <type>void</type>
      <name>build_recurse_successor_spoiler</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>d319da43c9ea1f1bc3ec2b08a8533722</anchor>
      <arglist>(spoiler_node *sn, std::ostringstream &amp;os)</arglist>
    </member>
    <member kind="function" protection="private">
      <type>void</type>
      <name>build_recurse_successor_duplicator</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>5629ad8a4f120bfd5e9825ef1d394fe9</anchor>
      <arglist>(duplicator_node *dn, spoiler_node *sn, std::ostringstream &amp;os)</arglist>
    </member>
    <member kind="function" protection="private" virtualness="virtual">
      <type>virtual void</type>
      <name>build_graph</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>a3267ddcaa77068c5fd9b811bfb610d9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="private" virtualness="virtual">
      <type>virtual void</type>
      <name>lift</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>7251ed6aa48022be4a109b46ba1cd475</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd_v</type>
      <name>sub_set_acc_cond_</name>
      <anchorfile>classspot_1_1parity__game__graph__delayed.html</anchorfile>
      <anchor>12733aa07e291109c1b24a2b16de0058</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::tgba_run_dotty_decorator</name>
    <filename>classspot_1_1tgba__run__dotty__decorator.html</filename>
    <base>spot::dotty_decorator</base>
    <member kind="function">
      <type></type>
      <name>tgba_run_dotty_decorator</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>c225adfcddc76c0cb109cdd6209e25b4</anchor>
      <arglist>(const tgba_run *run)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~tgba_run_dotty_decorator</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>061c9488698b789f22673a703b2c65c8</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>state_decl</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>0bd524289d70985fdebac241f8127276</anchor>
      <arglist>(const tgba *a, const state *s, int n, tgba_succ_iterator *si, const std::string &amp;label)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual std::string</type>
      <name>link_decl</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>d350e1e551b6cc9ecab570b6d3d59bf8</anchor>
      <arglist>(const tgba *a, const state *in_s, int in, const state *out_s, int out, const tgba_succ_iterator *si, const std::string &amp;label)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static dotty_decorator *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1dotty__decorator.html</anchorfile>
      <anchor>3f65ca9c0c1a37d7aba531c243f52c6e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>std::pair&lt; tgba_run::steps::const_iterator, int &gt;</type>
      <name>step_num</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>bca4e3af5b46c5f1f26dff21ba5a4045</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>std::list&lt; step_num &gt;</type>
      <name>step_set</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>d6d84a3f76628096c736b5e75c488cc0</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>std::map&lt; const state *, std::pair&lt; step_set, step_set &gt;, spot::state_ptr_less_than &gt;</type>
      <name>step_map</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>7e396d304340ec45bfc8efaa6daffa76</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const tgba_run *</type>
      <name>run_</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>e64df61811600525e3deebbc9926737f</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>step_map</type>
      <name>map_</name>
      <anchorfile>classspot_1_1tgba__run__dotty__decorator.html</anchorfile>
      <anchor>7138e9522fbcb6ca469926ac0a7b0b9f</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::tgba_statistics</name>
    <filename>structspot_1_1tgba__statistics.html</filename>
    <member kind="variable">
      <type>unsigned</type>
      <name>transitions</name>
      <anchorfile>structspot_1_1tgba__statistics.html</anchorfile>
      <anchor>1f65ce745d9e9bb6d81ea61f9bace9b1</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>unsigned</type>
      <name>states</name>
      <anchorfile>structspot_1_1tgba__statistics.html</anchorfile>
      <anchor>6f789b90914f70873393efc5dca88e53</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::weight</name>
    <filename>classspot_1_1weight.html</filename>
    <member kind="function">
      <type></type>
      <name>weight</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>8ec48103bbf49e3362dba6c3a43ec9ca</anchor>
      <arglist>(const bdd &amp;neg_all_cond)</arglist>
    </member>
    <member kind="function">
      <type>weight &amp;</type>
      <name>operator+=</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>06d51ff55f29297828cccb306700d7f0</anchor>
      <arglist>(const bdd &amp;acc)</arglist>
    </member>
    <member kind="function">
      <type>weight &amp;</type>
      <name>operator-=</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>7eca972118d15fc6c951bcf422676b75</anchor>
      <arglist>(const bdd &amp;acc)</arglist>
    </member>
    <member kind="function">
      <type>bdd</type>
      <name>operator-</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>b7cbd9813f13acc9c783934a0fdf1170</anchor>
      <arglist>(const weight &amp;w) const</arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>std::map&lt; int, int &gt;</type>
      <name>weight_vector</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>dd7a3a5521c457495ee82ac845019ac7</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="private" static="yes">
      <type>static void</type>
      <name>inc_weight_handler</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>39e8672571c340287b28251340327318</anchor>
      <arglist>(char *varset, int size)</arglist>
    </member>
    <member kind="function" protection="private" static="yes">
      <type>static void</type>
      <name>dec_weight_handler</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>4f105303719bd2ab4d88d1ae29a704dc</anchor>
      <arglist>(char *varset, int size)</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>weight_vector</type>
      <name>m</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>ca7ddf550b6b8ee3df88416838568f91</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd</type>
      <name>neg_all_acc</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>bfe7971d6d67b81ad2aca6c97b9b374f</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private" static="yes">
      <type>static weight_vector *</type>
      <name>pm</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>1da581ec507a702be3f1f9dfc1395a3b</anchor>
      <arglist></arglist>
    </member>
    <member kind="friend">
      <type>friend std::ostream &amp;</type>
      <name>operator&lt;&lt;</name>
      <anchorfile>classspot_1_1weight.html</anchorfile>
      <anchor>5cfdc8dedccf19d15b2fce8c75cf868a</anchor>
      <arglist>(std::ostream &amp;os, const weight &amp;w)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::gspn_exception</name>
    <filename>classspot_1_1gspn__exception.html</filename>
    <member kind="function">
      <type></type>
      <name>gspn_exception</name>
      <anchorfile>classspot_1_1gspn__exception.html</anchorfile>
      <anchor>e1ef4e3b5c03c87d7d6bf01cf5c23f65</anchor>
      <arglist>(const std::string &amp;where, int err)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>get_err</name>
      <anchorfile>classspot_1_1gspn__exception.html</anchorfile>
      <anchor>89c7377232c40813596c2977f167479e</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>get_where</name>
      <anchorfile>classspot_1_1gspn__exception.html</anchorfile>
      <anchor>f8431afe4f569f5c589c59c3bd780f99</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>int</type>
      <name>err_</name>
      <anchorfile>classspot_1_1gspn__exception.html</anchorfile>
      <anchor>a573103d82b05f7124870d49a173f83c</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>std::string</type>
      <name>where_</name>
      <anchorfile>classspot_1_1gspn__exception.html</anchorfile>
      <anchor>b539155ee271d6d02261517766968df5</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::gspn_interface</name>
    <filename>classspot_1_1gspn__interface.html</filename>
    <member kind="function">
      <type></type>
      <name>gspn_interface</name>
      <anchorfile>classspot_1_1gspn__interface.html</anchorfile>
      <anchor>3f504fcfdb56d5852e8cd5c984e99c59</anchor>
      <arglist>(int argc, char **argv, bdd_dict *dict, ltl::declarative_environment &amp;env, const std::string &amp;dead=&quot;true&quot;)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~gspn_interface</name>
      <anchorfile>classspot_1_1gspn__interface.html</anchorfile>
      <anchor>6e692261f65e49bde240afc51800ead2</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1gspn__interface.html</anchorfile>
      <anchor>19c8d31515fb9aee7632d57b0d10e568</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd_dict *</type>
      <name>dict_</name>
      <anchorfile>classspot_1_1gspn__interface.html</anchorfile>
      <anchor>8a3c7a717be4930ec163481276928147</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>ltl::declarative_environment &amp;</type>
      <name>env_</name>
      <anchorfile>classspot_1_1gspn__interface.html</anchorfile>
      <anchor>33c2f439f811f77fee8ecaa03d3063b4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const std::string</type>
      <name>dead_</name>
      <anchorfile>classspot_1_1gspn__interface.html</anchorfile>
      <anchor>b8010a8f917077a05b0b31646a10d222</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::gspn_ssp_interface</name>
    <filename>classspot_1_1gspn__ssp__interface.html</filename>
    <member kind="function">
      <type></type>
      <name>gspn_ssp_interface</name>
      <anchorfile>classspot_1_1gspn__ssp__interface.html</anchorfile>
      <anchor>8f082936dbea3d5a5a859f60e9c9f843</anchor>
      <arglist>(int argc, char **argv, bdd_dict *dict, const ltl::declarative_environment &amp;env, bool inclusion=false, bool doublehash=true)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~gspn_ssp_interface</name>
      <anchorfile>classspot_1_1gspn__ssp__interface.html</anchorfile>
      <anchor>80cc1869602527d252cd6edf37518827</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>tgba *</type>
      <name>automaton</name>
      <anchorfile>classspot_1_1gspn__ssp__interface.html</anchorfile>
      <anchor>8aec366a85d2eb8daa08ff9c93e36eef</anchor>
      <arglist>(const tgba *operand) const </arglist>
    </member>
    <member kind="variable" protection="private">
      <type>bdd_dict *</type>
      <name>dict_</name>
      <anchorfile>classspot_1_1gspn__ssp__interface.html</anchorfile>
      <anchor>96c14a3b951283db8022e4af767ae31e</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const ltl::declarative_environment &amp;</type>
      <name>env_</name>
      <anchorfile>classspot_1_1gspn__ssp__interface.html</anchorfile>
      <anchor>554f835a25b16430b19e6a56c82cd823</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="namespace">
    <name>spot::ltl</name>
    <filename>namespacespot_1_1ltl.html</filename>
    <class kind="class">spot::ltl::atomic_prop</class>
    <class kind="class">spot::ltl::binop</class>
    <class kind="class">spot::ltl::constant</class>
    <class kind="class">spot::ltl::formula</class>
    <class kind="struct">spot::ltl::formula_ptr_less_than</class>
    <class kind="struct">spot::ltl::formula_ptr_hash</class>
    <class kind="class">spot::ltl::multop</class>
    <class kind="class">spot::ltl::ref_formula</class>
    <class kind="class">spot::ltl::unop</class>
    <class kind="struct">spot::ltl::visitor</class>
    <class kind="struct">spot::ltl::const_visitor</class>
    <class kind="class">spot::ltl::declarative_environment</class>
    <class kind="class">spot::ltl::default_environment</class>
    <class kind="class">spot::ltl::environment</class>
    <class kind="class">spot::ltl::read_only_environment</class>
    <class kind="class">spot::ltl::clone_visitor</class>
    <class kind="class">spot::ltl::language_containment_checker</class>
    <class kind="class">spot::ltl::unabbreviate_logic_visitor</class>
    <class kind="class">spot::ltl::postfix_visitor</class>
    <class kind="class">spot::ltl::random_ltl</class>
    <class kind="class">spot::ltl::simplify_f_g_visitor</class>
    <class kind="class">spot::ltl::unabbreviate_ltl_visitor</class>
    <member kind="typedef">
      <type>std::pair&lt; ltlyy::location, std::string &gt;</type>
      <name>parse_error</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g9bf19c38b4ae7d74e6a9633ed360c147</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::list&lt; parse_error &gt;</type>
      <name>parse_error_list</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g9eb0f7867a212f92b0fd64a6ac5a12cd</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::set&lt; atomic_prop *, formula_ptr_less_than &gt;</type>
      <name>atomic_prop_set</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g305ebfb1906e717fc70cfba0fa14b4b9</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumeration">
      <name>reduce_options</name>
      <anchor>gc9e66395d0e9cb870fa7b1ca208b70ca</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_None</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70cabff3607cc02f12d6756d0244a8f5464a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Basics</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70cab83ef042ab620af2f258a817e95f8f80</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Syntactic_Implications</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca22d75bbadb5b030981574ae49668ad94</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Eventuality_And_Universality</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70caabb627af73b5817a542506be482f396d</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Containment_Checks</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca22286d57705e7511f13a75c05ac0a39f</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_Containment_Checks_Stronger</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca0721d15d048b11cfe234f14850dbc9c5</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Reduce_All</name>
      <anchor>ggc9e66395d0e9cb870fa7b1ca208b70ca1629bc689540d42e2f86eea77a6cd275</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>parse</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g64770999ec846fe07292163e33509da5</anchor>
      <arglist>(const std::string &amp;ltl_string, parse_error_list &amp;error_list, environment &amp;env=default_environment::instance(), bool debug=false)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>format_parse_errors</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gc69f09c520acfe742653158946413faf</anchor>
      <arglist>(std::ostream &amp;os, const std::string &amp;ltl_string, parse_error_list &amp;error_list)</arglist>
    </member>
    <member kind="function">
      <type>atomic_prop_set *</type>
      <name>atomic_prop_collect</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g335fb11ef18d07a729b03ee76719ead4</anchor>
      <arglist>(const formula *f, atomic_prop_set *s=0)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>basic_reduce</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>g0f4e7d16383675691de1722ee00388a2</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_GF</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g937576e148ca0cea8678e2e35f95d80c</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_FG</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gbd383d0bbdfa61428df282309cadddc6</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>clone</name>
      <anchorfile>group__ltl__essential.html</anchorfile>
      <anchor>g8e017c7cfd3dcd47b3cc1074371da6bc</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>reduce_tau03</name>
      <anchorfile>namespacespot_1_1ltl.html</anchorfile>
      <anchor>16bd0bb215f85d6aed821ecbca7e36de</anchor>
      <arglist>(const formula *f, bool stronger=true)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>destroy</name>
      <anchorfile>group__ltl__essential.html</anchorfile>
      <anchor>g7dfba082e4a6aca346befcc46f87e358</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dotty</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g7e27d31b2209954c1d57d3b8b5224473</anchor>
      <arglist>(std::ostream &amp;os, const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gaf316635f1320fa38885fa89e23e2098</anchor>
      <arglist>(std::ostream &amp;os, const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>int</type>
      <name>length</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gbf324b4e946522d1b4caf3ce003ec903</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>unabbreviate_logic</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>ge5f253667eed8184ea82a34db7ae2f71</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>negative_normal_form</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>gfe4e6a149b451249b6c8bf03dedc5eeb</anchor>
      <arglist>(const formula *f, bool negated=false)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>reduce</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>g31729856de4be685ad913e4e8da59344</anchor>
      <arglist>(const formula *f, int opt=Reduce_All)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_eventual</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g3210a9b433640efe1ef74e0da6c678e5</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>is_universal</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g10c79e8a8c5b58fa77cbb6dab5857083</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>simplify_f_g</name>
      <anchorfile>group__ltl__rewriting.html</anchorfile>
      <anchor>gee8b3f409c756decaa12345b2bac6091</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>syntactic_implication</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>g0117add821f085e941eada4aa2ce4bf9</anchor>
      <arglist>(const formula *f1, const formula *f2)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>syntactic_implication_neg</name>
      <anchorfile>group__ltl__misc.html</anchorfile>
      <anchor>gd190a68d649650c2b608829dcf258ea1</anchor>
      <arglist>(const formula *f1, const formula *f2, bool right)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>to_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gda7ab7261e386edb04ba2949b1a83210</anchor>
      <arglist>(const formula *f, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>to_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>g49f14e7d6937a9ed58173e6af1080592</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>to_spin_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gc80486e726928b415b2da6c41eabf02b</anchor>
      <arglist>(const formula *f, std::ostream &amp;os)</arglist>
    </member>
    <member kind="function">
      <type>std::string</type>
      <name>to_spin_string</name>
      <anchorfile>group__ltl__io.html</anchorfile>
      <anchor>gcc70e63a877976973682279353031407</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>unabbreviate_ltl</name>
      <anchorfile>namespacespot_1_1ltl.html</anchorfile>
      <anchor>dcb3082ccb3a689482acbe76c25c3840</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::atomic_prop</name>
    <filename>classspot_1_1ltl_1_1atomic__prop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>be10f90a296a14205a041db361f2bbd5</anchor>
      <arglist>(visitor &amp;visitor)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>0790b2f3854f827d73f9f3e4ca9b8106</anchor>
      <arglist>(const_visitor &amp;visitor) const</arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>eab0dcb1692b086853d8ea8355b8e82a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>environment &amp;</type>
      <name>env</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>b224450a438a83bab27d1f5688257938</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>ref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9ffd2ea0476ff6c21c5e15a635558950</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>875af3ab4f48361f5ab0ab757d6e034f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>3147fea52d3d463a12997434f5ca406f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static atomic_prop *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>f8ad2798fad4b4ee871ab712e7c7e421</anchor>
      <arglist>(const std::string &amp;name, environment &amp;env)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>7f519f7edb2766b90e28edc1e28d6917</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static std::ostream &amp;</type>
      <name>dump_instances</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>7aa747d99e75f74ad2bc0d886647e61c</anchor>
      <arglist>(std::ostream &amp;os)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>unref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>afbf68193084bcf22d95627f131c86ab</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; std::string, environment * &gt;</type>
      <name>pair</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>28a571f2664ae32b40af1ccabcf2ce72</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::map&lt; pair, atomic_prop * &gt;</type>
      <name>map</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>927e2a5811dc62f78973348c24341b0f</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>atomic_prop</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>02310138eed43b1639665f5b30b1939a</anchor>
      <arglist>(const std::string &amp;name, environment &amp;env)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~atomic_prop</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>1bea4a6631d6e77ac0575ea3fbe2164e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>f6ec5b9ad70411f6c5af0a740516872b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>e62a1b6f0142d643c003a9eec2de8151</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>ref_count_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>3519cc90d5db1faf5bf6cfc02924f6da</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>set_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>032c84a304fceb86f44090e561699501</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::string</type>
      <name>dump_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9b9dfaa3e12136cdbcab6be273553d5b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>size_t</type>
      <name>hash_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>4dce0db9773af2d2082af85a6445ecb0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected" static="yes">
      <type>static map</type>
      <name>instances</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>df723d974658c5ededcf19955402c210</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>std::string</type>
      <name>name_</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>5abe1df4d378de9d57f42d59c7954a66</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>environment *</type>
      <name>env_</name>
      <anchorfile>classspot_1_1ltl_1_1atomic__prop.html</anchorfile>
      <anchor>6057413492eb4c968f8edef08b15c8bb</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::binop</name>
    <filename>classspot_1_1ltl_1_1binop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <member kind="enumeration">
      <name>type</name>
      <anchor>7c5967c6908151a90ff72f210bfb59a2</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Xor</name>
      <anchor>7c5967c6908151a90ff72f210bfb59a271177fc6c4bfbc11a0fd7acceeed1ed5</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Implies</name>
      <anchor>7c5967c6908151a90ff72f210bfb59a2c5d2a034f0dd62b98fe785d0372c0c9a</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Equiv</name>
      <anchor>7c5967c6908151a90ff72f210bfb59a20177c3febeabb4808b46226565b8df22</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>U</name>
      <anchor>7c5967c6908151a90ff72f210bfb59a2b01994b5e43401a5cf70fc7ef1599119</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>R</name>
      <anchor>7c5967c6908151a90ff72f210bfb59a22912f480a149c4899b9f61f1a5975c38</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>dbc5c7397eb200bc0ef20028a00b8af4</anchor>
      <arglist>(visitor &amp;v)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>5d031fe98a76316a362b8685ce2efa06</anchor>
      <arglist>(const_visitor &amp;v) const</arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>first</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>5addf7525b02fa1bcdd707787fd5e71a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>first</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>82122b0cdb730331934e37df5cfe73e2</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>second</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>49cc7c12023e607fac531f86f2e1c77c</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>second</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>768565083f9d381d3d767238a6a60950</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>op</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>8d24ac43b23863c2f21cf6d3df348245</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>op_name</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>a477a2c5cbc78d11cc09365297bbb13d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>ref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9ffd2ea0476ff6c21c5e15a635558950</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>875af3ab4f48361f5ab0ab757d6e034f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>3147fea52d3d463a12997434f5ca406f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static binop *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>ee86c3f1dc8cc436365e27200f0dc269</anchor>
      <arglist>(type op, formula *first, formula *second)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>e2374ced030c8da8cadf295f6fcac010</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>unref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>afbf68193084bcf22d95627f131c86ab</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; formula *, formula * &gt;</type>
      <name>pairf</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>df408c59d4c06144ff1272183f6f4f43</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; type, pairf &gt;</type>
      <name>pair</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>3484b43ead650bad7e0752b4efe803b1</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::map&lt; pair, formula * &gt;</type>
      <name>map</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>f47f4518d58532356e5da653c0872252</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>binop</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>8021dccc1fdebc70965892db5c2ef85f</anchor>
      <arglist>(type op, formula *first, formula *second)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~binop</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>26cdd7b747a37cb4e274d17d30f24fe8</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>f6ec5b9ad70411f6c5af0a740516872b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>e62a1b6f0142d643c003a9eec2de8151</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>ref_count_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>3519cc90d5db1faf5bf6cfc02924f6da</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>set_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>032c84a304fceb86f44090e561699501</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::string</type>
      <name>dump_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9b9dfaa3e12136cdbcab6be273553d5b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>size_t</type>
      <name>hash_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>4dce0db9773af2d2082af85a6445ecb0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected" static="yes">
      <type>static map</type>
      <name>instances</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>afdae9c944f5e8078d844b16f1afac79</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>type</type>
      <name>op_</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>9880620b2cebccb9e8f6c15f684af6c4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>formula *</type>
      <name>first_</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>7c82a2b204cfa503adfa5e4bab200eba</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>formula *</type>
      <name>second_</name>
      <anchorfile>classspot_1_1ltl_1_1binop.html</anchorfile>
      <anchor>38196c91daf86c61a7e38a8ab024eaeb</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::constant</name>
    <filename>classspot_1_1ltl_1_1constant.html</filename>
    <base>spot::ltl::formula</base>
    <member kind="enumeration">
      <name>type</name>
      <anchor>3a23c9193ea7c7513518bfbb38b02833</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>False</name>
      <anchor>3a23c9193ea7c7513518bfbb38b02833ef70d3cc109f761554b5e9cb23798236</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>True</name>
      <anchor>3a23c9193ea7c7513518bfbb38b028338a0b88d378578f5b5900fb67221cd592</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>675e0f2dbaade2bf83fb4e6502ed3dba</anchor>
      <arglist>(visitor &amp;v)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>d727271ef71817b8d2ccba460e20c8ef</anchor>
      <arglist>(const_visitor &amp;v) const</arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>val</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>2a0de4a4fb6afc9447cc246610cf78ef</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>val_name</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>a704a6f4f75eef7b15c57fe4e6e485ad</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>ref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9ffd2ea0476ff6c21c5e15a635558950</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>875af3ab4f48361f5ab0ab757d6e034f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>3147fea52d3d463a12997434f5ca406f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static constant *</type>
      <name>true_instance</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>343dd7a20e2df06851836a6bd7fc1b90</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static constant *</type>
      <name>false_instance</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>0ddc280986f98b088800542d8012b6f2</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>unref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>afbf68193084bcf22d95627f131c86ab</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>constant</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>83153ebfd9015aec95c9881e0f77fbc5</anchor>
      <arglist>(type val)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~constant</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>a3211b7e05643d59c4850edc67ded15a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>494a539e4bcb790d6ceec3948ab4fb02</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ec84949753feb9485d1cdfb32c75df80</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>set_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>032c84a304fceb86f44090e561699501</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::string</type>
      <name>dump_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9b9dfaa3e12136cdbcab6be273553d5b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>size_t</type>
      <name>hash_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>4dce0db9773af2d2082af85a6445ecb0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>type</type>
      <name>val_</name>
      <anchorfile>classspot_1_1ltl_1_1constant.html</anchorfile>
      <anchor>1a18193f8242096e6e87b1ec815d2d14</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::formula</name>
    <filename>classspot_1_1ltl_1_1formula.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>74df9cd120a778c441d304f19636b7e9</anchor>
      <arglist>(visitor &amp;v)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>1a92ec345c89d1eb526ee8fe5b97db4d</anchor>
      <arglist>(const_visitor &amp;v) const=0</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>ref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9ffd2ea0476ff6c21c5e15a635558950</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>875af3ab4f48361f5ab0ab757d6e034f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>3147fea52d3d463a12997434f5ca406f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>unref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>afbf68193084bcf22d95627f131c86ab</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~formula</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>2b0d3cd1db7797aa154d3348d7cfa1c9</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>494a539e4bcb790d6ceec3948ab4fb02</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>ec84949753feb9485d1cdfb32c75df80</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>set_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>032c84a304fceb86f44090e561699501</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::string</type>
      <name>dump_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9b9dfaa3e12136cdbcab6be273553d5b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>size_t</type>
      <name>hash_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>4dce0db9773af2d2082af85a6445ecb0</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_ptr_less_than</name>
    <filename>structspot_1_1ltl_1_1formula__ptr__less__than.html</filename>
    <member kind="function">
      <type>bool</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1ltl_1_1formula__ptr__less__than.html</anchorfile>
      <anchor>6f38f7582164f5db76b187f34ad451a7</anchor>
      <arglist>(const formula *left, const formula *right) const</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::formula_ptr_hash</name>
    <filename>structspot_1_1ltl_1_1formula__ptr__hash.html</filename>
    <member kind="function">
      <type>size_t</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1ltl_1_1formula__ptr__hash.html</anchorfile>
      <anchor>9d98a406db6dde48e664f0c696b56b01</anchor>
      <arglist>(const formula *that) const </arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::multop</name>
    <filename>classspot_1_1ltl_1_1multop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <member kind="enumeration">
      <name>type</name>
      <anchor>7ff9fb342ce923eed135a23285d686fe</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Or</name>
      <anchor>7ff9fb342ce923eed135a23285d686feec9dacb72caa01e417f04a398770b16f</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>And</name>
      <anchor>7ff9fb342ce923eed135a23285d686fe134bbaa37c9a0db99c61af1f61b7f037</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef">
      <type>std::vector&lt; formula * &gt;</type>
      <name>vec</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>1a088b220b7736744c0408b4baa93999</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>6554d3e50ac3a1e8b626dde2869ac74b</anchor>
      <arglist>(visitor &amp;v)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>87a8e6d65edf56d44510cb119b5a6a03</anchor>
      <arglist>(const_visitor &amp;v) const</arglist>
    </member>
    <member kind="function">
      <type>unsigned</type>
      <name>size</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>a0a825959a87eab1d2ea17b5d56ce64a</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>nth</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>20c58b69464f7f55889400aa51186594</anchor>
      <arglist>(unsigned n) const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>nth</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>47f7a6d6dc1bbde26286022ce9233fe0</anchor>
      <arglist>(unsigned n)</arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>op</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>5347afbd7ac972c4d0f0d2a425f8b550</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>op_name</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>666c2f53f7c65f140fe490429dcd8ec3</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>ref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9ffd2ea0476ff6c21c5e15a635558950</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>875af3ab4f48361f5ab0ab757d6e034f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>3147fea52d3d463a12997434f5ca406f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static formula *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>23890ba510df288bea354551723fcd9b</anchor>
      <arglist>(type op, formula *first, formula *second)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static formula *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>699f14f2f5c48c20a3e8d9151de0e46c</anchor>
      <arglist>(type op, vec *v)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>07737a4f338b029504fe22d3dc44e342</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>unref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>afbf68193084bcf22d95627f131c86ab</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; type, vec * &gt;</type>
      <name>pair</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>cdc8c90654790dbc39a5024a7f341390</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::map&lt; pair, formula *, paircmp &gt;</type>
      <name>map</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>36781d687211f17e054320358c326270</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>multop</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>589dc735daaaa15b658a5be1d3bbd37c</anchor>
      <arglist>(type op, vec *v)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~multop</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>84f15109e2961f1fd1e84c460233580c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>f6ec5b9ad70411f6c5af0a740516872b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>e62a1b6f0142d643c003a9eec2de8151</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>ref_count_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>3519cc90d5db1faf5bf6cfc02924f6da</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>set_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>032c84a304fceb86f44090e561699501</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::string</type>
      <name>dump_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9b9dfaa3e12136cdbcab6be273553d5b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>size_t</type>
      <name>hash_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>4dce0db9773af2d2082af85a6445ecb0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected" static="yes">
      <type>static map</type>
      <name>instances</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>448f320d062b647d7c15ea8de870eb4d</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>type</type>
      <name>op_</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>6bd7bcbfae468e8e11bb7a2b5b9b4856</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>vec *</type>
      <name>children_</name>
      <anchorfile>classspot_1_1ltl_1_1multop.html</anchorfile>
      <anchor>6c6cc6d3a375ea04e5bfa076b0e7c1f1</anchor>
      <arglist></arglist>
    </member>
    <class kind="struct">spot::ltl::multop::paircmp</class>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::multop::paircmp</name>
    <filename>structspot_1_1ltl_1_1multop_1_1paircmp.html</filename>
    <member kind="function">
      <type>bool</type>
      <name>operator()</name>
      <anchorfile>structspot_1_1ltl_1_1multop_1_1paircmp.html</anchorfile>
      <anchor>17d9c6f50d9200c75c025a437bc314bb</anchor>
      <arglist>(const pair &amp;p1, const pair &amp;p2) const</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::ref_formula</name>
    <filename>classspot_1_1ltl_1_1ref__formula.html</filename>
    <base>spot::ltl::formula</base>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>74df9cd120a778c441d304f19636b7e9</anchor>
      <arglist>(visitor &amp;v)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>1a92ec345c89d1eb526ee8fe5b97db4d</anchor>
      <arglist>(const_visitor &amp;v) const=0</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>ref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9ffd2ea0476ff6c21c5e15a635558950</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>875af3ab4f48361f5ab0ab757d6e034f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>3147fea52d3d463a12997434f5ca406f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>unref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>afbf68193084bcf22d95627f131c86ab</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~ref_formula</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>f8b205c7e1f4a039189dda5a520fe861</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>ref_formula</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>2212a90c446841570b4ad87a458745cf</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>f6ec5b9ad70411f6c5af0a740516872b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>e62a1b6f0142d643c003a9eec2de8151</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>ref_count_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>3519cc90d5db1faf5bf6cfc02924f6da</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>set_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>032c84a304fceb86f44090e561699501</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::string</type>
      <name>dump_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9b9dfaa3e12136cdbcab6be273553d5b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>size_t</type>
      <name>hash_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>4dce0db9773af2d2082af85a6445ecb0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>unsigned</type>
      <name>ref_counter_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>9987e5fa9cd3542818521f84ac683d87</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::unop</name>
    <filename>classspot_1_1ltl_1_1unop.html</filename>
    <base>spot::ltl::ref_formula</base>
    <member kind="enumeration">
      <name>type</name>
      <anchor>b6cd495a942b8c203f547cccb50916bc</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>Not</name>
      <anchor>b6cd495a942b8c203f547cccb50916bc5c70279f9221ae04c99dfcfb1d8cce21</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>X</name>
      <anchor>b6cd495a942b8c203f547cccb50916bc710394e09b867640d569b71577db1a36</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>F</name>
      <anchor>b6cd495a942b8c203f547cccb50916bc22dc60c18bb1140957d534dab87a8e82</anchor>
      <arglist></arglist>
    </member>
    <member kind="enumvalue">
      <name>G</name>
      <anchor>b6cd495a942b8c203f547cccb50916bcfe9a76cee45f8e8d6f3d970f526090bb</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>fb60620274c1259ba8dee5ef2271c2de</anchor>
      <arglist>(visitor &amp;v)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>accept</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>0e00e1d3482a0c88ce44513054f36b28</anchor>
      <arglist>(const_visitor &amp;v) const</arglist>
    </member>
    <member kind="function">
      <type>const formula *</type>
      <name>child</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>7a4fca073623afc515483b45f4aeae6d</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>child</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>8a4d72aa890dd9182e7bf4225a2fb920</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>type</type>
      <name>op</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>8e5f9213744f97abdcab8e49ab5c2dfe</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>op_name</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>f437e14c953022488a0a08ce70d52531</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>ref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9ffd2ea0476ff6c21c5e15a635558950</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const std::string &amp;</type>
      <name>dump</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>875af3ab4f48361f5ab0ab757d6e034f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>const size_t</type>
      <name>hash</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>3147fea52d3d463a12997434f5ca406f</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unop *</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>f2fc027e151f8bddab5780d9f401665b</anchor>
      <arglist>(type op, formula *child)</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static unsigned</type>
      <name>instance_count</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>0c08b2cfc5767255a9096910b287fe42</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static void</type>
      <name>unref</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>afbf68193084bcf22d95627f131c86ab</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::pair&lt; type, formula * &gt;</type>
      <name>pair</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>0dd247b7319fa154b74275247d6025ca</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="protected">
      <type>std::map&lt; pair, formula * &gt;</type>
      <name>map</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>c9c1080d91b6a761400c39a766c9bfc3</anchor>
      <arglist></arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>unop</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>dc8e93cab8551cc7ea291813be6c2d2e</anchor>
      <arglist>(type op, formula *child)</arglist>
    </member>
    <member kind="function" protection="protected" virtualness="virtual">
      <type>virtual</type>
      <name>~unop</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>30927c2a32a1b733c19841589fb7705a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>ref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>f6ec5b9ad70411f6c5af0a740516872b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>unref_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>e62a1b6f0142d643c003a9eec2de8151</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>unsigned</type>
      <name>ref_count_</name>
      <anchorfile>classspot_1_1ltl_1_1ref__formula.html</anchorfile>
      <anchor>3519cc90d5db1faf5bf6cfc02924f6da</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>set_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>032c84a304fceb86f44090e561699501</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>std::string</type>
      <name>dump_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>9b9dfaa3e12136cdbcab6be273553d5b</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>size_t</type>
      <name>hash_key_</name>
      <anchorfile>classspot_1_1ltl_1_1formula.html</anchorfile>
      <anchor>4dce0db9773af2d2082af85a6445ecb0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected" static="yes">
      <type>static map</type>
      <name>instances</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>430de0a2f329f88fa4ff087a9ab95997</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>type</type>
      <name>op_</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>fbba940238b36a0b73a975f03ad90c56</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>formula *</type>
      <name>child_</name>
      <anchorfile>classspot_1_1ltl_1_1unop.html</anchorfile>
      <anchor>a84ae06796a583285c4e66e837457286</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::visitor</name>
    <filename>structspot_1_1ltl_1_1visitor.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~visitor</name>
      <anchorfile>structspot_1_1ltl_1_1visitor.html</anchorfile>
      <anchor>a41e3c3ecb2e90b636cf1d64fe61970a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1visitor.html</anchorfile>
      <anchor>bfa6b3a8d42cdac5f7a52daf98e7a7fb</anchor>
      <arglist>(atomic_prop *node)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1visitor.html</anchorfile>
      <anchor>c67c58b830c9e12cd1b0fb0f45a8cfcd</anchor>
      <arglist>(constant *node)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1visitor.html</anchorfile>
      <anchor>c2deeda57d565f735069a7de1df1dfa9</anchor>
      <arglist>(binop *node)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1visitor.html</anchorfile>
      <anchor>07b25aef75d011fe474b7dcdb3edb71c</anchor>
      <arglist>(unop *node)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1visitor.html</anchorfile>
      <anchor>0a7f123120507ac09433470ef4a35ef1</anchor>
      <arglist>(multop *node)=0</arglist>
    </member>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::const_visitor</name>
    <filename>structspot_1_1ltl_1_1const__visitor.html</filename>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~const_visitor</name>
      <anchorfile>structspot_1_1ltl_1_1const__visitor.html</anchorfile>
      <anchor>6bb2c2f258bbb3cb73e041f60888eb6d</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1const__visitor.html</anchorfile>
      <anchor>2e4225540b5fa1b0722b4d1437f77ecc</anchor>
      <arglist>(const atomic_prop *node)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1const__visitor.html</anchorfile>
      <anchor>23eb7191f7b1373fa6aa4552aebb02ab</anchor>
      <arglist>(const constant *node)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1const__visitor.html</anchorfile>
      <anchor>3ad17ed90e25013a6cae1576695466ae</anchor>
      <arglist>(const binop *node)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1const__visitor.html</anchorfile>
      <anchor>2e83d54d0f066f30e2a9acf818936f2a</anchor>
      <arglist>(const unop *node)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual void</type>
      <name>visit</name>
      <anchorfile>structspot_1_1ltl_1_1const__visitor.html</anchorfile>
      <anchor>73c45ac134e1b97515cfacd52a420cd0</anchor>
      <arglist>(const multop *node)=0</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::declarative_environment</name>
    <filename>classspot_1_1ltl_1_1declarative__environment.html</filename>
    <base>spot::ltl::environment</base>
    <member kind="typedef">
      <type>std::map&lt; const std::string, ltl::atomic_prop * &gt;</type>
      <name>prop_map</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>7ce69665ade0151e9a594bf57a7d115a</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>declarative_environment</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>beba4cd5a5dab75bd4959ee503b557c5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~declarative_environment</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>c3b81c3e7cf0f39de8e81fe015f8c0b7</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>declare</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>0ee488854ba5d0eb54accf9d08aebcaf</anchor>
      <arglist>(const std::string &amp;prop_str)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual ltl::formula *</type>
      <name>require</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>f48433cab1745d40c465c5ab6c07683e</anchor>
      <arglist>(const std::string &amp;prop_str)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>304df69a8a1201bc1aea4dd36b4d7b5c</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const prop_map &amp;</type>
      <name>get_prop_map</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>22759ddad2b9ef13ca04f4869b800ee7</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>prop_map</type>
      <name>props_</name>
      <anchorfile>classspot_1_1ltl_1_1declarative__environment.html</anchorfile>
      <anchor>56b8d03f1238eb4177f1808426b309ad</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::default_environment</name>
    <filename>classspot_1_1ltl_1_1default__environment.html</filename>
    <base>spot::ltl::environment</base>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~default_environment</name>
      <anchorfile>classspot_1_1ltl_1_1default__environment.html</anchorfile>
      <anchor>5eddb8ea1babd42d2d7874bc93788b26</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual formula *</type>
      <name>require</name>
      <anchorfile>classspot_1_1ltl_1_1default__environment.html</anchorfile>
      <anchor>dd21146b5f19443a54100e9bdc50e83d</anchor>
      <arglist>(const std::string &amp;prop_str)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1default__environment.html</anchorfile>
      <anchor>598bf78f41d23916e1d2c02fba636740</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" static="yes">
      <type>static default_environment &amp;</type>
      <name>instance</name>
      <anchorfile>classspot_1_1ltl_1_1default__environment.html</anchorfile>
      <anchor>6f82853f22c3bf57128a484a28a4d550</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" protection="protected">
      <type></type>
      <name>default_environment</name>
      <anchorfile>classspot_1_1ltl_1_1default__environment.html</anchorfile>
      <anchor>10d0a1d6fdea0d1c20d8a75b48ea673a</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::environment</name>
    <filename>classspot_1_1ltl_1_1environment.html</filename>
    <member kind="function" virtualness="pure">
      <type>virtual formula *</type>
      <name>require</name>
      <anchorfile>classspot_1_1ltl_1_1environment.html</anchorfile>
      <anchor>735b7e739bfa6a0e01ce4e6cc7069252</anchor>
      <arglist>(const std::string &amp;prop_str)=0</arglist>
    </member>
    <member kind="function" virtualness="pure">
      <type>virtual const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1environment.html</anchorfile>
      <anchor>52845adbef2d65748b4bba6287827270</anchor>
      <arglist>()=0</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~environment</name>
      <anchorfile>classspot_1_1ltl_1_1environment.html</anchorfile>
      <anchor>3b77feb83fd482fb7f299969234312c2</anchor>
      <arglist>()</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::read_only_environment</name>
    <filename>classspot_1_1ltl_1_1read__only__environment.html</filename>
    <base>spot::ltl::environment</base>
    <member kind="typedef">
      <type>std::map&lt; const std::string, ltl::atomic_prop * &gt;</type>
      <name>prop_map</name>
      <anchorfile>classspot_1_1ltl_1_1read__only__environment.html</anchorfile>
      <anchor>1443bb163752589ec28378aea10a99a1</anchor>
      <arglist></arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>declarative_environment</name>
      <anchorfile>classspot_1_1ltl_1_1read__only__environment.html</anchorfile>
      <anchor>9722b84ce6ed0039c132ac578a515569</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~declarative_environment</name>
      <anchorfile>classspot_1_1ltl_1_1read__only__environment.html</anchorfile>
      <anchor>2723ed40eb8693ed665767fe08ec59d5</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>declare</name>
      <anchorfile>classspot_1_1ltl_1_1read__only__environment.html</anchorfile>
      <anchor>8efb7f62755ff95704ffd1bd3509da52</anchor>
      <arglist>(const std::string &amp;prop_str)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual ltl::formula *</type>
      <name>require</name>
      <anchorfile>classspot_1_1ltl_1_1read__only__environment.html</anchorfile>
      <anchor>d1edfc3259fec1d6917da07fd1cf7f13</anchor>
      <arglist>(const std::string &amp;prop_str)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual const std::string &amp;</type>
      <name>name</name>
      <anchorfile>classspot_1_1ltl_1_1read__only__environment.html</anchorfile>
      <anchor>1fa21309220489d95a493f6d466f961e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>const prop_map &amp;</type>
      <name>get_prop_map</name>
      <anchorfile>classspot_1_1ltl_1_1read__only__environment.html</anchorfile>
      <anchor>4a3ff1f48f9cc15765a164115d2e4866</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>prop_map</type>
      <name>props_</name>
      <anchorfile>classspot_1_1ltl_1_1read__only__environment.html</anchorfile>
      <anchor>c0089d0bb0912006ceebbf5eb678d866</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::clone_visitor</name>
    <filename>classspot_1_1ltl_1_1clone__visitor.html</filename>
    <base>spot::ltl::visitor</base>
    <member kind="function">
      <type></type>
      <name>clone_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>cc6fc905996e481c2c0ab2943206db1f</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~clone_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>bf8deaf053b0db8434b68ad6a190c265</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>result</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>c5d144a3d5fd04460583a7288fd57713</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>818de31a16cd8f2108dc1805b355bf5e</anchor>
      <arglist>(atomic_prop *ap)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>3cb7109d821d0c45c517f852f5acb7b7</anchor>
      <arglist>(unop *uo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>534945fc3f5a8592505cf62c587f59c2</anchor>
      <arglist>(binop *bo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>e111a2b5b04069e1f31e7bdf97478122</anchor>
      <arglist>(multop *mo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>a918745a995a9b4d61f718bbf23e93be</anchor>
      <arglist>(constant *c)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual formula *</type>
      <name>recurse</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>43817c147675ee928824be710649ffa9</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>formula *</type>
      <name>result_</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>32a63ad362fc3c5fbf3973ef4c8d3712</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::language_containment_checker</name>
    <filename>classspot_1_1ltl_1_1language__containment__checker.html</filename>
    <member kind="function">
      <type></type>
      <name>language_containment_checker</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>f1d93e29c75d3c0b5f18aadbae7a1f08</anchor>
      <arglist>(bdd_dict *dict, bool exprop, bool symb_merge, bool branching_postponement, bool fair_loop_approx)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~language_containment_checker</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>dc77fb113f0ff8c0bca5941018b2992a</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>contained</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>41da10076c5996501f6c50a6bb8232df</anchor>
      <arglist>(const formula *l, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>neg_contained</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>bd13da68e9214781e803149d0a24540b</anchor>
      <arglist>(const formula *l, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>contained_neg</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>6c33553adb7b06fdb776b4545b2bdecd</anchor>
      <arglist>(const formula *l, const formula *g)</arglist>
    </member>
    <member kind="function">
      <type>bool</type>
      <name>equal</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>c90866276d5666262fb7cf385825ba7e</anchor>
      <arglist>(const formula *l, const formula *g)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>bool</type>
      <name>incompatible_</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>6a87e3c69b4812b40d487b316427767f</anchor>
      <arglist>(record_ *l, record_ *g)</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>record_ *</type>
      <name>register_formula_</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>25c34e986c1c5e3f78b78292373ace11</anchor>
      <arglist>(const formula *f)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bdd_dict *</type>
      <name>dict_</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>057a8b8ab245f697c8439118120aab39</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>exprop_</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>95cd0c1966276c943d5d805541b972df</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>symb_merge_</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>e04a7200fa965603baabc939f9da8d49</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>branching_postponement_</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>a687220ab77ab748bf0f16decbea9315</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>bool</type>
      <name>fair_loop_approx_</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>50a81db42eb6c9e28b0bbfd53a24db69</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>trans_map</type>
      <name>translated_</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>884aae37d75913ecbdcc0ec02ab81c15</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>Sgi::hash_map&lt; const formula *, record_, formula_ptr_hash &gt;</type>
      <name>trans_map</name>
      <anchorfile>classspot_1_1ltl_1_1language__containment__checker.html</anchorfile>
      <anchor>2e4e705afa5565341f98d6fc81188c39</anchor>
      <arglist></arglist>
    </member>
    <class kind="struct">spot::ltl::language_containment_checker::record_</class>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::language_containment_checker::record_</name>
    <filename>structspot_1_1ltl_1_1language__containment__checker_1_1record__.html</filename>
    <member kind="typedef">
      <type>std::map&lt; const record_ *, bool &gt;</type>
      <name>incomp_map</name>
      <anchorfile>structspot_1_1ltl_1_1language__containment__checker_1_1record__.html</anchorfile>
      <anchor>6de543a3248568e8f17422a204d4d03f</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>const tgba *</type>
      <name>translation</name>
      <anchorfile>structspot_1_1ltl_1_1language__containment__checker_1_1record__.html</anchorfile>
      <anchor>b1cc85a1312071d13f527376a447eb22</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>incomp_map</type>
      <name>incompatible</name>
      <anchorfile>structspot_1_1ltl_1_1language__containment__checker_1_1record__.html</anchorfile>
      <anchor>5f981b6573e682b1a0a975a02122f10c</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::unabbreviate_logic_visitor</name>
    <filename>classspot_1_1ltl_1_1unabbreviate__logic__visitor.html</filename>
    <base>spot::ltl::clone_visitor</base>
    <member kind="function">
      <type></type>
      <name>unabbreviate_logic_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__logic__visitor.html</anchorfile>
      <anchor>b5fbd1feec2c1524298a32efa2aab338</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~unabbreviate_logic_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__logic__visitor.html</anchorfile>
      <anchor>a07212b8be470a4b537b1a555f2529fc</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__logic__visitor.html</anchorfile>
      <anchor>bd048be2e19500821c4ad80eb57816b3</anchor>
      <arglist>(binop *bo)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual formula *</type>
      <name>recurse</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__logic__visitor.html</anchorfile>
      <anchor>93e59fa4a0e1044e1cac826abbd98090</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>result</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>c5d144a3d5fd04460583a7288fd57713</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>818de31a16cd8f2108dc1805b355bf5e</anchor>
      <arglist>(atomic_prop *ap)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>3cb7109d821d0c45c517f852f5acb7b7</anchor>
      <arglist>(unop *uo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>e111a2b5b04069e1f31e7bdf97478122</anchor>
      <arglist>(multop *mo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>a918745a995a9b4d61f718bbf23e93be</anchor>
      <arglist>(constant *c)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>formula *</type>
      <name>result_</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>32a63ad362fc3c5fbf3973ef4c8d3712</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>clone_visitor</type>
      <name>super</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__logic__visitor.html</anchorfile>
      <anchor>6d9e94e7f62fb841630d5ba5b7f1c7fd</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::postfix_visitor</name>
    <filename>classspot_1_1ltl_1_1postfix__visitor.html</filename>
    <base>spot::ltl::visitor</base>
    <member kind="function">
      <type></type>
      <name>postfix_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>03fab2a1197f8d0efad8882f3f4734aa</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~postfix_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>ab120e5ad9b6ad6350564521953879ed</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>58d5093bbc10066305a6a873ef10ea39</anchor>
      <arglist>(atomic_prop *ap)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>6ff1124485cbdd53f72c29b74cd8e500</anchor>
      <arglist>(unop *uo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>2783c586fd665d912ecd8e2c3ad6d720</anchor>
      <arglist>(binop *bo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>b238dead359ba60eebdbfd54f44cb28a</anchor>
      <arglist>(multop *mo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>801c1ad94b197f73f3013850dd19b22a</anchor>
      <arglist>(constant *c)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>doit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>9047cc8648d1c428fad8152c81056a9e</anchor>
      <arglist>(atomic_prop *ap)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>doit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>78d789d1329c549a151473f605d1d555</anchor>
      <arglist>(unop *uo)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>doit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>fd2f961a58be5af7e096e7b0ce970b90</anchor>
      <arglist>(binop *bo)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>doit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>edee7915087ffde12b6fec2112f4ee39</anchor>
      <arglist>(multop *mo)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>doit</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>526955d79235c99b2c100315901bb303</anchor>
      <arglist>(constant *c)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual void</type>
      <name>doit_default</name>
      <anchorfile>classspot_1_1ltl_1_1postfix__visitor.html</anchorfile>
      <anchor>3e62681d02d0f99c58061ef4aa65d6eb</anchor>
      <arglist>(formula *f)</arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::random_ltl</name>
    <filename>classspot_1_1ltl_1_1random__ltl.html</filename>
    <member kind="function">
      <type></type>
      <name>random_ltl</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>3a6eaaabb6c56d573ecb114a3d1959d9</anchor>
      <arglist>(const atomic_prop_set *ap)</arglist>
    </member>
    <member kind="function">
      <type></type>
      <name>~random_ltl</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>2b7dbcad1989e1eb546473e5beb23d81</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>generate</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>d9005a4287d09219b11c84f340e157d5</anchor>
      <arglist>(int n) const</arglist>
    </member>
    <member kind="function">
      <type>std::ostream &amp;</type>
      <name>dump_priorities</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>2664dd8aba375ae2649b94a19289ab2a</anchor>
      <arglist>(std::ostream &amp;os) const</arglist>
    </member>
    <member kind="function">
      <type>const char *</type>
      <name>parse_options</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>f5fbfbd12935b43ca7bc7a8b7a6ed7c2</anchor>
      <arglist>(char *options)</arglist>
    </member>
    <member kind="function">
      <type>const atomic_prop_set *</type>
      <name>ap</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>f4dbcebf15d1f65894074236b4d178de</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function" protection="protected">
      <type>void</type>
      <name>update_sums</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>f58923aee8a58f029d50d78395ccff9e</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="variable" protection="private">
      <type>op_proba *</type>
      <name>proba_</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>659974993b3d9422fffee9a511c30caa</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>double</type>
      <name>total_1_</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>f00ede174d47f791bf0762f397301cc8</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>op_proba *</type>
      <name>proba_2_</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>7c05683dfa06e6cafa78e5d2f18d175f</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>double</type>
      <name>total_2_</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>5ad54b6df6feaa2fd2f3d56ace6332d0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>double</type>
      <name>total_2_and_more_</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>73312a3175558a19833763892924f1c4</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable" protection="private">
      <type>const atomic_prop_set *</type>
      <name>ap_</name>
      <anchorfile>classspot_1_1ltl_1_1random__ltl.html</anchorfile>
      <anchor>0ade0cd5de429b0275f7c4fbe2248c18</anchor>
      <arglist></arglist>
    </member>
    <class kind="struct">spot::ltl::random_ltl::op_proba</class>
  </compound>
  <compound kind="struct">
    <name>spot::ltl::random_ltl::op_proba</name>
    <filename>structspot_1_1ltl_1_1random__ltl_1_1op__proba.html</filename>
    <member kind="typedef">
      <type>formula *(*)</type>
      <name>builder</name>
      <anchorfile>structspot_1_1ltl_1_1random__ltl_1_1op__proba.html</anchorfile>
      <anchor>c3241d82f0d10a76e14893c0509eaec9</anchor>
      <arglist>(const random_ltl *rl, int n)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>setup</name>
      <anchorfile>structspot_1_1ltl_1_1random__ltl_1_1op__proba.html</anchorfile>
      <anchor>2faf42d1d54e8b06f22d7683c69bb2b1</anchor>
      <arglist>(const char *name, int min_n, builder build)</arglist>
    </member>
    <member kind="variable">
      <type>const char *</type>
      <name>name</name>
      <anchorfile>structspot_1_1ltl_1_1random__ltl_1_1op__proba.html</anchorfile>
      <anchor>23c1108562d9375f33e71019320c6297</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>int</type>
      <name>min_n</name>
      <anchorfile>structspot_1_1ltl_1_1random__ltl_1_1op__proba.html</anchorfile>
      <anchor>6c5d238c9400cb03242d207d3e2a6ef0</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>double</type>
      <name>proba</name>
      <anchorfile>structspot_1_1ltl_1_1random__ltl_1_1op__proba.html</anchorfile>
      <anchor>a569f97b4366248732aace95ff85f312</anchor>
      <arglist></arglist>
    </member>
    <member kind="variable">
      <type>builder</type>
      <name>build</name>
      <anchorfile>structspot_1_1ltl_1_1random__ltl_1_1op__proba.html</anchorfile>
      <anchor>c90c90762635d65f86e995d443880eb8</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::simplify_f_g_visitor</name>
    <filename>classspot_1_1ltl_1_1simplify__f__g__visitor.html</filename>
    <base>spot::ltl::clone_visitor</base>
    <member kind="function">
      <type></type>
      <name>simplify_f_g_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1simplify__f__g__visitor.html</anchorfile>
      <anchor>42f5bf4e42b7727f045878a201125c48</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~simplify_f_g_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1simplify__f__g__visitor.html</anchorfile>
      <anchor>441cf8abdd96701d2b99c86949d8d76f</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1simplify__f__g__visitor.html</anchorfile>
      <anchor>e4077b2051b73b031bb8b6c64deddc6e</anchor>
      <arglist>(binop *bo)</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual formula *</type>
      <name>recurse</name>
      <anchorfile>classspot_1_1ltl_1_1simplify__f__g__visitor.html</anchorfile>
      <anchor>cc92ee2e8d255ff74945f573037666d0</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>result</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>c5d144a3d5fd04460583a7288fd57713</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>818de31a16cd8f2108dc1805b355bf5e</anchor>
      <arglist>(atomic_prop *ap)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>3cb7109d821d0c45c517f852f5acb7b7</anchor>
      <arglist>(unop *uo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>e111a2b5b04069e1f31e7bdf97478122</anchor>
      <arglist>(multop *mo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>a918745a995a9b4d61f718bbf23e93be</anchor>
      <arglist>(constant *c)</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>formula *</type>
      <name>result_</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>32a63ad362fc3c5fbf3973ef4c8d3712</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>clone_visitor</type>
      <name>super</name>
      <anchorfile>classspot_1_1ltl_1_1simplify__f__g__visitor.html</anchorfile>
      <anchor>b1c32770fb9bfb45c91c437842b87999</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="class">
    <name>spot::ltl::unabbreviate_ltl_visitor</name>
    <filename>classspot_1_1ltl_1_1unabbreviate__ltl__visitor.html</filename>
    <base>spot::ltl::unabbreviate_logic_visitor</base>
    <member kind="function">
      <type></type>
      <name>unabbreviate_ltl_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__ltl__visitor.html</anchorfile>
      <anchor>9f65ad7891246e6122327f0350e2590b</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function" virtualness="virtual">
      <type>virtual</type>
      <name>~unabbreviate_ltl_visitor</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__ltl__visitor.html</anchorfile>
      <anchor>fd89f4a60841a845c4f084e90ab45c51</anchor>
      <arglist>()</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__ltl__visitor.html</anchorfile>
      <anchor>26b8f5d58e2337d7c41f5af3497c425a</anchor>
      <arglist>(unop *uo)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>recurse</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__ltl__visitor.html</anchorfile>
      <anchor>36bcea2821322afbda8e29d8bf29437c</anchor>
      <arglist>(formula *f)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__logic__visitor.html</anchorfile>
      <anchor>bd048be2e19500821c4ad80eb57816b3</anchor>
      <arglist>(binop *bo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>818de31a16cd8f2108dc1805b355bf5e</anchor>
      <arglist>(atomic_prop *ap)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>e111a2b5b04069e1f31e7bdf97478122</anchor>
      <arglist>(multop *mo)</arglist>
    </member>
    <member kind="function">
      <type>void</type>
      <name>visit</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>a918745a995a9b4d61f718bbf23e93be</anchor>
      <arglist>(constant *c)</arglist>
    </member>
    <member kind="function">
      <type>formula *</type>
      <name>result</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>c5d144a3d5fd04460583a7288fd57713</anchor>
      <arglist>() const</arglist>
    </member>
    <member kind="variable" protection="protected">
      <type>formula *</type>
      <name>result_</name>
      <anchorfile>classspot_1_1ltl_1_1clone__visitor.html</anchorfile>
      <anchor>32a63ad362fc3c5fbf3973ef4c8d3712</anchor>
      <arglist></arglist>
    </member>
    <member kind="typedef" protection="private">
      <type>unabbreviate_logic_visitor</type>
      <name>super</name>
      <anchorfile>classspot_1_1ltl_1_1unabbreviate__ltl__visitor.html</anchorfile>
      <anchor>ba42e6473a8f2e3a10291f9a0aebb219</anchor>
      <arglist></arglist>
    </member>
  </compound>
  <compound kind="dir">
    <name>evtgba/</name>
    <path>/home/adl/proj/spot/src/evtgba/</path>
    <filename>dir_22e507a6e76e0f99d38a3ebfc2e400e1.html</filename>
    <file>evtgba.hh</file>
    <file>evtgbaiter.hh</file>
    <file>explicit.hh</file>
    <file>product.hh</file>
    <file>symbol.hh</file>
  </compound>
  <compound kind="dir">
    <name>evtgbaalgos/</name>
    <path>/home/adl/proj/spot/src/evtgbaalgos/</path>
    <filename>dir_068d108bf4cb31b8f383cca1be41c665.html</filename>
    <file>dotty.hh</file>
    <file>reachiter.hh</file>
    <file>save.hh</file>
    <file>tgba2evtgba.hh</file>
  </compound>
  <compound kind="dir">
    <name>evtgbaparse/</name>
    <path>/home/adl/proj/spot/src/evtgbaparse/</path>
    <filename>dir_41728f7edef17b6f7618753ea07d4c02.html</filename>
    <file>public.hh</file>
  </compound>
  <compound kind="dir">
    <name>gspn/</name>
    <path>/home/adl/proj/spot/iface/gspn/</path>
    <filename>dir_2646a8c7da3ddb2da41abd9f230687b2.html</filename>
    <file>common.hh</file>
    <file>gspn.hh</file>
    <file>ssp.hh</file>
  </compound>
  <compound kind="dir">
    <name>tgbaalgos/gtec/</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/gtec/</path>
    <filename>dir_8f715ec84b8ed801ac76079a5af54168.html</filename>
    <file>ce.hh</file>
    <file>explscc.hh</file>
    <file>gtec.hh</file>
    <file>nsheap.hh</file>
    <file>sccstack.hh</file>
    <file>status.hh</file>
  </compound>
  <compound kind="dir">
    <name>ltlast/</name>
    <path>/home/adl/proj/spot/src/ltlast/</path>
    <filename>dir_eba6d954d7634e91eb138374e81052ec.html</filename>
    <file>allnodes.hh</file>
    <file>atomic_prop.hh</file>
    <file>binop.hh</file>
    <file>constant.hh</file>
    <file>formula.hh</file>
    <file>multop.hh</file>
    <file>predecl.hh</file>
    <file>refformula.hh</file>
    <file>unop.hh</file>
    <file>visitor.hh</file>
  </compound>
  <compound kind="dir">
    <name>ltlenv/</name>
    <path>/home/adl/proj/spot/src/ltlenv/</path>
    <filename>dir_c5a04603248cee438262253ec34e096f.html</filename>
    <file>declenv.hh</file>
    <file>defaultenv.hh</file>
    <file>environment.hh</file>
    <file>rodeco.hh</file>
  </compound>
  <compound kind="dir">
    <name>ltlparse/</name>
    <path>/home/adl/proj/spot/src/ltlparse/</path>
    <filename>dir_0b482e3c28b9f26e04f093065460c65a.html</filename>
    <file>location.hh</file>
    <file>position.hh</file>
    <file>public.hh</file>
    <file>stack.hh</file>
  </compound>
  <compound kind="dir">
    <name>ltlvisit/</name>
    <path>/home/adl/proj/spot/src/ltlvisit/</path>
    <filename>dir_b3ac78f1b4241ab45904be9859630079.html</filename>
    <file>apcollect.hh</file>
    <file>basicreduce.hh</file>
    <file>clone.hh</file>
    <file>contain.hh</file>
    <file>destroy.hh</file>
    <file>dotty.hh</file>
    <file>dump.hh</file>
    <file>length.hh</file>
    <file>lunabbrev.hh</file>
    <file>nenoform.hh</file>
    <file>postfix.hh</file>
    <file>randomltl.hh</file>
    <file>reduce.hh</file>
    <file>simpfg.hh</file>
    <file>syntimpl.hh</file>
    <file>tostring.hh</file>
    <file>tunabbrev.hh</file>
  </compound>
  <compound kind="dir">
    <name>misc/</name>
    <path>/home/adl/proj/spot/src/misc/</path>
    <filename>dir_f81fe1d35537116213ffa91a5f440cd7.html</filename>
    <file>bareword.hh</file>
    <file>bddalloc.hh</file>
    <file>bddlt.hh</file>
    <file>escape.hh</file>
    <file>freelist.hh</file>
    <file>hash.hh</file>
    <file>hashfunc.hh</file>
    <file>ltstr.hh</file>
    <file>memusage.hh</file>
    <file>minato.hh</file>
    <file>modgray.hh</file>
    <file>optionmap.hh</file>
    <file>random.hh</file>
    <file>timer.hh</file>
    <file>version.hh</file>
  </compound>
  <compound kind="dir">
    <name>sautparse/</name>
    <path>/home/adl/proj/spot/src/sautparse/</path>
    <filename>dir_d219759fbd12ba277c2ddb6ffb2e7d70.html</filename>
    <file>location.hh</file>
    <file>position.hh</file>
    <file>stack.hh</file>
  </compound>
  <compound kind="dir">
    <name>tgba/</name>
    <path>/home/adl/proj/spot/src/tgba/</path>
    <filename>dir_b4bd8eb93ce3363584c0178d1597c14d.html</filename>
    <file>bdddict.hh</file>
    <file>bddprint.hh</file>
    <file>formula2bdd.hh</file>
    <file>public.hh</file>
    <file>state.hh</file>
    <file>statebdd.hh</file>
    <file>succiter.hh</file>
    <file>succiterconcrete.hh</file>
    <file>tgba.hh</file>
    <file>tgbabddconcrete.hh</file>
    <file>tgbabddconcretefactory.hh</file>
    <file>tgbabddconcreteproduct.hh</file>
    <file>tgbabddcoredata.hh</file>
    <file>tgbabddfactory.hh</file>
    <file>tgbaexplicit.hh</file>
    <file>tgbaproduct.hh</file>
    <file>tgbareduc.hh</file>
    <file>tgbatba.hh</file>
  </compound>
  <compound kind="dir">
    <name>tgbaalgos/</name>
    <path>/home/adl/proj/spot/src/tgbaalgos/</path>
    <filename>dir_3176162069ccf504ec78e9183a9d7554.html</filename>
    <dir>tgbaalgos/gtec/</dir>
    <file>bfssteps.hh</file>
    <file>dotty.hh</file>
    <file>dottydec.hh</file>
    <file>dupexp.hh</file>
    <file>emptiness.hh</file>
    <file>emptiness_stats.hh</file>
    <file>gv04.hh</file>
    <file>lbtt.hh</file>
    <file>ltl2tgba_fm.hh</file>
    <file>ltl2tgba_lacim.hh</file>
    <file>magic.hh</file>
    <file>neverclaim.hh</file>
    <file>powerset.hh</file>
    <file>projrun.hh</file>
    <file>randomgraph.hh</file>
    <file>reachiter.hh</file>
    <file>reducerun.hh</file>
    <file>reductgba_sim.hh</file>
    <file>replayrun.hh</file>
    <file>rundotdec.hh</file>
    <file>save.hh</file>
    <file>se05.hh</file>
    <file>stats.hh</file>
    <file>tau03.hh</file>
    <file>tau03opt.hh</file>
    <file>weight.hh</file>
  </compound>
  <compound kind="dir">
    <name>tgbaparse/</name>
    <path>/home/adl/proj/spot/src/tgbaparse/</path>
    <filename>dir_9f5081b22ae8a5e54871b11cf9e796ed.html</filename>
    <file>public.hh</file>
  </compound>
</tagfile>
