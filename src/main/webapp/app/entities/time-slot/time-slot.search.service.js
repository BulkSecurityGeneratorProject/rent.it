(function() {
    'use strict';

    angular
        .module('rentitApp')
        .factory('TimeSlotSearch', TimeSlotSearch);

    TimeSlotSearch.$inject = ['$resource'];

    function TimeSlotSearch($resource) {
        var resourceUrl =  'api/_search/time-slots/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
