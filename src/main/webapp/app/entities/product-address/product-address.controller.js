(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('ProductAddressController', ProductAddressController);

    ProductAddressController.$inject = ['$scope', '$state', 'ProductAddress', 'ProductAddressSearch'];

    function ProductAddressController ($scope, $state, ProductAddress, ProductAddressSearch) {
        var vm = this;
        
        vm.productAddresses = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            ProductAddress.query(function(result) {
                vm.productAddresses = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ProductAddressSearch.query({query: vm.searchQuery}, function(result) {
                vm.productAddresses = result;
            });
        }    }
})();
