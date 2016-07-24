(function() {
    'use strict';
    angular
        .module('rentitApp')
        .factory('AppSettings', AppSettings);

    AppSettings.$inject = ['$resource'];

    function AppSettings ($resource) {
        var resourceUrl =  'api/app-settings/:id';

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
