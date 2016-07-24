(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('WishListDialogController', WishListDialogController);

    WishListDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'WishList', 'User', 'Product'];

    function WishListDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, WishList, User, Product) {
        var vm = this;

        vm.wishList = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();
        vm.products = Product.query({filter: 'wishlist-is-null'});
        $q.all([vm.wishList.$promise, vm.products.$promise]).then(function() {
            if (!vm.wishList.product || !vm.wishList.product.id) {
                return $q.reject();
            }
            return Product.get({id : vm.wishList.product.id}).$promise;
        }).then(function(product) {
            vm.products.push(product);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.wishList.id !== null) {
                WishList.update(vm.wishList, onSaveSuccess, onSaveError);
            } else {
                WishList.save(vm.wishList, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:wishListUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
