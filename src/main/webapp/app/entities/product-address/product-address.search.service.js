(function() {
    'use strict';

    angular
        .module('rentitApp')
        .factory('ProductAddressSearch', ProductAddressSearch);

    ProductAddressSearch.$inject = ['$resource'];

    function ProductAddressSearch($resource) {
        var resourceUrl =  'api/_search/product-addresses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
