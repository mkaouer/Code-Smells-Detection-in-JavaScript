package rootbeer.examples.gtc2013;

public class Calculation {

  public int sub_matrix_row;
  public int sub_matrix_col;
  public int sub_matrix;
  public int m_size;
  public int thread_row;
  public int thread_col;
  public int dest_row;
  public int dest_col;
  public int block_size;
  public int dest_index;
  public int m;
  public int k;
  public int a_src_row;
  public int a_src_col;
  public int b_src_row;
  public int b_src_col;
  public float a_value;
  public float b_value;

  @Override
  public String toString(){
    StringBuilder ret = new StringBuilder();
    ret.append("calc row: \n");
    ret.append("  sub_matrix_row: ");
    ret.append(sub_matrix_row);
    ret.append("\n");

    ret.append("  sub_matrix_col: ");
    ret.append(sub_matrix_col);
    ret.append("\n");

    ret.append("  sub_matrix: ");
    ret.append(sub_matrix);
    ret.append("\n");

    ret.append("  m_size: ");
    ret.append(m_size);
    ret.append("\n");

    ret.append("  thread_row: ");
    ret.append(thread_row);
    ret.append("\n");

    ret.append("  thread_col: ");
    ret.append(thread_col);
    ret.append("\n");

    ret.append("  dest_row: ");
    ret.append(dest_row);
    ret.append("\n");

    ret.append("  dest_col: ");
    ret.append(dest_col);
    ret.append("\n");

    ret.append("  block_size: ");
    ret.append(block_size);
    ret.append("\n");

    ret.append("  dest_index: ");
    ret.append(dest_index);
    ret.append("\n");

    ret.append("  m: ");
    ret.append(m);
    ret.append("\n");

    ret.append("  k: ");
    ret.append(k);
    ret.append("\n");

    ret.append("  a_src_row: ");
    ret.append(a_src_row);
    ret.append("\n");

    ret.append("  a_src_col: ");
    ret.append(a_src_col);
    ret.append("\n");

    ret.append("  b_src_row: ");
    ret.append(b_src_row);
    ret.append("\n");

    ret.append("  b_src_col: ");
    ret.append(b_src_col);
    ret.append("\n");

    ret.append("  a_value: ");
    ret.append(a_value);
    ret.append("\n");

    ret.append("  b_value: ");
    ret.append(b_value);
    ret.append("\n");


    return ret.toString();
  }
}
