from base_test import scenario_output, BaseApiTestCase


class flickr_scenario_output(scenario_output):
    def __init__(self, scenario_name, extra_params=""):
        super(flickr_scenario_output, self).__init__("flickr",
                                                     "/services/rest",
                                                     "flickr-api",
                                                     scenario_name,
                                                     extra_params)


class FlickrTestCase(BaseApiTestCase):
    def test_oneSearchResult(self):
        with flickr_scenario_output("oneSearchResult") as output:
            self.assertEqual(len(self.get_photos(output)), 1)

    def test_resultsFiveYearsAgo(self):
        with flickr_scenario_output("resultsFiveYearsAgo") as output:
            self.assertEqual(len(self.get_photos(output)), 2)

    def test_tryAtMostFiveTimes(self):
        with flickr_scenario_output("tryAtMostFiveTimes") as output:
            self.assertEqual(len(self.get_photos(output)), 0)

    def test_errorInTheMiddle(self):
        with flickr_scenario_output("errorInTheMiddle") as output:
            self.assertEqual(len(self.get_photos(output)), 2)

    def test_olderYearsFirst(self):
        with flickr_scenario_output("olderYearsFirst",
                                    extra_params="2014-01-17") as output:
            self.assertEqual(len(self.get_photos(output)), 1)

