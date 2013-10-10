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


// As the Flickr API uses a single URL (/services/rest), but putting
// everything in a single RoboHydra head would be a mess, instead we
// make each head check if the "method" GET or POST argument has the
// correct value. If so, process the request; if not, pass it on to
// the next.
//
// To avoid repeating that logic in all heads, make a base head type
// that takes care of that.
exports.RoboHydraHeadFlickrApi = robohydra.roboHydraHeadType({
    name: 'flickr-api',

    mandatoryProperties: ['method', 'handler'],
    defaultProps: {method: '', handler: function() {}},

    parentPropBuilder: function() {
        this.originalHandler = this.handler;

        return {
            path: '/services/rest/?',
            handler: function(req, res, next) {
                var method = req.queryParams.method ||
                        req.bodyParams.method;
                if (method === this.method) {
                    this.originalHandler(req, res, next);
                } else {
                    next(req, res);
                }
            }
        };
    }
});


exports.RoboHydraHeadPhotoSearch = robohydra.roboHydraHeadType({
    name: 'photo-search',
    parentClass: exports.RoboHydraHeadFlickrApi,

    mandatoryProperties: ['photos'],
    defaultProps: {photos: []},

    parentPropBuilder: function() {
        return {
            method: 'flickr.photos.search',
            handler: function(req, res) {
                var photoCount = this.photos.length;

                var rsp = et.Element('rsp');
                rsp.set('stat', 'ok');
                var photosElem = et.SubElement(rsp, 'photos');
                photosElem.set('pages', photoCount > 0 ? 1 : 0);
                photosElem.set('page', 1);
                photosElem.set('perPage', photoCount);
                photosElem.set('total', photoCount);
                this.photos.forEach(function(photo) {
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
                var etree = new et.ElementTree(rsp);
                res.send(etree.write());
            }
        };
    }
});
