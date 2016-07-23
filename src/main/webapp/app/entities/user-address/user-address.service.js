(function() {
    'use strict';
    angular
        .module('rentitApp')
        .factory('UserAddress', UserAddress);

    UserAddress.$inject = ['$resource'];

    function UserAddress ($resource) {
        var resourceUrl =  'api/user-addresses/:id';

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
