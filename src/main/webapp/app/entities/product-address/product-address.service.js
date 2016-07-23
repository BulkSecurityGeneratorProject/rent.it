(function() {
    'use strict';
    angular
        .module('rentitApp')
        .factory('ProductAddress', ProductAddress);

    ProductAddress.$inject = ['$resource'];

    function ProductAddress ($resource) {
        var resourceUrl =  'api/product-addresses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
