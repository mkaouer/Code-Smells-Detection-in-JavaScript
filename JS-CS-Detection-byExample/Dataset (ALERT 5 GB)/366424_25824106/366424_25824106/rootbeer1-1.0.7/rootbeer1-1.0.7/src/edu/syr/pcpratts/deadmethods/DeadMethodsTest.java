/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.deadmethods;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.BasicBlockNormalize;
import edu.syr.pcpratts.jpp.parser.CharacterReader;
import edu.syr.pcpratts.jpp.parser.DigraphReplacer;
import edu.syr.pcpratts.jpp.parser.DoNormalization;
import edu.syr.pcpratts.jpp.parser.ForSemiExpression;
import edu.syr.pcpratts.jpp.parser.NewlineRemover;
import edu.syr.pcpratts.jpp.parser.NewlineSplicer;
import edu.syr.pcpratts.jpp.parser.PreSemiExpression;
import edu.syr.pcpratts.jpp.parser.SemiExpression;
import edu.syr.pcpratts.jpp.parser.StringAndCommentTokenizer;
import edu.syr.pcpratts.jpp.parser.Token;
import edu.syr.pcpratts.jpp.parser.Tokenizer;
import edu.syr.pcpratts.jpp.parser.cpp.CppRunner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;

public class DeadMethodsTest {

  public static void main(String[] args){
    try {
      BufferedReader reader = new BufferedReader(new FileReader("/home/pcpratts/code/Rootbeer/Rootbeer-Product/generated.cu"));
      Iterator<Token>      iter0 = new CharacterReader(reader);
      Iterator<Token>      iter1 = new NewlineSplicer(iter0);
      Iterator<Token>      iter1b = new StringAndCommentTokenizer(iter1);
      Iterator<Token>      iter2 = new Tokenizer(iter1b);
      Iterator<Token>      iter3 = new PreSemiExpression(iter2);
      Iterator<Token>      iter2a = new DigraphReplacer(iter3);
      Iterator<Token>      iter3b = new NewlineRemover(iter2a);
      Iterator<Token>      iter4 = new BasicBlockNormalize(iter3b);
      Iterator<Token>      iter5 = new DoNormalization(iter4);
      Iterator<CStatement> iter6 = new SemiExpression(iter5);
      //Iterator<CFileItem>  iter7 = new ForSemiExpression(iter6);
      while(iter6.hasNext()){
        CStatement next = iter6.next();
        System.out.println(next.toString());
      }
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
