
m_a = zeros(256,256);
value = 0;
for i=1:256
  for j=1:256
    m_a(i,j) = value;
    value++;
    if value == 3
      value = 0;
    end
  end
end

m_b = zeros(256,256*256*14);
value = 0;
for i=1:256
  for j=1:256*256*14
    m_b(i,j) = value;
    value++;
    if value == 3
      value = 0;
    end
  end
end

m_c = m_a * m_b;

fid = fopen('gold_output.txt', 'w');

for i=1:256
  for j=1:256*256*14
    fprintf(fid, 'm_c[%d][%d]=%d\n', i-1, j-1, m_c(i,j));
    fflush(fid);
  end
end

fclose(fid);
