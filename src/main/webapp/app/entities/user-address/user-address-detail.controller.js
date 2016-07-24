(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('UserAddressDetailController', UserAddressDetailController);

    UserAddressDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'UserAddress', 'User'];

    function UserAddressDetailController($scope, $rootScope, $stateParams, entity, UserAddress, User) {
        var vm = this;

        vm.userAddress = entity;

        var unsubscribe = $rootScope.$on('rentitApp:userAddressUpdate', function(event, result) {
            vm.userAddress = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
