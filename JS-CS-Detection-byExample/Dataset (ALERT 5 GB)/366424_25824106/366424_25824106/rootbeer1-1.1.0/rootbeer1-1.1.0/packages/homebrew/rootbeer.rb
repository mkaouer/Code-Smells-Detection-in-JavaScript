require 'formula'

class Rootbeer < Formula
  homepage 'http://rbcompiler.com/'
  url 'http://rbcompiler.com/dist/Rootbeer-1.0.45.homebrew.sh'
  version '1.0.45'
  sha1 '4efe39653ad11ac55a89866b35871d6287556a2c'

  depends_on 'wget'
  depends_on 'cuda'

  def install
    system "cp", "/Library/Caches/Homebrew/rootbeer-1.0.45.sh", "/usr/local/Cellar/rootbeer/1.0.45/"
    system "chmod", "770", "/usr/local/Cellar/rootbeer/1.0.45/rootbeer-1.0.45.sh"
    system "/usr/local/Cellar/rootbeer/1.0.45/rootbeer-1.0.45.sh"
    system "chmod", "770", "/usr/local/Cellar/rootbeer/1.0.45/Rootbeer"
    system "ln", "-s", "/usr/local/Cellar/rootbeer/1.0.45/Rootbeer", "/usr/local/bin/Rootbeer"
  end

  def test
    system "Rootbeer -runtest SimpleTest"
  end
end
