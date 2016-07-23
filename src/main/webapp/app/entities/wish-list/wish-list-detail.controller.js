(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('WishListDetailController', WishListDetailController);

    WishListDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'WishList'];

    function WishListDetailController($scope, $rootScope, $stateParams, entity, WishList) {
        var vm = this;

        vm.wishList = entity;

        var unsubscribe = $rootScope.$on('rentitApp:wishListUpdate', function(event, result) {
            vm.wishList = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
