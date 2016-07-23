(function() {
    'use strict';

    angular
        .module('rentitApp')
        .factory('WishListSearch', WishListSearch);

    WishListSearch.$inject = ['$resource'];

    function WishListSearch($resource) {
        var resourceUrl =  'api/_search/wish-lists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
