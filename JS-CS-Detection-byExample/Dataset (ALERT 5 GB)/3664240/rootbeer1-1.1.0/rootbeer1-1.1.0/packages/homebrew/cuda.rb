require 'formula'

class Cuda < Formula
  homepage 'http://nvidia.com/'
  url 'http://developer.download.nvidia.com/compute/cuda/5_0/rel-update-1/installers/cuda_5.0.36_macos-2.pkg'
  version '5.0.36'
  sha1 'b7808d5a30c4f41ceffb70b5fc9010febe3ce3e8'

  def install
    system "touch", "/usr/local/Cellar/cuda/5.0.36/pass_brew"
    system "sudo", "installer", "-pkg", "/Library/Caches/Homebrew/cuda-5.0.36.pkg", "-target", "/"
  end

end
