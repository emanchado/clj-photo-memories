var heads                   = require("robohydra").heads,
    RoboHydraHeadFilesystem = heads.RoboHydraHeadFilesystem;
var flickrHeads = require("./flickr-heads");


exports.getBodyParts = function() {
    return {
        heads: [
            new flickrHeads.RoboHydraHeadFlickrApi({
                name: 'urls.lookupUser',
                method: 'flickr.urls.lookupUser',
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
            }),

            new flickrHeads.RoboHydraHeadPhotoSearch({
                name: 'photos.search.empty',
                photos: []
            }),

            new RoboHydraHeadFilesystem({
                name: 'static.flickr.com',
                mountPath: '/static',
                documentRoot: 'robohydra/static'
            })
        ],

        scenarios: {
            oneSearchResult: {
                heads: [
                    new flickrHeads.RoboHydraHeadPhotoSearch({
                        name: 'photos.search.one',
                        photos: [
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
                        ]
                    })
                ]
            }
        }
    };
};
