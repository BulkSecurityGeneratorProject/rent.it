(function() {
    'use strict';
    angular
        .module('rentitApp')
        .factory('TimeSlot', TimeSlot);

    TimeSlot.$inject = ['$resource'];

    function TimeSlot ($resource) {
        var resourceUrl =  'api/time-slots/:id';

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
