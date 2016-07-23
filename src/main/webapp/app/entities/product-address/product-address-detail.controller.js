(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('ProductAddressDetailController', ProductAddressDetailController);

    ProductAddressDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'ProductAddress'];

    function ProductAddressDetailController($scope, $rootScope, $stateParams, entity, ProductAddress) {
        var vm = this;

        vm.productAddress = entity;

        var unsubscribe = $rootScope.$on('rentitApp:productAddressUpdate', function(event, result) {
            vm.productAddress = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
