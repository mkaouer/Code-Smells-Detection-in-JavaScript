/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.body;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.generate.opencl.OpenCLClass;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;
import org.trifort.rootbeer.generate.opencl.OpenCLType;
import org.trifort.rootbeer.generate.opencl.fields.OpenCLField;
import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;

import soot.*;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.JimpleBody;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.TableSwitchStmt;
import soot.options.Options;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.util.Chain;

public class OpenCLBody {
  private Body m_body;
  private List<Unit> m_labels;
  private boolean m_isConstructor;
  private boolean m_isConstructorBodyWithoutHeader;
  private int m_refFieldsSize;
  private MethodStmtSwitch m_stmtSwitch;
  private int m_derivedType;
  private int m_allocSize;
  private SootClass m_sootClass;
  private SootMethod m_sootMethod;
  private Map<Unit, List<TrapItem>> m_trapMap;
  private List<TrapItem> m_allTraps;

  /**
   * Use this for either a constructor with header or a normal body
   * @param body
   * @param is_constructor 
   */
  public OpenCLBody(SootMethod method, boolean is_constructor){
    m_labels = new ArrayList<Unit>();
    m_trapMap = new HashMap<Unit, List<TrapItem>>();
    m_allTraps = new ArrayList<TrapItem>();
    m_isConstructor = is_constructor;
    m_isConstructorBodyWithoutHeader = false;
    m_sootMethod = method;

    m_sootClass = method.getDeclaringClass();
    Body body = method.retrieveActiveBody();
    m_body = body;  
  }
  
  /**
   * Use this ctor for a body that is a constructor body without a header
   * @param body 
   */
  public OpenCLBody(Body body){
    m_labels = new ArrayList<Unit>();
    m_trapMap = new HashMap<Unit, List<TrapItem>>();
    m_allTraps = new ArrayList<TrapItem>();
    m_isConstructor = false;
    m_isConstructorBodyWithoutHeader = true;
    m_body = body;  
    m_sootMethod = body.getMethod();
  }
  
  private Iterator<Unit> bodyIterator(){
    PatchingChain<Unit> chain_units = m_body.getUnits();
    return chain_units.iterator();
  }

  public String getBody(){
    determineLabels();

    StringBuilder ret = new StringBuilder();
    ret.append(writeLocals());
    ret.append(writeBody());
    return ret.toString();
  }
  
  public String getBodyNoLocals(){
    determineLabels();

    StringBuilder ret = new StringBuilder();
    ret.append(writeBody());
    return ret.toString();  
  }
  
  public String getLocals(){
    return writeLocals();
  }
  
  private void addTrapItem(Unit unit, TrapItem item){
    if(m_trapMap.containsKey(unit)){
      List<TrapItem> traps = m_trapMap.get(unit);
      traps.add(item);
    } else {
      List<TrapItem> traps = new ArrayList<TrapItem>();
      traps.add(item);
      m_trapMap.put(unit, traps);
    }
  }

  private String writeMethodBody(){
    Iterator<Trap> traps = m_body.getTraps().iterator();
    int trap_num = 0;
    while(traps.hasNext()){
      Trap t = traps.next();
      TrapItem item = new TrapItem(t, bodyIterator(), trap_num, t.getException());
      List<Unit> trap_units = item.getUnits();
      for(Unit u : trap_units){
        addTrapItem(u, item);
      }
      trap_num++;
      m_allTraps.add(item);
    }

    MonitorGroups monitor_groups = new MonitorGroups();
    List<MonitorGroupItem> root_items = monitor_groups.getItems(m_body);
    for(MonitorGroupItem item : root_items){
      handleMonitorGroupItem(item);
    }
    return m_stmtSwitch.toString();
  }
  
  private void handleMonitorGroupItem(MonitorGroupItem item){
    List<Unit> units = item.getPrefixUnits();
    for(Unit next : units){
      handleUnit(next);
    } 
    Unit enter = item.getEnterMonitor();
    if(enter != null){
      handleUnit(enter); 
    }
    List<MonitorGroupItem> items = item.getGroups();
    for(MonitorGroupItem sub_item : items){
      handleMonitorGroupItem(sub_item);
    }
    if(enter != null){     
      m_stmtSwitch.append("  }\n");
      m_stmtSwitch.append("}\n");
      m_stmtSwitch.popMonitor(); 
    }
  }
  
  private void handleUnit(Unit next){
    int label_num = labelNum(next);
    if(label_num != -1){
      m_stmtSwitch.append("label" + Integer.toString(label_num) + ":\n");
    }
    int trap_num2 = trapNum(next);
    if(trap_num2 != -1){
      m_stmtSwitch.append("trap" + Integer.toString(trap_num2) + ":\n");
    }
    m_stmtSwitch.append("//"+next.toString()+"\n");
    List<TrapItem> trap_items = null;
    if(m_trapMap.containsKey(next)){
      trap_items = m_trapMap.get(next);
    }
    m_stmtSwitch.reset();
    m_stmtSwitch.setTrapItems(trap_items);
    next.apply(m_stmtSwitch);
    if(m_stmtSwitch.hasCaughtExceptionRef()){
      m_stmtSwitch.append("*exception = 0;\n");
    } 
  }
  
  private void determineConstructorInfo(){
    SootMethod soot_method = m_body.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(soot_class);

    m_allocSize = ocl_class.getSize();
    m_refFieldsSize = ocl_class.getRefFieldsSize();
    m_derivedType = OpenCLScene.v().getClassType(soot_class);
  }
  
