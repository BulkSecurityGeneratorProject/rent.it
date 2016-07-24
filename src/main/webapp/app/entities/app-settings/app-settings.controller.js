(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('AppSettingsController', AppSettingsController);

    AppSettingsController.$inject = ['$scope', '$state', 'AppSettings', 'AppSettingsSearch'];

    function AppSettingsController ($scope, $state, AppSettings, AppSettingsSearch) {
        var vm = this;
        
        vm.appSettings = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            AppSettings.query(function(result) {
                vm.appSettings = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AppSettingsSearch.query({query: vm.searchQuery}, function(result) {
                vm.appSettings = result;
            });
        }    }
})();
