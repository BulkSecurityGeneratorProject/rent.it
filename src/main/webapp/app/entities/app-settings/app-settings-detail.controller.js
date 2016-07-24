(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('AppSettingsDetailController', AppSettingsDetailController);

    AppSettingsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'AppSettings'];

    function AppSettingsDetailController($scope, $rootScope, $stateParams, entity, AppSettings) {
        var vm = this;

        vm.appSettings = entity;

        var unsubscribe = $rootScope.$on('rentitApp:appSettingsUpdate', function(event, result) {
            vm.appSettings = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
