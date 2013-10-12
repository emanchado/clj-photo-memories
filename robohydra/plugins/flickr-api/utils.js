var robohydra               = require("robohydra"),
    heads                   = robohydra.heads,
    RoboHydraHead           = heads.RoboHydraHead;
var et = require('elementtree');

var defaultAttributes = {
    id: '1234567890',
    owner: '12345678@N00',
    secret: '123456789a',
    server: '1111',
    farm: '1',
    title: 'Untitled photo',
    ispublic: '1',
    isfriend: '0',
    isfamily: '0'
};
var defaultElements = {
    description: 'No description'
};


// As the Flick API uses a single URL for everything, but keeping all
// code for everything in a single head doesn't really work (huge code
// mess + extremely clunky to write scenarios), make heads pass on the
// request to the next matching head if the request is not for the
// appropriate Flickr API method.
//
// Fiddling directly with the "handler" property is kind of a hack but
// it works well enough.
exports.onlyForMethod = function(method, head) {
    head.originalHandler = head.handler;

    head.handler = function(req, res, next) {
        var m = req.queryParams.method || req.bodyParams.method;
        if (m === 'flickr.' + method) {
            this.originalHandler(req, res, next);
        } else {
            next(req, res);
        }
    };

    return head;
};

exports.xmlForPhotos = function(photos) {
    var photoCount = photos.length;

    var rspElem = et.Element('rsp');
    rspElem.set('stat', 'ok');
    var photosElem = et.SubElement(rspElem, 'photos');
    photosElem.set('pages', photoCount > 0 ? 1 : 0);
    photosElem.set('page', 1);
    photosElem.set('perPage', photoCount);
    photosElem.set('total', photoCount);
    photos.forEach(function(photo) {
        var photoElem = et.SubElement(photosElem, 'photo');
        Object.keys(defaultAttributes).forEach(function(attr) {
            var value = photo.hasOwnProperty(attr) ?
                    photo[attr] : defaultAttributes[attr];
            photoElem.set(attr, value);
        });
        Object.keys(defaultElements).forEach(function(elem) {
            var value = photo.hasOwnProperty(elem) ?
                    photo[elem] : defaultElements[elem];
            var se = et.SubElement(photoElem, elem);
            se.text = value;
        });
    });
    var etree = new et.ElementTree(rspElem);
    return etree.write();
};

exports.instructions = function(expectedResult) {
    return "Call clj-flickr-memories like " +
        "`lein run foo -u http://localhost:3000/services/rest " +
        "-s localhost:3000/static`.\n\n" +
        expectedResult;
};
