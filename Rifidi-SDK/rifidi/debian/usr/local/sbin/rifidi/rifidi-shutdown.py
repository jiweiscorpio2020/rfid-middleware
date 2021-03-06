#!/usr/bin/env python

import os
import sys
import re
import time
import signal
from os.path import join
import subprocess
from subprocess import Popen, PIPE
from telnetlib import Telnet

if __name__ == '__main__':
    
    # try a nice shutdown
    try :
        telnet = Telnet('localhost', '2020')
        actual = telnet.read_eager()
        telnet.write('close\r\n')
        time.sleep(1)
        actual = telnet.read_eager()
        telnet.write('y\r\n')
    except:
        pass
        
    # kill anything that is left
    p1 = Popen(['ps','ax'], stdout=PIPE)
    p2 = Popen(['grep', 'org.rifidi.edge'], stdin=p1.stdout, stdout=PIPE)
    output = p2.communicate()[0]    
    output = output.split('\n')
    if output:
        for i in output:
            if re.search('grep', i):
                continue
            else:
                pid = i.split(' ')
                assert pid.__len__() > 0
                fnull = open('/dev/null', 'w')
                print pid[0]
                #out = subprocess.call(['kill', '-9', str(pid[0])], shell=True, stdout=fnull, stderr=fnull)
                os.kill(int(pid[0]), signal.SIGTERM)
