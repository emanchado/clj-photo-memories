var heads                   = require("robohydra").heads,
    RoboHydraHead           = heads.RoboHydraHead,
    RoboHydraHeadStatic     = heads.RoboHydraHeadStatic,
    RoboHydraHeadFilesystem = heads.RoboHydraHeadFilesystem;
var u = require("./utils");


exports.getBodyParts = function() {
    return {
        heads: [
            u.onlyForMethod('urls.lookupUser', new RoboHydraHead({
                name: 'urls.lookupUser',
                path: '/services/rest/?',
                handler: function(req, res) {
                    var userUrl = req.queryParams.url || req.bodyParams.url;
                    var urlName = userUrl.replace(/\/$/, '').
                        replace(/.*\//, '');

                    if (urlName === undefined || urlName === '') {
                        res.send('<?xml version="1.0" encoding="utf-8" ?>\n<rsp stat="fail">\n    <err code="1" msg="User not found" />\n</rsp>');
                        return;
                    }

                    res.write('<?xml version="1.0" encoding="utf-8" ?>\n');
                    res.write('<rsp stat="ok">\n');
                    res.write('<user id="' + urlName + '@N00">\n');
                    res.write('    <username>Username For ' + urlName + '</username>\n');
                    res.write('</user>\n</rsp>');
                    res.end();
                }
            })),

            u.onlyForMethod('photos.search', new RoboHydraHeadStatic({
                name: 'photos.search.empty',
                path: '/services/rest/?',
                content: u.xmlForPhotos([])
            })),

            new RoboHydraHeadFilesystem({
                name: 'static.flickr.com',
                mountPath: '/static',
                documentRoot: 'robohydra/static'
            })
        ],

        scenarios: {
            oneSearchResult: {
                heads: [
                    u.onlyForMethod('photos.search', new RoboHydraHeadStatic({
                        name: 'photos.search.one',
                        path: '/services/rest/?',
                        content: u.xmlForPhotos([
                            {id: '6837662941',
                             owner: '24881879@N00',
                             secret: '24315d29c1',
                             server: '7174',
                             farm: '1',
                             title: 'IMG_5539.JPG',
                             ispublic: '1',
                             isfriend: '0',
                             isfamily: '0',
                             description: "Arriving in Tau (close to Stavanger) to take the bus to start the hike to Preikestolen (the Pulpit Rock) in Rogaland, Norway."},
                            {id: 'foobar',
                             description: "Believe it or not, it worked!"}
                        ])
                    }))
                ]
            }
        }
    };
};
