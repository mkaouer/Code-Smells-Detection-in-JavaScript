/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.internal.JSpecialInvokeExpr;

public class SpecialInvokeFixup {
 
  private Value m_CastLhs;
  private Value m_CastRhs;
  private Unit m_CastUnit;
  private List<Unit> m_ToDelete;
  
  public Body fixup(Body body){
    m_ToDelete = new ArrayList<Unit>();
    Iterator<Unit> iter = body.getUnits().iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      if(next instanceof AssignStmt){
        AssignStmt assign = (AssignStmt) next;
        Value lhs = assign.getLeftOp();
        Value rhs = assign.getRightOp();
        if(rhs instanceof CastExpr){
          m_CastLhs = lhs;
          CastExpr cast = (CastExpr) rhs;
          m_CastRhs = cast.getOp();
          m_CastUnit = next;
        }
      }      
      
      List<ValueBox> boxes = next.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        if(value instanceof JSpecialInvokeExpr){
          JSpecialInvokeExpr expr = (JSpecialInvokeExpr) value;
          Value base = expr.getBase();
          if(base.equals(m_CastLhs) == false)
            continue;
          expr.getBaseBox().setValue(m_CastRhs);
          m_ToDelete.add(m_CastUnit);
        }
      }
    }
    
    for(Unit unit : m_ToDelete){
      PatchingChain<Unit> units = body.getUnits();
      units.remove(unit);
    }
    
    return body;
  }
}
