(function() {
    'use strict';

    angular
        .module('rentitApp')
        .factory('AppSettingsSearch', AppSettingsSearch);

    AppSettingsSearch.$inject = ['$resource'];

    function AppSettingsSearch($resource) {
        var resourceUrl =  'api/_search/app-settings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
