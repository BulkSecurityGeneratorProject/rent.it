(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('WishListController', WishListController);

    WishListController.$inject = ['$scope', '$state', 'WishList', 'WishListSearch'];

    function WishListController ($scope, $state, WishList, WishListSearch) {
        var vm = this;
        
        vm.wishLists = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            WishList.query(function(result) {
                vm.wishLists = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            WishListSearch.query({query: vm.searchQuery}, function(result) {
                vm.wishLists = result;
            });
        }    }
})();
