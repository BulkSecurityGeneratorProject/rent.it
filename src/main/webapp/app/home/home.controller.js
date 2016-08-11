(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'Product', 'ProductSearch'];

    function HomeController ($scope, Principal, LoginService, $state, Product, ProductSearch) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.products = [];
        vm.search = search;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }
        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ProductSearch.query({query: vm.searchQuery}, function(result) {
                vm.products = result;
            });
        }
    }
})();
