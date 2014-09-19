import unittest

import sys
import os
import time
import httplib2
import subprocess
from BeautifulSoup import BeautifulSoup

ROBOHYDRA_COMMAND = "./node_modules/.bin/robohydra"
ROBOHYDRA_PROTOCOL = "http://"
ROBOHYDRA_HOSTNAME = "localhost"
ROBOHYDRA_PORT = "3001"
ROBOHYDRA_BASE_URL = "%s:%s" % (ROBOHYDRA_HOSTNAME, ROBOHYDRA_PORT)
ROBOHYDRA_USER = "foo"
CFM_COMMAND = "lein run %s -t %s -u %s%s/%s -s %s/static %s" % (
    ROBOHYDRA_USER,
    "%s",
    ROBOHYDRA_PROTOCOL,
    ROBOHYDRA_BASE_URL,
    "%s",
    ROBOHYDRA_BASE_URL,
    "%s")


class scenario_output(object):
    def __init__(self, service_type, base_url, plugin_name, scenario_name,
                 extra_params=""):
        self.service_type = service_type
        self.base_url = base_url
        self.plugin_name = plugin_name
        self.scenario_name = scenario_name
        self.extra_params = extra_params
        self.scenario_url = \
            "%s%s/robohydra-admin/rest/plugins/%s/scenarios/%s" % (
                ROBOHYDRA_PROTOCOL, ROBOHYDRA_BASE_URL,
                plugin_name, scenario_name)

    def __enter__(self):
        h = httplib2.Http()
        resp, content = h.request(self.scenario_url, "POST",
                                  body="active=true")
        if resp['status'] != "200":
            raise RuntimeError(
                "Could not start scenario %s in plugin %s, response was %s" % (
                    self.scenario_name, self.plugin_name, resp))

        return subprocess.check_output(CFM_COMMAND % (self.service_type,
                                                      self.base_url,
                                                      self.extra_params),
                                       shell=True)

    def __exit__(self, type, value, traceback):
        h = httplib2.Http()
        resp, content = h.request(self.scenario_url, "POST",
                                  body="active=false")
        if resp['status'] != "200":
            raise RuntimeError(
                "Could not stop scenario %s in plugin %s, response was %s" % (
                    self.scenario_name, self.plugin_name, resp))


class BaseApiTestCase(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.pid = os.fork()
        if cls.pid == 0:
            sys.stdout.flush()
            sys.stderr.flush()
            dev_null_r = file('/dev/null', 'r')
            dev_null_w = file('/dev/null', 'w')
            os.dup2(dev_null_w.fileno(), sys.stdout.fileno())
            os.dup2(dev_null_w.fileno(), sys.stderr.fileno())
            os.dup2(dev_null_r.fileno(), sys.stdin.fileno())
            os.execl(ROBOHYDRA_COMMAND, "robohydra", "-p", ROBOHYDRA_PORT,
                     "robohydra/mock-apis.conf")
        else:
            # Wait for RoboHydra to start (up to 5 seconds)
            h = httplib2.Http()
            counter, increment = 0.0, 0.2
            while 1:
                try:
                    resp, content = h.request("%s%s" % (ROBOHYDRA_PROTOCOL,
                                                        ROBOHYDRA_BASE_URL))
                except httplib2.socket.error:
                    time.sleep(increment)
                    counter += increment
                    if counter > 5:
                        raise RuntimeError("Could not start mock server")
                else:
                    break

    @classmethod
    def tearDownClass(cls):
        os.kill(cls.pid, 15)

    def get_photos(self, html):
        soup = BeautifulSoup(html)
        return soup.findAll("div", "photo")

