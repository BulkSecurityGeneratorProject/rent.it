(function() {
    'use strict';

    angular
        .module('rentitApp')
        .factory('ImageSearch', ImageSearch);

    ImageSearch.$inject = ['$resource'];

    function ImageSearch($resource) {
        var resourceUrl =  'api/_search/images/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
