(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('UserAddressController', UserAddressController);

    UserAddressController.$inject = ['$scope', '$state', 'UserAddress', 'UserAddressSearch'];

    function UserAddressController ($scope, $state, UserAddress, UserAddressSearch) {
        var vm = this;
        
        vm.userAddresses = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            UserAddress.query(function(result) {
                vm.userAddresses = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            UserAddressSearch.query({query: vm.searchQuery}, function(result) {
                vm.userAddresses = result;
            });
        }    }
})();
