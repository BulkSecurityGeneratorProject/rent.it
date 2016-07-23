(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('ProductDetailController', ProductDetailController);

    ProductDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Product', 'ProductAddress', 'Comment'];

    function ProductDetailController($scope, $rootScope, $stateParams, entity, Product, ProductAddress, Comment) {
        var vm = this;

        vm.product = entity;

        var unsubscribe = $rootScope.$on('rentitApp:productUpdate', function(event, result) {
            vm.product = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
