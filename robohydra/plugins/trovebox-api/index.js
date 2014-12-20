var heads                   = require("robohydra").heads,
    RoboHydraHead           = heads.RoboHydraHead,
    RoboHydraHeadStatic     = heads.RoboHydraHeadStatic,
    RoboHydraHeadFilesystem = heads.RoboHydraHeadFilesystem;

exports.getBodyParts = function(conf, modules) {
    var assert   = modules.assert,
        fixtures = modules.fixtures;

    return {
        heads: [
            new RoboHydraHeadFilesystem({
                mountPath: '/photos',
                documentRoot: 'robohydra/static/photos'
            })
        ],

        scenarios: {
            oneSearchResult: {
                heads: [
                    new RoboHydraHeadStatic({
                        path: '/photos/list.json',
                        content: fixtures.load('simple-result.json').toString()
                    })
                ]
            },

            canGoBackTenYears: {
                heads: [
                    new RoboHydraHead({
                        path: '/photos/list.json',
                        handler: function(req, res) {
                            var takenBefore = req.queryParams.takenBefore,
                                takenAfter  = req.queryParams.takenAfter;
                            if (takenBefore.indexOf("2012") === -1 ||
                                takenAfter.indexOf("2012") === -1) {
                                res.send(fixtures.load('no-results.json'));
                            } else {
                                res.send(fixtures.load('simple-result.json'));
                            }
                        }
                    })
                ]
            }
        }
    };
};