  private String writeConstructorBody(){
    determineConstructorInfo();
    
    StringBuilder ret = new StringBuilder();
    String pointer_namespace_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    ret.append("int thisref;\n");
    ret.append(pointer_namespace_qual+" char * thisref_deref;\n");
    ret.append("thisref = -1;\n");
    int alloc_size = m_allocSize;
    int mod = m_allocSize % 8;
    if(mod != 0)
      alloc_size += (8 - mod);
    ret.append("org_trifort_gc_assign(&thisref, org_trifort_gc_malloc("+Integer.toString(alloc_size)+"));\n");
    if(Configuration.compilerInstance().getExceptions()){
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = "+OpenCLScene.v().getOutOfMemoryNumber()+";\n");
      ret.append("  return -1;\n");
      ret.append("}\n");
    }
    ret.append("thisref_deref = org_trifort_gc_deref(thisref);\n");
    ret.append("\n//class info\n");
    ret.append("org_trifort_gc_set_count(thisref_deref, "+Integer.toString(m_refFieldsSize)+");\n");
    ret.append("org_trifort_gc_set_color(thisref_deref, COLOR_GREY);\n");
    ret.append("org_trifort_gc_set_type(thisref_deref, "+Integer.toString(m_derivedType)+");\n");
    ret.append("org_trifort_gc_set_ctor_used(thisref_deref, 1);\n");
    ret.append("org_trifort_gc_set_size(thisref_deref, "+Integer.toString(alloc_size)+");\n");
    ret.append("org_trifort_gc_init_monitor(thisref_deref);\n");
    
    if(m_sootClass != null){
      ret.append(initFields());
    }
    
    m_stmtSwitch = new ConstructorStmtSwitch(this, m_body.getMethod(), false);
    ret.append(writeMethodBody());

    ret.append("return "+m_stmtSwitch.getThisRef()+";\n");
    return ret.toString();
  }
  
  private String initFields(){
    StringBuilder ret = new StringBuilder();
    SootClass soot_class = m_sootClass;
    while(true){
      initFieldsForClass(soot_class, ret);      
      if(soot_class.getName().equals("java.lang.Object"))
        break;
      soot_class = Scene.v().getSootClass(soot_class.getSuperclass().getName());
      if(soot_class.getName().equals("java.lang.Object"))
        break;
    }
    return ret.toString();
  }

  private void initFieldsForClass(SootClass soot_class, StringBuilder ret){
    List<OpenCLField> ref_fields = getFields(soot_class, true);
    for(OpenCLField field : ref_fields){
      ret.append(field.getInstanceSetterInvokeWithoutThisref()+"thisref, -1, exception);\n");
    }    
    List<OpenCLField> non_ref_fields = getFields(soot_class, false);
    for(OpenCLField field : non_ref_fields){
      ret.append(field.getInstanceSetterInvokeWithoutThisref()+"thisref, 0, exception);\n");
    }   
  }
  
  private List<OpenCLField> getFields(SootClass soot_class, boolean ref_fields){
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(soot_class);  
    if(ref_fields)
      return ocl_class.getInstanceRefFields();
    else
      return ocl_class.getInstanceNonRefFields();
  }
  
  private String writeBody(){
    if(m_isConstructorBodyWithoutHeader == false){
      if(m_isConstructor)
        return writeConstructorBody();
      else {
        m_stmtSwitch = new MethodStmtSwitch(this, m_body.getMethod());
        return writeMethodBody();
      }
    } else {
      StringBuilder ret = new StringBuilder();
      m_stmtSwitch = new ConstructorStmtSwitch(this, m_body.getMethod(), true);
      ret.append(writeMethodBody());
      return ret.toString();
    }
  }
  
  private String writeLocals() {
    Chain<Local> locals = m_body.getLocals();
    String ret = "";
    for(Local local : locals){
      OpenCLType type = new OpenCLType(local.getType());
      String local_init = type.getCudaTypeString()+" "+local.getName();
      if(type.isRefType()){
        local_init += " = -1";
      } else {
        local_init += " = 0";
      }
      local_init += ";\n";
      ret += local_init;
    }
    return ret;
  }

  private void determineLabels() {
    Iterator<Unit> iter = bodyIterator();
    while(iter.hasNext()){
      Unit curr = iter.next();
      if(curr instanceof IfStmt){
        IfStmt ifstmt = (IfStmt) curr;
        m_labels.add(ifstmt.getTarget());
      } else if(curr instanceof GotoStmt){
        GotoStmt gotostmt = (GotoStmt) curr;
        m_labels.add(gotostmt.getTarget());
      } else if(curr instanceof LookupSwitchStmt){
        LookupSwitchStmt lookup = (LookupSwitchStmt) curr;
        addTargets(lookup.getTargets());
        m_labels.add(lookup.getDefaultTarget());
      } else if(curr instanceof TableSwitchStmt){
        TableSwitchStmt table = (TableSwitchStmt) curr;
        addTargets(table.getTargets());
      }
    }
  }
  
  private void addTargets(List<Unit> units){
    for(Unit unit : units){
      m_labels.add(unit);
    }
  }

  int labelNum(Unit unit){
    for(int i = 0; i < m_labels.size(); ++i){
      if(m_labels.get(i).equals(unit))
        return i;
    }
    return -1;
  }

  private int trapNum(Unit next) {
    for(TrapItem trap : m_allTraps){
      if(trap.unitIsHandler(next)){
        return trap.getTrapNum();
      }
    }
    return -1;
  }
}
