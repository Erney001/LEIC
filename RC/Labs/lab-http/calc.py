#!/usr/bin/python3

import sys
import hashlib

if __name__ == '__main__':
    assert(len(sys.argv) > 1)
    istid = sys.argv[1]
    s = []

    for istid in range(76196, 99999):
        result = hashlib.md5(bytes(istid)).hexdigest()[:4]
        if result not in s:
            s.append(result)
    
    print(99999-76196, len(s))
