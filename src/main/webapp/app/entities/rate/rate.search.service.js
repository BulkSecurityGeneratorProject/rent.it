(function() {
    'use strict';

    angular
        .module('rentitApp')
        .factory('RateSearch', RateSearch);

    RateSearch.$inject = ['$resource'];

    function RateSearch($resource) {
        var resourceUrl =  'api/_search/rates/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
