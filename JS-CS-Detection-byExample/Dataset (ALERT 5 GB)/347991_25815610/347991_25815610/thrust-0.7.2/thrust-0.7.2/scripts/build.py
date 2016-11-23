#!/usr/bin/env python

import argparse
import os
import subprocess
import sys


CONFIGURATIONS = ['Release', 'Debug']
SOURCE_ROOT = os.path.abspath(os.path.dirname(os.path.dirname(__file__)))


def main():
  os.chdir(SOURCE_ROOT)

  ninja = os.path.join('vendor', 'depot_tools', 'ninja')
  if sys.platform == 'win32':
    ninja += '.exe'

  args = parse_args()
  for config in args.configuration:
    build_path = os.path.join('out', config)
    ret = subprocess.call([ninja, '-C', build_path, args.target])
    if ret != 0:
      sys.exit(ret)


def parse_args():
  parser = argparse.ArgumentParser(description='Build thrust_shell')
  parser.add_argument('-c', '--configuration',
                      help='Build with Release or Debug configuration',
                      nargs='+',
                      default=CONFIGURATIONS,
                      required=False)
  parser.add_argument('-t', '--target',
                      help='Build specified target',
                      default='thrust_shell',
                      required=False)
  return parser.parse_args()


if __name__ == '__main__':
  sys.exit(main())
