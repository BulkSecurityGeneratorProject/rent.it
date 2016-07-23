(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('CurrencyController', CurrencyController);

    CurrencyController.$inject = ['$scope', '$state', 'Currency', 'CurrencySearch'];

    function CurrencyController ($scope, $state, Currency, CurrencySearch) {
        var vm = this;
        
        vm.currencies = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Currency.query(function(result) {
                vm.currencies = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CurrencySearch.query({query: vm.searchQuery}, function(result) {
                vm.currencies = result;
            });
        }    }
})();
