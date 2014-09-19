from base_test import scenario_output, BaseApiTestCase


class trovebox_scenario_output(scenario_output):
    def __init__(self, scenario_name, extra_params=""):
        super(trovebox_scenario_output, self).__init__("trovebox",
                                                       "/photos/list.json",
                                                       "trovebox-api",
                                                       scenario_name,
                                                       extra_params)


class TroveboxTestCase(BaseApiTestCase):
    def test_oneSearchResult(self):
        with trovebox_scenario_output("oneSearchResult") as output:
            self.assertEqual(len(self.get_photos(output)), 1)
