(function() {
    'use strict';

    angular
        .module('rentitApp')
        .factory('UserAddressSearch', UserAddressSearch);

    UserAddressSearch.$inject = ['$resource'];

    function UserAddressSearch($resource) {
        var resourceUrl =  'api/_search/user-addresses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
