package rootbeer.examples.gtc2013;

import java.util.List;
import java.util.ArrayList;

public class CalcList {

  private Calculation[] m_data;
  private int m_size;

  public CalcList(){
    m_data = new Calculation[8];
    m_size = 0;
  }

  public void add(Calculation calc){
    m_data[m_size] = calc;
    ++m_size;

    if(m_size == m_data.length){
      Calculation[] new_data = new Calculation[m_size * 2];
      for(int i = 0; i < m_size - 1; ++i){
        new_data[i] = m_data[i];
      }
      m_data = new_data;
    }
  }

  public List<Calculation> getList(){
    List<Calculation> ret = new ArrayList<Calculation>();
    for(int i = 0; i < m_size - 1; ++i){
      ret.add(m_data[i]);
    }
    return ret;
  }
}
